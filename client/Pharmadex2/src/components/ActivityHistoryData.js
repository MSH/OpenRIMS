import React , {Component} from 'react'
import {Container, Row, Col, Breadcrumb, BreadcrumbItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import CheckList from './CheckList'
import Pharmadex from './Pharmadex'
import ViewEdit from './form/ViewEdit'
import ViewEditDate from './form/ViewEditDate'
import Thing from './Thing'

/**
 * Show history data 
 * @example
 *  <ActivityHistoryData historyId={this.state.historyId} recipient={this.state.identifier} />
 * Issues event activityHistoryClose
 * Just copy it
 */
class ActivityHistoryData extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{
                historyId:this.props.historyId,
            },                                    //ActivityHistoryDTO
            identifier:Date.now().toString(),
            labels:{
                next:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
        this.breadCrumb=this.breadCrumb.bind(this)
        this.activityData=this.activityData.bind(this)
        this.checklist=this.checklist.bind(this)
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

    componentDidUpdate(){
        if(this.props.historyId != this.state.data.historyId){
            this.state.data.historyId=this.props.historyId
            this.load()
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Load ActivityHistoryDataDTO
     */
    load(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/activity/history/data", this.state.data, (query, result)=>{
            this.state.data=result
            Locales.createLabels(this)
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
    }
    /**
     * create a breadcrumb
     */
    breadCrumb(){
        let data=this.state.data
        if(this.state.data.activity.length>0){
            //it is nor trace, neither monitoring
            return(
                <Breadcrumb className='m-4'>
                    <BreadcrumbItem className="d-inline"  key={1}>
                            <h6 className="d-inline">{data.workflow}</h6>
                    </BreadcrumbItem>
                    <BreadcrumbItem className="d-inline"  key={2}>
                            <h6 className="d-inline">{data.activity}</h6>
                    </BreadcrumbItem>
                    <BreadcrumbItem className="d-inline"  key={3}>
                            <h6 className="d-inline">{data.prefLabel}</h6>
                    </BreadcrumbItem>
                    <BreadcrumbItem className="d-inline"  key={4}>
                        <div className="btn btn-link p-0 border-0"
                            onClick={()=>{
                                Navigator.message(this.state.identifier, this.props.recipient, "activityHistoryClose", {})
                            }}
                            >
                            <h6 className="d-inline">{this.state.labels.next}</h6>
                        </div>
                    </BreadcrumbItem>
                </Breadcrumb>
            )
        }else{
            //this is trace or monitoring
            return(
                <Breadcrumb className='m-4'>
                    <BreadcrumbItem className="d-inline"  key={4}>
                        <div className="btn btn-link p-0 border-0"
                            onClick={()=>{
                                Navigator.message(this.state.identifier, this.props.recipient, "activityHistoryClose", {})
                            }}
                            >
                            <h6 className="d-inline">{this.state.labels.next}</h6>
                        </div>
                    </BreadcrumbItem>
                </Breadcrumb>
                )
        }
    }
    /**
     * Activity data if one
     */
    activityData(){
        if(this.state.data.activityDataId>0 && this.state.data.activity.length>0){
            let data={
                nodeId:this.state.data.activityDataId,
                repaint:true
            }
            return(
                <Thing
                    data={data}
                    recipient={this.state.identifier}
                    readOnly
                    narrow
                />
            )
        }else{
            return []
        }
    }
    /**
     * 
     * @returns Checklist if one
     */
    checklist(){
        if(this.state.data.activity.length>0){
            return(
                <CheckList historyId={this.props.historyId} recipient={this.state.identifier} readOnly/>
            )
        }else{
            return []
        }
    }

    render(){
        let data=this.state.data
        if(data.workflow == undefined || this.state.labels.locale==undefined){
            return <div> <i className="blink fas fa-circle-notch fa-spin" style={{color:'#D3D3D3'}}/></div>
        }
        return(
            <Container fluid className={Pharmadex.settings.activeBorder}>
               {this.breadCrumb()}
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <ViewEditDate attribute="global_startdate" component={this} />
                            </Col>
                            <Col xs='12' sm='12' lg='6' xl='6' hidden={!this.state.data.completed}>
                                <ViewEditDate attribute="completeddate" component={this} />
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <ViewEdit mode='textarea' attribute='expert' component={this}/>
                            </Col>
                        </Row>
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <ViewEdit mode='textarea' attribute='notes' component={this}/>
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                       {this.checklist()}
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.activityData()}
                    </Col>
                </Row>
                {this.breadCrumb()}
            </Container>
        )
    }


}
export default ActivityHistoryData
ActivityHistoryData.propTypes={
    historyId:PropTypes.number.isRequired,      //ID of the history record
    recipient:PropTypes.string.isRequired       //recipient of the messages
}