import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './Locales'
import Pharmadex from '../Pharmadex'
import CollectorTable from './CollectorTable'
import SearchControl from './SearchControl'

/**
 * The Search component along with the Table component. 
 * It is only to avoid redundancy
 * ```
 * tableData: PropTypes.object.isRequired, //complex table data model, see Java implementation
 * loader:PropTypes.func.isRequired,    // reload table data
 * label:PropTypes.string.isRequired    // label inside the search
 * ```
 * 
 */
class TableSearch extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            labels:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
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
                <Row className='mb-3'>
                    <Col>
                        <SearchControl  label={this.props.label} table={this.props.tableData} loader={this.props.loader}/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <CollectorTable
                            tableData={this.props.tableData}
                            loader={this.props.loader}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                        />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default TableSearch
TableSearch.propTypes={
    tableData: PropTypes.object.isRequired, //complex table data model, see Java implementation TableQtb.java
    loader:PropTypes.func.isRequired,    // reload table data
    label:PropTypes.string.isRequired,    // label inside the search
    title:PropTypes.string                  //the upper title (optional)      
}