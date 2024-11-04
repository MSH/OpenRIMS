/**
 * Variable name assistant - edit existing or introduces a new variable
 */
import React,{useState, useEffect, useRef } from 'react'
import {Container, Row, Col, Button} from 'reactstrap' 
import PropTypes from 'prop-types'
import Fetchers from '../utils/Fetchers';
import Locales from '../utils/Locales';
import Pharmadex from '../Pharmadex';
import FieldInputFunctional from '../form/FieldInputFunctional';
import Navigator from '../utils/Navigator';


/**
 * Edit  
 */
function VariableAssistantEdit({recipient, currentName}){
    let identifier = useRef(Date.now().toString()+Math.random())          //Who am I in messages identifier.current
    const[labels,setLabels] = useState({
        form_ok:'',
        global_cancel:'',
        global_help:'',
        edit_labels:'',
        assist:'',
        restore:'',
        utilization:'',
    })                              //all labels
    const[data, setData] = useState({currentName:currentName});  //VariableAssistantDTO.java   

    useEffect(()=>{
        Fetchers.postJSON("/api/admin/variable/assistant/edit", data, (query,result)=>{
            let labels1 = Locales.createLabelsFunctional(result,labels)
            let labelsAll=Locales.createLabelsFunctional(result.labels,labels1)
            Locales.resolveLabelsFunctional(labelsAll, setLabels)
            setData(result)
        })
        
    },[])

    /**
     * Set satate for "labels" fields 
     */
    function setLabelsFields(cloneLabels){
        setData({
            ...data,
            labels:cloneLabels
        })
    }

    function setVarNameField(value){
        let fieldClone={...data.varName, value:value}
        setData({
            ...data,
            varName:fieldClone
        })
    }

    /**
     * Place label values
     */
    function placeLabels(){
        let ret=[]
        for (const property in data.labels) {
            let prop=data.labels[property]
            if(typeof prop == 'object'){
                ret.push(<FieldInputFunctional key={property} mode='text' attribute={property} 
                    data={data.labels} labels={labels} setData={setLabelsFields} />)
            }
        }
        return ret  
    }
    function capitalizeFirstLetter(string, toUpper) {
        let ch=string.charAt(0).toUpperCase()
        if(!toUpper){
            ch=string.charAt(0).toLowerCase()
        }
        return ch + string.slice(1);
    }

    /**
     * Create the name of a variable from US English definition
     */
    function assist(){
        let assist=''
        for(const property in data.labels){
            if(property.toUpperCase()=='EN_US'){
                let words=data.labels[property].value.split(' ')
                if(Fetchers.isGoodArray(words)){
                    words.forEach((word) => {
                        assist=assist+capitalizeFirstLetter(word.trim(),true)
                    });
                }
                setVarNameField(capitalizeFirstLetter(assist,false))
                break;
            }
        }
    }
    /**
     * Validate and Save the variable name. Save labels. Pass the variable name as a field value and close this tab
     */
    function saveAndClose(){
        Fetchers.postJSON("/api/admin/variable/assistant/edit/save", data,(query,result)=>{
            if(result.valid){
                Navigator.message(identifier.current,recipient,'VARIABLE', result.varName.value, window.opener)
                window.close()
                window.opener.focus()
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', 
                    {mess:result.identifier, color:'danger'})
            }
        })
    }
    
    if(labels.locale== undefined){
        return Pharmadex.wait()
    }
    return(
        <Container fluid>
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                <Row>
                                    <Col>
                                        <h5>{labels.edit_labels}</h5>
                                    </Col>
                                </Row>
                                {placeLabels()}
                            </Col>
                        </Row>
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col xs='12' sm='12' lg='7' xl='7'>
                                <FieldInputFunctional  mode='text' attribute='varName' data={data} labels={labels} setData={setData}/>
                            </Col>
                            <Col xs='12' sm='12' lg='5' xl='5'>
                                <Button
                                    onClick={()=>{
                                        assist()
                                    }}
                                    color='success'
                                >
                                {labels.assist}
                                </Button>
                                {' '}
                                <Button
                                    onClick={()=>{
                                        setVarNameField(data.currentName)
                                    }}
                                    color='warning'
                                >
                                {labels.restore}
                                </Button>
                                {' '}
                                <Button
                                onClick={()=>{
                                   saveAndClose()
                                }}
                                color='primary'
                                >
                                {labels.form_ok}
                                </Button>
                                
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <small>{labels.utilization + ": " + data.usageCount}</small>
                            </Col>
                        </Row>
                </Col>
            </Row>
        </Container>
    )

}
VariableAssistantEdit.propTypes={
    recipient:PropTypes.string.isRequired,        //for messages
    currentName:PropTypes.string,                 //current name of the variable
}
export default VariableAssistantEdit