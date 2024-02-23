import React , {Component} from 'react'
import {
  Collapse,
  Navbar,
  NavbarToggler,
  Nav,
  NavItem,
  NavLink,
  NavbarBrand,
  Form
} from 'reactstrap'
import Locales from './utils/Locales'
import Languages from './Languages'
import FieldDisplay from './form/FieldDisplay'
import Fetchers from './utils/Fetchers'



/**
 * Main menu for a guest user (i.e. Google authorized)
 */
class UserGuestMenu extends Component{
    constructor(props){
        super(props)
        this.state={
            labels:{
                manageapplications:"",
                login:"",
                logout:""
            },
            isOpen:false
        }
    }
    componentDidMount(){
      Fetchers.postJSONNoSpinner("/api/public/userdata/form", this.state.data,(query,result)=>{
        this.state.data=result
        this.setState(this.state)
        Locales.createLabels(this)
        Locales.resolveLabels(this)
      })
  }


    render(){
      if(this.state.labels.userNameFld == undefined || this.state.data.userNameFld == undefined || this.state.labels.logout.length==0){
        return []
      }
      return(
        <Navbar dark expand="md">
          <NavbarToggler onClick={()=>{this.state.isOpen=!this.state.isOpen; this.setState(this.state)}} className="me-2" />
          <Collapse isOpen={this.state.isOpen} navbar>
            <Nav className="me-auto" navbar>
              <NavItem>
                <Form inline style={{color:'white'}}>
                  <FieldDisplay mode="text" attribute="userNameFld" component={this} hideEmpty={false} />
                </Form>
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
export default UserGuestMenu