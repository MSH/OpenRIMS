import React , {Component} from 'react'
import {Container,Row, Col, FormGroup, Label, Input} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import FieldInput from './form/FieldInput'
import FieldDisplay from './form/FieldDisplay'
import ButtonUni from './form/ButtonUni'
import Navigator from './utils/Navigator'

/**
 * A component responsible for edit/display a concept's node and all literals defined for it
 * Answers to askData by DictNodeDTO
 * @example
 * <DictNode identifier={this.props.identifier+".node"}
                                    nodeId='0'
                                    parentId={this.state.data.selection.value.id}
                                    url={this.state.data.url}          
                                    onCancel={()=>{this.state.edit=false
                                                   this.setState(this.state)}
                                    }
                        />
Provides static procedure createFields(literals, component) to create literals directly to a component
@example
DictNode.createFields(this.state.data.literals, this)
Provides static procedure rewriteLabels to rewrite prefLAbel and description labels
@example
DictNode.rewriteLabels(this.state.data.node, this)
 */
class DictNode extends Component{
    constructor(props){
        super(props)
        this.state={
            labels:{
                save:'',
                cancel:'',
                global_suspend:'',
                warningRemove:'',
                workflows:'',
                validate:'',
            },
            data:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
        this.buildButtons=this.buildButtons.bind(this)
        this.cancelButton=this.cancelButton.bind(this)
        this.suspendButton=this.suspendButton.bind(this)
        this.saveButton=this.saveButton.bind(this)
        this.assistButton=this.assistButton.bind(this)
        this.processButton=this.processButton.bind(this)
    }

    /**
     * Processor for all events
     * @param {object} event 
     */
     eventProcessor(event){
        let data=event.data
        if(data.from != this.props.identifier && (data.to==data.to==this.props.identifier)){
            if(data.subject=="askData"){
                Navigator.message(this.props.identifier,from, "onGetData", this.state.data)
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor);
        this.load()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * create or load literals in object of DictElementDTO class
     */
    load(){
        this.state.data.url=this.props.url
        this.state.data.parentId=this.props.parentId
        this.state.data.nodeId=this.props.nodeId
        Fetchers.postJSONNoSpinner("/api/common/literals/load", this.state.data, (query,result)=>{
            this.state.data=result
            Locales.createLabels(this,"literals")
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
    }

    /**
     * Create FiedInputs in a column
     * @param {map<key,FormFieldDTO<String>} literals  
     * @param {object} component - this component
     * @returns FieldInput in one column
     * @example
     * DictNode.createFields(this.state.data.literals, this)
     */
    static createFields(literals, component, lines){
        let ret=[]
        if(lines==undefined){
            lines="4"
        }
        let keys = Object.keys(literals)
        if(Fetchers.isGoodArray(keys)){
            keys.forEach((key, index)=>{
                if(literals[key].value != undefined){
                    let mode="text"
                    if(key=="description" || key=="prefLabel" || literals[key].textArea){
                        mode="textarea"
                    }else{
                        mode="text"
                    }
                    if(literals[key].readOnly){
                        ret.push(
                            <Row key={index}>
                                <Col>
                                    <FieldDisplay mode={mode} attribute={key} component={component} data={literals} rows={lines}/>
                                </Col>
                            </Row>
                        )
                    }else{
                        ret.push(
                            <Row key={index}>
                                <Col>
                                    <FieldInput mode={mode} attribute={key} component={component} data={literals} rows={lines}/>
                                </Col>
                            </Row>
                    )
                    }
                }else{
                    delete literals[key]
                }
            }
        )
        }
        return ret
    }
    /**
     * Customize labels for prefLabel and description fields
     * @param {DictNodeDTO} node 
     * @param {ReactJS} component 
     * @example
     * DictNode.rewriteLabels(this.state.data.node, this)
     */
    static rewriteLabels(node, component){
        if(node != undefined && node.mainLabels != undefined){
            if(node.mainLabels.prefLabel != undefined){
                component.state.labels.prefLabel=node.mainLabels.prefLabel
            }
            if(node.mainLabels.description != undefined){
                component.state.labels.description=node.mainLabels.description
            }
        }
    }

    saveButton(){
        return (<ButtonUni
            label={this.state.labels.save}
            color='primary'
            onClick={()=>{
                Fetchers.postJSONNoSpinner("/api/admin/dictionary/node/save", this.state.data,(query,result)=>{
                    this.state.data=result
                    if(this.state.data.valid){
                        this.props.onCancel()
                    }else{
                        this.setState(this.state)
                    }
                })
            }}
        />)
    }

    suspendButton(){
        return (
            <ButtonUni
                disabled={!this.state.data.leaf || this.props.nodeId==0}
                label={this.state.labels.global_suspend}
                color="warning"
                onClick={()=>{
                    Fetchers.alerts(this.state.labels.warningRemove, ()=>{
                        Fetchers.postJSONNoSpinner("/api/admin/dictionary/node/suspend",this.state.data,(query,result)=>{
                            this.state.data=result
                            if(this.state.data.valid){
                                this.props.onCancel()
                            }else{
                                this.setState(this.state)
                            }
                        })
                    }, null)
                }}
            />
        )
    }

    cancelButton(){
        return (
        <ButtonUni
            label={this.state.labels.cancel}
            color='secondary'
            outline
            onClick={this.props.onCancel}
        />
        )
    }
    /**
     * Workflow assistance 
     * @returns button that issue a "onWorkflowAssist" message
     */
    assistButton(){
        return (
        <ButtonUni
            label={this.state.labels.validate}
            color='info'
            onClick={()=>{
                Navigator.message(this.props.identifier, "*", "onWorkflowAssist", this.state.data)
            }}
        />
        )
    }
    /**
     * 
     * @returns button to run workflow configuration
     */
    processButton(){
        return(
            <ButtonUni
                label={this.state.labels.workflows}
                color='primary'
                disabled={this.props.nodeId==0}
                onClick={()=>{
                    let param={
                        dictNodeId:this.props.nodeId
                    }
                    let paramStr=JSON.stringify(param)
                    if(paramStr.length>2){
                        Navigator.navigate("administrate", "workflowconfigurator",paramStr)
                    } 
                }}
            />
        )
    }

    buildButtons(){
        if(this.props.processButtons){
            return(
                <Row>
                    <Col xs='12' sm='12' lg='3' xl='2'>
                        {this.saveButton()}
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='2'>
                        {this.suspendButton()}
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='2'>
                        {this.cancelButton()}
                    </Col>
                    <Col xs='12' sm='12' lg='12' xl='6'>
                        <Row>
                            <Col xs='0' sm='0' lg='1' xl='2' className='d-flex justify-content-end align-items-center'>
                            <i className="fas fa-grip-lines-vertical fa-lg" style={{color: '#d1d3d6'}}></i>
                            </Col>
                            <Col xs='12' sm='12' lg='5' xl='5' className="d-flex justify-content-end">
                                {this.assistButton()}
                            </Col>
                            <Col xs='12' sm='12' lg='6' xl='5' className="d-flex justify-content-end">
                                {this.processButton()}
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )
        }else{
            return (
                <Row>
                    <Col xs='0' sm='0' lg='3' xl='6' />
                    <Col xs='12' sm='12' lg='3' xl='2'>
                        {this.saveButton()}
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='2'>
                        {this.suspendButton()}
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='2'>
                        {this.cancelButton()}
                    </Col>
                </Row>
            )
        }
    }
    render(){
        if(this.state.data.literals == undefined
            || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                {this.buildButtons()}
                {DictNode.createFields(this.state.data.literals, this)}
                
                <Row style={{height:'20px'}}></Row>
            </Container>
        )
    }


}
export default DictNode
DictNode.propTypes={
    identifier: PropTypes.string.isRequired,    //unique name of the instance of component for messages.
    nodeId:PropTypes.number.isRequired,         //node id
    parentId:PropTypes.number.isRequired,       //parent node id
    url:PropTypes.string.isRequired,            //url of node
    processButtons:PropTypes.bool,              // Add process specific buttons
    onCancel:PropTypes.func.isRequired,         //close callback
    display:PropTypes.bool,                     // display only
}