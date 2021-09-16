import React , {Component} from 'react'
import {Input, ListGroup,ListGroupItem, Popover,PopoverBody} from 'reactstrap';
import Fetchers from './Fetchers';

/**
 * Pick a value from a list. Incremental search that rebuild list each time
 * @property placeHolder - string that invites to input or selected country for edit
 * @property options {[string]} - list of options to pick
 * @property getOptions(substring) {function} - new list for selection
 * @property onChange(id) {function}
 * @property invalid {boolean} for validators
 */
class ListPicker extends Component{
    constructor(props){
        super(props)
        this.onSearchChange=this.onSearchChange.bind(this)
        this.createItems = this.createItems.bind(this)
        this.state = {
            popoverOpen: false,
            searchValue:"",
            selectedIndex:"-1",
          };
    }

    /**
     * ask for a options list
     */
    onSearchChange(e){
        let value = e.target.value
        let s = this.state
        if(value.length>2){  //TRESHOLD TRESHOLD TRESHOLD
            s.popoverOpen=true
            this.props.getOptions(value);
        }else{
            if(s.searchValue.length>2){
                s.popoverOpen=false
                value=""
                this.props.onChange(-1) //nothing selected
            }
        }
        if(typeof value != 'undefined'){
            s.searchValue=value
        }else{
            s.searchValue="";
        }
       
        this.setState(s)
    }
    /**
     * User selects a country
     * @param {event} e value id index inside options
     */
    onSelect(e){
        let s = this.state
        s.searchValue=this.props.options[e.target.value]
        this.state.popoverOpen=false
        this.props.onChange(e.target.value)
        this.setState(s)

    }
    /**
     * Create list group items
     */
    createItems(){
        let ret=[];
        if(Fetchers.isGoodArray(this.props.options)){
            let i=0
            this.props.options.forEach(element => {               
                ret.push(<ListGroupItem key={i} value={i} action 
                    onClick={(e)=>{this.onSelect(e)}}>
                    {element}
                    </ListGroupItem>);
                i++;
            });
        }
        return ret
    }
    /**
     * Get string value to display
     */
    getStringValue(){
        if(this.state.searchValue.length>0){
            return this.state.searchValue
        }else{
            return this.props.placeHolder
        }
    }
    /**
     * check mandatory properties
     */
    checkProperties(){
        if(
            typeof this.props.id == "string"
            && typeof this.props.getOptions == "function"
            && typeof this.props.options == "object"
            && typeof this.props.onChange == "function"
        ){
            return ""
        }else{
            return(
            "id " + typeof this.props.id
            +", value "+ typeof this.props.value
            +", getOptions "+ typeof this.props.getOptions
            +", options "+ typeof this.props.options
            +", onChange "+ typeof this.props.onChange
            )
        }
    }

    render(){
        let err = this.checkProperties()
        if(err.length===0){
        return(
            <div>
            <Input value={this.state.searchValue} id={this.props.id} 
                onChange={(e)=>{this.onSearchChange(e)}}
                bsSize="sm"
                placeholder={this.props.placeHolder}
                invalid={this.props.invalid}/>
            <Popover placement="right" isOpen={this.state.popoverOpen} target={this.props.id}
             style={{backgroundColor:'lightgoldenrodyellow'}} >
              <PopoverBody>
                  <ListGroup >
                      {this.createItems()}
                  </ListGroup>
              </PopoverBody>
            </Popover>
          </div>
        )
    }else{
        return err
    }
    }
}

export default ListPicker