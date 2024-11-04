import React , {Component} from 'react'
import PropTypes from 'prop-types'
import FieldInput from './FieldInput'
import FieldDisplay from'./FieldDisplay'

/**
 * ~~~~
 * mode: PropTypes.oneOf(['text','textarea','number']).isRequired,
    attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
    component   :PropTypes.object.isRequired,                        //caller component
    data:PropTypes.object,                                           //data source where FormFieldDTO can be get by name. The default is component.state.data
    edit:   PropTypes.bool,                                           //view or edit
    hideEmpty : PropTypes.bool,                                      //hide empty in display mode
~~~~
 * @example
 * <ViewEdit mode='text' attribute='firstName' component={this} edit /> will be editable
 * <ViewEdit mode='text' attribute='firstName' component={this} />      will not be editable
 */
class ViewEdit extends Component{

    constructor(props){
        super(props)
    }



    render(){
        let data=this.props.component.state.data
        if(this.props.data != undefined){
            data=this.props.data
        }
        if(this.props.edit){
            return <FieldInput mode={this.props.mode} attribute={this.props.attribute} component={this.props.component} data={data} functional={this.props.functional}/>
        }else{
            return <FieldDisplay mode={this.props.mode} attribute={this.props.attribute} component={this.props.component} 
                        data={data} hideEmpty={this.props.hideEmpty} nolabel={this.props.nolabel} functional={this.props.functional}/>
        }
    }

}
export default ViewEdit
ViewEdit.propTypes={
    mode: PropTypes.oneOf(['text','textarea','number']).isRequired,
    attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
    component   :PropTypes.object.isRequired,                        //caller component
    data:PropTypes.object,                                           //data source where FormFieldDTO can be get by name. The default is component.state.data
    edit:   PropTypes.bool,                                           //view or edit
    hideEmpty : PropTypes.bool,                                      //hide empty in display mode
}
