import React , {Component} from 'react'
import {Container,Row, Col,Alert, Breadcrumb, BreadcrumbItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Literals from './Literals'
import Navigator from './utils/Navigator'
import ButtonUni from './form/ButtonUni'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Dictionary from './Dictionary'

/**
 * Container to Edit/add an authority
 * @example
        <Auhority url={data.url} parentId={data.parentId} nodeId={data.nodeId}/>
/>
 */
class Authority extends Component{
    constructor(props){
        super(props)
        this.identifier="Authority"
        this.state={
            keys:[],
            ready:false,
            data:{},
            labels:{
                add:'',
                save:'',
                cancel:'',
                global_details:'',
                global_suspend:'',
                arearesponsibility:''
            }
        }
        this.onGetData=this.onGetData.bind(this)
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
        this.cancel=this.cancel.bind(this)
    }

    createBreadCrumb(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.node.title)){
            this.state.data.node.title.forEach((title,index)=>{
                ret.push(
                    <BreadcrumbItem key={index}>
                        {title}
                    </BreadcrumbItem>
                )
            })
        }
        return ret
    }

    /**
     * Get data from a component
     * @param {name:'onGetData',sender:sender,data:this.state.data of the sender} data 
     */
    onGetData(data){
        let key=data.from
        this.state.keys.push(key)
        if(key.length>0){

            //place data
            if(this.state.data[key]==undefined){
                if(this.state.data.dictionaries[key]==undefined){

                }else{
                    this.state.data.dictionaries[key]=data.data
                }
            }else{
                this.state.data[key]=data.data
            }

            //check finalization
            if(this.isCollected()){
                Fetchers.postJSONNoSpinner("/api/admin/organization/save", this.state.data,(query,result)=>{
                    if(result.valid){
                        //return to the caller
                        let caller=this.props.caller
                        Navigator.navigate(caller.tab,caller.component,caller.parameter)
                    }else{
                        this.state.data=result
                        this.setState(this.state)
                    }
                })
            }
        }
    }
    /**
     * Check component pooling completion
     * @returns true if all data has been collected from components
     */
    isCollected(){
        return true
    }

    eventProcessor(event){
        let data=event.data
        if(data.from != this.identifier && (data.to=="*" || data.to==this.identifier)){
            if(data.subject=="onGetData"){
                this.onGetData(data)   //in answer to ask data
            }
        }
    }

    componentDidMount(){
        //create listener
        window.addEventListener("message",this.eventProcessor)
        //load necessary data
        this.state.data.node={}
        this.state.data.node.url=this.props.url
        this.state.data.node.parentId=this.props.parentId
        this.state.data.node.nodeId=this.props.nodeId
        Fetchers.postJSONNoSpinner("/api/admin/organization/load", this.state.data,(query,result)=>{
            this.state.data=result;
            Locales.createLabels(this)
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
        this.state.ready=true
        this.setState(this.state)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Place dictionaries
     */
    dictionaries(){
        let ret=[]
        let dictionaries= this.state.data.dictionaries
        let keys = Object.keys(dictionaries)
        if(Fetchers.isGoodArray(keys)){
            keys.forEach((key)=>{
                if(Fetchers.isTrueObject(dictionaries[key])){
                    ret.push(
                        <Row  key={key}>
                            <Col>                   
                                <Row>
                                    <Col>
                                        <Dictionary identifier= {key}
                                                    data={dictionaries[key]} 
                                                    recipient={this.identifier}/>
                                    </Col>
                                </Row>
                                <Row hidden={dictionaries[key].valid}>
                                    <Col>
                                        <Alert color="danger" className="p-0 m-0">
                                            <small>{dictionaries[key].identifier}</small>
                                        </Alert>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                    )
                }
            })
        }
        return ret
    }

    cancel(){
        let caller=this.props.caller
        Navigator.navigate(caller.tab,caller.component,caller.parameter)
    }

    render(){
        if(!this.state.ready || this.state.labels.locale==undefined){
            return []
        }
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
                  <Col xs='12' sm='12' lg='6' xl='6'>
                    <Literals identifier="node" url={this.props.url} parentId={this.props.parentId} nodeId={this.props.nodeId} />
                  </Col>
                  <Col  xs='12' sm='12' lg='6' xl='6'>
                   {this.dictionaries()}
                  </Col>
              </Row>
              <Row>
              <Col xs='6' sm='6' lg='3' xl='3'>
                    <ButtonUni
                            label={this.state.labels.save}
                            onClick={()=>{
                                this.state.keys=[]
                                Navigator.message(this.identifier,"*","askData",{}) //ask all components for data, real save after complete reply
                            }}
                            outline
                            color="primary"
                        />
                  </Col>
                  <Col  xs='6' sm='6' lg='3' xl='3'>
                    <ButtonUni
                            label={this.state.labels.global_suspend}
                            onClick={()=>{
                              Fetchers.postJSONNoSpinner("/api/admin/organization/suspend", this.state.data,(query,result)=>{
                                  if(this.state.data.valid){
                                    this.cancel()
                                  }else{
                                      this.state.data=result
                                      this.setState(this.state)
                                  }
                              })
                            }}
                            outline
                            color="warning"
                            disabled={!this.state.data.node.leaf}
                        />
                  </Col>
                  <Col  xs='6' sm='6' lg='3' xl='3'>
                      <ButtonUni
                         label={this.state.labels.cancel}
                         onClick={this.cancel}
                         outline
                         color="secondary"
                      />
                  </Col>
              </Row>
          </Container>
        )
    }


}
export default Authority
Authority.propTypes={
    url:PropTypes.string.isRequired,        //url to find the root if parent and id both are zero
    parentId:PropTypes.number.isRequired,   //id of a node of the parent organization. 0 means add to the root
    nodeId:PropTypes.number.isRequired,     //id of a node of this organization. 0 means add to the parent
    caller:PropTypes.shape({
        tab:PropTypes.string.isRequired,
        component:PropTypes.string.isRequired,
        parameter:PropTypes.string
        }).isRequired                       //caller component navigation
}