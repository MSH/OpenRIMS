import React , {Component} from 'react'
import {Container, Row, Col, Button} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import Thing from './Thing'
import Alerts from './utils/Alerts'
import Spinner from './utils/Spinner'
/**
 * Import addresses
 */
class Import_A extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},
            labels:{
                global_cancel:'',
                global_save:'',
                askforimportrun:'',
                errorvariffile:'Error varification file!',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.headerFooter=this.headerFooter.bind(this)
        this.load=this.load.bind(this)
        this.verifyImport=this.verifyImport.bind(this)
        this.runImport=this.runImport.bind(this)
        this.reloadStatus=this.reloadStatus.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=="onSelectionChange"){
                    this.state.data=data
                }
                if(data.subject=="savedByAction"){
                    this.state.data=data.data
                    Alerts.warning(this.state.labels.askforimportrun,
                        ()=>{   //yes
                            //run import
                            this.verifyImport()
                        },
                        ()=>{   //no

                        })
                }
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.load()
    }

    verifyImport(){
        Fetchers.postJSON("/api/admin/importa/verif", this.state.data, (query, result)=>{
            this.state.data=result
            if(this.state.data.valid){
                Navigator.message('*', '*', 'show.alert.pharmadex.2', "File is OK. Start import data")
                window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                this.setState(this.state)
                this.runImport()
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.errorvariffile, color:'danger'})
                this.setState(this.state)
            }
        })
    }

    runImport(){
        clearTimeout()  
        this.setState(this.state)

        var data = this.state.data.url + "&&" + this.state.data.nodeId
        let formData = new FormData()
        formData.append('data', data)
        Fetchers.postForm("/api/admin/importa/run", formData, (formData, result)=>{   
        })
        const timeout = setTimeout(() => {
            this.reloadStatus();
        }, 10000); 
    }

    reloadStatus(){
        Fetchers.postJSONNoSpinner("/api/admin/importa/verifstatus",this.state.data,(query,result)=>{
            let s = this.state
            if(result){//import end
                Navigator.message('*', '*', 'show.alert.pharmadex.2', "End import data")
                this.setState( {data: this.state.data})
                clearTimeout()          
                let loop2 = setTimeout(this.loadData,60000)
            }else{
                clearTimeout()
                let loop1 = setTimeout(this.reloadStatus,1500)
                this.setState(this.state)
            }
            
        })
    }

    /**
     * Load data
     */
    load(){
        Fetchers.postJSON("/api/admin/import/adminunits/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
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
                    window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                }}
                >{this.state.labels.global_cancel}</Button>{' '}
            </div>
        )
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
export default Import_A