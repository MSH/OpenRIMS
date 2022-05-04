import React , {Component} from 'react'
import {Container,Row, Col, Navbar, NavbarBrand, Collapse, NavbarToggler,Nav, NavItem, NavLink, 
    UncontrolledDropdown,DropdownToggle, DropdownMenu, DropdownItem} from 'reactstrap'
import Locales from './utils/Locales'
import Navigator from './utils/Navigator'
import Authorities from './Authorities'
import Authority from './Authority'
import UserElement from './UserElement'
import Dictionaries from './Dictionaries'
import Tiles from './Tiles'
import Workflows from './Workflows'
import WorkflowConfigurator from './WorkflowConfigurator'
import DataConfigurator from './DataConfigurator'
import DataFormPreview from './dataconfig/DataFormPreview'
import Messages from './Messages'
import Resources from './Resources'
import Actions from './Actions'
import ATCCodes from './ATCCodes'
import Excipients from './Excipients'
import Inns from './Inns'
import ReportConfigurator from './reports/ReportConfigurator'
import Import_A from './Import_A'
import Import_B from './Import_B'

/**
 * Administrative functions for the supervisor
 */
class Administrate extends Component{
    constructor(props){
        super(props)
        this.state={
            labels:{
                specialfeatures:'',
                users:'',
                applicants:'',
                manageapplications:'',
                dictionaries:'',
                tiles:"",
                workflows:'',
                authorities:'',
                system:'',
                language:'',
                global_exit:'',
                workflows:'',
                dataconfigurator:'',
                reports:'',
                messages:'',
                resources:'',
                processes:'',
                stages:'',
                label_actions:'',
                configurations:'',
                dataimport:'',
                adminunits:'',
                legacydata:'',
            },
            isOpen:false,
            menu:'',
            identifier:Date.now().toString(),
        }
    }
    componentDidMount(){
        Locales.resolveLabels(this)
    }
    /**
     * Load the current component in accordance with menu item selected
     * Mark the menu item
     */
    currentComponent(){
        let parStr = ""
        let params = {}
        this.state.menu=Navigator.componentName().toLowerCase()
        switch(this.state.menu){
            case "dictionaries":
                return <Dictionaries />
            case "authorities":
                return <Authorities />
            case "tiles":
                return <Tiles />
            case "workflows":
                return <Workflows/>
            case "resources":
                return <Resources />
            case "workflowconfigurator":
                parStr = Navigator.parameterValue()
                params=JSON.parse(parStr)
                return <WorkflowConfigurator dictNodeId={params.dictNodeId}/>
            case "actions":
                return <Actions />
            case "dataconfigurator":
                parStr = Navigator.parameterValue()
                if(parStr!=undefined && parStr.length>0 && parStr!='dataconfigurator'){
                    params=JSON.parse(parStr)
                }else{
                    params={
                        nodeId:0
                    }
                }
                return <DataConfigurator nodeId={params.nodeId} vars={params.nodeId!=0}/>
            case "reports":
                return <ReportConfigurator/>
            case "messages":
                return <Messages/>
            case "dataformpreview":
                parStr = Navigator.parameterValue()
                params=JSON.parse(parStr)
                return <DataFormPreview nodeId={params.nodeId} />
            case "authority":
                parStr = Navigator.parameterValue()
                params=JSON.parse(parStr)
                return <Authority url={params.url} parentId={params.parentId} nodeId={params.nodeId} caller={params.caller}/>
            case "userelement":
                parStr = Navigator.parameterValue()
                params=JSON.parse(parStr)
                return <UserElement conceptId={params.conceptId} userId={params.userId} caller={params.caller}/>
            case "atccodes":
                return <ATCCodes recipient={this.state.identifier} readOnly={false}/>
            case "excipients":
                return <Excipients recipient={this.state.identifier} readOnly={false}/>
            case "inns":
                return <Inns recipient={this.state.identifier} readOnly={false}/>
            case "import_a":
                return <Import_A/>
            case "import_b":
                return <Import_B/>
            default:
                return []
        }
    }

