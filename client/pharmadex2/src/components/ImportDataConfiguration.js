import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import ButtonUni from './form/ButtonUni'
import Thing from './Thing'

/**
 * Import Data Configuration from Excel file
 * Accept events:
 * DataConfigurationImportReload - reload the elecronic form. Please note, that the it is only way to do this
 * Issue events:
 * DataConfigurationImportCancel - data configuration import has been cancelled
 * DataConfigurationImportSuccess - data configuration import is successfull
 */
class ImportDataConfiguration extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{},        //ThingDTO
            labels:{
                upload_file:'',
                global_import_short:'',
                global_close:'',
                success:'',
                error:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
        this.runImport=this.runImport.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.from==this.props.recipient){
                if(data.subject=='DataConfigurationImportReload'){
                    this.load()
                }
            }
            if(data.to==this.state.identifier){
                if(data.subject=="savedByAction"){
                    if(data.data.valid){
                        this.state.data=data.data
                        this.runImport()
                    }
                }
                if(data.subject=="onSelectionChange"){
                    this.state.data=data
                }
            }
        }
    load(){
        Fetchers.postJSON(this.props.loadapi, this.state.data, (query,result)=>{
            this.state.data=result
            if(!this.state.data.valid){
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
            }
            this.setState(this.state)
            Navigator.message(this.state.identifier, '*', 'thingReload',this.state.data)
        })
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        Navigator.message(this.state.identifier, this.props.recipient,'DataConfigurationImportMounted',this.state.data)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    runImport(){
        Fetchers.postJSON(this.props.importapi, this.state.data, (query,result)=>{
            this.state.data=result
            if(this.state.data.valid){
                Navigator.message(this.state.identifier, this.props.recipient,'DataConfigurationImportSuccess',this.state.data)
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.success, color:'success'})
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
            }
            this.setState(this.state)
            Navigator.message(this.state.identifier, '*', 'thingReload',this.state.data)
        })
    }

    render(){
        if(this.state.labels.locale==undefined || this.state.data.valid==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='12' xl='12'>
                        <Thing
                            data={this.state.data}
                            recipient={this.state.identifier}
                            narrow
                            noload 
                        />
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='4' xl='4' >
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4' className="d-flex justify-content-end">
                            <ButtonUni
                                label={this.state.labels.global_import_short}
                                onClick={()=>{
                                    Navigator.message(this.state.identifier, "*", "saveAll", {})
                                }}
                                color="success"
                            />
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4' className="d-flex justify-content-end">
                            <ButtonUni
                                label={this.state.labels.global_close}
                                onClick={()=>{
                                    Navigator.message(this.state.identifier, this.props.recipient,'DataConfigurationImportCancel',this.state.data)
                                }}
                                color="info"
                            />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ImportDataConfiguration
ImportDataConfiguration.propTypes={
    recipient:PropTypes.string.isRequired,      //recipient for messages
    loadapi:PropTypes.string.isRequired,        //api to load a thing with file uploader
    importapi:PropTypes.string.isRequired,      //api to run import 
}