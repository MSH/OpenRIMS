import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import ActionBar from './ActionBar'
import Dictionary from './Dictionary'
import ViewEdit from './form/ViewEdit'
import FormLayout from './FormLayout'
import CollectorTable from './utils/CollectorTable'
import SearchControl from './utils/SearchControl'
import ButtonUni from './form/ButtonUni'
import Pharmadex from './Pharmadex'

/**
 * Responsible for an Applicant data
 * @fires
 * openApplicant - ask to open the applicant in a form
 * @example
 * <Applicant data={applicant} identifier={name}
                                caller={this.state.identifier}
                                tableView
                                narrow={this.props.narrow}
                                readOnly={this.props.readOnly}
                            />
 */
class Applicant extends Component{
    constructor(props){
        super(props)
        this.state={
            tableView:this.props.tableView,    //view as a table or a form
            data:{},
            labels:{
                global_add:'',
                search:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.formView=this.formView.bind(this)
        this.tableView=this.tableView.bind(this)
        this.tableLoader=this.tableLoader.bind(this)
        this.formLoader=this.formLoader.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            //TODO
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.state.data=this.props.data
        if(this.props.tableView){
            this.tableLoader()
        }else{
            this.formLoader()
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Reload only the form
     */
    formLoader(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/application/applicant/form",
        this.state.data,
        (query,result)=>{
            this.state.data=result
            Locales.createLabels(this)
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
    }
    /**
     * Reload only table
     */
    tableLoader(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/application/applicant/table",
                                    this.state.data,
                                    (query,result)=>{
                                        this.state.data=result
                                        Locales.createLabels(this)
                                        Locales.resolveLabels(this)
                                        this.setState(this.state)
                                    })
    }
    /**
     * Show a table. This table allows select an applicant for an application
     */
    tableView(){
        return(
        <Container fluid>
            <Row>
                <Col xs='12' sm='12' lg='8' xl='8'>
                    <div hidden={this.state.data.table.rows==0}>
                        <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.tableLoader}/>
                    </div>
                </Col>
                <Col  xs='12' sm='12' lg='4' xl='4'>
                    <ButtonUni
                        onClick={()=>{
                            Navigator.message(this.props.identifier, this.props.caller,"openApplicant",this.state.data)
                        }}
                        label={this.state.labels.global_add}
                        outline={true}
                        color="primary"
                    />
                </Col>
            </Row>
            <Row>
                <Col>
                    <CollectorTable
                        tableData={this.state.data.table}
                        loader={this.tableLoader}
                        headBackground={Pharmadex.settings.tableHeaderBackground}
                        selectRow={(rowNo)=>{
        
                        }}
                        linkProcessor={(rowNo, cell)=>{

                        }}
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
    /**
     * Show a form
     */
    formView(){
        let col21='6'
        let col22='6'
        if(this.props.narrow){
            col21='12'
            col22='12'
        }
        return (
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12'lg={col21} xl={col21}>
                        <Dictionary identifier={this.props.identifier+".business"}
                            data={this.state.data.business}/>
                    </Col>
                    <Col xs='12' sm='12'lg={col22} xl={col22}>
                        <Row>
                            <Col>
                                <ViewEdit mode='text' component={this} attribute="prefLabel" edit={!this.state.data.readOnly}/>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <ViewEdit mode='textarea' component={this} attribute="description" edit={!this.state.data.readOnly}/>
                            </Col>
                        </Row>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <FormLayout data={this.state.data.data} readOnly={this.props.readOnly} narrow={this.props.narrow}/>
                    </Col>
                </Row>
            </Container>
        )
    }
    render(){
        if(this.state.data.title == undefined){
            return []
        }
        let col11='8'
        let col12='4'
        let col21='6'
        let col22='6'
        if(this.props.narrow){
            col11='12'
            col12='12'
            col21='12'
            col22='12'
        }
        let component=[]
        if(this.state.tableView){
            component=this.tableView()
        }else{
            component=this.formView()
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12'lg={col11} xl={col11}>
                        <h4>{this.state.data.title}</h4>
                    </Col>
                    <Col xs='12' sm='12'lg={col12} xl={col12}>
                        <ActionBar actions={this.state.data.data.actionBar.actions} sendTo={this.props.identifier}/>
                    </Col>
                </Row>
                {component}
            </Container>
        )
    }
}
export default Applicant
Applicant.propTypes={
    data:PropTypes.object.isRequired,           //applicantDTO.java
    identifier:PropTypes.string.isRequired,     //identifier for messages, typically variable name
    caller:PropTypes.string.isRequired,         //caller that will receive messages
    tableView:PropTypes.bool.isRequired,        //true- table view, false- form view
    narrow:PropTypes.bool,                      //single column layout
    readOnly:PropTypes.bool                     //Read only mode
}