import React , {Component} from 'react'
import {Container, Row, Col, Breadcrumb, BreadcrumbItem, NavItem, Spinner} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import ButtonUni from './form/ButtonUni'
import Thing from './Thing'
import SpinnerMain from './utils/SpinnerMain'
import Pharmadex from './Pharmadex'
import Downloader from './utils/Downloader'
import ImportDataConfiguration from './ImportDataConfiguration'
/**
 * Responsible for workflow configuration process
 * Simialr to ThingManager
 * @example
 * <WorkflowConfigurator dictNodeId={this.state.selectedId} />
 */
class WorkflowConfigurator extends Component{
    constructor(props){
        super(props)
        this.state={
            showimport:false,
            wait:false,                             //for the first time
            identifier:Date.now().toString(),
            data:{},                                //WorkflowDTO
            labels:{
                global_save:'',
                global_cancel:'',
                global_suspend:'',
                global_import_short:'',
                workflows:'',
                next:'',
                insertbefore:'',
                exportExcel:'',
                warningRemove:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
        this.afterSave=this.afterSave.bind(this)
        this.content=this.content.bind(this)
        this.loader=this.loader.bind(this)
    }

    /**
     * The current thing is saved
     * data - the thing
     * byAction - saved by the button Save, otherwise saved by "next" 
     */
    afterSave(data, byAction){
        this.state.data.path[this.state.data.selected]=data
        this.setState(this.state)
        if(data.valid){
            if(byAction){
                //this.state.data.selected--
                if(this.state.data.selected>=0){
                    this.state.data.path[this.state.data.selected].repaint=true
                    this.setState(this.state)
                }else{
                    window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()+"/processes"
                }
            }else{
                this.state.data.selected++
                if(this.state.data.selected>=this.state.data.path.length){
                    Fetchers.postJSON("/api/admin/workflow/activity/add", this.state.data, (query,result)=>{
                        this.state.data=result
                        this.state.data.path[this.state.data.selected].repaint=true
                        this.setState(this.state)   //I believe that this.state.data.selected rests the same
                    })
                }else{
                    this.state.data.path[this.state.data.selected].repaint=true
                    this.setState(this.state)
                }
            }
        }else{
            this.setState(this.state)
        }
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=="thingLoaded"){
                    if(this.state.data.selected==0){
                        this.state.data.path[0]=data.data
                        this.state.thingIdentifier=data.data.uxIdentifier
                        SpinnerMain.hide()
                    }
                }
                if(data.subject=="saved"){
                    this.afterSave(data.data)
                }
                if(data.subject=="savedByAction"){
                    this.afterSave(data.data,true)
                }
                if(data.subject=='DataConfigurationImportCancel'){
                    this.state.showimport=false
                    this.loader()
                }
            }  
        }

    componentDidMount(){
        SpinnerMain.show()
        window.addEventListener("message",this.eventProcessor)
        this.state.data.dictNodeId=this.props.dictNodeId
        this.loader()
    }

