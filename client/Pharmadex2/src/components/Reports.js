import React , {Component} from 'react'
import {Container, Row, Col, Navbar, NavbarBrand, Collapse, NavbarToggler,Nav, NavItem, NavLink, Button} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Dictionary from './Dictionary'
import TableReport from './reports/TableReport'
import PrintPreviewReport from './reports/PrintPreviewReport'

/**
 * Responsible for reports
 */
class Reports extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},                            //ReportDTO
            identifier:Date.now().toString(),
            labels:{
                global_exit:'',
                reports:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.content=this.content.bind(this)
        this.loader=this.loader.bind(this)
        this.exitNav=this.exitNav.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject == 'onSelectionChange'){
                    this.state.data.dict=data.data
                    this.loader();
                }
                if(data.subject=='reportRootDictionary'){
                    Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/reset/root", this.state.data, (query,result)=>{
                        this.state.data=result
                        this.setState(this.state)
                    })
                }
                if(data.subject=='reportPathDictionary'){
                    this.state.data.dict.pathSelected=data.data
                    Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/reset/path", this.state.data, (query,result)=>{
                        this.state.data=result
                        this.setState(this.state)
                    })
                }
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loader();
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    loader(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/load", this.state.data,(query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }
    /**
     * Depend on data state, load a coponent
     */
    content(){
        //print preview
        let component=Navigator.componentName().toLowerCase()
        if(component=='printpreview'){
            let parStr = Navigator.parameterValue()
            let params=JSON.parse(parStr)
            return <PrintPreviewReport data={params} />
        }
        //the rest
        let data = this.state.data
        if(data.thing.nodeId>0){
            return []   //TODO thing with the breadcrumb
        }
        if(Fetchers.isGoodArray(data.table.headers.headers)){
            return <TableReport data={this.state.data} recipient={this.state.identifier} />
        }
        return  <Dictionary identifier={'dict.'+this.state.identifier} 
                            recipient={this.state.identifier} 
                            data={data.dict} 
                            display/>
    }
    exitNav(){
        if(Navigator.componentName().toLowerCase()=='printpreview'){
            return(
            <Nav className="me-auto" navbar>
                <Nav>     
                    <NavItem>
                        <Button color="link"
                            onClick={()=>{
                                window.close()
                            }}
                        >
                            {this.state.labels.global_exit}
                        </Button>
                    </NavItem>
                </Nav>
            </Nav>
            )
        }else{
            return(
                <Nav className="me-auto" navbar>
                    <Nav>     
                        <NavItem>
                            <NavLink href={"/"+Navigator.tabSetName()+"#"}>{this.state.labels.global_exit}</NavLink>
                        </NavItem>
                    </Nav>
                </Nav>
            )
        }
    }
    render(){
        if(this.state.data.dict == undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                    <Navbar light expand="md">
                            <NavbarBrand>{this.state.labels.reports}</NavbarBrand>
                            <NavbarToggler onClick={()=>{this.state.isOpen=!this.state.isOpen; this.setState(this.state)}} className="me-2" />
                                <Collapse isOpen={this.state.isOpen} navbar>
                                   {this.exitNav()}
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