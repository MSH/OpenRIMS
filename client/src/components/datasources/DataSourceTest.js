import React,{useState, useRef, useEffect} from 'react'
import {Container, Row, Col} from 'reactstrap' 
import Pharmadex from '../Pharmadex';
import Fetchers from '../utils/Fetchers';
import Locales from '../utils/Locales';
import Navigator from '../utils/Navigator';
import CollectorTable from '../utils/CollectorTable';

/**
 * Shows random 1000 records received from the database using the data source's select execution
 * The DataSourceDTO originates from the browser's loacal storage  
 */
function DataSourceTest(){
    let identifier = useRef(Date.now().toString()+Math.random())          //Who am I in messages identifier.current
    const[labels,setLabels] = useState({
        Test:'',
    })                                //all labels
    const[data, setData] = useState(Fetchers.readLocaly("DataSourceDTO",
        {
            url:'-------------------------',
            sql:'',
        }
    ));                                                                     //DataSourcedto from the local storage
    
    function handleMessages(event){
        let eventData=event.data
        if(eventData.to==identifier.current){
            //TODO handle messages from next level
        }
    }

    /**
     * fetch data from the server, resolve labels, etc.
     * The common implementation that may be improved
     */
    function loadData(){
        Fetchers.postJSON("/api/admin/datasources/source/sql/test", data, (query,result)=>{
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
     * load the table
     */
    useEffect(()=>{
        loadData()
    },[])

    if(labels.locale== undefined){
        return Pharmadex.wait()
    }

    return(
        <Container fluid>
            <Row>
                <Col>
                    <h3>{labels.Test+" SQL - "+data.url.value}</h3>
                </Col>
            </Row>
            <Row>
                <Col>
                <CollectorTable
                            tableData={data.testSql}
                            loader={loadData}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                    if(header=='Lang'){
                                        return {width:'5%'}
                                    }
                                }
                            }
                        />
                </Col>
            </Row>
        </Container>
    )


}

export default DataSourceTest