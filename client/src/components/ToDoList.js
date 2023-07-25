import React , {Component} from 'react'
import {Row, Col, Container, Navbar, NavbarBrand, Collapse, NavbarToggler,Nav, NavItem, NavLink} from 'reactstrap'
import Fetchers from './utils/Fetchers'
import Locales from './utils/Locales'
import Navigator from './utils/Navigator'
import ActivityManager from './ActivityManager'
import Table from './form/Table'

/**
 * Responsible for assigned activities. Any user, except an applicant
 */
class ToDoList extends Component{
    constructor(props){
        super(props)
        this.params={
            attentionAPI:"/api/"+Navigator.tabSetName()+"/activity/todo/attention",
            attention:"attention",
            actualAPI:"/api/"+Navigator.tabSetName()+"/activity/todo/actual",
            actual: "actual",
            scheduledAPI:"/api/"+Navigator.tabSetName()+"/activity/todo/scheduled",
            scheduled:"scheduled"
        }
        this.state={
            menu:'',
            identifier:Date.now().toString(),
            labels:{
                global_exit:'',
                todolist:'',
                global_cancel:'',
                search:'',
                alert:'',
                actual:'',
                scheduled:'',
            },
            data:{},
        }
        this.eventProcessor=this.eventProcessor.bind(this)
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
            if(data.subject==this.params.actual || data.subject==this.params.scheduled || data.subject==this.params.attention){
                    let dataParams={
                        historyId:data.data.selectedID,
                    }
                    let param = JSON.stringify(dataParams)
                    Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/application/or/activity", dataParams, (query,result)=>{
                        if(result.application){
                            Navigator.navigate("applications","applicationstart",param) 
                        }else{
                            Navigator.navigate(Navigator.tabName(),"activitymanager",param)
                        }
                    })
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

    alert(){
        return(
            <Table recipient={this.state.identifier} onSelectSubject={this.params.attention} loadAPI={this.params.attentionAPI}/>
         )
    }
    actual(){
        return(
           <Table recipient={this.state.identifier} onSelectSubject={this.params.actual} loadAPI={this.params.actualAPI}/>
        )
    }

    scheduled(){
        return(
            <Table recipient={this.state.identifier} onSelectSubject={this.params.scheduled} loadAPI={this.params.scheduledAPI}/>
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
            case "alert":
                return(this.alert())
            case "actual":
                return(this.actual())
            case "scheduled":
                return(this.scheduled())
            default:
                return (this.alert())

        }
    }
    render(){
        this.state.menu=Navigator.componentName().toLowerCase()
            return(
            <Container fluid>
                <Row>
                    <Col>
                        <Navbar light expand="md">
                            <NavbarBrand>{this.state.labels.todolist}</NavbarBrand>
                            <NavbarToggler onClick={()=>{this.state.isOpen=!this.state.isOpen; this.setState(this.state)}} className="me-2" />
                                <Collapse isOpen={this.state.isOpen} navbar>
                                    <Nav className="me-auto" navbar>
                                        <Nav>     
                                            <NavItem active={this.state.menu=='alert' || this.state.menu.length==0}>
                                                <NavLink href={"/"+Navigator.tabSetName()+"#"+Navigator.tabName()+"/alert"}>{this.state.labels.alert}</NavLink>
                                            </NavItem>
                                        </Nav>
                                        <Nav>     
                                            <NavItem active={this.state.menu=='actual'}>
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
export default ToDoList
ToDoList.propTypes={
    
}