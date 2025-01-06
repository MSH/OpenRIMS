import React,{useState, useEffect } from 'react'
import PropTypes from 'prop-types'
import FieldDisplay from './FieldDisplay'

/**
 * Functional edition of FieldDisplay
 * ~~~
 * mode: PropTypes.oneOf(['text','textarea','number']).isRequired, //type of data
    rows: PropTypes.string,                                         //rows in textarea
    attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
    labels:PropTypes.object.isRequired,                             //labels defined
    data:PropTypes.object.isRequired,                               //data in which the field defined in useState
    hideEmpty   :PropTypes.bool,                                     //hide empty fields
 * ~~~
    @example
     <FieldInputFunctional  mode='text' attribute='varName' data={data} labels={labels} hideEmpty/>
 */
function FieldDisplayFunctional({mode,rows,attribute, labels, data, hideEmpty}){
    let component={
        state:{
            data:data,
            labels:labels
        }
    }
    let hideEmptyIt=false
    if(hideEmpty != undefined){
        hideEmptyIt=hideEmpty
    }
    return (
        <FieldDisplay mode={mode} rows={rows} attribute={attribute} component={component} hideEmptyIt/> 
    )
}

FieldDisplayFunctional.propTypes={
    mode: PropTypes.oneOf(['text','textarea','number']).isRequired, //type of data
    rows: PropTypes.string,                                         //rows in textarea
    attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
    labels:PropTypes.object.isRequired,                             //labels defined
    data:PropTypes.object.isRequired,                               //data in which the field defined in useState
    hideEmpty   :PropTypes.bool,                                     //hide empty fields
}

export default FieldDisplayFunctional