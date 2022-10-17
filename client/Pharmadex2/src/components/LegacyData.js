import React , {Component} from 'react'
import {Container, Row, Col,Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import SearchControl from './utils/SearchControl'
import CollectorTable from './utils/CollectorTable'


/**
 * Allows find and select legacy data.
 * issues onSelectionChange
 */
class LegacyData extends Component{
    constructor(props){
        super(props)
        this.state={
            data:this.props.data,
            identifier:Date.now().toString(),
            labels:{
                search:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadTable=this.loadTable.bind(this)
    }
    /**
     * 
     * @param {LegacyDataDTO} data 
     * @param {number} index - for the key property 
     * @param {boolean} readOnly 
     * @param {string} recipient - address of teh parent Thing for messages - onSelectionChange event 
     * @param {*} label 
     */
    static place(data, index, readOnly, recipient, label){
        if(data==undefined){
            return []
        }
        //help or error message
        let color="info"
        if(data.strict){
            color="danger"
        }
        return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{label}</h6>
                        </Col>
                    </Row>
                    <Row hidden={data.valid}>
                        <Col>
                            <Alert color={color} className="p-0 m-0">
                                <small>{data.identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <LegacyData key={data.varName+index} recipient={recipient} data={data} readOnly={readOnly}/>
                        </Col>
                    </Row>
                    
                </Col>
            </Row>
        )
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
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Reload a table
     */
    loadTable(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/legacy/data", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
            Navigator.message(this.state.identifier, this.props.recipient, "onSelectionChange", this.state.data)
        })
    }
    render(){
        if(this.props.readOnly){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.loadTable} />
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>

                    </Col>
                </Row>
                <Row>
                    <Col>
                        <CollectorTable
                            tableData={this.state.data.table}
                            loader={this.loadTable}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            selectRow={(rowNo)=>{
                                if(this.props.readOnly){
                                    return
                                }
                                let id =this.state.data.table.rows[rowNo].dbID
                                if(this.state.data.selectedNode==id){
                                    this.state.data.selectedNode=0
                                }else{
                                    this.state.data.selectedNode=id 
                                }
                                this.loadTable()
                            }}
                            
                        />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default LegacyData
LegacyData.propTypes={
    data:PropTypes.object.isRequired,    //LegacyDataDTO
    recipient:PropTypes.string.isRequired,   //recipient for messaging
    readOnly:PropTypes.bool
}