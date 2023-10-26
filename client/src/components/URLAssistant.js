import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import CollectorTable from './utils/CollectorTable'
import SearchControl from './utils/SearchControl'

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
                select:this.props.select
            },            //URLAssistantDTO.java
            labels:{
                url_assistant:'',
                dictionaries:'',
                domain:'',
                subdomain:'',
                urls:'',
                search:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.selectDomain=this.selectDomain.bind(this)
        this.selectSubDomain=this.selectSubDomain.bind(this)
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
        this.loader()
    }
    /**
     * Select a URL from the list
     * @param {String} url 
     */
    selectUrl(url){
        if(this.state.data.selectedUrl==url){
            this.state.data.selectedUrl=''
        }else{
            this.state.data.selectedUrl=url
        }
        this.loader()
    }
   
    render(){
        if(this.state.data.domain==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        let header = this.state.labels.url_assistant + ". " + this.state.labels[this.props.assistant]
        return(
            <Container fluid>
                <Row className='mb-5 mt-2'>
                    <Col>
                        <h4>{header}</h4>
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='3' xl='3'>
                        <Row>
                            <Col xs='12' sm='12' lg='3' xl='3'>
                                <h5>{this.state.labels.domain}</h5>
                            </Col>
                            <Col xs='12' sm='12' lg='9' xl='9'>
                                <SearchControl label={this.state.labels.search} table={this.state.data.domain}
                                 loader={this.loader}
                                 disabled={this.state.data.domain.rows.length<10}/>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.domain}
                                    loader={this.loader}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    selectRow={(row)=>{
                                    this.selectDomain(this.state.data.domain.rows[row].row[0].value)
                                    }}
                                />
                            </Col>
                        </Row>
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='3' hidden={this.state.data.subDomain.rows.length==0}>
                        <Row>
                            <Col xs='12' sm='12' lg='3' xl='3' >
                                <h5>{this.state.labels.subdomain}</h5>
                            </Col>
                            <Col xs='12' sm='12' lg='9' xl='9'>
                                <SearchControl label={this.state.labels.search} table={this.state.data.subDomain} 
                                loader={this.loader}/>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.subDomain}
                                    loader={this.loader}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    selectRow={(row)=>{
                                    this.selectSubDomain(this.state.data.subDomain.rows[row].row[0].value)
                                    }}
                                />
                            </Col>
                        </Row>
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='6' hidden={this.state.data.urls.rows.length==0}>
                        <Row>
                            <Col xs='12' sm='12' lg='3' xl='3' >
                                <h5>{this.state.labels.urls}</h5>
                            </Col>
                            <Col xs='12' sm='12' lg='9' xl='9'>
                                <SearchControl label={this.state.labels.search} table={this.state.data.urls} 
                                loader={this.loader}/>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.urls}
                                    loader={this.loader}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    selectRow={(row)=>{
                                    this.selectUrl(this.state.data.subDomain.rows[row].row[0].value)
                                    }}
                                />
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
    assistant:PropTypes.oneOf(['dictionaries','data','workflow','activity', 'resource']).isRequired,    //for which assistant will be needed
    select:PropTypes.bool   //false construct the URL, true, select the existing
}