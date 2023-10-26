import React , {Component} from 'react'
import {Button,ButtonGroup} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'

/**
 * Buttons at the left of URL fiels
 */
class URLButtons extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            labels:{
                url_assistant:'',
                paste_url:''
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
        Locales.resolveLabels(this)
        Locales.createLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        let select=false
        if(this.props.select){
            select=true
        }
        let paramJSON={
            assistant:this.props.assistant,
            select:select
        }
        let param = encodeURI(JSON.stringify(paramJSON))
        return []   //temporarely
       /*  return(
            <ButtonGroup>
                <Button title={this.state.labels.url_assistant}
                    onClick={()=>window.open('/admin#urlassistant/'+param,'_blank')}
                >
                    <i className="fa fa-question-circle" aria-hidden="true"></i>
                </Button>
                <Button title={this.state.labels.paste_url}>
                    <i className="fa fa-clipboard" aria-hidden="true"></i>
                </Button>
            </ButtonGroup> 
        )*/
    }


}
export default URLButtons
URLButtons.propTypes={
    assistant:PropTypes.oneOf(['dictionaries','data','workflow','activity', 'resource']).isRequired,    //for which assistant will be needed
    select:PropTypes.bool   //false construct the URL, true, select the existing
}