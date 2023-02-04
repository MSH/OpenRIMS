import React , {Component} from 'react'
import {Card, CardHeader, Container, CardBody, Row, Col, Alert, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Pharmadex from './Pharmadex'
import Question from './Question'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'

/**
 *checklist component
 @example
 <CheckList
        historyId={this.state.data.path[this.state.pathIndex].historyId}
        recipient={this.state.identifier} 
        readOnly
    />
 */
class CheckList extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),  //my address for messages
            data:{
                dictUrl:this.props.dictUrl,
                historyId:this.props.historyId
            },
            labels:{
                global_yes:"",
                global_no:"",
                global_na:"",
                notes:"",
                global_help:"",
                global_save:"",
                global_submit:"",
                return_action:"",
                route_action:"",
                cancel:"",
                requiredvalue:"",
                success:""
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.checkList=this.checkList.bind(this)
        this.load=this.load.bind(this)
        this.save=this.save.bind(this)
    }

    /**
     * listen for askData broadcast and getData only to own address
     */
    eventProcessor(event){
        let data=event.data
        if(this.state.data.readOnly || this.props.readOnly){
            return
        }
        if(data.from==this.props.recipient){
            if(data.subject=="saveChecklistSilent"){
                Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/application/checklist/save", 
                this.state.data, (query,result)=>{
                    this.state.data=result
                    Navigator.message(this.state.identifier,this.props.recipient,"checklist_saved_silently", this.state.data)
                });
            }
            if(data.subject=="saveAll" || data.subject=='saveChecklist' || data.subject=='saveAllGuest'){
                this.save();
                Navigator.message(this.state.identifier,this.props.recipient,"checklist_saved", this.state.data)
            }
            if(data.subject=="submit"){
                this.state.submit=true
                this.save();
            }
        }
        return
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.load()
    }
    componentDidUpdate(){
        if(this.state.data.historyId!=this.props.historyId){
            this.state.data.dictUrl=this.props.dictUrl,
            this.state.data.historyId=this.props.historyId
            this.load()
        }
    }
    /**
     * create or load checklist
     */
    load(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/application/checklist/load", this.state.data, (query,result)=>{
            this.state.data=result
            Locales.createLabels(this)
            Locales.resolveLabels(this);
            this.setState(this.state)
        })
    }


    save(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/application/checklist/save", 
        this.state.data, (query,result)=>{
            if(result.valid){
                if(this.state.submit){
                    Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/application/submit", this.state.data, (query,result)=>{
                        this.state.data=result
                        if(this.state.data.valid){
                            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.success, color:'success'})
                            Navigator.message(this.state.identifier, this.props.recipient, "cancelThing", {})
                            Navigator.message(this.state.identifier, this.props.recipient, "afterSubmit", {})
                        }else{
                            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                            this.setState(this.state)
                        }
                    })
                }else{
                    Navigator.message(this.state.identifier, this.props.recipient, "cancelThing", {})
                }
            }else{
                this.state.data=result
                this.setState(this.state)
                let dd = {
                    mess:this.state.labels.requiredvalue,
                    color:'danger'
                }
                Navigator.message(this.state.identifier, "*", 'show.alert.pharmadex.2', dd)
            }
        })
    }


    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }


    /**
     * Create a checklist
     */
    checkList(){
        let ret=[]
        let list = this.state.data.questions
        let readOnly=this.props.readOnly || this.state.data.readOnly
        if(Fetchers.isGoodArray(list)){
            list.forEach((q,index) => {
                ret.push(
                    <Question key={index} data={q} index={index} labels={this.state.labels} recipient={this.state.identifier} readOnly={readOnly}/>
                )
            });
        }
        return ret
    }

    validCheckList(){
        var valid = true
        let list = this.state.data.questions
        if(Fetchers.isGoodArray(list)){
            list.forEach((q,index) => {
                if(!q.valid){
                    valid = false
                }
            });
        }
        return valid
    }

    render(){
        if(this.state.data.questions == undefined || this.state.labels.locale==undefined){
            return []
        }else{
            return(
                <Container fluid className={Pharmadex.settings.activeBorder}>
                    <Card key={this.state.data.id+this.state.data.prevStatusId} style={{fontSize:"0.8rem"}}>
                        <CardHeader>
                            <Row>
                                <Col xs='12' sm='12' lg='12' xl='12'>
                                    <h6>{this.state.data.title}</h6>
                                </Col>
                            </Row>
                            <Row hidden={this.validCheckList()}>
                                <Col>
                                    <Alert color="danger" className="p-0 m-0">
                                        <small>{this.state.labels.requiredvalue}</small>
                                    </Alert>
                                </Col>
                            </Row>
                        </CardHeader>
                        <CardBody>
                            <Row>
                                <Col xs='12' sm='12' lg='12' xl='12'>
                                    {this.checkList()}
                                </Col>
                            </Row>
                        </CardBody>
                    </Card>
                </Container>
            )
        }
    }


}
export default CheckList
CheckList.propTypes={
    historyId:PropTypes.number.isRequired,      //which activity and application
    recipient:PropTypes.string.isRequired,      //recipient for messages
    readOnly:PropTypes.bool,                    //read only
}