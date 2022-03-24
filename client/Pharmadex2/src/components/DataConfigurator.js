import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import SearchControl from './utils/SearchControl'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'
import ButtonUni from './form/ButtonUni'
import DataCollForm from './dataconfig/DataCollForm'
import DataVarForm from './dataconfig/DataVarForm'

/**
 * Configure workflow data
 */
class DataConfigurator extends Component{
    constructor(props){
        super(props)
        this.state={
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
        Fetchers.postJSONNoSpinner("/api/admin/data/collections/load", this.state.data, (query,result)=>{
            this.state.data=result
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
                        <Col xs='12' sm='12' lg='6' xl='6'>
                            <SearchControl label={this.state.labels.search} table={this.state.data.varTable} loader={this.loader}/>
                        </Col>
                        <Col xs='12' sm='12' lg='4' xl='2'>
                        </Col>
                        <Col xs='12' sm='12' lg='4' xl='2'>
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
                        <Col xs='12' sm='12' lg='2' xl='2'>
                            <ButtonUni
                            label={this.state.labels.preview}
                            onClick={()=>{
                               let data={
                                   nodeId:this.state.data.nodeId
                               }
                                let param=JSON.stringify(data)
                                Navigator.navigate("administrate", "dataformpreview",param) 
                            }}
                            color="success"
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
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.loader}/>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
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
                <Row>
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
                    <DataVarForm nodeId={this.state.data.nodeId} varNodeId={this.state.data.varNodeId} recipient={this.state.identifier} />
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
                    <Col xs='12' sm='12' lg='11' xl='11'>
                    </Col>
                    <Col className="d-flex justify-content-end" xs='12' sm='12' lg='1' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_cancel}
                            onClick={()=>{
                                window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                            }}
                            color="info"
                        />
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6'xl='6'>
                        <Row>
                            <Col>
                                <h6>{this.state.labels.directories}</h6>
                            </Col>
                        </Row>
                        {this.left()} 
                    </Col>
                    <Col xs='12' sm='12' lg='6'xl='6'>
                        <Row>
                            <Col>
                                <h6>{this.state.labels.variables}</h6>
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
    vars:PropTypes.bool.isRequired       //show vars table 
}