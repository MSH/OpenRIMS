import React , {Component} from 'react'
import {Container, Row, Col, Alert, Breadcrumb, BreadcrumbItem, FormText} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import SearchControl from './utils/SearchControl'
import CollectorTable from './utils/CollectorTable'
import Dictionary from './Dictionary'
import ApplicationData from './ApplicationData'
import Pharmadex from './Pharmadex'

/**
 * Responsible to manage links between objects, using a classifier
 *  e.g. Medicinal Product->Final->Manufacturer
 */
class Links extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            labels:{
                search:'',
            },
            data:props.data,
            objects:true,   // display objects or classifiers for selected object
            thing:{         // expand object in read only mode
                nodeId:0
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.paintTable=this.paintTable.bind(this)
        this.paintClassifiers=this.paintClassifiers.bind(this)
        this.allSelected=this.allSelected.bind(this)
        this.currentSelected=this.currentSelected.bind(this)
        this.loadObjects=this.loadObjects.bind(this)
        this.selectRow=this.selectRow.bind(this)
        this.readOnly=this.readOnly.bind(this)
    }
    /**
     * Place Links to a thing
     * @param {LinksDTO} data to build links 
     * @param {int} index - unique key for ReactJS
     * @param {*} readOnly 
     * @param {*} recipient - the thing (parent component)
     * @param {*} label - typically, the name of variable is an i18 key 
     * @returns 
     */
    static place(data, recipient, readonly, index, label){
        if(data==undefined){
            return []
        }
        //help or error message
        let color="info"
        if(data.strict){
            color="danger"
        }
        return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{label}</h6>
                        </Col>
                    </Row>
                    <Row hidden={data.valid}>
                        <Col>
                            <Alert color={color} className="p-0 m-0">
                                <small>{data.identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                    <Row>
                        <Links data={data} recipient={recipient} readOnly={readonly || data.readOnly} />
                    </Row>
                </Col>
            </Row>
        )
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=='onApplicationDataClose'){
                    this.state.thing.nodeId=0
                    Navigator.message(this.state.identifier, '*', 'openApplicationData', this.state.thing)
                }
            }
            if(data.from=='links_dict'){
                if(data.subject=='onSelectionChange'){
                    this.state.data.selectedLink.dictDto=data.data
                    Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/links/dictionary/select", this.state.data, (query, result)=>{
                        this.state.data=result
                        this.state.objects=this.state.data.selectedLink.objectID==0   //display list of objects or a dictionary
                        this.setState(this.state)
                        Navigator.message(this.state.identifier, this.props.recipient, 'onSelectionChange', this.state.data)
                    })
                }
            }
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Paint a table contains objects or classifiers
     */
    paintTable(){
        if(this.state.objects){
            return this.paintObjects()
        }else{
            return this.paintClassifiers()
        }
    }

    /**
     * Reload objects table
     */
    loadObjects(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/links/table", this.state.data, (query,result)=>{
            this.state.data=result
            this.state.data.selectedObj=0
            this.setState(this.state)
            //Navigator.message(this.state.identifier, this.props.recipient, 'onSelectionChange', this.state.data)
        })
    }
    selectRow(rowNo){
        this.state.data.selectedObj=this.state.data.table.rows[rowNo].dbID
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/links/selectrow", this.state.data, (query, result)=>{
            this.state.data=result
            this.state.data.selectedObj=0
            this.state.objects=this.state.data.selectedLink.objectID==0   //the dictionary item, if one
            this.setState(this.state)
            Navigator.message(this.state.identifier, this.props.recipient, 'onSelectionChange', this.state.data)
        })
        
    }
    /**
     * Paint a table with objects
     */
    paintObjects(){
        return (
            <Container fluid>
                <Row>
                    <Col>
                        <CollectorTable
                            tableData={this.state.data.table}
                            loader={this.loadObjects}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(header=='pref'){
                                    return {width:'30%'}
                                }
                            }}
                            linkProcessor={(rowNo,cellNo)=>{
                                    this.selectRow(rowNo)
                                }
                            }
                            selectRow={(rowNo)=>{
                                this.selectRow(rowNo)
                            }}
                        />
                    </Col>
                </Row>
            </Container>
        )
    }
    /**
     * paint a table with classifiers
     */
    paintClassifiers(){
        return (
            <Dictionary identifier='links_dict' recipient={this.state.identifier} data={this.state.data.selectedLink.dictDto} display noborder/>
        )
    }
    /**
     * Display all selected objects
     */
    allSelected(){
        let ret=[]
        let className="btn btn-link p-0 border-0"
        if(this.state.data.readOnly|| this.props.readOnly){
            className='d-inline-flex'
        }
        if(Fetchers.isGoodArray(this.state.data.links)){
            this.state.data.links.forEach((link,index) => {
                ret.push(
                    <Breadcrumb key={this.state.identifier+index}>
                        <BreadcrumbItem key={index*100+this.state.identifier}>
                            <div className={className} style={{fontSize:'0.8rem'}}
                                onClick={()=>{
                                  this.state.data.selectedObj=link.objectID
                                  this.loadObjects()
                                }}
                            >
                                {link.objectLabel}
                            </div>
                        </BreadcrumbItem>
                        <BreadcrumbItem key={index*100+1+this.state.identifier}>
                                {link.dictLabel}
                        </BreadcrumbItem>
                    </Breadcrumb>
                )
            });
        }
        return ret
    }
    /**
     * Display only currently selected object
     */
    currentSelected(){
        return []
    }
    /**
     * read only data rows
     */
    readOnlyRows(){
        let ret=[]
        let className="btn btn-link p-0 border-0"
        if(Fetchers.isGoodArray(this.state.data.links)){
            this.state.data.links.forEach((link,index) => {
                ret.push(
                    <Breadcrumb key={index}>
                        <BreadcrumbItem key={index*100}>
                        <div className={className} style={{fontSize:'0.8rem'}}
                                onClick={()=>{
                                 this.state.thing.nodeId=link.objectID
                                 Navigator.message(this.state.identifier, '*', 'openApplicationData', this.state.thing)
                                }}
                            >
                                {link.objectLabel}
                            </div>
                        </BreadcrumbItem>
                        <BreadcrumbItem key={index*100+1}>
                            {link.dictLabel}
                        </BreadcrumbItem>
                    </Breadcrumb>
                )
            })
        }
        return ret;
    }
    /**
     * Read only representation
     */
    readOnly(){
            return(
                <Container fluid >
                     <ApplicationData data={this.state.thing} recipient={this.state.identifier} narrow modal/>
                    {this.readOnlyRows()}
                </Container>
            )
    }

    render(){
        if(this.state.labels.locale==undefined){
            return []
        }
        if(this.props.readOnly){
            return(
                this.readOnly()
            )

        }else{
            return(
                <Container fluid className={Pharmadex.settings.activeBorder}>
                    <Row hidden={!this.state.objects} className='m-2'>
                        <Col>
                            <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.loadObjects}/>
                        </Col>
                        <Col>
                            {this.allSelected()}
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <FormText color="muted">
                                {this.state.data.description}
                            </FormText>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <h4 className='ml-3'>{this.state.data.selectedLink.objectLabel}</h4>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {this.paintTable()}
                        </Col>
                    </Row>
                </Container>
            )
        }
    }


}
export default Links
Links.propTypes={
    data:PropTypes.object.isRequired,        //LinksDTO
    recipient:PropTypes.string.isRequired,   //recipient for messaging
    readOnly:PropTypes.bool
}