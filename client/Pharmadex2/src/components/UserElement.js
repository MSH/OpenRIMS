import React , {Component} from 'react'
import {Container,Row, Col, Button, Label,Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import ButtonUni from './form/ButtonUni'
import FieldOption from './form/FieldOption'
import FieldsComparator from './form/FieldsComparator'
import Navigator from './utils/Navigator'
import ViewEdit from './form/ViewEdit'
import ViewEditOption from './form/ViewEditOption'
import Dictionary from './Dictionary'
import FieldGuarded from './form/FieldGuarded'
import AlertFloat from './utils/AlertFloat'

/**
 * Edit/add user's data
 * @example
        <UserElement 
            conceptId={data.organization.concept.id}
            userId={data.user,id}                       //zero means add    
        />
 *  details is not mandatory
 */
class UserElement extends Component{
    constructor(props){
        super(props)
        this.identifier=Date.now().toString()
        this.state={
            data:{},            //UserElementDTO.java
            labels:{
                save:'',
                global_suspend:'',
                cancel:'',
                executor_roles:'',
                appl_responsibility:'',
                save_error:'',
                warningRemove:'',
                saved:'',
            }
        }
        this.loadData=this.loadData.bind(this)
        this.comparator = new FieldsComparator(this)
        this.processEvent=this.processEvent.bind(this)
        this.placeLiterals=this.placeLiterals.bind(this)
        this.literal=this.literal.bind(this)
        this.applDicts=this.applDicts.bind(this)
    }

        /**
         * Check component pooling completion
         * @returns true if all data has been collected from components
         */
        isCollected(){
            return(
               this.dicionaries.length==0
            )
        }

    processEvent(event){
        let data=event.data
        if(data.to==this.identifier){
            if(data.subject="onSelectionChange"){
                this.state.data[data.data.varName]=data.data
            }
        }
    }
    componentDidMount(){
        //init messages
       window.addEventListener("message",this.processEvent)
        //init data load
        this.loadData()
    }
    componentWillUnmount(){
        window.removeEventListener("message",this.processEvent)
    }

    loadData(){
        this.state.data.conceptId=this.props.conceptId
        this.state.data.id=this.props.userId
        Fetchers.postJSONNoSpinner("/api/admin/user/load", this.state.data, (query, result)=>{
            Fetchers.setJustLoaded(result,false)
            this.state.data=result
            this.comparator = new FieldsComparator(this)
            this.setState(this.state)
            Locales.createLabels(this)
            Locales.createLabelsRecursive(this.state.data.node, this.state.labels, "literals")
            Locales.resolveLabels(this)
            Navigator.message(this.identifier,"*","refreshData",{}) //refresh dictionaries
        })
    }

    componentDidUpdate(){
        const fld = this.comparator.checkChanges()
        if(fld.includes("addPerson")){
            this.loadData(); 
        }
    }
       /**
     * Place a literal in a row 
     * @param {name (label of a literal)} name 
     * @param {number to create a key} index 
     */
        literal(name, index){
            let mode="text"
            if(name=="description"){
                mode="textarea"
            }else{
                mode="text"
            }
            return(
                <Row key={index}>
                    <Col>
                        <ViewEdit edit={!this.state.data.readOnly} mode={mode} attribute={name}
                         component={this} 
                         data={this.state.data.node.literals}
                         rows="6"
                        />
                    </Col>
                </Row>
            )
        }
    /**
     * Place literals from node
     */
    placeLiterals(){
        let node = this.state.data.node
        let ret=[]
        let keys=Object.keys(node.literals)
        if(Fetchers.isGoodArray(keys)){
            keys.forEach((name, index)=>{
                ret.push(
                    this.literal(name,index)
                )
            })
        }
        return ret
    }
    /**
     * Applications dictionaries
     */
    applDicts(){
        let ret = []
        let dicts = this.state.data.applDicts
        if(dicts != undefined){
            let keys = Object.keys(dicts)
            if(Fetchers.isGoodArray(keys)){
                keys.forEach((key, index)=>{
                    let dict=dicts[key]
                    ret.push(
                        Dictionary.placeDictionary(dict.varName, dict, index,false,this.identifier,"")
                    )
                })
            }
        }
        return ret
    }
    /**
     * Place buttons in the uniform way
     */
    buttons(){
        return(
            <Row className="mb-2">
                  <Col xs='0' sm='0' lg='6' xl='6'/>
                  <Col xs='12' sm='12' lg='2' xl='2'>
                    <ButtonUni
                         label={this.state.labels.save}
                         onClick={()=>{
                            Fetchers.postJSONNoSpinner("/api/admin/user/save", this.state.data,(query,result)=>{
                                Fetchers.setJustLoaded(result,false)
                                if(result.valid){
                                    //return to the caller
                                    let caller=this.props.caller
                                    Navigator.navigate(caller.tab,caller.component,caller.parameter)
                                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.saved, color:'success'})
                                }else{
                                    //send to components validation results
                                    this.state.data=result
                                    this.setState(this.state)
                                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.save_error, color:'danger'})
                                }
                            })
                         }}
                         color="primary"
                    />
                  </Col>
                  <Col  xs='12' sm='12' lg='2' xl='2'>
                    <ButtonUni
                         label={this.state.labels.global_suspend}
                         onClick={()=>{
                            Fetchers.alerts(this.state.labels.warningRemove, ()=>{
                                Fetchers.postJSONNoSpinner("/api/admin/user/suspend", this.state.data, (query, result)=>{
                                    Fetchers.setJustLoaded(result,false)
                                    this.state.data=result
                                    this.setState(this.state)
                                    let caller=this.props.caller
                                    Navigator.navigate(caller.tab,caller.component,caller.parameter)
                                })
                            }, null)
                         }}
                         color="warning"
                    /> 
                  </Col>
                  <Col  xs='12' sm='12' lg='2' xl='2'>
                      <ButtonUni
                         label={this.state.labels.cancel}
                         onClick={ ()=>{
                            let caller=this.props.caller
                            Navigator.navigate(caller.tab,caller.component,caller.parameter)
                            }
                        }
                         color="secondary"
                      />
                  </Col>
              </Row>
        )
    }
    render(){
        if(this.state.data.id == undefined || this.state.labels.locale == undefined){
            return []
        }
        return(
          <Container fluid>
              {this.buttons()}
              <Row hidden={this.state.data.organization.length==0}>
                  <Col>
                    <h4>{this.state.data.organization}</h4>
                  </Col>
              </Row>
               <Row hidden={this.props.userId>0}>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <FieldOption attribute="addPerson" component={this} />
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>

                    </Col> 
              </Row>
              <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                   {this. placeLiterals()}
                   <Row>
                        <Col xs='12' sm='12' lg='10' xl='10'>
                            <FieldGuarded mode='text' attribute='user_email' component={this} edit={this.state.edit}/>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ViewEditOption edit={!this.state.data.readOnly} attribute='global_enable'
                            component={this} 
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {Dictionary.placeDictionary('roles',this.state.data.roles, 0, false, this.identifier, this.state.labels.executor_roles)}
                        </Col>
                    </Row>
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Row>
                        <Col>
                            <h6>{this.state.labels.appl_responsibility}</h6>
                        </Col>    
                    </Row>
                    {this.applDicts()}
                </Col>
              </Row>
              {this.buttons()}
          </Container>
        )
    }


}
export default UserElement
UserElement.propTypes={
    conceptId : PropTypes.number.isRequired,    //id of an organization selected
    userId: PropTypes.number.isRequired,    //id of user selected, 0 for add new
    caller:PropTypes.shape({
        tab:PropTypes.string.isRequired,
        component:PropTypes.string.isRequired,
        parameter:PropTypes.string
        }).isRequired                       //caller component navigation
}