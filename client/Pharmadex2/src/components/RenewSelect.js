import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Dictionary from './Dictionary'
import ApplicationList from './ApplicationList'

/**
 * Responsible for renew objects and medicines existing in the database
 */
class RenewSelect extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},                            //DictionaryDTO
            labels:{
                renew:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
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
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/renewal", this.state.data, (query,result)=>{
            this.state.data=result
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
        let renTypeId=0
        if(this.state.data.table != undefined && Fetchers.isGoodArray(this.state.data.table.rows)){
            this.state.data.table.rows.forEach(row => {
                if(row.selected){
                    renTypeId=row.dbID
                }
            });
        }
        if(renTypeId>0){
            return <ApplicationList dictItemId={renTypeId} recipient={this.state.identifier} /> 
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
                                <h5>{this.state.labels.renew}</h5>
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
            </Container>
        )
    }


}
export default RenewSelect
RenewSelect.propTypes={

}