import React , {Component} from 'react'
import {Container, Row, Col,Collapse, Nav} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Pharmadex from './Pharmadex'
import ButtonUni from './form/ButtonUni'
import SearchControl from './utils/SearchControl'
import CollectorTable from './utils/CollectorTable'
import FieldInput from './form/FieldInput'
import TableSearch from './utils/TableSearch'
import Navigator from './utils/Navigator'
import AsyncInform from './AsyncInform'
import ReassignUsersLog from './ReassignUsersLog'

/**
 * Reassign a email for an applicant user
 */
class ReassignUsers extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},            //ReassignUserDTO.java
            selectApplicantOnly:true,   //allow only "Select an applicant"
            expandDetails:false,        //expand "details" section
            expandIcon:"fas fa-caret-right",    
            
            labels:{
                reassign_email_applicant:'',
                global_cancel:'',
                route_action:'',
                applicant_gmail:'',
                global_details:'',
                search:'',
                applicationsassigned:'',
                historyData:'',
                activities:'',
                global_help:'',
                background:'',
                starting:'',
                
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.searchApplicant=this.searchApplicant.bind(this)
        this.expandDetails=this.expandDetails.bind(this)
        this.loadDetails=this.loadDetails.bind(this)
        this.selectApplicantOnly=this.selectApplicantOnly.bind(this)
        this.placeInputForm=this.placeInputForm.bind(this)
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
        this.searchApplicant()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * laod appilicant's table
     */
    searchApplicant(){
        Fetchers.postJSON("/api/admin/reassign/applicant/search", this.state.data, (query, result)=>{
            this.state.data=result
            Locales.createLabels(this)
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
    }
    /**
     * ```
     * Manage UX
     * Set this.state.selectApplicantOnly if only select an applicant should be available
     * See "render"
     * ```
     */
    selectApplicantOnly(){
        let rows=this.state.data.applicants.rows
        this.state.selectApplicantOnly=true
        if(Fetchers.isGoodArray(rows)){
            rows.forEach((row)=>{
                this.state.selectApplicantOnly=this.state.selectApplicantOnly && !row.selected
            })
        }
    }
    /**
     * Expand details for the selected applicant
     */
    expandDetails(){
        if(!this.state.expandDetails){
            this.state.expandIcon="fas fa-caret-down"
           this.loadDetails()
        }else{
            this.state.expandIcon="fas fa-caret-right"
            this.state.expandDetails=false
            this.setState(this.state)
        }
    }

    loadDetails(){
        Fetchers.postJSON("/api/admin/reassign/applicant/details", this.state.data, (query,result)=>{
            this.state.data=result
            this.state.expandDetails=true
            this.setState(this.state) 
        })
    }
    /**
     * place data input form
     * Also possibe execution progress form
     */
    placeInputForm(){
        this.selectApplicantOnly()
        return(
            <Container fluid>
                
                <Row className='mb-5'>
                    <Col xs='12' sm='12' lg='6' xl='9'>
                        <h4>{this.state.labels.reassign_email_applicant}</h4>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_help}
                            color='info'
                            onClick={()=>{
                                window.open('/api/admin/help/reassign/applicant','_blank').focus()
                            }}
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_cancel}
                            outline
                            color='info'
                            onClick={()=>{
                                Navigator.navigate("administrate")
                            }}
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='1' hidden={this.state.selectApplicantOnly}>
                        <ButtonUni
                            label={this.state.labels.route_action}
                            color='primary'
                            onClick={()=>{
                                Fetchers.postJSON("/api/admin/reassign/applicant/run", this.state.data, (query,result)=>{
                                    this.state.data=result
                                    if(!this.state.data.valid){
                                        Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                                    }
                                    this.setState(this.state)
                                })
                            }}
                        />
                    </Col>
                </Row>
                <Row className='mb-3'>
                    <Col>
                       <ReassignUsersLog recipient={this.state.identifier} />
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row className='mb-3'>
                            <Col xs='12' sm='12' lg='4' xl='3'>
                                <b>{this.state.labels.applicant_gmail}</b>
                            </Col>
                            <Col xs='12' sm='12' lg='8' xl='9'>
                                <SearchControl label='someone@gmail.com' table={this.state.data.applicants} loader={this.searchApplicant}/>
                            </Col>    
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.applicants}
                                    loader={this.searchApplicant}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    selectRow={(rowno)=>{
                                        let rows= this.state.data.applicants.rows
                                        if(Fetchers.isGoodArray(rows)){
                                            rows.forEach((element,index) => {
                                                if(index==rowno){
                                                    element.selected=!element.selected
                                                }else{
                                                    element.selected=false
                                                }
                                            });
                                            this.state.expandDetails=false
                                            this.setState(this.state)
                                        }
                                    }}
                                />
                            </Col>
                        </Row>
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6' hidden={this.state.selectApplicantOnly}>
                        <FieldInput mode='text' attribute='reassignTo' component={this}/>
                    </Col>
                </Row>
                <Row hidden={this.state.selectApplicantOnly}>
                    <Col>
                        <Row>
                            <Col className="btn btn-link p-0 border-0 d-flex justify-content-start"
                                onClick={()=>{
                                this.expandDetails()
                                }}>
                                <Row>
                                    <Col>
                                        <h5 className="ml-3"><i className={this.state.expandIcon}></i>{this.state.labels.global_details}</h5>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                        <Collapse isOpen={this.state.expandDetails}>
                            <Row>
                                <Col xs='12' sm='12' lg='12' xl='4'>
                                   <TableSearch 
                                        label={this.state.labels.search}
                                        tableData={this.state.data.applications} 
                                        loader={this.loadDetails}
                                        title={this.state.labels.applicationsassigned}
                                    />
                                </Col>
                                <Col xs='12' sm='12' lg='12' xl='4'>
                                    <TableSearch 
                                        label={this.state.labels.search}
                                        tableData={this.state.data.dataTable} 
                                        loader={this.loadDetails}
                                        title={this.state.labels.historyData}
                                    />
                                </Col>
                                <Col xs='12' sm='12' lg='12' xl='4'>
                                    <TableSearch 
                                        label={this.state.labels.search}
                                        tableData={this.state.data.activities} 
                                        loader={this.loadDetails}
                                        title={this.state.labels.activities}
                                    />
                                </Col>
                            </Row>
                        </Collapse>
                    </Col>
                </Row>
            </Container>
        )
    }
    /**
     * Place execution progress form
     * Also possible data input form
     */
    placeProgressForm(){
        Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.starting, color:'info'})
        return(
            <AsyncInform 
                loadAPI='/api/admin/reassign/appicant/progress/load' 
                cancelAPI='/api/admin/reassign/appicant/progress/cancel'
            />
        )
    }

    render(){
        if(this.state.data.applicants == undefined || this.state.labels.locale == undefined){
            return Pharmadex.wait()
        }
        if(this.state.data.showProgress){
            return(this.placeProgressForm())
        }else{
            return(this.placeInputForm())
        }
 
    }


}
export default ReassignUsers
ReassignUsers.propTypes={
    
}