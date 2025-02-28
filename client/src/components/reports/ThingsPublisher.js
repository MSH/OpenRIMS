import React , {Component} from 'react'
import {Container, Row, Col, Collapse} from 'reactstrap'
import PropTypes from 'prop-types'
import Fetchers from '../utils/Fetchers'
import Thing from '../Thing'
import CheckList from '../CheckList'
import Locales from '../utils/Locales'
import TimeLine from '../TimeLine'

/**
 * Display List of things provided
 * Can reload it by event reloadThingPubliser
 * 
 */
class ThingsPublisher extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data: this.props.data,      //Array of ThingDTO
            labels:{
                previewunavailable:'',
            },
            fullcollapse:[],
            wf:''
        }
        
        this.eventProcessor=this.eventProcessor.bind(this)
        this.application=this.application.bind(this)
        this.toggle=this.toggle.bind(this)
        this.thingComp=this.thingComp.bind(this)
        this.prepareLists=this.prepareLists.bind(this)
        this.timeLine=this.timeLine.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.from=='reloadThingPubliser' && data.from==this.props.recipient ){
                this.state.data=data.data
                this.prepareLists()
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.prepareLists()
    }
    /**
     * Initially, all lists are collapsed
     */
    prepareLists(){
        this.state.fullcollapse = []
            if(Fetchers.isGoodArray(this.state.data)){
                this.state.data.forEach((thing, index)=>{
                    this.state.fullcollapse.push({
                        ind:index,
                        collapse:false
                    })
                })
            }
        this.setState(this.state)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    /**
     * Display an application
     */
    application(){
        let ret = []
        if(Fetchers.isGoodArray(this.state.data)){
            this.state.data.forEach((thing, index)=>{
                // if(thing.nodeId>0){
                    ret.push(
                        
                        <h5 className='btn-link' key={index+1000} style={{cursor:"pointer"}} 
                            onClick={()=>{this.toggle(index)}}>{thing.title}</h5>
                    )
                    ret.push(
                        <Collapse key={index+500} isOpen={this.state.fullcollapse[index].collapse} >
                            {this.timeLine(thing)}
                            {this.thingComp(index, thing)}
                        </Collapse>
                    )
                /* }else{
                    ret.push(
                        <h5 className='font-weight-light' key={index+1000}>{thing.title}</h5>
                    ) 
                } */
            })
        }
        return ret
    }
    
    /**
     * which thing should be opened?
     */
    toggle(ind) {
        if(this.state.data != undefined ){
            if(Fetchers.isGoodArray(this.state.fullcollapse)){
                this.state.fullcollapse.forEach((el, i)=>{
                    if(ind == el.ind){
                        el.collapse = !el.collapse
                    }
                })
            }
            this.setState(this.state);
        }
    }

    /**
     * Open a thing on the screen 
     */
    thingComp(index, thing){
        let flag = false
        if(Fetchers.isGoodArray(this.state.fullcollapse)){
            this.state.fullcollapse.forEach((el, i)=>{
                if(index == el.ind && el.collapse){
                    flag = true
                }
            })
        }
        if(flag){
            if(thing.nodeId>0){
                return (
                    <Row><Col>
                    {/* {this.timeLine(thing)} */}
                <Thing key={index}
                                data={thing}
                                recipient={this.state.identifier}
                                readOnly={true}
                                narrow
                                reload
                                /> 
                                {/* thing.historyId */}
                    <CheckList historyId={0} recipient={this.state.identifier} readOnly/>
                    </Col></Row>
                )
            }else{
                return (
                    <Row><Col>
                    {/* {this.timeLine(thing)} */}
                    <CheckList historyId={thing.historyId} recipient={this.state.identifier} readOnly/>
                    </Col></Row>
                )
            }
        }
        // else{
        //     return <small>{this.state.labels.previewunavailable}</small>
        // }
    }
    timeLine(thing){
        if(thing.historyId>0 && this.state.wf!=thing.uxIdentifier){
            this.state.wf=thing.uxIdentifier
            return(
                <Row>
                <Col>
                    <TimeLine historyId={thing.historyId} recipient={this.state.identifier}/>
                </Col>
                </Row>
            )
        }else{
            return []
        }
    }

    render(){
        if(!Fetchers.isGoodArray(this.state.fullcollapse)){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        {this.application()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ThingsPublisher
ThingsPublisher.propTypes={
    data: PropTypes.array.isRequired,       //list of ThingDTO
    recipient:PropTypes.string.isRequired,  ////recepient for messaging   
}