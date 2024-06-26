import React , {Component} from 'react'
import {Container, Row, Col, Progress} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Pharmadex from './Pharmadex'
import ButtonUni from './form/ButtonUni'
import Navigator from './utils/Navigator'


/**
 * ~~~
 * Progress informator for Async process
 *   events:
 *      OnAsyncProcessCompleted                //Async process has been completed
 *   properties:
 *   recipient:PropTypes.string.isRequired,   //The recipient of messages
 *   loadAPI :PropTypes.string.isRequired,   //e.g., '/api/reassign/appicant/progress'
 *   cancelAPI:PropTypes.string,             //e.g., '/api/reassign/applicant/cancel'
 * ~~~
 * @example
 * <AsyncInform recipient={this.state.identifier} loadAPI='/api/admin/reassign/appicant/progress/load' 
 *                              cancelAPI='/api/admin/reassign/appicant/progress/cancel' />
 */
class AsyncInform extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},                            //AsyncInformDTO.java
            labels:{
                global_close:'',
                global_cancel:'',
                stop:'',
                stopping:'',
            },
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.hideStopButton=this.hideStopButton.bind(this)
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
        this.loader();
    }

    loader(){
        Fetchers.postJSONNoSpinner(this.props.loadAPI, this.state.data, (query,result)=>{
            this.state.data=result
            this.timeoutID=setTimeout(this.loader,this.state.data.pollInSeconds*1000)
            if(this.state.data.cancelled){
                Navigator.message(this.state.identifier, this.props.recipient, "OnAsyncProcessCancelled",this.state.data)
            }else if(this.state.data.completed){
                Navigator.message(this.state.identifier, this.props.recipient, "OnAsyncProcessEnd",this.state.data)
            }
            this.setState(this.state)
        })
    }
    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
        if(this.timeoutID != undefined){
            clearTimeout(this.timeoutID)
        }
    }
    /**
     * Hide condition for Stop button
     */
    hideStopButton(){
        return this.state.data.completed || this.state.data.cancelled
    }

    render(){
        if(this.state.data.title==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row className='mb-5 d-flex align-items-center"'>
                    <Col xs='12' sm='12' lg='10' xl='11'>
                        <h4>{this.state.data.title}</h4>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='1' hidden={!this.hideStopButton()}>
                        <ButtonUni 
                            onClick={()=>{
                                if(this.timeoutID != undefined){
                                    clearTimeout(this.timeoutID)
                                }
                                Navigator.message(this.state.identifier, this.props.recipient, "OnAsyncProcessCompleted",this.state.data)
                            }}
                            label={this.state.labels.global_close}
                            color="info"
                        />
                    </Col>
                </Row>
                <Row className="d-flex align-items-center">
                    <Col xs='12' sm='12' lg='10' xl='11'>
                        <Progress color='success' 
                                animated={!this.hideStopButton()}
                                value={this.state.data.complPercent}>
                            {this.state.data.progressMessage}
                        </Progress>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='1' hidden={this.hideStopButton() || this.props.cancelAPI==undefined}>
                    <ButtonUni 
                            onClick={()=>{
                                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.stopping, color:'info'})
                                Fetchers.postJSONNoSpinner(this.props.cancelAPI, this.state.data, (query,result)=>{
                                    this.state.data=result
                                    this.setState(this.state)
                                })
                            }}
                            outline
                            label={this.state.labels.stop}
                            color="danger"
                        />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <small>{this.state.data.elapsedMessage}</small>
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default AsyncInform
AsyncInform.propTypes={
    recipient:PropTypes.string.isRequired,   //The recipient of messages
    loadAPI :PropTypes.string.isRequired,   //e.g., '/api/reassign/appicant/progress'
    cancelAPI:PropTypes.string,             //e.g., '/api/reassign/applicant/cancel'
}