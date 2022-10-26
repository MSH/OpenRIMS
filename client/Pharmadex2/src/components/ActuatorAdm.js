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
                saved:'',
                global_fail:'',
                healthEndpointStatus:'healthEndpointStatus',
                reload:""
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.headerFooter=this.headerFooter.bind(this)
        this.load=this.load.bind(this)
        this.postLoaded=this.postLoaded.bind(this)
        this.buildDataSla=this.buildDataSla.bind(this)
        this.buildData=this.buildData.bind(this)
        this.buildDataTwo=this.buildDataTwo.bind(this)
        this.buildDataThree=this.buildDataThree.bind(this)
        this.buildDataFour=this.buildDataFour.bind(this)
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

    postLoaded(){
        Locales.createLabels(this, "sla")
        Locales.createLabels(this, "literals")
        Locales.resolveLabels(this)
    }

    /**
     * Load data
     */
    load(){
        Fetchers.postJSON("/api/admin/actuator/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)

            this.postLoaded();
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    buildDataSla(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.keysSLA)){
            let sla = []
            this.state.data.keysSLA.forEach((k, index)=>{
                let fieldDTO = this.state.data.sla[k]
                if(fieldDTO != undefined){
                    sla.push(
                        <Col>
                            <ViewEdit mode='number' attribute={k}
                                    component={this} 
                                    data={this.state.data.sla}
                                    rows="6"
                                    hideEmpty
                                    />
                        </Col>
                    )
                }
            });
            ret.push(
                <Row>
                    {sla}
                    <Col>
                        <a href="/admin#administrate/actuator" onClick={()=>{window.open(this.state.data.linkReport,'_blank');}} 
                            hidden={this.state.data.linkReport == ""}>Open in DataStudio</a>
                    </Col>
                </Row>
            )
        }
        return ret;
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
                if(index >= 19 && index < 25){
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

    buildDataFour(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.keys)){
            this.state.data.keys.forEach((k, index)=>{
                if(index >= 25){
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
                 <Button size="sm"
                className="mr-1" color="primary"
                onClick={()=>{
                    Fetchers.postJSON("/api/admin/metrics/save", this.state.data, (query,result)=>{
                        if(result.valid){
                            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.saved, color:'success'})
                        }else{
                            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.global_fail, color:'warning'})
                        }
                    })
                }}
                >{this.state.labels.global_save}</Button>{' '}
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
                {this.buildDataSla()}
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
                    <Col>
                        {this.buildDataFour()}
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