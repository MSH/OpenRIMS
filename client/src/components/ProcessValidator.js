import React , {Component} from 'react'
import {Container, Row, Col,Collapse} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import ButtonUni from './form/ButtonUni'
import ViewEdit from './form/ViewEdit'
import CollectorTable from './utils/CollectorTable'
import DataVarTable from './dataconfig/DataVarTable'
import DataCollForm from './dataconfig/DataCollForm'
import ResourceFilling from './ResourceFilling'
import Dictionary from './Dictionary'

/**
 * Process validator
 * issue message "onProcessValidatorClose"
 */
class ProcessValidator extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            selectedID:0,
            url:'',
            data:{
                dictNodeID:this.props.dictNodeID
            },    //ProcessComponentsDTO.java
            dictData:{}, 
            collapse:[false,false,false,false],
            labels:{
                reload:'',
                global_help:'',
                global_close:'',
                global_details:'',
                pages:'',
                resources:'',
                dictionaries:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.leftColumn=this.leftColumn.bind(this)
        this.header=this.header.bind(this)
        this.toggle=this.toggle.bind(this)
        this.collapsator=this.collapsator.bind(this)
        this.load=this.load.bind(this)
        this.table=this.table.bind(this)
        this.tableHasFalse=this.tableHasFalse.bind(this)
        this.rightColumn=this.rightColumn.bind(this)
        this.dataFormConfiguration=this.dataFormConfiguration.bind(this)
        this.resource=this.resource.bind(this)
        this.dictionary=this.dictionary.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=='formCollCancel'){
                    this.state.selectedID=0
                    this.state.url=''
                    this.setState(this.state)
                }
                if(data.subject=='formCollSave'){
                    this.state.selectedID=data.data.nodeId
                    this.state.url=data.data.url.value
                    this.setState(this.state)
                }
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.load()
    }
    /**
     * Load process data
     */
    load(){
        this.state.data.dictNodeID=this.props.dictNodeID
        Fetchers.postJSON("/api/admin/process/components", this.state.data, (query,result)=>{
            this.state.data=result
            Locales.createLabels(this)
            Locales.resolveLabels(this)
            this.setState(this.state)
        })

    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    header(){
        return(
            <Row>
                <Col xs='12' sm='12' lg='6' xl='9'>
                    <Row>
                        <Col>
                            <h4>{this.state.data.applName.value}</h4>
                        </Col>
                    </Row>

                </Col>
                <Col xs='12' sm='12' lg='2' xl='1'>
                    <ButtonUni
                        label={this.state.labels.reload}
                        color="primary"
                        onClick={()=>{
                            this.load()
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='2' xl='1'>
                    <ButtonUni
                        label={this.state.labels.global_help}
                        color="info"
                        onClick={()=>{
                            Fetchers.openWindowHelp('/api/admin/process/validator/manual')
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='2' xl='1'>
                    <ButtonUni
                        label={this.state.labels.global_close}
                        color="secondary"
                        outline
                        onClick={()=>{
                            Navigator.message(this.state.identifier, this.props.recipient, "onProcessValidatorClose", {})
                        }}
                    />
                </Col>
            </Row>
        )
    }
    /**
     * which section should be opened?
     */
    toggle(ind) {
        if(this.state.data != undefined ){
            if(Fetchers.isGoodArray(this.state.collapse)){
                this.state.collapse.forEach((el, i)=>{
                    if(ind == i){
                        this.state.collapse[i] = !this.state.collapse[i]
                    }else{
                        this.state.selectedID=0
                        this.state.collapse[i]=false
                    }
                })
            }
            this.setState(this.state);
        }
    }
    /**
     * collapse control
     */
    collapsator(label, color, index){
        let awe="fas fa-caret-right"
        if(this.state.collapse[index]){
            awe="fas fa-caret-down"
        }
        return(
        <div className="btn btn-link p-0 border-0 d-flex justify-content-start"
            onClick={()=>{
                this.toggle(index)
            }}>
                <h4 style={{color:color}} className="ml-3"><i className={awe}></i>{label}</h4>
        </div>
        )
    }
    /**
     * any FormFieldDTO has an error
     */
    detailsHasErrors(){
        for(const property in this.state.data){
            if(this.state.data[property].error){
                return true
            }
        }
        return false
    }
    /**
     * expandable section details
     */
    details(){
        let color='#3eaf3c'
        if(this.detailsHasErrors()){
            color='#c53707'
        }
        return(
            <Col>
                {this.collapsator(this.state.labels.global_details,color,0)}
                <Collapse isOpen={this.state.collapse[0]} >
                    <Row>
                        <Col>
                            <ViewEdit mode='text' attribute="dictName" component={this} hideEmpty />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='text' attribute="dictURL" component={this} hideEmpty />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='textarea' attribute="dictDescr" component={this} hideEmpty />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='text' attribute="applURL" component={this} hideEmpty />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='textarea' attribute="applDescr" component={this} hideEmpty />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='textarea' attribute="acceptActivity" component={this} />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='textarea' attribute="declineActivity" component={this} />
                        </Col>
                    </Row>
                </Collapse>
            </Col>
        )
    }
    /**
     * At least one value in a column dataconfigurator (boolean) equilals false
     * Thus, this expandable section will require attention
     * @param {table data} data
     * @returns color of the header - green or red
     */
    tableHasFalse(data){
        if(Fetchers.isGoodArray(data.rows)){
            let color='#3eaf3c'
            data.rows.forEach((row)=>{
                if(row.row[1].originalValue==false){
                    color='#c53707'
                }
            })
            return color
        }else{
            return '#c53707'
        }
    }
    /**
     * Place a table with columns url (string) and dataconfigurator (boolean) in the expandable section
     * @param {table data} data 
     * @param {expandable} header 
     * @param {index in the collapse array} index 
     * @returns 
     */
    table(data, header,index){
        return(
            <Col>
                {this.collapsator(header,this.tableHasFalse(data),index)}
                <Collapse isOpen={this.state.collapse[index]} >
                    <CollectorTable
                        tableData={data}
                        loader={this.load}
                        headBackground={Pharmadex.settings.tableHeaderBackground}
                        styleCorrector={(key)=>{
                            if(key=='url'){
                                return {width:'90%'}
                            }else{
                                return {width:'10%'}
                            }
                        }}
                        linkProcessor={(row,cell)=>{
                            this.state.url=data.rows[row].row[0].value  //the only column in any table should be URL
                            let api=''
                            switch(index){
                                case 1:
                                    api='/api/admin/data/config/nodeid'
                                    break
                                case 2:
                                    api='/api/admin/resources/nodeid'
                                    break
                                case 3:
                                    api='/api/admin/dictionaries/nodeid'
                                    break
                                default:
                            }
                            if(api.length>0){
                                Fetchers.postJSON(api,{url:this.state.url,nodeId:0},(query,result)=>{
                                    this.state.selectedID=result.nodeId
                                    this.state.dictData=result.dict
                                    this.setState(this.state)
                                })
                            }
                        }}
                    />
                </Collapse>
            </Col>
        )
    }
    /**
     * content in the left column
     */
    leftColumn(){
        return(
            <Container fluid>
                <Row>
                    <Col>
                        {this.details()}
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.table(this.state.data.dataConfigurations, this.state.labels.pages,1)}
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.table(this.state.data.resources, this.state.labels.resources,2)}
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.table(this.state.data.dictionaries, this.state.labels.dictionaries,3)}
                    </Col>
                </Row>
            </Container>
        )
    }

    /**
     * Serve configuration of electronic forms
     */
    dataFormConfiguration(){
        if(this.state.selectedID>0){
            return(
                <DataVarTable nodeId={this.state.selectedID}  recipient={this.state.identifier} />
            )
        }else{
            return(
                <DataCollForm nodeId={0} url={this.state.url} recipient={this.state.identifier} />
            )
        }

    }
resource(){
    if(this.state.selectedID>0){
    return(<ResourceFilling
                        nodeId={this.state.selectedID}
                        recipient={this.state.identifier}
                        />)
    }else{
        return[]
    }
}
dictionary(){
    if(this.state.selectedID>0){
        return(
        <Dictionary identifier={this.state.url} data={this.state.dictData} />
)
    }else{
        return[]
    }
}
    /**
     * component designers
     */
    rightColumn(){
        if(this.state.selectedID==0){   //no selection
            return []
        }else{
            let index=-1
            this.state.collapse.forEach((e,ind)=>{
                if(e){
                    index=ind
                }
            })
            if(index==-1){                  //all are collapsed
                this.state.selectedID=0
                return []
            }else{
                switch(index){
                    case 1:                 // Electronic form configurator
                        return this.dataFormConfiguration()
                    case 2:                 // Electronic form resource
                        return this.resource()
                    case 3:                 // Electronic form resource
                        return this.dictionary()
                    default:
                        return []
                }
            }
        }
    }

    render(){
        if(this.state.labels.locale==undefined || this.state.data.dictName == undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                {this.header()}
                <Row>
                    <Col xs='12' sm='12' lg='12' xl='6'>
                        {this.leftColumn()}
                    </Col>
                    <Col xs='12' sm='12' lg='12' xl='6'>
                        {this.rightColumn()}
                    </Col>
                </Row>
                {this.header()}
            </Container>
        )
    }


}
export default ProcessValidator
ProcessValidator.propTypes={
    dictNodeID:PropTypes.number.isRequired,    //URL of an application selected
    recipient:PropTypes.string.isRequired,  //recipient for messages   
}