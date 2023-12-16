import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import TableSearch from './utils/TableSearch'
import ApplicationList from './ApplicationList'
import AmendmentAdd from './AmendmentAdd'

/**
 * Responsible for add new, send existing and inspect approved amendments
 */
class AmendmentSelect extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},                            //DictionaryDTO
            labels:{
                amdmt_type:'',
                search:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadData=this.loadData.bind(this)
        this.applications=this.applications.bind(this)
        this.selectRow=this.selectRow.bind(this)
        this.markSelected=this.markSelected.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
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

    selectRow(selRowNo){
        if(Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach((row, index)=>{
                row.selected = false
                if(index == selRowNo){
                    row.selected = true
                    Fetchers.writeLocaly("amendments_selected_row",row.dbID)
                }
            })
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loadData()
        
    }

    markSelected(){
        let selected_row=Fetchers.readLocaly("amendments_selected_row",-1);
        if(Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach(row=>{
                row.selected=false
                if(row.dbID==selected_row){
                    row.selected=true
                }
            })
        }
       // Navigator.message(this.state.identifier,this.state.identifier+"_dict",'refreshData',{})
    }

    loadData(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/amendments", this.state.data, (query,result)=>{
            this.state.data=result
            this.markSelected()
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
        let amdTypeId=0
        if(this.state.data.table != undefined && Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach((row,index) => {
                if(row.selected){
                    amdTypeId=row.dbID
                    //Fetchers.writeLocaly("amendments_selected_row", index);
                }
            });
        }
        if(amdTypeId>0){
            return <ApplicationList dictItemId={amdTypeId} recipient={this.state.identifier} amend noadd /> 
        }else{
            return []
        }
    }

    amendmentAdd(){
        let amdTypeId=0
        if(this.state.data.table != undefined && Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach(row => {
                if(row.selected){
                    amdTypeId=row.dbID
                }
            });
        }
        if(amdTypeId>0){
            return <AmendmentAdd dictItemId={amdTypeId} recipient={this.state.identifier} />
        }else{
            return []
        }
    }

    render(){
        if(this.state.data.table == undefined || this.state.labels.locale==undefined){
            return []
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
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.applications()}
                    </Col>
                </Row>
              
                <Row>
                    <Col xs='12' sm='12' lg='12' xl='12'>
                        {this.amendmentAdd()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default AmendmentSelect
AmendmentSelect.propTypes={

}