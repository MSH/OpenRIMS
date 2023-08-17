import React , {Component} from 'react'
import {Container, Row, Col, Navbar, NavbarBrand, Collapse, NavbarToggler,Nav, NavItem, NavLink, Button} from 'reactstrap'
import Navigator from './utils/Navigator'
import ApplicationSelect from './ApplicationSelect'
import ApplicationStart from './ApplicationStart'
import ActivityManager from './ActivityManager'
import Locales from './utils/Locales'
import AmendmentSelect from './AmendmentSelect'
import RenewSelect from './RenewSelect'
import DeRegistrationSelect from './DeRegistrationSelect'
import InspectionSelect from './InspectionSelect'
import PublicPermitData from './PublicPermitData'
/**
 * Tab Application. Switch between ApplicationSelect and ApplicationProcess components
 */
class Applications extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            menu:'',
            labels:{
                global_exit:'',
                manageapplications:'',
                newapplications:'',
                amendments:'',
                deregistration:'',
                renew:'',
                inspections:'',
                global_back:'',
            }
        }
        this.component=this.component.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
        this.back=this.back.bind(this)
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
    }
    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    component(){
        let parStr=""
        let data={}
        switch(this.state.menu){
            case "applicationstart":
                parStr = Navigator.parameterValue();
                data = JSON.parse(parStr)
                return <ApplicationStart data={data}/>
            case "activitymanager":
                parStr = Navigator.parameterValue();
                data = JSON.parse(parStr)
                return <ActivityManager historyId={data.historyId}/>
            case "publicpermitdata":
                parStr = Navigator.parameterValue();
                data = JSON.parse(parStr)
                if(data.historyId == undefined){
                    data.historyId=0
                }
                let permitData={
                    permitDataID:data.dataId,
                    historyID:data.historyId  
                }
                return <PublicPermitData data={permitData}/>
            case "amendments":
                return <AmendmentSelect/>
            case "deregistration":
                return <DeRegistrationSelect />
            case "renew":
                return <RenewSelect />
            case "inspections":
                    return <InspectionSelect />
            default:
                return <ApplicationSelect />
        }
    }
    /**
     * Go back
     */
    back(){
        return(
            <div className="d-flex justify-content-end">
            <Button size="sm"
                className="mr-1" color="info"
                onClick={()=>{
                    window.history.back()
                }}
            >
                    {this.state.labels.global_back}
            </Button>
            </div>
        )
    }

    render(){
        if(this.state.labels.locale == undefined){
            return []
        }
        this.state.menu=Navigator.componentName().toLowerCase()
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <Navbar light expand="md">
                            <NavbarBrand>{this.state.labels.manageapplications}</NavbarBrand>
                            <NavbarToggler onClick={()=>{this.state.isOpen=!this.state.isOpen; this.setState(this.state)}} className="me-2" />
                                <Collapse isOpen={this.state.isOpen} navbar>
                                    <Nav className="me-auto" navbar>
                                        <Nav>     
                                            <NavItem active={this.state.menu=='applicationselect' 
                                            || this.state.menu=='applicationstart' 
                                            || this.state.menu=='activitymanager'
                                            || this.state.menu.length==0}>
                                                <NavLink href={"/guest"+"#"+Navigator.tabName()+"/applicationselect"}>{this.state.labels.newapplications}</NavLink>
                                            </NavItem>
                                        </Nav>
                                        {/* <Nav>     
                                            <NavItem active={this.state.menu=='renew'}>
                                                <NavLink href={"/guest"+"#"+Navigator.tabName()+"/renew"}>{this.state.labels.renew}</NavLink>
                                            </NavItem>
                                        </Nav> */}
                                        <Nav>     
                                            <NavItem active={this.state.menu=='amendments'
                                                            || this.state.menu=='amendmentstart'}>
                                                <NavLink href={"/guest"+"#"+Navigator.tabName()+"/amendments"}>{this.state.labels.amendments}</NavLink>
                                            </NavItem>
                                        </Nav>
                                        <Nav>     
                                            <NavItem active={this.state.menu=='deregistration'}>
                                                <NavLink href={"/guest"+"#"+Navigator.tabName()+"/deregistration"}>{this.state.labels.deregistration}</NavLink>
                                            </NavItem>
                                        </Nav>
                                        <Nav>     
                                            <NavItem active={this.state.menu=='inspections'}>
                                                <NavLink href={"/guest"+"#"+Navigator.tabName()+"/inspections"}>{this.state.labels.inspections}</NavLink>
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
                    <Row>
                        <Col>
                            {this.back()}
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {this.component()}
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {this.back()}
                        </Col>
                    </Row>
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default Applications
Applications.propTypes={
    
}