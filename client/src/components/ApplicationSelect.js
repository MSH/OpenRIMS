import React , {Component} from 'react'
import {Row, Col, Container, Collapse} from 'reactstrap'
import Fetchers from './utils/Fetchers'
import Locales from './utils/Locales'
import TableSearch from './utils/TableSearch'
import ApplicationList from './ApplicationList'
import Navigator from './utils/Navigator'
import HostSchedule from './HostSchedule'

/**
 * Applications for guest user
 * Provides possibility to choice an application type from a left dictionary
 * and, then, refresh a list of activities (right)
 * @example
 * <ApplicationSelect />
 */
class ApplicationSelect extends Component{
    constructor(props){
        super(props)
        this.dictionary="appListDictionary"
        this.state={
            identifier:Date.now().toString(),
            labels:{
                manageapplications:'',
                global_cancel:'',
                search:'',
                /* application_info:'', */
                scheduled:'',
                global_archive:'',
            },
            data:{},    //DictionaryDTO
            showApp:false,
            showShc:false,
            showArch:false,
            dictId:0,
            countApp:'',
            countArch:'',
            countSch:'',
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadData=this.loadData.bind(this)
        this.inspectionApplications=this.applications.bind(this)
        this.inspectionArchive=this.inspectionArchive.bind(this)
        this.selectRow=this.selectRow.bind(this)
        this.markSelected=this.markSelected.bind(this)
    }

    /**
     * listen for onSelectionChange broadcast from the dictionary
     */
      eventProcessor(event){
        let data=event.data
        if(data.subject=="countApp"){
            this.state.countApp=data.data.count1
            this.setState(this.state)
        }
        if(data.subject=="countArch"){
            this.state.countArch=data.data.count2
            this.setState(this.state)
        }
        if(data.subject=="countSch"){
            this.state.countSch=data.data.count3
            this.setState(this.state)
        }
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loadData()
        
    }
    selectRow(selRowNo){
        if(Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach((row, index)=>{
                row.selected = false
                if(index == selRowNo){
                    row.selected = true
                    this.state.dictId=row.dbID
                    Fetchers.writeLocaly("application_selected_row",row.dbID)
                }
            })
        }
        if(this.state.dictId>0){
            this.state.showApp=true
            this.state.showShc=false
            this.state.showArch=false
        }
    }
    markSelected(){
        let selected_row=Fetchers.readLocaly("application_selected_row",-1);
        if(Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach(row=>{
                row.selected=false
                if(row.dbID==selected_row){
                    row.selected=true
                    this.state.dictId=row.dbID
                }
            })
        }
        //Navigator.message(this.state.identifier,this.state.identifier+"_dict",'refreshData',{})
    }

    loadData(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName() +"/applications", this.state.data, (query,result)=>{
            this.state.data=result
            this.markSelected()
            if(this.state.dictId>0){
                this.state.showApp=true
                this.state.showShc=false
                this.state.showArch=false
            }
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    /**
     * Show / hide the list of applications in accrdance with the dictionary choice
     */
    applications(){
        if(this.state.data.table != undefined && Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach((row,index) => {
                if(row.selected){
                    this.state.dictId=row.dbID
                }
            });
        }
        if(this.state.dictId>0){
            return <ApplicationList dictItemId={this.state.dictId} recipient={this.state.identifier} /> 
        }else{
            return []
        }
    }
   /**
     * Show / hide the list of applications Archive in accrdance with the dictionary choice
     */
   inspectionArchive(){
    // let dictId=0
    if(this.state.data.archive != undefined && Fetchers.isGoodArray(this.state.data.archive.rows)){
        this.state.data.archive.rows.forEach((row,index) => {
            if(row.selected){
                this.state.dictId=row.dbID
               // Fetchers.writeLocaly("application_selected_row",index);
            }
        });
    }
    if(this.state.dictId>0){
        return <ApplicationList dictItemId={this.state.dictId} recipient={this.state.identifier} archive /> 
    }else{
        return []
    }
}

    render(){
        if(this.state.data.table == undefined){
            return []
        }
        let awe="fas fa-caret-right"
        if(this.state.showApp){
            awe="fas fa-caret-down"
        }
        let awe1="fas fa-caret-right"
        if(this.state.showShc){
            awe1="fas fa-caret-down"
        }
        let awe2="fas fa-caret-right"
        if(this.state.showArch){
            awe2="fas fa-caret-down"
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <TableSearch 
                            label={this.state.labels.search}
                            tableData={this.state.data.table} 
                            loader={this.loadData}
                            title={this.state.data.home}
                            styleCorrector={(header)=>{
                                if(header=='pref'){
                                    return {width:'30%'}
                                }
                            }}
                            linkProcessor={(rowNo,cellNo)=>{
                                this.selectRow(rowNo)
                                this.setState(this.state)
                            }}
                            selectRow={(rowNo)=>{
                                this.selectRow(rowNo)
                                this.setState(this.state)
                            }}
                        />
                    </Col>
                    <Col hidden={this.state.dictId==0}>
                        {/* Applications live */}
                        <Row>
                            <Col className="btn btn-link p-0 border-0 d-flex justify-content-start"
                                onClick={()=>{
                                    this.state.showApp=!this.state.showApp
                                    if(this.state.showApp){
                                        this.state.showShc=false
                                        this.state.showArch=false
                                    }
                                    this.setState(this.state)
                                }}>
                                <Row>
                                    <Col>
                                        <h5 className="ml-3"><i className={awe}></i>{this.state.labels.manageapplications +' '+this.state.countApp}</h5>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                        <Collapse isOpen={this.state.showApp}>
                            <Row>
                                <Col>
                                    {this.applications()}
                                </Col>
                            </Row>
                        </Collapse>
                        {/* Archive */}
                        <Row>
                            <Col className="btn btn-link p-0 border-0 d-flex justify-content-start"
                                onClick={()=>{
                                    this.state.showArch=!this.state.showArch
                                    if(this.state.showArch){
                                        this.state.showApp=false
                                        this.state.showShc=false
                                    }
                                    this.setState(this.state)
                                }}>
                                <Row>
                                    <Col>
                                        <h5 className="ml-3"><i className={awe2}></i>{this.state.labels.global_archive+' '+this.state.countArch}</h5>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                        <Collapse isOpen={this.state.showArch}>
                            <Row>
                                <Col>
                                {this.inspectionArchive()}
                                </Col>
                            </Row>
                        </Collapse>
                        {/* Schedule */}
                        <Row>
                            <Col className="btn btn-link p-0 border-0 d-flex justify-content-start"
                                onClick={()=>{
                                    this.state.showShc=!this.state.showShc
                                    if(this.state.showShc){
                                        this.state.showApp=false
                                        this.state.showArch=false
                                    }
                                    this.setState(this.state)
                                }}>
                                <Row>
                                    <Col>
                                    <h5 className="ml-3"><i className={awe1}></i>{this.state.labels.scheduled+' '+this.state.countSch}</h5>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                        <Collapse isOpen={this.state.showShc}>
                            <Row>
                                <Col>
                                    <HostSchedule
                                        dictURL={'dictionary.host.applications'}
                                        recipient={this.state.identifier}
                                    />
                                </Col>
                            </Row>
                        </Collapse>
                    </Col>
                </Row>
            </Container>
        )
    }
}
export default ApplicationSelect
ApplicationSelect.propTypes={
    
}