import React , {Component} from 'react'
import {Container, Row, Col, FormText, Card, CardBody, CardTitle, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Pharmadex from './Pharmadex'
import ThingsPublisher from './reports/ThingsPublisher'
import Navigator from './utils/Navigator'
import Downloader from './utils/Downloader'
import Alerts from './utils/Alerts'
import TimeLine from './TimeLine'

/**
 * Display public available permit data
 * 
 */
class PublicPermitData extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data: this.props.data,      //PublicPermitDTO.java
            labels:{
                historyData:'',
                uploadApplReceipt:'',
                return_action:'',
                withdraw_Application:'',
                no_receipt:'',
                access_deny:'',
            },
            fullcollapse:[],
            show:true //show link uploadApplReceipt
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
        this.builLeftColumn=this.builLeftColumn.bind(this)
        this.buildRightColumn=this.buildRightColumn.bind(this)
        this.withdrawApplication=this.withdrawApplication.bind(this)
        this.timeLine=this.timeLine.bind(this)
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
        this.load()
       
    }
    /**
     * Load public available data
     */
    load(){
        Fetchers.postJSON("/api/public/permit/data", this.state.data, (query,result)=>{
            this.state.data=result
            //Locales.createLabels(this)
            if(this.state.data.valid){
                this.state.fullcollapse = []
                if(Fetchers.isGoodArray(this.state.data.application)){
                    this.state.data.application.forEach((thing, index)=>{
                        this.state.fullcollapse.push({
                            ind:index,
                            collapse:false
                        })
                    })
                }
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.access_deny, color:'danger'})
            }
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    builLeftColumn(){
        var applTitle = ""
        if(Fetchers.isGoodArray(this.state.data.application)){
            applTitle = this.state.data.application[0].title
        }
        
        if(applTitle == ""){
            return []
        }else{
            return (
                <Card className={Pharmadex.settings.activeBorder}>
                    <CardTitle className="text-center bg-light text-dark" tag="h3">
                        {applTitle}
                    </CardTitle>
                    <CardBody>
                        <Row className='d-print-none' hidden={!this.state.data.guest || !this.state.show}>
                            <Col xs='12' sm='12' lg='12' xl='12' className='d-flex justify-content-end'>
                                <Button key={100} color="link" size='sm'
                                    onClick={()=>{
                                                let data={
                                                    historyId:this.state.data.application[0].historyId
                                                }
                                                Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/application/receipt", data, (query,result)=>{
                                                    data=result
                                                    if(data.receiptDocumentID>0){
                                                        let dl = new Downloader()
                                                        dl.postDownload('/api/guest/application/receipt/open', data, "file.bin")
                                                    }else{
                                                        this.state.show=false
                                                        this.setState(this.state)
                                                        Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.no_receipt, color:'danger'})
                                                    }
                                                })
                                            }}>
                                                {this.state.labels.uploadApplReceipt}
                                </Button>
                            </Col>
                        </Row>
                        <ThingsPublisher data={this.state.data.application} recipient={this.state.identifier} />
                    </CardBody>
                </Card>
            )
        }
    }

    buildRightColumn(){
        var applTitle = ""
        if(Fetchers.isGoodArray(this.state.data.application)){
            applTitle = this.state.data.application[0].title
        }
        
        if(applTitle == ""){
            return []
        }else{
            return(
                <Card className={Pharmadex.settings.activeBorder}>
                    <CardTitle className="text-center bg-light text-dark" tag="h3">
                        {this.state.labels.historyData}
                    </CardTitle>
                    <CardBody>
                        <ThingsPublisher data={this.state.data.applHistory} recipient={this.state.identifier} />
                    </CardBody>
                </Card>
            )
        }
    }

    withdrawApplication(){
        Fetchers.postJSON("/api/guest/withdraw/application", this.state.data, (query,result)=>{
        window.history.back()
        })
    }
    timeLine(){
        let p=this.state.data.permitDataID
        let h=this.state.data.historyID
        if(Fetchers.isGoodArray(this.state.data.applHistory)){
            h=this.state.data.applHistory[0].historyId
        }
        return(
            <Row>
                <Col>
                    <TimeLine historyId={h} permitdataid={p} recipient={this.state.identifier}/>
                </Col>
            </Row>
        )
    }

    render(){
        if(this.state.data.title == undefined || this.state.labels.locale==undefined){// || !Fetchers.isGoodArray(this.state.data.application)){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <h1 className="text-center">{this.state.data.title}</h1>
                    </Col>
                </Row>
                {this.timeLine()}
                <Row>
                    <Col xs='12' sm='12' lg='10' xl='10'>
                        <FormText color="muted">
                            {this.state.data.description}
                        </FormText>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                    <div className="mb-1 d-flex justify-content-end">
                    <Button size="sm" hidden={!this.state.data.reject}
                            className="mr-1" color="warning"
                            onClick={()=>{
                                Alerts.warning(this.state.labels.withdraw_Application,()=>{
                                    this.withdrawApplication()
                                   }, ()=>{})
                            }}
                            //action return to applicant
                        >{this.state.labels.return_action}</Button>{' '}
                    </div>   
                    </Col>
                </Row>

                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.builLeftColumn()}
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.buildRightColumn()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default PublicPermitData
PublicPermitData.propTypes={
    data: PropTypes.shape(
        {
            permitDataID:PropTypes.number.isRequired   //PublicPermitDTO.java
        }.isRequired,
    )
}