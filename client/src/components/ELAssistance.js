import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import ELAssistanceData from './ELAssistanceData'
import ButtonUni from './form/ButtonUni'

/**
 * Electronic form for EL assistance
 * 
 */
class ELAssistance extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},
            labels:{
                elassistance :'',
                copy:'',
                global_help:'',
                global_cancel:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.header=this.header.bind(this)
       
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
     * @returns Form title, result and buttons
     */
    header(){
        return(
            <Row>
                <Col xs='12' sm='12' lg='3' xl='3'> {/* page tile*/}
                    <h4>{this.state.labels.elassistance}</h4>
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'> {/* EL result*/}

                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'> {/* copy button*/}
                    <ButtonUni
                        label={this.state.labels.copy}
                        color="primary"
                        onClick={()=>{

                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'> {/* help button*/}
                    <ButtonUni
                        label={this.state.labels.global_help}
                        color="info"
                        onClick={()=>{

                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'> {/* cancel button*/}
                    <ButtonUni
                        label={this.state.labels.global_cancel}
                        outline
                        color="secondary"
                        onClick={()=>{
                            window.opener.focus()
                            window.close()
                        }}
                    />
                </Col>
            </Row>
        )
    }
 

    render(){
        return(
            <Container fluid>
                <Row>  
                    <Col>
                        {this.header()}
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <ELAssistanceData />
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                    </Col>
                </Row>
                <Row>

                </Row>
            </Container>
        )
    }


}
export default ELAssistance
ELAssistance.propTypes={
    
}