import React , {Component} from 'react'
import {Row, Col, Container} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import SearchControl from './utils/SearchControl'
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
            this.loadTable()
        }
    }

    /**
     * Load a table only
     */
    loadTable(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/applications/table", this.state.data, (query,result)=>{
            this.state.data=result
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
                        <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.loadTable} />
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
                                if(header=='days'){
                                    return {width:'5%'}
                                }
                                if(header=='activityurl'){
                                    return {width:'15%'}
                                }
                            }}
                            linkProcessor={(row,col)=>{
                                let data={
                                    url:this.state.data.url,
                                    applDictNodeId:this.state.data.dictItemId,
                                    historyId:this.state.data.table.rows[row].dbID,
                                }
                                let param = JSON.stringify(data)
                                Fetchers.postJSONNoSpinner("/api/guest/application/or/activity", data, (query,result)=>{
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
}