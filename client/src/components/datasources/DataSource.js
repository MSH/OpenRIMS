import React,{useState, useRef, useEffect } from 'react'
import {Container, Row, Col, Breadcrumb, BreadcrumbItem} from 'reactstrap' 
import PropTypes from 'prop-types'
import Pharmadex from '../Pharmadex';
import Fetchers from '../utils/Fetchers';
import Locales from '../utils/Locales';
import Navigator from '../utils/Navigator';
import ViewEditFunctional from '../form/ViewEditFunctional';
import CollectorTable from '../utils/CollectorTable';
import DataSourceDetails from './DataSourceDetails';
import Downloader from '../utils/Downloader';

/**
 * Configure catalogue data source 
 */
function DataSource({recipient, dataSourceID}){
    let identifier = useRef(Date.now().toString()+Math.random())          //Who am I in messages identifier.current
    const[labels,setLabels] = useState({
        editdatasource:'',
        home:'',
        saved:'',
    }) 
    //data from the server loaded using the load function                               
    const[data, setData] = useState({
        dataSourceID:dataSourceID,
    }); 

    //label of the data type of detailed data selected from the dataTypes table by a user
    const[sourceLabel, setSourceLabel]= useState("") 
    
    // details or main screen
    const[detailScreen, setDetailScreen] = useState(false)
    
    //data type of detailed data selected from the dataTypes table by a user
    let source=useRef("")

    function handleMessages(event){
        let eventData=event.data
        if(eventData.from==recipient){
            if(eventData.subject=='saveDataSource'){
                Fetchers.postJSON("/api/admin/datasources/source/save",data, (query,result)=>{
                    if(result.valid){
                        Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.url.value + " "+ labels.saved, color:'success'})
                        Navigator.message(identifier.current, "*",'dataSourceSaved',{})
                    }else{
                        Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                    }
                    setData(result)
                })
            }
            if(eventData.subject=='sqlDataSource'){
                let dl=new Downloader()
                dl.postDownload('/api/admin/datasources/source/sql', data, "datasource_sql.txt")
            }
            if(eventData.subject=='sqlDataSourceTest'){
                Fetchers.writeLocaly('DataSourceDTO', data)
                window.open('/admin#datasourcetest/','_blank')
            }
        }
        if(eventData.to==identifier.current){
            if(eventData.subject=='onDataSourceDetails'){
                setSourceLabel(eventData.data.sourceLabel)
                let filtersNew=structuredClone(data.filters)
                let fieldsNew=structuredClone(data.fields)
                filtersNew[source.current]=eventData.data.filters
                fieldsNew[source.current]=eventData.data.fields
                setData({...data,
                    filters:filtersNew,
                    fields:fieldsNew,
                })
            }
        }
    }

    /**
     * fetch data from the server, resolve labels, etc.
     * The common implementation that may be improved
     */
    function loadData(){
        Fetchers.postJSON("/api/admin/datasources/source/load", {...data, dataSourceID:dataSourceID}, (query,result)=>{
            let newLabels=Locales.createLabelsFunctional(result,labels)
            Locales.resolveLabelsFunctional(newLabels,setLabels)
            if(result.valid){
                setData(result)
                setDetailScreen(false)
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
     * load/reload
     */
    useEffect(()=>{
        loadData()
    },[dataSourceID])

    /**
     * reload counters in the table when details has been changed to false
     */
    useEffect(()=>{
        if(!detailScreen){
            Fetchers.postJSON("/api/admin/datasources/source/counters", data, (query,result)=>{
                if(result.valid){
                    setData(result)
                }else{
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                }
            })
        }

    },[detailScreen])
    
    /**
     * build a breadcrumb
     */
    function breadcrumbItems(){
        let ret = []
        let className="btn btn-link p-0 border-0"
        //first item is Home It is active when the source is selected by a user
        ret.push(
            <BreadcrumbItem key="bci0">
                <div hidden={sourceLabel.length==0} className={className}
                    onClick={()=>{
                        setSourceLabel("")
                        setDetailScreen(false)
                    }}
                >
                    {labels.home}
                </div>
                <span hidden={sourceLabel.length>0}>
                    {labels.home}
                </span>
                </BreadcrumbItem>
        )
        if(sourceLabel.length>0){
            ret.push(
                <BreadcrumbItem key="bci1">
                    <span>
                        {sourceLabel}
                    </span>
                </BreadcrumbItem>
            )
        }
        
        return ret;
    }

    /**
     * empty, general, details
     */
    function content(){
        if(detailScreen){
            return(
                <DataSourceDetails recipient={identifier.current} 
                                    source={source.current}
                                    filters={data.filters[source.current]}
                                    fields={data.fields[source.current]}
                />
            )
        }
        return (
            <>
            <Row>
                <Col xs='12' sm='12' lg='12' xl='12'>
                    <Row>
                        <Col>
                            <ViewEditFunctional  mode='text' attribute='url' data={data} labels={labels} setData={setData}/>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEditFunctional  mode='textarea' attribute='description' data={data} labels={labels} setData={setData} edit/>
                        </Col>
                    </Row>
                </Col>
            </Row>
            <Row>
                <Col>
                    <CollectorTable
                        tableData={data.dataTypes}
                        loader={loadData}
                        headBackground={Pharmadex.settings.tableHeaderBackground}
                        linkProcessor={(rowNo,col)=>{
                            source.current=col.originalValue
                            setDetailScreen(true)
                        }}
                    />
                </Col>
            </Row>
        </>
        )
    }
    
    if(labels.locale== undefined){
        return Pharmadex.wait()         //waiting for data source loading
    }
    if(dataSourceID<0){
        return []           //data source is not selected by a user
    }
    return(
        <Container fluid>
            <Row>   {/*header */}
                <Col xs='12' sm='12' lg='12'xl='12'>
                    <h4>{labels.editdatasource}</h4>
                </Col>
            </Row>
            <Row hidden={data.dataSourceID<0}>
                <Col>
                <Breadcrumb>
                    {breadcrumbItems()}
                </Breadcrumb>
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

DataSource.propTypes={
    recipient:PropTypes.string.isRequired,  //parent recipient for messages
    dataSourceID:PropTypes.number.isRequired, //data source ID
}
export default DataSource