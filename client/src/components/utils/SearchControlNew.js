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
class SearchControlNew extends Component{
    constructor(props){
        super(props)
        this.state={
            commonFilter:this.props.table.generalSearch
        }
        this.searchText=this.searchText.bind(this)
        this.applyFilters=this.applyFilters.bind(this)
    }

   /**
     * set all text filters in headers and run them
     * more then 3 chars should be
     * @param {event} event 
     */
    searchText(event){
        let value = event.target.value;
        if(value.length > this.props.table.searchTreshold){// ввели более 2х символов
            this.state.commonFilter = value
            this.applyFilters();
        }else if(this.state.commonFilter.length > value.length){//clean up filters, user remove chars
            this.state.commonFilter = value
            this.applyFilters();
        }else{
            this.state.commonFilter = value
        }
        
        this.setState(this.state)
    }

    applyFilters(){
        let headers=this.props.table.headers;
        headers.page=1; //on second page maybe no results
        if(Fetchers.isGoodArray(headers.headers)){
            headers.headers.forEach(header => {
                if(header.filterAllowed){
                    header.generalCondition=this.state.commonFilter
                }
            });
        }
        this.props.table.generalSearch = this.state.commonFilter
        this.props.loader()
    }

    render(){
        return(
            <Input className="form-control" bsSize="sm" placeholder={this.props.label} 
                                    type="text" 
                                    value={this.state.commonFilter}
                                    disabled={this.props.disabled}
                                    onChange={(e)=>this.searchText(e)}/>
        )
    }

}
export default SearchControlNew

SearchControlNew.propTypes={
    label:PropTypes.string.isRequired,
    table:PropTypes.object.isRequired,
    loader:PropTypes.func.isRequired,
    disabled:PropTypes.bool
}