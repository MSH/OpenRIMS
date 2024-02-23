import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'

/**
 * Data for EL expression assistance
 */
class ELAssistanceData extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            labels:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.breadcrumb=this.breadcrumb.bind(this)
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
   /**
     * 
     * @returns the breadcrumb
     */
   breadcrumb(){
        return(
            []
        )
    }
    render(){
        return(
            <Container fluid>
                <Row>
                    <Col>
                        {this.breadcrumb()}
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6'>
                    </Col>
                    <Col xs='12' sm='12' lg='6'>
                    </Col>
                </Row>

            </Container>
        )
    }


}
export default ELAssistanceData
ELAssistanceData.propTypes={
    
}