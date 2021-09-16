import React , {Component} from 'react'
import {Container, Row, Col,Breadcrumb, BreadcrumbItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import ButtonUni from '../form/ButtonUni'
import AmendmentSelect from './AmendmentSelect'
import Amendment from './Amendment'
import AmendmentSubmit from './AmendmentSubmit'
import Alerts from '../utils/Alerts'

/**
 * Responsible for amendment application start
 */
class AmendmentStart extends Component{
    constructor(props){
        super(props)
        this.state={
            index:0,
            identifier:Date.now().toString(),
            data:this.props.data,   //AmendmentDTO
            labels:{
                save:'',
                cancel:'',
                amendment_details:'',
                conclude:'',
                amendment:'',
                submit_action:'',
                willbelater:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.header=this.header.bind(this)
        this.breadCrumb=this.breadCrumb.bind(this)
        this.content=this.content.bind(this)
        this.loader=this.loader.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){

                if(data.subject=="onAmendmentSelect"){
                    this.state.data=data.data
                    if(this.state.data.chapter.nodeId>0){
                        this.state.index=1
                        this.state.data.path=[]
                        this.state.data.pathIndex=0
                    }
                    this.setState(this.state)
                }
                if(data.subject=="onAmendmentReady"){
                    this.state.data=data.data
                    this.state.index=2
                    this.setState(this.state)
                }
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loader()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * load an application
     */
    loader(){
        Fetchers.postJSONNoSpinner("/api/guest/application/amendment/load", this.state.data, (query,result)=>{
            this.state.data=result;
            this.setState(this.state)
        })
    }
    /**
     * Save and submit or only save
     * @param {bool} submit - perform the submit 
     */
    submit(submit){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/amendment/save", this.state.data, (query, result)=>{
            this.state.data=result
            if(this.state.data.valid){
                if(submit){
                    
                }else{
                    Navigator.message(this.state.identifier, this.props.recipient, "amendmentSaved", this.state.data)
                }
            }
            this.setState(this.state)
        })
    }
    /**
     * Name and buttons
     */
    header(){
        if(this.state.data.title==undefined){
            return []
        }
        if(this.state.data.title.length==0){
            this.state.data.title=this.state.labels.amendment_details
        }
        return(
        <Row>
            <Col xs='12' sm='12' lg='9' xl='9'>
                <h4>{this.state.data.title}</h4>
            </Col>
            <Col xs='12' sm='12' lg='1' xl='1'>
                <div hidden={this.state.index<2}>
                    <ButtonUni
                        label={this.state.labels.save}
                        onClick={()=>{
                           this.submit(false)
                        }} 
                        color="primary"
                    />
                </div>
            </Col>
            <Col xs='12' sm='12' lg='1' xl='1'>
                <div hidden={this.state.index<2}>
                    <ButtonUni
                        label={this.state.labels.submit_action}
                        onClick={()=>{
                            Alerts.show(this.state.labels.willbelater,0)
                        }} 
                        color="success"
                    />
                </div>
            </Col>
            <Col xs='12' sm='12' lg='1' xl='1'>
                <ButtonUni
                    label={this.state.labels.cancel}
                    onClick={()=>{
                        Navigator.navigate(Navigator.tabName(),"amendments")
                    }} 
                    color="info"
                />
            </Col>
        </Row>
        )

    }
    /**
     * Navigation
     */
    breadCrumb(){
        let ret=[]
        if(this.state.index>0){
            ret.push(
                <BreadcrumbItem className="d-inline"  key='1'>
                    <div className="btn btn-link p-0 border-0"
                        onClick={()=>{
                            this.state.index=0
                            this.state.data.path=[]
                            this.state.data.pathIndex=0
                            this.state.data.chapter.nodeId=0
                            this.setState(this.state)
                        }}
                    >
                    <h6 className="d-inline">{this.state.labels.amendment_details}</h6>
                    </div>
                </BreadcrumbItem>
            )
        }
        if(this.state.index==0){
            ret.push(
                <BreadcrumbItem className="d-inline"  key='2'>
                    <h6 className="d-inline">{this.state.labels.amendment_details}</h6>
                </BreadcrumbItem>
            )
        }
        if(this.state.index==1){
            ret.push(
                <BreadcrumbItem className="d-inline"  key='4'>
                    <h6 className="d-inline">{this.state.data.chapterTitle}</h6>
                </BreadcrumbItem>
            ) 
        }
        if(this.state.index==2){
            ret.push(
                <BreadcrumbItem className="d-inline"  key='3'>
                    <div className="btn btn-link p-0 border-0"
                        onClick={()=>{
                            this.state.index=1
                            this.state.data.path=[]
                            this.state.data.pathIndex=0
                            this.setState(this.state)
                        }}
                    >
                    <h6 className="d-inline">{this.state.data.chapterTitle}</h6>
                    </div>
                </BreadcrumbItem>
            )
            ret.push(
                <BreadcrumbItem className="d-inline"  key='7'>
                    <h6 className="d-inline">{this.state.labels.conclude}</h6>
                </BreadcrumbItem>
            ) 
        }
        return ret
    }
    /**
     * Content to fill data
     */
    content(){
        switch(this.state.index){
            case 0:
                return(
                    <AmendmentSelect data={this.state.data} recipient={this.state.identifier} />
                )
            case 1:
                return(
                    <Amendment data={this.state.data} recipient={this.state.identifier}  />
                )
            case 2:
                return(
                    <AmendmentSubmit data={this.state.data} recipient={this.state.identifier}  />
                )
        }

    }
    render(){
        if(this.state.data.table==undefined){
            return []
        }
        return(
            <Container fluid>
               {this.header()}
               <Breadcrumb>
                {this.breadCrumb()}
               </Breadcrumb>
               {this.content()}
               <Breadcrumb>
                {this.breadCrumb()}
               </Breadcrumb>
               {this.header()}
            </Container>
        )
    }


}
export default AmendmentStart
AmendmentStart.propTypes={
    data:PropTypes.object.isRequired,       //AmendmentDTO
}