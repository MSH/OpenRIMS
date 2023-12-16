import React , {Component} from 'react'
import {Container, Row, Col, Collapse} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import TableSearch from './utils/TableSearch'
import Pharmadex from './Pharmadex'

/**
 * Events log or, strictly says, any table into expandable section
 * Can be reloaded by LogEventsReload event
 */
class LogEvents extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            expandLog:false,                    //expand event log section
            expandLogIcon:"fas fa-caret-right",
            data:{},                            //EventLog.java
            labels:{
                search:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.from==this.props.recipient && data.subject=='LogEventsReload'){
                this.loader()
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loader()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    loader(){
        Fetchers.postJSON(this.props.api, this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }
    render(){
        if(this.state.data.eventLog == undefined || this.state.labels.locale== undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                 <Row>
                    <Col className="btn btn-link p-0 border-0 d-flex justify-content-start"
                        onClick={()=>{
                            this.state.expandLog=!this.state.expandLog
                            if(this.state.expandLog){
                                this.state.expandLogIcon="fas fa-caret-down"
                            }else{
                                this.state.expandLogIcon="fas fa-caret-right"
                            }
                            this.setState(this.state)
                        }}>
                        <Row>
                            <Col>
                                <h5 className="ml-3"><i className={this.state.expandLogIcon}></i>{this.state.data.title}</h5>
                            </Col>
                        </Row>
                    </Col>
                    </Row>
                    <Collapse isOpen={this.state.expandLog}>
                        <TableSearch 
                            label={this.state.labels.search}
                            title={this.state.data.title}
                            tableData={this.state.data.eventLog} 
                            loader={this.loader}
                        />
                    </Collapse>
            </Container>
        )
    }


}
export default LogEvents
LogEvents.propTypes={
    api:PropTypes.string.isRequired,        //api to load EventLogDTO
    recipient:PropTypes.string.isRequired,  //recepient for messaging  
}