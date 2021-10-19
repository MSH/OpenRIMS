import React , {Component} from 'react'
import {Input, FormGroup, Label, FormText, FormFeedback} from 'reactstrap'
import PropTypes from 'prop-types'

/**
 * Free hand input - text, textarea, numbers
 * Require from the caller component this.state.data[attribute] and this.state.labels[attribute] as a valid label
 * Require FormFieldDTO object for this.state.data[attribute]
 * @example
 * <FieldInput mode='text' attribute='firstName' component={this}} />
 *  <FieldInput mode='text' attribute='firstName' component={this}} data={this.state.data.literals}/>
 * <FieldInput mode='textarea' attribute='notes' rows='12' component={this}} />
 */
class FieldInput extends Component{
    constructor(props){
        super(props)
        this.ensureText=this.ensureText.bind(this)
        this.isValid=this.isValid.bind(this)
        this.isStrict=this.isStrict.bind(this)
    }

    /**
     * Ensure valid string value
     * @param {string} value 
     */
    ensureText(value){
        if(typeof value == 'undefined'){
            return ""
        }
        if(value==null){
            return ""
        }
        if(typeof value == 'string'){
            return value
        }
        if(typeof value=='number'){
            return value
        }
        return "";
    }
    /**
     * Is this field  valid?
     */
    isValid(data){
        return !data.error
    }

    /**
     * Is this check preliminary or final
     */
    isStrict(data){
        return data.strict
    }

    render(){
        let component = this.props.component
        let key = this.props.attribute
        let rows=this.props.rows
        if(rows==undefined){
            rows=4
        }
        let disabled=false
        if(this.props.disabled){
            disabled=true
        }
        let data=component.state.data[key]
        if(this.props.data != undefined){
            data=this.props.data[key]
        }
        let lbl=component.state.labels[key]
        if(this.props.showkey){
            lbl=key
        }
        if(component.state.data !=undefined && data != undefined){
            data.justloaded=false
            return(
                <FormGroup>
                <Label for={key}>
                    {lbl}
                </Label>
                <Input disabled={disabled} bsSize='sm' type={this.props.mode} id={key} lang={component.state.labels.locale.replace("_","-")} step="1" rows={rows}
                    value={this.ensureText(data.value)}
                    onChange={(e)=>{
                        //let s = component.state
                        //s.data[key].value=e.target.value
                        data.value=e.target.value
                        component.setState(component.state)
                    }}
                    valid={this.isValid(data) && this.isStrict(data)}
                    invalid={!this.isValid(data) && this.isStrict(data)}/>
                <FormFeedback valid={false}>{data.suggest}</FormFeedback>
                <FormText hidden={this.isStrict(data) || this.isValid(data)}>{data.suggest}</FormText>
            </FormGroup>
            )
        }else{
            return []
        }
    }


}
export default FieldInput
FieldInput.propTypes={
    disabled:PropTypes.bool,
    mode: PropTypes.oneOf(['text','textarea','number']).isRequired, //type of data
    rows: PropTypes.string,                                         //rows in textarea
    attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
    component   :PropTypes.object.isRequired,                        //caller component
    data:PropTypes.object                                           //data source, default component.state.data
}