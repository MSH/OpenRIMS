import React , {Component} from 'react'
import {Container, Row, Col, FormText, Card,CardBody,CardHeader} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import SearchControlNew from './utils/SearchControlNew'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'
import ButtonUni from './form/ButtonUni'
import DataCollForm from './dataconfig/DataCollForm'
import DataVarForm from './dataconfig/DataVarForm'
import Downloader from './utils/Downloader'
import ImportDataConfiguration from './ImportDataConfiguration'

/**
 * Configure workflow data
 */
class DataConfigurator extends Component{
    constructor(props){
        super(props)
        this.state={
            showimport:false,
            identifier:Date.now().toString(),
            form:false,
            vars:this.props.vars,
            varForm:false,
            data:{
                nodeId:this.props.nodeId
            },                //DataConfigDTO
            labels:{
                directories:'',
                search:'',
                global_add:'',
                global_cancel:'',
                variables:'',
                preview:'',
                explore:'',
                restricted_edit:'',
                global_help:'',
                global_import_short:'',
                import_electronic_form:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.dataCollTable=this.dataCollTable.bind(this)
        this.left=this.left.bind(this)
        this.dataCollForm=this.dataCollForm.bind(this)
        this.right=this.right.bind(this)
        this.dataVarsTable=this.dataVarsTable.bind(this)
        this.loaderVar=this.loaderVar.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=="formCancel"){
                    this.state.form=false
                    this.loader()
                }
                if(data.subject=="formCollCancel"){
                    this.state.form=false
                    this.state.data.nodeId=0
                    this.loader()
                }
                if(data.subject=='DataConfigurationImportCancel'){
                    this.state.showimport=false
                    this.loader()
                }
            }
            
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.loader()
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Load a list of data collections
     */
    loader(){
        let srch = Fetchers.readLocaly("dataconfig_search", null);
        let api = "/api/admin/data/collections/load/search=" + srch

        srch = Fetchers.readLocaly("dataconfig_vars_search", null);
        api += "/svars=" + srch

        Fetchers.postJSONNoSpinner(api, this.state.data, (query,result)=>{
            this.state.data=result
            if(this.state.data.nodeId==0){
                this.state.vars=false
            }
            this.setState(this.state)
        })
    }
    /**
     * Laod variables for known collection
     */
    loaderVar(){
        Fetchers.postJSONNoSpinner("/api/admin/data/collection/variables/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }
    /**
     * Variables for a data definition
     */
    dataVarsTable(){
        if(this.state.data.varTable == undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
                <Container fluid>
                    <Row>
                        <Col xs='12' sm='12' lg='12' xl='6'>
                            <SearchControlNew label={this.state.labels.search} table={this.state.data.varTable} loader={this.loader}/>
                        </Col>
                       
                        <Col xs='12' sm='12' lg='12' xl='2'>
                            <ButtonUni
                                label={this.state.labels.global_add}
                                onClick={()=>{
                                    this.state.data.varNodeId=0
                                    this.state.form=true
                                    this.state.vars=true
                                    this.setState(this.state)
                                }}
                                color="primary"
                            />
                        </Col>
                        <Col xs='12' sm='12' lg='12' xl='2'>
                            <ButtonUni
                            label={this.state.labels.preview}
                            onClick={()=>{
                               let data={
                                   nodeId:this.state.data.nodeId
                               }
                                let param=JSON.stringify(data)
                                Fetchers.writeLocaly("dataconfig_search", this.state.data.table.generalSearch)
                                Fetchers.writeLocaly("dataconfig_vars_search", this.state.data.varTable.generalSearch)
                                Navigator.navigate("administrate", "dataformpreview",param) 
                            }}
                            color="success"
                            />
                        </Col>
                        <Col xs='12' sm='12' lg='12' xl='2'>
                            <ButtonUni
                                label={this.state.labels.explore}
                                onClick={()=>{
                                    let dl = new Downloader()
                                    dl.postDownload('/api/admin/data/collection/variables/export', this.state.data, "file.bin")
                                }}
                                color="secondary"
                            />
                        </Col>
                       
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable
                                tableData={this.state.data.varTable}
                                loader={this.loaderVar}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                                styleCorrector={(header)=>{
                                    if(["col","row", "ord", "ext"].includes(header)){
                                        return {width:'8%'}
                                    }
                                    if(header=="clazz"){
                                        return {width:'15%'}
                                    }
                                    if(header=="propertyName"){
                                        return {width:'20%'}
                                    }
                                }}
                                linkProcessor={(rowNo, col)=>{
                                    this.state.data.varNodeId=this.state.data.varTable.rows[rowNo].dbID
                                    this.state.form=true
                                    this.state.vars=true
                                    this.setState(this.state)
                                }}
                                
                            />
                        </Col>
                    </Row>
                </Container>
        )
    }

    /**
     * Data collections (directory) table
     */
    dataCollTable(){
        if(this.state.data.table == undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row hidden={this.state.showimport}>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <SearchControlNew label={this.state.labels.search} table={this.state.data.table} loader={this.loader}/>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <ButtonUni
                        label={this.state.labels.global_import_short}
                        onClick={()=>{
                            this.state.showimport=true;
                            this.setState(this.state)
                            Navigator.message(this.state.identifier, "*",'DataConfigurationImportReload',{})
                        }}
                        color="info"
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <ButtonUni
                        label={this.state.labels.global_add}
                        onClick={()=>{
                            this.state.data.nodeId=0
                            this.state.form=true
                            this.state.vars=false
                            this.setState(this.state)
                        }}
                        color="primary"
                        />
                    </Col>
                </Row>
                <Row hidden={!this.state.showimport}>
                    <Col xs='12' sm='12' lg='12' xl='12' >
                        <ImportDataConfiguration
                        // data={undefined}
                            recipient={this.state.identifier}
                            loadapi='/api/admin/data/configuration/load/import'
                            importapi='/api/admin/data/configuration/run/import' 
                        />
                    </Col>
                </Row>
                <Row hidden={this.state.showimport}>
                    <Col>
                        <CollectorTable
                            tableData={this.state.data.table}
                            loader={this.loader}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(header=='identifier'){
                                    return {width:'30%'}
                                }
                            }}
                            linkProcessor={(rowNo, col)=>{
                                this.state.data.nodeId=this.state.data.table.rows[rowNo].dbID
                                this.state.form=true
                                this.state.vars=false
                                this.setState(this.state)
                            }}
                            selectRow={(rowNo)=>{
                                let row = this.state.data.table.rows[rowNo]
                                this.state.data.table.rows.forEach(element => {
                                    if(element.dbID != row.dbID){
                                        element.selected=false
                                    }else{
                                        element.selected=!element.selected
                                    }
                                });
                                if(row.selected){
                                    this.state.vars=true
                                    this.state.form=false
                                    this.state.data.nodeId=row.dbID

                                    this.state.data.varTable.generalSearch = ""
                                    this.state.data.varTable.headers.headers.forEach((th) =>{
                                        th.generalCondition = ""
                                    })
                                    this.loaderVar()
                                }else{
                                    this.state.vars=false
                                    this.state.form=false
                                    this.state.data.nodeId=0
                                    this.setState(this.state)
                                }
                                
                            }}
                        />
                    </Col>
                </Row>
            </Container>
        )
    }
    /**
     * Form to add/edit data collection definition
     */
    dataCollForm(){
        return <DataCollForm nodeId={this.state.data.nodeId} recipient={this.state.identifier} />
    }
    /**
     * Left component
     */
    left(){
        if(this.state.vars){
            return this.dataCollTable()
        }
        if(this.state.form){
            return this.dataCollForm()
        }else{
            return this.dataCollTable()
        }
    }
    /**
     * Right column
     */
    right(){
        if(this.state.vars){
            if(this.state.form){
                return(
                    <DataVarForm nodeId={this.state.data.nodeId} varNodeId={this.state.data.varNodeId} 
                                        recipient={this.state.identifier} restricted={this.state.data.restricted}/>
                )
            }else{
                return this.dataVarsTable()
            }
        }
    }

    render(){
        if(this.state.data.table == undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='9' xl='11'>
                        <h6>{this.state.labels.directories}</h6>
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_help}
                            onClick={()=>{
                                window.open('/api/admin/help/wfrguide','_blank').focus()
                            }}
                            color="info"
                        />
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6'xl='6'>
                        
                        {this.left()} 
                    </Col>
                    <Col xs='12' sm='12' lg='6'xl='6'>
                        <Row>
                            <Col className="d-inline p-2">
                                <h6>
                                <i hidden={this.state.data.restricted} className="fa fa-exclamation-triangle" style={{FontSize:'2em',color: 'tomato'}} aria-hidden="true"></i>
                                {this.state.labels.variables}
                                </h6>
                            </Col>
                        </Row>
                        <Row hidden={this.state.data.restricted}>
                            <Col>
                                <FormText color="muted">
                                    {this.state.labels.restricted_edit}
                                </FormText>
                            </Col>
                        </Row>
                        {this.right()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default DataConfigurator
DataConfigurator.propTypes={
    nodeId:PropTypes.number.isRequired,  //selected data collection, zero is nothing to select
    vars:PropTypes.bool.isRequired,     //show vars table 
}