import React , {Component} from 'react'
import {Button,ButtonGroup} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'

/**
 * Buttons at the left of URL fiels
 * ~~~~
 *  assistant:PropTypes.oneOf(['NO','URL_DICTIONARY_NEW','URL_ANY']).isRequired,    //for which assistant will be needed
    recipient:PropTypes.string.isRequired,                                          //for messages
    title:PropTypes.string,     //title text to guide the user
 * ~~~~
 */
class URLButtons extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
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
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        let title=''
        if(this.props.title){
            title=this.props.title
        }
        let value=this.props.value
        if(value == undefined){
            value=''
        }
        let paramJSON={
            assistant:this.props.assistant,
            value:value,
            recipient:this.props.recipient,
            title:title
        }
        let param = encodeURIComponent(JSON.stringify(paramJSON))
        return(
            <ButtonGroup>
                <Button title={this.props.title}
                    onClick={()=>{
                        if(this.props.assistant.startsWith("URL")){
                            window.open('/admin#urlassistant/'+param,'_blank')
                        }else{
                            window.open('/admin#varassistant/'+param,'_blank') 
                        }
                    }}
                    color='primary'
                >
                    <i className="far fa-edit" aria-hidden="true"></i>
                </Button>
            </ButtonGroup> 
        )
    }


}
export default URLButtons
URLButtons.propTypes={
    assistant:PropTypes.string.isRequired,    //for which assistant will be needed
    recipient:PropTypes.string.isRequired,    //for messages
    value:PropTypes.string,                   //current URL value if one
    title:PropTypes.string,     //title text to guide the user
}