import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Pharmadex from '../Pharmadex'
import CollectorTable from './CollectorTable'
import SearchControl from './SearchControl'
import Fetchers from './Fetchers'
import Navigator from './Navigator'

/**
 * The Search component along with the Table component. 
 * It is only to avoid redundancy
 * ```
 * identifier:PropTypes.string,          //address to receie and return address to send messages. Default is timestamp
    tableData: PropTypes.object.isRequired, //complex table data model, see Java implementation TableQtb.java
    loader:PropTypes.func.isRequired,    // reload table data
    label:PropTypes.string.isRequired,    // label inside the search
    title:PropTypes.string,                  //the upper title (optional)      
    styleCorrector:PropTypes.func,          // styleCorrector function
    linkProcessor:PropTypes.func,          // function on click firstColumn
    selectRow:PropTypes.func,               // function on checkBox click

    Can process message CleanUpSearch - clean up filters and search criteria
 * ```
 * 
 */
class TableSearch extends Component{
    constructor(props){
        super(props)
        this.state={
            labels:{},
        }
        if(this.props.identifier == undefined){
            this.state.identifier=Date.now().toString() //default
        }else{
            this.state.identifier=this.props.identifier //the callar will send messages
        }
        this.eventProcessor=this.eventProcessor.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            if(data.subject=='CleanUpSearch'){
                let headers=this.props.tableData.headers.headers;
                if(Fetchers.isGoodArray(headers)){
                    headers.forEach(header => {
                        header.generalCondition=''
                    });
                }
                this.setState(this.state)
                Navigator.message(this.state.identifier, this.state.identifier+'searchbox','CleanUpSearchBox',{})
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        return(
            <Container fluid>
                <Row hidden={!this.props.title}>
                    <Col>
                        <h5>{this.props.title}</h5>
                    </Col>
                </Row>
                <Row className='mb-3' hidden={this.props.tableData.rows.length==0}>
                    <Col>
                        <SearchControl identifier={this.state.identifier+'searchbox'}  label={this.props.label} table={this.props.tableData} loader={this.props.loader}/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <CollectorTable
                            tableData={this.props.tableData}
                            loader={this.props.loader}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={this.props.styleCorrector}
                            linkProcessor={()=>{
                                if(this.props.linkProcessor == null){
                                    return ;
                                }else{
                                    this.props.linkProcessor
                                }
                            }
                                }
                            selectRow={this.props.selectRow}
                        />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default TableSearch
TableSearch.propTypes={
    identifier:PropTypes.string,            //address to receie and return address to send messages. Default is timestamp
    tableData: PropTypes.object.isRequired, //complex table data model, see Java implementation TableQtb.java
    loader:PropTypes.func.isRequired,    // reload table data
    label:PropTypes.string.isRequired,    // label inside the search
    title:PropTypes.string,                  //the upper title (optional)      
    styleCorrector:PropTypes.func,          // styleCorrector function
    linkProcessor:PropTypes.func,          // function on click firstColumn
    selectRow:PropTypes.func,               // function on checkBox click
}