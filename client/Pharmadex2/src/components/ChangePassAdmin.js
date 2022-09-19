import React , {Component} from 'react'
import {Container, Row, Col, Button} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import Thing from './Thing'
import Alerts from './utils/Alerts'
import Spinner from './utils/Spinner'

class ChangePassAdmin extends Component{

    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},
            labels:{
                global_cancel:'',
                global_save:'',
                passwordchanged:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.headerFooter=this.headerFooter.bind(this)
        this.load=this.load.bind(this)
        this.changePassword=this.changePassword.bind(this)
        this.cancelClick=this.cancelClick.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            /*if(data.subject=="onSelectionChange"){
                this.state.data=data
            }*/
            if(data.subject=="savedByAction"){
                this.state.data=data.data
                this.changePassword()
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.load()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    headerFooter(){
        return(
            <div className="mb-1 d-flex justify-content-end">
                <Button size="sm"
                className="mr-1" color="success"
                onClick={()=>{
                    Spinner.show()
                    Navigator.message(this.state.identifier, "*", "saveAll", {})
                }}
                >{this.state.labels.global_save}</Button>{' '}

                <Button size="sm"
                className="mr-1" color="info"
                onClick={()=>{
                    this.cancelClick()
                }}
                >{this.state.labels.global_cancel}</Button>{' '}
            </div>
        )
    }

    cancelClick(){
        window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
    }

    load(){
        Fetchers.postJSON("/api/admin/changepass/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    changePassword(){
        Fetchers.postJSON("/api/admin/changepass/save", this.state.data, (query,result)=>{
            this.state.data = result
            if(this.state.data.valid){
                Navigator.message('*', '*', 'show.alert.pharmadex.2', this.state.labels.passwordchanged)
                this.cancelClick()
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                this.setState(this.state)
            }
        })
    }

    render(){
        if(this.state.data.nodeId==undefined || this.state.labels.locale==undefined){
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
                        <Thing data={this.state.data} recipient={this.state.identifier} noload/>
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
export default ChangePassAdmin