import React , {Component} from 'react'
import {Container,Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import FieldInput from './form/FieldInput'

/**
 * A component responcible for literals (load, display,edit)
 * Provides useful static utilities as well
 * Accept message "getData", reply to it with data DictElementDTO
 * issues onLiteralUpdated event from componentDidUpdate
 * @example
 * <Literals
 *  identifier="node"                   
    url="node"
    parentId={this.data.node.id}
    nodeId:{this.data.node.id}
    display:false                     
 * />
 */
class Literals extends Component{
    constructor(props){
        super(props)
        this.state={
            labels:{},
            data:{}
        }
        this.createFields=this.createFields.bind(this)
        this.load=this.load.bind(this)
        this.onAskData=this.onAskData.bind(this)
        this.onGetData=this.onGetData.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
    }
    /**
     * Clean up all literal values
     * @param {object} literals
     * @returns literals with empty values 
     */
    static cleanUp(literals){
        let keys=Object.keys(literals)
        keys.forEach((key)=>{
            literals[key].value=""
        })
        return literals
    }


    /**
     * Container sent this data to replace existing one. Typically, after failed validation
     * @param {object} data 
     */
    onGetData(data){
        if(data.to==this.props.identifier){
            this.state.data=data.data
            this.setState(this.state)
        }
    }

    /**
     * Someone asked for this.state.data, we will reply
     * @param {string} from - who was asked  
     */
    onAskData(from){
        Navigator.message(this.props.identifier,from, "onGetData", this.state.data)
    }
    /**
     * Processor for all events
     * @param {object} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.from != this.props.identifier && (data.to=="*" || data.to==this.props.identifier)){
            if(data.subject=="onGetData"){
                this.onGetData(data)   //in answer to ask data
            }
            if(data.subject=="askData"){
                this.onAskData(data.from)
            }
        }
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor);
        this.load()
    }

    componentDidUpdate(){
        if(this.props.nodeId != this.state.data.nodeId){
            this.state.data.nodeId=this.props.nodeId
            Literals.cleanUp(this.state.data.literals)
            this.load()
        }
        let recipient='*'
        if(this.props.recipient != undefined){
            recipient=this.props.recipient
        }
        Navigator.message(this.props.identifier, recipient, "onLiteralUpdated", this.state.data)
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
     * @returns FieldInput in one column
     * @example
     * this.createFields(this.state.data.literals)
     */
    createFields(literals){
        let ret=[]
        let keys = Object.keys(literals)
        if(Fetchers.isGoodArray(keys)){
            keys.forEach((key, index)=>{
                if(literals[key].value != undefined){
                    let mode="text"
                    if(key=="description"){
                        mode="textarea"
                    }
                    ret.push(
                        <Row key={index}>
                            <Col>
                                <FieldInput mode={mode} attribute={key} component={this} data={literals} rows='8'/>
                            </Col>
                        </Row>
                    )
                }
            }
        )
        }
        return ret
    }

    render(){
        if(this.state.data.literals == undefined
            || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                {this.createFields(this.state.data.literals)}
            </Container>
        )
    }


}
export default Literals
Literals.propTypes={
    identifier: PropTypes.string.isRequired,    //unique name of the instance of component for messages.
    recipient: PropTypes.string,                //recipient of messages
    url:PropTypes.string.isRequired,            //url of literals tree for future reference only
    parentId:PropTypes.number.isRequired,       //id of parent component for future reference only
    nodeId:PropTypes.number.isRequired,         //id of this component, if no zero, will be loaded
    display:PropTypes.bool,                     // display only
}