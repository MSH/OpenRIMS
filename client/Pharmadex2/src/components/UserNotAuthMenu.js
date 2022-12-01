import React , {Component} from 'react'
import {
  Collapse,
  Navbar,
  NavbarToggler,
  NavbarBrand,
  Nav,
  NavItem,
  NavLink
} from 'reactstrap'
import Locales from './utils/Locales'
import Languages from './Languages'
import Fetchers from './utils/Fetchers'


/**
 * Main menu for a non-authorized user
 */
class UserNotAuthMenu extends Component{
    constructor(props){
        super(props)
        this.state={
            labels:{
                imguest:"",
                quickstart:"",
                login:"",
                logout:""
            },
            isOpen:false
        }
    }
    componentDidMount(){
        Locales.resolveLabels(this)
    }


    render(){
        if(this.state.labels.imguest.length==0){
            return []
        }
        return(
        <Navbar dark expand="md">
        <NavbarToggler onClick={()=>{this.state.isOpen=!this.state.isOpen; this.setState(this.state)}} className="me-2" />
        <Collapse isOpen={this.state.isOpen} navbar>
          <Nav className="me-auto" navbar>
            <NavItem>
              <NavLink href="/login">{this.state.labels.quickstart}</NavLink>
            </NavItem>
            <NavItem>
              <NavLink href="/form/login">{this.state.labels.login}</NavLink>
            </NavItem>
            <NavItem>
              <NavLink onClick={()=>{Fetchers.logout()}}><span style={{cursor:'pointer'}}>{this.state.labels.logout}</span></NavLink>
            </NavItem>
            <NavItem>
                <Languages />
            </NavItem>
          </Nav>
        </Collapse>
      </Navbar>
        )
    }


}
export default UserNotAuthMenu