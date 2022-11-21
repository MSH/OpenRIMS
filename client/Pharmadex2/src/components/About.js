import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'

/**
 * About box. Also is using as a footer
 */
class About extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},
            labels:{
                footermessage:'',
                terms:'',
                privacy:''
            }
        }
    }
    componentDidMount(){
        Fetchers.postJSONNoSpinner("/api/public/about", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
        Locales.resolveLabels(this)
    }

    render(){
        if(this.state.data.release == undefined){
            return[]
        }
        return(
            <Container fluid className="pl-0 ml-0 bg-light d-print-none">
                <Row  className="pl-0 ml-0" >
                    <Col xs="12" sm="12" lg="4" xl="3" className="pl-0 ml-0">
                        <a href="https://usaid.gov" target="_blank"><img src="/img/USAIDMTASP.svg" height="70" className="pl-0 ml-0" /> </a>
                    </Col>
                    <Col xs="12" sm="12" lg="6" xl="7" style={{borderLeftWidth:'5px !important'}}>
                        <Row xs="12" sm="12" lg="12" xl="12" >
                            <Col xs='12' sm='12' lg='9' xl='10' className={"pl-0 ml-0"}>
                                <small>{this.state.labels.footermessage}</small>
                            </Col>
                            <Col xs='12' sm='12' lg='3' xl='2' className="pl-0 ml-0">
                                <Row className="d-flex align-items-center">
                                    <Col>
                                    <Row className="d-flex align-items-center" style={{color:'#000080'}}>
                                    <Col>
                                    <Row className="d-flex align-items-center" style={{color:'#000080'}}>
                                    <Col>
                                        <Row>
                                            <Col>
                                                <small><i className="fas fa-users"></i><a href="api/public/terms" target="_blank" style={{fontFamily:'sans-serif', color:'#000080'}}>
                                                    {'  '}{this.state.labels.terms}</a>
                                                </small>
                                            </Col>
                                        </Row>
                                        <Row>
                                            <Col>
                                                <small><i className="fas fa-user"></i><a href="api/public/privacy" target="_blank" style={{fontFamily:'sans-serif', color:'#000080'}}>
                                                    {'    '}{this.state.labels.privacy}</a>
                                                </small>
                                            </Col>
                                        </Row>
                                    </Col>
                                </Row>
                                    </Col>
                                </Row>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12' className={"pt-2 justify-content-end d-flex align-items-center pt-1"}> 
                                <a href="https://openrims.org" target="_blank"><img src="/img/OpenRIMS.svg" height={40} /> </a>
                            </Col>
                        </Row>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12' className={"pt-1 justify-content-end d-flex align-items-center"}>
                                {/* justify-content-end */}
                                <small>{"R"+this.state.data.release+"@"+this.state.data.buildTime}</small>
                            </Col>
                        </Row>
                    </Col>
                </Row>

            </Container>
        )
    }


}
export default About
About.propTypes={
    menu:PropTypes.oneOf(["landing","guest","admin", "moderator", "screener","reviewer","accountant","inspector","secretary"]).isRequired,
    navigator:PropTypes.object.isRequired                                           //Navigator
}