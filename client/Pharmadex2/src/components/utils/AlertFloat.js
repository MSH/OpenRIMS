import React , {Component} from 'react'
import {Row, Col, Alert} from 'reactstrap'

/**
 * Place an alert and show it 2 sec
 * Accepts message show.alert.pharmadex.2
 * @example
 * Navigator.message('*', '*', 'show.alert.pharmadex.2' {mess:'Saved', color:'success'})
 * @example
 * <AlertFloat />
 * @usage
 * 
 */
class AlertFloat extends Component{
    constructor(props){
        super(props)
        this.state={
            data:'',
            color:"success"
        }
        this.eventProcessor=this.eventProcessor.bind(this)
    }
    /**
     * Place alert, set timeout
     * @param {Window Event} event 
     */
         eventProcessor(event){
            let data=event.data
            if(data.subject=='show.alert.pharmadex.2'){
                if(data.data.mess != undefined){
                    this.state.data=data.data.mess
                }else{
                    this.state.data=data.data
                }
                if(data.data.color != undefined){
                    this.state.color=data.data.color
                }else{
                    this.state.color="success"
                }
                setTimeout(() => {
                    this.state.data=''
                    this.setState(this.state)
                }, 10000);
                this.setState(this.state)
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        if(this.state.data.length==0){
            return[]
        }
        return(
            <Row>
                <Col>
                    <Alert className="m-0 p-0" color={this.state.color}><small>{this.state.data}</small></Alert>
                </Col>
            </Row>
        )
    }
}
export default AlertFloat