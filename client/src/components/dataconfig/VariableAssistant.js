/**
 * Variable name assistant
 */
import React,{useState, useEffect, useRef} from 'react'
import {Container, Row, Col, Button} from 'reactstrap' 
import PropTypes from 'prop-types'
import ButtonUni from '../form/ButtonUni';
import Fetchers from '../utils/Fetchers';
import Locales from '../utils/Locales';
import Pharmadex from '../Pharmadex';
import CollectorTable from '../utils/CollectorTable';
import SearchControl from '../utils/SearchControl';
import Navigator from '../utils/Navigator';
import VariableAssistantEdit from './VariableAssistantEdit';


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
        search:'',
        selectOrConfirmVariableName:''
    })                              
    const[data, setData] = useState({currentName:currentName, existing:'false'});  //VariableAssitantDTO
    const[forma, setForma] = useState("select")         //initial form is select from existing

    function load(){
        Fetchers.postJSON("/api/admin/variable/assistant/load", data, (query,result)=>{
            let labelsAll = Locales.createLabelsFunctional(result,labels)
            Locales.resolveLabelsFunctional(labelsAll, setLabels)
            setData(result)
        })
    }
    /**
     * an initial load
     */
    useEffect(()=>{
        load()
    },[currentName])
    
    /**
     * Provide the content - select, edit, add
     */
    function content(){
        switch (forma) {
            case 'select':
                return(
                    <>
                    <Row>
                    <Col xs='12' sm='12' lg='7' xl='7'>
                        <h4>{data.currentName}</h4>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <SearchControl
                            label={labels.search}
                            table={data.table}
                            loader={load}
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='3'>
                        <Button
                            onClick={()=>{
                                Fetchers.postJSON('/api/admin/variable/assistant/validate', data, (query,result)=>{
                                    if(result.valid){
                                        Navigator.message(identifier.current,recipient,'VARIABLE', result.currentName, window.opener)
                                        window.close()
                                        window.opener.focus()
                                    }else{
                                        Navigator.message('*', '*', 'show.alert.pharmadex.2', 
                                            {mess:result.identifier, color:'danger'})
                                    }
                                })
                            }}
                            
                            color='primary'
                        >
                            {labels.form_ok}
                        </Button>
                        {' '}
                        <Button
                            onClick={()=>{
                                setForma('edit')
                            }}
                            color='secondary'
                        >
                            {labels.label_edit} 
                        </Button>
                        {' '}
                        <Button
                            onClick={()=>{
                                setData({...data,
                                    currentName:""
                                    }
                                )
                                setForma('edit')
                            }}
                            color='success'
                        >
                            {labels.global_add} 
                        </Button>
                    </Col>
                </Row>
                <Row className='mt-3'>
                    <Col>
                    <CollectorTable
                        title={labels.selectOrConfirmVariableName}
                        tableData={data.table}
                        loader={load}
                        selectRow={(rowNumber)=>{
                            data.currentName=data.table.rows[rowNumber].row[0].value
                            load()
                        }}
                        styleCorrector={(header)=>{
                            if(header=='varname'){
                                return({width:'20%'})
                            }

                        }}
                        headBackground={Pharmadex.settings.tableHeaderBackground}
                    />
                    </Col>
                </Row>
                </>   
                )
            case 'edit':
                return (
                    <VariableAssistantEdit recipient={recipient} currentName={data.currentName} /> //recipient for messages should be same!
                )
            case 'add':
          }
    }

    if(labels.locale== undefined){
        return Pharmadex.wait()     //it is undefined yet
    }
    return(
        <Container fluid>
            <Row className='mb-5'>
                <Col xs='12' sm='12' lg='10' xl='10'>
                    <h3>{labels.variable_assistant}</h3>
                </Col>
               
                <Col xs='12' sm='12' lg='1' xl='1'>
                     <ButtonUni
                        onClick={()=>{
                                window.opener.focus()
                                window.close()
                        }}
                        label={labels.global_cancel}
                        color='secondary'
                        outline
                    />
                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'>
                    <ButtonUni
                        onClick={()=>{
                            window.open('/api/admin/variable/assistant/help','_blank').focus()
                        }}
                        label={labels.global_help}
                        color='info'
                    />
                </Col>
            </Row>
            <Row>
                <Col>
                    {content()}
                </Col>
            </Row>
        </Container>
    )

}
VariableAssistant.propTypes={
    recipient:PropTypes.string.isRequired,  //for messages
    currentName:PropTypes.string.isRequired,                 //current name of the variable

}
export default VariableAssistant