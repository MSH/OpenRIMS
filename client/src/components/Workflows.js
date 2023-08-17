import React , {Component} from 'react'
import {Container, Row, Col,Breadcrumb,BreadcrumbItem} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Dictionary from './Dictionary'
import ButtonUni from './form/ButtonUni'

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
            edit:false,
            labels:{
                processes:'',
                workflows:'',
                global_help:'',
                global_cancel:'',
                processes:'',
                dictionaries:'',
                workflowguide:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.rigthDict=this.rigthDict.bind(this)
        this.topButtons=this.topButtons.bind(this)
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
        if(!this.state.edit){   //display workflow dictionary
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
        }else{  //edit workflow dictionary
            return(
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Row>
                        <Col>
                            <Dictionary
                                identifier={this.state.data.slaveDict.url}
                                recipient={this.state.identifier}
                                data={this.state.data.slaveDict}
                            />
                        </Col>
                    </Row>
                </Col>
            )
        }
    }
    topButtons(){
        let ret=[]
        ret.push(
            <Col key='1top'>
                <ButtonUni
                    label={this.state.labels.workflowguide}
                    onClick={()=>{
                        window.open('/api/admin/manual/workflow','_blank').focus()
                    }}
                    color="info"
                />
            </Col>
        )
        if(this.state.edit){
            ret.push(
                <Col key='2top'>
                <ButtonUni
                    label={this.state.labels.processes}
                    onClick={()=>{
                        this.state.edit=false
                        this.loader()
                    }}
                    color="primary"
                />
            </Col>
            )
        }else{
            ret.push(
                <Col key='2top'>
                <ButtonUni
                    label={this.state.labels.dictionaries}
                    onClick={()=>{
                        this.state.edit=true
                        this.setState(this.state)
                    }}
                    color="primary"
                    disabled={this.state.data.slaveDict.url.length==0}
                />
            </Col>
            )
        }
        ret.push(
            <Col key='3top'>
                <ButtonUni
                    label={this.state.labels.global_cancel}
                    onClick={()=>{
                        window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                    }}
                    outline
                    color="info"
                />
            </Col>
        )
        return ret
    }

    render(){
        if(this.state.data.masterDict==undefined || this.state.labels.locale == undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
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
                    {this.topButtons()}
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Dictionary
                            identifier={this.state.data.masterDict.url}
                            recipient={this.state.identifier}
                            data={this.state.data.masterDict}
                            display={!this.state.edit}
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