import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'

/**
 * Select object to amend
 * 
 */
class Amended extends Component{
    constructor(props){
        super(props)
        this.state={
            repaint:false,
            data: this.props.data,                            //AmendmentDTO
            identifier:Date.now().toString(),
            labels:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.selectRow=this.selectRow.bind(this)
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

    /**
     * seelct a row in the Amended table
     */
    selectRow(rowNo){
        let rows = this.state.data.table.rows
        rows.forEach((row,index) => {
            if(index==rowNo){
                row.selected=!row.selected
            }else{
                row.selected=false
            }
        })
        Navigator.message(this.state.identifier, this.props.recipient, "onSelectionChange", this.state.data)
        this.setState(this.state)
    }
 
    render(){
        if(this.state.data.table.rows.length==0){
            return <h6>{this.state.data.selectedName}</h6>
        }
        return(
            <Container fluid>
                <CollectorTable
                    tableData={this.state.data.table}
                    loader={()=>{}}
                    headBackground={Pharmadex.settings.tableHeaderBackground}
                    linkProcessor={(rowNo, cell)=>{
                        this.selectRow(rowNo)
                    }}
                    selectRow={(rowNo)=>{
                        this.selectRow(rowNo)
                    }}
                />
            </Container>
        )
    }


}
export default Amended
Amended.propTypes={
    data:PropTypes.object.isRequired,           //AmendmentDTO
    recipient:PropTypes.string.isRequired,      //The recipient of messages
    readOnly:PropTypes.bool
}