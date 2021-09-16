import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'

/**
 * Responsible for de-registration
 * Just copy it
 */
class Deregistration extends Component{
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
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        return(
            <Container fluid>
                <h6>Coming soon...</h6>
            </Container>
        )
    }


}
export default Deregistration
Deregistration.propTypes={
    
}