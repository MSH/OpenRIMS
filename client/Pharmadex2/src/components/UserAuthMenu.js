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
import FieldOption from './form/FieldOption'
import Fetchers from './utils/Fetchers'
import FieldsComparator from './form/FieldsComparator'
import Navigator from './utils/Navigator'

/**
 * Main menu for an authorized user
 */
class UserAuthMenu extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},
            labels:{
                logout:""
            },
            isOpen:false
        }
        this.comparator = new FieldsComparator(this)
    }
    componentDidMount(){
        Fetchers.postJSONNoSpinner("/api/public/userdata/form", this.state.data,(query,result)=>{
          this.state.data=result
          this.setState(this.state.data)
          Locales.createLabels(this)
          Locales.resolveLabels(this)
          this.comparator = new FieldsComparator(this)
        })
    }
    /**
     * manage user roles changes
     */
    componentDidUpdate(){
      const fld = this.comparator.checkChanges()
      if(fld.includes("userRoleFld")){
          Fetchers.postJSONNoSpinner("/api/common/user/role/change",this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
            this.comparator = new FieldsComparator(this)
            Navigator.goHome()
          })
      }
    }

    render(){
        if(this.state.labels.userNameFld == undefined || this.state.data.userNameFld == undefined || this.state.labels.logout.length==0){
            return []
        }
        return(
        <Navbar dark expand="md">
        <NavbarToggler onClick={()=>{this.state.isOpen=!this.state.isOpen; this.setState(this.state)}} className="me-2"/>
        <Collapse isOpen={this.state.isOpen} navbar>
          <Nav className="me-auto" navbar>
            <NavItem >
              <Form inline style={{color:'white'}}>
                <FieldDisplay mode="text" attribute="userNameFld" component={this} hideEmpty={false} />
              </Form>
            </NavItem>
            <NavItem>
              <Form inline style={{color:'white'}}>
                <FieldOption attribute="userRoleFld" component={this} />
              </Form>
            </NavItem>
            <NavItem>
              <NavLink href="/logout">{this.state.labels.logout}</NavLink>
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
export default UserAuthMenu