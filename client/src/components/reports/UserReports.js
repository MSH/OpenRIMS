import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import CollectorTable from '../utils/CollectorTable'
import Pharmadex from '../Pharmadex'

/**
 * List of reports available for the current user
 * When ready - reportLoaded event
 */
class UserReports extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},                            
            labels:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadTable=this.loadTable.bind(this)
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
        this.loadTable()
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * load reports available for a user
     */
    loadTable(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/all", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }
    render(){
        if(this.state.data.rows==undefined){
            return []
        }
        return(
            <CollectorTable
                tableData={this.state.data}
                loader={this.loadTable}
                headBackground={Pharmadex.settings.tableHeaderBackground}
                styleCorrector={(header)=>{
                    
                }}
                linkProcessor={(row,col)=>{
                    let data={                  //ReportDTO
                        config:{
                            nodeId:this.state.data.rows[row].dbID
                        }
                    }
                    Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/load/", data, (query,result)=>{
                        Navigator.message(this.state.identifier, this.props.recipient, "reportLoaded", result)
                    })
                }}
            />
        )
    }


}
export default UserReports
UserReports.propTypes={
    recipient:PropTypes.string.isRequired,          //address for messages    
}