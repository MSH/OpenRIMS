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
 * Import addresses
 */
class Import_A extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},
            labels:{
                global_cancel:'',
                global_save:'',
                askforimportrun:'',
                reload:"",
                startImport:""
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.headerFooter=this.headerFooter.bind(this)
        this.load=this.load.bind(this)
        this.verifyImport=this.verifyImport.bind(this)
        this.runImport=this.runImport.bind(this)
        this.reload=this.reload.bind(this)
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
                    Alerts.warning(this.state.labels.askforimportrun,
                        ()=>{   //yes
                            //run import
                            this.verifyImport()
                        },
                        ()=>{   //no

                        })
                }
            }
           //Navigator.message(this.state.identifier, this.props.recipient, "thingUpdated", this.state.data)
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.load()
    }

    verifyImport(){
        Fetchers.postJSON("/api/admin/importa/verif", this.state.data, (query, result)=>{
            this.state.data=result
            if(this.state.data.valid){
                this.setState(this.state)
                this.runImport()
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                this.setState(this.state)
            }
        })
    }

    runImport(){
        Fetchers.postJSON("/api/admin/importa/run", this.state.data, (query, result)=>{
            this.state.data=result
            this.setState(this.state)
            Navigator.message('*', '*', 'show.alert.pharmadex.2', this.state.labels.startImport)
        })
    }

    /**
     * Load data
     */
    load(){
        Fetchers.postJSON("/api/admin/import/adminunits/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    reload(){
        Fetchers.postJSON("/api/admin/import/adminunits/reload", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
            Navigator.message(this.state.identifier, "*", "thingReload", this.state.data)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    headerFooter(){
        return(
            <div className="mb-1 d-flex justify-content-end">
                <Button size="sm"
                className="mr-1" color="success"
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
                        <Thing data={this.state.data} recipient={this.state.identifier} noload/>
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
export default Import_A