import React , {Component} from 'react'
import {Container,Row, Col, Breadcrumb, BreadcrumbItem, ButtonGroup, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Literals from './Literals'
import Navigator from './utils/Navigator'
import ButtonUni from './form/ButtonUni'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import CollectorTable from './utils/CollectorTable'
import SearchControlNew from './utils/SearchControlNew'
import Pharmadex from './Pharmadex'

/**
 * Container to Edit/add an authority
 * @example
        <Auhority url={data.url} parentId={data.parentId} nodeId={data.nodeId}/>
/>
 */
class Authority extends Component{
    constructor(props){
        super(props)
        this.identifier="Authority"
        this.state={
            keys:[],
            ready:false,
            data:{},
            labels:{
                prefLabel:'',
                label_list:'',
                selectedonly:'',
                responsibilityarea:'',
                search:'',
                add:'',
                save:'',
                cancel:'',
                global_details:'',
                global_suspend:'',
                arearesponsibility:'',
                warningRemove:''
            }
        }
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
        this.cancel=this.cancel.bind(this)
        this.buttons=this.buttons.bind(this)
        this.loadTable=this.loadTable.bind(this)
    }

    createBreadCrumb(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.node.title)){
            this.state.data.node.title.forEach((title,index)=>{
                ret.push(
                    <BreadcrumbItem key={index}>
                        {title}
                    </BreadcrumbItem>
                )
            })
        }
        return ret
    }

    eventProcessor(event){
        let data=event.data
        if(data.to==this.identifier){
            if(data.subject=='onSelectionChange'){
                this.state.data.dictionaries[data.from]=data.data
            }
            if(data.subject=='onLiteralUpdated'){
                this.state.data.node=data.data
            }
        }
    }

    componentDidMount(){
        //create listener
        window.addEventListener("message",this.eventProcessor)
        //load necessary data
        this.state.data.node={}
        this.state.data.node.url=this.props.url
        this.state.data.node.parentId=this.props.parentId
        this.state.data.node.nodeId=this.props.nodeId
        Fetchers.postJSONNoSpinner("/api/admin/organization/load", this.state.data,(query,result)=>{
            Fetchers.setJustLoaded(result,false)
            this.state.data=result;
            Locales.createLabels(this)
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
        this.state.ready=true
        this.setState(this.state)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
 

    cancel(){
        let caller=this.props.caller
        Navigator.navigate(caller.tab,caller.component,caller.parameter)
    }
    /**
     * Buttons toolbar
     */
    buttons(){
        return(
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                </Col>
                <Col xs='12' sm='12' lg='2' xl='2'>
                    <ButtonUni
                            label={this.state.labels.save}
                            onClick={()=>{
                                Fetchers.postJSONNoSpinner("/api/admin/organization/save", this.state.data,(query,result)=>{
                                    if(result.valid){
                                        //return to the caller
                                        let caller=this.props.caller
                                        this.state.data=result
                                        this.cancel()
                                    }else{
                                        this.state.data=result
                                        let mess = this.state.labels.prefLabel +"! " +this.state.data.node.literals.prefLabel.suggest
                                        Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:mess, color:'danger'})
                                        this.setState(this.state)
                                    }
                                })
                            }}
                            color="primary"
                        />
                </Col>
                <Col  xs='12' sm='12' lg='2' xl='2'>
                    <ButtonUni
                            label={this.state.labels.global_suspend}
                            onClick={()=>{
                                Fetchers.alerts(this.state.labels.warningRemove, ()=>{
                                    Fetchers.postJSONNoSpinner("/api/admin/organization/suspend", this.state.data,(query,result)=>{
                                        if(this.state.data.valid){
                                            this.cancel()
                                        }else{
                                            this.state.data=result
                                            this.setState(this.state)
                                        }
                                    })
                                }, null)
                            }}
                            color="warning"
                            disabled={!this.state.data.node.leaf}
                        />
                </Col>
                <Col  xs='12' sm='12' lg='2' xl='2'>
                    <ButtonUni
                        label={this.state.labels.cancel}
                        onClick={this.cancel}
                        color="secondary"
                    />
                </Col>
            </Row>
        )
    }
    /**
     * Load only territory responsible table
     */
    loadTable(){
        Fetchers.postJSONNoSpinner("/api/admin/organization/load/responsibility", this.state.data,(query,result)=>{
            Fetchers.setJustLoaded(result,false)
            this.state.data=result;
            this.setState(this.state)
        })
    }

    render(){
        if(!this.state.ready || this.state.labels.locale==undefined){
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
            {this.buttons()}
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                <Literals identifier="node" url={this.props.url} parentId={this.props.parentId} nodeId={this.props.nodeId}
                    recipient={this.identifier} />
                </Col>
                <Col  xs='12' sm='12' lg='6' xl='6'>
                    <Row>
                        <Col>
                            <h5>{this.state.labels.responsibilityarea}</h5>
                        </Col>
                    </Row>
                    <Row className='mt-1 mb-1'>
                        <Col xs='12' sm='12' lg='8' xl='8'>
                            <SearchControlNew label={this.state.labels.search} table={this.state.data.table} loader={this.loadTable} />
                        </Col>
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <ButtonGroup size="sm">
                                <Button
                                    outline
                                    color='success'
                                    active={this.state.data.all}
                                    onClick={()=>{
                                       this.state.data.all=true
                                       this.loadTable()
                                    }}
                                >
                                    {this.state.labels.label_list}
                                </Button>
                                <Button
                                    outline
                                    color='info'
                                    active={!this.state.data.all}
                                    onClick={()=>{
                                        this.state.data.all=false
                                        this.loadTable()
                                    }}
                                >
                                    {this.state.labels.selectedonly}
                                </Button>
                            </ButtonGroup>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                        <CollectorTable
                            tableData={this.state.data.table}
                            loader={this.loadTable}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            selectRow={ (rowNo)=>{
                                    let row=this.state.data.table.rows[rowNo]
                                    row.selected=!row.selected;
                                    this.state.data.rowId=row.dbID;
                                    Fetchers.postJSON("/api/admin/organization/load/responsibility/select", this.state.data, (query,result)=>{
                                        this.state.data=result
                                        this.setState(this.state)
                                    })
                                }
                            }
                        />
                        </Col>
                    </Row>
                </Col>
            </Row>
            {this.buttons()} 
          </Container>
        )
    }


}
export default Authority
Authority.propTypes={
    url:PropTypes.string.isRequired,        //url to find the root if parent and id both are zero
    parentId:PropTypes.number.isRequired,   //id of a node of the parent organization. 0 means add to the root
    nodeId:PropTypes.number.isRequired,     //id of a node of this organization. 0 means add to the parent
    caller:PropTypes.shape({
        tab:PropTypes.string.isRequired,
        component:PropTypes.string.isRequired,
        parameter:PropTypes.string
        }).isRequired                       //caller component navigation
}