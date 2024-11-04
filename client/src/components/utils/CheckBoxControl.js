import React,{useState, useEffect } from 'react'
import {FormGroup, Input,Label} from 'reactstrap' 
import PropTypes from 'prop-types'
import Navigator from './Navigator';

/**
 * A slightly advanced checkbox implementation
 * issue an event 'onCheckBox' when a user clicks on the checkbox
 */
function CheckBoxControl({recipient, identifier, label, value}){
                               
    const[checked, setChecked] = useState(value)

    useEffect(()=>{
        Navigator.message(identifier, recipient,'onCheckBox',checked)
    },[checked])

    return(
        <FormGroup check className="form-control-sm">
            <Label check>
                <Input 
                    type="checkbox"
                    value={checked}
                    checked={checked}
                    onChange={()=>{
                        setChecked(!checked)
                    }} 
                />
                {label}
            </Label>
        </FormGroup>
    )

}
CheckBoxControl.propTypes={
    recipient:PropTypes.string.isRequired,  //parent recipient for messages
    identifier:PropTypes.string.isRequired, //an identifier for messaging
    label:PropTypes.string.isRequired,      //label for a checkbox
    value:PropTypes.bool.isRequired,        //value of the checkbox
}
export default CheckBoxControl