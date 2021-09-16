import React , {Component} from 'react'
import {Container, Row, Col,Breadcrumb,BreadcrumbItem, Nav, NavItem} from 'reactstrap'
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

/**
 * Responsible for a table for report
 * Just copy it
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
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Create the breadcrumb from the path in the dictionary to initate seamless integration to it
     */
    createBreadCrumb(){
        let ret = []
        let className="btn btn-link p-0 border-0"
        if(this.state.data.readOnly|| this.props.readOnly){
            className='d-inline-flex'
        }
        //dictionary home
        ret.push(
            <BreadcrumbItem key={this.state.data.dict.home}>
                <div className={className} style={{fontSize:'0.8rem'}}
                             onClick={()=>{
                            Navigator.message(this.state.identifier, this.props.recipient, "reportRootDictionary", {})
                        }}>
                    {this.state.data.dict.home}
                </div>
                <div hidden={!this.props.readOnly}>
                    {this.state.data.dict.home}
                </div>
            </BreadcrumbItem>
            )
        // the rest of the dictionary
        let path=this.state.data.dict.path
        if(Fetchers.isGoodArray(path)){
            path.forEach((field, index) => {
                ret.push(
                    <BreadcrumbItem  key={index}>
                        <div className={className} style={{fontSize:'0.8rem'}}
                            onClick={()=>{
                                Navigator.message(this.state.identifier, this.props.recipient, "reportPathDictionary", field)
                        }}>
                            {field.code}
                        </div>
                    </BreadcrumbItem>
                )
            });
        }
        //the last item in the dictionary
        let selectedRow={}
        this.state.data.dict.table.rows.forEach(row=>{
            if(row.selected){
                selectedRow=row
            }
        })
        if(selectedRow.dbID != undefined){
            let lbl=selectedRow.row[0].value
            ret.push(
                <BreadcrumbItem  key={selectedRow.dbID}>
                    <div hidden={this.state.data.thing.nodeId==0} className={className} style={{fontSize:'0.8rem'}}
                        onClick={()=>{
                            this.state.data.thing={
                                nodeId:0
                            }
                            this.setState(this.state)
                        }}>
                        {lbl}
                    </div>
                    <span hidden={this.state.data.thing.nodeId>0}>
                        {lbl}
                    </span>
                </BreadcrumbItem>
            )
           
        }
         //the selected thing
         if(this.state.title.length>0 && this.state.data.thing.nodeId>0){
            ret.push(
                <BreadcrumbItem  key={this.state.title}>
                    <span>
                        {this.state.title}
                    </span>
                </BreadcrumbItem>
            )
        }

        return ret
    }
    /**
     * Paint full thing
     */
    paintApplication(){
        let ret=[]
        let path=this.state.data.thing.path
        if(Fetchers.isGoodArray(path)){
            ret.push(
                <Row key="showprintform" className='mb-1'>
                    <Col xs='12' sm='12' lg='10' xl='10'>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                    <ButtonUni
                        label= {this.state.labels.global_showPrint}
                        color='info'
                        onClick={()=>{
                            let data={
                                nodeId:this.state.data.thing.nodeId
                            }
                            let param = JSON.stringify(data)
                            var url = "/" + Navigator.tabSetName() + "#reports/printpreview/" + encodeURI(param)
                            let w = window.open(url, "_blank")
                        }}/>
                    </Col>
                </Row>
            )
            ret.push(
                <CollectorTable key="tableReg"
                tableData={this.state.data.thing.regTable}
                loader={()=>{}}
                headBackground={Pharmadex.settings.tableHeaderBackground}
                />
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
            this.state.data=result
            this.state.title=result.thing.path[0].title
            this.setState(this.state)
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
            return(
                <Row>
                    <Col>
                        <Row className='mb-1'>
                            <Col xs='12' sm='12' lg='4' xl='4'>
                                <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.loadTable} />
                            </Col>
                            <Col xs='12' sm='12' lg='6' xl='6'>

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
                <Row className='mb-1'>
                    <Col>
                        <Breadcrumb>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                </Row>
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