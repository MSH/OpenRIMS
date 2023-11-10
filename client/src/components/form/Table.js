import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import Pharmadex from '../Pharmadex'
import CollectorTable from '../utils/CollectorTable'
import SearchControlNew from '../utils/SearchControlNew'

/**
 * General table with auto load, search and select (box or link)
 * loadAPI : PropTypes.string.isRequired,           // API to load/reload content
    onSelectSubject: PropTypes.string.isRequired,   // message name for onSelect event
    recipient:PropTypes.string.isRequired,          //recepient for messaging  
 */
class Table extends Component{
    constructor(props){
        super(props)
        this.state={
            loadAPI:'',
            data:{},                            //data.table property is required
            identifier:Date.now().toString(),
            labels:{
                search:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.load = this.load.bind(this)
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
        this.load()
    }

    componentDidUpdate(){
        if(this.state.loadAPI!=this.props.loadAPI){
            this.state.loadAPI=this.props.loadAPI
            if(this.state.data.table != undefined){
                this.state.data.table.headers.headers=[]
                Navigator.message(this.state.identifier,'*','runSearch',{})
            }
           // this.load()
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    load(){
        Fetchers.postJSON(this.props.loadAPI, this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    render(){
        if(this.state.data.table==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait()
        }
        // calculate narrow columns
        let narrow=[]
        if(Fetchers.isGoodArray(this.state.data.table.headers.headers)){
            this.state.data.table.headers.headers.forEach(header => {
                if(header.columnType==4){
                    narrow.push(header.key)
                }
            });
        }
        return(
            <Container fluid>
                <Row className="mb-3">
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <SearchControlNew label={this.state.labels.search} table={this.state.data.table} 
                                loader={this.load} recipient={this.state.identifier}/>
                        </Col>
                    <Col xs='12' sm='12' lg='8' xl='8'/>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable
                            tableData={this.state.data.table}
                            loader={this.load}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(narrow.includes(header)){
                                    return {width:'10%'}
                                }
                            }}
                            linkProcessor={(row,col)=>{
                                    let data={
                                        selectedID:this.state.data.table.rows[row].dbID,
                                    }
                                    Navigator.message(this.state.identifier, this.props.recipient, this.props.onSelectSubject,data)
                                }}   
                            />
                        </Col>
                    </Row>
            </Container>
        )
    }


}
export default Table
Table.propTypes={
    loadAPI : PropTypes.string.isRequired,           // API to load/reload content
    onSelectSubject: PropTypes.string.isRequired,   // message name for onSelect or onload event
    recipient:PropTypes.string.isRequired,          //recepient for messaging  
}