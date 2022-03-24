import React , {Component} from 'react'
import {Container, Row, Col, Collapse} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import CollectorTable from '../utils/CollectorTable'
import Pharmadex from '../Pharmadex'

/**
 * Provides list of application history and changes sheet
 */
class ApplicationHistory extends Component{
    constructor(props){
        super(props)
        this.state={
            hist:false,
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
                            />
                        </Collapse>
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ApplicationHistory
ApplicationHistory.propTypes={
    nodeid:PropTypes.number.isRequired,         //initial applicatin data node
    recipient:PropTypes.string.isRequired       //recipient for messaging
}