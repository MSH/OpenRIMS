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
                footermessage:''
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
                <Row className="pl-0 ml-0">
                    <Col xs="12" sm="12" lg="4" xl="4" className="pl-0 ml-0">
                        <img src="/img/USAIDMTASP.svg" height="60" className="pl-0 ml-0"/>
                    </Col>
                    <Col xs="12" sm="12" lg="6" xl="6"
                     style={{borderLeftWidth:'10px !important'}} className="d-flex justify-content-center align-items-center">
                        <small>{this.state.labels.footermessage}</small>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <Row>
                            <Col className="d-flex justify-content-end align-items-center">
                                <a href="https://mtapsprogram.org/resources/pharmadex" target="_blank"><img src="api/public/footer.svg" height={30} /> </a>
                            </Col>
                        </Row>
                        <Row>
                            <Col className="d-flex justify-content-end align-items-center">
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