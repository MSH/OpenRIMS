import React , {Component} from 'react'
import {Container, Row, Col, Breadcrumb,BreadcrumbItem} from 'reactstrap'
import Locales from './utils/Locales'
import Navigator from './utils/Navigator'
import ButtonUni from './form/ButtonUni'
import ELAssistantBuildWF from './ELAssistantBuildWF'
import ELAssistantSelectWF from './ELAssistantSelectWF'
import Downloader from './utils/Downloader'

/**
 * Electronic form for EL assistance
 * 
 */
class ELAssistant extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:{
                workflowURL:'',
                breadcrumbIndex:0,
                workflowURL:'',
                el:'',
                elReady:false,
                thisPageURL:'',
            },
            
            labels:{
                elassistance :'',
                menu_references:'',
                global_cancel:'',
                copyel:'',
                Test:'',
                selectWF:'',
                buildWF:'',
                testWF:'',
                clipboard:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.header=this.header.bind(this)
        this.content=this.content.bind(this)
        this.breadcrumbItems=this.breadcrumbItems.bind(this)
        this.el=this.el.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject=='ELAssitant_Workflow'){
                    this.state.data.workflowURL=data.data
                    this.state.data.breadcrumbIndex=1
                    this.setState(this.state)
                }
                if(data.subject=='ELAssistant_EL'){
                    this.state.data.el=data.data.el
                    this.state.data.elReady=data.data.ready
                    this.state.data.thisPageURL=data.data.thisPageURL
                    this.setState(this.state)
                }
            }
           
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        Locales.createLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * 
     * @returns Form title, result and buttons
     */
    header(){
        return(
            <Row>
                <Col xs='12' sm='12' lg='3' xl='3'> {/* page tile*/}
                    <h4>{this.state.labels.elassistance}</h4>
                </Col>
                <Col xs='12' sm='12' lg='5' xl='5'> {/* EL result*/}
                    {this.el()}
                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'> {/* test button*/}
                    <ButtonUni
                        disabled={!this.state.data.elReady}
                        label={this.state.labels.Test}
                        color="warning"
                        onClick={()=>{
                            let dl = new Downloader()
                            dl.postDownload('/api/admin/el/assitant/test',{workflowURL:this.state.data.workflowURL,
                                                                            el:this.state.data.el, thisPageURL:this.state.data.thisPageURL}, 
                            "eltest.docx") 
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'> {/* copy button*/}
                    <ButtonUni
                        disabled={!this.state.data.elReady}
                        label={this.state.labels.copyel}
                        color="primary"
                        onClick={()=>{
                            let copyText = document.getElementById("ELAssist_EL").innerHTML;
                            navigator.clipboard.writeText(copyText);
                            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.clipboard + " " + copyText, color:'success'})
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'> {/* help button*/}
                    <ButtonUni
                        label={this.state.labels.menu_references}
                        onClick={()=>{
                            Fetchers.openWindowAssist('/api/admin/elreference')
                        }}
                        color="info"
                    />
                </Col>
                <Col xs='12' sm='12' lg='1' xl='1'> {/* cancel button*/}
                    <ButtonUni
                        label={this.state.labels.global_cancel}
                        outline
                        color="secondary"
                        onClick={()=>{
                            Navigator.navigate("administrate")
                        }}
                    />
                </Col>
            </Row>
        )
    }
    /**
     * Select workflow, build EL aor test EL
     */
    content(){
        switch(this.state.data.breadcrumbIndex){
            case 0:
                return <ELAssistantSelectWF workflowURL={this.state.data.workflowURL} recipient={this.state.identifier} />
            case 1:
                return <ELAssistantBuildWF workflowURL={this.state.data.workflowURL} recipient={this.state.identifier} />
        }
    }
    /**
     * @returns Content navigation
     */
    breadcrumbItems(){
        let items=[]
        //-- select a workflow
        if(this.state.data.breadcrumbIndex>0){
            items.push(
                <BreadcrumbItem className="d-inline"  key={'breadcrumb_0'}>
                                <div className="btn btn-link p-0 border-0"
                            onClick={()=>{
                                this.state.data.breadcrumbIndex=0
                                this.setState(this.state)
                            }}
                            >
                                <h6 className="d-inline">{this.state.data.workflowURL.length>0?this.state.data.workflowURL:this.state.labels.selectWF}</h6>
                            </div>
                            </BreadcrumbItem>
            )
        }else{
            items.push(
                <BreadcrumbItem className="d-inline"  key={'breadcrumb_0'}>
                            <h6 className="d-inline">{this.state.data.workflowURL.length>0?this.state.data.workflowURL:this.state.labels.selectWF}</h6>
                </BreadcrumbItem>
            )
        }
        //-- build an EL
        if(this.state.data.breadcrumbIndex!=1 && this.state.data.workflowURL.length>0){
            items.push(
                <BreadcrumbItem className="d-inline"  key={'breadcrumb_1'}>
                                <div className="btn btn-link p-0 border-0"
                            onClick={()=>{
                                this.state.data.breadcrumbIndex=1
                                this.setState(this.state)
                            }}
                            >
                                <h6 className="d-inline">{this.state.labels.buildWF}</h6>
                            </div>
                            </BreadcrumbItem>
            )
        }else{
            items.push(
                <BreadcrumbItem className="d-inline"  key={'breadcrumb_1'}>
                            <h6 className="d-inline">{this.state.labels.buildWF}</h6>
                </BreadcrumbItem>
            )
        }
       
        return items
    }
    /**
     * @returns EL built 
     */
    el(){
        return(
            <Row>
                <Col className="d-flex justify-content-center">
                    <h4 id='ELAssist_EL'>{this.state.data.el}</h4>
                </Col>
            </Row>
        )
    }

    render(){
        return(
            <Container fluid>
                {this.header()}
                <Breadcrumb>
                    {this.breadcrumbItems()}
                </Breadcrumb>
                {this.content()}
                {this.header()}
            </Container>
        )
    }


}
export default ELAssistant
ELAssistant.propTypes={
    
}