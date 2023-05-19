import React , {Component} from 'react'
import {Container,Row,Col,Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import ViewEdit from './form/ViewEdit'
import ViewEditDate from './form/ViewEditDate'
import Dictionary from './Dictionary'
import ApplicationFiles from './ApplicationFiles'
import ActionBar from './ActionBar'
import AddressForm from './AddressForm'

/**
 * Responsible for display/edit object's data. An object is person,site, product
 * issue event with subject 'mainscreen' after job will be completed 
 * @example
 * <PersonElement nodeId={event.data.data.nodeId} caller={this.props.identifier}  readOnly={this.props.readOnly} />
 */
class ObjectElement extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},                //ObjectDTO         
            labels:{
                persondata:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.paintRows=this.paintRows.bind(this)
        this.paintCells=this.paintCells.bind(this)
        this.paintCell=this.paintCell.bind(this)
        this.literal=this.literal.bind(this)
        this.dictionary=this.dictionary.bind(this)
        this.filesControl=this.filesControl.bind(this)
        this.load=this.load.bind(this)
        this.dateFiled=this.dateFiled.bind(this)
        this.addressComponent=this.addressComponent.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.props.nodeId){
            if(data.subject=='runAction'){
                if(data.data=='mainscreen'){
                    Navigator.message(this.props.nodeId+'', this.props.caller,'mainscreen',{})
                }
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.load()
        Locales.resolveLabels(this)
    }
    /**
     * Load object's data by node ID
     */
    load(){
        this.state.data.nodeId=this.props.nodeId
        this.state.data.classifierId=this.props.classifierId
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/object/load", this.state.data, (query,result)=>{
            Fetchers.setJustLoaded(result,false)
            this.state.data=result
            Locales.createLabels(this)
            Locales.createLabels(this,"dictionaries")
            Locales.createLabels(this,"addresses")
            Locales.createLabels(this,'files')
            Locales.createLabels(this,'dates')
            Locales.createLabelsRecursive(this.state.data, this.state.labels, "literals")
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    /**
     * Place a literal in a row 
     * @param {name (label of a literal)} name 
     * @param {number to create a key} index 
     */
     literal(name, index){
        let fieldDTO = this.state.data.literals[name]
        if(fieldDTO != undefined){
            let mode="text"
            if(fieldDTO.textArea){
                mode="textarea"
            }
            let readOnly=this.state.data.readOnly || fieldDTO.readOnly
            return(
                <Row key={index}>
                    <Col>
                        <ViewEdit edit={!readOnly} mode={mode} attribute={name}
                        component={this} 
                        data={this.state.data.literals}
                        rows="6"
                        />
                    </Col>
                </Row>
            )
        }else{
            return[]
        }
    }

    dateFiled(name, index){
        let fieldDTO = this.state.data.dates[name]
        if(fieldDTO != undefined){
            let readOnly=this.state.data.readOnly || fieldDTO.readOnly
            return(
                <Row key={index}>
                    <Col>
                        <ViewEditDate 
                            edit={!readOnly} 
                            attribute={name}
                            component={this} 
                            data={this.state.data.dates}
                        />
                    </Col>
                </Row>
            )
        }else{
            return[]
        }
    }


    /**
     * 
     * @param {label of a dictionary} name 
     * @param {number to create a key} index 
     */
    dictionary(name, index){
        let dict=this.state.data.dictionaries[name]
        if(dict == undefined){
            return []
        }
        return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{this.state.labels[name]}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Dictionary identifier={name} data={dict} display/>
                        </Col>
                    </Row>
                    <Row hidden={this.state.data.dictionaries[name].valid}>
                        <Col>
                            <Alert color={this.state.alertColor} className="p-0 m-0">
                                <small>{this.state.data.dictionaries[name].identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                </Col>
            </Row>
        )
    }

    filesControl(control, index){
        let files=this.state.data.files[control]
        if(files==undefined){
            return []
        }
        return(
        <Row key={index}>
            <Col>
                <Row>
                    <Col>
                        <h6>{this.state.labels[control]}</h6>
                    </Col>
                </Row>
                <Row>
                    <Col>
                    <ApplicationFiles dictUrl={files.dictUrl} actNodeId={0}
                            readOnly={false}/>
                    </Col>
                </Row>
            </Col>
        </Row>
        )
    }

    addressComponent(name, index){
       let addr = this.state.data.addresses[name]
       if(addr == undefined){
           return []
       }
        return (
            <Row key={index}>
            <Col>
                <Row>
                    <Col>
                        <h6>{this.state.labels[name]}</h6>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <AddressForm key={index} identifier="dummy" index={index} title={this.state.labels[name]} data={addr}/>
                    </Col>
                </Row>
            </Col>
        </Row>
        )
    }

    /**
     * Take component by variable name
     * @param {name of component in literals, dictionaries, files, etc} name 
     * @param {number to create a key} index 
     */
        takeComponent(name, index){
            if(this.state.data.literals.hasOwnProperty(name)){
                return (
                    this.literal(name,index)
                )
            }
            if(this.state.data.dates.hasOwnProperty(name)){
                return (
                    this.dateFiled(name,index)
                )
            }
            if(this.state.data.dictionaries.hasOwnProperty(name)){
                return(
                    this.dictionary(name,index)
                )
            }
            if(this.state.data.files.hasOwnProperty(name)){
                return(
                    this.filesControl(name,index)
                )
            }
            if(this.state.data.addresses.hasOwnProperty(name)){
                return(
                    this.addressComponent(name,index)
                )
            }
        }
     /**
     * Place components inside a cell in rows
     * @param {LayoutCellDTO} cell 
     */
         paintCell(cell){
            let ret=[]
            if(Fetchers.isGoodArray(cell.variables)){
                cell.variables.forEach((variable, index)=>{
                    ret.push(
                        this.takeComponent(variable, index)
                    )
                })
            }
            return ret
        }
        /**
         * Paint cells inside row
         * @param {LayoutRowDTO} row 
         */
        paintCells(row){
            let ret = []
            let lg='6'
            let xl='6'
            if(this.props.narrow){
                lg='12'
                xl='12'
            }
            if(Fetchers.isGoodArray(row.cells)){
                row.cells.forEach((cell,index)=>{
                    ret.push(
                        <Col xs='12' sm='12' lg={lg} xl={xl} key={index}>
                            {this.paintCell(cell)}
                        </Col>
                    )
                })
            }
            return ret;
        }
        /**
         * Paint the content in rows and cols
         * row is LayoutRowDTO
         */
        paintRows(){
            let ret=[]
            if(Fetchers.isGoodArray(this.state.data.layout)){
                this.state.data.layout.forEach((row,index) => {
                    ret.push(
                        <Row key={index}>
                            {this.paintCells(row)}
                        </Row>
                    )
                });
            }
            return ret;
        }

    render(){
        if(this.state.data.literals==undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <h4>{this.state.data.literals.description.value}</h4>
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6' hidden={this.state.data.readOnly}>
                        <ActionBar
                            actions={this.state.data.actionBar.actions}
                            sendTo={this.props.nodeId+''}
                        />
                    </Col>
                </Row>
                {this.paintRows()}
            </Container>
        )
    }


}
export default ObjectElement
ObjectElement.propTypes={
    nodeId:PropTypes.number.isRequired,         //node id of a person
    classifierId:PropTypes.number.isRequired,   //id of dictionary classifier node
    caller:PropTypes.string.isRequired,         //caller's identifier for messages
    readOnly:PropTypes.bool,

}