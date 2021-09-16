import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'
import Downloader from './utils/Downloader'

/**
 * This component is responsible for usage of resources
 */
class ResourcesUsage extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:this.props.data,
            labels:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * reload a table
     */
    loader(){
        
    }
    render(){
        if(this.state.data.table==undefined){
            return []
        }
        return(
            <Container fluid>
                <CollectorTable
                    tableData={this.state.data.table}
                    loader={this.loader}
                    headBackground={Pharmadex.settings.tableHeaderBackground}
                    linkProcessor={(rowNo, col)=>{
                        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/thing/values/extract", this.props.thing, (query,result)=>{
                            Fetchers.setJustLoaded(result,false);
                            let data={
                                data:result,
                                nodeId:this.state.data.table.rows[rowNo].dbID,
                                historyId:this.state.data.historyId
                            }
                            //let paramStr=JSON.stringify(data)
                            //let paramEnc = encodeURI(paramStr)
                            //window.open('/api/'+Navigator.tabSetName() +'/resource/download/param='+paramEnc, "_blank")
                            let dl = new Downloader()
                            dl.postDownload('/api/'+Navigator.tabSetName() +'/resource/download/form', data, "file.bin")
                        })
                    }}
                />
            </Container>
        )
    }


}
export default ResourcesUsage
ResourcesUsage.propTypes={
    data:PropTypes.object.isRequired,       //ResourceDTO
    thing:PropTypes.object.isRequired,      //parent thing
    recipient:PropTypes.string.isRequired,   //Thing message address
    readonly:PropTypes.bool
}