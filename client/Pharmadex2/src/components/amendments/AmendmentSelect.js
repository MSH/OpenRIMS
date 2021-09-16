import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import SelectObject from './SelectObject'
import SelectChapter from './SelectChapter'
import Thing from '../Thing'
import Pharmadex from '../Pharmadex'

/**
 * Responsible to select a thing to amend
 * Issue messages onThingSelected and onThingDeselected depends 
 * @example
 *  let data={
            nodeId:this.state.applId,
            chapterId:this.state.chapId,
        }
 *  <AmendmentSelect data={data} recipient={this.state.identifier} />
 */
class AmendmentSelect extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:this.props.data,
            labels:{
                item_requested:'',
                chapter:'',
                preview:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.chapter=this.chapter.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=='onObjectSelected'){
                    this.state.data.appl={nodeId:data.data}
                    this.state.data.chapter={nodeId:0}
                    if(this.state.data.appl.nodeId>0){
                        Fetchers.postJSON("/api/guest/thing/load", this.state.data.appl, (query,result)=>{
                            this.state.data.appl=result
                            this.state.data.title=result.literals["prefLabel"].value
                            Navigator.message(this.state.identifier, this.props.recipient, "onAmendmentSelect", this.state.data)
                            this.setState(this.state)
                        })
                    }else{
                        this.state.data.title=''
                        this.setState(this.state)
                    }
                    Navigator.message(this.state.identifier, this.props.recipient, "onAmendmentSelect", this.state.data)
                }
                if(data.subject=='onChapterSelected'){
                    this.state.data.chapter.nodeId=data.data.id
                    this.state.data.chapterTitle=data.data.title
                    Navigator.message(this.state.identifier, this.props.recipient, "onAmendmentSelect", this.state.data)
                    this.setState(this.state)
                }
            }
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    chapter(){
        if(this.state.data.appl.nodeId>0){
            return(
                <Col xs='12' sm='12' lg='5' xl='5' className={Pharmadex.settings.activeBorder}>
                    <Row>
                        <Col className='bg-light'>
                            <h5>{this.state.labels.chapter}</h5>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <SelectChapter nodeId={this.state.data.appl.nodeId} chapterId={this.state.data.chapter.nodeId} recipient={this.state.identifier} />
                        </Col>
                    </Row>
                </Col>
            )
        }else{
            return []
        }
    }

    render(){
        if(this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6' className={Pharmadex.settings.activeBorder}>
                        <Row>
                            <Col className='bg-light'>
                                <h5>{this.state.labels.item_requested}</h5>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <SelectObject nodeId={this.props.data.appl.nodeId} recipient={this.state.identifier} />
                            </Col>
                        </Row>
                    </Col>
                    {this.chapter()}
                </Row>
            </Container>
        )
    }


}
export default AmendmentSelect
AmendmentSelect.propTypes={
    data:PropTypes.object.isRequired,               //AmendmentDTO
    recipient:PropTypes.string.isRequired,          //recipient for messages (identifier)
}