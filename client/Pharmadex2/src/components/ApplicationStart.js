import React , {Component} from 'react'
import {Container,Row, Col, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'
import ThingsManager from './ThingsManager'
import CheckList from './CheckList'
import AlertFloat from './utils/AlertFloat'
import ActivityHistoryData from './ActivityHistoryData'
/**
 * Starts an application
 * Contains component's display logic and nothing else
 * @example
 * <ApplicationStart data={data}/>  data is {url, historyId}
 */
class ApplicationStart extends Component{
    constructor(props){
        super(props)
        this.state={
            next:true,
            submit:false,
            identifier:Date.now().toString(),
            title:'',
            reference:false,
            labels:{
                next:'',
                application_info:'',
                global_save:'',
                global_cancel:'',
                global_submit:'',
                return_action:'',
                route_action:'',
                global_showPrint:""
            },
            history:{},             //histroy table
            data:{},                //data to conclude                
        }
        this.loadHistory=this.loadHistory.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
        this.headerFooter=this.headerFooter.bind(this)
        this.activityHistory=this.activityHistory.bind(this)
        this.historyTable=this.historyTable.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
         eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=="appLoaded"){
                    this.state.title=data.data.title
                    this.setState(this.state)
                }
                if(data.subject=="cancelThing"){
                    //window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                    window.history.back()
                }
                if(data.subject=="conclude"){
                    this.state.submit=true
                    this.state.data=data.data
                    this.state.reference=true
                    this.setState(this.state)
                }
                if(data.subject=="activityHistoryClose"){
                    this.state.historyId=0
                    this.setState(this.state)
                }
            }
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loadHistory()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    loadHistory(){
        this.state.history.historyId=this.props.data.historyId
        this.state.history.applDictNodeId=this.props.data.applDictNodeId
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/application/history", this.state.history, (query,result)=>{
            this.state.history=result
            this.setState(this.state)
        })
    }

    /**
     * How to display content - only main column or reference column + main column
     */
    content(){
        if(this.state.reference){
            return(
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Row >
                        <Col>
                            <Button close
                                onClick={()=>{
                                    this.state.reference=false
                                    this.state.submit=false
                                    this.setState(this.state)
                                }} 
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col className="overflow-auto" style={{maxHeight:'70vh'}}>
                        <ThingsManager applicationUrl={this.state.data.url} 
                                        applDictNodeId={this.state.data.applDictNodeId}
                                        historyId={this.state.data.historyId}
                                        application
                                        recipient={this.state.identifier}
                                        narrow
                                        readOnly/>
                        </Col>
                    </Row>
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6' className="overflow-auto" style={{maxHeight:'70vh'}}>
                    <Row>
                        <Col className={Pharmadex.settings.activeBorder}>
                            <CheckList
                                historyId={this.state.data.historyId}
                                recipient={this.state.identifier} 
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }else{
            return(
                <Row className={Pharmadex.settings.activeBorder}>
                    <Col xs='12' sm='12' lg='12' xl='12'>
                    <ThingsManager  applicationUrl={this.props.data.url} 
                                    applDictNodeId={this.props.data.applDictNodeId}
                                    historyId={this.props.data.historyId}
                                    recipient={this.state.identifier}
                                    modiUnitId={this.props.data.modiUnitId}                      // id of data unit selected to modify
                                    prefLabel={this.props.data.prefLabel}                        // preflabel by default
                                           />
                    </Col>
                </Row>
            )
        }
    }
    headerFooter(){
        let applName= this.state.data.applName
        if(this.state.history != undefined){
            if(this.state.history.applName != undefined){
                applName=this.state.history.applName
            }
        }
        return(
                <Row>
                <Col xs='12' sm='12' lg='8' xl='8'>
                    <h4>{applName}</h4>
                </Col>
                <Col>
                    <div className="mb-1 d-flex justify-content-end">
                                <Button size="sm" hidden={this.state.history.historyId == 0}
                                        className="mr-1" color="info"
                                        onClick={()=>{
                                            let data={
                                                url:this.props.data.url,
                                                applDictNodeId:this.props.data.applDictNodeId,
                                                historyId:this.props.data.historyId,
                                            }
                                            let param = JSON.stringify(data)
                                            var url = "/" + Navigator.tabSetName() + "#printprev" + "/" + encodeURI(param)
                                            let w = window.open(url, "_blank")
                                        }}>
                                    {this.state.labels.global_showPrint}
                                </Button>{' '}
                                <Button size="sm"
                                className="mr-1" color="success"
                                onClick={()=>{
                                    Navigator.message(this.state.identifier,"*","saveAllGuest",{})
                                }}
                            
                                >{this.state.labels.global_save}</Button>{' '}
                            <Button size="sm"
                            className="mr-1" color="info"
                            onClick={()=>{
                                window.history.back()
                            }}
                            >{this.state.labels.global_cancel}</Button>{' '}

                            <Button size="sm" hidden={!this.state.submit}
                            className="mr-1" color="primary"
                            onClick={()=>{
                                Navigator.message(this.state.identifier,"*","submit",{})
                            }}
                            >{this.state.labels.global_submit}</Button>{' '}
                        </div>
                </Col>
            
            </Row>
        )
    }
    /**
     * 
     * @returns expanded activity record
     */
    activityHistory(){
        if(this.state.historyId>0){
            return <ActivityHistoryData historyId={this.state.historyId} recipient={this.state.identifier} />
        }else{
            return []
        }
    }
    historyTable(){
        if(this.state.history.table.rows.length==0){
            return []
        }
        return(
            <Row>
                <Col>
                    <CollectorTable
                        tableData={this.state.history.table}
                        loader={this.loadHistory}
                        headBackground={Pharmadex.settings.tableHeaderBackground}
                        linkProcessor={(rowNo, cell)=>{
                            this.state.historyId=this.state.history.table.rows[rowNo].dbID
                            this.setState(this.state)
                        }}
                        styleCorrector={(header)=>{
                            if(header=='come'){
                                return {width:'12%'}
                            }
                            if(header=='go'){
                                return {width:'12%'}
                            }
                            if(header=='days'){
                                return {width:'5%'}
                            }
                            if(header=='workflow'){
                                return {width:'20%'}
                            }
                            if(header=='activity'){
                                return {width:'15%'}
                            }
                        }}
                    />
                </Col>
            </Row>
        )
    }
    render(){
        if(this.state.labels.locale == undefined || this.state.history.table==undefined){
            return []
        }
        
        return(
            <Container fluid style={{fontSize:"0.8rem"}}>
                {this.historyTable()}
                {this.activityHistory()}
                {this.headerFooter()}
                {this.content()}
                {this.headerFooter()}
                <AlertFloat />
            </Container>   
        )
    }


}
export default ApplicationStart
ApplicationStart.propTypes={
    data:PropTypes.shape(
        {
            url:PropTypes.string.isRequired,            //url of an application, i.e. application.guest
            applDictNodeId:PropTypes.number,            //id of dictionary node that describes an application
            historyId:PropTypes.number.isRequired,      //id of the histry record to determine activity and data. Zero means new
            modiUnitId:PropTypes.number,                //id of data unit selected to modify
            prefLabel:PropTypes.string,                 //preflabel by default, if needed
        }
    ).isRequired       
}