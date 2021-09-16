import React , {Component} from 'react'
import {Row, Col, Container, Breadcrumb, BreadcrumbItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import CollectorTable from './utils/CollectorTable'
import ButtonUni from './form/ButtonUni'
import SearchControl from './utils/SearchControl'
import Pharmadex from './Pharmadex'
import Navigator from './utils/Navigator'

/** 
* This class represents a level of a dictionary
* The level is all children nodes for a parent node or if parentId='0' - directly under the root.
*
* @example
*    <OrgLevel  
                identifier={"org_"+element}
                url='organization.authority'
                parentId='0'
    />

 */
class DictLevel extends Component{
    constructor(props){
        super(props)
        this.state={
            selectedId:0,
            data:{},                //DictionaryLevel.java
            labels:{
                global_add:'',
                authority:'',
                search:''
            }
        }
        this.load=this.load.bind(this)
        this.selectRow=this.selectRow.bind(this)
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
    }
    componentDidMount(){
        Locales.resolveLabels(this)
        this.load()
    }
    /**
     * Change properties only
     */
    componentDidUpdate(){
        if((this.props.url != this.state.data.url)
         || (this.props.parentId != this.state.data.parentId)
         || (this.props.selectedId != this.state.selectedId)){
             this.state.selectedId=this.props.selectedId
             this.state.data.url=this.props.url
             this.state.data.parentId=this.props.parentId
            this.load()
        }
    }
    /**
     * Load a level
     */
     load(){
        this.state.data.url=this.props.url
        this.state.data.parentId=this.props.parentId
        Fetchers.postJSONNoSpinner("/api/common/dictionary/level/load", this.state.data, (query, result)=>{
            this.state.data=result
            if(this.props.selectedId != undefined){
                let rows = this.state.data.table.rows
                rows.forEach((element,index)=>{
                    if(element.dbID==this.props.selectedId){
                        element.selected=true
                    }else{
                        element.selected=false
                    }
                })
            }
            this.setState(this.state.data)
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

    createBreadCrumb(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.title)){
            this.state.data.title.forEach((title,index)=>{
                ret.push(
                    <BreadcrumbItem key={index}>
                        {title}
                    </BreadcrumbItem>
                )
            })
        }
        return ret
    }

    render(){
        if(this.state.labels.locale == undefined || this.state.data.table == undefined){
            return []
        }
        return(
            <Container fluid>
            <Row>
                <Col>
                    <Breadcrumb>
                        {this.createBreadCrumb()}
                    </Breadcrumb>
                </Col>
            </Row>
            <Row>
                <Col xs='6' sm='6' lg='8' xl='8'>
                    <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.load} />
                </Col>
                <Col xs='6' sm='6' lg='4' xl='4' className="d-flex justify-content-end">
                    <ButtonUni 
                        outline
                        label={this.state.labels.global_add}
                        onClick={()=>{
                            Navigator.message(this.props.identifier,"*","onEdit",
                                {url:this.state.data.url,parentId:this.state.data.parentId,nodeId:0})
                        }}
                    />
                </Col>
            </Row>
            <Row>
                <Col>
                    <CollectorTable
                        tableData={this.state.data.table}
                        loader={this.load}
                        selectRow={(rowNo)=>{
                            let selectedId=0
                            this.selectRow(rowNo)
                            let rows = this.state.data.table.rows
                            if(rows[rowNo].selected){
                                selectedId=rows[rowNo].dbID
                            }
                            this.setState(this.state)
                            Navigator.message(this.props.identifier,"*","onSelect",
                                {url:this.props.url,parentId:this.props.parentId,selectedId:selectedId})
                        }} 
                        linkProcessor={(rowNo,cell)=>{
                            this.selectRow(rowNo)
                            let rows = this.state.data.table.rows
                            rows[rowNo].selected=true
                            Navigator.message(this.props.identifier,"*","onEdit",
                            {url:this.state.data.url,parentId:this.state.data.parentId,nodeId:rows[rowNo].dbID})
                        }}
                        headBackground={Pharmadex.settings.tableHeaderBackground}
                        styleCorrector={(header)=>{
                            if(header=='pref'){
                                return {width:'30%'}
                            }
                        }}
                    />
                </Col>
            </Row>
        </Container>
        )
    }


}
export default DictLevel
DictLevel.propTypes={
    identifier: PropTypes.string.isRequired,        //unique name of the instance of component for messages. 
    url      : PropTypes.string.isRequired,         //URL of the dictionary. It allows parentID=0
    parentId : PropTypes.number.isRequired,         //id of the parent node
    selectedId: PropTypes.number                    //silently mark it as a selected
}