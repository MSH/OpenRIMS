import React , {Component} from 'react'
import {Container, Row, Col,Breadcrumb,BreadcrumbItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Dictionary from './Dictionary'

/**
 * The main component to configure workflows
 */
class Workflows extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),  //my address for messages
            data:{
            },                //Dict2DTO
            labels:{
                processes:'',
                workflows:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.rigthDict=this.rigthDict.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.subject=='onSelectionChange' && data.to==this.state.identifier){
            if(data.from==this.state.data.masterDict.url){
                this.state.data.masterDict=data.data
                let dictId=0
                if(this.state.data.masterDict.table != undefined && Fetchers.isGoodArray(this.state.data.masterDict.table.rows)){
                    this.state.data.masterDict.table.rows.forEach((row,index) => {
                        if(row.selected){
                            dictId=row.dbID
                            
                        }
                    });
                }
                this.loader()
            }
            if(data.from==this.state.data.slaveDict.url){
                let param={
                    dictNodeId:data.data.prevSelected[0]
                }
                let paramStr=JSON.stringify(param)
                if(paramStr.length>2){
                    Navigator.navigate("administrate", "workflowconfigurator",paramStr)
                } 
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.loader();
        
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    loader(){
          var api = "/api/admin/stages/workflow"
       Fetchers.postJSONNoSpinner(api, this.state.data, (query,result)=>{
            this.state.data=result
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
    }

    rigthDict(){
        if(this.state.data.slaveDict == undefined){
            return []
        }
        if(this.state.data.slaveDict.url.length==0){
            return []
        }
        return(
            <Col xs='12' sm='12' lg='6' xl='6'>
                <Row>
                    <Col>
                        <Dictionary
                            identifier={this.state.data.slaveDict.url}
                            recipient={this.state.identifier}
                            data={this.state.data.slaveDict}
                            display
                        />
                    </Col>
                </Row>
            </Col>
        )
    }

    render(){
        if(this.state.data.masterDict==undefined || this.state.labels.locale == undefined){
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
                                <h6 className="d-inline">{this.state.labels.workflows}</h6>
                            </BreadcrumbItem>
                        </Breadcrumb>  
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Dictionary
                            identifier={this.state.data.masterDict.url}
                            recipient={this.state.identifier}
                            data={this.state.data.masterDict}
                            display
                        />
                    </Col>
                   {this.rigthDict()}
                </Row>
                
            </Container>
        )
    }


}
export default Workflows
Workflows.propTypes={
    
}