import React , {Component} from 'react'
import {FormGroup, Label} from 'reactstrap'
import PropTypes from 'prop-types'
import CalendarPicker from './CalendarPicker'

/**
 * Date input
 * Require from the caller component this.state.data[attribute] as date value, 
 * Require complex FormFieldDTO object for this.state.data[attribute]
 * The component should has this.state.labels.locale as a valid language tag (i.e. en-US, not en_us) and this.state.labels[attribute] as a valid label
 * @example
 * <FieldDate attribute='requested_date' component={this} />
 */
class FieldDate extends Component{

    constructor(props){
        super(props)
        this.isValid=this.isValid.bind(this)
        this.isStrict=this.isStrict.bind(this)
        this.data=this.data.bind(this)
    }

    /**
     * Where the data
     */
    data(){
        let data=this.props.component.state.data
        if(this.props.data!=undefined){
            data=this.props.data
        }
        return data
    }

    /**
     * Is this field  valid?
     */
    isValid(){
        let key = this.props.attribute
        return !this.data()[key].error
    }

    /**
     * Is this check preliminary or final
     */
    isStrict(){
        let key = this.props.attribute
        return this.data()[key].strict
    }
    
    render(){
        let component = this.props.component
        let key = this.props.attribute
        let data=this.data()
        if(data != undefined && data[key] != undefined){
        data[key].justloaded=false
        return(
            <FormGroup>
                <Label for={key}>
                    {component.state.labels[key]}
                </Label>
                <CalendarPicker bsSize="sm" id={key} locale={component.state.labels.locale.replace("_","-")}
                valid={this.isValid()}
                strict={this.isStrict()}
                suggest={data[key].suggest}
                value={data[key].value}
                onChange={(value)=>{
                    if(value instanceof Date){
                        //let dateTimeStr = value.toISOString();
                        //let dateTime = dateTimeStr.split("T")
                        let month = "0"+ (value.getMonth()+1)
                        let day= "0"+ value.getDate()
                        let dateTime = value.getFullYear()+"-"+month.substr(-2)+"-"+day.substr(-2);
                        let s = component.state
                        data[key].value=dateTime
                        component.setState(s)
                    }else{
                        if(value==null){
                            let s = component.state
                            data[key].value=null
                            s.popoverOpened=false
                            component.setState(s) 
                        }
                    }
                }}
                />
            </FormGroup>
        )
        }else{
            return []
        }
    }


}
export default FieldDate
FieldDate.propTypes={
    attribute  :PropTypes.string.isRequired,                        //name of a OptionDTO text or number attribute
    component   :PropTypes.object.isRequired                        //caller component
}