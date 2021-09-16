import React , {Component} from 'react'
import {Container, Row, Col, Breadcrumb, BreadcrumbItem, Card, CardHeader,CardBody, CardFooter} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import Thing from '../Thing'
import Pharmadex from '../Pharmadex'

/**
 * Responsible to edit amendment data
 * Issue message "amendmentIsReady" with the own identifier
 * Reply to message "amendmentConclude"
 * Just copy it
 */
class Amendment extends Component{
    constructor(props){
        super(props)
        this.state={
            data:this.props.data,               //AmendmentDTO
            identifier:Date.now().toString(),
            labels:{
                next:'',
                global_curr_status:'',
                amendment:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.createBreadCrumb=this.createBreadCrumb
        this.paintCurrentThing=this.paintCurrentThing.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=="auxPath"){
                    this.state.data.path=data.data.auxPath
                    this.state.data.pathIndex=0
                    this.setState(this.state)
                }
                if(data.subject=="thingValidated"){
                    let path = this.state.data.path
                    if(Fetchers.isGoodArray(path)){
                        if(this.state.data.pathIndex<path.length){
                            path[this.state.data.pathIndex]=data.data
                            if(data.data.valid){
                                this.state.data.pathIndex++ 
                                if(this.state.data.pathIndex==path.length){
                                    Navigator.message(this.state.identifier, this.props.recipient, "onAmendmentReady", this.state.data)
                                }
                            }
                            this.setState(this.state)
                        }
                    }else{
                        this.state.data.chapter=data.data
                        if(this.state.data.chapter.valid){
                            Navigator.message(this.state.identifier, this.props.recipient, "onAmendmentReady", this.state.data)
                        }else{
                            this.setState(this.state)
                        }
                    }
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



    createBreadCrumb(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.path)){
            ret.push(
                <BreadcrumbItem className="d-inline"  key='index'>
                    <div className="btn btn-link p-0 border-0"
                        onClick={()=>{
                            this.state.data.pathIndex=0
                            this.state.data.path=[]
                            this.setState(this.state)
                        }}
                    >
                    <h6 className="d-inline">{this.state.data.chapterTitle}</h6>
                </div>
            </BreadcrumbItem>
            )
            this.state.data.path.forEach((thing, index)=>{
                if(index<=this.state.data.pathIndex){
                    if(index!=this.state.data.pathIndex){
                        ret.push(
                            <BreadcrumbItem className="d-inline"  key={index}>
                                <div className="btn btn-link p-0 border-0"
                            onClick={()=>{
                                this.state.data.pathIndex=index
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
        if(this.state.data.pathIndex<=this.state.data.path.length-1 || this.state.data.path.length==0){
            ret.push(
            <BreadcrumbItem className="d-inline"  key={this.state.data.pathIndex+1}>
                <div className="btn btn-link p-0 border-0"
                    onClick={()=>{
                        let thing=this.state.data.path[this.state.data.pathIndex]
                        Navigator.message(this.state.identifier, "*", "validateThing", {})
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
     * paint only the current thing. Edit mode
     */
     paintCurrentThing(){
        let index=0
        let data={}
        if(this.state.data.path.length>0){
            index=this.state.data.pathIndex
            if(index==this.state.data.path.length){
                index--
            }
            data=this.state.data.path[index]
        }else{
            this.state.data.chapter={
                nodeId:this.state.data.chapter.nodeId,
                parentId:this.state.data.appl.nodeId,
                repaint:false
            }
            data=this.state.data.chapter
        }
        data.repaint=data.valid &&  this.state.data.path.length>0                //repaint it!
        return(
            <Row>
                <Col>
                    <Card className={Pharmadex.settings.activeBorder}>
                        <CardHeader>
                            {this.state.labels.amendment}
                        </CardHeader>
                        <CardBody>
                            <Row>
                                <Col>
                                    <Breadcrumb className='m-0 p-0'>{this.createBreadCrumb()}</Breadcrumb>
                                </Col>
                            </Row>
                                <Thing key='activity12'
                                data={data}
                                recipient={this.state.identifier}
                                readOnly={false}
                            />
                            <Row className='mt-1'>
                                <Col>
                                    <Breadcrumb className='m-0 p-0'>{this.createBreadCrumb()}</Breadcrumb>
                                </Col>
                            </Row>
                        </CardBody>
                        <CardFooter>
                            {this.state.labels.amendment}
                        </CardFooter>
                    </Card>
                </Col>
            </Row>
                )
    }

    render(){
        if(this.state.data.path==undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col className='mt-1'>
                        {this.paintCurrentThing()}
                    </Col>
                </Row>

            </Container>
        )
    }


}
export default Amendment
Amendment.propTypes={
    data:PropTypes.object.isRequired,       //AmendmentDTO
    recipient:PropTypes.string.isRequired,  //the recipient of messages
}