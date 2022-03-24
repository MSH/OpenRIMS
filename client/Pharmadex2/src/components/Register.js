import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import ViewEditDate from './form/ViewEditDate'
import ViewEdit from './form/ViewEdit'
import Navigator from './utils/Navigator'
import ButtonUni from './form/ButtonUni'
import Fetchers from './utils/Fetchers'
/**
 * Responsible for the REgister component 
 * 
 */
class Register extends Component{
    constructor(props){
        super(props)
        this.state={
            readOnly:this.props.readOnly,
            data:{},            //RegisterDTO
            identifier:Date.now().toString(),
            labels:{
                registration_date:'',
                reg_number:'',
                expiry_date:'',
                get_number:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
    }

    /**
     * Create a register for Thing.js
     * @returns a scheduler ready to place to Thing.js
     */
    static place(res,index, readOnly,recipient,label){
        if(res!=undefined){
            Navigator.message(Date.now().toString(), recipient, "register_loaded", res)
            return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{label}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Register data={res}
                                            recipient={recipient}
                                            readOnly={readOnly || res.readOnly}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
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
        this.state.data=this.props.data
        Locales.createLabels(this)
        Locales.resolveLabels(this)
    }

    componentDidUpdate(){
        if(this.props.data.registration_date.justloaded || this.props.readOnly!=this.state.readOnly){
            this.state.data=this.props.data
            delete this.props.data.registration_date.justloaded
            this.state.readOnly=this.props.readOnly
            this.setState(this.state)
        }else{
            this.props.data.registration_date=this.state.data.registration_date
            this.props.data.reg_number=this.state.data.reg_number
            this.props.data.expiry_date=this.state.data.expiry_date
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        if(this.state.labels.locale == undefined || this.state.data.reg_number==undefined){
            return []
        }
        let edit = !(this.state.data.readOnly || this.props.readOnly)
        if(edit){
            return(
                <Container fluid>
                    <Row>
                        <Col>
                            <ViewEditDate attribute='registration_date' component={this} edit={edit} />
                        </Col>
                    </Row>
                    <Row>
                        <Col xs='12' sm='12' lg='6'xl='6'>
                            <ViewEdit mode='text' attribute='reg_number' component={this} edit={edit}/>
                        </Col>
                        {/* ik proba button*/}
                        <Col xs='12' sm='12' lg='3' xl='3'> 
                        <ButtonUni
                                label={this.state.labels.get_number}
                                color='success'
                                onClick={()=>{
                                        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName() +"/register/number/new", this.state.data, (query,result)=>{
                                            this.state.data=result
                                            this.setState(this.state)
                                            Navigator.message(this.state.identifier, this.props.recipient,"onSelectionChange", this.state.data)
                                        })
                                }}
                            />
                    </Col> 
                    </Row>
                    <Row hidden={!this.state.data.expirable}>
                        <Col>
                            <ViewEditDate attribute='expiry_date' component={this} edit={edit} />
                        </Col>
                    </Row>

                </Container>
            )
        }else{
            return(
                <Row>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <ViewEditDate attribute='registration_date' component={this} edit={edit} nolabel/>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <ViewEdit mode='text' attribute='reg_number' component={this} edit={edit} nolabel/>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4' hidden={!this.state.data.expirable}>
                        <ViewEditDate attribute='expiry_date' component={this} edit={edit} nolabel/>
                    </Col>
                </Row>
            )
        }
    }
}
export default Register
Register.propTypes={
    data:PropTypes.object.isRequired, //RegisterDTO
    recipient:PropTypes.string.isRequired,
    readOnly:PropTypes.bool
}