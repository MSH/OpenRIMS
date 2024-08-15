/**
 * Variable name assistant - edit existing or introduces a new variable
 */
import React,{useState, useEffect } from 'react'
import {Container, Row, Col} from 'reactstrap' 
import PropTypes from 'prop-types'
import ButtonUni from '../form/ButtonUni';
import Fetchers from '../utils/Fetchers';
import Locales from '../utils/Locales';
import Pharmadex from '../Pharmadex';
import FieldInputFunctional from '../form/FieldInputFunctional';


/**
 * Edit  
 */
function VariableAssistantEdit({recipient, currentName}){
    const[identifier,setIdentifier] = useState(Date.now().toString())   //Who am I in messages
    const[labels,setLabels] = useState({
        form_ok:'',
        global_cancel:'',
        global_help:'',
        variable_assistant_edit:'',
    })                              //all labels
    const[data, setData] = useState({currentName:currentName});  //VariableAssistantDTO.java             
    useEffect(()=>{
        Fetchers.postJSON("/api/admin/variable/assistant/edit", data, (query,result)=>{
            let labelsAll = Locales.createLabelsFunctional(result,labels)
            Locales.resolveLabelsFunctional(labelsAll, setLabels)
            setData(result)
        })
        
    },[])
    
    if(labels.locale== undefined){
        return Pharmadex.wait()
    }
    return(
        <Container fluid>
            <Row>
                <Col xs='12' sm='12' lg='9' xl='9'>
                    <h4>{labels.variable_assistant_edit}</h4>
                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'>
                    <ButtonUni
                        onClick={()=>{

                        }}
                        label={labels.form_ok}
                        color='primary'
                    />
                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'>
                     <ButtonUni
                        onClick={()=>{

                        }}
                        label={labels.global_cancel}
                        color='secondary'
                        outline
                    />
                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'>
                    <ButtonUni
                        onClick={()=>{

                        }}
                        label={labels.global_help}
                        color='info'
                    />
                </Col>
            </Row>
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                <FieldInputFunctional  mode='text' attribute='langLocal' data={data} labels={labels} setData={setData}/>
                            </Col>
                        </Row>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                <FieldInputFunctional  mode='text' attribute='langEnUS' data={data} labels={labels} setData={setData}/>
                            </Col>
                        </Row>
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'>
                        <FieldInputFunctional  mode='text' attribute='varName' data={data} labels={labels} setData={setData}/>
                </Col>
            </Row>
        </Container>
    )

}
VariableAssistant.propTypes={
    recipient:PropTypes.string.isRequired,  //for messages
    currentName:PropTypes.string,                 //current name of the variable
}
export default VariableAssistant