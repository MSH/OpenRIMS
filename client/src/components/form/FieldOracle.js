import React , {Component} from 'react'
import {FormGroup, Input, Label, Popover, PopoverBody, PopoverHeader, Row, Col, Button, FormFeedback, FormText} from 'reactstrap'
import PropTypes from 'prop-types'
import Fetchers from '../utils/Fetchers'
import { isUndefined } from 'util'

/**
 * Oracle - type field, i.e. incremental search
 * Should be used instead OptionField in cases when options more then 10
 * @example
 * <FieldOracle attribute="supplier" component={this} api="/api/common/suppliers" 
 * addlabel={this.state.labels.addsupplier}
 * selectlabel={this.state.labels.orselectbelow}
 * />
 */
class FieldOracle extends Component{
    constructor(props){
        super(props)
        this.state={
            popoverOpened:false,
            popoverDescrOpened:false,
            originalOptions:[],
            searchString:"",                //visible and typable name of...
            originalString:"",              //original name of...
            page:1,
            pages:1,
        }

        this.onSearchChange=this.onSearchChange.bind(this)
        this.loadOptions=this.loadOptions.bind(this)
        this.createOptions=this.createOptions.bind(this)
        this.refreshInternal=this.refreshInternal.bind(this)
        this.togglePopover=this.togglePopover.bind(this)
        this.popoverHeader=this.popoverHeader.bind(this)
        this.toggleDescrPopover=this.toggleDescrPopover.bind(this)
        this.showButton=this.showButton.bind(this)
        this.pager=this.pager.bind(this)
        this.isStrict=this.isStrict.bind(this)
        this.isValid=this.isValid.bind(this)

        this.refreshInternal()
    }
    /**
     * Refresh internal variables
     */
    refreshInternal(){
        this.component = this.props.component
        this.attribute = this.props.attribute
        this.data=this.component.state.data

    }


       /**
     * Search string has been changed. Maybe it's a time to ask for new optuions or remove existing ones
     * It depends on length of the search string > 2 search, <2 remove
     * ask the server for a options list
     */
    onSearchChange(e){
        let value = e.target.value
        if(value.length>=2){  //TRESHOLD TRESHOLD TRESHOLD
            this.loadOptions(value);
        }else{
           this.state.originalOptions=[]
           this.state.popoverOpened=false
           this.setState(this.state)
           this.data[this.attribute]={
               value:{
               id:0,
               code:"",
               description:"",
               options:[]
                },
                warning:"",
                error:""
            }
           this.component.setState(this.data)
        }
        this.state.searchString=value
        this.setState(this.state)
    }
    
    /**
     * 
     * @param {string} searchString 
     */
    loadOptions(searchString){
        let askFor={    //pseudo OptionDTO
            code:searchString,
        }
        Fetchers.postJSONNoSpinner(this.props.api, askFor,(query,option)=>{
            let s = this.state
            s.originalOptions=option.options
            s.page=1
            s.pages=Math.round(s.originalOptions.length / 3)
            if(s.pages==0){
                s.pages=1
            }
            s.popoverOpened=s.pages>0 || s.searchString.length>0
            this.setState(s)
        })
    }
    /**
     * Create options from DTO
     */
    createOptions(){
        let options=[]
        if(Fetchers.isGoodArray(this.state.originalOptions)){
            let i=0
            this.state.originalOptions.forEach(element => {
                options.push(
                    <p key={i} className="m-0 p-0">
                      <Button style={{whiteSpace:'normal'}} color="link" size="sm" onClick={()=>{this.onSelect(element)}} role="button">{element.code} </Button>
                    </p>
                    );
                    i++
                }
                )
        }
        let start=(this.state.page-1) * 3
        let end = start+3
        if(end>=this.state.originalOptions.length){
            end=this.state.originalOptions.length
        }
        return options.slice(start,end)
    }
    /**
     * User select an option
     * @param {OptionDTO} option 
     */
    onSelect(option){
        if(!isUndefined(option)){
            this.state.searchString=option.code
            this.state.popoverOpened=false;
            this.setState(this.state)
            option.options=[]
            this.data[this.attribute].value=option
            this.component.setState(this.data)
        }
    }
    /**
     * Toggele popover and restore value in search string if popover has been closed - nothing selected
     */
    togglePopover(){
        this.state.popoverOpened=!this.state.popoverOpened
        this.state.searchString=this.data[this.attribute].value.code
        this.setState(this.state)
    }
    /**
     * Create a popover header
     */
    popoverHeader(){
        if(this.state.originalString == this.state.searchString){
            return []
        }else{
            let selectIt=  <Label size="sm">
                                {this.props.selectlabel}
                            </Label>
            if(this.state.originalOptions.length==0){
                selectIt=[]
            }
            return(
            <PopoverHeader>
                <Label size="sm" className="p-0">
                    {this.props.addlabel}
                </Label>
                    <p className="m-0 p-0">
                    <Button style={{whiteSpace:'normal'}} color="link" size="sm"
                        onClick={()=>{
                            let element={
                                id:0,
                                code:this.state.searchString
                            }
                            let s = this.state
                            s.originalOptions=[]
                            s.popoverOpened=false
                            this.setState(s)
                            this.data[this.attribute].value=element
                            this.component.setState(this.data)
                        }}
                    >
                        {this.state.searchString} </Button>
                    </p>
                <Label size="sm" className="p-0">
                    {selectIt}
                </Label>
            </PopoverHeader>
            )
        }
    }
    /**
     * the pager at the bottom
     */
    pager(){
        let ret=[]
        if(this.state.page>1){
            ret.push(<Col key="1" className="d-flex justify-content-start" xs="4" sm="4" lg="4" xl="4">
                        <Button color="link" size="sm" onClick={()=>{
                            if(this.state.page>1){
                                let s = this.state
                                s.page--
                                this.setState(s)
                            }
                        }}>
                            <i className="fa fa-angle-double-left fa-lg"></i>
                        </Button>
                    </Col>)
        }else{
            ret.push(<Col key="1" xs="4" sm="4" lg="4" xl="4"></Col>)
        }
        ret.push( <Col key="2" className="d-flex justify-content-center" xs="4" sm="4" lg="4" xl="4">
                        {this.state.page +"/"+this.state.pages}                                    
                    </Col>)
        if(this.state.page<this.state.pages){
            ret.push(
                <Col key="3" className="d-flex justify-content-end" xs="4" sm="4" lg="4" xl="4">
                    <Button style={{whiteSpace:'normal'}} color="link" size="sm"  onClick={()=>{
                            if(this.state.page<this.state.pages){
                                let s = this.state
                                s.page++
                                this.setState(s)
                            }
                            }}>
                        <i className="fa fa-angle-double-right fa-lg"></i>
                    </Button>
                </Col>
            )
        }else{
            ret.push(<Col key="3" xs="4" sm="4" lg="4" xl="4"></Col>)
        }
        return ret
    }

