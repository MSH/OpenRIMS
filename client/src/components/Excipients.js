import React , {Component} from 'react'
import {FormGroup, Input, Container, Label, Row, Col, Alert, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Pharmadex from './Pharmadex'
import CollectorTable from './utils/CollectorTable'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import SearchControlNew from './utils/SearchControlNew'
import FieldInput from './form/FieldInput'
import ButtonUni from './form/ButtonUni'

/**
 *Excipients component
 @example
 <Excipients
        recipient={this.state.identifier} 
        readOnly
    />
 */
class Excipients extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),  //my address for messages
            data:{
            },
            labels:{
                requiredvalue:"",
                search:"",
                excipient_name:"",
                dos_strength:"",
                dos_unit:"",
                cancel:"",
                save:""
            },
            showform:false
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
            /*if(data.subject=="onExcipientChange"){
                console.log("onExcipientChange")
            }*/
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
        Fetchers.postJSONNoSpinner("/api/common/excipient/load/table", this.state.data, (query, result)=>{
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
            let code = row.dbID
            this.state.data.selectedtable.rows.forEach((r, ind) => {
                if(code == r.dbID){
                    isdublicate = true
                }
            })
        }

        if(!isdublicate){
            this.state.showform = true
            this.state.data.id = row.dbID
            this.state.data.excipient_name.value = row.row[0].value
            this.state.data.dos_strength.value = ""
            this.state.data.dos_unit.value = ""
        }
        
        this.setState(this.state)
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
        Navigator.message(this.state.identifier, this.props.recipient, "onExcipientChange",this.state.data)
        this.setState(this.state)
    }

    render(){
        if(this.state.data.table == undefined){
            return []
        }else{
            return(
                <Container fluid className={Pharmadex.settings.activeBorder}>
                    <Row>
                        <Col xs='12' sm='12' lg='6' xl='6'>
                            <Row hidden={this.state.data.readOnly}>
                                <Col xs='12' sm='12' lg='5' xl='5'>
                                    <div>
                                        <SearchControlNew label={this.state.labels.search} table={this.state.data.table} loader={this.tableLoader}/>
                                    </div>
                                </Col>
                                <Col xs='12' sm='12' lg='7' xl='7' className="d-flex justify-content-end">
                                    <h4>{this.state.data.title}</h4>
                                </Col>
                            </Row>
                            <Row className="pb-1">
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
                            <Row hidden={!this.state.showform} className={Pharmadex.settings.activeBorder}>
                                <Col xs='12' sm='12' lg='12' xl='12'>
                                    <Row>
                                        <Col xs='12' sm='12' lg='12' xl='12'>
                                            <h6>{this.state.data.excipient_name.value}</h6>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col xs='12' sm='12' lg='12' xl='6'>
                                            <FieldInput mode="text" attribute="dos_strength" component={this} />
                                        </Col>
                                        <Col xs='12' sm='12' lg='12' xl='6'>
                                            <FieldInput mode="text" attribute="dos_unit" component={this} />
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col xs='12' sm='12' lg='4' xl='4'></Col>
                                        <Col xs='12' sm='12' lg='4' xl='4'>
                                            <ButtonUni
                                                label={this.state.labels.save}
                                                color='primary'
                                                onClick={()=>{
                                                    Fetchers.postJSONNoSpinner("/api/common/excipient/add", this.state.data,(query,result)=>{
                                                        this.state.data=result
                                                        this.state.showform = false
                                                        Navigator.message(this.state.identifier, this.props.recipient, "onExcipientChange",this.state.data)
                                                        this.setState(this.state)
                                                    })
                                                }}
                                            />
                                        </Col>
                                        <Col xs='12' sm='12' lg='4' xl='4'>
                                            <ButtonUni
                                                    label={this.state.labels.cancel}
                                                    color='secondary'
                                                    onClick={()=>{
                                                        this.state.showform = false
                                                        this.state.data.id = 0
                                                        this.state.data.excipient_name.value = ""
                                                        this.state.data.dos_strength.value = ""
                                                        this.state.data.dos_unit.value = ""
                                                        this.setState(this.state)
                                                    }}/>
                                        </Col>
                                    </Row>
                                </Col>
                            </Row>
                            <Row>
                                <Col xs='12' sm='12' lg='6' xl='6'>
                                </Col>
                                <Col xs='12' sm='12' lg='6' xl='6' className="d-flex justify-content-end">
                                    <h6>Selected {this.state.data.title}</h6>
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
export default Excipients
Excipients.propTypes={                  
    recipient:PropTypes.string.isRequired,      //recipient for messages
    readOnly:PropTypes.bool,                    //read only
}