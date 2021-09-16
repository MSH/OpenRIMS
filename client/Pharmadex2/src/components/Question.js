import React , {Component} from 'react'
import {Row, Col, ButtonGroup, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import ViewEdit from './form/ViewEdit'


/**
 * Checklists question or header
 * 
 */
class Question extends Component{
    constructor(props){
        super(props)
        this.state={
            notes:false,
            descr:false,
            data:this.props.data,
            labels:this.props.labels
        }
        this.eventProcessor=this.eventProcessor.bind(this)
    }
        /**
     * listen for askData broadcast and getData only to own address
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            
        }
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.state.data.justloaded=false
    }
    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    componentDidUpdate(){
        if(this.props.data.justloaded){
            this.state.data=this.props.data
            this.state.data.justloaded=false
            this.setState(this.state)
        }
        if(this.props.data.valid != this.state.data.valid){
            this.state.data=this.props.data
            this.state.data.justloaded=false
            this.setState(this.state)
        }
    }
 
    render(){
        let border="mb-1"
        if(!this.state.data.valid){
            border="mb-1 border border-danger"
        }
        if(this.state.data.head){
            return(
                <Row className={border}>
                    <Col>
                        <h6>{this.state.data.question}</h6>
                    </Col>
                </Row>
            )
        }else{
            return(
                <Row className={border}>
                    <Col>
                        <Row className="p-0 m-0">
                            <Col xs='12' sm='12' lg='12' xl='7' className="p-0 m-0">
                                {this.state.data.question}
                            </Col>
                            <Col xs='12' sm='6' lg='6' xl='3' className="p-0 m-0">
                                <ButtonGroup size="sm">
                                    <Button
                                        outline
                                        color='success'
                                        active={this.state.data.answer==1}
                                        onClick={()=>{
                                            if(!this.props.readOnly){
                                                this.state.data.answer=1
                                                this.setState(this.state)
                                            }
                                        }}
                                    >
                                        {this.state.labels.global_yes}</Button>
                                    <Button
                                        outline
                                        color='info'
                                        active={this.state.data.answer==2}
                                        onClick={()=>{
                                            if(!this.props.readOnly){
                                                this.state.data.answer=2
                                                this.setState(this.state)
                                            }
                                        }}
                                    >
                                        {this.state.labels.global_no}</Button>
                                    <Button
                                        outline
                                        color='warning'
                                        active={this.state.data.answer==3}
                                        onClick={()=>{
                                            if(!this.props.readOnly){
                                                this.state.data.answer=3
                                                this.setState(this.state)
                                            }
                                        }}
                                    >
                                        {this.state.labels.global_na}</Button>
                                </ButtonGroup>
                            </Col>
                            <Col xs='12' sm='6' lg='6' xl='2' className="p-0 m-0">
                                <ButtonGroup size="sm">
                                    <Button
                                        outline
                                        active={this.state.notes}
                                        onClick={()=>{
                                            if(!this.props.readOnly){
                                                this.state.notes=!this.state.notes
                                                this.setState(this.state)
                                        }}
                                        }
                                    >
                                        {this.state.labels.notes}</Button>
                                    <Button
                                        outline
                                        active={this.state.descr}
                                        onClick={()=>{
                                            //if(!this.props.readOnly){
                                                this.state.descr=!this.state.descr
                                                this.setState(this.state)
                                            //}
                                        }}
                                        hidden={this.state.data.description == ""}
                                    >
                                        {this.state.labels.global_help}</Button>
                                </ButtonGroup>
                            </Col>
                        </Row>
                        <Row hidden={!(this.state.notes || this.state.data.comment.value.length>0)}>
                            <Col>
                                <ViewEdit mode='textarea' lines={4} attribute='comment' component={this} edit={!this.state.readOnly} />
                            </Col>
                        </Row>
                        <Row hidden={!this.state.descr}>
                            <Col>
                                 <small><b>{this.state.data.description}</b></small>   
                            </Col>
                        </Row>

                    </Col>
                </Row>
            )
        }

    }
}
export default Question
Question.propTypes={
    data:PropTypes.object.isRequired,           //QuestionDTO
    index:PropTypes.number.isRequired,          //index in the array of questions
    labels:PropTypes.object.isRequired,         //labels from top level to avoid redundant label's loading
    recipient:PropTypes.string.isRequired,       //receiver for messages  
    readOnly:PropTypes.bool,                    //read only     
}