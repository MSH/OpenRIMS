import React , {Component} from 'react'
import {Container, Row, Col, Label, Breadcrumb, BreadcrumbItem} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Pharmadex from './Pharmadex'
import ButtonUni from './form/ButtonUni'
import FieldInput from './form/FieldInput'
import Navigator from './utils/Navigator'
import Dictionary from './Dictionary'
import CollectorTable from './utils/CollectorTable'
/**
 * Import addresses
 */
class ExchangeConfiguration extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{
                identifier:""
            },
            labels:{
                serverurl:'',
                btn_connect:'',
                titleexistdictionary:'',
                titlenotexistdictionary:'',
                warningImportDictionary:'',
                next:''
            },
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.getStyleConnection=this.getStyleConnection.bind(this)

        this.ping=this.ping.bind(this)
        this.onNextClick=this.onNextClick.bind(this)
        this.importSelectDictionary=this.importSelectDictionary.bind(this)
        this.loadDictionary=this.loadDictionary.bind(this)

        this.content=this.content.bind(this)
        this.pageConnect=this.pageConnect.bind(this)
        this.pageProcess=this.pageProcess.bind(this)
        this.pageDictionaries=this.pageDictionaries.bind(this)
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
        this.rigthDict=this.rigthDict.bind(this)
        this.showDict=this.showDict.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.subject=='onSelectionChange' && data.to==this.state.identifier){
            if(data.from==this.state.data.wfdto.masterDict.url){
                this.state.data.wfdto.masterDict=data.data
                this.onNextClick()
            }
            if(data.from==this.state.data.wfdto.slaveDict.url){
                this.state.data.wfdto.slaveDict=data.data
                //this.loadDicts()
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.state.data.identifier = Fetchers.readLocaly("mainserveraddress", "")
        Fetchers.postJSON("/api/admin/exchange/load", this.state.data, (query, result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    getStyleConnection(){
        var color = "red"
        if(this.state.data.pingServer){
            color = 'green'
        }
        return color
    }

    ping(){
        this.state.data.identifier = window.location.hostname

        Fetchers.postJSON("/api/admin/exchange/ping", this.state.data, (query, result)=>{
            this.state.data=result
            Fetchers.writeLocaly("mainserveraddress", this.state.data.serverurl.value)
            this.setState(this.state)
        })
    }

    onNextClick(){
        if(this.state.data.currentPage == 0){
            this.setState(this.state)
        }else{
            Fetchers.postJSON("/api/admin/exchange/loadnext", this.state.data, (query, result)=>{
                this.state.data=result
                if(this.state.data.valid){
                    this.setState(this.state)
                }else{
                    this.state.data.currentPage--
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                    this.setState(this.state)
                }
            })
        }
    }

    importSelectDictionary(dictId){
        Fetchers.alerts(this.state.labels.warningImportDictionary, ()=>{
            this.state.data.dictByCopy.id = dictId
            Fetchers.postJSON("/api/admin/exchange/config/importdict",this.state.data,(query,result)=>{
                this.state.data=result
                this.onNextClick()
            })
        }, null)
    }

    loadDictionary(){
        Fetchers.postJSON("/api/admin/exchange/dictionary/load", this.state.data, (query, result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    showDict(){
        if(this.state.data.currentPage == 4){
            return []
        }
        if(this.state.data.showDict == undefined){
            return []
        }
        if(this.state.data.showDict.url == null){
            return []
        }
        if(this.state.data.showDict.url.length==0){
            return []
        }
        return(
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Dictionary
                            identifier={this.state.data.showDict.url}
                            recipient={this.state.identifier}
                            data={this.state.data.showDict}
                            display
                    />
                </Col>
        )
    }

    rigthDict(){
        if(this.state.data.wfdto.slaveDict == undefined){
            return []
        }
        if(this.state.data.wfdto.slaveDict.url == null){
            return []
        }
        if(this.state.data.wfdto.slaveDict.url.length==0){
            return []
        }
        return(
            <Col xs='12' sm='12' lg='6' xl='6'>
                <Row>
                    <Col>
                        <Dictionary
                            identifier={this.state.data.wfdto.slaveDict.url}
                            recipient={this.state.identifier}
                            data={this.state.data.wfdto.slaveDict}
                            display
                        />
                    </Col>
                </Row>
            </Col>
        )
    }

    pageConnect(){
        return(
            <Row>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <FieldInput mode='text' attribute="serverurl" component={this}/>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2' >
                        <ButtonUni 
                                label={this.state.labels.btn_connect}
                                onClick={()=>{
                                    this.ping()
                                }} 
                                color={"info"}
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1' >
                        <Label><i className="fas fa-solid fa-check mr-1" style={{color:this.getStyleConnection()}}></i></Label>
                    </Col>
                </Row>
        )
    }

    pageProcess(){
        if(this.state.data.wfdto == undefined || this.state.data.wfdto.masterDict == undefined || this.state.data.wfdto.masterDict.url == undefined){
            return Pharmadex.wait()
        }
        return (
            <Row hidden={!this.state.data.pingServer}>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Dictionary
                                identifier={this.state.data.wfdto.masterDict.url}
                                recipient={this.state.identifier}
                                data={this.state.data.wfdto.masterDict}
                                display
                            />
                </Col>
                {this.rigthDict()}
            </Row>
        )
    }

    pageDictionaries(){
        if(this.state.data.wfdto.slaveDict == undefined || this.state.data.wfdto.slaveDict.url == null || this.state.data.wfdto.slaveDict.url.length==0){
            return []
        }
        if(this.state.data.existTable == undefined || this.state.data.notExistTable == undefined){
            return []
        }
        let ret = []
        ret.push(
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Label>{this.state.labels.titleexistdictionary}</Label>
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Label>{this.state.labels.titlenotexistdictionary}</Label>
                </Col>
            </Row>
        )
        ret.push(
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <CollectorTable
                            tableData={this.state.data.existTable}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            loader={this.onNextClick}
                            linkProcessor={(rowNo, cell)=>{
                                this.state.data.urlSelect = cell.value
                                this.loadDictionary()
                            }}
                            styleCorrector={(header)=>{
                                if(header=='prefLbl'){
                                    return {width:'30%'}
                                }
                            }}
                        />
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <CollectorTable
                            tableData={this.state.data.notExistTable}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            loader={this.onNextClick}
                            styleCorrector={(header)=>{
                                if(header=='prefLbl'){
                                    return {width:'30%'}
                                }
                            }}
                            selectRow={(row)=>{
                                this.importSelectDictionary(this.state.data.notExistTable.rows[row].dbID)
                            }}
                        />
                </Col>
            </Row>
        )
        ret.push(
            <Row>
                {this.showDict()}
            </Row>
            
        )
        return ret
    }

    content(){
        if(this.state.data.currentPage == 0){
            return this.pageConnect()
        }
        if(this.state.data.currentPage == 1){
            return this.pageProcess()
        }
        if(this.state.data.currentPage == 2){
            return this.pageDictionaries()
        }
        if(this.state.data.currentPage == 3){
            return this.pageDictionaries()
        }
        if(this.state.data.currentPage == 4){
            return this.pageDictionaries()
        }
    }

    createBreadCrumb(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.headers)){
            this.state.data.headers.forEach((hdr, index)=>{
                if(index <= this.state.data.currentPage){
                    if(index != this.state.data.currentPage){
                        ret.push(
                            <BreadcrumbItem className="d-inline"  key={index}>
                                <div className="btn btn-link p-0 border-0"
                                    onClick={()=>{
                                        this.state.data.currentPage = index;
                                        this.onNextClick()
                                    }} >
                                    <h6 className="d-inline">{hdr}</h6>
                                </div>
                            </BreadcrumbItem>
                        )
                    }else{
                        ret.push(
                            <BreadcrumbItem className="d-inline"  key={index}>
                                <h6 className="d-inline">{hdr}</h6>
                            </BreadcrumbItem>
                        )
                    }
                }
            })
            if(this.state.data.currentPage < this.state.data.headers.length - 1){
                ret.push(
                    <BreadcrumbItem className="d-inline"  key={this.state.data.currentPage + 1}>
                        <div className="btn btn-link p-0 border-0"
                            onClick={()=>{
                                this.state.data.currentPage ++;
                                this.onNextClick()
                        }} >
                            <h6 className="d-inline">{this.state.labels.next}</h6>
                        </div>
                    </BreadcrumbItem>
                )
            }
        }
       
        return ret
    }

    render(){
        if(this.state.data.serverurl==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <Breadcrumb>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.content()}
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Breadcrumb>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ExchangeConfiguration