import React , {Component} from 'react'
import {Container, Row, Col, Collapse} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import CollectorTable from '../utils/CollectorTable'
import Pharmadex from '../Pharmadex'
import ApplicationEventData from './ApplicationEventData'

/**
 * It is a dummy component to create other components quickly
 * Just copy it
 */
class ApplicationEvents extends Component{
    constructor(props){
        super(props)
        this.state={
            hist:false,
            identifier:Date.now().toString(),
            labels:{
                applicationevents:'',
            },
            //ApplicationEventsDTO
            data:{  
                appldataid:this.props.appldataid,
                selected:0
            }     
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadTable=this.loadTable.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=='closeEventData'){
                    this.state.data.selected=0
                    this.setState(this.state)
                    Navigator.message(this.state.identifier, "*", "reload_application_event", this.state.data)
                }
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loadTable()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Load application events table
     */
    loadTable(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/application/events", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    render(){
        if(this.state.labels.locale == undefined || this.state.data.table==undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid >
                <Row>
                    <Col className="btn btn-link p-0 border-0 d-flex justify-content-start"
                        onClick={()=>{
                            this.state.hist=!this.state.hist
                            this.setState(this.state)
                        }}>
                        <h4 className="ml-3">{this.state.labels.applicationevents}</h4>
                    </Col>
                </Row>
                <Row className={Pharmadex.settings.activeBorder}>
                    <Col>
                        <Collapse isOpen={this.state.hist}>
                            <Row>
                                <Col>
                                    <CollectorTable
                                        tableData={this.state.data.table}
                                        loader={this.loadTable}
                                        headBackground={Pharmadex.settings.tableHeaderBackground}
                                        styleCorrector={(header)=>{
                                            if(header=='pref'){
                                                return {width:'70%'}
                                            }
                                        }}
                                        linkProcessor={(row,col)=>{
                                            this.state.data.selected=this.state.data.table.rows[row].dbID
                                            this.state.data.title=col.value
                                            this.state.data.eventDate=this.state.data.table.rows[row].row[0].value
                                            this.setState(this.state)
                                            Navigator.message(this.state.identifier, "*", "reload_application_event", this.state.data)
                                        }}
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <ApplicationEventData data={this.state.data} recipient={this.state.identifier} />
                                </Col>
                            </Row>
                        </Collapse>
                    </Col>
                </Row>
                
            </Container>
        )
    }


}
export default ApplicationEvents
ApplicationEvents.propTypes={
    appldataid :PropTypes.number.isRequired,        //application data ID
    recipient :PropTypes.string.isRequired          //recipient for messages
}