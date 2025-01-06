import React , {Component} from 'react'
import {Container, Row, Col, Button} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import Thing from './Thing'
import Alerts from './utils/Alerts'
import Downloader from './utils/Downloader'
import TableSearch from './utils/TableSearch'
import LogEvents from './LogEvents'
/**
 * Import messages
 */
class Import_Messages extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},
            tablemes:{},
            labels:{
                global_cancel:'',
                askforimportrun:'',
                global_download:"",
                global_import_short:'',
                startImport:"",
                search:'',
                messages:'',
                templatedownload:'',
                langreplcanceled:'',
                global_help:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.headerFooter=this.headerFooter.bind(this)
        this.load=this.load.bind(this)
        this.loadTable=this.loadTable.bind(this)
        this.verifyImport=this.verifyImport.bind(this)
        this.runImport=this.runImport.bind(this)
        this.buildRight=this.buildRight.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.from == "locales"){
            if(data.subject=="onSelectionChange"){// in dictionary Locales
                this.state.data.thing.dictionaries.locales=data.data
            }
        }
        if(data.to==this.state.identifier){
            if(data.subject=="thingUpdated"){// in table with files
                this.state.data.thing.documents.flags=data.data.documents.flags
                this.state.data.thing.documents.files=data.data.documents.files
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.load()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    verifyImport(){
        Fetchers.postJSON("/api/admin/import/locales/verif", this.state.data, (query, result)=>{
            this.state.data=result
            if(this.state.data.valid){
                this.setState(this.state)
                this.runImport()
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                Navigator.message(this.state.identifier, "*", "thingReload", this.state.data.thing)
                this.setState(this.state)
            }
        })
    }

    runImport(){
        Alerts.warning(this.state.labels.askforimportrun,
            ()=>{   //yes
                Fetchers.postJSON("/api/admin/import/locales/run", this.state.data, (query, result)=>{
                    this.state.data=result
                    this.setState(this.state)
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', "Import End")
                    window.location.reload(false)
                })
            },
            ()=>{   //no
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.langreplcanceled, color:'warning'})
            })
    }

    /**
     * Load data
     */
    load(){
        Fetchers.postJSON("/api/admin/import/locales/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    loadTable(){
        Fetchers.postJSON("/api/admin/import/locales/loadmessages", this.state.data.table, (query,result)=>{
            this.state.data.table=result
            this.setState(this.state)
        })
    }

    buildRight(){
        if(this.state.data.table.headers==undefined){
            return Pharmadex.wait()
        }
        return(
            <Col xs='12' sm='12' lg='6' xl='6'>
                <Row>
                    <Col><h6>{this.state.labels.messages}</h6></Col>
                </Row>
                <Row>
                    <Col>
                        <TableSearch label={this.state.labels.search}
                            tableData={this.state.data.table} 
                            loader={this.loadTable}
                            title={this.state.data.home}
                            styleCorrector={(header)=>{
                                if(header=='pref'){
                                    return {width:'30%'}
                            }
                        }}/>
                    </Col>
                </Row>
            </Col>
        )
    }

    headerFooter(){
        return(
            <div className="mb-1 d-flex justify-content-end">
                 <Button size="sm"
                    className="mr-1" color="info"
                    onClick={()=>{
                        Fetchers.openWindowHelp('/api/admin/help/import/messages')
                    }}
                    >{this.state.labels.global_help}</Button>{' '}
                <Button size="sm"
                    className="mr-1" color="primary"
                    onClick={()=>{
                        this.verifyImport()
                    }}
                    >{this.state.labels.global_import_short}</Button>{' '}

                <Button size="sm"
                    outline
                    color="info"
                    onClick={()=>{
                        window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                    }}
                    >{this.state.labels.global_cancel}</Button>{' '}
            </div>
        )
    }

    render(){
        if(this.state.data.thing == undefined || this.state.data.thing.nodeId==undefined || this.state.labels.locale == undefined){
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
                        <LogEvents recipient={this.state.identifier} api='/api/admin/import/locales/log' />
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col>
                                <Button color="link" size='sm'
                                    onClick={()=>{
                                        let downloader = new Downloader();
                                        Fetchers.setJustLoaded(this.state.data, false)
                                        downloader.postDownload("/api/admin/import/locales/download",
                                        this.state.data, "messages.xlsx");
                                    }}>
                                        {this.state.labels.templatedownload}
                                </Button>
                            </Col>
                        </Row>
                        <Thing data={this.state.data.thing} recipient={this.state.identifier} noload narrow/>
                    </Col>
                    {this.buildRight()}
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
export default Import_Messages