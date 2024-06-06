import React , {Component} from 'react'
import {Container, Row, Col, Breadcrumb, BreadcrumbItem, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import CollectorTable from './utils/CollectorTable'

/**
 * Assist to uild EL
 */
class ELAssistantBuildWF extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{//ELAssistantBuildDTO.java
                workflowURL:this.props.workflowURL,
                el:''
            },
            identifier:Date.now().toString(),
            labels:{
                datasource:'',
                datarepresentation:'',
                global_home:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
        this.breadcrumb=this.breadcrumb.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        Locales.createLabels(this)
        this.load()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    load(){
        Fetchers.postJSON("/api/admin/el/assitant/build", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
            Navigator.message(this.state.identifier, this.props.recipient, "ELAssistant_EL", {el:this.state.data.el, 
                ready:this.state.data.representation.length>0, thisPageURL:this.state.data.thisPageURL})
        })
    }
    /**
     * build sources breadcrumb
     */
    breadcrumb(){
        let ret=[]
        this.state.data.source.forEach((source,index)=>{
                if(source=='/'){
                    source=this.state.labels.global_home
                }
                ret.push(
                    <BreadcrumbItem key={source+index}>
                        <div className="btn btn-link p-0 border-0" style={{fontSize:'0.8rem'}}
                            onClick={()=>{
                                this.state.data.representation=''
                                this.state.data.el=''
                                if(index>0){
                                    this.state.data.source=this.state.data.source.slice(0,index)
                                    this.state.data.clazz=this.state.data.clazz.slice(0,index)
                                }else{
                                    this.state.data.source=[]
                                    this.state.data.clazz=[]
                                }
                                this.load()
                            }}
                        >
                            {source}
                        </div>
                    </BreadcrumbItem>
                )
        })
        return ret
    }

    render(){
        if(this.state.data.sources==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait();
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='4'>
                                <h5>{this.state.labels.datasource}</h5>
                            </Col>
                            <Col xs='12' sm='12' lg='12' xl='8'>
                                <Breadcrumb hidden={this.state.data.source.length==0}>
                                    {this.breadcrumb()}
                                </Breadcrumb>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.sources}
                                    loader={this.load}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    selectRow={(rowNo)=>{
                                        let key=''
                                        let clazz=''
                                        this.state.data.sources.rows[rowNo].row.forEach(row => {
                                            if(row.key=="key"){
                                                key=row.value
                                            }
                                            if(row.key=="clazz"){
                                                clazz=row.value
                                            }
                                        });
                                        if(clazz.length==0){
                                            clazz=key   // for roots
                                        }
                                        this.state.data.source.push(key)
                                        this.state.data.clazz.push(clazz)
                                        this.state.data.sources.headers.headers=[]   // drop headers
                                        this.load()
                                    }}
                                />
                            </Col>
                        </Row>
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col xs='12' sm='12' lg='10' xl='10'>
                                <h5>{this.state.labels.datarepresentation}</h5>
                            </Col>
                            <Col xs='12' sm='12' lg='2' xl='2'className="d-flex justify-content-end p-0 m-0">
                                <Button
                                    size='lg'
                                    className="p-0 m-0"
                                    color="link"
                                    onClick={()=>{
                                        window.open('/api/admin/help/elassistant','_blank').focus()
                                    }}
                                >
                                    <i className="far fa-question-circle"></i>
                                </Button>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable 
                                    tableData={this.state.data.representations}
                                    loader={this.load}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    selectRow={(rowNum)=>{
                                        this.state.data.representation=''
                                        this.state.data.representations.rows.forEach((row,num)=>{
                                            if(num==rowNum){
                                                row.selected=!row.selected
                                                if(row.selected){
                                                    row.row.forEach((cell)=>{
                                                        if(cell.key=='key'){
                                                            this.state.data.representation=cell.value
                                                        }
                                                    })
                                                }
                                            }else{
                                                row.selected=false
                                            }
                                        })
                                        this.load()
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
export default ELAssistantBuildWF
ELAssistantBuildWF.propTypes={
    workflowURL: PropTypes.string.isRequired,   //URL of the Certification Workflow selected
    recipient :PropTypes.string                 //ELAssistant component for messaging
}