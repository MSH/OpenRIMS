import React , {Component} from 'react'
import {Container, Row, Col, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import SearchControl from '../utils/SearchControl'
import ButtonUni from '../form/ButtonUni'
import Pharmadex from '../Pharmadex'
import CollectorTable from '../utils/CollectorTable'
import Thing from '../Thing'
import Downloader from '../utils/Downloader'
import ApplicationEvents from '../applevents/ApplicationEvents'
import ApplicationHistory from './ApplicationHistory'
import ApplicationRegisters from './ApplicationRegisters'

/**
 * Responsible for a table for report
 * It issues onDrillDown and onDrillUp
 */
class TableReport extends Component{
    constructor(props){
        super(props)
        this.state={
            title:'',
            data:this.props.data,
            identifier:Date.now().toString(),
            labels:{
                exportExcel:'',
                search:'',
                global_showPrint:'',
                cancel:'',
                warning:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadTable=this.loadTable.bind(this)
        this.content=this.content.bind(this)
        this.paintApplication=this.paintApplication.bind(this)
        this.drillDown=this.drillDown.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.from==this.props.recipient){
                if(data.subject=='refreshReportTable'){
                    this.state.data.thing.nodeId=0
                    this.setState(this.state)
                }
            }
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }


    /**
     * Paint full thing
     */
    paintApplication(){
        let ret=[]
        let path=this.state.data.thing.path
        if(Fetchers.isGoodArray(path)){
            ret.push(
                <ApplicationHistory key='ah1' nodeid={this.state.data.thing.nodeId} recipient={this.state.identifier}/>
            )
            ret.push(
                <ApplicationEvents key='ae1' appldataid={this.state.data.thing.nodeId} recipient={this.state.identifier} />
            )
            ret.push(
                <ApplicationRegisters key='ar1' nodeid={this.state.data.thing.nodeId} recipient={this.state.identifier}/>
            )
            path.forEach((element, index)=>{
                ret.push(
                    <Thing
                        key={index}
                        data={element}
                        recipient={this.state.identifier}
                        readOnly
                        narrow
                    />
                )
            })
        }
        return ret
    }
    /**
     * Prepare thing to display
     * @param {selected row} rowNo 
     */
    drillDown(rowNo){
        this.state.data.thing.nodeId=this.state.data.table.rows[rowNo].dbID
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/application", this.state.data, (query,result)=>{
            let path=result.thing.path
            if(Fetchers.isGoodArray(path)){
            this.state.data=result
            this.state.title=result.thing.path[0].title
            this.setState(this.state)
            Navigator.message(this.state.identifier, this.props.recipient, "onDrillDown", this.state.data.thing.title)
        }else{
            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'info'})
        }
        })
    }
    /**
     * Table or Thing?
     * 
     */
    content(){
        if(this.state.data.thing.nodeId>0){
            return (
                <Row>
                    <Col>
                       {this.paintApplication()}
                    </Col>
                </Row>
            )
        }else{
            Navigator.message(this.state.identifier, this.props.recipient, "onDrillUp", {})
            return(
                <Row>
                    <Col>
                        <Row className='mb-1'>
                            <Col xs='12' sm='12' lg='10' xl='10'>
                                <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.loadTable} />
                            </Col>
                            <Col xs='12' sm='12' lg='2' xl='2'>
                                <ButtonUni
                                    label={this.state.labels.exportExcel}
                                    onClick={()=>{
                                        let downloader = new Downloader();
                                        Fetchers.setJustLoaded(this.state.data,false)
                                        downloader.postDownload("/api/"+Navigator.tabSetName()+ "/report/export/excel",
                                        this.state.data, "applications.xlsx");
                                    }} 
                                    color={"info"}
                                />
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.table}
                                    loader={this.loadTable}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    selectRow={(rowNo)=>{
                                        this.drillDown(rowNo)
                                    }}
                                    linkProcessor={(rowNo,col)=>{
                                        this.drillDown(rowNo)
                                    }}
                                    styleCorrector={(header)=>{
                                        if(header=='prefLabel'){
                                            return {width:'15%'}
                                        }
                                        if(header=='address'){
                                            return {width:'20%'}
                                        }
                                    }}
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )
        }
    }
    /**
     * Reload only table
     */
    loadTable(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }
    render(){
        if(this.state.data.table == undefined || this.state.labels.locale == undefined){
            return <div> <i className="blink fas fa-circle-notch fa-spin" style={{color:'#D3D3D3'}}/></div>
        }
        return(
            <Container fluid>
               {this.content()}
            </Container>
        )
    }


}
export default TableReport
TableReport.propTypes={
    data:PropTypes.object.isRequired,           //ReportDTO
    recipient:PropTypes.string.isRequired,
    readOnly:PropTypes.bool
}