    render(){
        if(this.state.labels.specialfeatures.length==0){
            return []
        }
        this.state.menu=Navigator.componentName().toLowerCase()
        return(
            <Container fluid>
                <Row>
                    <Col>
                    <Navbar light expand="md">
                        <NavbarBrand>{this.state.labels.specialfeatures}</NavbarBrand>
                        <NavbarToggler onClick={()=>{this.state.isOpen=!this.state.isOpen; this.setState(this.state)}} className="me-2" />
                            <Collapse isOpen={this.state.isOpen} navbar>
                                <Nav className="me-auto" navbar>
                                    <NavItem>
                                        <NavLink active={this.state.menu=='authorities' || 
                                                         this.state.menu=='authority'}
                                                 href="/admin#administrate/authorities">
                                            <div>
                                                <i className="fas fa-xs fa-users mr-1"></i>
                                                {this.state.labels.authorities}
                                            </div>
                                        </NavLink>
                                    </NavItem>


                                    <UncontrolledDropdown nav inNavbar>
                                        <DropdownToggle nav caret>
                                            {this.state.labels.processes}
                                        </DropdownToggle>
                                        <DropdownMenu right>
                                            <DropdownItem>
                                                <NavItem active={this.state.menu=='workflows' || this.state.menu=='workflowconfigurator'}>
                                                    <NavLink href="/admin#administrate/workflows">
                                                    <div>
                                                        <i className="fas fa-xs fa-link mr-1"></i>
                                                        {this.state.labels.workflows}
                                                    </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                            <div>
                                                <NavItem active={this.state.menu=='actions'}>
                                                    <NavLink href="/admin#administrate/actions">
                                                    <div>
                                                        <i className="fas fa-xs fa-check-double mr-1"></i>
                                                        {this.state.labels.label_actions}
                                                    </div>
                                                    </NavLink>
                                                </NavItem>
                                            </div>
                                            </DropdownItem>
                                        </DropdownMenu>
                                        </UncontrolledDropdown>


                                        <UncontrolledDropdown nav inNavbar>
                                        <DropdownToggle nav caret>
                                            {this.state.labels.configurations}
                                        </DropdownToggle>
                                        <DropdownMenu right>
                                            <DropdownItem>
                                                <NavItem active={this.state.menu=='messages'}>
                                                    <NavLink active={this.state.menu=='messages'}
                                                                href="/admin#administrate/messages">
                                                    <div>
                                                        <i className="fas fa-xs fa-comments mr-1"></i>
                                                        {this.state.labels.messages}
                                                    </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                                <NavItem>
                                                    <NavLink active={this.state.menu=='dictionaries'}
                                                                        href="/admin#administrate/dictionaries">
                                                        <div>
                                                            <i className="fas fa-xs fa-atlas mr-1"></i>
                                                            {this.state.labels.dictionaries}
                                                        </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                                <NavItem>
                                                    <NavLink active={this.state.menu=='resources'}
                                                                    href="/admin#administrate/resources">
                                                        <div>
                                                            <i className="fas fa-xs fa-boxes mr-1"></i>
                                                            {this.state.labels.resources}
                                                        </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                                <NavItem>
                                                    <NavLink active={this.state.menu=='dataconfigurator' || this.state.menu=='dataformpreview'}
                                                        href="/admin#administrate/dataconfigurator">
                                                        <div>
                                                            <i className="fas fa-xs fa-adjust mr-1"></i>
                                                            {this.state.labels.dataconfigurator}
                                                        </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                                <NavItem>
                                                    <NavLink active={this.state.menu=='reports' || this.state.menu=='reportconfig'}
                                                        href="/admin#administrate/reports">
                                                        <div>
                                                            <i className="fas fa-xs fa-table mr-1"></i>
                                                            {this.state.labels.reports}
                                                        </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                                <NavItem>
                                                    <NavLink active={this.state.menu=='tiles'}
                                                        href="/admin#administrate/tiles">
                                                        <div>
                                                            <i className="fas fa-xs fa-th mr-1"></i>
                                                            {this.state.labels.tiles}
                                                        </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                        </DropdownMenu>
                                        </UncontrolledDropdown>
                                        <UncontrolledDropdown nav inNavbar>
                                        <DropdownToggle nav caret>
                                            {this.state.labels.dataimport}
                                        </DropdownToggle>
                                        <DropdownMenu right>
                                            <DropdownItem>
                                                <NavItem active={this.state.menu=='import_a'}>
                                                    <NavLink active={this.state.menu=='import_a'}
                                                                href="/admin#administrate/import_a">
                                                    <div>
                                                        <i className="fas fa-map-marked-alt mr-1"></i>
                                                        {this.state.labels.adminunits}
                                                    </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                                <NavItem active={this.state.menu=='import_b'}>
                                                    <NavLink active={this.state.menu=='import_b'}
                                                                href="/admin#administrate/import_b">
                                                    <div>
                                                        <i className="fas fa-file-import mr-1"></i>
                                                        {this.state.labels.legacydata}
                                                    </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                        </DropdownMenu>
                                        </UncontrolledDropdown>
                                        <NavItem>
                                            <NavLink href="/admin">{this.state.labels.global_exit}</NavLink>
                                        </NavItem>
                                </Nav>
                            </Collapse>
                        </Navbar>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.currentComponent()}
                    </Col>
                </Row>
            </Container>
        )
    }
}
export default Administrate
