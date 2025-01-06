import React,{useState, useRef, useEffect } from 'react'
import {Container,Row,Col,Button} from 'reactstrap' 
import Pharmadex from '../Pharmadex';
import Fetchers from '../utils/Fetchers';
import Locales from '../utils/Locales';
import Navigator from '../utils/Navigator';
import AsyncInform from '../AsyncInform'
import DataSourceslist from './DataSourcesList';
import DataSource from './DataSource';

/**
 * Data Sources for external consumers 
 */
function DataSources(){
    let identifier = useRef(Date.now().toString()+Math.random())          //Who am I in messages identifier.current
    const[labels,setLabels] = useState({
        datasourcesconfigurator:'',
        datasources:'',
        global_renewExternal:'',
        global_help:'',
        global_cancel:'',
        starting:'',
        selectdatasource:'',
        global_save:'',
        test:'',
        sql:'',
    })                                
    const[dataSourceID, setdataSourceID]=useState(-1)                      //ID of selected data source from the data sources list           
    const[progress,setProgress] = useState(false)                         //progress bar for DWHUpdate

    function handleMessages(event){
        let eventData=event.data
        if(eventData.to==identifier.current){
            if(eventData.subject=='OnAsyncProcessCompleted'){
                setProgress(false)
            }
            if(eventData.subject=='onSelectDataSource'){
                setdataSourceID(eventData.data.dataSourceID)
            }
        }
    }

    /**
     * Init messages just after component did mount
     */
    useEffect(()=>{
        //setup the messages listener
        window.addEventListener("message",handleMessages)
        //cleanup the messages listener
        return ()=>window.removeEventListener("message",handleMessages)
    })

    useEffect(()=>{
        Locales.resolveLabelsFunctional(labels,setLabels);
    },[])

    /**
     * Re-calculate the Data Warehouse
     */
    function renewExternal(){
        Fetchers.postJSON("/api/admin/report/renewexternal", {}, (query, result)=>{
            if(!result.valid){
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'warning'})
            }else{
                setProgress(true)
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:labels.starting, color:'success'})
            }
        })
    }

    if(labels.locale== undefined){
        return Pharmadex.wait()
    }
    if(progress){    //progress bar?
        return(
            <AsyncInform recipient={identifier.current} loadAPI='/api/admin/dwh/update/progress' />
        )
    }
    return(
        <Container fluid>
            <Row>
                <Col xs='12' sm='12' lg='6'xl='6'>
                    <h3>{labels.datasourcesconfigurator}</h3>
                </Col>
                <Col xs='12' sm='12' lg='6'xl='6'>
                    <Button color="primary"
                        onClick={renewExternal}
                        outline
                    >
                        {labels.global_renewExternal}
                    </Button>{' '}
                    <Button color="warning"
                        disabled={dataSourceID<0}
                        onClick={()=>{
                            Navigator.message(identifier.current, '*', 'sqlDataSourceTest',{})
                        }}
                    >
                        {labels.test}
                    </Button>{' '}
                    <Button color="success"
                        disabled={dataSourceID<0}
                        onClick={ ()=>{
                                Navigator.message(identifier.current, '*', 'sqlDataSource',{})
                            }
                        }
                    >
                        {labels.sql}
                    </Button>{' '}
                    <Button color="primary"
                        disabled={dataSourceID<0}
                        onClick={()=>{
                            Navigator.message(identifier.current, "*", 'saveDataSource', {})
                        }}
                    >{labels.global_save}</Button>{' '}
                    <Button color="info"
                        onClick={()=>{
                            Fetchers.openWindowHelp('/api/admin/help/report/config/manual')//, labels.global_help)
                        }}
                    >{labels.global_help}</Button>{' '}
                    <Button color="secondary" outline
                        onClick={()=>Navigator.navigate("administrate")}
                    >{labels.global_cancel}</Button>
                </Col>
            </Row>
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6' className='mt-5'>
                    <DataSourceslist recipient={identifier.current} />
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6' className='mt-5'>
                    <DataSource recipient={identifier.current} dataSourceID={dataSourceID} />
                </Col>
            </Row>
        </Container>
    )
}

export default DataSources