    componentDidUpdate(){
        if(this.state.data.path[this.state.data.selected]!=undefined){
            SpinnerMain.hide()
        }
        
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
loader(){
    Fetchers.postJSON("/api/admin/workflow/configuration/load", this.state.data, (query,result)=>{
        this.state.data=result
        Locales.resolveLabels(this)
        this.setState(this.state)
        Navigator.message(this.state.identifier, "*", "thingReload", {})
        SpinnerMain.show()
    })
}

    createBreadCrumb(){
        let ret=[]
        ret.push(
            <BreadcrumbItem className="d-inline"  key='workflows'>
                <div className="btn btn-link p-0 border-0"
                    onClick={()=>{
                        window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()+"/processes"
                    }}
                >
                     <h6 className="d-inline">{this.state.labels.workflows}</h6>
                </div>
            </BreadcrumbItem>
        )
        if(Fetchers.isGoodArray(this.state.data.path)){
            this.state.data.path.forEach((thing,index) => {
                if(index<=this.state.data.selected){
                    if(index!=this.state.data.selected){
                        ret.push(
                            <BreadcrumbItem className="d-inline"  key={index}>
                                <div className="btn btn-link p-0 border-0"
                                    onClick={()=>{
                                SpinnerMain.show()
                                this.state.data.selected=index
                                this.state.data.path[index].repaint=true
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
        }
        ret.push(
            <BreadcrumbItem className="d-inline"  key={this.state.data.pathIndex+1}>
                <div className="btn btn-link p-0 border-0"
                    onClick={()=>{
                        Navigator.message(this.state.identifier, this.state.thingIdentifier, "save", {})
                    }}
                >
                    <h6 className="d-inline">{this.state.labels.next}</h6>
                </div>
            </BreadcrumbItem>
            )
        return ret
    }
    /**
     * Display a content
     */
    content(){
        return(
            <div hidden={this.state.showimport}>
            <Row>
            <Col>
                <Breadcrumb>
                    {this.createBreadCrumb()}
                </Breadcrumb>
            </Col>
        </Row>
        <Row>
            <Col>
                <Thing key='activity'
                    data={this.state.data.path[this.state.data.selected]}
                    recipient={this.state.identifier}
                    readOnly={false}
                />
            </Col>
        </Row>
        <Row>
            <Col>
                <Breadcrumb>
                    {this.createBreadCrumb()}
                </Breadcrumb>
            </Col>
        </Row>
        </div>
        )
    }
    render(){
        if(this.state.data.title==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        if(this.state.data.path== undefined){
            return Pharmadex.wait()
        }
        if(this.state.data.selected==-1){
            this.state.data.selected=0
        }
        return(
            <Container fluid>
                 <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                       <h4>{this.state.data.title}</h4>
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1' className={"text-center m-0 p-1"} hidden={this.state.showimport}>
                        <ButtonUni
                            label={this.state.labels.global_save}
                            onClick={()=>{
                                Navigator.message(this.state.identifier,"*","saveAll",{})
                            }}
                            color="success"
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1' className={"text-center m-0 p-1"} hidden={this.state.showimport}>
                        <ButtonUni
                            label={this.state.labels.global_cancel}
                            onClick={()=>{
                                window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()+"/processes"
                            }}
                            color="secondary"
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1' className={"text-center m-0 p-1"} hidden={this.state.showimport}>
                        <div hidden={this.state.data.selected==0}>
                            <ButtonUni
                                label={this.state.labels.insertbefore}
                                onClick={()=>{
                                    Fetchers.postJSON("/api/admin/workflow/activity/insert", this.state.data, (query,result)=>{
                                        this.state.data=result
                                        this.state.data.path.forEach(element=>{
                                            element.repaint=true
                                        })
                                        this.setState(this.state)
                                    })
                                }}
                                color="primary"
                            />
                        </div>
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1' className={"text-center m-0 p-1"} hidden={this.state.showimport}>
                        <div hidden={this.state.data.selected==0}>
                            <ButtonUni
                                label={this.state.labels.global_suspend}
                                onClick={()=>{
                                    Fetchers.alerts(this.state.labels.warningRemove, ()=>{
                                        Fetchers.postJSON("/api/admin/workflow/activity/suspend", this.state.data, (query,result)=>{
                                            this.state.data=result
                                            this.state.data.selected=0
                                            this.state.data.path.forEach(element=>{
                                                element.repaint=true
                                            })
                                            this.setState(this.state)
                                        })
                                    }, null)
                                }}
                                color="warning"
                            />
                        </div>
                    </Col>
                    {/* </Row>
                    <Row> */}
                    <Col xs='12' sm='12' lg='1' xl='1'className={"text-center m-0 p-1"} hidden={this.state.showimport}>
                    <div hidden={this.state.data.selected>0}>
                        <ButtonUni
                        label={this.state.labels.global_import_short}
                        onClick={()=>{
                            this.state.showimport=true;
                            this.setState(this.state)
                            Navigator.message(this.state.identifier, "*",'DataConfigurationImportReload',{})
                        }}
                        color="info"
                        />
                        </div>
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1' className={"text-center m-0 p-1"} hidden={this.state.showimport}>
                        <ButtonUni
                            label={this.state.labels.exportExcel}
                            onClick={()=>{
                                let downloader = new Downloader();
                                        Fetchers.setJustLoaded(this.state.data,false)
                                        downloader.postDownload("/api/admin/workflow/export/excel",
                                        this.state.data, "workflow.xlsx");
                            }}
                            color="secondary"
                        />
                    </Col>
                </Row>
                <Row hidden={!this.state.showimport}>
                    <Col xs='12' sm='12' lg='6' xl='6' >
                        <ImportDataConfiguration
                            dataID={this.state.data.path[0].nodeId}
                            recipient={this.state.identifier}
                            loadapi='/api/admin/data/workflow/load/import'
                            importapi='/api/admin/data/workflow/run/import' 
                        />
                    </Col>
                </Row>
              {this.content()}
            </Container>
        )
    }


}
export default WorkflowConfigurator
WorkflowConfigurator.propTypes={
    dictNodeId:PropTypes.number.isRequired,         //dictionary node id in "dictionary.guest.applications"
}