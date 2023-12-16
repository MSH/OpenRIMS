import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import TableSearch from './utils/TableSearch'
import HostSchedule from './HostSchedule'
import ApplicationList from './ApplicationList'
import PermitList from './PermitList'

/**
 * Inspections dashboard for a business user
 * Just copy it
 */
class InspectionSelect extends Component{
    constructor(props){
        super(props)
        this.dictionary="appInspectionListDictionary"
        this.state={
            identifier:Date.now().toString(),
            data : {},                          //DictionaryDTO
            labels:{
                inspections:'',
                clickforinspect : '',
                search:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.applications=this.applications.bind(this)
        this.permits=this.permits.bind(this)
        this.selectedDictID=this.selectedDictID.bind(this)
        this.addInspection=this.addInspection.bind(this)
        this.selectRow=this.selectRow.bind(this)
        this.markSelected=this.markSelected.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        /*if(data.subject=="onSelectionChange" && data.from==this.state.identifier+"_dict"){
            this.selectRow(data.data)
            this.state.data=data.data
            this.setState(this.state)
        }*/
        if(data.to==this.state.identifier && data.subject=='onPermitSelected'){
            this.addInspection(data.data)
        }
        /*if(data.subject=="onDictionaryReloaded" && data.from==this.state.identifier+"_dict"){
            this.state.data=data.data
            this.markSelected()
            this.setState(this.state)
        }*/
    }

    /**
     * Run a new ask for inspection application
     * @param {ID of selected application Data} applDataID 
     */
       addInspection(applDataID){
        this.state.data.dictItemId=this.selectedDictID()
        let data={
            dataId:0,
            url:'',                                     //url of an application, i.e. application.guest, deprecated in favor of applDictNodeId
            applDictNodeId:this.state.data.dictItemId,  //id of dictionary node that describes an application
            historyId:0,                                //id of the histry record to determine activity and data. Zero means new
            modiUnitId:applDataID,                      //id of data unit selected to modify
            prefLabel:'',                               //preflabel by default
        }
        let param = JSON.stringify(data)
        Navigator.navigate(Navigator.tabName(),"applicationstart",param)
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        Locales.createLabels(this)
        this.loadData()
    }
    selectRow(selRowNo){
        if(Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach((row, index)=>{
                row.selected = false
                if(index == selRowNo){
                    row.selected = true
                    Fetchers.writeLocaly("application_inspection_selected_row",row.dbID)
                }
            })
        }
    }
    markSelected(){
        let selected_row=Fetchers.readLocaly("application_inspection_selected_row",-1);
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
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName() +"/applications/inspections", this.state.data, (query,result)=>{
            this.state.data=result
            this.markSelected()
            /* let selected_row=Fetchers.readLocaly("application_inspection_selected_row",-1);
            if(this.state.data.table.rows.length > selected_row && selected_row>-1)
                this.state.data.table.rows[selected_row].selected=true */
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    /**
     * Return ID of a row selected in the dictionary
     * Set it to a local storage var
     * 0 - if no selections
     */
    selectedDictID(){
        let dictId=0
        if(this.state.data.table != undefined && Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach((row,index) => {
                if(row.selected){
                    dictId=row.dbID
                    //Fetchers.writeLocaly("application_inspection_selected_row",index);
                }
            });
        }
        return dictId
    }

    /**
     * Show / hide the list of applications in accrdance with the dictionary choice
     * Any given inspection application has own data and  
     *
     */
       applications(){
        let dictId=this.selectedDictID()
        if(dictId>0){
            return <ApplicationList dictItemId={dictId} recipient={this.state.identifier} amend noadd/> 
        }else{
            return []
        }
    }
    /**
     * Show, hide a list of the possible permits for selection
     */
    permits(){
        let dictId=this.selectedDictID()
        if(dictId>0){
            return <PermitList dictItemId={dictId} recipient={this.state.identifier} hint={this.state.labels.clickforinspect}/> 
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
                {/*<Row>
                    <Col>
                        <h5>{this.state.labels.inspections}</h5>
                    </Col>
        </Row>*/}
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
                        <Row>
                            <Col>
                            <HostSchedule
                                    dictURL={'dictionary.host.inspections'}
                                    recipient={this.state.identifier}
                                />                         
                            </Col>
                        </Row>
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col>
                                {this.applications()}
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                {this.permits()}
                            </Col>
                        </Row>
                        
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default InspectionSelect
InspectionSelect.propTypes={
    
}