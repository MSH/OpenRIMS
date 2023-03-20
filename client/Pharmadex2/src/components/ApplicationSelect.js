import React , {Component} from 'react'
import {Row, Col, Container} from 'reactstrap'
import Fetchers from './utils/Fetchers'
import Locales from './utils/Locales'
import Dictionary from './Dictionary'
import ApplicationList from './ApplicationList'
import Navigator from './utils/Navigator'

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
            },
            data:{},    //DictionaryDTO
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadData=this.loadData.bind(this)
        this.applications=this.applications.bind(this)
    }

    /**
     * listen for onSelectionChange broadcast from the dictionary
     */
      eventProcessor(event){
        let data=event.data
        if(data.subject=="onSelectionChange" && data.from==this.dictionary){
            this.state.data=data.data
            this.setState(this.state)
            //this.loadData()
        }
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loadData()
        
    }

    loadData(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName() +"/applications", this.state.data, (query,result)=>{
            this.state.data=result
            let selected_row=Fetchers.readLocaly("application_selected_row",1);
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
        let dictId=0
        if(this.state.data.table != undefined && Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach((row,index) => {
                if(row.selected){
                    dictId=row.dbID
                    Fetchers.writeLocaly("application_selected_row",index);
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
                        <Dictionary
                            identifier={this.dictionary}
                            data={this.state.data}
                            display
                        />
                    </Col>
                    <Col>
                        {this.applications()}
                    </Col>
                </Row>
            </Container>
        )
    }
}
export default ApplicationSelect
ApplicationSelect.propTypes={
    
}