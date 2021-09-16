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
            },
            data:{},
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadData=this.loadData.bind(this)
        this.content=this.content.bind(this)
        this.actual=this.actual.bind(this)
        this.scheduled=this.scheduled.bind(this)
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
                    <Col xs='12' sm='12' lg='8' xl='8'/>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable key='11'
                            tableData={this.state.data.table}
                            loader={this.loadData}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(header=='days'){
                                    return {width:'10%'}
                                }
                            }}
                            linkProcessor={(row,col)=>{
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
                    <Col xs='12' sm='12' lg='8' xl='8'/>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable key='33'
                            tableData={this.state.data.scheduled}
                            loader={this.loadData}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(header=='days'){
                                    return {width:'10%'}
                                }
                            }}
                            linkProcessor={(row,col)=>{
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
 
    /**
     * determine the content
     */
    content(){
        let parStr=""
        let data={}
        switch( this.state.menu){
            case "activitymanager":
                parStr = Navigator.parameterValue();
                data = JSON.parse(parStr)
                return <ActivityManager historyId={data.historyId} recipient={this.state.identifier}/>
            case "actual":
                return(this.actual())
            case "scheduled":
                return(this.scheduled())
            default:
                return (this.actual())

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