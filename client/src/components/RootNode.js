import React , {Component} from 'react'
import {Container,Row, Col, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import FieldInput from './form/FieldInput'
import ButtonUni from './form/ButtonUni'
import Pharmadex from './Pharmadex'
import FieldDisplay from './form/FieldDisplay'

/**
 * A component responsible for edit/display a root node
 * Answers to askData by RootNodeDTO
 * @example
 * <RootNode identifier={this.props.identifier+".node"}
                                    rootId={this.pdops.data.rootId}         
                                    onCancel={()=>{this.state.edit=false
                                                   this.setState(this.state)}
                                    }
                        />
 */
class RootNode extends Component{
    constructor(props){
        super(props)
        this.state={
            labels:{
                save:'',
                cancel:'',
                global_suspend:'',
                assist:'',
            },
            data:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
        this.buildButtons=this.buildButtons.bind(this)
    }

    /**
     * Processor for all events
     * @param {object} event 
     */
     eventProcessor(event){
        let data=event.data
        if(data.subject=="URL_ASSIST_OK"){              //from URL assistance
            this.state.data.url.value=event.data.data
            this.setState(this.state)
        }
        if(data.from != this.props.identifier && (data.to==data.to==this.props.identifier)){
            if(data.subject=="askData"){    //It seems as abandomed and unusable
                Navigator.message(this.props.identifier,from, "onGetData", this.state.data)
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor);
        this.load()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    componentDidUpdate(){
        if(this.state.data.rootId != this.props.rootId){
            this.load()
        }
    }
    /**
     * create or load literals in object of DictElementDTO class
     */
    load(){
        this.state.data.rootId=this.props.rootId
        Fetchers.postJSONNoSpinner("/api/admin/root/node/load", this.state.data, (query,result)=>{
            this.state.data=result
            Locales.createLabels(this)
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
    }

    buildButtons(){
        return (
            <Row>
                <Col xs='12' sm='12' lg='6' xl='8'>
                </Col>
                <Col xs='12' sm='12' lg='3' xl='2'>
                    <ButtonUni
                        label={this.state.labels.save}
                        color='primary'
                        onClick={()=>{
                            Fetchers.postJSONNoSpinner("/api/admin/root/node/save", this.state.data,(query,result)=>{
                                this.state.data=result
                                if(this.state.data.valid){
                                    this.props.onCancel()
                                }else{
                                    this.setState(this.state)
                                }
                            })
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='3' xl='2'>
                    <ButtonUni
                        outline
                        label={this.state.labels.cancel}
                        color='secondary'
                        onClick={this.props.onCancel}
                    />
                </Col>
            </Row>
        )
    }

    render(){
        if(this.state.data.url == undefined
            || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid className={Pharmadex.settings.activeBorder}>
                {this.buildButtons()}
                <Row className='mt-4'>
                    <Col>   
                        <FieldInput mode='text' attribute='prefLabel' component={this}/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <FieldInput mode='textarea' lines={4} attribute='description' component={this}/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Row>
                            <Col>
                                <FieldDisplay mode='text' attribute='url' component={this}/>
                            </Col>
                        </Row>
                    </Col>
                </Row>
                <Row hidden={!this.state.data.gisvisible}>
                    <Col>   
                        <FieldInput mode='text' attribute='gisLocation' component={this}/>
                    </Col>
                </Row>
                <Row hidden={!this.state.data.gisvisible}>
                    <Col>   
                        <FieldInput mode='text' attribute='zoom' component={this}/>
                    </Col>
                </Row>
                <Row style={{height:'20px'}}></Row>
            </Container>
        )
    }


}
export default RootNode
RootNode.propTypes={
    identifier: PropTypes.string.isRequired,    //unique name of the instance of component for messages.
    rootId:PropTypes.number.isRequired,         //node id
    onCancel:PropTypes.func.isRequired,         //cancel callback
    display:PropTypes.bool,                     // display only
}