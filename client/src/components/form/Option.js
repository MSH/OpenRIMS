import React , {Component} from 'react'
import {Input, Row, Col, Button, Popover, PopoverBody, FormFeedback, FormText} from 'reactstrap'
import PropTypes from 'prop-types'
import Fetchers from '../utils/Fetchers'

/**
 * Component to edit OptionDTO
 */
class Option extends Component{
    constructor(props){
        super(props)
        this.state={
            popoverOpened:false,
        }
        this.options=this.options.bind(this)
        this.ensureOption=this.ensureOption.bind(this)
        this.ensureSelection=this.ensureSelection.bind(this)
        this.togglePopover=this.togglePopover.bind(this)
        this.showButton=this.showButton.bind(this)
    }
    /**
     * create options ftom DTO
     */
    options(){
        let opts = [];
       // let emptyOption = <option key='-1' value='-1'>-</option>
       // opts.push(emptyOption)
        if(Fetchers.isGoodArray(this.props.value.options)){
            this.props.value.options.forEach(element => {
                opts.push(<option key={element.id+''} value={element.id+''}>{element.code}</option>)
            });
        }
        return opts

    }
    /**
     * Ensure the right "empty" value
     * @param {string} value 
     */
    ensureOption(value){
        if(typeof value == 'undefined'){
            return '-1'
        }
        if(value==null){
            return '-1'
        }
        if(value==0){
            return '-1'
        }
        return value
    }
    /**
     * Ensure right option for this optionValue
     * @param {string} optonValue 
     */
    ensureSelection(optionValue){
        let ret={
            id:0,
            code:'',
            description:'',
            options:this.props.value.options
        }
        if(optionValue != '-1' && Fetchers.isGoodArray(this.props.value.options)){
            this.props.value.options.forEach((element)=>{
                if(element.id == optionValue){
                    ret = Object.assign({}, element)            //To avoid circular references
                    ret.options=this.props.value.options
                }
            })
        }
        return ret;
    }

    togglePopover(){
        let s = this.state
        s.popoverOpened= !s.popoverOpened
        this.setState(s)
    }
    /**
     * Do we have the description?
     */
    showButton(){
        let d = this.props.value.description
        if(typeof d == 'undefined' || d == null || d.length==0){
            return[]
        }else{
            return(
            <Col xs='3' sm='3' lg='3' xl='3' className='p-0'>
                <Button size='sm' id={this.props.id+'button'} outline>       
                    <i className="fa fa-question" style={{fontSize:'16px'}}/>
                </Button>
                <Popover
                isOpen={this.state.popoverOpened}
                target={this.props.id+"button"} 
                toggle={this.togglePopover}
                trigger="legacy">
                    <PopoverBody style={{fontSize:"0.7rem"}}>
                        {this.props.value.description}
                    </PopoverBody>
                </Popover>
            </Col>
            )
        }
    }

    render(){
        if(typeof this.props.value != 'undefined'){
            const btn = this.showButton()
            let large="9"
            if(btn.length==0){
                large="12"
            }
            return(
                <Row className="ml-1 mr-1">
                    <Col xs={large} sm={large} lg={large} xl={large} className='pr-0'>
                        <Input type="select" bsSize="sm"
                        value={this.ensureOption(this.props.value.id)}
                        onChange={(e)=>{
                            let ret = this.ensureSelection(e.target.value)
                            this.props.onChange(ret)
                        }}
                        valid={this.props.valid && this.props.strict}
                        invalid={(!this.props.valid) && this.props.strict}
                        >
                            {this.options()}
                        </Input>
                        <FormFeedback valid={false}>{this.props.suggest}</FormFeedback>
                        <FormText hidden={this.props.strict || this.props.valid}>{this.props.suggest}</FormText>
                    </Col>
                    {btn}
                </Row>
            )
        }else{
            return []
        }
    }


}
export default Option
Option.propTypes={
    id:PropTypes.string.isRequired,                             // input element ID
    bsSize:PropTypes.oneOf(["sm",'default',"lg"]).isRequired,   // input element size
    value:PropTypes.object,                                     //option object or undefined
    onChange:PropTypes.func.isRequired,                          //returns the selected one
    valid:PropTypes.bool.isRequired,                            // is value valid
    strict:PropTypes.bool.isRequired,                           // is it final check
    suggest:PropTypes.string.isRequired                         // suggest for field input
}