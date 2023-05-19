import React , {Component} from 'react'
import {Container, Row, Col, Label, Alert} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import ButtonUni from './form/ButtonUni'
import SearchControlNew from './utils/SearchControlNew'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'
import FieldInput from './form/FieldInput'
import Navigator from './utils/Navigator'
import FieldGuarded from './form/FieldGuarded'

/**
 * All Messages
 */
class Messages extends Component{
    constructor(props){
        super(props)
        this.state={
            showForm:false, 
            data:{},
            labels:{
                res_key:'',
                res_value_pt:'',
                res_value_en:'',
                search:'',
                messages:'',
                global_cancel:'',
                global_save:'',
                global_add:"",
                message:"",
                reload:"",
            }
        }
        this.load=this.load.bind(this)
        this.buildTable=this.buildTable.bind(this)
        this.buildForm=this.buildForm.bind(this)
        this.buildButtons=this.buildButtons.bind(this)
        this.createFields=this.createFields.bind(this)
    }
    componentDidMount(){
        this.load()
        Locales.resolveLabels(this)
    }

    load(){
        Fetchers.postJSONNoSpinner("/api/admin/messages/load", this.state.data, (query,responce)=>{
            this.state.data=responce
            this.setState(this.state.data)
            
        })
    }

    createFields(){
        let ret=[]
        let keys = Object.keys(this.state.data.values)
        if(Fetchers.isGoodArray(keys)){
            keys.forEach((key, index)=>{
                if(this.state.data.values[key].value != undefined){
                    ret.push(
                        <Row key={index}>
                            <Col>
                                <FieldInput mode='textarea' attribute={key} component={this} data={this.state.data.values} rows={"3"} showkey/>
                            </Col>
                        </Row>
                    )
                }else{
                    delete this.state.data.values[key]
                }
            }
        )
        }
        return ret
    }

    buildTable(){
        return(
            <Row>
                <Col>
                    <Row>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <SearchControlNew label={this.state.labels.search} table={this.state.data.table} loader={this.load}/>
                            </Col>
                            <Col xs='12' sm='12' lg='4' xl='4'>
                            </Col>
                            <Col xs='12' sm='12' lg='2' xl='2'>
                                <ButtonUni
                                label={this.state.labels.global_add}
                                onClick={()=>{
                                    this.state.showForm = true
                                    this.state.data.selected = 0;
                                    this.load()
                                }}
                                color="primary"
                                />
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                            <CollectorTable
                                tableData={this.state.data.table}
                                loader={this.load}
                                linkProcessor={(rowNo,cellNo)=>{
                                    this.state.showForm = true
                                    this.state.data.selected = this.state.data.table.rows[rowNo].dbID;
                                    this.load()
                                }}
                                selectRow={(rowNo)=>{}}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                                styleCorrector={(header)=>{
                                    if(header=='message_key'){
                                        return {width:'40%'}
                                    }
                                }}
                            />
                            </Col>
                        </Row>
                </Col>
            </Row>
        )
    }

    buildButtons(showtitle){
        let ret = []
        ret.push(
            <Row key={"btns"}>
                {showtitle?
                <Col xs='12' sm='12' lg='4' xl='4'>
                    <h6>{this.state.labels.message}</h6>
                </Col>
                :
                <Col xs='12' sm='12' lg='4' xl='4'>
                </Col>}
                <Col xs='12' sm='12' lg='4' xl='4'>
                    <ButtonUni
                        label={this.state.labels.global_save}
                        color='primary'
                        onClick={()=>{
                                Fetchers.postJSONNoSpinner("/api/admin/messages/save", this.state.data,(query,result)=>{
                                    this.state.data=result
                                    if(this.state.data.valid){
                                        this.state.showForm=false
                                        window.location.reload(false)
                                    }else{
                                        this.setState(this.state)
                                    }
                                })
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='4' xl='4'>
                    <ButtonUni
                            label={this.state.labels.global_cancel}
                            color='secondary'
                            onClick={()=>{
                                this.state.showForm=false
                                this.load()
                            }}
                    />
                </Col>
                </Row>
        )
        if(showtitle){
            ret.push(
                <Row key={"lrt"}>
                    <Col hidden={this.state.data.valid}>
                        <Alert color="danger" className="p-0 m-0">
                            <small>{this.state.data.identifier}</small>
                        </Alert>       
                    </Col>
                </Row>
            )
        }
        return ret
    }

    buildForm(){
        if(this.state.showForm){
            return (
                <Container fluid className={Pharmadex.settings.activeBorder}>
                    <Row style={{height:'10px'}}></Row>
                    {this.buildButtons(true)}
                    <Row>
                        <Col> 
                            <FieldGuarded mode="text" attribute="res_key" component={this} /> 
                        </Col>
                    </Row>
                    {this.createFields()}
                    {this.buildButtons(false)}
                    <Row style={{height:'20px'}}></Row>
                </Container>
            )
        }else
            return []
    }

    render(){
        if(this.state.data == undefined || this.state.labels.locale == undefined || 
            this.state.data.table == undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12'sm='12' lg='8' xl='8' className="d-flex justify-content-center">
                        <h6>{this.state.labels.messages}</h6>
                    </Col>
                    <Col xs='12'sm='12' lg='3' xl='3'>
                        <ButtonUni 
                            label={this.state.labels.reload}
                            onClick={()=>{
                                Fetchers.postJSONNoSpinner("/api/admin/reloadmessages", "",(query,result)=>{
                                    console.log(result);
                                    window.location.reload(false)
                                } )
                            }}
                            color="success"
                        />
                    </Col>
                    <Col xs='12'sm='12' lg='1' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_cancel}
                            onClick={()=>{
                                window.location="/"+Navigator.tabSetName()+"#"+Navigator.tabName()
                            }}
                            color="info"
                        />
                    </Col>
                </Row>
                <Row className="pb-1">
                    <Col xs={12} sm={7} lg={7} xl={7}>
                        {this.buildTable()}
                    </Col>
                    <Col xs={12} sm={5} lg={5} xl={5}>
                        <Label></Label>
                        {this.buildForm()}
                    </Col>
                </Row>
            </Container>
        )
    }
}
export default Messages
Messages.propTypes={
    
}