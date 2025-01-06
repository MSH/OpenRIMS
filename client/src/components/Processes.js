import React , {Component} from 'react'
import Locales from './utils/Locales'
import ProcessConfigurator from './ProcessConfigurator'
import ProcessValidator from './ProcessValidator'
import RunTestProcess from './RunTestProcess'

/**
 * Processes configurator and validator
 * Switch between them
 */
class Processes extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            screen:'configurator',                       //default is the configurator        
            dictNode:{},                       //ditionary node                    
            configState:{},                     //saved state of the Process Configurator
            labels:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data

            if(data.subject=="onRunTestProcess"){
                this.state.dictNode=data.data
                this.state.screen='runtest'
                this.setState(this.state)
            }

            if(data.subject=="onWorkflowAssist" ){
                this.state.dictNodeID=data.data.nodeId
                this.state.screen='validator';
                this.setState(this.state)
            }
            if(data.to==this.state.identifier){
                if(data.subject=='onRunTestProcessClose'){
                    this.state.screen='configurator'
                    this.setState(this.state)
                }
                if(data.subject=="onProcessValidatorClose"){
                    this.state.screen='configurator'
                    this.setState(this.state)
                }
                if(data.subject=="onProcessOpen"){
                    this.state.configState=data.data
                    this.setState(this.state)
                }
               
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
    }


    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        if(this.state.screen=='configurator'){
            return (<ProcessConfigurator recipient={this.state.identifier}  master={this.state.configState}/>)
        }
        if(this.state.screen=='validator'){
            return (<ProcessValidator dictNodeID={this.state.dictNodeID} recipient={this.state.identifier}/>)
        }
        if(this.state.screen=='runtest'){
            return (<RunTestProcess dictNode={this.state.dictNode} recipient={this.state.identifier}/>)
        }
        
    }


}
export default Processes
Processes.propTypes={
    
}