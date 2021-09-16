import React , {Component} from 'react'
import {Navbar, NavbarToggler, Collapse, Nav, NavItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Navigator from './utils/Navigator'
import Fetchers from './utils/Fetchers'
import ButtonUni from './form/ButtonUni'

/**
 * Paint actions for the activity as buttons
 * Click on a button sends a message runAction with string action
 * Required actionBar in this.state.data ActionBarDTO
 * @example
 * <ActionBar actions={this.state.data.actionBar.actions} sendTo={this.props.identifier}/>
 */
class ActionBar extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},
            labels:{
                global_submit:'',
                label_save:'',
            }
        }
        this.createActions=this.createActions.bind(this)
        this.resolveLabels=this.resolveLabels.bind(this)
    }
    /**
     * Resolve actions as lables
     */
    componentDidMount(){
       this.resolveLabels()
    }

    resolveLabels(){
        this.state.data=this.props.actions
        if(Fetchers.isGoodArray(this.state.data)){
            this.state.data.forEach(action => {
                this.state.labels[action]=''
            });
        }
        Locales.resolveLabels(this)
    }

    componentDidUpdate(){
        if(this.state.data.length != this.props.actions.length){
            this.state.data=this.props.actions
            this.resolveLabels()
            this.setState(this.state)
        }
    }
    /**
     * Create menu items
     */
    createActions(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data)){
            this.state.data.forEach((action,index) => {
                ret.push(
                    <NavItem key={index} className='pr-2'>
                        <ButtonUni
                            label={this.state.labels[action]}
                            onClick={()=>{
                                Navigator.message("actionbar",this.props.sendTo, "runAction", action)
                            }}
                            color="success"
                        />
                    </NavItem>
                )
            });
        }
        return ret
    }
    render(){
        if(this.state.labels.locale == undefined){
            return []
        }
        return(
            <Navbar light expand="md">
            <NavbarToggler onClick={()=>{this.state.isOpen=!this.state.isOpen; this.setState(this.state)}} className="me-2" />
                <Collapse isOpen={this.state.isOpen} navbar>
                    <Nav className="me-auto" navbar>
                        {this.createActions()}
                    </Nav>
                </Collapse>
            </Navbar>
        )
    }


}
export default ActionBar
ActionBar.propTypes={
    actions:PropTypes.array,             //ActionBarDTO.actions
    sendTo:PropTypes.string.isRequired,   //address to send messages "runAction"
}