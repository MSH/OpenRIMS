import React , {Component} from 'react'
import Spinner from './Spinner'
import Alerts from './Alerts'

/**
 * Fetch API utilities as well as any useful utilities related to local storage arrays, window urls etc. All are static
 */
class Fetchers{

    /**
     * Show Alert question, after answer YES send request
     * ansfer NO - nothing
     */
    static alerts(alert, ansYES, ansNO){
        if(alert != undefined && alert.length > 0){
            Alerts.warning(alert,
                ()=>{   //yes
                    ansYES()
                },
                ()=>{   //no
                    if(ansNO != null){
                        ansNO()
                    }
                })
        }else{

        }
    }

    



    /**
     * Post a JSON Query to server with or a spinner
     * set justloaded=true for any object loaded - deep
     * @param {String} api API URL, like "/api/countryDetails"
     * @param {Object} bodyDTO DTO that will be converted to JSON and placed to Body
     * @param {function} parser function with params api and result of query
     * @example  Fetchers.postJSON('/api/countryDetails', parseInt(countryId),(query, result)=>{ ... process result ...})
     */
    static postJSON(api,bodyDTO, parser){
        Spinner.show()
        Fetchers.postJSONNoSpinner(api,bodyDTO, parser)
    }
   /**
     * Post a JSON Query to server Do not use a Spinner!
     * set justloaded=true for any object inside the response - deep
     * @param {String} api API URL, like "/api/countryDetails"
     * @param {Object} bodyDTO DTO that will be converted to JSON and placed to Body
     * @param {function} parser function with params api and result of query
     * @example  Fetchers.postJSON('/api/countryDetails', parseInt(countryId),(query, result)=>{ ... process result ...})
     */
    static postJSONNoSpinner(api,bodyDTO, parser){
        Fetchers.setJustLoaded(bodyDTO,false)
        //let cooks = document.cookie
        fetch(Fetchers.contextPath()+api , {
            credentials: 'include',
            method: 'POST',          
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'              
            },           
            body: JSON.stringify(bodyDTO)
          })      
          .then(response => {            
            if (response.ok) { 
                let resp = response.json()
                return resp;              
            }else{
               // if(response.statusText.includes("/api/guest/thing/save/application") || response.status.includes(500)){
               //     Spinner.hide()
               // }else{
                let time=Date(Date.now()).toString()
                    Spinner.hide();
                    Alerts.show(time + "; url:" +api +"; status:" + response.status +  response.statusText,3)
               // }
            }
          }) 
          .then(res=> {
              Spinner.hide()
              if(typeof res != "undefined"){
                Fetchers.setJustLoaded(res,true)
                parser(api,res)
              }
          })
          .catch(error=> {
              Spinner.hide()
              Alerts.showSessionTimeOut("Your session has timed out. Please login again", ()=>{Fetchers.logoutTimeOut()})
            }
           ) 
    }

    static logoutTimeOut(){
        let h = window.location.hash
        window.document.cookie = "PDX2_SENDURL=" + encodeURIComponent(h);

        window.location="/logout";
        location.reload();
    }

    static logout(){
        window.document.cookie = "PDX2_SENDURL=";

        window.location.hash = ""
        window.location.href="/logout";
    }
    /**
     * For this and all objects downto sets justloaded property
     * @param {object} resp - just loaded data
     * @param {boolean} value - set or reset
     */
    static setJustLoaded(resp,value){
        if(Fetchers.isTrueObject(resp)){
            if(value){
                resp.justloaded=value
            }else{
                delete resp.justloaded
            }
            Object.keys(resp).forEach(element=>{
                if(typeof resp[element] == 'object' && (resp[element]!= undefined)){
                    if(Fetchers.isGoodArray(resp[element])){
                        let arr = resp[element]
                        arr.forEach(e=>Fetchers.setJustLoaded(e,value))
                    }else{
                        Fetchers.setJustLoaded(resp[element],value)
                    }
                }
            })
        }
    }

    /**
     * is obj is true obj
     * @param {object or come else} obj 
     */
    static isTrueObject(obj){
        return (typeof obj == 'object') && (obj != null) && (!Array.isArray(obj))
    }

    /**
     * POST a form JSON reply
     * return JSON as well
     * set justloaded=true for any object inside the response - deep
     * @param {String} api API URL, like "/api/save/file"
     * @param {FormData} formData object FormData that should be initiated and filled by form fields, files and other data that will POST to the api
     * @param {function} processor parser function with params api and result of POST, may contain any JSON object that will be processed by the processor
     * @example   
     *       formData.append('dto', JSON.stringify(this.state.data))    //whole DTO will packed to JSON and placed to the field
     *       formData.append('file', this.state.file.file);
     *       Fetchers.postForm("/api/common/upload/save/file",formData, (api,result)=>{ ... process result ...})
     */
    static postFormJson(api, formData, processor){
        Spinner.show()
        fetch(Fetchers.contextPath()+api, {
            credentials: 'include',
            method: 'POST',
            body: formData
        })
        .then(res => { 
            Spinner.hide()            
            if (res.ok) { 
                return res.json();             
            }else{
                Spinner.hide()
                Alerts.show(api +" ER " + res.status + " ER " + res.statusText,3)
            }
        }) 
        .then(res=> {
            Spinner.hide()
            if(typeof res != "undefined"){
                Fetchers.setJustLoaded(res,true)
                processor(api,res)
            }
        })
        .catch(res=>{ 
            Spinner.hide()
            Alerts.show(api +" " + res.message,3)
            }
        )
   }    

   /**
     * POST a form NON-JSON reply
     * @param {String} api API URL, like "/api/save/file"
     * @param {FormData} formData object FormData that should be initiated and filled by form fields, files and other data that will POST to the api
     * @param {function} parser function with params api and result of POST, may contain any JSON object that will be processed by the processor
     * @example   
     *       formData.append('dto', JSON.stringify(this.state.data))    //whole DTO will packed to JSON and placed to the field
     *       formData.append('file', this.state.file.file);
     *       Fetchers.postForm("/api/common/upload/save/file",formData, (api,result)=>{ ... process result ...})
     */
    static postForm(api, formData, processor){
        Spinner.show()
        fetch(Fetchers.contextPath()+api, {
            credentials: 'include',
            method: 'POST',
            body: formData
        })
        .then(res => { 
            Spinner.hide()            
            if (res.ok) { 
                return res;             
            }else{
                Spinner.hide()
                Alerts.show(api +" " + response.status + " " + response.statusText,3)
            }
        }) 
        .then(res=> {
            Spinner.hide()
            if(typeof res != "undefined"){
                processor(api,res)
            }
        })
        .catch(res=>{ 
            Spinner.hide()
            Alerts.show(api +" " + res.message,3)
            }
        )
   }    

    /**
     * Is result not empty array?
     * @param {[]} result 
     */
    static isGoodArray(result){
        return typeof result != 'undefined' && result != null && Array.isArray(result) && result.length>0
    }
    
    /**
     * Determine context path properly - full path with protocol
     */
    static contextPath(){
        let ret = window.location.protocol + "//" + window.location.host
        if (Fetchers.contextName.length>0){
            return ret + window.contextPathCollector
        }else{
            return ret
        }
    }

    /**
     * pure name of context or empty
     */
    static contextName(){
        let ret = window.contextPathApplication
        if( typeof ret == 'undefined'){
            ret=''
        }
        return ret
    }
    /**
     * Store  data to browser's local storage
     * @param {string} containerName - name the data storage (the key)
     * @param {object} data - object  to store to window.localStorage
     * @example
     * Fetchers.writeLocaly("Authorities", this.state.parentIds)
     */
    static writeLocaly(containerName, data){
        let str = JSON.stringify(data);
        window.localStorage.setItem(containerName+"_Pharmadex2", str)
    }
    /**
     * Read data from the browser's local storage
     * @param {string} containerName name of the container component
     * @param {*} data the current object of the container component that will be write to local storage if ones is not existing yet
     * @returns stored object or the current object
     */
    static readLocaly(containerName, data){
        let str = window.localStorage.getItem(containerName+"_Pharmadex2")
        if(str!=null){
            return JSON.parse(str)
        }else {
            this.writeLocaly(containerName,data)
            return data
        }
    }

    static removeLocaly(containerName){
        let str = window.localStorage.getItem(containerName+"_Pharmadex2")
        if(str!=null){
            window.localStorage.removeItem(containerName+"_Pharmadex2")
        }
    }
    /**
     * Store an object to the session storage
     * @param {any JS object} thing 
     * @param {string} key to session storage 
     */
    static storeThing(thing, key){
        if(key != undefined){
            let value = JSON.stringify(thing)
            window.sessionStorage.removeItem(key)
            window.sessionStorage.setItem(key,value)
        }

    }
    /**
     * Read object from the session storage
     * @param {string} key possible key in the session storage
     * @returns the object or undefined if the objectt is not found
     */
    static readThing(key){
        let ret = undefined
        if(key != undefined){
            if(window.sessionStorage.getItem(key)){
                let value=window.sessionStorage.getItem(key)
                ret=JSON.parse(value)
            }
        }
        return ret
    }

    static openWindowHelp(url){
        window.open(url,'helpwnd').focus()
        /*let wnd = window.open(url,'helpwnd')
        wnd.onload = ()=>{
            wnd.document.title = 'YOUR TITLE'
        }
        wnd.onfocus = ()=>{
            wnd.document.title = 'YOUR TITLE'
        }
        wnd.focus()*/
    }

    static openWindowAssist(url){
        window.open(url,'assistwnd').focus()
    }
}
export default Fetchers