import React , {Component} from 'react'
import {Input, FormGroup, Label, FormText, FormFeedback, Row, Col} from 'reactstrap'
import URLButtons from './URLButtons'
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
        this.identifier=Date.now().toString()+this.props.attribute

        this.ensureText=this.ensureText.bind(this)
        this.isValid=this.isValid.bind(this)
        this.isStrict=this.isStrict.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
        this.fieldData=this.fieldData.bind(this)
        this.onChange=this.onChange.bind(this)
    }
   /**
     * Listen messages from the assistant
     * @param {Window Event} event 
     */
   eventProcessor(event){
        let eventData=event.data
        let data = this.fieldData()             //get FieldDTO object
        let assistant=data.assistant
        if(eventData.subject==assistant){
            if(eventData.to==this.identifier){
                data.value=eventData.data
                this.props.component.setState(this.props.component.state)
            }
        }
    }   

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
    }
    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Place the assistant
     */
    assist(assistant, description){
        let ret=""
        let component=this.props.component
        let key = this.props.attribute
        let data=this.fieldData()
        if(assistant != 'NO'){
            ret=<URLButtons assistant={assistant} value={this.ensureText(data.value)} recipient={this.identifier} title={component.state.labels[key]}/>
        }
        return ret
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
            if(this.props.mode=='number'){
                return '0'
            }else{
                return ""
            }
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
    /**
     * Calculate field's data (FieldDTO object)
     */
    fieldData(){
        let key = this.props.attribute
        let data=this.props.component.state.data[key]
        if(this.props.data != undefined){
            data=this.props.data[key]
        }
        return data
    }
    /**
     * OnChange processor
     */
    onChange(value,data, component){
        if(!this.props.functional){
            data.value=value
            component.setState(component.state)
        }else{
            component.setValue(value)
        }
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
        let data=this.fieldData()
        let lbl=component.state.labels[key]
        if(this.props.showkey){
            lbl=key
        }
        let assistCol=0
        if(data.assistant!='NO'){
            assistCol=1
        }
        if(component.state.data !=undefined && data != undefined){
            data.justloaded=false
            return(
                <Row>
                    <Col xs='12' sm='12' lg='12' xl={12-assistCol}>
                        <FormGroup>
                            <Label for={key}>
                                {lbl}
                            </Label>
                            <Input disabled={disabled} bsSize='sm' type={this.props.mode} id={key} lang={component.state.labels.locale.replace("_","-")} step="1" rows={rows}
                                value={this.ensureText(data.value)}
                                onChange={(e)=>{
                                   this.onChange(e.target.value,data, component)
                                }}
                                valid={this.isValid(data) && this.isStrict(data)}
                                invalid={!this.isValid(data) && this.isStrict(data)}/>
                            <FormFeedback valid={false}>{data.suggest}</FormFeedback>
                            <FormText hidden={this.isStrict(data) || this.isValid(data)}>{data.suggest}</FormText>
                        </FormGroup>
                    </Col>
                    <Col xs='12' sm='12' lg='12' xl={assistCol}>
                        {this.assist(data.assistant, data.description)}
                    </Col>
                </Row>
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
    data:PropTypes.object,                                           //data source, default component.state.data
    functional:PropTypes.bool,                                       //caller is functional component
}