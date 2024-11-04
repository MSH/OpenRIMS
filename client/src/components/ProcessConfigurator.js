import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import PropTypes from 'prop-types'
import Dictionary from './Dictionary'
import ButtonUni from './form/ButtonUni'

/**
 * The main component to configure workflows
 */
class ProcessConfigurator extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),  //my address for messages
            data:{
            },                //Dict2DTO
            labels:{
                processes:'',
                workflows:'',
                global_help:'',
                global_add:'',
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
                Navigator.message(this.state.identifier, this.props.recipient, "onProcessOpen", this.state.data.masterDict)
                this.loader(false)
            }
            if(data.from==this.state.data.slaveDict.url){
               Navigator.message(this.state.identifier, this.state.data.slaveDict.url, "editWorkflowItem",{})
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.loader(true);
        
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    loader(usePropsForMaster){
            var api = "/api/admin/stages/workflow"
            if(this.props.master.url!=undefined && usePropsForMaster){
                this.state.data.masterDict=this.props.master
            }
            Fetchers.postJSONNoSpinner(api, this.state.data, (query,result)=>{
                this.state.data=result
                this.state.add=false
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
                            display={!this.state.edit}
                        />
                    </Col>
                </Row>
            </Col>
        )
    }
    topButtons(){
        return(
        <Row>
            <Col xs='12' sm='12' lg='6' xl='9'>
            </Col>
            <Col xs='12' sm='12' lg='2' xl='1'>
                <ButtonUni
                    label={this.state.labels.global_add}
                    onClick={()=>{
                       Navigator.message(this.state.identifier,this.state.data.slaveDict.url,"addDictItem",{})
                    }}
                    disabled={this.state.data.slaveDict.url.length==0}
                    color="primary"
                />
            </Col>
            <Col xs='12' sm='12' lg='2' xl='1'>
                <ButtonUni
                    label={this.state.labels.global_help}
                    onClick={()=>{
                        window.open('/api/admin/manual/workflow','_blank').focus()
                    }}
                    color="info"
                />
            </Col>
            <Col xs='12' sm='12' lg='2' xl='1'>
                <ButtonUni
                    label={this.state.labels.global_cancel}
                    onClick={()=>{
                        window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                    }}
                    outline
                    color="secondary"
                />
            </Col>
        </Row>
        )
    }

    render(){
        if(this.state.data.masterDict==undefined || this.state.labels.locale == undefined){
            return []
        }
        return(
            <Container fluid>
                {this.topButtons()}
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
export default ProcessConfigurator
ProcessConfigurator.propTypes={
    recipient:PropTypes.string.isRequired,
    master:PropTypes.object                      //stored state of the master dict
}