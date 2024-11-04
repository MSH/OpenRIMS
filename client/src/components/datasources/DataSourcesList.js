import React,{useState, useRef, useEffect } from 'react'
import {Container, Row, Col, Button} from 'reactstrap' 
import PropTypes from 'prop-types'
import Pharmadex from '../Pharmadex';
import Fetchers from '../utils/Fetchers';
import Locales from '../utils/Locales';
import Navigator from '../utils/Navigator';
import CollectorTable from '../utils/CollectorTable';

/**
 * List of the stored data sources
 */
function DataSourceslist({recipient}){
    let identifier = useRef(Date.now().toString()+Math.random())          //Who am I in messages identifier.current
    const[labels,setLabels] = useState({
        datasources:'',
        global_add:'',
        global_suspend:'',
        duplicate:'',

    })                                //all labels
    const[data, setData] = useState({
        dataSources:{
            selections:[]
        }
    });                                  //data from the server and/or properties
    
    let dataSourceID = useRef(-1)       //selected data source ID
    
    function handleMessages(event){
        let eventData=event.data
        if(eventData.subject=='dataSourceSaved'){
            dataSourceID.current=-1
            loadData()
        }
        if(eventData.to==identifier.current){
            //TODO handle messages from next level
        }
    }

    /**
     * fetch data from the server, resolve labels, etc.
     * The common implementation that may be improved
     */
    function loadData(){
        Fetchers.postJSON("/api/admin/datasources/list", {}, (query,result)=>{
            let newLabels=Locales.createLabelsFunctional(result,labels)
            Locales.resolveLabelsFunctional(newLabels,setLabels)
            if(result.valid){
                setData(result)
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
            }
        })
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
     * Reload the list when the dataSourceID is changed
     */
    useEffect(()=>{
        loadData()
    },[])

    useEffect(()=>{
        let selected=data.dataSources.selections
        if(selected.length==1){
            Navigator.message(identifier.current, recipient,"onSelectDataSource", {dataSourceID:selected[0]})
        }else{
            Navigator.message(identifier.current, recipient,"onSelectDataSource", {dataSourceID:dataSourceID.current})
        }
    },[data.dataSources.selections, dataSourceID])
    
    if(labels.locale== undefined){
        return Pharmadex.wait()
    }else{
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <h4>{labels.datasources}</h4>
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6' className="d-flex justify-content-end">
                        <Button className='m-1' color="primary"
                            onClick={()=>{
                                dataSourceID.current=0
                                let newTable=CollectorTable.deselectAll(data.dataSources)
                                setData({
                                    ...data,
                                    dataSources:newTable
                                })
                            }}
                        >{labels.global_add}</Button>
                        
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <CollectorTable
                            tableData={data.dataSources}
                            loader={loadData}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            selectRow={(rowNo)=>{
                                setData({...data,
                                    dataSources:CollectorTable.selectRow(rowNo, data.dataSources,true)
                                })
                            }}
                        />
                    </Col>
                </Row>
            </Container>
        )
    }

}
DataSourceslist.propTypes={
    recipient:PropTypes.string.isRequired,  //parent recipient for messages
}
export default DataSourceslist