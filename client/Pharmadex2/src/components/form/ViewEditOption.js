import React , {Component} from 'react'
import PropTypes from 'prop-types'
import FieldOption from './FieldOption'
import FieldDisplay from'./FieldDisplay'

/**
 * View or edit data for options fields, i.e. choices from pre-defined set of values
 * Require from the caller component this.state.data[attribute] and this.state.labels[attribute] as a valid label
 * Require FormFieldDTO<OptionDTO> object for this.state.data[attribute]
 * @example
 * <ViewEditOption attribute='gender' component={this} data={this.props.data} edit /> will be editable
 * <ViewEditOption attribute='firstName' component={this} hideEmpty/>      will not be editable and hide when empty
 */
class ViewEditOption extends Component{

    constructor(props){
        super(props)
    }

    render(){
        if(this.props.edit){
            return <FieldOption attribute={this.props.attribute} component={this.props.component} data={this.props.data}/>
        }else{
            return <FieldDisplay mode='text' attribute={this.props.attribute} component={this.props.component} data={this.props.data} hideEmpty={this.props.hideEmpty}/>
        }
    }

}
export default ViewEditOption
ViewEditOption.propTypes={
    attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
    component   :PropTypes.object.isRequired,                       //caller component
    data:   PropTypes.object,                                       //optional place for field's data
    edit:   PropTypes.bool,                                          //view or edit
    hideEmpty: PropTypes.bool                                       //hide empty fields in display mode
}
