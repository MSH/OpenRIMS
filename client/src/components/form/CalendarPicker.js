import React , {Component} from 'react'
import {Input, Row, Col, ButtonGroup, Button,Popover,PopoverBody, FormFeedback, FormText} from 'reactstrap'
import PropTypes from 'prop-types'
import Calendar from 'react-calendar'
import 'react-calendar/dist/Calendar.css'

/**
 * Calendar picker based on react-calendar
 * Just copy it
 */
class CalendarPicker extends Component{
    constructor(props){
        super(props)
        this.state={
            popoverOpened:false,
            labels:{
            }
        }
        this.togglePopover=this.togglePopover.bind(this)
        this.ensureDate=this.ensureDate.bind(this)
        this.dateToStringWithExceptions=this.dateToStringWithExceptions.bind(this)
    }

    togglePopover(){
        let s = this.state
        s.popoverOpened= !s.popoverOpened
        this.setState(s)
    }

    /**
     * convert value to date anyway
     * @param {string} value 
     * @returns 
     */
    ensureDate(value){
        if(typeof value == 'undefined'){
            return new Date()
        }
        if(value==null){
            return new Date
        }
        if(value instanceof Date){
            return value
        }else{
            if(typeof value == 'string'){
                let ret = new Date(value);
                if(!isNaN(ret)){
                    return ret;
                }else{
                    return new Date
                }
            }else{
                return new Date
            }
        }
    }
    /**
     * Convert date to string using locale
     * For ill-defined locales, like ne-NP, use ISO date
     * @param {date type} value 
     */
    dateToStringWithExceptions(value){
        if(this.props.locale=='ne-NP'){
            return value.toISOString().substr(0,10)+' A.D.'
        }
        return value.toLocaleDateString(this.props.locale, {dateStyle: 'medium'})
    }

    /**
     * Convert date to locale string or empty string
     */
    ensureDateAsString(value){
        if(typeof value == 'undefined'){
            return new Date
        }
        if(value instanceof Date){
            return  this.dateToStringWithExceptions(value)
        }else{
            if(typeof value == 'string'){
                let ret = new Date(value);
                if(!isNaN(ret)){
                    return  this.dateToStringWithExceptions(ret)
                }else{
                    return ""
                }
            }else{
                return ""
            }
        }
    }


    render(){
        let type=this.props.type
        if(type.length==0){
            type='month'
        }
        return(
            <div>
            <Row>
                <Col xs='9' sm='9' lg='9' xl='9' className='pr-0'>
                <Input type='text' bsSize={this.props.bsSize} id={this.props.id}
                value={this.ensureDateAsString(this.props.value)}
                onClick={this.togglePopover}
                readOnly
                valid={this.props.valid && this.props.strict}
                invalid={(!this.props.valid) && this.props.strict}
                />
                <FormFeedback valid={false}>{this.props.suggest}</FormFeedback>
                <FormText hidden={this.props.strict || this.props.valid}>{this.props.suggest}</FormText>
                </Col>
                <Col xs='3' sm='3' lg='3' xl='3' className='p-0'>
                    <ButtonGroup>
                        <Button size='sm' id={this.props.id+'button'} outline
                            onClick={this.togglePopover}> 
                                <i className="fa fa-calendar" style={{fontSize:'16px'}}/>
                        </Button>
                        <Button size='sm' id={this.props.id+'button'} outline
                            onClick={()=>this.props.onChange(null)}> 
                                <i className="fa fa-eraser" style={{fontSize:'16px'}}/>
                        </Button>
                    </ButtonGroup>
                   
                </Col>
            </Row>
            <Popover
                isOpen={this.state.popoverOpened}
                target={this.props.id+'button'} 
                toggle={this.togglePopover}
                trigger="legacy">
                    <PopoverBody style={{fontSize:"0.7rem"}}>
                        <Calendar
                            locale={this.props.locale}
                            value={this.ensureDate(this.props.value)}
                            onChange={(e)=>{
                                this.togglePopover()
                                this.props.onChange(e)
                            }}
                            defaultView={type}
                        />
                    </PopoverBody>
                </Popover>
            </div>
        )
    }


}
export default CalendarPicker
CalendarPicker.propTypes={
    bsSize:PropTypes.string.isRequired,
    id:PropTypes.string,
    locale:PropTypes.string.isRequired, //actually the language tag
    value:PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.instanceOf(Date)
      ]),
    onChange:PropTypes.func.isRequired,
    valid:PropTypes.bool.isRequired,        //valid or not
    strict:PropTypes.bool.isRequired,       //is "valid" above final?
    suggest:PropTypes.string.isRequired     //suggest to this field
}