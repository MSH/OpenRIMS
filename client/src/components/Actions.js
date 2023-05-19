import React , {Component} from 'react'
import {Container, Row, Col, Breadcrumb,BreadcrumbItem} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Dictionary from './Dictionary'

/**
 * System dictionary to rename/explain process actions
 */
class Actions extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},           //DictionaryDTO
            labels:{
                processes:'',
                label_actions:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.loader()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * load actions dictionary
     */
    loader(){
        Fetchers.postJSONNoSpinner("/api/admin/processes/actions", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }
    render(){
        if(this.state.data.table==undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <Breadcrumb>
                            <BreadcrumbItem className="d-inline">
                            <div className="btn btn-link p-0 border-0"
                                    onClick={()=>{
                                        window.location='/admin#administrate'
                                    }}
                            >
                                <h6 className="d-inline">{this.state.labels.processes}</h6>
                            </div>
                            </BreadcrumbItem>
                            <BreadcrumbItem className="d-inline">
                                <h6 className="d-inline">{this.state.labels.label_actions}</h6>
                            </BreadcrumbItem>
                        </Breadcrumb>  
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Dictionary
                            identifier={this.state.data.url} data={this.state.data} 
                        /> 
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default Actions
Actions.propTypes={
    
}