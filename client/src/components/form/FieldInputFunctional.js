import React,{useState, useEffect } from 'react'
import PropTypes from 'prop-types'
import FieldInput from './FieldInput'

/**
 * Functional edition of FieldInput
 * ~~~
 * disabled:PropTypes.bool,
    mode: PropTypes.oneOf(['text','textarea','number']).isRequired, //type of data
    rows: PropTypes.string,                                         //rows in textarea
    attribute  :PropTypes.string.isRequired,                        //name of FormFieldDTO member
    data:PropTypes.object.isRequired,                               //data in which the field defined in useState.
    setData:PropTypes.func.isRequired,                              //set data function defined in useState
 * ~~~
    @example
     <FieldInputFunctional  mode='text' attribute='varName' data={data} labels={labels} setData={setData}/>
 */
function FieldInputFunctional({disabled, mode,rows,attribute, labels, data, setData}){
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
    let dis=false
    if(disabled != undefined){
        dis=disabled
    }
    return (
        <FieldInput disabled={dis} mode={mode} rows={rows} attribute={attribute} component={component} functional/> 
    )
}

FieldInputFunctional.propTypes={
    disabled:PropTypes.bool,
    mode: PropTypes.oneOf(['text','textarea','number']).isRequired, //type of data
    rows: PropTypes.string,                                         //rows in textarea
    attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
    labels:PropTypes.object.isRequired,                             //labels defined
    data:PropTypes.object.isRequired,                               //data in which the field defined in useState
    setData:PropTypes.func.isRequired,                              //set data function defined in useState
}

export default FieldInputFunctional