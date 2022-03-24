import React , {Component} from 'react'
import {Container, Row, Col,Collapse} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import Pharmadex from '../Pharmadex'
import CollectorTable from '../utils/CollectorTable'

/**
 * List of all office filing system's registers assigned to this application
 */
class ApplicationRegisters extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{                          //ApplicationHistoryDTO
                nodeId:this.props.nodeid,
            },
            labels:{
                registration_numbers:'',
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
     * Load registers
     */
    load(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/application/registers", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        if(this.state.data.table==undefined || this.state.labels.locale==undefined){
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
                        <h4 className="ml-3">{this.state.labels.registration_numbers}</h4>
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
export default ApplicationRegisters
ApplicationRegisters.propTypes={
    nodeid:PropTypes.number.isRequired,     //application's main data node
    recipient:PropTypes.string.isRequired   //for
}