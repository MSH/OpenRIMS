import React , {Component} from 'react'
import {Container, Row, Col, Breadcrumb, BreadcrumbItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Thing from './Thing'

/**
 * Allows access data before modification inside a modification workflow activity
 */
class AmendmentActivity extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:this.props.data,
            path:{},
            index:0,
            labels:{
                checklist:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.items=this.items.bind(this)
        this.content=this.content.bind(this)
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
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Create breadcrumb items
     */
    items(){
        let ret=[]
            if(this.state.index>0){
            ret.push(
                <BreadcrumbItem className="d-inline"  key={0}>
                            <div className="btn btn-link p-0 border-0"
                        onClick={()=>{
                            this.state.index=0
                            this.state.path={}
                            this.setState(this.state)
                        }}
                        >
                            <h6 className="d-inline">{this.state.labels.checklist}</h6>
                        </div>
                </BreadcrumbItem>
                )
            }else{
                ret.push(
                    <BreadcrumbItem className="d-inline"  key={0}>
                            <h6 className="d-inline">{this.state.labels.checklist}</h6>
                    </BreadcrumbItem>
                )
            }
            if(Fetchers.isGoodArray(this.state.data.modiPath)){
                this.state.data.modiPath.forEach((dataUnit,index) => {
                    if(index==this.state.index-1){
                        ret.push(
                            <BreadcrumbItem className="d-inline"  key={index+100}>
                                <h6 className="d-inline">{dataUnit.label}</h6>
                            </BreadcrumbItem>
                        )
                    }else{
                        ret.push(
                            <BreadcrumbItem className="d-inline"  key={index+100}>
                                <div className="btn btn-link p-0 border-0"
                                    onClick={()=>{
                                        if(!Fetchers.isGoodArray(this.state.data.modiPath[index])){
                                            Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/activity/amendment/path", this.state.data.modiPath[index], (query,result)=>{
                                                this.state.data.modiPath[index]=result
                                                this.state.index=index+1
                                                this.setState(this.state)
                                            })
                                        }else{
                                            this.state.index=index+1
                                            this.setState(this.state)
                                        }
                                    }}
                                    >
                                        <h6 className="d-inline">{dataUnit.label}</h6>
                                </div>
                            </BreadcrumbItem>
                        )
                }
                    
                });
            }
        return ret
    }

    /**
     * display content depends on user's choice
     */
    content(){
        if(this.state.index==0){
            return[]
        }else{
            let ret = []
            let dataUnit=this.state.data.modiPath[this.state.index-1]
            if(dataUnit != undefined){
                let path=this.state.data.modiPath[this.state.index-1].path
                if(Fetchers.isGoodArray(path)){
                    path.forEach((thing, index)=>{
                        //thing.readOnly=true
                        ret.push(
                            <h4 key={index+1000}>
                                {thing.title}
                            </h4>
                        )
                    thing.repaint=true
                    ret.push(
                        <Thing key={index+1}
                        data={thing}
                        recipient={this.state.identifier}
                        readOnly={true}
                        narrow
                        repaint
                        />
                        )
                    })
                }
            }
        return ret
        }
        
    }

    render(){
        if(this.state.data.modiPath.length==0 || this.state.labels.locale==undefined){
            return []
        }else{
            return(
                <Container fluid>
                    <Row>
                        <Col>
                            <Breadcrumb>
                                {this.items()}
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


}
export default AmendmentActivity
AmendmentActivity.propTypes={
    data:PropTypes.object.isRequired,       //ActivityDTO.java
    recipient:PropTypes.string.isRequired,  //recipient for messaging
    
}