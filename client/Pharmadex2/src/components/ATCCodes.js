import React , {Component} from 'react'
import {Container, Row, Col, Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Pharmadex from './Pharmadex'
import CollectorTable from './utils/CollectorTable'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import SearchControl from './utils/SearchControl'

/**
 *ATCCodes component
 @example
 <ATCCodes
        recipient={this.state.identifier} 
        readOnly
    />
 */
class ATCCodes extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),  //my address for messages
            data:{
            },
            labels:{
                requiredvalue:"",
                search:"",
                atc_code:"",

            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.tableLoader=this.tableLoader.bind(this)
        this.content=this.content.bind(this)
        this.selectedTable=this.selectedTable.bind(this)
        this.atcTable=this.atcTable.bind(this)
    }
    /**
     * Creates this component to place in the Thing
     * @param {string} label - label on the top
     * @param {AtcDTO} data - data to process
     * @param {string} recipient - recipient's address for messaging
     * @param {boolean} readOnly
     * @param {number} index - the "key" property
     */
    static thingControl(label, data, recipient, readOnly, index){
        return(
            <Row key={index}>
                <Col>
                    <Row hidden={data.valid} className="mb-1">
                        <Col>
                            <Alert color="danger" className="p-0 m-0">
                                <small>{data.identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <h6>{label}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ATCCodes   data={data}
                                        recipient={recipient}
                                        readOnly={readOnly}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
        )
    }

    /**
     * listen for askData broadcast and getData only to own address
     */
    eventProcessor(event){
        let data=event.data
        if(this.state.data.readOnly || this.props.readOnly){
            return
        }
        if(data.from==this.props.recipient){
            if(data.subject=="onATCChange"){
                console.log("onATCChange")
            }
        }
        return
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.state.data=this.props.data
        Locales.createLabels(this)
        Locales.resolveLabels(this)
        this.tableLoader()
    }
    componentDidUpdate(){
        /*if(this.state.data.historyId!=this.props.historyId){
            this.state.data.dictUrl=this.props.dictUrl,
            this.state.data.historyId=this.props.historyId
            this.tableLoader()
        }*/
    }
    
    tableLoader(){
        Fetchers.postJSONNoSpinner("/api/common/atc/load/table", this.state.data, (query, result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    selectRow(rowNumber){
        let row = this.state.data.table.rows[rowNumber]
        let selectedrows = this.state.data.selectedtable.rows
        
        let isdublicate = false
        if(selectedrows.length > 0){
            let code = row.row[0].value
            this.state.data.selectedtable.rows.forEach((r, ind) => {
                if(code == r.row[0].value){
                    isdublicate = true
                }
            })
        }

        if(!isdublicate){
            var size = this.state.data.selectedtable.rows.length
            this.state.data.selectedtable.rows[size] = row
        }
        
        Navigator.message(this.state.identifier, this.props.recipient, "onSelectionChange",this.state.data)
        this.setState(this.state.data)
    }

    deselectRow(rowNumber){
        let rows = this.state.data.selectedtable.rows
        this.state.data.selectedtable.rows = []

        let i = 0
        rows.forEach((r, ind) => {
            if(ind != rowNumber){
                this.state.data.selectedtable.rows[i] = r
                i++
            }
        });
        Navigator.message(this.state.identifier, this.props.recipient, "onSelectionChange",this.state.data)
        this.setState(this.state.data)
    }
    /**
     * Read only or not read only
     * @returns 
     */
    content(){
        if(this.props.readOnly || this.state.data.readOnly){
            return(
                <Row>
                    <Col>
                        {this.selectedTable()}
                    </Col>
                </Row>
            )
        }else{
            return(
                <Row>
                    <Col xs='12' sm='12' lg='12' xl='6'>
                        {this.atcTable()}
                    </Col>
                    <Col xs='12' sm='12' lg='12' xl='6'>
                        {this.selectedTable()}
                    </Col>
                </Row>
            )
        }
    }
    /**
     * Table to which selection occurs
     */
    selectedTable(){
        return(
            <Row>
                <Col>
                    <Row>
                        <Col>
                            <h6>{this.state.labels.atc_code}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable
                                tableData={this.state.data.selectedtable}
                                loader={this.tableLoader}
                                linkProcessor={(rowNo,cellNo)=>{
                                    if(this.state.data.readOnly || this.props.readOnly){
                                        return
                                    }
                                    this.deselectRow(rowNo)
                                }}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                                styleCorrector={(header)=>{
                                    if(header=='atccode'){
                                        return {width:'30%'}
                                    }
                                }}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
        )
    }
    /**
     * Table from which selection occurs
     * 
     */
    atcTable(){
        return(
            <Row>
                <Col>
                    <Row>
                        <Col>
                            <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.tableLoader}/>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable
                                tableData={this.state.data.table}
                                loader={this.tableLoader}
                                linkProcessor={(rowNo,cellNo)=>{
                                    if(this.state.data.readOnly || this.props.readOnly){
                                        return
                                    }
                                    this.selectRow(rowNo)
                                }}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                                styleCorrector={(header)=>{
                                    if(header=='Identifier'){
                                        return {width:'30%'}
                                    }
                                }}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
        )
    }
    render(){
        if(this.state.data.table == undefined){
            return []
        }else{
            return(
                <Container fluid className={Pharmadex.settings.activeBorder}>
                        {this.content()}
                </Container>
            )
        }
    }
}
export default ATCCodes
ATCCodes.propTypes={ 
    data:PropTypes.object.isRequired,           //AtcDTO.java                 
    recipient:PropTypes.string.isRequired,      //recipient for messages
    readOnly:PropTypes.bool,                    //read only
}