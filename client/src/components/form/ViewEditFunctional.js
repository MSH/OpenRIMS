import React,{useState, useEffect } from 'react'
import PropTypes from 'prop-types'
import ViewEdit from './ViewEdit'
/**
 * Fubctional edition of ViewEdit
 * ~~~
    mode: PropTypes.oneOf(['text','textarea','number']).isRequired, //type of data
    rows: PropTypes.string,                                         //rows in textarea
    attribute  :PropTypes.string.isRequired,                        //name of FormFieldDTO member
    data:PropTypes.object.isRequired,                               //data in which the field defined in useState.
    setData:PropTypes.func.isRequired,                              //set data function defined in useState
    edit:PropTypes.bool,                                            //view or edit this field
    hideEmpty : PropTypes.bool,                                     //hide if empty for view
 * ~~~
    @example
    <ViewEditFunctional  mode='text' attribute='url' data={data} labels={labels} setData={setData}/>
 */
function ViewEditFunctional({mode,attribute, labels, data, setData, edit, hideEmpty}){

    let component={
        setValue:(value)=>{
            let field={...data[attribute], value:value}
            setData(
                {
                ...data,
                [attribute]:field
            })
        },
        state:{
            data:data,
            labels:labels
        }
    }
    let editIt=false
    if(editIt != undefined){
        editIt=edit
    }
    let hideEmptyIt=false
    if(hideEmpty != undefined){
        hideEmptyIt=hideEmpty
    }
    return (
        <ViewEdit mode={mode} component={component} attribute={attribute} edit={editIt} hideEmpty={hideEmptyIt} functional/>
    )
    
}

ViewEditFunctional.propTypes={
    mode: PropTypes.oneOf(['text','textarea','number']).isRequired, //type of data
    attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
    labels:PropTypes.object.isRequired,                             //labels defined
    data:PropTypes.object.isRequired,                               //data in which the field defined in useState
    setData:PropTypes.func.isRequired,                              //set data function defined in useState
    edit:PropTypes.bool,                                            //view or edit this field
    hideEmpty : PropTypes.bool,                                     //hide empty when this field is for view
}
export default ViewEditFunctional
