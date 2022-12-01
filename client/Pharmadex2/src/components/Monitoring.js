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
import SearchControl from './utils/SearchControl'
import Downloader from './utils/Downloader'
import ApplicationEvents from './applevents/ApplicationEvents'
import ApplicationHistory from './reports/ApplicationHistory'
import ApplicationRegisters from './reports/ApplicationRegisters'
import Thing from './Thing'
import FieldDisplay from './form/FieldDisplay'

/**
 * Responsible for assigned activities. Any user, except an applicant
 */
class ToDoList extends Component{
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
        this.loadData=this.loadData.bind(this)
        this.content=this.content.bind(this)
        this.actual=this.actual.bind(this)
        this.scheduled=this.scheduled.bind(this)
        this.fullsearch=this.fullsearch.bind(this)
        this.drillDown=this.drillDown.bind(this)
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
                this.loadData()
            }
        }
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.loadData();
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    loadData(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/my/monitoring", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    actual(){
        return(
            <Row>
                <Col>
                    <Row className="mb-3">
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <SearchControl key="1" label={this.state.labels.search} table={this.state.data.table} loader={this.loadData}/>
                        </Col>
                        <Col xs='0' sm='0' lg='6' xl='6'/>
                        <Col xs='12' sm='12' lg='2' xl='2' className="d-flex justify-content-end">
                        <ButtonUni
                            label={this.state.labels.exportExcel}
                            onClick={()=>{
                                let downloader = new Downloader();
                                Fetchers.setJustLoaded(this.state.data,false)
                                downloader.postDownload("/api/"+Navigator.tabSetName()+ "/my/monitoring/actual/excel",
                                this.state.data, "monitoring_actual.xlsx");
                            }} 
                            color={"info"}
                                />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable key='11'
                            tableData={this.state.data.table}
                            loader={this.loadData}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(header=='scheduled'){
                                    return {width:'10%'}
                                }
                            }}
                            selectRow={(row)=>{
                                let data={
                                    url:this.state.data.url,
                                    applDictNodeId:this.state.data.dictItemId,
                                    historyId:this.state.data.table.rows[row].dbID,
                                }
                                    let param = JSON.stringify(data)
                                    Navigator.navigate(Navigator.tabName(),"activitymanager",param)
                                }}   
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
        )
    }

    scheduled(){
        return(
            <Row>
                <Col>
                    <Row className="mb-3">
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <SearchControl key='3' label={this.state.labels.search} table={this.state.data.scheduled} loader={this.loadData}/>
                        </Col>
                        <Col xs='0' sm='0' lg='6' xl='6'/>
                        <Col xs='12' sm='12' lg='2' xl='2' className="d-flex justify-content-end">
                        <ButtonUni
                            label={this.state.labels.exportExcel}
                            onClick={()=>{
                                let downloader = new Downloader();
                                Fetchers.setJustLoaded(this.state.data,false)
                                downloader.postDownload("/api/"+Navigator.tabSetName()+ "/my/monitoring/scheduled/excel",
                                this.state.data, "monitoring_scheduled.xlsx");
                            }} 
                            color={"info"}
                                />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable key='33'
                            tableData={this.state.data.scheduled}
                            loader={this.loadData}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(header=='scheduled'){
                                    return {width:'10%'}
                                }
                            }}
                            selectRow={(row)=>{
                                let data={
                                    url:this.state.data.url,
                                    applDictNodeId:this.state.data.dictItemId,
                                    historyId:this.state.data.scheduled.rows[row].dbID,
                                }
                                    let param = JSON.stringify(data)
                                    Navigator.navigate(Navigator.tabName(),"activitymanager",param)
                                }}   
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
        )
    }
 
    fullsearch(){
        return(
<Row>
<Col>
<Row className="mb-3">
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <SearchControl key='4' label={this.state.labels.search} table={this.state.data.fullsearch} loader={this.loadData}/>
                        </Col>
                        <Col xs='12' sm='12' lg='6' xl='6' className="d-flex justify-content-center">
                        <small>{this.state.labels.dateactualLb}</small>
                        <FieldDisplay attribute='dateactual' component={this} mode='time'/>
                        </Col>
                        <Col xs='12' sm='12' lg='2' xl='2' className="d-flex justify-content-end">
                        <ButtonUni
                            label={this.state.labels.exportExcel}
                            onClick={()=>{
                                let downloader = new Downloader();
                                Fetchers.setJustLoaded(this.state.data,false)
                                downloader.postDownload("/api/"+Navigator.tabSetName()+ "/my/monitoring/fullsearch/excel",
                                this.state.data, "monitoring_fullsearch.xlsx");
                            }} 
                            color={"info"}
                                />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable key='44'
                            tableData={this.state.data.fullsearch}
                            loader={this.loadData}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(header=='fullsearch'){
                                    return {width:'10%'}
                                }
                            }}
                            selectRow={(row)=>{
                                this.drillDown(row)
                                }}   
                            />
                        </Col>
                    </Row>
</Col>
</Row>
        )
    }
    drillDown(rowNo){
        this.state.data.thing.nodeId=this.state.data.fullsearch.rows[rowNo].dbID
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/my/monitoring/application", this.state.data, (query,result)=>{
            this.state.data=result
            this.state.title=result.thing.path[0].title
            this.setState(this.state)
        })
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
    switch( this.state.menu){
        case "activitymanager":
            parStr = Navigator.parameterValue();
            data = JSON.parse(parStr)
            return <ActivityManager historyId={data.historyId} recipient={this.state.identifier}/>
        case "actual":
            return(this.actual())
        case "scheduled":
            return(this.scheduled())
        case "fullsearch":
            return(this.fullsearch())
        default:
            return (this.actual())
    }
 }
    /**
     * determine the content
     */
    content(){
        if(this.state.data.thing.nodeId>0){
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
        if(this.state.data.table== undefined || this.state.data.table.rows == undefined || this.state.labels.locale==undefined){
            return []
        }else{
            this.state.menu=Navigator.componentName().toLowerCase()
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
                                            <NavItem active={this.state.menu=='fullSearch'}>
                                                <NavLink href={"/"+Navigator.tabSetName()+"#"+Navigator.tabName()+"/fullSearch"}>{this.state.labels.fullSearch}</NavLink>
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
}
export default ToDoList
ToDoList.propTypes={
    
}