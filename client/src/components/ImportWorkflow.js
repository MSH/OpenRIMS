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
import AsyncInform from './AsyncInform'

/**
 * Import addresses
 */
class ImportWorkflow extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{
                serverurl:{value:""}
            },
            showProgress:false,
            showResultTable:false,
            labels:{
                exchangeconfig:'',
                serverurl:'',
                btn_connect:'',
                processes:'',
                statusimport:'',
                workflows:'',
                global_cancel:"",
                global_help:'',
                btn_run:'',
                selectedonly:'',
                search:'',
                importwf:'',
                processeError:''
            },
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.getStyleConnection=this.getStyleConnection.bind(this)
        this.proccesTables=this.proccesTables.bind(this)
        this.connectLoadProccess=this.connectLoadProccess.bind(this)
        this.runImport=this.runImport.bind(this)
        this.load = this.load.bind(this)
        this.loadResultTable=this.loadResultTable.bind(this)
        this.resultTable=this.resultTable.bind(this)
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.comparator = new FieldsComparator(this)
       // this.state.data.identifier = Fetchers.readLocaly("mainserveraddress", "")
        this.state.data.serverurl.value = Fetchers.readLocaly("mainserveraddress", "")
        this.load()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            if(data.to== this.state.identifier && data.subject=='OnAsyncProcessCompleted'){
                this.state.showProgress=false
                this.state.showResultTable = true
                this.connectLoadProccess()
            }
            if(data.to== this.state.identifier && data.subject=='OnAsyncProcessCancelled'){
                this.state.showResultTable = true
                this.state.showProgress=true
                this.loadResultTable()
            }
            if(data.to== this.state.identifier && data.subject=='OnAsyncProcessEnd'){
                this.state.showResultTable = true
                this.state.showProgress=true
                this.loadResultTable()
            }
        }
    }

    getStyleConnection(){
        var color = "red"
        if(this.state.data.valid){
            color = 'green'
        }
        return color
    }

    /**
     * load start data (on click menu item)
     */
    load(){
        Fetchers.postJSON("/api/admin/importwf/load", this.state.data, (query, result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    /**
     * first connect to main server
     * load data to proccess tables
     */
    connectLoadProccess(){
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

    /**
     * run Async import procces
     */
    runImport(){
        this.state.showResultTable = false
        Fetchers.postJSON("/api/admin/importwf/runimport", this.state.data, (query, result)=>{
            this.state.data = result
            if(this.state.data.valid){
                Navigator.message('*', '*', 'show.alert.pharmadex.2', this.state.labels.startImport)
                this.state.showProgress=true
            }else{
                this.state.showProgress=false
            }
            this.setState(this.state)
        })
    }

    /**
     * load relust table data
     */
    loadResultTable(){
        Fetchers.postJSON("/api/admin/importwf/loadresult", this.state.data, (query, result)=>{
            this.state.data = result
            
            this.setState(this.state)
        })
    }

    /**
     * build Rows with tables proccesses
     */
    proccesTables(){
        let ret = []
        if(this.state.data.connect){
            if(this.state.data.procTable == undefined || this.state.data.wfTable == undefined){
                return Pharmadex.wait()
            }
            ret.push(
                <Row key='iw0'>
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
                                            this.connectLoadProccess()
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
                                    hidden={this.state.data.wfIDselect == 0}
                            />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )
            ret.push(
                <Row key='iw1'>
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
                                if(Fetchers.isGoodArray(this.state.data.procTable.rows)){
                                    this.state.data.procTable.rows.forEach((r,index) => {
                                        r.selected = false
                                        if(index == rowNo){
                                            r.selected = !r.selected
                                        }
                                    });
                                }
                                this.state.showResultTable = false
                                this.connectLoadProccess()
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
                                if(Fetchers.isGoodArray(this.state.data.wfTable.rows)){
                                    this.state.data.wfTable.rows.forEach((r,index) => {
                                        r.selected = false
                                        if(index == rowNo){
                                            r.selected = !r.selected
                                        }
                                    });
                                }
                                this.state.showResultTable = false
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
        }
        return ret
    }

    resultTable(){
        let ret = []
        if(this.state.showResultTable){
            ret.push(
                <Row key='iw3'>
                    <Col xs='12' sm='12' lg='12' xl='12'>
                        <Label>{this.state.labels.statusimport}</Label>
                    </Col>
                </Row>
            )
            ret.push(
                <Row key='iw4'>
                    <Col xs='12' sm='12' lg='12' xl='12'>
                        <Label>{this.state.data.titleResultTable}</Label>
                    </Col>
                </Row>
            )
            ret.push(
                <Row key='iw5'>
                    <Col xs='12' sm='12' lg='12' xl='12'>
                        <CollectorTable
                                    tableData={this.state.data.statusTable}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    loader={this.loadResultTable}
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
        return ret
    }

    render(){
        if((this.state.data.serverurl==undefined && this.state.showProgress==false) || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        if(this.state.showProgress){
            let ret = []
            ret.push(
                <AsyncInform recipient={this.state.identifier} loadAPI='/api/admin/importwf/progress'/>
            )
            ret.push(this.resultTable())
            return (
                ret
            )
        }else{
            return(
                <Container fluid>
                    <Row style={{alignItems:'center'}}>
                        <Col xs='12' sm='12' lg='3' xl='3'>
                            <h4>
                                {this.state.labels.exchangeconfig}
                            </h4>
                        </Col>
                        <Col xs='12' sm='12' lg='7' xl='7' ></Col>
                        <Col key='1top' xs='6' sm='6' lg='1' xl='1'>
                            <ButtonUni
                                label={this.state.labels.global_help}
                                onClick={()=>{
                                    Fetchers.openWindowHelp('/api/admin/help/impconfigprocess')
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
                    <Row style={{alignItems:'center'}}>
                        <Col xs='12' sm='12' lg='2' xl='2'>
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
                        <Col xs='12' sm='12' lg='2' xl='2' >
                            <ButtonUni 
                                    label={this.state.labels.btn_connect}
                                    onClick={()=>{
                                        this.connectLoadProccess()
                                    }} 
                                    color={"info"}
                                    disabled={this.state.data.connect}
                            />
                        </Col>
                        <Col xs='12' sm='12' lg='4' xl='4' ></Col>
                    </Row>
                    <Row style={{paddingTop:'30px'}}>
                        <Col>
                            {this.proccesTables()}
                        </Col>
                    </Row>
                    {this.resultTable()}
                </Container>
            )
        }
    }


}
export default ImportWorkflow