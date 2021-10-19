import React , {Component} from 'react'
import {FormGroup, Input, Container, Label, Row, Col, Alert, Button} from 'reactstrap'
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

            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.tableLoader=this.tableLoader.bind(this)
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
        
        Navigator.message(this.state.identifier, this.props.recipient, "onATCChange",this.state.data)
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
        Navigator.message(this.state.identifier, this.props.recipient, "onATCChange",this.state.data)
        this.setState(this.state.data)
    }

    render(){
        if(this.state.data.table == undefined){
            return []
        }else{
            return(
                <Container fluid className={Pharmadex.settings.activeBorder}>
                    <Row>
                        <Col xs='12' sm='12' lg='6' xl='6'>
                            <Row hidden={this.state.data.readOnly || this.props.readOnly}>
                                <Col xs='12' sm='12' lg='5' xl='5'>
                                    <div>
                                        <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.tableLoader}/>
                                    </div>
                                </Col>
                                <Col xs='12' sm='12' lg='7' xl='7' className="d-flex justify-content-end">
                                    <h4>{this.state.data.title}</h4>
                                </Col>
                            </Row>
                            <Row hidden={this.state.data.readOnly || this.props.readOnly} className="pb-1">
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
                                        selectRow={(rowNo)=>{
                                            
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
                            <Row>
                                <Col xs='12' sm='12' lg='6' xl='6'>
                                </Col>
                                <Col xs='12' sm='12' lg='6' xl='6' className="d-flex justify-content-end">
                                    <h6>Selected ATC codes</h6>
                                </Col>
                            </Row>
                            <Row className="pb-1">
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
                                        selectRow={(rowNo)=>{
                                            
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
                </Container>
            )
        }
    }


}
export default ATCCodes
ATCCodes.propTypes={                  
    recipient:PropTypes.string.isRequired,      //recipient for messages
    readOnly:PropTypes.bool,                    //read only
}