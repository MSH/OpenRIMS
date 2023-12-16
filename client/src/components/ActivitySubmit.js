import React , {Component} from 'react'
import {Container, Row, Col,Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import CollectorTable from './utils/CollectorTable'
import ViewEdit from './form/ViewEdit'
import Pharmadex from './Pharmadex'
import Alerts from './utils/Alerts'
/**
 * Responsible for Activity Submit screen, e.g., a screen that appears after Activity-Submit is pressed and valdation is OK
 */
class ActivitySubmit extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{
                historyId:this.props.historyId,
                supervisor:this.props.supervisor,
                reassign:this.props.reassign,
                reject:this.props.reject,
                monitoring:this.props.monitoring
            },                                                  // ActivitySubmitDTO
            labels:{
                nextactivity:'',
                executors:'',
                conclusion:'',
                notes:'',
                scheduled:'',
                questionDecline:'',
                workflows:''
            },
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.rightUp=this.rightUp.bind(this)
        this.leftBottom=this.leftBottom.bind(this)
        this.rightBottom=this.rightBottom.bind(this)
        this.notes=this.notes.bind(this)
        this.loadNext=this.loadNext.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            if(data.subject="onSelectionChange"){
                this.state.data.actions=data.data
                this.loader()
            }
        }
        if(data.from==this.props.recipient){
            if(data.subject=="sendToNext"){
                if(this.state.data.actions.selectedRow.row[0].value=='decline'){
                    Alerts.warning(this.state.labels.questionDecline,()=>{   //yes
                        this.loadNext()
                    },()=>{})//no
                } else{
                   this.loadNext()
                }  
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.state.data.reload=true
        this.loader()
    }

    componentDidUpdate(){
        this.state.data.reject=this.props.reject
        this.state.data.reassign=this.props.reassign
        //this.state.data.monitoring=this.props.monitoring
        if(this.state.data.historyId != this.props.historyId || this.state.data.supervisor != this.props.supervisor){
            this.state.data.historyId=this.props.historyId
            this.state.data.supervisor=this.props.supervisor
            this.state.data.reload=true
            this.loader()
        }
    }

loadNext(){
    Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/activity/submit/send", this.state.data, (query,result)=>{
        this.state.data=result
        if(this.state.data.valid){
            Navigator.message(this.state.identifier, this.props.recipient, "cancelThing", {})
            Navigator.message(this.state.identifier, this.props.recipient, "afterSubmit", {})
        }else{
            this.setState(this.state)
        }
    })
}

    loader(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/activity/submit/create/data", this.state.data, (query, result)=>{
            this.state.data=result
            if(this.state.data.valid){
                if(this.state.data.actions.rows.length>0){
                    this.state.data.reloadExecs=false
                    Locales.createLabels(this)
                    Locales.resolveLabels(this)
                    this.setState(this.state)
                }else{
                    Navigator.message(this.state.identifier, this.props.recipient, "submitClose", {})
                }
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                this.setState(this.state)
            }
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    notes(){
        return(
            <Row>
                <Col>
                    <ViewEdit mode='textarea' attribute='notes' component={this} edit/>
                </Col>
            </Row>
        )
    }
    /**
     * Right upper corner for next job selection or notes or schedule
     */
    rightUp(){
        //next activity
        if(Fetchers.isGoodArray(this.state.data.nextJob.rows)){
            return(
                <Row>
                    <Col>
                    <Row>
                        <Col>
                            <h6>{this.state.labels.nextactivity}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable
                                tableData={this.state.data.nextJob}
                                loader={this.loader}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                                selectRow={(rowNo)=>{
                                    if(this.state.data.applicant){
                                        return
                                    }
                                    let rows= this.state.data.nextJob.rows
                                    rows.forEach((element,index) => {
                                        if(index!=rowNo){
                                            element.selected=false
                                        }
                                    });
                                    rows[rowNo].selected=!rows[rowNo].selected
                                    this.state.data.reloadExecs=true
                                    this.loader()
                                }}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            )

        }
        //scheduled activities
        if(Fetchers.isGoodArray(this.state.data.scheduled.rows)){
            return(
                <Row>
                    <Col>
                        <Row>
                            <Col>
                                <h6>{this.state.labels.scheduled}</h6>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.scheduled}
                                    loader={this.loader}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )
        }
        if(Fetchers.isGoodArray(this.state.data.runHosts.rows)){
            return(
            <Row>
                    <Col>
                        <Row>
                            <Col>
                                <h6>{this.state.labels.workflows}</h6>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.runHosts}
                                    loader={this.loader}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    selectRow={(rowNo)=>{
                                        let rows= this.state.data.runHosts.rows
                                        rows[rowNo].selected=!rows[rowNo].selected
                                        this.setState(this.state)
                                    }}
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )
        }
        return this.notes()

    }

    rightBottom(){
        if(Fetchers.isGoodArray(this.state.data.nextJob.rows)){
            return(
                this.notes()
            )
        }else{
            return []
        }
    }
    /**
     * Empty or executors table
     */
    leftBottom(){
        if(Fetchers.isGoodArray(this.state.data.execs.rows)){
            return(
                <Row hidden={this.state.data.applicant}>
                    <Row>
                        <Col>
                            <h6>{this.state.labels.executors}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable
                                tableData={this.state.data.execs}
                                loader={this.loader}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                                selectRow={(rowNo)=>{
                                    let rows= this.state.data.execs.rows
                                    rows[rowNo].selected=!rows[rowNo].selected
                                    this.setState(this.state)
                                }}
                            />
                        </Col>
                    </Row>
                </Row>
            )
        }
        if(Fetchers.isGoodArray(this.state.data.scheduled.rows) || Fetchers.isGoodArray(this.state.data.runHosts.rows)){
            return(
                this.notes()
            )
        }
        return []
    }
    render(){
        if(this.state.data.nextJob==undefined || this.state.labels.locale==undefined){
            return []
        }
        let color = "info"
        if(this.state.data.colorAlert != undefined){
            color = this.state.data.colorAlert
        }//className="p-0 m-0"
        return(
            <Container fluid>
                 <Row hidden={this.state.data.identifier.length==0}>
                    <Col>
                        <Alert color={color}>
                            <small>{this.state.data.identifier}</small>
                        </Alert>
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col>
                                <h6>{this.state.labels.conclusion}</h6>
                            </Col>
                        </Row>
                        <Row hidden={this.state.data.actions.valid}>
                            <Col>
                                <Alert className="p-0 m-0">
                                    <small>{this.state.data.actions.identifier}</small>
                                </Alert>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.actions}
                                    loader={this.loader}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    styleCorrector={(header)=>{
                                        if(header=='pref'){
                                            return {width:'30%'}
                                        }
                                    }}
                                    selectRow={(rowNo)=>{
                                        let rows= this.state.data.actions.rows
                                        rows.forEach((row,index)=>{
                                            if(index!=rowNo){
                                                row.selected=false
                                            }
                                        })
                                        rows[rowNo].selected=!rows[rowNo].selected
                                        this.loader()
                                    }}
                                />
                            </Col>
                        </Row>
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.rightUp()}
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.leftBottom()}
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.rightBottom()}
                    </Col>
                </Row>
               
            </Container>
        )
    }

}
export default ActivitySubmit
ActivitySubmit.propTypes={
    historyId : PropTypes.number.isRequired,    //history id of the activity
    supervisor: PropTypes.bool.isRequired,      //initiated by supervisor
    reassign:   PropTypes.bool.isRequired,       //reassign only
    reject:PropTypes.bool.isRequired,            //return to applicant
    recipient : PropTypes.string.isRequired,    //for messages
    readonly:PropTypes.bool,                    //for future extension
    monitoring: PropTypes.bool
}