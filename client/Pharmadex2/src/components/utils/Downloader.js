import Spinner from '../utils/Spinner'
import Alerts from '../utils/Alerts'
import Fetchers from '../utils/Fetchers'
/**
 * this class necessary to implement Ajax file download for IE and rest of browsers :)
 * Depends on fetch, Spinner and Alerts
 * ToDo check Edge?
 */
class Downloader{

    /**
     * simple download by simple get request
     */
    pureGetDownload(url){
       let link = document.createElement('a');
       link.href = Fetchers.contextPath() + url;
       link.id="idoftemporarydownloadlink"
       document.body.appendChild(link)
       link.click();
       document.body.removeChild(link)
        // throw "OK"
    }



    /**
     * Implement download using POST
     * @param {String} url 
     * @param {any} params 
     * @param {String} defaultFileName 
     */
    postDownload(url, params, defaultFileName){
        if(this.isIE()){
            this.downloadIE(Fetchers.contextPath() + url, params, defaultFileName,true)
        }else{
            this.downloadRest(Fetchers.contextPath() + url, params, defaultFileName,true)
        }
    }

    getDownload(url, defaultFileName){
        if(this.isIE()){
            this.downloadIE(url,"", "unknown.qtb",false)
        }else{
            this.downloadRest(url, "", "unknown.qtb",false)
        }
    }

    /**
     * Is it IE or the rest?
     */
    isIE() {
        let ua = window.navigator.userAgent;
        /* MSIE used to detect old browsers and Trident used to newer ones*/
        var is_ie = ua.indexOf("MSIE ") > -1 || ua.indexOf("Trident/") > -1;
        return is_ie; 
      }
    /**
     * Special download for IE only
     */
    downloadIE(url, params, defaultFileName,isPost){
        Spinner.show()
        let promise
        if(isPost){
            promise = this.callPostFetch(url, params)
        }else{
            promise = this.callGetFetch(url)
        }
        promise.then(response => {
            this.filename=response.headers.get('filename');
            return response.blob();
        })
        .then(
            blob=>{
                if(typeof blob != "undefined"){
                if(typeof this.filename === undefined){
                    this.filename=defaultFileName
                }
                Spinner.hide()
                window.navigator.msSaveOrOpenBlob(blob, this.filename)
            }else{
                this.pureGetDownload()
                return undefined
            }
            }
        )
        .catch(res=>{
            Spinner.hide()
            if(res != "OK"){
                Alerts.show(res,3)
            }
         }
        )
    }
    /**
     * Fetch for POST
     * @param {String} url
     * @param {any} params
     */
    callPostFetch(url, params){
        let  body=params     
        return fetch(url,  {
            credentials: 'include', //pass cookies, for authentication
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'              
            },           
            body: JSON.stringify(body)//this.state.data
          })      
    }

    /**
     * Fetch for GET
     * @param {String} url
     */
    callGetFetch(url){
        return  fetch(url,  {credentials: 'include' })
    }

    /**
     * Download for all browsers, except IE
     */
    downloadRest(url, params, defaultFileName,isPost){
        Spinner.show()   
        let promise
        if(isPost){
            promise = this.callPostFetch(url, params)
        }else{
            promise = this.callGetFetch(url)
        }
        promise.then(response => {
            this.filename=response.headers.get('filename');
            if(typeof this.filename === "undefined" ){
                this.filename=defaultFileName;
            }
           return response.body;
        })
        .then(body => {
        if(typeof body != "undefined"){
        const reader = body.getReader();
        return new ReadableStream({
            start(controller) {
                return pump();
                function pump() {
                    return reader.read().then(({ done, value }) => {
                    // When no more data needs to be consumed, close the stream
                    if (done) {
                        controller.close();
                        return;
                    }
                    // Enqueue the next data chunk into our target stream
                    controller.enqueue(value);
                    return pump();
                    });
                }
            }
        })
        }else{
            this.pureGetDownload(url);
            return undefined;
        }
        })
        .then(stream => new Response(stream))
        .then(response => response.blob())
        .then(blob => URL.createObjectURL(blob))
        .then(blobUrl => {
            Spinner.hide()
            this.downloadFile(blobUrl)
        })
       .catch(res=> {
           Spinner.hide()
           if(res != "OK"){
            Alerts.show(res,3)
            }
         }
       ) 
      
    }
    /**
     * Real download Excel
     */
    downloadFile(blobURL){
        if(typeof this.filename==="undefined" ){
            this.filename = this.filename
        }
        const tempLink = document.createElement('a');
        tempLink.style.display = 'none';
        tempLink.href = blobURL;
        tempLink.setAttribute('download', this.filename);

        if (typeof tempLink.download === 'undefined') {
            tempLink.setAttribute('target', '_blank');
        }
        document.body.appendChild(tempLink);
        tempLink.click();
        document.body.removeChild(tempLink);
        setTimeout(() => {
            // For Firefox it is necessary to delay revoking the ObjectURL
            window.URL.revokeObjectURL(blobURL);
        }, 100);
    }
}
export default Downloader