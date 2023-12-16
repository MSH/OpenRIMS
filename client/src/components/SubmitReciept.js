import React , {Component} from 'react'
import {Container, Row, Col, Card, CardHeader, CardBody} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import ButtonUni from './form/ButtonUni'
import ViewEdit from './form/ViewEdit'
import Pharmadex from './Pharmadex'
import FieldDisplay from './form/FieldDisplay'
import Downloader from './utils/Downloader'

/**
 * Submit reciept data
 * To visual confirmation of any submit
 * The OK button issues a message "submit_reciept" to a caller
 * @example
 * <SubmitReciept recipient={this.state.identifier} historyId={this.state.data.historyId}/>
 */
class SubmitReciept extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            labels:{
                form_ok:'',
                submitreceipt:'',
                global_print:'',
                global_upload:'',
            },
            data:{
                historyId:this.props.historyId, //SubmitRecieptDTO
            },
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.buttons=this.buttons.bind(this)
        this.content=this.content.bind(this)
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
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/submit/reciept", this.state.data, (query,result)=>{
            this.state.data=result
            Locales.createLabels(this)
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
    }


    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * show buttons
     */
    buttons(){
        return(
            <Row className='d-print-none'>
                <Col xs='12' sm='12' lg='6' xl='9'>
                </Col>
                <Col xs='12' sm='12' lg='2' xl='1'>
                    <ButtonUni
                        label={this.state.labels.global_upload}
                        color='primary'
                        onClick={()=>{
                            let data={
                                historyId:this.state.data.historyId
                            }
                            Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/application/receipt", data, (query,result)=>{
                                data=result
                                if(data.receiptDocumentID>0){
                                    let dl = new Downloader()
                                    dl.postDownload('/api/guest/application/receipt/open', data, "file.bin")
                                }
                            })
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='2' xl='1'>
                    <ButtonUni
                        label={this.state.labels.global_print}
                        color='primary'
                        onClick={()=>{
                            window.print()
                            }
                        }
                    />
                </Col>
                <Col xs='12' sm='12' lg='2' xl='1'>
                    <ButtonUni
                        label={this.state.labels.form_ok}
                        color='success'
                        onClick={()=>{
                                Navigator.message(this.state.identifier,this.props.recipient, 'submit_reciept',{})
                            }
                        }
                    />
                </Col>
            </Row>
        )

    }

    /**
     * Additional content, i.e., follou up`s, changes, etc
     */
    content(){
        return(
            <Card className={Pharmadex.settings.activeBorder}>
                <CardHeader>
                    <h1>{this.state.labels.submitreceipt}</h1>
                </CardHeader>
                <CardBody>
                    <Row>
                        <Col>
                            <FieldDisplay mode='date' attribute='submitted_date' component={this} /> 
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='text' attribute='reg_processing' component={this} /> 
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='textarea' attribute='description' component={this} /> 
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='text' attribute='prefLabel' component={this} /> 
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='textarea' attribute='references' component={this} /> 
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEdit mode='textarea' attribute='office' component={this} /> 
                        </Col>
                    </Row>
                </CardBody>
            </Card>
        )
    }
    render(){
        if(this.state.data.prefLabel==undefined || this.state.labels.locale == undefined){
            return Pharmadex.wait()
        }else{
            return(
                <Container fluid>
                    {this.content()}
                    {this.buttons()}
                </Container>
            )
        }
    }


}
export default SubmitReciept
SubmitReciept.propTypes={
    recipient : PropTypes.string.isRequired,    //for messages
    historyId:PropTypes.number.isRequired,
}