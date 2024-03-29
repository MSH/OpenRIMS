import React , {Component} from 'react'
import Locales from './utils/Locales'
import ProcessConfigurator from './ProcessConfigurator'
import ProcessValidator from './ProcessValidator'

/**
 * Processes configurator and validator
 * Switch between them
 */
class Processes extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            configurator:true,                  //default is the configurator        
            dictNodeID:0,                       //ditionary node ID                    
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

            if(data.subject=="onWorkflowAssist" ){
                this.state.dictNodeID=data.data.nodeId
                this.state.configurator=false;
                this.setState(this.state)
            }
            if(data.to==this.state.identifier){
                if(data.subject=="onProcessValidatorClose"){
                    this.state.configurator=true
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
        if(this.state.configurator){
            return (<ProcessConfigurator recipient={this.state.identifier}  master={this.state.configState}/>)
        }else{

            return (<ProcessValidator dictNodeID={this.state.dictNodeID} recipient={this.state.identifier}/>)
        }
    }


}
export default Processes
Processes.propTypes={
    
}