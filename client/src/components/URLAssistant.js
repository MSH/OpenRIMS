import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import TableSearch from './utils/TableSearch'
import FieldInput from './form/FieldInput'
import ButtonUni from './form/ButtonUni'
import Dictionary from './Dictionary'
import Thing from './Thing'

/**
 * Provides assistance to construct or select URL
 *
 * assistant:PropTypes.oneOf(['dictionaries','data','workflow','activity', 'resource']).isRequired,    //for which assistant will be needed
 * select:PropTypes.bool   //false construct the URL, true, select the existing
 * 
 */
class URLAssistant extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{
                assistant:this.props.assistant,
                oldValue:this.props.value,
                title:this.props.title
            },            //URLAssistantDTO.java
            labels:{
                url_assistant:'',
                dictionaries:'',
                domain:'',
                subdomain:'',
                urls:'',
                search:'',
                url_assistance1:'',
                url_assistance2:'',
                global_cancel:'',
                global_help:'',
                form_ok:'',
                URL_DICTIONARY_NEW:'',
                URL_ANY:'',
                preview:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.selectDomain=this.selectDomain.bind(this)
        this.selectSubDomain=this.selectSubDomain.bind(this)
        this.urlInput=this.urlInput.bind(this)
        this.urlPreview=this.urlPreview.bind(this)
        this.clearPreview=this.clearPreview.bind(this)
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
        this.loader()
        Locales.resolveLabels(this)
        Locales.createLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * load/reload
     */
    loader(){
        Fetchers.postJSON('/api/admin/url/assist', this.state.data, (query,result)=>{
            this.state.data=result;
            this.setState(this.state)
        })
    }
    /**
     * Select a domain - list subdomains
     * @param {String} domain
     */
    selectDomain(domain){
        if(this.state.data.selectedDomain==domain){
            this.state.data.selectedDomain=''
        }else{
            this.state.data.selectedDomain=domain
        }
        this.state.data.selectedSubDomain=''
        this.state.data.selectedUrl=''
        this.state.data.oldValue=''
        this.state.data.subDomain.headers.headers=[]
        this.state.data.urls.headers.headers=[]
        this.loader()
    }
    /**
     * Select a subdomain - list URLs
     * @param {String} subDomain 
     */
    selectSubDomain(subDomain){
        if(this.state.data.selectedSubDomain==subDomain){
            this.state.data.selectedSubDomain=''
        }else{
            this.state.data.selectedSubDomain=subDomain
        }
        this.state.data.selectedUrl=''
        this.state.data.oldValue=''
        this.state.data.urls.headers.headers=[]
        this.loader()
    }

    /**
     * Selelct a URL
     * @param {string} url 
     */
    selectURL(url){
        if(this.state.data.selectedUrl==url){
            this.state.data.selectedUrl=''
        }else{
            this.state.data.selectedUrl=url
        }
        this.loader()
    }
    /**
     * clear preview area
     */
    clearPreview(){
        this.state.data.previewDict.url=''
        this.state.data.previewThing.url=''
        this.state.data.previewOther=''
        this.setState(this.state)

    }
    /**
     * 
     * @returns URL input field and control buttons
     */
    urlInput(){
        return (
            <Row>
            <Col xs='12' sm='12' lg='6' xl='6'>
                <FieldInput mode='textarea' attribute='url' component={this}/>
            </Col>
            <Col xs='12' sm='12' lg='2' xl='2'>
                <ButtonUni
                    onClick={
                        ()=>{
                            Fetchers.postJSON("/api/admin/url/assist/validate",this.state.data,(query,result)=>{
                                this.state.data=result
                                if(this.state.data.valid){
                                    let value=this.state.data.url.value
                                    Navigator.message(this.state.identifier,this.props.recipient
                                        ,this.props.assistant, value, window.opener)
                                    window.close()
                                }else{
                                    Navigator.message('*', '*', 'show.alert.pharmadex.2', 
                                        {mess:this.state.data.identifier, color:'danger'})
                                    this.setState(this.state)
                                }
                            })
                        }
                    } label={this.state.labels.form_ok} color="primary"
                />
            </Col>
            <Col xs='12' sm='12' lg='2' xl='2'>
                <ButtonUni
                    onClick={
                        ()=>{
                            this.clearPreview()
                            Fetchers.postJSON("/api/admin/url/assist/preview", this.state.data, (query,result)=>{
                                this.state.data=result
                                this.setState(this.state)
                            })
                        }
                    } label={this.state.labels.preview} color="success"
                />
            </Col>
            <Col xs='12' sm='12' lg='2' xl='2'>
                <ButtonUni
                    onClick={
                        ()=>{
                            window.opener.focus()
                            window.close()
                        }
                    } label={this.state.labels.global_cancel} color="info" outline 
                />
            </Col>
        </Row>
        )
    }
    /**
     * Preview dictionary or thing placed on URL, otherwise "preview is unavailable"
     */
    urlPreview(){
        if(this.state.data.previewDict.url.trim().length>0){
            return(
                <Dictionary identifier={this.state.identifier+'dict'} 
                    data={this.state.data.previewDict}
                    recipient={this.state.identifier}
                    display
                    noborder/>
            )
        }
        if(this.state.data.previewThing.url.trim().length>0){
            return(
                <Thing data={this.state.data.previewThing} identifier={this.state.identifier}/>
            )
        }
        return(<h4>{this.state.data.previewOther}</h4>)
    }
   
    render(){
        if(this.state.data.domain==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row className='mb-5 mt-2'>
                    <Col xs='12' sm='12' lg='10' xl='11'>
                        <h4>{this.state.labels.url_assistant}</h4>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_help}
                            onClick={()=>{
                                window.open('/api/admin/help/url/assistant','_blank').focus()
                            }}
                            color="info"
                        />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <h5>{this.state.labels.url_assistance1}</h5>
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='3' xl='3'>
                        <TableSearch
                             identifier={this.state.identifier+'domain'}
                             title={this.state.labels.domain}
                             label={this.state.labels.search}
                             tableData={this.state.data.domain}
                             loader={this.loader}
                             selectRow={(row)=>{
                             this.selectDomain(this.state.data.domain.rows[row].row[0].value)
                             }}
                        />
                        
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='3' hidden={this.state.data.selectedDomain.length==0}>
                        <TableSearch
                            identifier={this.state.identifier+'subdomain'}
                             title={this.state.labels.subdomain}
                             label={this.state.labels.search}
                             tableData={this.state.data.subDomain}
                             loader={this.loader}
                             selectRow={(row)=>{
                                this.selectSubDomain(this.state.data.subDomain.rows[row].row[0].value)
                             }}
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='6' hidden={this.state.data.selectedSubDomain.length==0}>
                        <TableSearch
                            identifier={this.state.identifier+'urls'}
                             title={this.state.labels.urls}
                             label={this.state.labels.search}
                             tableData={this.state.data.urls}
                             loader={this.loader}
                             selectRow={(row)=>{
                                this.selectURL(this.state.data.urls.rows[row].row[0].value)
                             }}
                        />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <Row className='mb-3'>
                            <Col>
                                <h5>{this.state.labels.url_assistance2 }</h5>
                            </Col>
                        </Row>
                        <Row>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                               {this.urlInput()}
                            </Col>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <Container fluid className={Pharmadex.settings.activeBorder}>
                                    {this.urlPreview()}
                                </Container>
                            </Col>
                        </Row>
                    </Col>
                </Row>       
            </Container>
        )
    }


}
export default URLAssistant
URLAssistant.propTypes={
    assistant:PropTypes.string.isRequired,    //for which assistant will be needed
    recipient:PropTypes.string.isRequired,  //for messages
    value:PropTypes.string,                   //previous value
    title:PropTypes.string,   //title of the window
   
}