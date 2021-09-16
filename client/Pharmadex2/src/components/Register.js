import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import ViewEditDate from './form/ViewEditDate'
import ViewEdit from './form/ViewEdit'
/**
 * Responsible for the REgister component 
 * 
 */
class Register extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},            //RegisterDTO
            identifier:Date.now().toString(),
            labels:{
                registration_date:'',
                reg_number:'',
                expiry_date:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
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
        if(this.props.data.registration_date.justloaded){
            this.state.data=this.props.data
            delete this.props.data.registration_date.justloaded
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
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <ViewEditDate attribute='registration_date' component={this} edit />
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6'xl='6'>
                        <ViewEdit mode='text' attribute='reg_number' component={this} edit/>
                    </Col>
                    <Col xs='12' sm='12' lg='6'xl='6'>
                        <ViewEdit mode='text' attribute='prev' component={this}/>
                    </Col>
                </Row>
                <Row hidden={!this.state.data.expirable}>
                    <Col>
                        <ViewEditDate attribute='expiry_date' component={this} edit />
                    </Col>
                </Row>

            </Container>
        )
    }
}
export default Register
Register.propTypes={
    data:PropTypes.object.isRequired, //RegisterDTO
    recipient:PropTypes.string.isRequired,
    readonly:PropTypes.bool
}