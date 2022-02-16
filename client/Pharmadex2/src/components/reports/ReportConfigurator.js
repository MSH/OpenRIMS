import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import ButtonUni from '../form/ButtonUni'
import CollectorTable from '../utils/CollectorTable'
import Pharmadex from '../Pharmadex'
import Thing from '../Thing'

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
                form:false,
            },
            labels:{
                reports:'',
                global_cancel:'',
                global_suspend:'',
                global_save:'',
                global_add:'',
                success:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.content=this.content.bind(this)
        this.load=this.load.bind(this)
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
                Fetchers.postJSON("/api/admin/report/parameters/renew", this.state.data, (query,result)=>{
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.success, color:'success'})
                    this.state.data.form=false
                    this.load()
                })
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
                        {this.buttons()}
                        <Row>
                            <Col>
                                <Thing
                                    data={this.state.data.report}
                                    recipient={this.state.identifier}
                                />
                            </Col>
                        </Row>
                        {this.buttons()}
                    </Col>
                </Row>
            )
        }else{
            return(
                <Row>
                    <Col>
                        <Row>
                            <Col xs='0' sm='6' lg='11' xl='11'>
                            </Col>
                            <Col xs='12' sm='6' lg='1' xl='1'>
                                <ButtonUni
                                    label={this.state.labels.global_add}
                                    color="primary"
                                    onClick={()=>{
                                        this.state.data.report.nodeId=0
                                        this.state.data.form=true
                                        this.load()
                                    }}
                                />
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