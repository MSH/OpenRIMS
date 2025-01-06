import React,{useState, useRef, useEffect } from 'react'
import {Row, Col, Container, Label,Alert} from 'reactstrap' 
import PropTypes from 'prop-types'
import Pharmadex from './Pharmadex';
import Fetchers from './utils/Fetchers';
import Locales from './utils/Locales';
import Navigator from './utils/Navigator';
import ButtonUni from './form/ButtonUni';
import FieldDisplayFunctional from './form/FieldDisplayFunctional';
import FieldInputFunctional from './form/FieldInputFunctional';
import CollectorTable from './utils/CollectorTable';
import AsyncInform from './AsyncInform';

/**
 * Run test workflows in background
 */
function RunTestProcess({recipient, dictNode}){
    let identifier = useRef(Date.now().toString()+Math.random())          //Who am I in messages identifier.current
    const[labels,setLabels] = useState({
        createTestApplications:'',
        run:'',
        global_help:'',
        global_cancel:'',
        selectStages:'',
        success:'',
    })                                //all labels
    const[data, setData] = useState({
        node:dictNode, //DictNodeDTO                    
    });                                  //RunTestProcessDTO.java 
    const[asyncInformer, setAsyncInformer] = useState(false)  
    
    function handleMessages(event){
        let eventData=event.data
        if(eventData.to==identifier.current){
            if(eventData.subject=='OnAsyncProcessCompleted'){
                Fetchers.postJSON("/api/admin/test/process/completed", data,(query,result)=>{
                    if(result.valid){
                        Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:labels.success, color:'success'})
                    }else{
                        Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                    }
                    setData(result);
                    setAsyncInformer(false)
                })
                
            }
        }
    }
    
    /**
     * Reset event processor after each render to avoid usage of the previous data
     */
    useEffect(()=>{
        //setup the messages listener
        window.addEventListener("message",handleMessages)
        //cleanup the messages listener
        return ()=>window.removeEventListener("message",handleMessages)
    })

    /**
     * load/reload the on-screen form
     */
    function loader(){
        Fetchers.postJSON("/api/admin/test/process/load", data, (query,result)=>{
            if(result.valid){
                let newLabels = Locales.createLabelsFunctional(result,labels)
                Locales.resolveLabelsFunctional(newLabels,setLabels)
                setData(result)
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
            }
            
        })
    }

    /**
     * load labels
     */
    useEffect(()=>{
       loader()
    },[])
    
    if(labels.locale== undefined || data.application_info==undefined){
        return Pharmadex.wait()
    }else{
        if(asyncInformer){
            return(
                <AsyncInform recipient={identifier.current} loadAPI='/api/admin/test/process/progress'/>
            )
        }else{
            return(
                <Container fluid>
                    <Row>
                        <Col xs='12' sm='12' lg='6' xl='9'>
                            <h4>{labels.createTestApplications}</h4>
                        </Col>
                        <Col xs='12' sm='12' lg='2' xl='1'>
                            <ButtonUni
                                label={labels.run}
                                onClick={()=>{
                                    Fetchers.postJSON("/api/admin/test/process/run", data,(query,result)=>{
                                        if(!result.valid){
                                            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                                        }else{
                                            setAsyncInformer(true)
                                        }
                                        setData(result)
                                    })
                                }}
                                color="primary"
                            />
                        </Col>
                        <Col xs='12' sm='12' lg='2' xl='1'>
                            <ButtonUni
                                label={labels.global_help}
                                onClick={()=>{
                                
                                }}
                                color="info"
                            />
                        </Col>
                        <Col xs='12' sm='12' lg='2' xl='1'>
                            <ButtonUni
                                label={labels.global_cancel}
                                onClick={()=>{
                                    Navigator.message(identifier.current, recipient, 'onRunTestProcessClose',{})
                                }}
                                outline
                                color="secondary"
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col xs='12' sm='12' lg='6' xl='6'>
                            <Row>
                                <Col>
                                    <FieldDisplayFunctional data={data} labels={labels} mode='text' attribute='application_info'/>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <FieldDisplayFunctional data={data} labels={labels} mode='text' attribute='applicationurl'/>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <FieldDisplayFunctional data={data} labels={labels} mode='text' attribute='dataurl'/>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <FieldDisplayFunctional data={data} labels={labels} mode='text' attribute='current_applications'/>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <FieldInputFunctional data={data} setData={setData} labels={labels} mode='text' attribute='prefLabel'/>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <FieldInputFunctional data={data} setData={setData} labels={labels} mode='text' attribute='applicant_email'/>
                                </Col>
                            </Row>
                            <Row>
                                <Col xs='12' sm='12' lg='12' xl='4'>
                                    <FieldInputFunctional data={data} setData={setData} labels={labels} mode='number' attribute='repeat'/>
                                </Col>
                                <Col xs='12' sm='12' lg='12' xl='4'>
                                    <FieldInputFunctional data={data} setData={setData} labels={labels} mode='number' attribute='year'/>
                                </Col>
                                <Col xs='12' sm='12' lg='12' xl='4'>
                                    <FieldInputFunctional data={data} setData={setData} labels={labels} mode='number' attribute='daysonreview'/>
                                </Col>
                            </Row>
                        
                        </Col>
                        <Col xs='12' sm='12' lg='6' xl='6'>
                            <Row>
                                <Label>
                                    {labels.selectStages}
                                </Label>
                            </Row>
                            <Row>
                                <Col>
                            <Row hidden={data.stagesError.length==0}>
                                <Col>
                                    <Alert color='danger' className="p-0 m-0">
                                        <small>{data.stagesError}</small>
                                    </Alert>
                                </Col>
                            </Row>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <CollectorTable
                                        tableData={data.stages}
                                        loader={loader}
                                        headBackground={Pharmadex.settings.tableHeaderBackground}
                                        selectRow={(rowNo)=>{
                                            setData({...data,
                                                    stages:CollectorTable.selectRow(rowNo, data.stages,true)})
                                        }}
                                    />
                                </Col>
                            </Row>
                        </Col>
                    </Row>
                </Container>
            )
        }
    }

}
RunTestProcess.propTypes={
    recipient:PropTypes.string.isRequired,  //parent recipient for messages
    dictNode:PropTypes.object.isRequired      //node id in the dictionary.guest.applications
}
export default RunTestProcess