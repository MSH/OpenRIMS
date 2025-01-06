import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import SearchControlNew from './utils/SearchControlNew'
import ButtonUni from './form/ButtonUni'
import Pharmadex from './Pharmadex'
import CollectorTable from './utils/CollectorTable'
import ViewEdit from './form/ViewEdit'
import ResourceFilling from './ResourceFilling'
//import Dictionary from './Dictionary'
import FieldDisplay from './form/FieldDisplay'

/**
 * Administrator of resources
 */
class Resources extends Component{
    constructor(props){
        super(props)
        this.state={
            form:false,
            thing:false,
            //dict:false,
            identifier:Date.now().toString(),
            data:{},                                //ResourceDTO
           // thingData:{},                           //ThingDTO
           // dictData:{},                            //DictionaryDTO
            labels:{
                search:'',
                global_add:'',
                global_cancel:'',
                save:'',
                global_suspend:'',
                resources:'',
                url:'',
                configUrl:'',
                dictUrl:'',
                description:'', 
                global_elreference:'',
                warningRemove:'',
                global_help:'',
               // elassistance:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.left=this.left.bind(this)
        this.prepareThing=this.prepareThing.bind(this)
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
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * catalogue table loader
     */
    loader(){
        Fetchers.postJSONNoSpinner("/api/admin/resources/load", this.state.data, (query, result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }
    /**
     * Prepare a thing before loading and ask to load it by Thing.js
     */
    prepareThing(){
        if(this.state.thing){
          return(  <ResourceFilling
            nodeId={this.state.data.nodeId}
            recipient={this.state.identifier}
            />)
        }else{
            return []
        }
    }
    /**
     * Control buttons
     * @returns 
     */
     buttons(){
        return(
            <Row>
                <Col xs='12' sm='12' lg='4' xl='4'>
                    <ButtonUni
                        label={this.state.labels.save}
                        color='primary'
                        onClick={()=>{
                            Fetchers.postJSONNoSpinner("/api/admin/resource/definition/save", this.state.data, (query, result)=>{
                                this.state.data=result
                                if(this.state.data.valid){
                                    this.state.form=false
                                    this.loader()
                                }else{
                                    this.setState(this.state)
                                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                                }
                            })
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='4' xl='4'>
                    <ButtonUni
                        disabled={this.state.data.nodeId==0}
                        label={this.state.labels.global_suspend}
                        color="warning"
                        onClick={()=>{
                            Fetchers.alerts(this.state.labels.warningRemove, ()=>{
                                Fetchers.postJSONNoSpinner("/api/admin/resource/definition/suspend", this.state.data, (query, result)=>{
                                    this.state.data=result
                                    if(this.state.data.valid){
                                        this.state.form=false
                                        this.loader()
                                    }else{
                                        this.setState(this.state)
                                    }
                                })
                            }, null)
                        }}
                    />
                </Col>
                <Col xs='12' sm='12' lg='4' xl='4'>
                    <ButtonUni
                            label={this.state.labels.global_cancel}
                            color='info'
                            onClick={()=>{
                                this.state.form=false
                                this.setState(this.state)
                            }}
                        />
                </Col>
            </Row>
        )
    }

    //form or table?
    left(){
        if(this.state.form && !this.state.thing){
            return(
            <Container fluid className={Pharmadex.settings.activeBorder}>
                {this.buttons()}
                    <FieldDisplay mode="text" attribute="url" component={this} />
                 <Row>
                    <Col hidden={this.state.data.url.assistant=='URL_RESOURCE_NEW' && this.state.data.dictUrl.value==''}>
                        <FieldDisplay mode='text' component={this} attribute='dictUrl' />
                    </Col>
                </Row> 
                <Row>
                    <Col hidden={this.state.data.url.assistant=='URL_RESOURCE_NEW' && this.state.data.configUrl.value==''}>
                        <FieldDisplay mode='text' component={this} attribute='configUrl' />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <ViewEdit mode='textarea' component={this} attribute='description' edit />
                    </Col>
                </Row>
            </Container>
            )
        }else{
            return(
                <Row>   
                    <Col>
                        <Row>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <SearchControlNew label={this.state.labels.search} table={this.state.data.table} loader={this.loader}/>
                            </Col>
                            <Col xs='12' sm='12' lg='4' xl='4'>
                            </Col>
                            <Col xs='12' sm='12' lg='2' xl='2'>
                                <ButtonUni
                                label={this.state.labels.global_add}
                                onClick={()=>{
                                    this.state.data.nodeId=0
                                    this.state.form=true
                                    this.state.thing=false
                                    this.loader()
                                }}
                                color="primary"
                                />
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <CollectorTable
                                    tableData={this.state.data.table}
                                    loader={this.loader}
                                    headBackground={Pharmadex.settings.tableHeaderBackground}
                                    styleCorrector={(header)=>{
                                        if(header=='url'){
                                            return {width:'30%'}
                                        }
                                    }}
                                    linkProcessor={(rowNo, col)=>{
                                        this.state.data.nodeId=this.state.data.table.rows[rowNo].dbID
                                        this.state.form=true
                                        this.state.thing=false
                                        //this.state.dict=false
                                        this.loader()
                                    }}
                                    selectRow={(rowNo)=>{
                                        let row = this.state.data.table.rows[rowNo]
                                        this.state.data.table.rows.forEach(element => {
                                            if(element.dbID != row.dbID){
                                                element.selected=false
                                            }else{
                                                element.selected=!element.selected
                                            }
                                        });
                                        if(row.selected){
                                            this.state.data.nodeId=row.dbID
                                            this.state.thing=true
                                            this.state.form=false
                                           this.setState(this.state)
                                        }else{
                                            this.state.vars=false
                                            this.state.thing=false
                                            //this.state.dict=false
                                            this.state.data.nodeId=0
                                            this.setState(this.state)
                                        }
                                    }}
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )
        }
    }
    /* //thing or nothing
    right(){
        if(this.state.thing){
            let data=this.state.thingData
            data.repaint=true
            return(
                <Row>
                    <Col>
                            <Thing
                                data={data} 
                                recipient={this.state.identifier}
                                narrow
                            />
                    </Col>
                    </Row>
            )
        }else{
            return []
        }
    }
    dict(){
        if(this.state.dict){
            let data=this.state.dictData
            return(
                <Row>
                    <Col>
                        <Row>
                            <Col>
                                <h5>{this.state.labels.items}</h5>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <Dictionary identifier={data.url} data={data} />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )
        }
    } */
    render(){
        if(this.state.data.table==undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row className='mb-5'>
                    <Col xs='12' sm='12' lg='6' xl='6'/>
                    <Col xs='12' sm='12' lg='3' xl='2'>
                        <ButtonUni
                            label={this.state.labels.global_help}
                            onClick={()=>{
                                Fetchers.openWindowHelp('/api/admin/resource/help','_blank').focus()
                            }}
                            color="info"
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='3'>
                        <ButtonUni
                            label={this.state.labels.global_elreference}
                            outline
                            onClick={()=>{
                                Fetchers.openWindowHelp('/api/admin/elreference')
                            }}
                            color="info"
                        />
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Row>
                            <Col>
                                <h6>{this.state.labels.resources}</h6>
                            </Col>
                        </Row>
                        {this.left()}
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.prepareThing()}
                    </Col>
                </Row>

            </Container>
        )
    }


}
export default Resources
Resources.propTypes={
    
}