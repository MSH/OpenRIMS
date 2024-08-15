import React , {Component} from 'react'
import {Container, Row, Col, Popover,OverlayTrigger} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Pharmadex from './Pharmadex'
import Navigator from './utils/Navigator'
class TimeLine extends Component{
    constructor(props){
        super(props)
        this.state={
            tl:false,
            data:{
                historyId:this.props.historyId,
                permitdataid:this.props.permitdataid
            },                                    
            identifier:Date.now().toString(),
            labels:{
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
        this.paintTimeLine=this.paintTimeLine.bind(this)
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
componentWillUnmount(){
    window.removeEventListener("message",this.eventProcessor)
}
 /**
     * Load TimeLineWokflowDTO
     */
 load(){
    this.state.data.historyId=this.props.historyId
    this.state.data.permitdataid=this.props.permitdataid
    Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/activity/timeline/workflow", this.state.data, (query, result)=>{
        this.state.data=result
        Locales.createLabels(this)
        Locales.resolveLabels(this)
        if(Fetchers.isGoodArray(this.state.data.point)){
            this.state.tl=true
        }
        this.setState(this.state)
    })
}
/*  popoverLeft()  {
    return(
    <Popover id="popover-positioned-left" title="Popover left">
      <strong>Holy guacamole!</strong> Check this info.
    </Popover>
    )
} */
paintTimeLine(){
    let ret=[]
    let step='timeline-step'
    let content='timeline-content'
    let step1='timeline-step1'
    let content1='timeline-content1'
    let style=''
    let style1=''
    if(Fetchers.isGoodArray(this.state.data.point)){
        this.state.data.point.forEach((point, index) => {
            if(point.date=='---'){
                style=step1
                style1=content1
            }else{
                style=step
                style1=content
            }
                ret.push(
                <div className={style} key={index}>
                    <div className={style1}>
                        <div className='inner-circle'></div>
                        <p className='mt-3 mb-1' style={{fontSize:'0.8rem'}}>{point.date}</p>
                        <p className='text-muted mb-0 mb-lg-0' style={{fontSize:'0.8rem', fontWeight:500}}>{point.name}</p>
                    </div>
                </div>
            )
        })
    }
     return ret
}

render(){
    let data=this.state.data
    if(!this.state.tl){
        return <div> <i className="blink fas fa-circle-notch fa-spin" style={{color:'#D3D3D3'}}/></div>
    } 
    return(   
        <Container fluid className={Pharmadex.settings.activeBorder}>
            <Row className='text-center justify-content-center mb-1'>
                <Col xl='12' lg='12'>
                <div className='timeline-steps aos-init aos-animate' data-aos='fade-up'>
                    {this.paintTimeLine()}
            </div>
                </Col>
            </Row>
        </Container>
    )
}
}
export default TimeLine
TimeLine.propTypes={
    permitdataid:PropTypes.number,//ID application
    historyId:PropTypes.number.isRequired,      //ID of the history record
    recipient:PropTypes.string.isRequired       //recipient of the messages
}