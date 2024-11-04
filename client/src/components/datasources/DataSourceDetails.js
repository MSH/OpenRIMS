import React,{useState, useRef, useEffect } from 'react'
import {Container, Row, Col} from 'reactstrap' 
import PropTypes from 'prop-types'
import Pharmadex from '../Pharmadex';
import Fetchers from '../utils/Fetchers';
import Locales from '../utils/Locales';
import Navigator from '../utils/Navigator';
import CollectorTable from '../utils/CollectorTable';
import SearchControl from '../utils/SearchControl';
import CheckBoxControl from '../utils/CheckBoxControl';

/**
 * Filters and fields for a particular source of data - applications, classifiers, etc
 * received as properties, return in the event
 * ~~~
 *  recipient:PropTypes.string.isRequired,                  //parent recipient for messages
    source:PropTypes.string.isRequired,                     //applictions, or classifiers or...
    filtes:PropTypes.arrayOf(PropTypes.string).isRequired,  //filters selected 
    fieds:PropTypes.arrayOf(PropTypes.string).isRequired,  //fieds selected  
 * ~~~
 *  @example
 * 
 */
function DataSourceDetails({recipient, source, filters, fields}){
    let identifier = useRef(Date.now().toString()+Math.random())          //Who am I in messages identifier.current
    const[labels,setLabels] = useState({
        filters:'',
        fields:'',
        search:'',
        selectedonly:'',
    })                                //all labels
    const[data, setData] = useState({
        source:source,
        sourceLabel:'',
        filtersTable:{
            headers:{},
            selections:filters
        },
        fieldsTable:{
            headers:{},
            selections:fields
        },
        filtersSelectedOnly:false,
        fieldsSelectedOnly:false,
    });                                  //DataSourceElementDTO   
    
    function handleMessages(event){
        let eventData=event.data
        if(eventData.to==identifier.current){
           if(eventData.subject=='onCheckBox'){
                if(eventData.from=='filtersCheckbox'){
                    setData({
                        ...data,
                        filtersSelectedOnly:eventData.data
                    })
                }
                if(eventData.from=='fieldsCheckbox'){
                    setData({
                        ...data,
                        fieldsSelectedOnly:eventData.data
                    })
                }
           }
        }
    }

    /**
     * fetch data from the server, resolve labels, etc.
     * The common implementation that may be improved
     */
    function loadData(){
        Fetchers.postJSON("/api/admin/datasources/source/details", data, (query,result)=>{
            let newLabels=Locales.createLabelsFunctional(result,labels)
            Locales.resolveLabelsFunctional(newLabels,setLabels)
            if(result.valid){
                setData(result)
                Navigator.message(identifier.current, recipient, 'onDataSourceDetails', {
                    sourceLabel:result.sourceLabel,
                    filters:result.filtersTable.selections,
                    fields:result.fieldsTable.selections
                })
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                Navigator.message(identifier.current, recipient, 'onDataSourceDetails', {
                    sourceLabel:'--------------',
                    filters:result.filtersTable.selections,
                    fields:result.fieldsTable.selections
                })
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
     * reload if any parameter has been changed
     * @todo selected only as well
     */
    useEffect(()=>{
        loadData()
    },[source,data.filtersSelectedOnly, data.fieldsSelectedOnly])


    
    
    if(labels.locale== undefined){
        return Pharmadex.wait()
    }else{
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <h4>{labels.filters}</h4>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <SearchControl label={labels.search} table={data.filtersTable} loader={loadData}/>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <CheckBoxControl recipient={identifier.current} identifier='filtersCheckbox' 
                            label={labels.selectedonly} value={data.filtersSelectedOnly}  />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <CollectorTable
                            tableData={data.filtersTable}
                            loader={loadData}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            selectRow={(rowNo)=>{
                                let filtersTableNew = CollectorTable.selectRow(rowNo, data.filtersTable,false)
                                Navigator.message(identifier.current, recipient, 'onDataSourceDetails', {
                                    sourceLabel:data.sourceLabel,
                                    filters:filtersTableNew.selections,
                                    fields:data.fieldsTable.selections
                                })
                                setData({...data,
                                        filtersTable:filtersTableNew})
                            }}
                        />
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <h4>{labels.fields}</h4>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <CheckBoxControl recipient={identifier.current} identifier='fieldsCheckbox' 
                            label={labels.selectedonly} value={data.fieldsSelectedOnly}  />
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <CollectorTable
                            tableData={data.fieldsTable}
                            loader={loadData}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            selectRow={(rowNo)=>{
                                let fieldsTableNew=CollectorTable.selectRow(rowNo, data.fieldsTable,false)
                                Navigator.message(identifier.current, recipient, 'onDataSourceDetails', {
                                    sourceLabel:data.sourceLabel,
                                    filters:data.filtersTable.selections,
                                    fields:fieldsTableNew.selections
                                })
                                setData({...data,
                                        fieldsTable:fieldsTableNew})
                            }}
                        />
                    </Col>
                </Row>
            </Container>
        )
    }

}
DataSourceDetails.propTypes={
    recipient:PropTypes.string.isRequired,                  //parent recipient for messages
    source:PropTypes.string.isRequired,                     //applictions, or classifiers or...
    filters:PropTypes.array.isRequired,  //filters selected 
    fields:PropTypes.array.isRequired,  //fieds selected     
}
export default DataSourceDetails