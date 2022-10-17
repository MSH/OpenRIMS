import React , {Component} from 'react'
import {Container, Row, Col, Button} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import Thing from './Thing'
import Alerts from './utils/Alerts'
import Spinner from './utils/Spinner'
/**
 * Import ATC codes
 */
class Import_ATC extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},
            labels:{
                global_cancel:'',
                global_save:'',
                continue:'',
                reload:"",
                startImport :""
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.headerFooter=this.headerFooter.bind(this)
        this.load=this.load.bind(this)
        this.reload=this.reload.bind(this)
        this.runImport=this.runImport.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            if(data.subject=="onSelectionChange"){
                this.state.data=data
            }
            if(data.subject=="savedByAction"){
                this.state.data=data.data
                Alerts.warning(this.state.labels.continue,
                    ()=>{   //yes
                        //run import
                        this.runImport()
                        this.reload()
                    },
                    ()=>{   //no

                    })
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.load()
    }

    runImport(){
        Fetchers.postJSON("/api/admin/import/atccodes/run", this.state.data, (query, result)=>{
            this.state.data=result
            this.setState(this.state)
            Navigator.message('*', '*', 'show.alert.pharmadex.2', this.state.labels.startImport)
            Navigator.message(this.state.identifier, "*", "thingReload", this.state.data)
        })
    }

    /**
     * Load data
     */
    load(){
        Fetchers.postJSON("/api/admin/import/atccodes/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    reload(){
        window.location.reload()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    headerFooter(){
        return(
            <div className="mb-1 d-flex justify-content-end">
                <Button size="sm"
                className="mr-1" color="success"
                hidden={this.state.data.readOnly}
                onClick={()=>{
                    Spinner.show()
                    Navigator.message(this.state.identifier, "*", "saveAll", {})
                }}
                >{this.state.labels.global_save}</Button>{' '}

                <Button size="sm"
                className="mr-1" color="primary"
                hidden={this.state.data.nodeId == 0}
                onClick={()=>{
                    this.reload()
                }}
                >{this.state.labels.reload}</Button>{' '}

                <Button size="sm"
                className="mr-1" color="info"
                onClick={()=>{
                    window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                }}
                >{this.state.labels.global_cancel}</Button>{' '}
            </div>
        )
    }
    render(){
        if(this.state.data.nodeId==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        {this.headerFooter()}
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Thing data={this.state.data} recipient={this.state.identifier} readOnly={this.state.data.readOnly} noload/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.headerFooter()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default Import_ATC