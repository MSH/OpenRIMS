import React , {Component} from 'react'
import {Container, Row, Col, Navbar, NavbarBrand, Collapse, NavbarToggler,Nav, NavItem, NavLink, Button} from 'reactstrap'
import Locales from './utils/Locales'
import ReportsExternal from './ReportsExternal'
import Navigator from './utils/Navigator'
import ReportsOld from './ReportsOld'
/**
 * Responsible for reports
 */
class Reports extends Component{
    constructor(props){
        super(props)
        this.state={
            menu:'',
            title:'',
            identifier:Date.now().toString(),
            data:{}, 
            labels:{
                global_exit:'',
                reports:'',
                reportOld:'',
                publicavailable:'',
                externalreport:""
            },
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.content=this.content.bind(this)
        this.exitNav=this.exitNav.bind(this)
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

    exitNav(){
        if(Navigator.componentName().toLowerCase()=='printpreview'){
            return(
                    <NavItem>
                        <Button color="link"
                            onClick={()=>{
                                window.close()
                            }}
                        >
                            {this.state.labels.global_exit}
                        </Button>
                    </NavItem>
            )
        }else{
            return(
                        <NavItem>
                            <NavLink href={"/"+Navigator.tabSetName()+"#"}>{this.state.labels.global_exit}</NavLink>
                        </NavItem>
            )
        }
    }

    
    content(){ 
        switch( this.state.menu){
            case "externalreport":
                return <ReportsExternal/>
            case "reportold":
                return <ReportsOld/>
            default:
                return <ReportsExternal/>
        }
    }
    render(){
        this.state.menu=Navigator.componentName().toLowerCase()
        return(
            <Container fluid>
                <Row>
                    <Col>
                    <Navbar light expand="md">
                            <NavbarBrand>{this.state.labels.reports}</NavbarBrand>

                            <NavbarToggler onClick={()=>{this.state.isOpen=!this.state.isOpen; this.setState(this.state)}} className="me-2" />
                                <Collapse isOpen={this.state.isOpen} navbar>
                                    <Nav className="me-auto" navbar>
                                     <Nav>     
                                            <NavItem active={this.state.menu=='externalreport' || this.state.menu.length==0}>
                                                <NavLink href={"/"+Navigator.tabSetName()+"#"+Navigator.tabName()+"/externalreport"}>{this.state.labels.publicavailable}</NavLink>
                                            </NavItem>
                                        </Nav> 
                                        <Nav>     
                                            <NavItem active={this.state.menu=='reportold'}>
                                                <NavLink href={"/"+Navigator.tabSetName()+"#"+Navigator.tabName()+"/reportOld"}>{this.state.labels.reportOld}</NavLink>
                                            </NavItem>
                                        </Nav>
                                        
                                        {this.exitNav()}
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
export default Reports
Reports.propTypes={
    
}