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
import Processes from './Processes'
import WorkflowConfigurator from './WorkflowConfigurator'
import DataConfigurator from './DataConfigurator'
import DataFormPreview from './dataconfig/DataFormPreview'
import Messages from './Messages'
import Resources from './Resources'
import Actions from './Actions'
import ATCCodes from './ATCCodes'
import Excipients from './Excipients'
import Inns from './Inns'
import DataSources from './datasources/DataSources'
import Import_A from './Import_A'
import Import_B from './Import_B'
import ImportWorkflow from './ImportWorkflow'
import ActuatorAdm from './ActuatorAdm'
import ChangePassAdmin from './ChangePassAdmin'
import Import_ATC from './Import_ATC'
import Fetchers from './utils/Fetchers'
import Formats from './Formats'
import ReassignUsers from './ReassignUsers'
import Import_Messages from './Import_Messages'
import ReassignActivities from './ReassignActivities'
import ELAssistant from './ELAssistant'
import HelpFrame from './HelpFrame'

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
                processes:'',
                authorities:'',
                system:'',
                language:'',
                global_exit:'',
                dataconfigurator:'',
                datasourcesconfigurator:'',
                messages:'',
                resources:'',
                processes:'',
                stages:'',
                label_actions:'',
                configurations:'',
                dataimport:'',
                adminunits:'',
                legacydata:'',
                changePassAdmin:'',
                importATC:'',
                actuatorMonitoring:'',
                test_mail:'',
                systemsetting:'',
                exchangeconfig:'',
                formatdate:'',
                manageauthorities:'',
                reassignusers:'',
                importLocales:'',
                reassignactivities:'',
                elassistance:'',
                global_help:'',
                deployment:'',
                quick_start:'',
                setup_sandbox:'',
                production:'',
                community_label:'',
                community_site_addr:'',

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
            case "processes":
                return <Processes/>
            case "resources":
                return <Resources />
            case "elassistant":
                return <ELAssistant />
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
            case "datasourcesconfigurator":
                return <DataSources/>
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
            case "import_atc":
                return <Import_ATC/>
            case "importwf":
                return <ImportWorkflow/>
            case "import_messages":
                return <Import_Messages/>
            case "actuator":
                return <ActuatorAdm/>
            case "changepass":
                return <ChangePassAdmin/>
            case "formats":
                return <Formats />
            case "reassignusers":
                return <ReassignUsers />
            case "reassignactivities":
                    return <ReassignActivities/>
            case "helpframe":
                return <HelpFrame recipient={this.state.identifier} showIt={true}/>
            default:
                return <HelpFrame recipient={this.state.identifier} showIt={false}/>
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
                                <UncontrolledDropdown nav inNavbar>
                                    <DropdownToggle nav caret>
                                        {this.state.labels.authorities}
                                    </DropdownToggle>
                                    <DropdownMenu right>
                                        <DropdownItem>
                                            <NavItem>
                                                <NavLink active={this.state.menu=='authorities' || 
                                                                this.state.menu=='authority'}
                                                        href="/admin#administrate/authorities">
                                                    <div>
                                                        <i className="fas fa-xs fa-users mr-1"></i>
                                                        {this.state.labels.manageauthorities}
                                                    </div>
                                                </NavLink>
                                            </NavItem>
                                        </DropdownItem>
                                        <DropdownItem>
                                            <NavItem>
                                                <NavLink active={this.state.menu=='authorities' || 
                                                                this.state.menu=='authority'}
                                                        href="/admin#administrate/reassignusers">
                                                    <div>
                                                        <i className="fas fa-xs fa-users mr-1"></i>
                                                        {this.state.labels.reassignusers}
                                                    </div>
                                                </NavLink>
                                            </NavItem>
                                        </DropdownItem>
                                        <DropdownItem>
                                            <NavItem>
                                                <NavLink active={this.state.menu=='authorities' || 
                                                                this.state.menu=='authority'}
                                                        href="/admin#administrate/reassignactivities">
                                                    <div>
                                                        <i className="fas fa-xs fa-users mr-1"></i>
                                                        {this.state.labels.reassignactivities}
                                                    </div>
                                                </NavLink>
                                            </NavItem>
                                        </DropdownItem>
                                        <DropdownItem>
                                                <NavItem active={this.state.menu=='changepass'}>
                                                <NavLink active={this.state.menu=='changepass'}
                                                        href="/admin#administrate/changepass">
                                                    <div>
                                                    <i className="fas fa-file-import mr-1"></i>
                                                        {this.state.labels.changePassAdmin}
                                                    </div>
                                                </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                    </DropdownMenu>
                                </UncontrolledDropdown>

                                    <UncontrolledDropdown nav inNavbar>
                                        <DropdownToggle nav caret>
                                            {this.state.labels.processes}
                                        </DropdownToggle>
                                        <DropdownMenu right>
                                            <DropdownItem>
                                                <NavItem active={this.state.menu=='processes'|| this.state.menu=='workflowconfigurator'}>
                                                    <NavLink href="/admin#administrate/processes">
                                                    <div>
                                                        <i className="fas fa-xs fa-link mr-1"></i>
                                                        {this.state.labels.processes}
                                                    </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                            <div>
                                                <NavItem active={false}>
                                                    <div
                                                        style={{color:'gray'}}
                                                        onClick={()=>{
                                                            Fetchers.postJSON("/api/admin/mail/test",{},(query,result)=>{
                                                                let color='success'
                                                                if(!result.valid){
                                                                    color='danger'
                                                                }
                                                                Navigator.message('*', '*', 
                                                                    'show.alert.pharmadex.2', {mess:result.identifier, color:color})
                                                            })
                                                        }}
                                                    >
                                                        <i className="fas fa-xs fa-envelope mr-1"></i>
                                                        {this.state.labels.test_mail}
                                                    </div>
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
                                                <NavItem active={this.state.menu=='formats'}>
                                                    <NavLink active={this.state.menu=='formats'}
                                                                href="/admin#administrate/formats">
                                                    <div>
                                                        <i className="fa fa-globe fa-comments mr-1"></i>
                                                        {this.state.labels.formatdate}
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
                                                    <NavLink active={this.state.menu=='resources'}
                                                                    href="/admin#administrate/elassistant">
                                                        <div>
                                                            <i className="fas fa-xs fa-file-word mr-1"></i>
                                                            {this.state.labels.elassistance}
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
                                                    <NavLink active={this.state.menu=='datasourcesconfigurator' || this.state.menu=='reportconfig'}
                                                        href="/admin#administrate/datasourcesconfigurator">
                                                        <div>
                                                            <i className="fas fa-xs fa-table mr-1"></i>
                                                            {this.state.labels.datasourcesconfigurator}
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
                                            <DropdownItem>
                                                <NavItem active={this.state.menu=='import_atc'}>
                                                    <NavLink active={this.state.menu=='import_atc'}
                                                                href="/admin#administrate/import_atc">
                                                    <div>
                                                        <i className="fas fa-file-import mr-1"></i>
                                                        {this.state.labels.importATC}
                                                    </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                                <NavItem active={this.state.menu=='importwf'}>
                                                    <NavLink active={this.state.menu=='importwf'}
                                                                href="/admin#administrate/importwf">
                                                    <div>
                                                        <i className="fas fa-file-import mr-1"></i>
                                                        {this.state.labels.exchangeconfig}
                                                    </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                                <NavItem active={this.state.menu=='import_messages'}>
                                                    <NavLink active={this.state.menu=='import_messages'}
                                                                href="/admin#administrate/import_messages">
                                                    <div>
                                                        <i className="fas fa-file-import mr-1"></i>
                                                        {this.state.labels.importLocales}
                                                    </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                        </DropdownMenu>
                                        </UncontrolledDropdown>
                                <UncontrolledDropdown nav inNavbar>
                                <DropdownToggle nav caret>
                                            {this.state.labels.systemsetting}
                                        </DropdownToggle>
                                        <DropdownMenu right>
                                        
                                            <DropdownItem>
                                                <NavItem active={this.state.menu=='actuator'}>
                                                <NavLink active={this.state.menu=='actuator'}
                                                        href="/admin#administrate/actuator">
                                                    <div>
                                                    <i className="fas fa-file-import mr-1"></i>
                                                        {this.state.labels.actuatorMonitoring}
                                                    </div>
                                                </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            
                                        </DropdownMenu>
                                </UncontrolledDropdown>
                                <UncontrolledDropdown nav inNavbar>
                                <DropdownToggle nav caret >
                                            {this.state.labels.global_help}
                                        </DropdownToggle>
                                        <DropdownMenu right>
                                            <DropdownItem>
                                                <NavItem active={this.state.menu=='helpframe'}>
                                                    <NavLink
                                                        onClick={()=>{
                                                            window.open("/admin#administrate/helpframe","_blank")
                                                        }}
                                                    >
                                                        <div>
                                                            <i className="fas fa-question-circle mr-1"></i>
                                                                {this.state.labels.global_help}
                                                        </div>
                                                    </NavLink>
                                                </NavItem>
                                            </DropdownItem>
                                            <DropdownItem>
                                                <NavItem>
                                                    <NavLink>
                                                        <div
                                                            onClick={()=>{
                                                                let community_site_addr="https://openrims.org"
                                                                if(this.state.labels.community_site_addr.startsWith("https://")){
                                                                    community_site_addr=this.state.labels.community_site_addr
                                                                }
                                                                window.open(community_site_addr,'_blank').focus()
                                                            }}
                                                            >
                                                            <i className="fas fa-users-cog mr-1"></i>
                                                                {this.state.labels.community_label}
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
                <Row style={{height:'100%'}}>
                    <Col>
                        {this.currentComponent()}
                    </Col>
                </Row>
            </Container>
        )
    }
}
export default Administrate
