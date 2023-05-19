import React , {Component} from 'react'
import {Row, Col, Container, Navbar, NavbarBrand, Collapse, NavbarToggler,Nav, NavItem, NavLink, 
    UncontrolledDropdown,DropdownToggle, DropdownMenu, DropdownItem} from 'reactstrap'
import Fetchers from './utils/Fetchers'
import Locales from './utils/Locales'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import CollectorTable from './utils/CollectorTable'
import ButtonUni from './form/ButtonUni'
import ActivityManager from './ActivityManager'
import SearchControlNew from './utils/SearchControlNew'
import Downloader from './utils/Downloader'
import ApplicationEvents from './applevents/ApplicationEvents'
import ApplicationHistory from './reports/ApplicationHistory'
import ApplicationRegisters from './reports/ApplicationRegisters'
import Thing from './Thing'
import FieldDisplay from './form/FieldDisplay'
import MonitoringActual from './MonitoringActual'
import MonitoringScheduled from './MonitoringScheduled'
import MonitoringFullsearch from './MonitoringFullsearch'

/**
 * Responsible for assigned activities. Any user, except an applicant
 */
class Monitoring extends Component{
    constructor(props){
        super(props)
        this.state={
            menu:'',
            identifier:Date.now().toString(),
            labels:{
                global_exit:'',
                monitoring:'',
                global_cancel:'',
                search:'',
                actual:'',
                scheduled:'',
                exportExcel:'',
                cancel:'',
                fullSearch:'',
                dateactualLb:''
            },
            data:{},
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.content=this.content.bind(this)
        this.swich=this.swich.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
     eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            if(data.subject=="reload"){
                //this.loadActual()
                this.setState(this.state)
            }
        }
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

     /**
     * Paint full thing
     */
    paintApplication(){
        let ret=[]
        let path=this.state.data.thing.path
        this.state.data.thing.nodeId=0
        if(Fetchers.isGoodArray(path)){
            ret.push(
                <Row  style={{justifyContent: 'right'}} >
                <Col xs='12' sm='12' lg='2' xl='2' className="d-flex justify-content-end">
                            <ButtonUni
                                    label={this.state.labels.cancel}
                                    color='info'
                                    onClick={()=>{
                                        this.state.data.thing.nodeId=0
                                        this.setState(this.state)
                                    }}
                                />
                        </Col>
                        </Row>
            )
            ret.push(
                <ApplicationHistory key='ah1' nodeid={this.state.data.thing.nodeId} recipient={this.state.identifier}/>
            )
            ret.push(
                <ApplicationEvents key='ae1' appldataid={this.state.data.thing.nodeId} recipient={this.state.identifier} />
            )
            ret.push(
                <ApplicationRegisters key='ar1' nodeid={this.state.data.thing.nodeId} recipient={this.state.identifier}/>
            )
            path.forEach((element, index)=>{
                ret.push(
                    <Thing
                        key={index}
                        data={element}
                        recipient={this.state.identifier}
                        readOnly
                        narrow
                    />
                )
            })
        }
        return ret
    }
 
    swich(){
        let parStr=""
        let data={}
        //this.state.data.thing.nodeId=0
        switch(this.state.menu){
            case "activitymanager":
                parStr = Navigator.parameterValue();
                data = JSON.parse(parStr)
                return <ActivityManager historyId={data.historyId} recipient={this.state.identifier}/>
            case "actual":
                return <MonitoringActual/>
            case "scheduled":
                return <MonitoringScheduled/>
            case "fullsearch":
                return <MonitoringFullsearch/>
            default:
                return <MonitoringActual/>
        }
    }
    /**
     * determine the content
     */
    content(){
        if(this.state.data.thing != undefined && this.state.data.thing.nodeId > 0){
            return (
                <Row>
                    <Col>
                       {this.paintApplication()}
                    </Col>
                </Row>
            )
        }else{
            return(
                <Row>
                    <Col>
                        {this.swich()}
                    </Col>
                </Row>
            )
        }
    }

    render(){
        if(this.state.labels.locale == undefined || this.state.data == undefined){
            return []
        }

        this.state.menu = Navigator.componentName().toLowerCase()

            return(
            <Container fluid>
                <Row>
                    <Col>
                        <Navbar light expand="md">
                            <NavbarBrand>{this.state.labels.monitoring}</NavbarBrand>
                            <NavbarToggler onClick={()=>{this.state.isOpen=!this.state.isOpen; this.setState(this.state)}} className="me-2" />
                                <Collapse isOpen={this.state.isOpen} navbar>
                                    <Nav className="me-auto" navbar>
                                        <Nav>     
                                            <NavItem active={this.state.menu=='actual' || this.state.menu.length==0}>
                                                <NavLink href={"/"+Navigator.tabSetName()+"#"+Navigator.tabName()+"/actual"}>{this.state.labels.actual}</NavLink>
                                            </NavItem>
                                        </Nav>
                                        <Nav>     
                                            <NavItem active={this.state.menu=='scheduled'}>
                                                <NavLink href={"/"+Navigator.tabSetName()+"#"+Navigator.tabName()+"/scheduled"}>{this.state.labels.scheduled}</NavLink>
                                            </NavItem>
                                        </Nav>
                                        {/* ik */}
                                        <Nav>     
                                            <NavItem active={this.state.menu=='fullsearch'}>
                                                <NavLink href={"/"+Navigator.tabSetName()+"#"+Navigator.tabName()+"/fullsearch"}>{this.state.labels.fullSearch}</NavLink>
                                            </NavItem>
                                        </Nav>
                                        <Nav>     
                                            <NavItem>
                                                <NavLink href={"/"+Navigator.tabSetName()+"#"}>{this.state.labels.global_exit}</NavLink>
                                            </NavItem>
                                        </Nav>
                                    </Nav>
                                </Collapse>
                        </Navbar>
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
export default Monitoring
Monitoring.propTypes={
    
}