import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import ButtonUni from '../form/ButtonUni'
import CollectorTable from '../utils/CollectorTable'
import Pharmadex from '../Pharmadex'
import Thing from '../Thing'
import Dictionary from '../Dictionary'

/**
 * Report configurator
 * 
 */
class ReportConfigurator extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            thingid:'*',
            data:{                      //ReportConfigDTO.java
                form:false
            },
            labels:{
                reports:'',
                reportOld:'',
                global_cancel:'',
                global_suspend:'',
                global_save:'',
                global_add:'',
                success:'',
                global_renewExternal:'',
                warningRemove:'',
                publicavailable:'',
            },
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.content=this.content.bind(this)
        this.load=this.load.bind(this)
        this.renewExternal=this.renewExternal.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            if(data.subject=="thingLoaded"){
                this.state.thingid=data.from
            }
            if(data.subject=="saved" || data.subject=="savedByAction"){
                this.state.data.report=data.data
                this.load()
            }
        }
        
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
        Fetchers.postJSON("/api/admin/report/configuration/load", this.state.data, (query,result)=>{
            this.state.data=result          //ReportConfigDTO.java
            this.setState(this.state)
        })
    }
    /**
     * Re-calculate the Data Warehouse
     */
    renewExternal(){
        Fetchers.postJSON("/api/admin/report/renewexternal", this.state.data, (query, result)=>{

        })
    }

    /**
     * Save, Suspend, Cancel on the form
     * @returns the button bar
     */
    buttons(){
        return(
            <Row>
                <Col xs='0' sm='6' lg='9' xl='9'>
                </Col>
                <Col xs='12' sm='2' lg='1' xl='1'>
                    <ButtonUni
                        label={this.state.labels.global_cancel}
                        color="info"
                        onClick={()=>{
                            this.state.data.form=false
                            this.setState(this.state)
                        }}
                    />
                </Col>
                <Col xs='12' sm='2' lg='1' xl='1'>
                    <ButtonUni
                        label={this.state.labels.global_suspend}
                        color="warning"
                        onClick={()=>{
                            Fetchers.alerts(this.state.labels.warningRemove, ()=>{
                                    
                            }, null)
                        }}
                    />
                </Col>
                <Col xs='12' sm='2' lg='1' xl='1'>
                    <ButtonUni
                        label={this.state.labels.global_save}
                        color="success"
                        onClick={()=>{
                            Navigator.message(this.state.identifier, this.state.thingid, "saveAll", {})
                        }}
                    />
                </Col>
            </Row>
        )
    }

    content(){
        if(this.state.data.form){
            if(this.state.data.form==undefined){
                return []
            }
            return(
                <Row>
                    <Col>
                        <Row>
                            <Col>
                                <Thing
                                    data={this.state.data.report}
                                    recipient={this.state.identifier}
                                    readOnly={true}
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )
        }else{
            return(
                <Row>
                    <Col>
                        <Row>
                            <Col xs='0' sm='0' lg='10' xl='10'>
                                <h6>{this.state.labels.publicavailable}</h6>
                            </Col>
                            <Col xs='12' sm='6' lg='2' xl='2'>
                                <ButtonUni
                                    label={this.state.labels.global_renewExternal}
                                    color="primary"
                                    disabled = {!this.state.data.enabledrenewext}
                                    onClick={()=>{
                                        this.state.data.report.nodeId=0
                                        this.state.data.form=false
                                        this.state.data.enabledrenewext=false
                                        this.setState(this.state)
                                        this.renewExternal()
                                    }}
                                />
                            </Col>
                        </Row>
                        <Row>
                    <Col xs='12' sm='6' lg='6' xl='6' >
                        <Dictionary identifier={this.state.data.select.url} data={this.state.data.select} />
                    </Col>
                    <Col xs='12' sm='6' lg='6' xl='6' >
                        <small>here will be NRA reports</small>
                    </Col>
                    </Row>
                        <Row>
                            <Col>
                            <h6>{this.state.labels.reportOld}</h6>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.table}
                                    loader={this.load}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    styleCorrector={(header)=>{
                                        
                                    }}
                                    linkProcessor={(row,col)=>{
                                        this.state.data.report.nodeId=this.state.data.table.rows[row].dbID
                                        this.state.data.form=true
                                        this.load()
                                    }}
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )
        }
    }
    
    render(){
        if(this.state.labels.locale==undefined || this.state.data.table==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                    <h5>{this.state.labels.reports}</h5>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.content()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ReportConfigurator
ReportConfigurator.propTypes={
    
}