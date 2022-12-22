import React , {Component} from 'react'
import {Container, Modal, ModalHeader, ModalBody, ModalFooter, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Thing from './Thing'

/**
 * Application's data page by page initially collapsed, read only
 * 
 */
class ApplicationData extends Component{
    constructor(props){
        super(props)
        this.state={
            data:this.props.data,
            modal:this.props.modal,
            identifier:Date.now().toString(),
            labels:{
                more:'',
                global_close:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadPath=this.loadPath.bind(this)
        this.paintThings=this.paintThings.bind(this)
        this.toggle=this.toggle.bind(this)
        this.toggleModal=this.toggleModal.bind(this)
    }

    /**
     * Listen openApplicationData event
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.from==this.props.recipient){
                if(data.subject='openApplicationData'){
                    this.state.data=data.data
                    this.state.modal=this.props.modal
                    if(this.state.data.nodeId>0){
                        this.loadPath()
                    }else{
                        this.setState(this.state)   //cleanup
                    }
                }
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        if(!this.props.noLoad && this.state.data.nodeId>0){
            this.loadPath();
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    loadPath(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/activity/path", this.state.data, (query,result)=>{
            this.state.data=result
            this.state.fullcollapse = []
            if(Fetchers.isGoodArray(this.state.data.path)){
                this.state.data.path.forEach((thing, index)=>{
                    this.state.fullcollapse.push({
                        ind:index,
                        collapse:false
                    })
                })
            }
            this.setState(this.state)
        } )
    }

    /**
     * Paint things from the path
     */
        paintThings(){
            let ret = []
            if(Fetchers.isGoodArray(this.state.data.path)){
                this.state.data.path.forEach((thing, index)=>{
                    thing.readOnly=true
                    ret.push(
                        <h4 className='btn-link' key={index+1000} style={{cursor:"pointer"}} 
                            onClick={()=>{this.toggle(index)}}>{thing.title}</h4>
                    )
                    if(this.state.fullcollapse[index].collapse){
                        ret.push(
                            <Thing key={index+"_thing"}
                            data={thing}
                            recipient={this.state.identifier}
                            readOnly={true}
                            narrow={this.props.narrow}
                            />
                        )
                    }
                })
            }
            return ret
        }
        /**
         * Which page open/close
         * @param {integer} ind 
         */
        toggle(ind) {
            if(this.state.data != undefined && this.state.data.path != undefined){
                if(Fetchers.isGoodArray(this.state.fullcollapse)){
                    this.state.fullcollapse.forEach((el, i)=>{
                        if(ind == i){
                            el.collapse = !el.collapse
                        }
                    })
                }
                this.setState(this.state);
            }
        }
    
    /**
     * Toggle modal representation
     */
    toggleModal() {
        this.state.modal=!this.state.modal
        if(!this.state.modal){
            Navigator.message(this.state.identifier, this.props.recipient, "onApplicationDataClose",{})
        }
        this.setState(this.state)
    }
    render(){
        if(this.state.data.nodeId==0){
            return []
        }
        let title=this.state.labels.more
        if(this.state.data.title != undefined){
            title=this.state.data.title
        }
        if(!this.props.modal){
            return(
                <Container fluid>
                    {this.paintThings()}
                </Container>
            )
        }else{
            return(
                <Modal isOpen={this.state.modal} toggle={this.toggleModal} size='lg'>
                    <ModalHeader toggle={this.toggleModal}>{title}</ModalHeader>
                    <ModalBody>
                        {this.paintThings()}
                    </ModalBody>
                    <ModalFooter>
                        <Button color="info" onClick={this.toggleModal}>{this.state.labels.global_close}</Button>
                    </ModalFooter>
                </Modal>
            )
        }
    }


}
export default ApplicationData
ApplicationData.propTypes={
    data:PropTypes.object.isRequired,        //ThingDTO, at least the nodeId should be non-zero
    recipient:PropTypes.string.isRequired,   //recipient for messaging
    noLoad:PropTypes.bool,                   //if true, the data is already loaded 
    narrow:PropTypes.bool,                    //if true - one 
    modal:PropTypes.bool                      //show in a modal box
}