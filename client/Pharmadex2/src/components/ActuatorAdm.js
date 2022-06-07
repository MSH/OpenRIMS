import React , {Component} from 'react'
import {Container, Row, Col, Button} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import ViewEdit from './form/ViewEdit'
import Spinner from './utils/Spinner'
/**
 * Import addresses
 */
class ActuatorAdm extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},
            labels:{
                global_cancel:'',
                global_save:'',
                healthEndpointStatus:'healthEndpointStatus',
                reload:""
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.headerFooter=this.headerFooter.bind(this)
        this.load=this.load.bind(this)
        this.buildData=this.buildData.bind(this)
        this.buildDataTwo=this.buildDataTwo.bind(this)
        this.buildDataThree=this.buildDataThree.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
                
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.load()
    }

    /**
     * Load data
     */
    load(){
        Fetchers.postJSON("/api/admin/actuator/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)

            Locales.createLabels(this, "literals")
            Locales.resolveLabels(this)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    buildData(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.keys)){
            this.state.data.keys.forEach((k, index)=>{
                if(index >= 0 && index < 10){
                    let fieldDTO = this.state.data.literals[k]
                    if(fieldDTO != undefined){
                        ret.push(
                            <Row key={index}>
                                <Col>
                                    <ViewEdit mode='text' attribute={k}
                                    component={this} 
                                    data={this.state.data.literals}
                                    rows="6"
                                    hideEmpty
                                    />
                                </Col>
                            </Row>
                        )
                    }
                }
            })
        }
        return ret;
    }

    buildDataTwo(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.keys)){
            this.state.data.keys.forEach((k, index)=>{
                if(index >= 10 && index < 19){
                    let fieldDTO = this.state.data.literals[k]
                    if(fieldDTO != undefined){
                        ret.push(
                            <Row key={index}>
                                <Col>
                                    <ViewEdit mode='text' attribute={k}
                                    component={this} 
                                    data={this.state.data.literals}
                                    rows="6"
                                    hideEmpty
                                    />
                                </Col>
                            </Row>
                        )
                    }
                }
            })
        }
        return ret;
    }

    buildDataThree(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.keys)){
            this.state.data.keys.forEach((k, index)=>{
                if(index >= 19){
                    let fieldDTO = this.state.data.literals[k]
                    if(fieldDTO != undefined){
                        ret.push(
                            <Row key={index}>
                                <Col>
                                    <ViewEdit mode='text' attribute={k}
                                    component={this} 
                                    data={this.state.data.literals}
                                    rows="6"
                                    hideEmpty
                                    />
                                </Col>
                            </Row>
                        )
                    }
                }
            })
        }
        return ret;
    }

    headerFooter(){
        return(
            <div className="mb-1 d-flex justify-content-end">
                <Button size="sm"
                className="mr-1" color="info"
                onClick={()=>{
                    window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                }}
                >{this.state.labels.global_cancel}</Button>{' '}
            </div>
        )
    }
    render(){
        if(this.state.data.keys == undefined || this.state.data.keys.size == 0 || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        {this.headerFooter()}
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.buildData()}
                    </Col>
                    <Col>
                        {this.buildDataTwo()}
                    </Col>
                    <Col>
                        {this.buildDataThree()}
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.headerFooter()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ActuatorAdm