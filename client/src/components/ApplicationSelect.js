import React , {Component} from 'react'
import {Row, Col, Container} from 'reactstrap'
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
                search:''
            },
            data:{},    //DictionaryDTO
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadData=this.loadData.bind(this)
        this.inspectionApplications=this.inspectionApplications.bind(this)
        this.selectRow=this.selectRow.bind(this)
        this.markSelected=this.markSelected.bind(this)
    }

    /**
     * listen for onSelectionChange broadcast from the dictionary
     */
      eventProcessor(event){
        /*let data=event.data
        if(data.subject=="onSelectionChange" && data.from==this.state.identifier+"_dict"){
            this.selectRow(data.data)
            this.state.data=data.data
            this.setState(this.state)
        }
        if(data.subject=="onDictionaryReloaded" && data.from==this.state.identifier+"_dict"){
            this.state.data=data.data
            this.markSelected()
            this.setState(this.state)
        }*/
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
                    Fetchers.writeLocaly("application_selected_row",row.dbID)
                }
            })
        }
    }
    markSelected(){
        let selected_row=Fetchers.readLocaly("application_selected_row",-1);
        if(Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach(row=>{
                row.selected=false
                if(row.dbID==selected_row){
                    row.selected=true
                }
            })
        }
        //Navigator.message(this.state.identifier,this.state.identifier+"_dict",'refreshData',{})
    }

    loadData(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName() +"/applications", this.state.data, (query,result)=>{
            this.state.data=result
            this.markSelected()
            /* let selected_row=Fetchers.readLocaly("application_selected_row",-1);
            if(this.state.data.table.rows.length > selected_row && selected_row>-1)
                this.state.data.table.rows[selected_row].selected=true */
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    /**
     * Show / hide the list of applications in accrdance with the dictionary choice
     */
    inspectionApplications(){
        let dictId=0
        if(this.state.data.table != undefined && Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach((row,index) => {
                if(row.selected){
                    dictId=row.dbID
                   // Fetchers.writeLocaly("application_selected_row",index);
                }
            });
        }
        if(dictId>0){
            return <ApplicationList dictItemId={dictId} recipient={this.state.identifier} /> 
        }else{
            return []
        }
    }

    render(){
        if(this.state.data.table == undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
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
                        <Row>
                            <Col>
                                <HostSchedule
                                    dictURL={'dictionary.host.applications'}
                                    recipient={this.state.identifier}
                                />
                            </Col>
                        </Row>
                    </Col>
                    <Col>
                        {this.inspectionApplications()}
                    </Col>
                </Row>
            </Container>
        )
    }
}
export default ApplicationSelect
ApplicationSelect.propTypes={
    
}