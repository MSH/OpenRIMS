import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import ViewEditDate from './form/ViewEditDate'
import ButtonUni from './form/ButtonUni'
import CollectorTable from './utils/CollectorTable'
import SearchControlNew from './utils/SearchControlNew'
import Pharmadex from './Pharmadex'

/**
 * Responsible for scheduler, i.e. allows assign processes to run in Host phase.
 * These process includ, but not limited Inspection, Renew, etc
 */
class Scheduler extends Component{
    constructor(props){
        super(props)
        this.state={
            form:true,
            identifier:Date.now().toString(),
            labels:{
                all:'',
                cancel:'',
                label_close:'',
            },
            data:this.props.data,        //SchedulerDTO
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.form=this.form.bind(this)
        this.table=this.table.bind(this)
        this.loader=this.loader.bind(this)
    }
    /**
     * Create a scheduler for Thing.js
     * @returns a scheduler ready to place to Thing.js
     */
    static place(res,index, readOnly,identifier,label){
        if(res!=undefined){
            return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{label}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Scheduler data={res}
                                            recipient={identifier}
                                            readOnly={readOnly || res.readOnly}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
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
        Locales.createLabels(this)
        Locales.resolveLabels(this)
    }

    componentDidUpdate(){
        if(this.props.data.schedule.justloaded){
            this.state.data=this.props.data
            delete this.props.data.schedule.justloaded
            this.setState(this.state)
        }else{
            this.props.data.schedule=this.state.data.schedule
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * 
     * @returns scheduler data form
     */
    form(){
        let edit=true
        if(this.props.readOnly){
            edit=false
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='12'xl='12'>
                        <ViewEditDate attribute='schedule' component={this} edit={edit}/>
                    </Col>
                </Row>

            </Container>
        )
    }
    /**
     * reload only table data. For search, filter, etc
     */
    loader(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/scheduler/table", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }
    table(){
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <SearchControlNew label={this.state.labels.search} table={this.state.data.table} loader={this.loader}/>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <ButtonUni
                             label={this.state.labels.label_close}
                             onClick={()=>{
                                this.state.form=false
                                this.setState(this.state)
                             }}
                             color='primary'
                        />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <CollectorTable
                              tableData={this.state.data.table}
                              loader={this.loader}
                              headBackground={Pharmadex.settings.tableHeaderBackground} 
                        />
                    </Col>
                </Row>
            </Container>
        )
    }
    render(){
       if(this.state.data.schedule == undefined || this.state.labels.locale==undefined){
           return []
       }
       if(this.state.form){
           return this.form()
       }else{
           return this.table()
       }
    }


}
export default Scheduler
Scheduler.propTypes={
    data:PropTypes.object.isRequired,       //SchedulerDTO
    recipient:PropTypes.string.isRequired,
    readOnly:PropTypes.bool
}