    toggleDescrPopover(){
        this.state.popoverDescrOpened=!this.state.popoverDescrOpened
        this.setState(this.state)
    }

    /**
     * Do we have the description?
     */
    showButton(){
        let d = this.data[this.attribute].description
        if(typeof d == 'undefined' || d == null || d.length==0){
            return[]
        }else{
            return(
            <div>
                <Button size='sm' id={this.attribute+'bdescr'} outline>       
                    <i className="fa fa-question" style={{fontSize:'16px'}}/>
                </Button>
                <Popover
                isOpen={this.state.popoverDescrOpened}
                target={this.attribute+'bdescr'} 
                toggle={this.toggleDescrPopover}
                trigger="legacy">
                    <PopoverBody style={{fontSize:"0.7rem"}}>
                        {d}
                    </PopoverBody>
                </Popover>
            </div>
            )
        }
    }

       /**
     * Is this field  valid?
     */
    isValid(){
        let component = this.props.component
        let key = this.props.attribute
        return !component.state.data[key].error
    }

    /**
     * Is this check preliminary or final
     */
    isStrict(){
        let component = this.props.component
        let key = this.props.attribute
        return component.state.data[key].strict
    }
    render(){
        this.refreshInternal()
        if(!isUndefined(this.data) && !isUndefined(this.data[this.attribute]) && !isUndefined(this.data[this.attribute].value)){
            let key=this.attribute
            //check changes in parent form
            if(this.data[key].value.code != this.state.originalString){
                this.state.originalString=this.data[key].value.code
                this.state.searchString=this.data[key].value.code
            }
            component.state.data[key].justloaded=false
            return(
                <FormGroup>
                    <Label for={key}>
                        {this.component.state.labels[key]}
                    </Label>
                    <Row>
                        <Col xs='11' sm='11' lg='11' xl='11' className='pr-0'>
                        <Input autoComplete='off'
                            value={this.state.searchString} id={key} 
                            onChange={(e)=>{this.onSearchChange(e)}}
                            bsSize="sm"
                            valid={this.isValid() && this.isStrict()}
                            invalid={(!this.isValid()) && this.isStrict()}/>
                        <FormFeedback valid={false}>{this.data[this.attribute].suggest}</FormFeedback>
                        <FormText hidden={this.isStrict() || this.isValid()}>{this.data[this.attribute].suggest}</FormText>
                        </Col>
                        <Col xs='1' sm='1' lg='1' xl='1' className='p-0 m-0'>
                            {this.showButton()}
                        </Col>
                    </Row>
                    <Popover
                        placement="bottom"
                        isOpen={this.state.popoverOpened}
                        target={key} 
                        toggle={this.togglePopover}
                        trigger="legacy">
                        {this.popoverHeader()}
                        <PopoverBody>
                            <Row style={{marginLeft:'0rem', marginRight:'0rem', marginTop:'0rem', borderBottom: '0.01rem solid gray'}}>
                                <Col>
                                    {this.createOptions()}
                                </Col>
                            </Row>
                            <Row >
                                {this.pager()}
                            </Row>
                        </PopoverBody>
                    </Popover>
                </FormGroup>
            )
        }else{
            return []
        }
    }


}
export default FieldOracle
FieldOracle.propTypes={
    attribute:PropTypes.string.isRequired,   //name of a OptionDTO type attribute
    component:PropTypes.object.isRequired,  //caller component
    api:PropTypes.string.isRequired,         //api to load options should accept OptionDTO as parameter
    addlabel:PropTypes.string.isRequired,    // ask to add a new element
    selectlabel:PropTypes.string.isRequired //ask to select existing one
    
}