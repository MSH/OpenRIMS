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
import Thing from './Thing'
import FieldsComparator from './form/FieldsComparator'

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
            dictData:{},
            thingData:{},
            tableData:{},
            showComponent:false,
            selectleft:false,
            titleProc:"",
            labels:{
                exchangeconfig:'',
                serverurl:'',
                btn_connect:'',
                titleexistdictionary:'',
                titlenotexistdictionary:'',
                titleexistdata:'',
                titlenotexistdata:'',
                titleexistrecords:'',
                titlenotexistrecords:'',
                warningImportDictionary:'',
                warningImportResource:'',
                warningImportResConfig:'',
                warningImportActivities:'',
                warningUpdateActivity:'',
                warningInsertBeforeActivity:'',
                warningInsertAfterActivity:'',
                next:'',
                global_import_short:"",
                global_cancel:"",
                global_close:"",
                global_help:'',
                import_systemdata:"",
                import_userrole:"",
                authorities:"",
            },
        }
        this.getStyleConnection=this.getStyleConnection.bind(this)

        this.ping=this.ping.bind(this)
        this.onNextClick=this.onNextClick.bind(this)
        this.importSelectItem=this.importSelectItem.bind(this)
        this.loadSelectComponent=this.loadSelectComponent.bind(this)

        this.content=this.content.bind(this)
        this.pageConnect=this.pageConnect.bind(this)
        this.pageProcess=this.pageProcess.bind(this)
        this.pageDictionaries=this.pageDictionaries.bind(this)
        this.pageWorkflows=this.pageWorkflows.bind(this)
        this.pageResult=this.pageResult.bind(this)
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
        this.showSelectComponent=this.showSelectComponent.bind(this)
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.comparator = new FieldsComparator(this)
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
        if(this.state.data.valid){
            color = 'green'
        }
        return color
    }

    ping(){
        Fetchers.postJSON("/api/admin/exchange/ping", this.state.data, (query, result)=>{
            this.state.data = result
            if(this.state.data.valid){
                Fetchers.writeLocaly("mainserveraddress", this.state.data.serverurl.value)
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'success'})
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
            }
            this.setState(this.state)
        })
    }

    onNextClick(){
        if(this.state.data.currentPage == 0){
            this.state.data.processId = 0
            this.state.data.itProcessID = 0
            this.state.data.title = ""
            this.state.dictData = {}
            this.state.tableData = {}
            this.state.thingData = {}
            this.state.showTable = false
            this.setState(this.state)
        }else{
            var cont = true
            if(this.state.data.currentPage == 7){
                if(this.state.data.showImpAll){
                    cont = false
                    this.state.data.currentPage = 6
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:"Import data", color:'danger'})
                    this.setState(this.state)
                }
            }

            if(cont){
                if(this.state.data.currentPage == 1){
                    Fetchers.writeLocaly("mainserveraddress", this.state.data.serverurl.value)
        
                    this.state.dictData = {}
                    this.state.tableData = {}
                    this.state.thingData = {}
                    this.state.showTable = false
                }
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
    }

    importSelectItem(warningLbl, dictId, api){
        this.state.data.nodeIdSelect = dictId
        Fetchers.alerts(warningLbl, ()=>{
            Fetchers.postJSON(api,this.state.data,(query,result)=>{
                this.state.data = result
                if(this.state.data.valid){
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'success'})
                    this.onNextClick()
                }else{
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                    this.setState(this.state)
                }
            })
        }, null)
    }

    loadSelectComponent(){
        if(this.state.data.currentPage == 2 || this.state.data.currentPage == 3){// show dictionary
            Fetchers.postJSON("/api/admin/exchange/dictionary/load", this.state.data, (query, result)=>{
                this.state.dictData=result
                this.setState(this.state)
            })
        }else if(this.state.data.currentPage == 4){
            Fetchers.postJSONNoSpinner("/api/admin/exchange/resource/load", this.state.data, (query, result)=>{
                this.state.thingData=result
                if(!this.state.thingData.valid){
                    this.state.showComponent = false
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.thingData.identifier, color:'danger'})
                }
                this.setState(this.state)
            })
        }else if(this.state.data.currentPage == 5){
            Fetchers.postJSONNoSpinner("/api/admin/exchange/resconfig/load", this.state.data, (query,result)=>{
                this.state.tableData = result
                this.state.showComponent = true
                this.setState(this.state)
            })
        }
    }

    showSelectComponent(){
        let ret = []
        if(this.state.showComponent){
            if(this.state.data.currentPage == 2 || this.state.data.currentPage == 3){// show dictionary
                if(this.state.data.urlSelect.length==0){
                    return ret
                }
                if(this.state.dictData.url == undefined){
                    return ret
                }
                ret.push(
                        <Row>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <Row>
                                    <Col></Col>
                                    <Col xs='12' sm='12' lg='2' xl='2' className="d-flex justify-content-end">
                                        <ButtonUni
                                            label={this.state.labels.global_close}
                                            onClick={()=>{
                                                this.state.showComponent = false
                                                this.setState(this.state)
                                            }}
                                            outline
                                            color="info"
                                        />
                                    </Col>
                                </Row>
                                <Row>
                                    <Col>
                                        <Dictionary
                                            identifier={this.state.dictData.url}
                                            recipient={this.state.identifier}
                                            data={this.state.dictData}
                                            display
                                        />
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                )
            }else if(this.state.data.currentPage == 4){
                if(this.state.data.nodeIdSelect > 0){
                    if(this.state.thingData.nodeId != undefined){
                        let data = this.state.thingData
                        data.repaint = true
                        ret.push(
                            <Row>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <Row>
                                    <Col></Col>
                                    <Col xs='12' sm='12' lg='2' xl='2' className="d-flex justify-content-end">
                                        <ButtonUni
                                            label={this.state.labels.global_close}
                                            onClick={()=>{
                                                this.state.showComponent = false
                                                this.setState(this.state)
                                            }}
                                            outline
                                            color="info"
                                        />
                                    </Col>
                                </Row>
                                <Row>
                                    <Col>
                                        <Thing
                                            data={data} 
                                            recipient={this.state.identifier}
                                            narrow
                                        />
                            </Col>
                                </Row>
                            </Col>
                        </Row>
                        )
                    }
                }
            }else if(this.state.data.currentPage == 5){
                if(this.state.data.nodeIdSelect > 0){
                    if(this.state.tableData.rows != undefined){
                        ret.push(
                            <Row>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <Row>
                                    <Col>
                                        <Label>{this.state.tableData.generalSearch}</Label>
                                    </Col>
                                    <Col xs='12' sm='12' lg='2' xl='2' className="d-flex justify-content-end">
                                        <ButtonUni
                                            label={this.state.labels.global_close}
                                            onClick={()=>{
                                                this.state.showComponent = false
                                                this.setState(this.state)
                                            }}
                                            outline
                                            color="info"
                                        />
                                    </Col>
                                </Row>
                                <Row>
                                    <Col>
                                        <CollectorTable
                                            tableData={this.state.tableData}
                                            loader={this.loadSelectComponent}
                                            headBackground={Pharmadex.settings.tableHeaderBackground}
                                            styleCorrector={(header)=>{
                                                if(["col","row", "ord", "ext"].includes(header)){
                                                    return {width:'8%'}
                                                }
                                                if(header=="clazz"){
                                                    return {width:'15%'}
                                                }
                                                if(header=="propertyName"){
                                                    return {width:'20%'}
                                                }
                                            }}
                                            linkProcessor={(rowNo, col)=>{
                                            }}
                                        />
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                        )
                    }
                }
            }
        }
        return ret
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
        if(this.state.data.existTable == undefined || this.state.data.notExistTable == undefined){
            return Pharmadex.wait()
        }
        return(
            <Row>
            <Col xs='12' sm='12' lg='6' xl='6'>
                    <CollectorTable
                            tableData={this.state.data.existTable}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            loader={this.onNextClick}
                            linkProcessor={(rowNo, cell)=>{
                                
                            }}
                            selectRow={(rowNo)=>{
                                this.state.titleProc = this.state.data.existTable.rows[rowNo].row[0].value
                                this.state.data.title = this.state.titleProc
                                this.state.data.processId = this.state.data.existTable.rows[rowNo].dbID
                                this.state.data.itProcessID = 0
                                if(Fetchers.isGoodArray(this.state.data.existTable.rows)){
                                    this.state.data.existTable.rows.forEach((r,index) => {
                                        r.selected = false
                                        if(index == rowNo){
                                            r.selected = !r.selected
                                        }
                                    });
                                }
                                this.onNextClick()
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
                            selectRow={(rowNo)=>{
                                this.state.data.title = this.state.titleProc + " - " + this.state.data.notExistTable.rows[rowNo].row[0].value
                                this.state.data.itProcessID = this.state.data.notExistTable.rows[rowNo].dbID

                                if(Fetchers.isGoodArray(this.state.data.notExistTable.rows)){
                                    this.state.data.notExistTable.rows.forEach((r,index) => {
                                        r.selected = false
                                        if(index == rowNo){
                                            r.selected = !r.selected
                                        }
                                    });
                                }
                                this.setState(this.state)
                            }}
                        />
                </Col>
            </Row>
        )
    }

    pageDictionaries(){
        if(this.state.data.existTable == undefined || this.state.data.notExistTable == undefined){
            return []
        }
        let ret = []
        var lbl = this.state.labels.titleexistdictionary
        if(this.state.data.currentPage == 4 || this.state.data.currentPage == 5){
            lbl = this.state.labels.titleexistdata
        }
        var lblno = this.state.labels.titlenotexistdictionary
        if(this.state.data.currentPage == 4 || this.state.data.currentPage == 5){
            lblno = this.state.labels.titlenotexistdata
        }
        ret.push(
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Label>{lbl}</Label>
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Label>{lblno}</Label>
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
                                this.state.data.nodeIdSelect=this.state.data.existTable.rows[rowNo].dbID
                                this.state.showComponent = true
                                this.loadSelectComponent()
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
                                let id = this.state.data.notExistTable.rows[row].dbID
                                //let url = this.state.data.notExistTable.rows[row].row[0].value
                                let api = "/api/admin/exchange/dictionary/import"
                                let lbl = this.state.labels.warningImportDictionary
                                if(this.state.data.currentPage == 4){
                                    lbl = this.state.labels.warningImportResource
                                    api = "/api/admin/exchange/resource/import"
                                }else if(this.state.data.currentPage == 5){
                                    lbl = this.state.labels.warningImportResConfig
                                    api = "/api/admin/exchange/dataconfig/import"
                                }
                                this.importSelectItem(lbl, id, api)
                            }}
                        />
                </Col>
            </Row>
        )
        ret.push(
            this.showSelectComponent()
        )
        return ret
    }

    pageWorkflows(){
        if(this.state.data.existTable == undefined || this.state.data.notExistTable == undefined){
            return []
        }
        let ret = []
        var lbl = this.state.labels.titleexistrecords
        var lblno = this.state.labels.titlenotexistrecords
        ret.push(
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Label>{lbl}</Label>
                </Col>
                <Col xs='12' sm='12' lg='4' xl='4'>
                    <Label>{lblno}</Label>
                </Col>
                <Col xs='12' sm='12' lg='2' xl='2' hidden={!this.state.data.showImpAll}>
                    <ButtonUni
                        label={this.state.labels.global_import_short}
                            onClick={()=>{
                                this.importSelectItem(this.state.labels.warningImportActivities, null, "/api/admin/exchange/workflows/importall")
                        }}
                        color="primary"
                    />
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
                            selectRow={(index)=>{
                                if(Fetchers.isGoodArray(this.state.data.existTable.rows)){
                                    this.state.data.existTable.rows.forEach((r,ind) => {
                                        r.selected = false
                                        if(ind == index){
                                            r.selected = !r.selected
                                            this.state.selectleft = true
                                        }
                                    });
                                }

                                if(index == 0){
                                    this.state.data.showAfter = false
                                    this.state.data.showBefore = false
                                }else if(index == 1){
                                    this.state.data.showAfter = true
                                    this.state.data.showBefore = false
                                }else{
                                    this.state.data.showAfter = true
                                    this.state.data.showBefore = true
                                }
                                this.setState(this.state)
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
                            selectRow={(index)=>{
                                if(Fetchers.isGoodArray(this.state.data.notExistTable.rows)){
                                    this.state.data.notExistTable.rows.forEach((r,ind) => {
                                        r.selected = false
                                        if(ind == index){
                                            r.selected = !r.selected
                                            this.state.data.nodeIdSelect = r.dbID
                                        }
                                    });
                                }
                                this.setState(this.state)
                            }}
                        />
                </Col>
            </Row>
        )
        return ret
    }

    pageResult(){
        let ret = []
        ret.push(
            <Row>
                <Col xs='12' sm='12' lg='12' xl='12'>
                    <Label>{this.state.labels.import_systemdata}</Label>
                </Col>
            </Row>
        )
        if(this.state.data.warnings.length > 0){
            if(Fetchers.isGoodArray(this.state.data.warnings)){
                this.state.data.warnings.forEach((w, index)=>{
                    ret.push(
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                <Label>{w}</Label>
                            </Col>
                        </Row>
                    )
                })
            }
        }
        var l = "/"+Navigator.tabSetName()+"#administrate/authorities"
        ret.push(
            <Row>
                <Col xs='12' sm='12' lg='12' xl='12'>
                    <Label>{this.state.labels.import_userrole} <a href={l} style={{cursor:'pointer'}}>{this.state.labels.authorities}</a></Label>
                </Col>
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
        if(this.state.data.currentPage == 5){
            return this.pageDictionaries()
        }
        if(this.state.data.currentPage == 6){
            return this.pageWorkflows()
        }
        if(this.state.data.currentPage == 7){
            return this.pageResult()
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
                                        this.state.showComponent = false;
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
                                this.state.showComponent = false;
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
                        <h4>
                            {this.state.labels.exchangeconfig}
                        </h4>
                    </Col>
                </Row>
                <Row>
                    <Col key='1top' xs='12' sm='12' lg='10' xl='10'>
                        <h4 className='ml-5'>
                            {this.state.data.title}
                        </h4>
                    </Col>
                    <Col key='1top' xs='6' sm='6' lg='1' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_help}
                            onClick={()=>{
                                window.open('/api/admin/help/impconfigprocess','_blank').focus()
                            }}
                            color="info"
                        />
                    </Col>
                    <Col key='3top' xs='6' sm='6' lg='1' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_cancel}
                            onClick={()=>{
                                window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                            }}
                            outline
                            color="info"
                        />
                    </Col>
                </Row>
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