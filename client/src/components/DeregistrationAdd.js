import React , {Component} from 'react'
import {Container, Row, Col,FormText} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'
import SearchControl from './utils/SearchControl'

/**
 * It is a dummy component to create other components quickly
 * Just copy it
 */
class DeregistrationAdd extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{                                  //AmendmentNewDTO.java
                dictItemId:this.props.dictItemId
            },
            labels:{
                search:'',
                selectforderigester:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadTable=this.loadTable.bind(this)
        this.runDeregistration=this.runDeregistration.bind(this)
        this.verifyAdd=this.verifyAdd.bind(this)
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
        this.loadTable()
        Locales.resolveLabels(this)
    }

    componentDidUpdate(){
        if(this.props.dictItemId != this.state.data.dictItemId){
            this.state.data.dictItemId=this.props.dictItemId
            this.loadTable()
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    /**
     * load/reload AmendmentNewDTO
     */
    loadTable(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/deregistration/propose/add", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    verifyAdd(rowNo){
        let row=this.state.data.applications.rows[rowNo]
        let verifdto={
            url:'',
            applDictNodeId:this.state.data.dictItemId,
            applID:row.dbID,
        }
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/deregistration/verif/add", verifdto, (query,result)=>{
            if(result.valid){
                this.runDeregistration(rowNo)
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                this.setState(this.state)
            }
        })
    }

    /**
     * Run a new de-registration application
     * @param {selected application data row} rowNo 
     */
    runDeregistration(rowNo){
        let row=this.state.data.applications.rows[rowNo]
        let data={
            dataId:0,
            url:'',                                     //url of an application, i.e. application.guest, deprecated in favor of applDictNodeId
            applDictNodeId:this.state.data.dictItemId,  //id of dictionary node that describes an application
            historyId:0,                                //id of the histry record to determine activity and data. Zero means new
            modiUnitId:row.dbID,                        //id of data unit selected to modify
            prefLabel:row.row[0].value,                 //preflabel by default
        }
        let param = JSON.stringify(data)
        Navigator.navigate(Navigator.tabName(),"applicationstart",param)
    }

    render(){
        if(this.state.data.applications == undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <h5>{this.state.data.permitType}</h5>
                    </Col>
                    
                </Row>
                <Row>
                    <Col  xs='12' sm='12' lg='10' xl='10'>
                        <FormText color="muted">{this.state.labels.selectforderigester}</FormText>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <SearchControl label={this.state.labels.search} table={this.state.data.applications} loader={this.loadTable} />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <CollectorTable
                            tableData={this.state.data.applications}
                            loader={this.loadTable}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            selectRow={(rowNo)=>{
                                {this.verifyAdd(rowNo)}
                            }}
                            linkProcessor={(rowNo, cell)=>{
                                {this.verifyAdd(rowNo)}
                            }}
                        />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default DeregistrationAdd
DeregistrationAdd.propTypes={
    dictItemId:PropTypes.number.isRequired,         //de-registration type
    recipient:PropTypes.string.isRequired,          //for messaging
}