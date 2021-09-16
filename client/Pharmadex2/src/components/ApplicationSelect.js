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
 * and, then, refresh a list of activities (left)
 * @example
 * <ApplicationSelect />
 */
class ApplicationSelect extends Component{
    constructor(props){
        super(props)
        this.dictionary="appListDictionary"
        this.state={
            labels:{
                manageapplications:'',
                global_cancel:'',
            },
            data:{},
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadData=this.loadData.bind(this)
    }

    /**
     * listen for onSelectionChange broadcast from the dictionary
     */
      eventProcessor(event){
        let data=event.data
        if(data.subject=="onSelectionChange" && data.from==this.dictionary){
            this.state.data[this.dictionary]=data.data
            this.loadData()
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
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        if(this.state.data[this.dictionary] == undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <Dictionary
                            identifier={this.dictionary}
                            data={this.state.data[this.dictionary]}
                            display
                        />
                    </Col>
                    <Col>
                        <div hidden={this.state.data.applications.url.length==0}>
                            <ApplicationList
                                data={this.state.data.applications}
                            />
                        </div>
                    </Col>
                </Row>
            </Container>
        )
    }
}
export default ApplicationSelect
ApplicationSelect.propTypes={
    
}