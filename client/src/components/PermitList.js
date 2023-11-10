import React , {Component} from 'react'
import {Container, Row, Col, FormText} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import CollectorTable from './utils/CollectorTable'
import SearchControlNew from './utils/SearchControlNew'
import Pharmadex from './Pharmadex'

/**
 * List of all actual permits or going to be permitted for the current type of the permit/user
 * Issues onPermitSelected with permit data ID 
 */
class PermitList extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data :{
                dictItemId:this.props.dictItemId,
                permitType:'************'
            },           //PermitListDTO
            labels:{
                search:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load=this.load.bind(this)
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
        this.load()
    }
    /**
     * Refresh it if it will be needed
     */
    componentDidUpdate(){
        if(this.state.data.dictItemId!=this.props.dictItemId){
            this.state.data.dictItemId=this.props.dictItemId
            this.state.data.permitType=''
            this.load()
        }
    }
    /**
     * Load a permit's
     */
    load(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/permits",this.state.data,(query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        if(this.state.data.table==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <h5>{this.state.data.permitType}</h5>
                    </Col>
                    
                </Row>
                <Row>
                    <Col  xs='12' sm='12' lg='10' xl='10'>
                        <FormText color="muted">{this.props.hint}</FormText>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <SearchControlNew label={this.state.labels.search} table={this.state.data.table} loader={this.load} />
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <CollectorTable
                             tableData={this.state.data.table}
                             loader={this.load}
                             headBackground={Pharmadex.settings.tableHeaderBackground}
                             linkProcessor={(row,col)=>{
                                Navigator.message(this.state.identifier, this.props.recipient, "onPermitSelected",
                                        this.state.data.table.rows[row].dbID)
                             }}
                        />
                    </Col>
                </Row>

            </Container>
        )
    }


}
export default PermitList
PermitList.propTypes={
    dictItemId:PropTypes.number.isRequired,              // item in any "guest" dictionary
    hint :PropTypes.string.isRequired,               // hint to a user, typically what will be when selection  will occur
    recipient:PropTypes.string.isRequired,           //identifier of the recepient for messaging
}