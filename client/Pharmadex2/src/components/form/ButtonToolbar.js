import React , {Component} from 'react'
import {Button, Tooltip} from 'reactstrap'
import PropTypes from 'prop-types'

/**
 * The toolbar's button with font awersome glyph and tooltip
 * @example for not outline button <ButtonToolbar onClick={this.buttonAction} tooltip={this.state.labels.action} awersome="far fa-folder-open" disabled={false}/>
 */
class ButtonToolbar extends Component{
    constructor(props){
        super(props)
        this.state={
            tooltipOpen:false,
        }
    }

    render(){
        let disabled=false
        if(this.props.disabled==undefined){
            disabled=this.props.disabled
        }
        const buttonId=this.props.awersome.replace(" ", "")
        let ret = []
        ret.push(<Button outline 
                    key="1"
                    disabled={disabled}
                    onClick={this.props.onClick}
                    className='p-0 m-0 border-0'
                    id={buttonId}>
                          <i className={this.props.awersome + " p-2"}/>
                </Button>)
        ret.push(
            <Tooltip
                    key="2"
                    style={{backgroundColor:"#757E94",borderTopColor: "#757E94"}}
                    isOpen={this.state.tooltipOpen}
                    target={buttonId}
                    toggle={()=>{
                        this.state.tooltipOpen=!this.state.tooltipOpen;
                        this.setState(this.state)
                    }}
                >
                    {this.props.tooltip}
                </Tooltip>
        )
        return ret
    }


}
export default ButtonToolbar
ButtonToolbar.propTypes={
    onClick:PropTypes.func.isRequired,
    tooltip:PropTypes.string.isRequired,
    awersome:PropTypes.string.isRequired,
    disabled:PropTypes.bool
}