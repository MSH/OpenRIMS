import React , {Component} from 'react'
import {FormGroup, Label, Container, FormText} from 'reactstrap'
import PropTypes from 'prop-types'

/**
 * Display field data - text, number, date, time, boolean
 * Require from the caller component this.state.data[attribute] and this.state.labels[attribute] as a valid label
 * Require complex FormFieldDTO object for this.state.data[attribute]
 * modes are ['text','textarea','number', 'date', 'boolean','time']
 * The component should has this.state.labels.locale as a valid language tag (i.e. en-US, not en_us) and this.state.labels[attribute] as a valid label
 * @property attribute  :PropTypes.string.isRequired,                        //name of a OptionDTO text or number attribute mandatory
 * @property hideEmpty   :PropTypes.bool,                                     //hide empty fields non-mandatory
 * @property component   :PropTypes.object.isRequired                        //caller component mandatory
 * @example
 * <FieldDisplay data={this.state.data.literals} mode='text' attribute='firstName' component={this} hideEmpty=false} />
 */
class FieldDisplay extends Component{
    constructor(props){
        super(props)
        this.prepareText=this.prepareText.bind(this)
        this.notEmptyText=this.notEmptyText.bind(this)
        this.hideIt=this.hideIt.bind(this)
        this.notEmptyLabel=this.notEmptyLabel.bind(this)
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
    /**
     * Ensure text, prepare right format for numbers, dates
     * @param {string} value 
     */
    prepareText(){
        let data = this.props.component.state.data
        if(this.props.data != undefined){
            data=this.props.data
        }
        let value = data[this.props.attribute].value
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
           // if(!Number.isNaN(value)){
           //     let numVal = new Number(value)
           //     return numVal.toLocaleString(this.props.component.state.labels.locale.replace("_","-"),{useGrouping:true, minimumFractionDigits:2,maximumFractionDigits:2})
        //
        //    }else{
                if(value!='0'){
                    return value;
                }else{
                    return'0'
                }
        //    }
        }

        if(this.props.mode=='date'){
            if(!isNaN(Date.parse(value))){
                let dateValue= new Date(value)
                let locale=this.props.component.state.labels.locale
                let lang = locale.replace("_","-")
                return dateValue.toLocaleString(lang, {dateStyle:'long'})
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
    render(){
        if(this.hideIt()){
            return []
        }
        let data = this.props.component.state.data
        if(this.props.data != undefined){
            data=this.props.data
        }

        let text = this.prepareText()
        if(text != 'undefined'){
            return(
            <FormGroup>
                {this.notEmptyLabel()}     
                <Container fluid style={{fontSize:'0.8rem'}}>
                    {text}
                    <FormText color="danger">{data[this.props.attribute].suggest}</FormText>
                </Container>
            </FormGroup>
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