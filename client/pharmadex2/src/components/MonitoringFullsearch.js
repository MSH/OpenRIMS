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
import FieldDisplay from './form/FieldDisplay'

/**
 * Responsible for assigned activities. Any user, except an applicant
 */
class MonitoringFullsearch extends Component{
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
        this.drillDown=this.drillDown.bind(this)
    }

    componentDidMount(){
        Locales.resolveLabels(this)
        this.loadData()
    }

    loadData(){
        var searchStr = Fetchers.readLocaly("monitor_fullsearch_search", "")
        var api = "/api/"+Navigator.tabSetName()+"/my/monitoring/type=" + "fullsearch" + "&search=" + searchStr
        Fetchers.postJSONNoSpinner(api, this.state.data, (query,result)=>{
            this.state.data=result
            Fetchers.writeLocaly("monitor_fullsearch_search", "")
            this.setState(this.state)
        })
    }

    /**
     * Convert application data ID to convinient history ID and, then, open the application in the usual way
     * @param {Application Data ID} apID 
     */
     drillDown(apID){
        Fetchers.writeLocaly("monitor_fullsearch_search", this.state.data.fullsearch.generalSearch)
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/my/monitoring/convinient/history", apID, (query,result)=>{
            let data={
                url:this.state.data.url,
                applDictNodeId:this.state.data.dictItemId,
                historyId:result,
            }
                let param = JSON.stringify(data)
                Navigator.navigate(Navigator.tabName(),"activitymanager",param)
        })
    }

    render(){
        if(this.state.data == undefined || this.state.data.fullsearch == undefined || this.state.data.fullsearch.rows == undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row>
                <Col>
                    <Row className="mb-3">
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <SearchControlNew key='4' label={this.state.labels.search} table={this.state.data.fullsearch} loader={this.loadData}/>
                        </Col>
                        <Col xs='12' sm='12' lg='6' xl='6' className="d-flex justify-content-center">
                        <small>{this.state.labels.dateactualLb}</small>
                        <FieldDisplay attribute='dateactual' component={this} mode='time'/>
                        </Col>
                        <Col xs='12' sm='12' lg='2' xl='2' className="d-flex justify-content-end">
                        <ButtonUni
                            label={this.state.labels.exportExcel}
                            onClick={()=>{
                                let downloader = new Downloader();
                                Fetchers.setJustLoaded(this.state.data,false)
                                downloader.postDownload("/api/"+Navigator.tabSetName()+ "/my/monitoring/fullsearch/excel",
                                this.state.data, "monitoring_fullsearch.xlsx");
                            }} 
                            color={"info"}
                                />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable key='44'
                            tableData={this.state.data.fullsearch}
                            loader={this.loadData}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(header=='fullsearch'){
                                    return {width:'10%'}
                                }
                            }}
                            selectRow={(row)=>{
                                    this.drillDown(this.state.data.fullsearch.rows[row].dbID)
                                }   
                            }
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            </Container>
        )
    }
}
export default MonitoringFullsearch
MonitoringFullsearch.propTypes={
    
}