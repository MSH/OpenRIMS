import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Dictionary from './Dictionary'
/**
 * 
 */
class ReportsExternal extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},
            identifier:Date.now().toString(),
            labels:{externalreport:""}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadTable=this.loadTable.bind(this)
        this.getUrlGoogleTools=this.getUrlGoogleTools.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier+"_dict"){
                if(data.subject=="onSelectionChange"){
                    this.state.data.select=data.data
                    this.getUrlGoogleTools()
                }
            }
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        Locales.createLabels(this)
        this.loadTable()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
/**
     * load only table reportsGoogleTools
     */
loadTable(){
    Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/load/googletools", this.state.data, (query,result)=>{
        this.state.data=result
        this.setState(this.state)
    })
}
getUrlGoogleTools(){
    Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/geturl/googletools",this.state.data, (query,result)=>{
       if(result.dataUrl!=""){
        window.open(result.dataUrl,'_blank')
       }
    })
}
    render(){
        if(this.state.labels.locale==undefined || this.state.data.select==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <Dictionary identifier={'dictionary.reports.googletools'+this.state.identifier} recipient={this.state.identifier+"_dict"} data={this.state.data.select} display={true}/>
                    </Col>
                </Row> 
            </Container>
        )
    }


}
export default ReportsExternal
ReportsExternal.propTypes={
    
}