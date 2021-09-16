import React , {Component} from 'react'
import {Container, Row, Col, Button, NavItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import ButtonUni from '../form/ButtonUni'
import ViewEdit from '../form/ViewEdit'
import Pharmadex from '../Pharmadex'
import FieldGuarded from '../form/FieldGuarded'

/**
 * Form to add/edit/display a definition of a data collection
 */
class DataCollForm extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{
                nodeId:this.props.nodeId
            },            //DataCollectionDTO
            labels:{
                save:'',
                global_suspend:'',
                cancel:'',
                duplicate:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadData=this.loadData.bind(this)
        this.buttons=this.buttons.bind(this)
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
        this.loadData()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Load a data
     */
    loadData(){
        Fetchers.postJSONNoSpinner("/api/admin/data/collection/definition/load", this.state.data, (query,result)=>{
            this.state.data=result
            Locales.createLabels(this);
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
    }
    /**
     * Control buttons
     * @returns 
     */
    buttons(){
        return(
            <Row>
                <Col xs='12' sm='12' lg='3' xl='3'>
                    <ButtonUni
                        label={this.state.labels.save}
                        color='primary'
                        onClick={()=>{
                            Fetchers.postJSONNoSpinner("/api/admin/data/collection/definition/save", this.state.data, (query,result)=>{
                                this.state.data=result
                                if(this.state.data.valid){
                                    Navigator.message(this.state.identifier, this.props.recipient,"formCollCancel",{})
                                }else{
                                    this.setState(this.state)
                                }
                            })
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='3' xl='3'>
                    <ButtonUni
                        disabled={this.state.data.nodeId==0}
                        label={this.state.labels.global_suspend}
                        color="warning"
                        onClick={()=>{
                            Fetchers.postJSONNoSpinner("/api/admin/data/collection/definition/suspend", this.state.data, (query,result)=>{
                                Navigator.message(this.state.identifier, this.props.recipient,"formCollCancel",{})
                            })
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='3' xl='3'>
                    <ButtonUni
                            label={this.state.labels.cancel}
                            color='info'
                            onClick={()=>{
                                Navigator.message(this.state.identifier, this.props.recipient,"formCollCancel",{})
                            }}
                        />
                </Col>
                <Col xs='12' sm='12' lg='3' xl='3'>
                    <ButtonUni
                            label={this.state.labels.duplicate}
                            color='secondary'
                            onClick={()=>{
                                Fetchers.postJSON("/api/admin/data/collection/definition/duplicate", this.state.data, (query,result)=>{
                                    if(this.state.data.valid){
                                        Navigator.message(this.state.identifier, this.props.recipient,"formCollCancel",{})
                                    }else{
                                        this.state.data=result
                                        this.setState(this.state.data)
                                    }
                                })
                                
                            }}
                        />
                </Col>
            </Row>
        )
    }
    render(){
        if(this.state.data.url==undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid className={Pharmadex.settings.activeBorder}>
                {this.buttons()}
                    <FieldGuarded mode="text" attribute="url" component={this} />
                <Row>
                    <Col>
                        <ViewEdit mode='textarea' component={this} attribute='description' edit />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default DataCollForm
DataCollForm.propTypes={
    nodeId:PropTypes.number.isRequired,     //node id of a data collection
    recipient:PropTypes.string.isRequired,  //recipient for messaging
}