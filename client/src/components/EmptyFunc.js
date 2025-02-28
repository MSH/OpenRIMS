import React,{useState, useRef, useEffect } from 'react'
import {Container} from 'reactstrap' 
import PropTypes from 'prop-types'
import Pharmadex from '../Pharmadex';
import Fetchers from './utils/Fetchers';
import Locales from './utils/Locales';
import Navigator from './utils/Navigator';

/**
 * Empty functional component 
 */
function EmptyFunc({recipient}){
    let identifier = useRef(Date.now().toString()+Math.random())          //Who am I in messages identifier.current
    const[labels,setLabels] = useState({})                                //all labels
    const[data, setData] = useState({});                                  //data from the server and/or properties   
    
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
        Fetchers.postJSON("/api/unknown", data, (query,result)=>{
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
    
    if(labels.locale== undefined){
        return Pharmadex.wait()
    }else{
        return(
            <Container fluid>
                
            </Container>
        )
    }

}
EmptyFunc.propTypes={
    recipient:PropTypes.string.isRequired,  //parent recipient for messages
}
export default EmptyFunc