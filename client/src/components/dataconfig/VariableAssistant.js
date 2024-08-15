/**
 * Variable name assistant
 */
import React,{useState, useEffect, useRef} from 'react'
import {Container, Row, Col} from 'reactstrap' 
import PropTypes from 'prop-types'
import ButtonUni from '../form/ButtonUni';
import Fetchers from '../utils/Fetchers';
import Locales from '../utils/Locales';
import Pharmadex from '../Pharmadex';
import CollectorTable from '../utils/CollectorTable';


/**
 * Variable Assistant 
 */
function VariableAssistant({recipient, currentName}){
    let identifier = useRef(Date.now().toString()+Math.random())          //Who am I in messages identifier.current
    const[labels,setLabels] = useState({
        form_ok:'',
        global_cancel:'',
        global_help:'',
        variable_assistant:'',
        label_edit:'',
        global_add:'',
    })                              
    const[data, setData] = useState({currentName:currentName, existing:'false'});  //data from the server 

    function load(){
        Fetchers.postJSON("/api/admin/variable/assistant/load", data, (query,result)=>{
            let labelsAll = Locales.createLabelsFunctional(result,labels)
            Locales.resolveLabelsFunctional(labelsAll, setLabels)
            setData(result)
        })
    }

    useEffect(()=>{
        load()
    },[currentName])
    
    if(labels.locale== undefined){
        return Pharmadex.wait()
    }
    return(
        <Container fluid>
            <Row className='mb-5'>
                <Col xs='12' sm='12' lg='9' xl='9'>
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
                <Col>
                    <Row>
                        <Col xs='12' sm='12' lg='9' xl='9'/>
                        <Col xs='12' sm='12' lg='1' xl='1'>
                            <ButtonUni
                                onClick={()=>{

                                }}
                                label={labels.label_edit}
                                color='primary'
                            />
                        </Col>
                        <Col xs='12' sm='12' lg='1' xl='1'>
                            <ButtonUni
                                onClick={()=>{

                                }}
                                label={labels.global_add}
                                color='success'
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable
                                tableData={data.table}
                                loader={load}
                                selectRow={(rowNumber)=>{
                                    data.currentName=data.table.rows[rowNumber].row[0].value
                                    load()
                                }}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                            />
                        </Col>
                    </Row>
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