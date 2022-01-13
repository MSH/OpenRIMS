import React , {Component} from 'react'
import {Container, Row, Col, Breadcrumb, BreadcrumbItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Thing from './Thing'
import SpinnerMain from './utils/SpinnerMain'
import Register from './Register'


/**
 * Responsible for edit/view multy-things activity
 * issues events:
 * cancelThing - ask to cancel an activity
 * save - ask to save a thing
 * reply to events:
 * thingLoaded - after thing succesfully loaded
 * saved after thing has been saved, no matter successfully or not
 * save_action - after thing has been saved by Save button
 * 
 *  <ThingsManager applicationUrl={this.props.data.url} 
                                        applDictNodeId={this.props.data.applDictNodeId}
                                        historyId={this.props.data.historyId}
                                        application                                     //load an application instead the current activity
                                        recipient={this.state.identifier}
                                        narrow
                                        readOnly/>
 * 
 */
class ThingsManager extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{                                      //ThingDTO.java path to fill out things                   
                applicationUrl:this.props.applicationUrl,        
                historyId:this.props.historyId,
                application:this.props.application,
                applDictNodeId:this.props.applDictNodeId,
                readOnly:this.props.readOnly,
                prefLabel:this.props.prefLabel,
                modiUnitId:this.props.modiUnitId,
            },                                            
            identifier:Date.now().toString(),           //address for messages for this object
            recipient:this.props.recipient,             //recipient of messages from this object
            labels:{
                next:'',
                next_step_error:'',
                of:'',
                conclude:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.paintThings=this.paintThings.bind(this)
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
        this.loadPath=this.loadPath.bind(this)
        this.afterSave=this.afterSave.bind(this)
        this.paintCurrentThing=this.paintCurrentThing.bind(this)
        this.createAuxBreadCrumb=this.createAuxBreadCrumb.bind(this)
        this.paintRegister=this.paintRegister.bind(this)
    } 
    /**
     * Propagate values from the master thing to the rest of path
     * @param {ThingDTO} pt 
     */
    propagateMaster(pt,data,indexInPath){
        pt.activityId=data.activityId
        pt.activityName=data.activityName
        pt.historyId=data.historyId
        pt.applicationUrl=data.applicationUrl
        pt.applName=data.applName
        pt.applDictNodeId=data.applDictNodeId
        if(pt.parentIndex==indexInPath){
            pt.parentId=data.nodeId
        }

    }


    /**
     * The current thing is saved
     * data - the thing
     * byAction - saved by the button Save, otherwise saved by "next" or "conclude" 
     */
    afterSave(data, byAction){
        
        if(data.valid){
            if(this.state.data.auxPath.length>0){
                this.state.data.auxPath[this.state.data.auxPathIndex]=data
                //process aux path
                let auxLength=this.state.data.auxPath.length
                if(this.state.data.auxPathIndex==0){
                    //propagate master to the rest of path
                    this.state.data.auxPath.forEach((pt,index)=>{
                        this.propagateMaster(pt,data,this.state.data.auxPathIndex)
                    })
                }
              
                //movement forward or backward, next has been pressed
                if(byAction){
                    //button Save has been pressed
                    if(this.state.data.auxPathIndex>0){
                        this.state.data.auxPathIndex--
                    }else{
                        this.state.data.auxPath=[]
                        this.state.data.auxPathIndex=0
                    }

                }else{
                    //NEXT has been pressed
                    if(this.state.data.auxPathIndex<(auxLength-1)){
                        this.state.data.auxPathIndex++
                    }else{
                        this.state.data.auxPath=[]
                        this.state.data.auxPathIndex=0
                    }
                }
                this.setState(this.state)
                return
            }else{
                ////////////////// REGULAR PATH ////////////////////////////////////////////
                this.state.data.path[this.state.data.pathIndex]=data
                if(this.state.conclude){
                    //message to ApplicaionStart
                    Navigator.message(this.state.identifier, this.props.recipient, "conclude", this.state.data.path[0])
                    return
                }
                if(data.pathIndex==0){
                    this.state.data.path.forEach((pt, index)=>{
                    //propagate master to the rest of path
                        this.propagateMaster(pt,data,this.state.data.pathIndex)
                    })
                }
                if(byAction){
                //movement backward,save has been pressed
                    if(this.state.data.pathIndex>0){
                        this.state.data.pathIndex--
                        this.setState(this.state)
                    }else{
                        Navigator.message(this.state.identifier, this.props.recipient,"cancelThing",{})
                    }
                }else{
                    //movement forward, next has been pressed
                    this.state.data.pathIndex++
                }
            }
            this.setState(this.state)
        }else{
            SpinnerMain.hide()
        }
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.from==this.props.recipient){
                if(data.subject=="nextTab"){
                    Navigator.message(this.state.identifier, this.state.thingIdentifier, "saveGuest", {})
                }
                if(data.subject=="saveGuest"){
                    Navigator.message(this.state.identifier,"*", "saveGuest",{})
                }
                if(data.subject=="saveAllGuest"){
                    Navigator.message(this.state.identifier,"*", "saveAllGuest",{})
                }
                if(data.subject=="submit"){
                    Navigator.message(this.state.identifier,"*","submit",{})
                }
                return
            }
            if(data.to==this.state.identifier){
                if(data.subject=="register_loaded"){
                    this.state.register=data.data
                }
                if(data.subject=="thingLoaded"){
                    this.state.thingIdentifier=data.data.identifier
                    if(this.state.data.auxPath.length>0){
                        this.state.data.auxPath[this.state.data.auxPathIndex]=data.data
                    }else{
                        this.state.data.path[this.state.data.pathIndex]=data.data
                    }
                    setTimeout(SpinnerMain.hide,500)
                }
                if(data.subject=="saved"){
                    this.afterSave(data.data)
                }
                if(data.subject=="savedByAction"){
                    this.afterSave(data.data,true)
                }
                //come to the auxiliary path loop
                if(data.subject=="auxPath"){
                    this.state.data.path[this.state.data.pathIndex]=data.data
                    this.state.data.auxPath=data.data.auxPath
                    this.state.data.auxPathIndex=0
                    this.setState(this.state)
                }
            }
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.state.recipient=this.props.recipient
        this.loadPath()
    }
    /**
     * load a path until submit for the current application/activity
     */
    loadPath(){
        SpinnerMain.show()
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/activity/path", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        } )
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * paint only the current thing. Edit mode
     */
    paintCurrentThing(){
        let index=0
        let data={}
        if(this.state.data.path != undefined && this.state.data.path[index] != undefined){
            index=this.state.data.pathIndex
            data=this.state.data.path[index]
            let norepaint = (index==0 && data.nodeId==0) //to avoid double register on the first form
            data.repaint=!norepaint                   //repaint it!
        }
        //aux has a priority!
        if(this.state.data.auxPath != undefined && this.state.data.auxPath.length>0){
            index=this.state.data.auxPathIndex
            data=this.state.data.auxPath[index]
            data.repaint=true  
        }
        return(
            <Row>
                <Col>
                    <Thing key='activity'
                        data={data}
                        recipient={this.state.identifier}
                        readOnly={false}
                        narrow={this.props.narrow}
                    />
                </Col>
            </Row>
                )
    }
    /**
     * Paint things from this.state.things
     */
    paintThings(){
        if(this.props.readOnly){
            let ret = []
            if(Fetchers.isGoodArray(this.state.data.path)){
                this.state.data.path.forEach((thing, index)=>{
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
                        narrow={this.props.narrow}
                    />
                    )
                })
            }
            return ret
        }else{
            return this.paintCurrentThing()
        }
    }
    /**
     * Auxiliary "loop" cycle
     */
    createAuxBreadCrumb(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.auxPath)){
                this.state.data.auxPath.forEach((thing, index)=>{
                    if(index<=this.state.data.auxPathIndex){
                        if(index!=this.state.data.auxPathIndex){
                            ret.push(
                                <BreadcrumbItem className="d-inline"  key={index}>
                                    <div className="btn btn-link p-0 border-0"
                                onClick={()=>{
                                    this.state.data.auxPathIndex=index
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
                    }})
            ret.push(
                <BreadcrumbItem className="d-inline"  key={this.state.data.pathIndex+1}>
                    <div className="btn btn-link p-0 border-0"
                        onClick={()=>{
                            SpinnerMain.show()
                            Navigator.message(this.state.identifier, this.state.thingIdentifier, "saveGuest", {})
                        }}
                    >
                        <h6 className="d-inline">{this.state.labels.next}</h6>
                    </div>
                </BreadcrumbItem>
            )
        }
        return ret
    }

    /**
     * Create a bold breadcrumb
     */
    createBreadCrumb(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.path)){
            this.state.data.path.forEach((thing, index)=>{
                if(index<=this.state.data.pathIndex){
                    if(index!=this.state.data.pathIndex || this.state.data.auxPath.length>0){
                        ret.push(
                            <BreadcrumbItem className="d-inline"  key={index}>
                                <div className="btn btn-link p-0 border-0"
                            onClick={()=>{
                                this.state.data.pathIndex=index
                                this.state.data.auxPath=[]
                                this.state.data.auxPathIndex=0
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
                }
            })
            if(this.state.data.pathIndex<this.state.data.path.length-1){
                    if(this.state.data.auxPath.length==0){
                        ret.push(
                        <BreadcrumbItem className="d-inline"  key={this.state.data.pathIndex+1}>
                            <div className="btn btn-link p-0 border-0"
                                onClick={()=>{
                                    SpinnerMain.show()
                                    Navigator.message(this.state.identifier, this.state.thingIdentifier, "saveGuest", {})
                                }}
                            >
                                <h6 className="d-inline">{this.state.labels.next}</h6>
                            </div>
                        </BreadcrumbItem>
                        )
                    }
            }
            //the concluder
            if(this.state.data.pathIndex==(this.state.data.path.length-1) && this.state.data.auxPath.length==0){
                Navigator.message(this.state.identifier,this.props.recipient,"hideNext",{})
                ret.push(
                    <BreadcrumbItem className="d-inline"  key={this.state.data.pathIndex+2}>
                        <div className="btn btn-link p-0 border-0"
                            onClick={()=>{
                                this.state.conclude=true
                                Navigator.message(this.state.identifier, this.state.thingIdentifier, "saveGuest", {})
                            }}
                        >
                            <h6 className="d-inline">{this.state.labels.conclude}</h6>
                        </div>
                    </BreadcrumbItem>
                )
            }
            //add auxpath, if one
            ret.push(this.createAuxBreadCrumb())
        }
        return ret
    }
    /**
     * paint register or not
     */
    paintRegister(){
        if(this.state.register != undefined){
            return(
                <Register
                    data={this.state.register}
                    recipient={this.state.identifier}
                    readonly
                />
            )
        }
    }
    render(){
        if(this.state.data.path == undefined){
            return []
        }
        let currentIndex=this.state.data.pathIndex+1;
        if(this.state.data.auxPath.length>0){
            currentIndex=currentIndex+this.state.data.auxPathIndex+1
        }
        return(
            <Container fluid>
                <Row hidden={this.props.readOnly}>
                    <Col className="d-flex justify-content-end">
                        <h4>{" " + currentIndex
                                    +" "+ this.state.labels.of +" " + (this.state.data.path.length+this.state.data.auxPath.length)}</h4>
                    </Col>
                    <Col>
                        {this.paintRegister()}
                    </Col>
                </Row>
                <Row hidden={this.props.readOnly}>
                    <Col>
                    <Breadcrumb>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.paintThings()}
                    </Col>
                </Row>
                <Row hidden={this.props.readOnly || this.state.thingIdentifier == undefined}>
                    <Col>
                        <Breadcrumb>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ThingsManager
ThingsManager.propTypes={
    applicationUrl:PropTypes.string,                //an url of the application, deprecated in favor to applDictNodeId
    applDictNodeId:PropTypes.number,                //application description's node
    historyId:PropTypes.number.isRequired,          //link to the history record.
    application:PropTypes.bool,                     //load an application instead the current activity
    recipient:PropTypes.string.isRequired,          //recipient for messages (identifier)
    narrow:PropTypes.bool,                          // single or double columns layout, default double
    readOnly:PropTypes.bool,                        // read only or not, default editable
    modiUnitId:PropTypes.number,                    // id of data unit selected to modify
    prefLabel:PropTypes.string,                     // preflabel by default
}