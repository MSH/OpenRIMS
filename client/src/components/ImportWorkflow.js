import React , {Component} from 'react'
import {Container, Row, Col, Label, Input, FormGroup} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Pharmadex from './Pharmadex'
import ButtonUni from './form/ButtonUni'
import FieldInput from './form/FieldInput'
import Navigator from './utils/Navigator'
import Dictionary from './Dictionary'
import CollectorTable from './utils/CollectorTable'
import SearchControlNew from './utils/SearchControlNew'
import FieldsComparator from './form/FieldsComparator'

/**
 * Import addresses
 */
class ImportWorkflow extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{
                identifier:""
            },
            runclick:false,
            labels:{
                exchangeconfig:'',
                serverurl:'',
                btn_connect:'',
                processes:'',
                workflows:'',
                statusimport:'',
                global_cancel:"",
                global_help:'',
                btn_run:'',
                selectedonly:'',
                search:'',
                importwf:'',
                processeError:''
            },
        }
        this.getStyleConnection=this.getStyleConnection.bind(this)
        this.content=this.content.bind(this)
        this.connect=this.connect.bind(this)
        this.runImport=this.runImport.bind(this)
        this.loadProccesses = this.loadProccesses.bind(this)
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.comparator = new FieldsComparator(this)
        this.state.data.identifier = Fetchers.readLocaly("mainserveraddress", "")
        this.loadProccesses(true)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    getStyleConnection(){
        var color = "red"
        if(this.state.data.valid){
            color = 'green'
        }
        return color
    }

    connect(){
        Fetchers.postJSON("/api/admin/importwf/connect", this.state.data, (query, result)=>{
            this.state.data = result
            if(this.state.data.valid){
                Fetchers.writeLocaly("mainserveraddress", this.state.data.serverurl.value)
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'success'})
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
            }
            this.setState(this.state)
        })
    }

    loadProccesses(isload){
        if(isload){
            Fetchers.postJSON("/api/admin/importwf/load", this.state.data, (query, result)=>{
                this.state.data=result
                this.setState(this.state)
            })
        }else{
            Fetchers.postJSON("/api/admin/importwf/reload", this.state.data, (query, result)=>{
                this.state.data=result
                this.state.runclick = false
                this.setState(this.state)
            })
        }
    }

    runImport(){
        this.state.runclick=true
        Fetchers.postJSON("/api/admin/importwf/runimport", this.state.data, (query, result)=>{
            this.state.data = result
            this.setState(this.state)
            Fetchers.postJSON("/api/admin/importwf/dictionaries", this.state.data, (query, result)=>{
                this.state.data = result 
                this.setState(this.state)

                if(this.state.data.valid){
                    Fetchers.postJSON("/api/admin/importwf/resources", this.state.data, (query, result)=>{
                        this.state.data = result
                        this.setState(this.state)

                        if(this.state.data.valid){
                            Fetchers.postJSON("/api/admin/importwf/dataconfigs", this.state.data, (query, result)=>{
                                this.state.data = result
                                this.setState(this.state)

                                if(this.state.data.valid){
                                    Fetchers.postJSON("/api/admin/importwf/wfconfigs", this.state.data, (query, result)=>{
                                        this.state.data = result
                                        this.setState(this.state)

                                        if(this.state.data.valid){
                                            // reload
                                            this.loadProccesses(false)
                                        }else{
                                            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                                        }
                                    })
                                }else{
                                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                                }
                            })
                        }else{
                            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                        }
                    })
                }else{
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                }
            })
        })
    }

    content(){
        let ret = []
        if(this.state.data.connect){
            if(this.state.data.procTable == undefined || this.state.data.wfTable == undefined){
                return Pharmadex.wait()
            }
            ret.push(
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Label>{this.state.labels.processes}</Label>
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6' hidden={this.state.data.wfTable.headers.headers.length == 0}>
                        <Row>
                            <Col xs='12' sm='12' lg='2' xl='2'>
                                <Label>{this.state.labels.workflows}</Label>
                            </Col>
                            {/*<Col xs='12' sm='12' lg='8' xl='8' hidden={this.state.data.wfTable.rows.length != 0}></Col>*/}
                            <Col xs='12' sm='12' lg='5' xl='5' >
                                <div >
                                    <SearchControlNew label={this.state.labels.search} table={this.state.data.wfTable} loader={this.connect}/>
                                </div>
                            </Col>
                            <Col xs='12' sm='12' lg='3' xl='3' >
                                <FormGroup  check className="form-control-sm">
                                    <Label check>
                                    <Input 
                                        type="checkbox"
                                        value={this.state.data.selectedOnly}
                                        checked={this.state.data.selectedOnly}
                                        onChange={()=>{
                                            this.state.data.selectedOnly=!this.state.data.selectedOnly
                                            this.setState(this.state)
                                            this.connect()
                                        }} 
                                        />
                                        {this.state.labels.selectedonly}
                                    </Label>
                                </FormGroup>   
                            </Col>
                            <Col xs='12' sm='12' lg='2' xl='2'>
                                <ButtonUni 
                                    label={this.state.labels.btn_run}
                                    onClick={()=>{
                                        this.runImport()
                                    }} 
                                    color={"info"}
                                    hidden={this.state.data.wfIDselect == 0 || this.state.runclick}
                            />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )
            ret.push(
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <CollectorTable
                            tableData={this.state.data.procTable}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            loader={this.connect}
                            linkProcessor={(rowNo, cell)=>{
                                
                            }}
                            selectRow={(rowNo)=>{
                                this.state.data.processIDselect = this.state.data.procTable.rows[rowNo].dbID
                                this.state.data.wfIDselect = 0
                                this.state.runclick = false
                                if(Fetchers.isGoodArray(this.state.data.procTable.rows)){
                                    this.state.data.procTable.rows.forEach((r,index) => {
                                        r.selected = false
                                        if(index == rowNo){
                                            r.selected = !r.selected
                                        }
                                    });
                                }
                                this.connect()
                            }}
                            styleCorrector={(header)=>{
                                if(header=='prefLbl'){
                                    return {width:'30%'}
                                }
                            }}
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <CollectorTable
                            tableData={this.state.data.wfTable}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            loader={this.connect}
                            linkProcessor={(rowNo, cell)=>{
                                
                            }}
                            selectRow={(rowNo)=>{
                                this.state.data.wfIDselect = this.state.data.wfTable.rows[rowNo].dbID
                                this.state.data.wfURL = this.state.data.wfTable.rows[rowNo].row[0].originalValue
                                this.state.runclick = false
                                if(Fetchers.isGoodArray(this.state.data.wfTable.rows)){
                                    this.state.data.wfTable.rows.forEach((r,index) => {
                                        r.selected = false
                                        if(index == rowNo){
                                            r.selected = !r.selected
                                        }
                                    });
                                }
                                this.setState(this.state)
                            }}
                            styleCorrector={(header)=>{
                                if(header=='prefLbl'){
                                    return {width:'30%'}
                                }
                            }}
                        />
                    </Col>
                </Row>
            )
            if(this.state.runclick){
                ret.push(
                    <Row>
                        <Col xs='12' sm='12' lg='12' xl='12'>
                            <Label>{this.state.labels.statusimport}</Label>
                        </Col>
                    </Row>
                )
                ret.push(
                    <Row>
                        <Col xs='12' sm='12' lg='12' xl='12'>
                            <CollectorTable
                                tableData={this.state.data.statusTable}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                                loader={this.runImport}
                                linkProcessor={(rowNo, cell)=>{
                                    
                                }}
                                selectRow={(rowNo)=>{
                                }}
                                styleCorrector={(header)=>{
                                    if(header=='prefLbl'){
                                        return {width:'30%'}
                                    }
                                }}
                            />
                        </Col>
                    </Row>
                )
            }
        }
        return ret
    }

    render(){
        if(this.state.data.serverurl==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row style={{alignItems:'center'}}>
                    <Col xs='12' sm='12' lg='3' xl='3'>
                        <h4>
                            {this.state.labels.exchangeconfig}
                        </h4>
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1'>
                        <h6>
                            {this.state.labels.serverurl}
                        </h6>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <Input bsSize='sm' type='text' lang={this.state.labels.locale.replace("_","-")} step="1" 
                            rows={1} value={this.state.data.serverurl.value}
                                onChange={(e)=>{
                                    this.state.data.serverurl.value=e.target.value
                                    this.setState(this.state)
                                }}
                                valid={this.state.data.connect && this.state.data.valid}
                                invalid={!(this.state.data.connect && this.state.data.valid)}
                                disabled={this.state.data.connect}/>
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1' >
                        <ButtonUni 
                                label={this.state.labels.btn_connect}
                                onClick={()=>{
                                    this.connect()
                                }} 
                                color={"info"}
                                disabled={this.state.data.connect}
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1' ></Col>
                    <Col key='1top' xs='6' sm='6' lg='1' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_help}
                            onClick={()=>{
                                window.open('/api/admin/help/impconfigprocess','_blank').focus()
                            }}
                            color="info"
                        />
                    </Col>
                    <Col key='3top' xs='6' sm='6' lg='1' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_cancel}
                            onClick={()=>{
                                window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                            }}
                            outline
                            color="info"
                        />
                    </Col>
                </Row>
                <Row style={{paddingTop:'30px'}}>
                    <Col>
                        {this.content()}
                    </Col>
                </Row>

            </Container>
        )
    }


}
export default ImportWorkflow