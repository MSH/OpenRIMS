import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Pharmadex from '../Pharmadex'
import SearchControlNew from '../utils/SearchControlNew'
import ButtonUni from '../form/ButtonUni'
import CollectorTable from '../utils/CollectorTable'
import DataVarForm from './DataVarForm'
import Downloader from '../utils/Downloader'

/**
 * Table to manage electronic form variables.
 * Parameters:
 * ~~~
 *  nodeId :PropTypes.number.isRequired,        //Electronic form configuration node
    recipient :PropTypes.string.isRequired,     //for messageing
 * ~~~
   Example:
   ~~~
   <DataVarTable nodeId={this.state.selectedID}  recipient={this.state.identifier} />
   ~~~
 */
class DataVarTable extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            view:'table',   //what should be rendered - table, form, preview, import
            data:{      //DataConfigDTO.java
                nodeId:this.props.nodeId,
            },
            labels:{
                search:'',
                global_add:'',
                preview:'',
                explore:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.dataVarsTable=this.dataVarsTable.bind(this)
        this.content=this.content.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=='onVariableSave'){
                    this.loader();
                }
                if(data.subject=='formCancel'){
                    this.state.view='table'
                    this.setState(this.state)
                }
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loader()
    }
    componentDidUpdate(){
        if(this.state.data.nodeId != this.props.nodeId){
            this.state.data.nodeId=this.props.nodeId
            this.loader()
        }
    }
    /**
     * load the variables as a table
     */
    loader(){
        Fetchers.postJSONNoSpinner("/api/admin/data/collection/variables/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.state.view='table'
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    dataVarsTable(){
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
                                    this.state.view='form'
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
                                window.open('/admin#administrate/dataformpreview/'+encodeURIComponent(param),'_blank')
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
                                loader={this.loader}
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
                                    this.state.view='form'
                                    this.setState(this.state)
                                }}
                                
                            />
                        </Col>
                    </Row>
                </Container>
        )
    }
    /**
     * 
     * @returns cotent depends on the view
     */
    content(){
            switch(this.state.view){
                case "table":{
                    return(
                        this.dataVarsTable()
                     )
                }
                case "form":{
                    return (
                        <DataVarForm nodeId={this.state.data.nodeId} varNodeId={this.state.data.varNodeId} 
                                recipient={this.state.identifier} restricted={this.state.data.restricted}/>
                    )
    
                }
                default:
                    return []
    
            }
    }
    render(){
        if(this.state.data.varTable == undefined || this.state.labels.locale==undefined){
            return []
        }else{
            return(
                <Container fluid>
                    <Row>
                        <Col>
                            <h5>{this.state.data.url}</h5>
                        </Col>
                    </Row>
                    <Row className='mt-1'>
                        <Col>
                            {this.content()}
                        </Col>
                    </Row>
                </Container>
            )
        }
    }


}
export default DataVarTable
DataVarTable.propTypes={
    nodeId :PropTypes.number.isRequired,        //Electronic form configuration node
    recipient :PropTypes.string.isRequired,     //for messageing
    
}