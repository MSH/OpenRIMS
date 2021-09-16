import React , {Component} from 'react'
import {Container, Row, Col, Button, Breadcrumb, BreadcrumbItem,Collapse, Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'
import Thing from './Thing'
import CheckList from './CheckList'
import ActivitySubmit from './ActivitySubmit'
import Spinner from './utils/Spinner'
import ButtonUni from './form/ButtonUni'

/**
 * Manages an activity
 */
class ActivityManager extends Component{
    constructor(props){
        super(props)
        this.state={
            reassign:false,
            saveCounter:1,                      //by default we wait saved only for checklist, however, sometaimes from data block
            conclude:false,                    //only for alive checklist!
            send:false,                        //display breadcrumb "send"
            hist:false,                        //initially closed
            supervisor:false,                   //action has been initiated by the supervisor
            pathIndex:0,
            identifier:Date.now().toString(),
            labels:{
                global_save:'',
                global_cancel:'',
                global_submit:'',
                send:'',
                return_action:'',
                route_action:'',
                application_info:'',
                done:'',
                activitycancelled:'',
                app_save_success:''
            },
            history:{ //history table
                historyId:this.props.historyId
            },
            data:{  //breadcrumb and data ActivityDTO.java
                historyId:this.props.historyId
            },
            color:"success"             
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadHistory=this.loadHistory.bind(this)
        this.loadData=this.loadData.bind(this)
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
        this.application=this.application.bind(this)
        this.dataThing=this.dataThing.bind(this)
        this.content=this.content.bind(this)
        this.prevNotes=this.prevNotes.bind(this)
        this.headerFooter=this.headerFooter.bind(this)
        this.hasCancelled=this.hasCancelled.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=="savedByAction" || data.subject=="cancelThing"){
                        this.state.saveCounter--
                        if(this.state.saveCounter==0){
                            Navigator.message(this.state.identifier, "*", 'show.alert.pharmadex.2', this.state.labels.app_save_success)
                            if(this.state.submit){ 
                                this.state.submit=false
                                Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/activity/done", this.state.data, (query,result)=>{
                                    if(result.done){
                                        Navigator.message(this.state.identifier, this.props.recipient, 'reload', {})
                                        window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                                    }else{
                                        this.state.send=true
                                        this.setState(this.state)
                                    }
                                })
                               
                            }else{
                                if(this.state.done){        //done background activity
                                    this.state.done=false
                                    Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/activity/background/done",
                                        this.state.data, (query,result)=>{
                                            if(result.valid){
                                                Navigator.message(this.state.identifier, this.props.recipient, 'reload', {})
                                                window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                                            }else{
                                                this.state.data=result
                                                this.setState(this.state)
                                            }
                                        })
                                }else{
                                    Navigator.message(this.state.identifier, this.props.recipient, 'reload', {})
                                    window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                                }
                            }
                        }
                }
                if(data.subject=='afterSubmit'){
                    Navigator.message(this.state.identifier, this.props.recipient, 'reload', {})
                    window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                }
            }
            
        }

    loadHistory(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/application/history", this.state.history, (query,result)=>{
            this.state.history=result
            this.state.color = "success"
            this.setState(this.state)
        })
    }
    /**
     * Load data necessary for the activity
     */
    loadData(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/activity/load", this.state.data, (query, result)=>{
            this.state.data=result
            this.state.color = "success"
            this.loadHistory()
        })        

    }
    /**
     * Create breadcrumb items
     */
    createBreadCrumb(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.path)){
            this.state.data.path.forEach((thing, index) => {
                if(index!=this.state.pathIndex || this.state.send){
                    ret.push(
                        <BreadcrumbItem className="d-inline"  key={index}>
                            <div className="btn btn-link p-0 border-0"
                        onClick={()=>{
                            this.state.pathIndex=index
                            this.state.send=false
                            this.setState(this.state)
                        }}
                        >
                            <h6 className="d-inline">{thing.title}</h6>
                        </div>
                        </BreadcrumbItem>
                    )
                }else{
                    ret.push(
                        <BreadcrumbItem className="d-inline"  key={index}>
                            <h6 className="d-inline">{thing.title}</h6>
                        </BreadcrumbItem>
                    )
                }
            })
            if(this.state.send){            //Bradcrumb for send+submit
                ret.push(
                    <BreadcrumbItem className="d-inline"  key={1000}>
                        <h6 className="d-inline">{this.state.labels.global_submit}</h6>
                    </BreadcrumbItem>
                )
            }
        }
        return ret
    }
    /**
     * Display an application
     */
    application(){
        let ret = []
        if(Fetchers.isGoodArray(this.state.data.application)){
            this.state.data.application.forEach((thing, index)=>{
                thing.readOnly=true
                ret.push(
                    <h4 key={index+1000}>
                        {thing.title}
                    </h4>
                )
               ret.push(
                   <Thing key={index}
                   data={thing}
                   recipient={this.state.identifier}
                   readOnly={true}
                   narrow
                   repaint
                />
                )
            })
        }
        return ret
    }
 

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.loadData()
        Locales.resolveLabels(this)
    }

    componentDidUpdate(){
        if(this.state.history.table == undefined
            || this.state.labels.locale==undefined
            || this.state.data.path==undefined){
           
            Spinner.show()
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Data thing if one
     */
    dataThing(){
        if(this.state.data.data.length>0){
            if(this.state.data.data[this.state.pathIndex].url.length>0){
                this.state.data.data[this.state.pathIndex].repaint=true
                return(
                    <Thing
                        data={this.state.data.data[this.state.pathIndex]}
                        recipient={this.state.identifier}
                        readOnly={!this.state.conclude}
                        narrow
                    />
                )
            }else{
                return []
            }
        }else{
            return []
        }
    }
    /**
     * Notes from previous step
     */
    prevNotes(){
        let mess=""
        if(this.state.data.notes.length>0){
            mess = this.state.data.notes[this.state.pathIndex]
        }
        return(
            <Row hidden={mess.length==0}>
                <Col>
                    <Alert>
                        {mess}
                    </Alert>
                </Col>
            </Row>
        )
    }

    /**
     * Has this activity been cancelled?
     */
    hasCancelled(){
        let cancelled=false
        if(this.state.data.cancelled.length>0){
            cancelled = this.state.data.cancelled[this.state.pathIndex]
        }
        return(
            <Row hidden={!cancelled}>
                <Col>
                    <Alert color='warning'>
                        {this.state.labels.activitycancelled}
                    </Alert>
                </Col>
            </Row>
        )
    }

    checkList(){
        if(this.state.data.path.length>0){
        return(
            <CheckList
                    historyId={this.state.data.path[this.state.pathIndex].historyId}
                    recipient={this.state.identifier} 
                    readOnly={!this.state.conclude}
            />
        )
        }else{
            return []
        }
    }

    /**
     * Activity data or send after submit data
     */
    content(){
        if(this.state.send){
            return(
                <Row>
                    <Col>
                        <ActivitySubmit historyId={this.state.data.historyId} reassign={this.state.reassign} supervisor={this.state.supervisor} recipient={this.state.identifier} />
                    </Col>
                </Row>
            )
        }else{
            return(
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6' className="overflow-auto scrollPnl p-0 m-0" style={{maxHeight:'70vh'}}>
                    {this.hasCancelled()}
                    {this.prevNotes()}
                    <Row>
                        <Col>
                            {this.application()}
                        </Col>
                    </Row>
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6' className="overflow-auto scrollPnl p-0 m-0" style={{maxHeight:'70vh'}}>
                    <Row>
                        <Col>
                            {this.dataThing()}
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {this.checkList()}
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
    }
    /**
     * Place name and buttons top and bottom
     * @returns Name and buttons
     */
    headerFooter(){
        return(
            <Row>
            <Col xs='12' sm='12' lg='8' xl='8'>
                <h4>{this.state.history.applName}</h4>
            </Col>
            <Col>
                <div className="mb-1 d-flex justify-content-end">
                        <Button size="sm"
                         className="mr-1" color="success"
                         hidden={!this.state.conclude || this.state.send}
                         onClick={()=>{
                             if(this.state.data.data[this.state.pathIndex].url.length>0){
                                 this.state.saveCounter=2
                             }else{
                                 this.state.saveCounter=1
                             }
                            Navigator.message(this.state.identifier,"*","saveAll",{})
                         }}
                        >{this.state.labels.global_save}</Button>{' '}

                        <Button size="sm"
                         className="mr-1" color="info"
                         onClick={()=>{
                            window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                         }}
                        >{this.state.labels.global_cancel}</Button>{' '}

                        <Button size="sm" hidden={!this.state.conclude || this.state.send || this.state.data.background}
                         className="mr-1" color="primary"
                         onClick={()=>{
                            if(this.state.data.data[this.state.pathIndex].url.length>0){
                                this.state.saveCounter=2    //save form and checklist
                            }else{
                                this.state.saveCounter=1    //save only checklist
                            }
                            this.state.submit=true
                            Navigator.message(this.state.identifier,"*","saveAll",{})
                         }}
                        >{this.state.labels.global_submit}</Button>{' '}

                        <Button size="sm" hidden={!this.state.conclude || this.state.send}
                         className="mr-1" color="warning"
                         onClick={()=>{
                            this.state.send=true
                            this.state.reassign=true
                            this.setState(this.state)
                         }}
                        >{this.state.labels.route_action}</Button>{' '}

                        <Button size="sm" hidden={!(this.state.conclude && this.state.data.background)}
                         className="mr-1" color="primary"
                         onClick={()=>{
                            if(this.state.data.data[this.state.pathIndex].url.length>0){
                                this.state.saveCounter=2    //save form and checklist
                            }else{
                                this.state.saveCounter=1    //save only checklist
                            }
                            this.state.done=true
                            Navigator.message(this.state.identifier,"*","saveAll",{})
                         }}
                        >{this.state.labels.done}</Button>{' '}
                        

                        <Button size="sm" hidden={!this.state.send}
                         className="mr-1" color="primary"
                         onClick={()=>{
                           Navigator.message(this.state.identifier, "*", "sendToNext", {})
                         }}
                        >{this.state.labels.send}</Button>{' '}
                    </div>
            </Col>
           
        </Row>
        )
    }

    render(){
        if(this.state.history.table == undefined
            || this.state.labels.locale==undefined
            || this.state.data.path==undefined){
           
            return[]
        }
        //determine conclude condition
        this.state.conclude=false
        if(this.state.data.path.length>0){
            this.state.conclude=!this.state.data.path[this.state.pathIndex].readOnly
        }
        Spinner.hide()
        return(
            <Container fluid>
               {this.headerFooter()}
                <Row>
                    <Col>
                        <Row hidden={this.state.data.identifier.length==0}>
                            <Col>
                                 <Alert className="p-0 m-0" color={this.state.color}>
                                     <small>{this.state.data.identifier}</small>
                                 </Alert>
                            </Col>
                        </Row>
                        <Row>
                            <Col className="btn btn-link p-0 border-0 d-flex justify-content-start"
                                onClick={()=>{
                                    this.state.hist=!this.state.hist
                                    this.setState(this.state)
                                }}>
                                <h6 className="ml-3">{this.state.labels.application_info}</h6>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <Collapse isOpen={this.state.hist}>
                                    <CollectorTable
                                        tableData={this.state.history.table}
                                        loader={this.loadHistory}
                                        headBackground={Pharmadex.settings.tableHeaderBackground}
                                        styleCorrector={(header)=>{
                                            if(header=='days'){
                                                return {width:'10%'}
                                            }
                                        }}
                                        selectRow={(rowNo)=>{
                                            let rows= this.state.history.table.rows
                                            rows.forEach((element,index) => {
                                                if(index!=rowNo){
                                                    element.selected=false
                                                }
                                            });
                                            rows[rowNo].selected=!rows[rowNo].selected
                                            if(rows[rowNo].selected){
                                                this.state.data.historyId=rows[rowNo].dbID
                                                Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/activity/history/is/monitoring",
                                                    this.state.data,(query,result)=>{
                                                    this.state.data=result
                                                    if(this.state.data.valid){
                                                        this.state.supervisor=true
                                                        this.state.send=true
                                                    }else{
                                                        this.state.supervisor=false
                                                        this.state.send=false
                                                        rows[rowNo].selected=false
                                                    }
                                                    this.setState(this.state)
                                                })
                                            }else{
                                                this.state.supervisor=false
                                                this.state.send=false
                                                this.setState(this.state)
                                            }
                                            
                                        }}
                                    />
                                </Collapse>
                            </Col>
                        </Row>
                        <Row hidden={this.state.data.identifier.length==0}>
                            <Col>
                                 <Alert className="p-0 m-0" color={this.state.color}>
                                     <small>{this.state.data.identifier}</small>
                                 </Alert>
                            </Col>
                        </Row>   
                    </Col>
                </Row>
                <Row className="mb-2">
                    <Col>
                    <Breadcrumb>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                </Row>
               
                {this.content()}
                
                <Row className="mt-2">
                    <Col>
                    <Breadcrumb>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                </Row>
                {this.headerFooter()}
            </Container>
        )
    }


}
export default ActivityManager
ActivityManager.propTypes={
    historyId:PropTypes.number.isRequired,  //ID of History table entry
    recipient:PropTypes.string,  //recepient for messaging   
}