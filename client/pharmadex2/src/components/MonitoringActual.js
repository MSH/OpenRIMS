import React , {Component} from 'react'
import {Row, Col, Container} from 'reactstrap'
import Fetchers from './utils/Fetchers'
import Locales from './utils/Locales'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import CollectorTable from './utils/CollectorTable'
import ButtonUni from './form/ButtonUni'
import SearchControlNew from './utils/SearchControlNew'
import Downloader from './utils/Downloader'

/**
 * Responsible for assigned activities. Any user, except an applicant
 */
class MonitoringActual extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            labels:{
                search:'',
                exportExcel:''
            },
            data:{},
        }
        this.loadData=this.loadData.bind(this)
    }

    componentDidMount(){
        Locales.resolveLabels(this)
        this.loadData()
    }

    loadData(){
        var searchStr = Fetchers.readLocaly("monitor_actual_search", "")
        var api = "/api/"+Navigator.tabSetName()+"/my/monitoring/type=" + "actual" + "&search=" + searchStr
        Fetchers.postJSONNoSpinner(api, this.state.data, (query,result)=>{
            this.state.data=result
            Fetchers.writeLocaly("monitor_actual_search", "")
            this.setState(this.state)
        })
    }

    render(){
        if(this.state.data == undefined || this.state.data.table == undefined || this.state.data.table.rows == undefined){
            return Pharmadex.wait()
        }

        return(
            <Container fluid>
                <Row>
                    <Col>
                        <Row className="mb-3">
                            <Col xs='12' sm='12' lg='4' xl='4'>
                                <SearchControlNew key="1" label={this.state.labels.search} table={this.state.data.table} loader={this.loadData}/>
                            </Col>
                            <Col xs='0' sm='0' lg='6' xl='6'/>
                            <Col xs='12' sm='12' lg='2' xl='2' className="d-flex justify-content-end">
                            <ButtonUni
                                label={this.state.labels.exportExcel}
                                onClick={()=>{
                                    let downloader = new Downloader();
                                    Fetchers.setJustLoaded(this.state.data,false)
                                    downloader.postDownload("/api/"+Navigator.tabSetName()+ "/my/monitoring/actual/excel",
                                    this.state.data, "monitoring_actual.xlsx");
                                }} 
                                color={"info"}
                                    />
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable key='11'
                                tableData={this.state.data.table}
                                loader={this.loadData}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                                styleCorrector={(header)=>{
                                    if(header=='scheduled'){
                                        return {width:'10%'}
                                    }
                                }}
                                selectRow={(row)=>{
                                    let data={
                                        url:this.state.data.url,
                                        applDictNodeId:this.state.data.dictItemId,
                                        historyId:this.state.data.table.rows[row].dbID,
                                    }
                                        let param = JSON.stringify(data)
                                        Fetchers.writeLocaly("monitor_actual_search", this.state.data.table.generalSearch)
                                        Navigator.navigate(Navigator.tabName(),"activitymanager",param)
                                    }}   
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </Container>
        )
    }
}
export default MonitoringActual
MonitoringActual.propTypes={
    
}