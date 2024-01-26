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
                warningRemove:''
            },
            data:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
        this.buildButtons=this.buildButtons.bind(this)
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

    buildButtons(){
        return (
            <Row>
                    <Col xs='0' sm='0' lg='0' xl='3' />
                    <Col xs='12' sm='12' lg='4' xl='3'>
                        <ButtonUni
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
                        />
                    </Col>
                    <Col hidden={this.state.data.nodeId==0} xs='12' sm='12' lg='4' xl='3'>
                        <ButtonUni
                            disabled={!this.state.data.leaf}
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
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='3'>
                    <ButtonUni
                            label={this.state.labels.cancel}
                            color='secondary'
                            outline
                            onClick={this.props.onCancel}
                        />
                    </Col>
                </Row>
        )
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
    onCancel:PropTypes.func.isRequired,         //cancel callback
    display:PropTypes.bool,                     // display only
}