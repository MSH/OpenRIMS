import React , {Component} from 'react'
import {Container,Card, CardBody, CardHeader, Row, Col, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import Thing from '../Thing'
import Pharmadex from '../Pharmadex'
import CheckList from '../CheckList'

/**
 * It is a dummy component to create other components quickly
 * Just copy it
 */
class ApplicationEventData extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:this.props.data,
            labels:{},
            ready:false
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
        this.leftContent=this.leftContent.bind(this)
        this.rightContent=this.rightContent.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=='thingLoaded'){
                    this.state.ready=true
                    this.setState(this.state)
                }
            }
            if(data.from==this.props.recipient){
                if(data.subject='reload_application_event'){
                    this.state.data=this.props.data
                    this.state.data.leftThing.repaint=true
                    this.state.data.rightThing.repaint=true
                    this.setState(this.state)
                    this.load()
                }
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
    }
    /**
     * load data for left and right column
     */
    load(){
        this.state.ready=false
        this.setState(this.state)
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/application/events/data", this.state.data, (query,result)=>{
            let selected = this.state.data.selected
            this.state.data=result
            this.state.data.selected=selected
            this.state.data.leftThing.repaint=true
            this.state.data.rightThing.repaint=true
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Content of left column
     */
    leftContent(){
        if(this.state.data.leftThing.nodeId>0){
            return(
                <Thing
                    data={this.state.data.leftThing}
                    recipient={this.state.identifier}
                    readOnly
                    narrow
                />
            )
        }else{
            return []
        }
    }
    /**
     * Content of the right column
     */
    rightContent(){
        if(this.state.data.checklist.historyId>0){
            return(
                <CheckList historyId={this.state.data.checklist.historyId}
                           recipient={this.state.identifier}
                           readOnly />
            )
        }else{
            return (
                <Thing
                    data={this.state.data.rightThing}
                    recipient={this.state.identifier}
                    readOnly
                    narrow
                />
            )
        }
    }

    render(){
        if(this.state.data.selected==0){
            return []
        }
        return(
            <Container fluid className={Pharmadex.settings.activeBorder}>
                <Card>
                    <CardHeader>
                        <Row>
                            <Col xs='11' sm='11' lg='2' xl='2'>
                                <h5>{this.state.data.eventDate}</h5>
                            </Col>
                            <Col xs='11' sm='11' lg='9' xl='9'>
                                <h5>{this.state.data.title}</h5>
                            </Col>
                            <Col xs='1' sm='1' lg='1' xl='1'>
                                <Button close
                                    onClick={()=>{
                                        Navigator.message(this.state.identifier, this.props.recipient, "closeEventData", {})
                                    }} 
                                />
                            </Col>
                        </Row>
                    </CardHeader>
                    <CardBody>
                        <Row>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <Row>
                                    <Col>
                                        <h5>{this.state.data.leftTitle}</h5>
                                    </Col>
                                </Row>
                                <Row>
                                    <Col>
                                        {this.leftContent()}
                                    </Col>
                                </Row>
                            </Col>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <Row>
                                    <Col>
                                        <h5>{this.state.data.rigthTitle}</h5>
                                    </Col>
                                </Row>
                                <Row>
                                    <Col>
                                        {this.rightContent()}
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                    </CardBody>
                </Card>
            </Container>
        )
    }


}
export default ApplicationEventData
ApplicationEventData.propTypes={
    data:PropTypes.object.isRequired,       //ApplicationEventDTO.java         
    recipient:PropTypes.string.isRequired
}