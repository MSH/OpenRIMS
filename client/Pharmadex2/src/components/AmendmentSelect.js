import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Dictionary from './Dictionary'
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
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadData=this.loadData.bind(this)
        this.applications=this.applications.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.subject=="onSelectionChange" && data.from==this.state.identifier+"_dict"){
            this.state.data=data.data
            this.setState(this.state)
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loadData()
        
    }

    loadData(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/amendments", this.state.data, (query,result)=>{
            this.state.data=result
            let selected_row=Fetchers.readLocaly("amendments_selected_row",1);
            if(this.state.data.table.rows.length > 0)
                this.state.data.table.rows[selected_row].selected=true
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
                    Fetchers.writeLocaly("amendments_selected_row", index);
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
                        <Row>
                            <Col>
                                <h5>{this.state.labels.amdmt_type}</h5>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <Dictionary
                                    data={this.state.data}
                                    identifier={this.state.identifier+"_dict"}
                                    display
                                />
                            </Col>
                        </Row>
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