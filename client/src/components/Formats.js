import React , {Component} from 'react'
import {Container, Row, Col, Button, Card, CardHeader, CardBody} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import FieldInput from './form/FieldInput'
import FieldDate from './form/FieldDate'
import FieldDisplay from './form/FieldDisplay'
import CollectorTable from './utils/CollectorTable'

/**
 * Administrate-Configurations-Formats
 */
class Formats extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},        //FormatsDTO.java
            labels:{
                formatdate:'',
                formatDate:'',
                samples:'',
                Test:'',
                global_cancel:'',
                reset:'',
                global_save:'',
                global_date:'',
                number:'',
                dateDisplaySample:'',
                dateInputSample:'',
                save_error:'',
                global_help:'',
                tableCell:'',
                template:'',

            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.buttons=this.buttons.bind(this)
        this.content=this.content.bind(this)
        this.load=this.load.bind(this)
        this.formats=this.formats.bind(this)
        this.samples=this.samples.bind(this)
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
        Locales.createLabels(this)
        this.load()
    }

    load(){
        Fetchers.postJSON("/api/admin/formats", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    buttons(){
        return(
            <Row>
            <Col>
                <div className="mb-1 d-flex justify-content-end">
                        <Button size="sm"
                         className="mr-1" color="info"
                         onClick={()=>{
                            Fetchers.openWindowHelp('/api/admin/date/format/help')
                        }}
                        >{this.state.labels.global_help}</Button>{' '}
                        <Button size="sm"
                         className="mr-1" color="success"
                         onClick={()=>{
                            Fetchers.postJSON("/api/admin/formats/save", this.state.data, (query,result)=>{
                                this.state.data=result
                                if(result.valid){
                                    Navigator.navigate(Navigator.tabName())
                                }else{
                                    this.setState(this.state)
                                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                                }
                            })
                         }}
                        >{this.state.labels.global_save}</Button>{' '}
                        <Button size="sm"
                         className="mr-1" color="info"
                         outline
                         onClick={()=>{
                            Navigator.navigate(Navigator.tabName())
                         }}
                        >{this.state.labels.global_cancel}</Button>{' '}
                    </div>
            </Col>
        </Row>
        )
    }
    /**
     * Input date and number formats
     */
    formats(){
        return (
            <Card>
                <CardHeader>
                    <h4>{this.state.labels.formatdate}</h4>
                </CardHeader>
                <CardBody>
                    <Row>
                        <Col xs='12' sm='12' lg='8' xl='8'>
                            <FieldInput mode='text' attribute='formatDate' component={this} />
                        </Col>
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <Row>
                                <Col>
                                    <Button size="sm"
                                    className="btn-block mt-1" color="info"
                                    onClick={()=>{
                                        Fetchers.postJSON("/api/admin/formats/test", this.state.data, (query,result)=>{
                                            this.state.data=result
                                            this.setState(this.state)
                                            if(!result.valid){
                                                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                                            }
                                        })
                                    }}
                                    >{this.state.labels.Test}</Button>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                <Button size="sm"
                                    className="btn-block mt-1" color="warning"
                                    onClick={()=>{
                                        Fetchers.postJSON("/api/admin/formats/reset", this.state.data, (query,result)=>{
                                            this.state.data=result
                                            this.setState(this.state)
                                            if(!result.valid){
                                                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                                            }
                                        })
                                    }}
                                    >{this.state.labels.reset}</Button>
                                </Col>
                            </Row>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable
                                tableData={this.state.data.table}
                                loader={this.load}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                            />
                        </Col>
                    </Row>
                    
                </CardBody>
            </Card>
        )
    }
    /**
     * display samples
     */
    samples(){
        return(
        <Card>
                <CardHeader>
                    <h4>{this.state.labels.samples}</h4>
                </CardHeader>
                <CardBody>
                    <Row>
                        <Col>
                            <FieldDate attribute='dateInputSample' component={this} />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <FieldDisplay mode='date' attribute='dateDisplaySample' component={this}/>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {this.state.labels.tableCell+ ': ' + this.state.data.dateCell.value}
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {this.state.labels.template+' : ' + this.state.data.dateEL}
                        </Col>
                    </Row>
                </CardBody>
            </Card>
        )
    }

    content(){
        return(
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    {this.formats()}
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    {this.samples()}
                </Col>
            </Row>
        )
    }


    render(){
        if(this.state.labels.locale==undefined || this.state.data.formatDate==undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                {this.buttons()}
                {this.content()}
                {this.buttons()}
            </Container>
        )
    }


}
export default Formats
Formats.propTypes={
    
}