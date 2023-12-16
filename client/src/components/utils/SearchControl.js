import React , {Component} from 'react'
import PropTypes from 'prop-types'
import {Input} from 'reactstrap';
import Fetchers from './Fetchers'

/**
 * Search input field
 * @property label - "Search"
 * @property {Object} table
 * @property {function} loader() external function that loads data to the table
 * @property {boolean} disabled 
 * @example <SearchControl label={this.state.labels.search} table={this.data.table} loader={this.loadData} disabled={this.state.noSearch} />
 */
class SearchControl extends Component{
    constructor(props){
        super(props)
        let crit="";
        let headers =  props.table.headers.headers
        if(Fetchers.isGoodArray(headers)){
                headers.forEach(element => {
                if(element.generalCondition.length>0){
                    crit=element.generalCondition;
                }
                 });
        }
        this.state={
            commonFilter:crit
        }
        if(this.props.identifier == undefined){
            this.state.identifier=Date.now().toString() //default
        }else{
            this.state.identifier=this.props.identifier //the callar will send messages
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.searchText=this.searchText.bind(this)
        this.applyFilters=this.applyFilters.bind(this)
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
       /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            if(data.subject=='CleanUpSearchBox'){
                this.state.commonFilter=''
                this.setState(this.state)
            }
        }
    }
   /**
     * set all text filters in headers and run them
     * more then 3 chars should be
     * @param {event} event 
     */
    searchText(event){
        let value = event.target.value;
        let s = this.state
        if(value.length>this.props.table.searchTreshold){
            this.applyFilters(value);
        }else{
            if(this.state.commonFilter.length>this.props.table.searchTreshold){ //clean up filters, user remove chars
                this.applyFilters(value);
            }
        }
        s.commonFilter=value
        this.setState(s)
    }
    /**
     * apply search
     * @param {string} value - search value
     */
    applyFilters(value){
        let headers=this.props.table.headers;
        headers.page=1; //on second page maybe no results
        if(Fetchers.isGoodArray(headers.headers)){}
        headers.headers.forEach(header => {
            if(header.filterAllowed){
                    if(value.length>this.props.table.searchTreshold){
                        header.generalCondition=value
                    }else{
                        header.generalCondition=""
                    }
            }
        });
        this.props.loader()
    }

    render(){
        return(
            <Input className="form-control" bsSize="sm" placeholder={this.props.label} 
                                    type="text" value={this.state.commonFilter}
                                    disabled={this.props.disabled}
                                    onChange={(e)=>this.searchText(e)}/>
        )
    }

}
export default SearchControl

SearchControl.propTypes={
    identifier:PropTypes.string,            //address to receie and return address to send messages. Default is timestamp
    label:PropTypes.string.isRequired,
    table:PropTypes.object.isRequired,
    loader:PropTypes.func.isRequired,
    disabled:PropTypes.bool
}