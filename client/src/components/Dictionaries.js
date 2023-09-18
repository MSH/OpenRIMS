import React , {Component} from 'react'
import {Container, Row, Col, Button, Label} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import ButtonUni from './form/ButtonUni'
import Dictionary from './Dictionary'
import RootNode from './RootNode'
import SearchControlNew from './utils/SearchControlNew'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'
import Navigator from './utils/Navigator'

/**
 * All dictionaries in use with the possibility to add new ones
 */
class Dictionaries extends Component{
    constructor(props){
        super(props)
        this.state={
            showEditForm:false, //select Row by edit data (url, label, desc) Dictionary
            showListForm:false, // onClick Row by show (edit, add, suspend) all Dictionary items
            data:{},
            labels:{
                label_edit:'',
                label_view:'',
                newdictionary:'',
                dictionaries:"",
                search:"",
                global_cancel:"",
                global_help:''
            }
        }
        this.load=this.load.bind(this)
        this.buildTable=this.buildTable.bind(this)
        this.buildForm=this.buildForm.bind(this)
    }
    componentDidMount(){
        this.load()
    }
    load(){
        Fetchers.postJSONNoSpinner("/api/admin/dictionary/all", this.state.data, (query,responce)=>{
            this.state.data=responce
            this.setState(this.state.data)
            Locales.resolveLabels(this)
        })
    }

    /**
     * Select/deselect a row
     * @param {number} rowNo number of row 
     */
    selectRow(rowNo){
        let rows = this.state.data.table.rows
        let selected=rows[rowNo].selected
        rows.forEach(row => {
            row.selected=false
        });
        rows[rowNo].selected=!selected
    }

    buildTable(){
        return(
            <Row>
                <Col>
                    <Row>
                        <Col xs='12' sm='12' lg='6' xl='6'>
                            <SearchControlNew label={this.state.labels.search} table={this.state.data.table} loader={this.load}/>
                        </Col>
                        <Col xs='12' sm='12' lg='3' xl='3'>
                        </Col>
                        <Col xs='12' sm='12' lg='3' xl='3'>
                            <ButtonUni
                                label={this.state.labels.newdictionary}
                                onClick={()=>{
                                    this.state.data.selectId = 0;
                                    this.state.data.editor = true
                                    this.state.showEditForm = true
                                    this.state.showListForm=false
                                    this.load()
                                }}
                                color="primary"
                                />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable
                                    tableData={this.state.data.table}
                                    loader={this.load}
                                    linkProcessor={(rowNo,cellNo)=>{
                                        this.state.data.selectId = this.state.data.table.rows[rowNo].dbID;
                                        this.state.data.editor = true
                                        this.state.showEditForm = true
                                        this.state.showListForm=false
                                        this.load()
                                    }}
                                    selectRow={(rowNo)=>{
                                        let selectedId=0
                                        this.selectRow(rowNo)
                                        let rows = this.state.data.table.rows
                                        if(rows[rowNo].selected){
                                            selectedId=rows[rowNo].dbID
                                        }
                                        this.state.data.selectId = selectedId;
                                        this.state.data.editor = false
                                        this.state.showEditForm = false
                                        this.state.showListForm=true
                                        this.load()
                                    }}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    styleCorrector={(header)=>{
                                        if(header=='prefLabel'){
                                            return {width:'25%'}
                                        }
                                    }}
                                />
                        </Col>
                    </Row>
                </Col>
            </Row>
        )
    }

    buildForm(){
        if(this.state.showEditForm){
            return(
                <RootNode
                        identifier={this.state.data.select.url}
                        rootId={this.state.data.select.urlId}
                        onCancel={()=>{
                            this.state.showEditForm=false
                            this.state.showListForm=false
                            this.state.data.editor=false
                            this.state.data.selectId = -1
                            this.load()
                        }}
                />
            )
        }else if(this.state.showListForm){
            return(
                <Row>
                    <Col xs='11' sm='11' lg='11' xl='11' >
                        <Dictionary identifier={this.state.data.select.url} data={this.state.data.select} />
                    </Col>
                    <Col xs='1' sm='1' lg='1' xl='1' >
                        <Button close
                                onClick={()=>{
                                    this.state.showEditForm=false
                                    this.state.showListForm=false
                                    this.state.data.editor=false
                                    this.state.data.selectId = -1
                                    this.load()
                                }} 
                            />
                    </Col>
                </Row>
            )
        }else
            return []
    }

    render(){
        if(this.state.data == undefined || this.state.labels.locale == undefined || 
            this.state.data.table == undefined){
            return []
        }
        return(
            /*<Container fluid>
                <Row className="pb-5">
                    <Col xs='12'sm='12' lg='10' xl='10'>
                    </Col>
                    <Col xs='12'sm='12' lg='2' xl='2'>
                        <ButtonUni
                            label={this.state.labels.newdictionary}
                            color="primary"
                            onClick={()=>{
                                this.state.data.selectId = 0
                                this.state.data.editor = true
                                this.state.showEditForm = true
                                this.state.showListForm=false
                                this.load()
                            }}
                        />
                    </Col>
                </Row>
                {this.activeComponent()}
            </Container>*/
            <Container fluid>
                <Row>
                    <Col xs='12'sm='12' lg='10' xl='10' className="d-flex justify-content-center">
                        <h6>{this.state.labels.dictionaries}</h6>
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_help}
                            onClick={()=>{
                                window.open('/api/admin/help/dictionaries','_blank').focus()
                            }}
                            color="info"
                        />
                    </Col>
                    <Col xs='12'sm='12' lg='1' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_cancel}
                            onClick={()=>{
                                window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                            }}
                            color="info"
                            outline
                        />
                    </Col>
                </Row>
                <Row className="pb-1">
                    <Col xs={12} sm={7} lg={7} xl={7}>
                        {this.buildTable()}
                    </Col>
                    <Col xs={12} sm={5} lg={5} xl={5}>
                        <Label></Label>
                        {this.buildForm()}
                    </Col>
                </Row>
            </Container>
        )
    }
}
export default Dictionaries
Dictionaries.propTypes={
    
}