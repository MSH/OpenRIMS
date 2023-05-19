import React , {Component} from 'react'
import {Container,Row,Col,Alert, Breadcrumb, BreadcrumbItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import ViewEdit from './form/ViewEdit'
import ViewEditDate from './form/ViewEditDate'
import Dictionary from './Dictionary'
import ApplicationFiles from './ApplicationFiles'
import Applicant from './Applicant'
import ActionBar from './ActionBar'

/**
 * Here components are living
 * @example
 * <FormLayout data={this.state.data}  readOnly={this.props.readOnly} />
 */
class FormLayout extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString,  //my address for messages
            auxComponent:[],
            mainArea:true,
            identifier:'',
            data:{},                //ThingDTO         
            labels:{
                application:'',
                application_info:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.paintRows=this.paintRows.bind(this)
        this.paintCells=this.paintCells.bind(this)
        this.paintCell=this.paintCell.bind(this)
        this.literal=this.literal.bind(this)
        this.dictionary=this.dictionary.bind(this)
        this.filesControl=this.filesControl.bind(this)
        this.dateFiled=this.dateFiled.bind(this)
        this.applicantsControl=this.applicantsControl.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            if(data.subject=='openApplicant'){
                this.showInAux(
                    <Applicant data={data.data} identifier={data.from}
                                                        caller={this.state.identifier}
                                                        tableView={false}
                                                        narrow={this.props.narrow}
                                                        readOnly={this.props.readOnly}
                    />
                )
            }
        }
    }

    /**
     * Place a component to 
     * @param {react component} component 
     */
    showInAux(component){
        this.state.auxComponent=component
        this.state.mainArea=false
        this.setState(this.state)
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.state.data=this.props.data
        this.state.identifier=this.props.data.url+this.props.data.dictionaryUrl
        Locales.createLabels(this,"dictionaries")
        Locales.createLabels(this,"applicants")
        Locales.createLabels(this,'files')
        Locales.createLabels(this, "literals")
        Locales.createLabels(this, "dates")
        Locales.resolveLabels(this)
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
    /**
     * Responsible for dates
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
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
     * Responsible for dictionaries
     * @param {string} name 
     * @param {number} index 
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
    /**
     * Responsible for files 
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
    filesControl(name, index){
        let files=this.state.data.documents[name] 
        if(files!=undefined){
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
                            <ApplicationFiles dictUrl={files.dictUrl} actNodeId={this.props.data.nodeId}
                                readOnly={this.state.data.readOnly || this.props.readOnly}/>
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
    }
    /**
     * Responsible for applicants
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
    applicantsControl(name, index){
        let applicant=this.state.data.applicants[name] 
        if(applicant!=undefined){
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
                            <Applicant data={applicant} identifier={name}
                                                        caller={this.state.identifier}
                                                        tableView
                                                        narrow={this.props.narrow}
                                                        readOnly={this.props.readOnly}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
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
            if(this.state.data.documents.hasOwnProperty(name)){
                return(
                    this.filesControl(name,index)
                )
            }
            if(this.state.data.applicants.hasOwnProperty(name)){
                return(
                    this.applicantsControl(name, index)
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
                    <Col>
                    <Breadcrumb>
                        <BreadcrumbItem><a href="#">Home</a></BreadcrumbItem>
                        <BreadcrumbItem><a href="#">Library</a></BreadcrumbItem>
                        <BreadcrumbItem active>Data</BreadcrumbItem>
                     </Breadcrumb>
                    </Col>
                </Row>
                <Row hidden={!this.state.mainArea}>
                    <Col>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12' className="d-flex justify-content-end">
                            <ActionBar actions={this.state.data.actionBar.actions} sendTo={this.state.identifier}/>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                {this.paintRows()}
                            </Col>
                        </Row>
                    </Col>
                </Row>
                    
                <Row hidden={this.state.mainArea}>
                    <Col>
                        {this.state.auxComponent}
                    </Col>
                </Row>
                
            </Container>
        )
    }


}
export default FormLayout
FormLayout.propTypes={
    data:PropTypes.object.isRequired,       //ThingDTO object
    readOnly:PropTypes.bool,                //read only mode
    narrow:PropTypes.bool,                  //one column mode

}