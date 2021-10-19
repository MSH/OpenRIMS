import React , {Component} from 'react'
import {ButtonGroup, Button} from 'reactstrap'
import Fetchers from './utils/Fetchers'
import Alerts from './utils/Alerts'
import Navigator from './utils/Navigator'

/**
 * Languages switch
 */
class Languages extends Component{
    constructor(props){
        super(props)
        this.state={
          data:{}
        }
        this.createFlags=this.createFlags.bind(this)
        this.switchLanguage=this.switchLanguage.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
    }
    componentDidMount(){
      window.addEventListener("message",this.eventProcessor)
      Fetchers.postJSONNoSpinner("/api/public/languages", this.state.data,(query,result)=>{
        this.state.data=result
        this.setState(this.state)
      })
    }

    componentWillUnmount(){
      window.removeEventListener("message",this.eventProcessor)
  }

    eventProcessor(event){
      let data=event.data
      
    }

     /**
       * Switch a language for UI
       * @param {i.e. en_us} localeStr 
       */
      switchLanguage(localeStr){
        let path = ""
        if(window.location.pathname){
          path=window.location.pathname
        }
        let params="?lang="+localeStr
        if(window.location.search){
          let search=window.location.search
          let params = search.split("&");
          params.forEach((value)=>{
            if(value.indexOf("lang=")==-1){
              params=params+"&"+value
            }
          })

        }
        let newURL = window.location.protocol +"//" 
                     + window.location.host
                     +path
                     +params
                     +window.location.hash
        window.location.replace(newURL);
      }

      /**
       * create language switch control
       */
      createFlags(){
        let flags=this.state.data.langs
        let ret = [];
        if(Fetchers.isGoodArray(flags)){
          flags.forEach((value)=>{
            if(value.localeAsString != this.state.data.selected.localeAsString){
              ret.push(
                <Button size="sm" color="link" key={value.displayName} 
                      onClick={()=>{
                        Alerts.warning("Did you save your job?", ()=>{this.switchLanguage(value.localeAsString)}, ()=>{})
                      }}
                      >
                    <img src={"/api/public/flag?"+"localeStr="+value.localeAsString} width="20" title={value.displayName} />
                </Button>)
              }  
          })
        }
        return ret;
      }
    render(){
        if (this.state.data.langs == undefined){
          return []
        }
        return(
          <ButtonGroup size="sm">
            {this.createFlags()}
          </ButtonGroup>
        )
    }
}

export default Languages;