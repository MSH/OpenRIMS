import React , {Component} from 'react'
import {Container, Row, Col, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import TableSearch from './utils/TableSearch'

/**
 * EL Assistant - select a workflow
 * The goal is to send to the ELAssistant the workflow URL selected, otherwise the next step will be impossible
 */
class ELAssistantSelectWF extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{
                workflowURL:this.props.workflowURL,
            },
            labels:{
                selectWF:'',
                search:'',
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

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    load(){
        Fetchers.postJSON("/api/admin/el/assitant/workflows", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
            if(this.state.data.selectedURL.length>0){ //inform ELAssistant about selection  
                Navigator.message(this.state.identifier, this.props.recipient, "ELAssitant_Workflow",this.state.data.selectedURL)
            }
        })
    }

    render(){
        if(this.state.data.table==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='11' xl='11'>
                        <h5>{this.state.labels.selectWF}</h5>
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1'className="d-flex justify-content-end p-0 m-0">
                        <Button
                            size='lg'
                            className="p-0 m-0"
                            color="link"
                            onClick={()=>{
                                window.open('/api/admin/help/elassistant','_blank').focus()
                            }}
                        >
                        <i className="far fa-question-circle"></i>
                        </Button>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <TableSearch
                            tableData={this.state.data.table}
                            loader={this.load} 
                            label={this.state.labels.search}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            selectRow={(rowNum)=>{
                                this.state.data.table.rows.forEach((row) => {
                                    row.selected=false
                                });
                                this.state.data.table.rows[rowNum].selected= !this.state.data.table.rows[rowNum].selected
                                this.load()
                            }}
                        />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ELAssistantSelectWF
ELAssistantSelectWF.propTypes={
    workflowURL: PropTypes.string,   //URL of the Certification Workflow selected
    recipient :PropTypes.string      //ELAssistant component for messaging
}