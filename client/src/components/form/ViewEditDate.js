import React , {Component} from 'react'
import PropTypes from 'prop-types'
import FieldDate from './FieldDate'
import FieldDisplay from'./FieldDisplay'

/**
 * View or edit date
 * Require from the caller component this.state.data[attribute] and this.state.labels[attribute] as a valid label
 * Require FormFieldDTO object for this.state.data[attribute]
 * @example
 * <ViewEditDate attribute='firstName' component={this} edit /> will be editable
 * <ViewEdit attribute='firstName' component={this} />      will not be editable
 * <ViewEdit attribute='firstName' data={this.state.data.literals} component={this} />     data based on this.state.data.literals
 */
class ViewEditDate extends Component{

    constructor(props){
        super(props)
    }

    render(){
        let data = this.props.component.state.data
        if(this.props.data != undefined){
            data=this.props.data
        }
        if(this.props.edit){
            return <FieldDate data={data} attribute={this.props.attribute} component={this.props.component} />
        }else{
            return <FieldDisplay data={data} mode='date' attribute={this.props.attribute} component={this.props.component} nolabel={this.props.nolabel}/>
        }
    }

}
export default ViewEditDate
ViewEditDate.propTypes={
    attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
    component   :PropTypes.object.isRequired,                       //caller component
    data:   PropTypes.object,                                       //alternative of component.state.data
    edit:   PropTypes.bool                                          //view or edit
}
