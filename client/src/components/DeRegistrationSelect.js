import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import TableSearch from './utils/TableSearch'
import ApplicationList from './ApplicationList'
import DeregistrationAdd from './DeregistrationAdd'

/**
 * Responsible for de-registration applications
 */
class DeRegistrationSelect extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},
            labels:{
                search:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.derRegistrationAdd=this.derRegistrationAdd.bind(this)
        this.selectRow=this.selectRow.bind(this)
        this.markSelected=this.markSelected.bind(this)
        this.loadData=this.loadData.bind(this)
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

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loadData()
    }

    loadData(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/deregistration", this.state.data, (query,result)=>{
            this.state.data=result
            this.markSelected()
            /* let selected_row=Fetchers.readLocaly("deregistr_selected_row", 0);
            if(this.state.data.table.rows.length > 0)
                this.state.data.table.rows[selected_row].selected=true */
            this.setState(this.state)
        })
    }

    selectRow(selRowNo){
        if(Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach((row, index)=>{
                row.selected = false
                if(index == selRowNo){
                    row.selected = true
                    Fetchers.writeLocaly("deregistr_selected_row",row.dbID)
                }
            })
        }
    }
    markSelected(){
        let selected_row=Fetchers.readLocaly("deregistr_selected_row",-1);
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
                    //Fetchers.writeLocaly("deregistr_selected_row",index);
                }
            });
        }
        if(amdTypeId>0){
            return <ApplicationList dictItemId={amdTypeId} recipient={this.state.identifier} amend noadd/> 
        }else{
            return []
        }
    }
    /**
     * Get objects to de-registration
     * @returns table with the list of appropriative object data
     */
    derRegistrationAdd(){
        let applTypeId=0
        if(this.state.data.table != undefined && Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach(row => {
                if(row.selected){
                    applTypeId=row.dbID
                }
            });
        }
        if(applTypeId>0){
            return <DeregistrationAdd dictItemId={applTypeId} recipient={this.state.identifier} />
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
                    <Col>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                {this.applications()}
                            </Col>
                        </Row>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                {this.derRegistrationAdd()}
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default DeRegistrationSelect
DeRegistrationSelect.propTypes={

}