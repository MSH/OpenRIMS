import React , {Component} from 'react'
import {Container, Row, Col, Navbar, NavbarBrand, Collapse, NavbarToggler,Nav, NavItem, NavLink, Button, Breadcrumb, BreadcrumbItem} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import UserReports from './reports/UserReports'
import TableReport from './reports/TableReport'
import PrintPreviewReport from './reports/PrintPreviewReport'

/**
 * Responsible for reports
 */
class ReportsOld extends Component{
    constructor(props){
        super(props)
        this.state={
            title:'',
            identifier:Date.now().toString(),
            labels:{
                reports:'',
                reportOld:'',
            },
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.content=this.content.bind(this)
        this.reportReady=this.reportReady.bind(this)
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.to==this.state.identifier){
                if(data.subject == 'reportLoaded'){
                    this.state.data=data.data
                    this.setState(this.state);
                }
                if(data.subject == 'onDrillDown'){
                    this.state.title=data.data
                    this.setState(this.state);
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

    /**
     * Report is ready
     */
    reportReady(){
        if(this.state.data!= undefined){
            if(this.state.data.table != undefined){
                if(this.state.data.table.headers.headers.length>0){
                    return true
                }else{
                    return false
                }
            }else{
                return false
            }
        }else{
            return false
        }
    }

    /**
     * Depend on data state, load a coponent
     */
    content(){
        let component=Navigator.componentName().toLowerCase()
        if(component=='printpreview'){
            let parStr = Navigator.parameterValue()
            let params=JSON.parse(parStr)
            return <PrintPreviewReport data={params} />
        }
        if(this.reportReady()){
            return <TableReport recipient={this.state.identifier} data={this.state.data}/>
        }
        return <UserReports recipient={this.state.identifier} />
    }

    createBreadCrumb(){
        let ret=[]
        let component=Navigator.componentName().toLowerCase()
        if(component!='printpreview'){
            let home=[]
            if(!this.reportReady()){
                home=<BreadcrumbItem className="d-inline"  key='1'>
                        <h6 className="d-inline">{this.state.labels.reportOld}</h6>
                    </BreadcrumbItem>
            }else{
                home=<BreadcrumbItem className="d-inline"  key='1'>
                    <div className="btn btn-link p-0 border-0"
                        onClick={()=>{
                            if(this.state.data!=undefined){
                                Fetchers.postJSON("/api/"+Navigator.tabSetName()+'/report/reset/root/', this.state.data, (query,result)=>{
                                    this.state.data=result
                                    this.setState(this.state)
                                })
                            }
                        }}
                    >
                        <h6 className="d-inline">{this.state.labels.reportOld}</h6>
                    </div>
                </BreadcrumbItem>
            }
            ret.push(home)
            if(this.reportReady()){
                if(this.state.title.length==0){
                    ret.push(
                        <BreadcrumbItem className="d-inline"  key='2.1'>
                            <h6 className="d-inline">{this.state.data.config.title}</h6>
                        </BreadcrumbItem>
                    )
                }else{
                    ret.push(
                        <BreadcrumbItem className="d-inline"  key='2.2'>
                            <div className="btn btn-link p-0 border-0"
                                onClick={()=>{
                                    this.state.title=''
                                    this.setState(this.state)
                                    Navigator.message(this.state.identifier,'*', 'refreshReportTable',{})
                                }}
                                >
                                <h6 className="d-inline">{this.state.data.config.title}</h6>
                            </div>
                        </BreadcrumbItem>
                    )
                    ret.push(
                        <BreadcrumbItem className="d-inline"  key='2.3'>
                            <h6 className="d-inline">{this.state.title}</h6>
                        </BreadcrumbItem>
                    )
                }
            }
        }
        return ret
    }
    render(){
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <Breadcrumb>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.content()}
                    </Col>
                </Row>
               
            </Container>
        )
    }


}
export default ReportsOld
ReportsOld.propTypes={
    
}