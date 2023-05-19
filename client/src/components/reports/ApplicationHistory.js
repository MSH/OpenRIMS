import React , {Component} from 'react'
import {Container, Row, Col, Collapse} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import CollectorTable from '../utils/CollectorTable'
import Pharmadex from '../Pharmadex'
import ActivityData from './ActivityData'

/**
 * Provides list of application history and changes sheet
 */
class ApplicationHistory extends Component{
    constructor(props){
        super(props)
        this.state={
            hist:false,
            tableView:true,
            histId:0,
            data:{
                nodeId:this.props.nodeid,       //ApplicationHistoryDTO
            },
            identifier:Date.now().toString(),
            labels:{
                application_info:'',
            }
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
            if(data.to==this.state.identifier){
                if(data.subject=="closeActivityData"){
                    this.state.tableView=true;
                    this.state.histId=0;
                    this.setState(this.state)
                }
            }
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.load()
    }
    /**
     * Load all tables by nodeId
     */
    load(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/application/history", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })

    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        if(this.state.data.table == undefined || this.state.labels.locale == undefined){
            return Pharmadex.wait()
        }
        if(this.state.tableView){
                return(
                    <Container fluid>
                        <Row>
                            <Col className="btn btn-link p-0 border-0 d-flex justify-content-start"
                                onClick={()=>{
                                    this.state.hist=!this.state.hist
                                    this.setState(this.state)
                                }}>
                                <h4 className="ml-3">{this.state.labels.application_info}</h4>
                            </Col>
                        </Row>
                        <Row className={Pharmadex.settings.activeBorder}>
                            <Col>
                                <Collapse isOpen={this.state.hist}>
                                    <CollectorTable
                                        tableData={this.state.data.table}
                                        loader={this.load}
                                        headBackground={Pharmadex.settings.tableHeaderBackground}
                                        linkProcessor={(rowNo, cell)=>{
                                            this.state.histId=this.state.data.table.rows[rowNo].dbID
                                            this.state.tableView=false
                                            this.setState(this.state)
                                        }}
                                    />
                                </Collapse>
                            </Col>
                        </Row>
                    </Container>
                )
        }else{
            return (
                <ActivityData historyId={this.state.histId} recipient={this.state.identifier} />
            )
        }
    }


}
export default ApplicationHistory
ApplicationHistory.propTypes={
    nodeid:PropTypes.number.isRequired,         //initial applicatin data node
    recipient:PropTypes.string.isRequired       //recipient for messaging
}