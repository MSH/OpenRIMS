import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import CollectorTable from '../utils/CollectorTable'
import Pharmadex from '../Pharmadex'
import SearchControl from '../utils/SearchControl'

/**
 * Display all user's ojects with the current registration
 * Allows to select one of them and, then, pass "onObjectSelected" event with ID of selected object. ID=0 means unselected
 * @example
 * <SelectObject recipient={this.state.identifier} />
 */
class SelectObject extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{
                appl:{
                    nodeId:this.props.nodeId
                }
            },                                
            identifier:Date.now().toString(),
            labels:{
                search:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.select=this.select.bind(this)
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
        this.loader()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * load all user's objects - sites and medicines
     */
    loader(){
        Fetchers.postJSON("/api/guest/objects/registered/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }
    /**
     * Select a row in the table
     * @param {*} rowNo 
     */
    select(rowNo){
        let rows = this.state.data.table.rows
        let selected= rows[rowNo].selected
        rows.forEach(row => {
            row.selected=false
        });
        rows[rowNo].selected=!selected
        if(rows[rowNo].selected){
            let id = rows[rowNo].dbID
            Navigator.message(this.state.identifier,this.props.recipient,"onObjectSelected",id)
        }else{
            Navigator.message(this.state.identifier,this.props.recipient,"onObjectSelected",0)
        }
        this.setState(this.state)
    }
    render(){
        if(this.state.data.table == undefined || this.state.labels.locale == undefined){
            return []
        }
        return(
            <Container fluid>
                <Row className="mb-1">
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.loader} />
                    </Col>
                    <Col xs='0' sm='0' lg='6' xl='6'>
                    </Col>
                </Row>
                <Row>
                    <Col>
                    <CollectorTable
                        tableData={this.state.data.table}
                        loader={this.loader}
                        headBackground={Pharmadex.settings.tableHeaderBackground}
                        selectRow={(rowNo)=>{
                            this.select(rowNo)
                        }}
                        linkProcessor={(rowNo, cell)=>{
                            this.select(rowNo)
                        }}
                    />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default SelectObject
SelectObject.propTypes={
    nodeId:PropTypes.number.isRequired,         //ID of the selected item or zero
    recipient:PropTypes.string.isRequired,      //Who is autorized to listen me
}