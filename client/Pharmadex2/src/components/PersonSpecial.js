import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'

/**
 * Determine person with the special function, like Pharmacist, Custom Brocker, etc
 * Allows selelect a person from the list and, then, create/read/update a thing related to this person 
 * 
 */
class PersonSpecial extends Component{
    constructor(props){
        super(props)
        this.state={
            repaint:false,
            data: this.props.data,                            //PersonSpecialDTO
            identifier:Date.now().toString(),
            labels:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader
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
     * Loads the defined thing for a selected person
     */
    loader(){
        Navigator.message(this.state.identifier, this.props.recipient, "onSelectionChange", this.state.data)
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/application/person/special/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.state.repaint=true
            Locales.createLabels(this)
            Locales.resolveLabels(this)
            Navigator.message(this.state.identifier, this.props.recipient, "changeNodeId", this.state.data.personDataId)
        })
    }
    /**
     * seelct a row in the Persons table
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
        this.loader()
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
export default PersonSpecial
PersonSpecial.propTypes={
    data:PropTypes.object.isRequired,           //PersonSpecialDTO
    recipient:PropTypes.string.isRequired,      //The recipient of messages
    readOnly:PropTypes.bool
}