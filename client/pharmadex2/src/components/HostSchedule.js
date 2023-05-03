import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import SearchControl from './utils/SearchControl'
import CollectorTable from './utils/CollectorTable'

/**
 * Provides a schedule for a host processes
 * First usage in the InspectionSelect
 */
class HostSchedule extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{
                dictURL:this.props.dictURL
            },                                  //HostScheduleDTO.java
            labels:{
                search:'',
                scheduled:'',
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
        Locales.createLabels(this)
        this.load()
    }
    /**
     * load the HostScheduleDTO
     */
    load(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/host/schedule", this.state.data, (query,result)=>{
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
                    <Col xs='12' sm='12' lg='9' xl='9'>
                        <h5>{this.state.data.hostDictionary+' '+this.state.labels.scheduled}</h5>
                    </Col>
                    <Col  xs='12' sm='12' lg='3' xl='3'>
                        <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.load} />
                    </Col>
                </Row>
                <Row>
                    <Col className={Pharmadex.settings.activeBorder}>
                        <CollectorTable
                             tableData={this.state.data.table}
                             loader={this.load}
                             headBackground={Pharmadex.settings.tableHeaderBackground}
                             styleCorrector={(key)=>{
                                let keyStr= key.toUpperCase()
                                if(keyStr=='COME' || keyStr=="DONE"){
                                    return {width:'15%'}
                                }
                             }}
                        />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default HostSchedule
HostSchedule.propTypes={
    dictURL: PropTypes.string.isRequired,   //url of dictionary.host.{applications,inspections}
    recipient:PropTypes.string.isRequired,           //identifier of the recepient for messaging
}