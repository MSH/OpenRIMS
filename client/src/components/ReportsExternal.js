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
            labels:{externalreport:"",
            pub_reports:"",
            nmra_reports:"",
            appl_reports:""}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadTable=this.loadTable.bind(this)
        this.getUrlGoogleTools=this.getUrlGoogleTools.bind(this)
        this.dictNMRA=this.dictNMRA.bind(this)
        this.dictAPPL=this.dictAPPL.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier+"_dict" ||
            data.to==this.state.identifier+"_nmra" ||
            data.to==this.state.identifier+"_appl"){
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
dictNMRA(){
    if(this.state.data.selectNMRA==undefined){
        return[]
    }else{
        return <Col xs='12' sm='6' lg='6' xl='6' className="d-inline">
        <small>{this.state.labels.nmra_reports}</small>
        <Dictionary identifier={'dictionary.report.googletools.nmra'+this.state.identifier} recipient={this.state.identifier+"_nmra"} data={this.state.data.selectNMRA} display={true}/>
        </Col>
    }
}

dictAPPL(){
    if(this.state.data.selectAPPL==undefined){
        return[]
    }else{return<Row>
        <Col xs='12' sm='6' lg='6' xl='6' className="d-inline">
        <small> {this.state.labels.appl_reports}</small>
        <Dictionary identifier={'dictionary.report.googletools.appl'+this.state.identifier} recipient={this.state.identifier+"_appl"} data={this.state.data.selectAPPL} display={true}/>
        </Col>
        </Row>
    }
}
    render(){
        if(this.state.labels.locale==undefined || this.state.data.select==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                <Col xs='12' sm='6' lg='6' xl='6' className="d-inline">
                <small>{this.state.labels.pub_reports}</small>
                        <Dictionary identifier={'dictionary.reports.googletools'+this.state.identifier} recipient={this.state.identifier+"_dict"} data={this.state.data.select} display={true}/>
                    </Col>
                        {this.dictNMRA()}
                </Row> 
                        {this.dictAPPL()}
            </Container>
        )
    }


}
export default ReportsExternal
ReportsExternal.propTypes={
    
}