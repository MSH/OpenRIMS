import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Dictionary from './Dictionary'
import Thing from './Thing'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'

/**
 * It is a dummy component to create other components quickly
 * Just copy it
 */
class ResourceFilling extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{nodeId:this.props.nodeId,},
            thing:false,
            labels:{
                resource:'',
                items:'',
            },
            thingData:{},                           //ThingDTO
            dictData:{}, 
            dict:false,
            url:'',
            configUrl:'',
            dictUrl:'',
            description:'', 
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.prepareDict=this.prepareDict.bind(this)
        this.right=this.right.bind(this)
        this.dict=this.dict.bind(this)
       // this.closeThing=this.closeThing.bind(this)
    }

     /**
     * clothe the thing ( the right column)
     */
     /* closeThing(){
        let table=this.state.data.table
        if(Fetchers.isGoodArray(table.rows)){
            table.rows.forEach((row,index)=>{
                row.selected=false
            })
        }
        this.state.thing=false
        this.state.dict=false
        this.setState(this.state)
    } */
    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            if(data.subject=='resourceSaved'){
                this.state.thing=false
                this.closeThing()
            }
        }  
         if(data.to=='*'){
            if(data.subject=='onDictionaryReloaded'){
                Navigator.message(this.state.identifier,'*','thingReload',{})
            }
        } 
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.loader()
        Locales.resolveLabels(this)
    }
    componentDidUpdate(){
        if(this.state.data.nodeId != this.props.nodeId){
            this.state.data.nodeId=this.props.nodeId
            this.loader()
        }
    }
    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

loader(){
    Fetchers.postJSONNoSpinner("/api/admin/resource/thing/prepare", this.state.data, (query, result)=>{
        this.state.thingData=result
        this.state.thing=true
        this.prepareDict()
        this.setState(this.state) 
    })
}
prepareDict(){
    Fetchers.postJSONNoSpinner("/api/admin/resource/dictionary/prepare", this.state.thingData, (query, result)=>{
        this.state.dictData=result
        if(this.state.dictData.valid){
        this.state.dict=true
        this.setState(this.state)
        }else{
            this.setState(this.state)
            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
        }
    })
}
//thing or nothing
right(){
    if(this.state.thing){
        let data=this.state.thingData
        data.repaint=true
        return(
            <Row>
                <Col>
                        <Thing
                            data={data} 
                            recipient={this.state.identifier}
                            narrow
                        />
                </Col>
                </Row>
        )
     }else{
        return []
    } 
}
dict(){
    if(this.state.dict){
        let data=this.state.dictData
        return(
            <Row>
                <Col>
                    <Row>
                        <Col>
                            <h5>{this.state.labels.items}</h5>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Dictionary identifier={data.url} data={data} />
                        </Col>
                    </Row>
                </Col>
            </Row>
        )
    }else{
        return []
    } 
}
    render(){
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <h6>{this.state.labels.resource}</h6>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.right()}
                    </Col>
                </Row> 
                <Row>
                    <Col>
                        {this.dict()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ResourceFilling
ResourceFilling.propTypes={
    nodeId :PropTypes.number.isRequired,
    recipient:PropTypes.string.isRequired,      //recipient of messages from this thing
}