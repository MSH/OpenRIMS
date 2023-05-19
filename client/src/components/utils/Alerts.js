import React , {Component} from 'react'
import {Modal, ModalBody, ModalHeader,ModalFooter,Row,Col,Button} from 'reactstrap';
import Locales from './Locales';

/**
 * Provides Alert and Warning message with yes/no choice
 * Shoud be included to all pages of an application
 * See "show" and "warning" static methods
 */
class Alerts extends Component { 

    constructor(props) {
        super(props);
        this.state={
            labels:{
                    info:"",
                    success:"",
                    warning:"",
                    error:"",
                    global_yes:"",
                    global_no:""
            },
            modal: false,     
            message: "",
            type: 0,
            redirectUrl: ""
        }
        this.alertClass = this.alertClass.bind(this)
        this.alertHeader = this.alertHeader.bind(this)
        this.alertBody=this.alertBody.bind(this)
        this.hide = this.hide.bind(this)
        this.footer = this.footer.bind(this)
    }

    componentDidMount(){
        window.addEventListener("ua.com.theta.alert", event => this.controlAlert(event.detail)); 
        Locales.resolveLabels(this)
    }

    controlAlert(detail){
        let s = this.state
        s.message=detail.message
        s.modal=detail.modal
        s.type=detail.type
        s.redirectUrl=detail.redirectUrl
        if(s.type===100 || s.type===200){
            s.choiceYes=detail.choiceYes
            s.choiceNo=detail.choiceNo
        }
        this.setState(s)
    } 
    
    /**
     * show an alert from anything
     * @param {String} mess
     * @param {number} [type] 0-info, 1-success, 2-warning, 3-error, 
     * @param (String) redirectUrl to which url redirect 
     */
    static show(mess, type, redirectUrl){
        let mType=0;
        if(typeof type != "undefined"){
            mType=type;
        }
        let mRedirectUrl=""
        if(typeof type === "String"){
            mRedirectUrl = redirectUrl
        }
        var event = new CustomEvent("ua.com.theta.alert", {
            detail: {
                modal: true,     
                message: mess,
                type: mType,
                redirectUrl: mRedirectUrl
            }
          });     

        window.dispatchEvent(event); 
    }
    /**
     * Show warning message and propose yes/no choice
     * @param {string} mess
     * @param {function} choiceYes - mandatory
     * @param {function} choiceNo - mandatory
     * @example Alerts.warning(this.state.labels.areyousure, ()=>{actions on "yes"}, ()=>{actions on "no"})
     */
    static warning(mess,choiceYes, choiceNo){
        let type = 100;
        if(typeof choiceNo == "undefined"){
            type=200
        }
        var event = new CustomEvent("ua.com.theta.alert", {
            detail: {
                modal: true,     
                message: mess,
                type: type,
                choiceYes: choiceYes,
                choiceNo:choiceNo
            }
          });     

        window.dispatchEvent(event); 
    }

 /**
     * show an alert from anything
     * @param {String} mess
     * @param {function} choiceYes - mandatory
     * @param (String) redirectUrl to which url redirect 
     */
    static showSessionTimeOut(mess, choiceYes){
        var event = new CustomEvent("ua.com.theta.alert", {
            detail: {
                modal: true,     
                message: mess,
                type: 200,
                choiceYes: choiceYes,
            }
          });     

        window.dispatchEvent(event); 
    }

    /**
     * Calculate class od alert header
     */
    alertClass(){
        let type= this.state.type
        switch (type) {
            case 0:
                return("text-warning")
            case 1:
                return("text-success")
            case 2:
                return("text-warning")
            case 3:
                return("text-danger")
            case 100:
                return("text-warning")
            case 200:
                return("text-warning")
        }
    }
    /**
     * Create alert header
     */
    alertHeader(){
        let type= this.state.type
        switch (type) {
            case 0:
                return(this.state.labels.info)
            case 1:
                return(this.state.labels.success)
            case 2:
                return(this.state.labels.warning)
            case 3:
                return(this.state.labels.error)
            case 100:
                return(this.state.labels.warning)
            case 200:
                return(this.state.labels.warning)
        }
    }
    /**
     * Create alert body
     */
    alertBody(){
        let ret =  this.state.message;
        if(typeof ret === "string"){
            return ret;
        }else{
            if(typeof ret.message != "undefined"){
                let retu= ret.message
                if(typeof ret.stacktrace != "undefined"){
                    return retu +" " + ret.stacktrace;  
                }else{
                    return retu;
                }
            }
        }
    }
    /**
     * Hide alert box
     */
    hide(){
        let s = this.state
        s.modal=false
        this.setState(s)
    }
    /**
     * User pressed "Yes" button in warning alert 
     */
    answerYes(){
        if(typeof this.state.choiceYes === 'function'){
            this.state.choiceYes()
        }
        this.hide();
    }

    /**
     * User pressed "No" button in warning alert 
     */
    answerNo(){
        if(typeof this.state.choiceNo === 'function'){
            this.state.choiceNo()
        }

        this.hide();
    }

    /**
     * Generate a footer for yes/no warnings
     */
    footer(){
        let ret=""
        if(this.state.type===100){
            ret=<ModalFooter>
                    <Row>
                        <Col  className="col-6">
                            <Button color='primary' size="sm" className="float-right" onClick={()=>this.answerYes()}>{this.state.labels.global_yes} </Button>
                        </Col>
                        <Col  className="col-6">
                            <Button color='secondary' outline size="sm" className="float-right" onClick={()=>this.answerNo()}>{this.state.labels.global_no} </Button>
                        </Col>
                    </Row>
                </ModalFooter>
        }
        if(this.state.type===200){
            ret=<ModalFooter>
            <Row>
                <Col  className="col-6">
                    <Button color='primary' size="sm" className="float-right" onClick={()=>this.answerYes()}>{this.state.labels.global_yes} </Button>
                </Col>
            </Row>
        </ModalFooter>
        }
        return ret;
    }

    render() {  
         return (
            <div id="modal-root">   
              <Modal isOpen={this.state.modal}
                    toggle={()=>this.answerNo()}  >
                    <ModalHeader toggle={()=>this.answerNo()} className={this.alertClass()}>
                        {this.alertHeader()}                       
                    </ModalHeader>
                    <ModalBody>
                       {this.alertBody()}
                    </ModalBody>
                    {this.footer()}                
                </Modal>
          </div> 
         )              
       }
}

export default Alerts; 