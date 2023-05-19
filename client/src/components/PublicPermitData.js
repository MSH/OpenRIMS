import React , {Component} from 'react'
import {Container, Row, Col, FormText, Card, CardBody, CardTitle} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Pharmadex from './Pharmadex'
import ThingsPublisher from './reports/ThingsPublisher'

/**
 * Display public available permit data
 * 
 */
class PublicPermitData extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data: this.props.data,      //PublicPermitDTO.java
            labels:{
                historyData:''
            },
            fullcollapse:[]
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
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
        this.load()
       
    }
    /**
     * Load public available data
     */
    load(){
        Fetchers.postJSON("/api/public/permit/data", this.state.data, (query,result)=>{
            this.state.data=result
            //Locales.createLabels(this)
            
            this.state.fullcollapse = []
            if(Fetchers.isGoodArray(this.state.data.application)){
                this.state.data.application.forEach((thing, index)=>{
                    this.state.fullcollapse.push({
                        ind:index,
                        collapse:false
                    })
                })
            }
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        if(this.state.data.title == undefined || this.state.labels.locale==undefined || !Fetchers.isGoodArray(this.state.data.application)){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <h1 className="text-center">{this.state.data.title}</h1>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <FormText color="muted">
                            {this.state.data.description}
                        </FormText>
                    </Col>
                </Row>

                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Card className={Pharmadex.settings.activeBorder}>
                            <CardTitle className="text-center bg-light text-dark" tag="h3">
                                {this.state.data.application[0].title}
                            </CardTitle>
                            <CardBody>
                                <ThingsPublisher data={this.state.data.application} recipient={this.state.identifier}/ >
                            </CardBody>
                        </Card>
                        
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Card className={Pharmadex.settings.activeBorder}>
                            <CardTitle className="text-center bg-light text-dark" tag="h3">
                                {this.state.labels.historyData}
                            </CardTitle>
                            <CardBody>
                                <ThingsPublisher data={this.state.data.applHistory} recipient={this.state.identifier}/ >
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default PublicPermitData
PublicPermitData.propTypes={
    data: PropTypes.shape(
        {
            permitDataID:PropTypes.number.isRequired   //PublicPermitDTO.java
        }.isRequired,
    )
}