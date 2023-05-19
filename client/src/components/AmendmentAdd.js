import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import CollectorTable from './utils/CollectorTable'

/**
 * Add new amendment
 * Raise an event
 * @example
 * <AmendmentAdd recipient={this.state.identifier} />
 */
class AmendmentAdd extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            labels:{
                selectobjecttomodify:'',
                selectdatatomodify:'',
                addmodification:'',
            },
            data:{                                  //AmendmentNewDTO.java
                dictItemId:this.props.dictItemId
            }
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
    componentDidUpdate(){
        if(this.state.data.dictItemId!=this.props.dictItemId){
            this.state.data.dictItemId=this.props.dictItemId
            this.state.data.applications.rows=[]
            this.state.data.dataUnits.rows=[]
            this.loadTable()
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * load/reload AmendmentNewDTO
     */
    loadTable(){
        let selected_row=Fetchers.readLocaly("amendmentsadd_dataNodeId", 0);
        this.state.data.dataNodeId = selected_row
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/amendment/propose/add", this.state.data, (query,result)=>{
            this.state.data=result
            
            this.setState(this.state)
        })
    }

    /**
     * Run a new amendment using AmendmentNewDTO
     * @param rowNo - row selected in dataUnits table
     */
    runNewAmendment(rowNo){
        let row=this.state.data.dataUnits.rows[rowNo]
        let data={
            url:'',                                     //url of an application, i.e. application.guest, deprecated in favor of applDictNodeId
            applDictNodeId:this.state.data.dictItemId,  //id of dictionary node that describes an application
            historyId:0,                                //id of the histry record to determine activity and data. Zero means new
            modiUnitId:row.dbID,                        //id of data unit selected to modify
            prefLabel:row.row[0].value,                 //preflabel by default
        }
        let param = JSON.stringify(data)
        Navigator.navigate(Navigator.tabName(),"applicationstart",param)
    }
    /**
     * Data selected to modify
     */
    dataToModify(){
        let dataUnits = this.state.data.dataUnits
        if(dataUnits != undefined){
            if(dataUnits.rows.length>0){
                return(
                    <Container fluid>
                        <Row>
                            <Col>
                                <h6>{this.state.labels.selectdatatomodify}</h6>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={dataUnits}
                                    loader={this.loadTable}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    linkProcessor={(rowNo, cell)=>{
                                        this.runNewAmendment(rowNo)
                                    }}
                                />
                            </Col>
                        </Row>
                    </Container>
                )
            }
        }
        return []
    }
    render(){
        if(this.state.data.applications == undefined){
            return Pharmadex.wait();
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <h5>{this.state.labels.addmodification}</h5>
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col>
                                <h6>{this.state.labels.selectobjecttomodify}</h6>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.applications}
                                    loader={this.loadTable}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    selectRow={(rowNo)=>{
                                        this.state.data.dataNodeId=this.state.data.applications.rows[rowNo].dbID
                                        Fetchers.writeLocaly("amendmentsadd_dataNodeId", this.state.data.dataNodeId);
                                        this.loadTable()
                                    }}
                                    linkProcessor={(rowNo, cell)=>{
                                        this.state.data.dataNodeId=this.state.data.applications.rows[rowNo].dbID
                                        Fetchers.writeLocaly("amendmentsadd_dataNodeId", this.state.data.dataNodeId);
                                        this.loadTable()
                                    }}
                                />
                            </Col>
                        </Row>
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.dataToModify()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default AmendmentAdd
AmendmentAdd.propTypes={
    dictItemId:PropTypes.number.isRequired,         //amendment type
    recipient:PropTypes.string.isRequired,          //for messaging
}