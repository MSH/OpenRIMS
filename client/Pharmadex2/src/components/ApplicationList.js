import React , {Component} from 'react'
import {Row, Col, Container} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import SearchControlNew from './utils/SearchControlNew'
import CollectorTable from './utils/CollectorTable'
import ButtonUni from './form/ButtonUni'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import Spinner from './utils/Spinner'
/**
 * List of applications with possibility to add new ones
 */
class ApplicationList extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{                                      //ApplicationsDTO
                dictItemId:this.props.dictItemId
            },
            labels:{
                search:'',
                global_add:'',
                manageapplications:'',
                init:'',
            }
        }
        this.loadTable=this.loadTable.bind(this)
    }
    componentDidMount(){
        Locales.resolveLabels(this)
        this.loadTable()
    }
    componentDidUpdate(){
        if(this.state.data.dictItemId!=this.props.dictItemId){
            this.state.data.dictItemId=this.props.dictItemId
            this.state.data.table.generalSearch = "onSelDict"
            this.loadTable()
        }
    }

    /**
     * Load a table only
     */
    loadTable(){
        let api="/api/guest/applications/table"
        if(this.props.amend){
            api="/api/guest/applications/table/amendments"
        }
        let srch = Fetchers.readLocaly("applicationlist_search", "");
        api += "/search=" + srch

        Fetchers.postJSON(api, this.state.data, (query,result)=>{
            this.state.data=result
            Fetchers.writeLocaly("applicationlist_search", "");
            this.setState(this.state)
        })
    }

    render(){
        if(this.state.data.table==undefined){
            return []
        }
        return(
            <Container fluid className={Pharmadex.settings.activeBorder}>
                <Row className="mt-1 mb-3">
                    <Col>
                        <h6>{this.state.labels.manageapplications}</h6>
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='10' xl='10'>
                        <SearchControlNew label={this.state.labels.search} table={this.state.data.table} loader={this.loadTable} />
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2' hidden={Navigator.tabSetName()!='guest' || this.props.noadd}>
                        <ButtonUni
                            label={this.state.labels.global_add}
                            onClick={()=>{
                                Spinner.show()
                                let data={
                                    url:this.state.data.url,
                                    applDictNodeId:this.state.data.dictItemId,
                                    historyId:0,
                                }
                                let param = JSON.stringify(data)
                                Navigator.navigate(Navigator.tabName(),"applicationstart",param)
                            }}
                            color="primary"
                        />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <CollectorTable
                            tableData={this.state.data.table}
                            loader={this.loadTable}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(header=='come'){
                                    return {width:'15%'}
                                }
                                if(header=='term'){
                                    return {width:'5%'}
                                }
                                if(header=='prefLabel'){
                                    return {width:'60%'}
                                }
                            }}
                            linkProcessor={(row,col)=>{
                                let data={
                                    url:this.state.data.url,
                                    applDictNodeId:this.state.data.dictItemId,
                                    historyId:0,    //unknown yet, will be resolved by application_or_activity
                                    dataId:this.state.data.table.rows[row].dbID,    //since 2023-03-17 application data node ID
                                }
                                Fetchers.writeLocaly("applicationlist_search", this.state.data.table.generalSearch)
                                Fetchers.postJSONNoSpinner("/api/guest/application/or/activity", data, (query,result)=>{
                                    let param = JSON.stringify(result)
                                    if(result.application){
                                        Navigator.navigate(Navigator.tabName(),"applicationstart",param) 
                                    }else{
                                        Navigator.navigate(Navigator.tabName(),"activitymanager",param)
                                    }
                                })
                                
                            }}
                        />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ApplicationList
ApplicationList.propTypes={
    dictItemId:PropTypes.number.isRequired,         //id of dict item selected
    recipient:PropTypes.string.isRequired,          //the recipient of messages
    noadd:PropTypes.bool,                           //disable "add" button
    amend:PropTypes.bool,                           //it is amendment, see loadTable()
}