import React , {Component} from 'react'
import {Container, Row, Col, Card, CardHeader, CardBody, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import Thing from '../Thing'
import Pharmadex from '../Pharmadex'

/**
 * Display activity data
 * historyId
 * recipient
 */
class ActivityData extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},    //ThingDTO
            identifier:Date.now().toString(),
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
        this.load()
    }

    load(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/activity/data/load", this.props.historyId, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })

    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        if(this.state.data.nodeId==undefined || this.state.data.nodeId==0){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Card>
                    <CardHeader>
                        <Row>
                            <Col xs='11' sm='11' lg='11' xl='11'>
                                <h5>{this.state.data.title}</h5>
                            </Col>
                            <Col xs='1' sm='1' lg='1' xl='1'>
                                <Button close
                                    onClick={()=>{
                                        Navigator.message(this.state.identifier, this.props.recipient, "closeActivityData", {})
                                    }} 
                                />
                            </Col>
                        </Row>
                    </CardHeader>
                    <CardBody>
                        <Thing
                            data={this.state.data}
                            readOnly
                            recipient={this.state.identifier}
                        />
                    </CardBody>
                </Card>
            </Container>
        )
    }


}
export default ActivityData
ActivityData.propTypes={
    historyId:PropTypes.number.isRequired,  //id of the history record contains activity data
    recipient:PropTypes.string.isRequired,  // messages recipient
}