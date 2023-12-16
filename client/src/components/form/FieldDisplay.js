import React , {Component} from 'react'
import {FormGroup, Label, Container, FormText,Row,Col} from 'reactstrap'
import PropTypes from 'prop-types'
import URLButtons from './URLButtons'

/**
 * Display field data - text, number, date, time, boolean
 * ~~~~
 * mode: PropTypes.oneOf(['text','textarea','number', 'date', 'boolean','time']).isRequired,
    attribute  :PropTypes.string.isRequired,                        //name of a OptionDTO text or number attribute
    hideEmpty   :PropTypes.bool,                                     //hide empty fields
    component   :PropTypes.object.isRequired,                        //caller component
    data:PropTypes.object,                                           //data source where FormFieldDTO can be get by name. The default is component.state.data
 * ~~~~
 * @example
 * <FieldDisplay data={this.state.data.literals} mode='text' attribute='firstName' component={this} hideEmpty=false} />
 */
class FieldDisplay extends Component{
    constructor(props){
        super(props)
        this.identifier=Date.now().toString()+this.props.attribute
        
        this.prepareText=this.prepareText.bind(this)
        this.notEmptyText=this.notEmptyText.bind(this)
        this.hideIt=this.hideIt.bind(this)
        this.notEmptyLabel=this.notEmptyLabel.bind(this)
        this.assist=this.assist.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
        this.fieldData=this.fieldData.bind(this)
        this.simpleText=this.simpleText.bind(this)
    }
   /**
     * Listen messages from the assistant
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let eventData=event.data
        let data = this.fieldData()[this.props.attribute]             //get FieldDTO object
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
     * hide an empty display only attribute
     */
    hideIt(){
        if(this.props.hideEmpty){
            let data = this.props.component.state.data
            if(this.props.data != undefined){
                data=this.props.data
            }
            let value = data[this.props.attribute].value
            if(value != undefined){
                if(value.code==undefined){
                    return !this.props.edit && value.length==0
                }else{
                    return !this.props.edit && value.code.length==0
                }
            }else{
                return true
            }
        }else{
            return false
        }
    }

    /**
     * REturn nbsp for empty strings
     * @param {string} value 
     */
    notEmptyText(value){
        if(value.length==0){
            return <span>&nbsp;</span>
        }else{
            return value
        }
    }
    simpleText(value){
        let ret = this.prepareText()
        if(typeof ret === 'object'){
            return ''
        }else{
            return ret
        }
    }
    /**
     * Ensure text, prepare right format for numbers, dates
     * @param {string} value 
     */
    prepareText(){
        /* let data = this.props.component.state.data
        if(this.props.data != undefined){
            data=this.props.data
        } */
        let data=this.fieldData()
        let value = data[this.props.attribute].value    //not formatted
        let valueStr=data[this.props.attribute].valueStr   //pre-formatted, if one
        if(this.props.mode=='text' || this.props.mode=='textarea'){
            //may be option or real text
            //if(isNullOrUndefined(value)){
            if(value==null || value==undefined){
                return <span>&nbsp;</span>
            }
            if(value.code == undefined){
                return this.notEmptyText(value)
            }else{
                return this.notEmptyText(value.code)
            }

        }

        if(this.props.mode=='number'){
                if(value!='0'){
                    return value;
                }else{
                    return'0'
                }
        //    }
        }

        if(this.props.mode=='date'){
            if(valueStr.length>1){
                return valueStr
            }
            if(!isNaN(Date.parse(value))){
                let dateValue= new Date(value)
                let locale=this.props.component.state.labels.locale
                let lang = locale.replace("_","-")
                return dateValue.toLocaleString(lang, {dateStyle:'medium'})
            }else{
                return value
            }
        }

        if(this.props.mode=='time'){
            if(!isNaN(Date.parse(value))){
                let dateValue= new Date(value)
                return dateValue.toLocaleString(this.props.component.state.labels.locale.replace("_","-"), {dateStyle:'short',timeStyle:'short'})
            }else{
                return value
            }
        }

        if(this.props.mode=='boolean'){
            if(value){
                return (
                     <i className="fa fa-check text-success" aria-hidden="true"></i>
                )
            }else{
                return <span>&nbsp;</span>
            }
        }

    }

    notEmptyLabel(){
        let component=this.props.component
        let key=this.props.attribute
        let text = component.state.labels[key]
        if(text == undefined){
            return []
        }
        if(this.props.nolabel){
            return []
        }
        if(text.length>0){
            return(
                <Label for={key+"display"} >
                    {component.state.labels[key]}
                </Label>  
            )
        }else{
            return[]
        }
    }
    /**
     * Place the assistant
     */
    assist(assistant, description){
        let ret=""
        let component=this.props.component
        let key = this.props.attribute
        if(assistant != 'NO'){
            ret=<URLButtons assistant={assistant} value={this.simpleText()} recipient={this.identifier} title={component.state.labels[key]}/>
        }
        return ret
    }
   /**
     * Calculate field's data (FieldDTO object)
     */
   fieldData(){
        let data=this.props.component.state.data
        if(this.props.data != undefined){
            data=this.props.data
        }
        return data
    }
    render(){
        if(this.hideIt()){
            return []
        }
       /*  let data = this.props.component.state.data
        if(this.props.data != undefined){
            data=this.props.data
        } */
        let data=this.fieldData()
        let text = this.prepareText()
        let assistCol=0
        if(data.assistant!='NO'){
            assistCol=1
        }
        if(text != 'undefined'){
            return(
            <Row>
                <Col xs='12' sm='12' lg='12' xl='11'>
                    <FormGroup>
                        {this.notEmptyLabel()}     
                        <Container fluid style={{fontSize:'0.8rem'}}>
                        {text}
                            <FormText color="danger">{data[this.props.attribute].suggest}</FormText>
                        </Container>
                    </FormGroup>
                </Col>
                <Col xs='12' sm='12' lg='12' xl={assistCol}>
                    {this.assist(data[this.props.attribute].assistant, data[this.props.attribute].description)}
                </Col>
            </Row>
            )
        }else{
            return []
        }
    }

}
export default FieldDisplay
FieldDisplay.propTypes={
    mode: PropTypes.oneOf(['text','textarea','number', 'date', 'boolean','time']).isRequired,
    attribute  :PropTypes.string.isRequired,                        //name of a OptionDTO text or number attribute
    hideEmpty   :PropTypes.bool,                                     //hide empty fields
    component   :PropTypes.object.isRequired,                        //caller component
    data:PropTypes.object,                                           //data source where FormFieldDTO can be get by name. The default is component.state.data
}