import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import Thing from '../Thing'
import ButtonUni from '../form/ButtonUni'
import ApplicationStart from '../ApplicationStart'

/**
 * This is a mock of Renewal Control, Issue 888
 */
class DeregMock extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},    //ThingDTO
            choice:{},  // mock/ChoiceDTO
            index:0,
            identifier:Date.now().toString(),
            labels:{
                global_add:'',
                deregistration:'',
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
            if(data.to==this.state.identifier){
                if(data.subject=='thingValidated'){
                    this.state.data=data.data
                    if(this.state.data.valid){
                        Fetchers.postJSON("/api/guest/mock/choice", this.state.data, (query,result)=>{
                            this.state.choice=result
                            this.state.index=1
                            this.setState(this.state)
                        })
                    }else{
                        this.setState(this.state)
                    }
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

    content(){
        let data={}
        if(this.state.index==0){
            if(this.state.data.url == undefined){
            data.nodeId=0;
            data.url="mock.renewal"
            }else{
                data=this.state.data
            }
            return(
                <Thing data={data} recipient={this.state.identifier} />
            )
        }else{
            data.url=this.state.choice.url
            data.applDictNodeId=this.state.choice.dictNodeId
            data.historyId=0
            return(
                <ApplicationStart data={data} />
            )
        }
    }

    render(){
        if(this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
               <Row className="mb-1">
               <Col xs='12' sm='12' lg='10' xl='10'>
                        <h5>{this.state.labels.deregistration}</h5>
                   </Col>
                   <Col xs='12' sm='12' lg='2' xl='1' hidden={this.state.index==1}>
                        <ButtonUni
                            label={this.state.labels.global_add}
                            onClick={()=>{
                               Navigator.message(this.state.identifier,"*", "validateThing", {})
                            }}
                            color="primary"
                        />
                   </Col>
               </Row>
                <Row>
                    <Col>
                        {this.content()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default DeregMock
DeregMock.propTypes={
    
}