import React , {Component} from 'react'
import {Container, Row, Col, Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import Pharmadex from '../Pharmadex'
import ButtonUni from '../form/ButtonUni'
import FieldGuarded from '../form/FieldGuarded'
import ViewEdit from '../form/ViewEdit'
import ViewEditOption from '../form/ViewEditOption'

/**
 * Responsible for a data variable
 * @example
 * <DataVarForm nodeId={this.state.data.nodeId} varNodeId={this.state.data.varNodeId}
 *  recipient={this.state.identifier} />
 */
class DataVarForm extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{                              //DataVariableDTO
                nodeId:this.props.nodeId,
                varNodeId:this.props.varNodeId,
            },
            labels:{
                save:'',
                global_suspend:'',
                cancel:'',
                screenposition:'',
                auxiliarydata:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.buttons=this.buttons.bind(this)
        this.loadData=this.loadData.bind(this)
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
     * Load form data
     */
    loadData(){
        Fetchers.postJSONNoSpinner("/api/admin/data/configuration/variable/load", this.state.data, (query,result)=>{
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
                <div>
                    <Row>
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <ButtonUni
                                label={this.state.labels.save}
                                color='primary'
                                onClick={()=>{
                                    Fetchers.postJSONNoSpinner("/api/admin/data/configuration/variable/save", this.state.data, (query,result)=>{
                                        if(result.valid){
                                            Navigator.message(this.state.identifier, this.props.recipient,"formCancel",{})
                                        }else{
                                            if(result.strict){
                                                this.state.data=result
                                                this.setState(this.state)
                                            }else{
                                                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'warning'})
                                                Navigator.message(this.state.identifier, this.props.recipient,"formCancel",{})
                                            }
                                        }
                                    })
                                }}
                            />
                        </Col>
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <ButtonUni
                                disabled={this.state.data.varNodeId==0}
                                label={this.state.labels.global_suspend}
                                color="warning"
                                onClick={()=>{
                                    Fetchers.postJSONNoSpinner("/api/admin/data/configuration/variable/suspend", this.state.data, (query,result)=>{
                                        if(result.valid){
                                            Navigator.message(this.state.identifier, this.props.recipient,"formCancel",{})
                                        }else{
                                            this.state.data=result
                                            this.setState(this.state)
                                        }
                                    })
                                }}
                            />
                        </Col>
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <ButtonUni
                                    label={this.state.labels.cancel}
                                    color='info'
                                    onClick={()=>{
                                        Navigator.message(this.state.identifier, this.props.recipient,"formCancel",{})
                                    }}
                                />
                        </Col>
                    </Row>
                    <Row>
                        <Col hidden={this.state.data.valid}>
                            <Alert color="danger" className="p-0 m-0">
                                <small>{this.state.data.identifier}</small>
                            </Alert>       
                        </Col>
                    </Row>
                </div>
            )
        }

    render(){
        if(this.state.data.clazz == undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid className={Pharmadex.settings.activeBorder}>
                {this.buttons()}
                <Row>
                    <Col>
                        <FieldGuarded mode="text" attribute="varName" component={this} />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <FieldGuarded mode="text" attribute="varNameExt" component={this} />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <ViewEdit mode='textarea' component={this} attribute='description' edit />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <ViewEditOption component={this} attribute='clazz' edit />
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='3' xl='3'>
                        <ViewEdit mode="number" attribute="minLen" component={this} edit />
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='3'>
                        <ViewEdit mode="number" attribute="maxLen" component={this} edit />
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='3'>
                        <ViewEditOption attribute="required" component={this} edit />
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='3'>
                        <ViewEditOption attribute="mult" component={this} edit />
                    </Col>
                </Row>
                <Row>
                    <Col className="d-flex justify-content-center">
                        <h5>{this.state.labels.screenposition}</h5>
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <ViewEdit mode='number' attribute='row' component={this} edit/>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <ViewEdit mode='number' attribute='col' component={this} edit/>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <ViewEdit mode='number' attribute='ord' component={this} edit/>
                    </Col>
                </Row>
                <Row>
                    <Col className="d-flex justify-content-center">
                        <h5>{this.state.labels.auxiliarydata}</h5>
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='4' xl='6'>
                        <ViewEdit mode='text' attribute='url' component={this} edit/>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='6'>
                        <ViewEdit mode='text' attribute='dictUrl' component={this} edit/>
                    </Col>
                    
                </Row>
                <Row>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <ViewEdit mode='text' attribute='auxUrl' component={this} edit/>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <ViewEditOption attribute='readOnly' component={this} edit/>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <ViewEditOption attribute='unique' component={this} edit/>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <ViewEditOption attribute='prefLabel' component={this} edit/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <ViewEdit mode='text' attribute='fileTypes' component={this} edit/>
                    </Col>
                </Row>
                
                {this.buttons()}
            </Container>
        )
    }


}
export default DataVarForm
DataVarForm.propTypes={
    nodeId:PropTypes.number.isRequired,         //data collection node id (a data collection consists of variables)
    varNodeId:PropTypes.number.isRequired,      //variable node
    recipient:PropTypes.string.isRequired,      //recipient for messages
}