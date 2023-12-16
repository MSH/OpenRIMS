import Fetchers from './Fetchers'
import Alerts from './Alerts'

/**
 * Common page navigator library
 * Provides onHashChanged and set of static utilities related to navigation
 * format of any url should be /tabset#tab,component,parameter
 * @example
 * /admin#administrate,authority,0
 * @example from tabset component
 * Navigator nav = new Navigator(this) - allow navigation for tabset component 
 * 
 * Provides management for window.postMessage listeners
 */
class Navigator{
    constructor(component) {
        this.state={
            tabset:component,
            toggler:false,  //to call render
            prevHash:""
        }
        this.onHashChanged=this.onHashChanged.bind(this)
        window.onhashchange=this.onHashChanged
    }

   /**
     * Hash change event
     */
    onHashChanged(){
        this.state.tabset.setState(this.state.tabset.state)
    }

    /**
     * Return a name of the current tab set. Empty string may means the default tab (not always the first)
     */
    static tabSetName(){
        let path=window.location.pathname
        if(path.length==0){
            return "";
        }else{
            let pathArr = path.split("/")
            if(pathArr.length==2 && pathArr[0].length==0){
                if(pathArr[1].toUpperCase != Fetchers.contextName){
                    return pathArr[1]
                }else{
                    if(pathArr.length>2){
                        return pathArr[2]
                    }else{
                        return ""
                    }
                }
            }else{
                return "";
            }
        }
    }
    /**
     * Return a name for the current tab in the current tabset. Empty string means default for it
     */
    static tabName(){
        let hash = window.location.hash
        hash=hash.replace(/^#/, '')
        let params = hash.split("/");
        if(Fetchers.isGoodArray(params) && params.length>=1 && params.length<=3 ){
            return params[0]
        }else{
            return ""
        }
    }

    /**
     * Return a name for the current component in the current tab. Empty string means default for it
     */
    static componentName(){
        let hash = window.location.hash
        hash=hash.replace(/^#/, '')
        let params = hash.split("/");
        if(Fetchers.isGoodArray(params) && params.length>=2 && params.length<=3 ){
            return params[1]
        }else{
            return ""
        }
    }
    /**
     * Return a value for the parameter
     */
    static parameterValue(){
        let hash = window.location.hash
        hash=hash.replace(/^#/, '')
        let params = hash.split("/");
        if(Fetchers.isGoodArray(params) && params.length==3){
            return decodeURI(params[2])
        }else if(Fetchers.isGoodArray(params) && params.length==2){
            return decodeURI(params[1])
        }else{
            return ""
        }
    }



    /**
     * May be called from any component. Creates new window.loaction from current url and tab, component, parameter
     * @param {String} tab a tab to switch 
     * @param {String} component a component to display inside the tab
     * @param {String} parameter a prarmeter for the component, interpret by the component, not mandatory
     */
    static navigate(tab, component,parameter){
        if(typeof tab == "string"){
            let hash=tab
            if(typeof component == "string"){
                hash=tab+"/"+component
                if(typeof parameter == "string"){
                    hash=hash+"/"+encodeURI(parameter)
                }
            }
            
            window.location.hash = hash
        }else{
            Alerts.show("bad navigate tab=" + tab +" component="+component + " parameter="+parameter,3)
        }

    }

    /**
     * Convnient method to open the home page
     */
    static goHome(){
        window.location="/"
    }
       /**
     * Create a reference to the current caller
     */
        static caller(){
            let ret={
                tab:Navigator.tabName(),
                component:Navigator.componentName(),
                parameter:Navigator.parameterValue()
            }
            return ret
        }

/* ************************************** window.postMessage simplify****************************************** */

/**
 * issue a message to other components on this or opener window
 * ~~~
 * {from:from,to:to,subject:subject,data:data}
 *  from - sender 
 *  to   - receiver, * means broadcast
 *  subject - subject to distinct messages, 
 *  data - data should be created by the sender and be inrerpretated by the receiver
 *  opener - window.opener in case if the message intends to the opener, otherwise skip this parameter 
 * ~~~
 */
static message(from,to, subject, data, opener){
    let win=window
    if(opener != undefined){
        win=opener
    }
    win.postMessage({from:from,to:to,subject:subject,data:data},window.location)
}


}export default Navigator 
