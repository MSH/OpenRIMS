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
                area_responsibility:'',
                save_error:''
            }
        }
        this.loadData=this.loadData.bind(this)
        this.comparator = new FieldsComparator(this)
        this.processEvent=this.processEvent.bind(this)
        this.placeLiterals=this.placeLiterals.bind(this)
        this.literal=this.literal.bind(this)
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
                this.state.data[data.from]=data.data
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

    render(){
        if(this.state.data.id == undefined || this.state.labels.locale == undefined){
            return []
        }
        return(
          <Container fluid>
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
                                <ViewEdit mode='text' attribute='user_email' component={this} edit={this.state.edit}/>
                            </Col>
                            <Col xs='12' sm='12' lg='2' xl='2' className="align-self-center">
                            <Button
                                style={{color:'green'}}
                                hidden={this.state.edit}
                                onClick={()=>{
                                    this.state.edit=true
                                    this.setState(this.state)
                                }}
                                outline
                            >
                                <i className="fas fa-lock"></i>
                            </Button>
                            <Button
                                style={{color:'red'}}
                                hidden={!this.state.edit}
                                onClick={()=>{
                                    this.state.edit=false
                                    this.setState(this.state)
                                }}
                                outline
                            >
                                <i className="fas fa-lock-open"></i>
                            </Button>
                            </Col>
                        </Row>
                    <Row>
                        <Col>
                            <ViewEditOption edit={!this.state.data.readOnly} attribute='global_enable'
                            component={this} 
                            />
                        </Col>
                    </Row>
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Row>
                        <Col>
                            <Label>{this.state.labels.executor_roles}</Label>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                        <Dictionary identifier="roles" recipient={this.identifier} data={this.state.data.roles} recipient={this.identifier} display />
                        </Col>
                    </Row>
                </Col>
              </Row>
             
              <Row>
              <Col xs='12' sm='12' lg='6' xl='6'>
                <Row>
                    <Col>
                        <Label>{this.state.labels.area_responsibility}</Label>
                    </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Dictionary identifier="areas" recipient={this.identifier} data={this.state.data.areas} recipient={this.identifier} display />
                        </Col>
                    </Row>
                    
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Row>
                        <Col>
                            <Label>{this.state.labels.appl_responsibility}</Label>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Dictionary identifier="applications" recipient={this.identifier} data={this.state.data.applications} recipient={this.identifier} display />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Dictionary identifier="amendments" recipient={this.identifier} data={this.state.data.amendments} recipient={this.identifier} display />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Dictionary identifier="renewal" recipient={this.identifier} data={this.state.data.renewal} recipient={this.identifier} display />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Dictionary identifier="deregistration" recipient={this.identifier} data={this.state.data.deregistration} recipient={this.identifier} display />
                        </Col>
                    </Row>

                </Col>
               
              </Row>

              <Row className="mb-2">
                  <Col xs='6' sm='6' lg='3' xl='3'>
                    <ButtonUni
                         label={this.state.labels.save}
                         onClick={()=>{
                            Fetchers.postJSONNoSpinner("/api/admin/user/save", this.state.data,(query,result)=>{
                                Fetchers.setJustLoaded(result,false)
                                if(result.valid){
                                    //return to the caller
                                    let caller=this.props.caller
                                    Navigator.navigate(caller.tab,caller.component,caller.parameter)
                                }else{
                                    //send to components validation results
                                    this.state.data=result
                                    this.setState(this.state)
                                }
                            })
                         }}
                         color="primary"
                    />
                  </Col>
                  <Col  xs='6' sm='6' lg='3' xl='3'>
                    <ButtonUni
                         label={this.state.labels.global_suspend}
                         onClick={()=>{
                            Fetchers.postJSONNoSpinner("/api/admin/user/suspend", this.state.data, (query, result)=>{
                                Fetchers.setJustLoaded(result,false)
                                this.state.data=result
                                this.setState(this.state)
                                let caller=this.props.caller
                                Navigator.navigate(caller.tab,caller.component,caller.parameter)
                            })
                         }}
                         color="warning"
                    /> 
                  </Col>
                  <Col  xs='6' sm='6' lg='3' xl='3'>
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
              <Row>
                  <Col>
                        <Alert className="p-0 m-0" color='danger' hidden={this.state.data.valid}>
                            <small>{this.state.labels.save_error}</small>
                        </Alert>
                  </Col>
              </Row>
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