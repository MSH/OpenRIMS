import React , {Component} from 'react'
import {Button} from 'reactstrap'
import PropTypes from 'prop-types'

/**
 * The uniform button
 * @example 
 * <ButtonUni onClick={this.buttonAction} label={this.state.labels.action} color="danger" outline={false} disabled={false}/>
 */
class ButtonUni extends Component{
    constructor(props){
        super(props)
    }

    render(){
        let outline=true
        if(this.props.outline != undefined){
            outline=this.props.outline
        }else{
            outline=this.props.outline
        }
        let disabled=false
        if(this.props.disabled != undefined){
            disabled=this.props.disabled
        }
        let color='primary'
        if(this.props.color != undefined){
            color=this.props.color
        }
        let hidden=false
        if(this.props.hidden != undefined){
            hidden = this.props.hidden
        }
        return(
            <Button className="btn-block mt-1" size="sm" outline={outline} disabled={disabled}  color={color} 
            onClick={this.props.onClick} hidden={hidden}
            >
             {this.props.label}
            </Button>
        )
    }


}
export default ButtonUni
ButtonUni.propTypes={
    onClick:PropTypes.func.isRequired,
    label:PropTypes.string.isRequired,
    color:PropTypes.string,
    outline:PropTypes.bool,
    disabled:PropTypes.bool
}