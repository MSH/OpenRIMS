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
        if(value.length>this.props.table.searchTreshold){
            this.props.table.generalSearch=value
            this.applyFilters(value);
        }else if(this.props.table.generalSearch.length > value.length){//clean up filters, user remove chars
                this.props.table.generalSearch=value
                this.applyFilters(value);
            
        }else{
            this.props.table.generalSearch=value
            this.state.commonFilter = value
        }
        this.setState(this.state)
    }
    /**
     * apply search
     * @param {string} value - search value
     */
    applyFilters(value){
        let headers=this.props.table.headers;
        headers.page=1; //on second page maybe no results
        if(Fetchers.isGoodArray(headers.headers)){
            headers.headers.forEach(header => {
                if(header.filterAllowed){
                        if(value.length>this.props.table.searchTreshold){
                            header.generalCondition=value
                        }else{
                            header.generalCondition=""
                        }
                }
            });
        }
        this.props.loader()
    }

    render(){
        return(
            <Input className="form-control" bsSize="sm" placeholder={this.props.label} 
                                    type="text" value={this.props.table.generalSearch}
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