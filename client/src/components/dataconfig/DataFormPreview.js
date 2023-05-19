import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import ButtonUni from '../form/ButtonUni'
import Thing from '../Thing'

/**
 * Preview a data form
 */
class DataFormPreview extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{                              //DataPreviewDTO
                nodeId:this.props.nodeId
            },
            labels:{
                cancel:'',
                validate:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadData=this.loadData.bind(this)
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
        this.loadData()
        Locales.resolveLabels(this)
    }

    loadData(){
        Fetchers.postJSONNoSpinner("/api/admin/data/collection/definition/preview", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        if(this.state.data.thing == undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='10' xl='10'></Col>
                    <Col xs='12' sm='12' lg='1' xl='1'>
                        <ButtonUni
                                label={this.state.labels.validate}
                                color='success'
                                onClick={()=>{
                                    Navigator.message(this.state.identifier, "*", "validateThing", {})
                                }}
                            />
                    </Col>
                    <Col xs='12' sm='12' lg='1' xl='1'>
                        <ButtonUni
                                label={this.state.labels.cancel}
                                color='info'
                                onClick={()=>{
                                    let data={
                                        nodeId:this.props.nodeId
                                    }
                                     let param=JSON.stringify(data)

                                    Navigator.navigate("administrate", "dataconfigurator",param)
                                }}
                            />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <h2>{this.state.data.thing.url}</h2>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Thing data={this.state.data.thing} recipient={this.state.identifier} />
                    </Col>
                </Row>

            </Container>
        )
    }


}
export default DataFormPreview
DataFormPreview.propTypes={
    nodeId:PropTypes.number.isRequired,                 //node id of a data collection
}