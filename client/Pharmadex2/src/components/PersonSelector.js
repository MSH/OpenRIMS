import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'

/**
 * Responsible to select all persons related to an application
 * @example
 * <PersonSelector data={res}
                    recipient={this.state.identifier}
 */
class PersonSelector extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            data:this.props.data,
            labels:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader
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
     * Load a list of persons
     */
    loader(){
        
    }
    render(){
        return(
            <Container fluid>
                <CollectorTable
                            tableData={this.state.data.table}
                            loader={this.loader}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            selectRow={(rowNo)=>{
                                let row = this.state.data.table.rows[rowNo]
                                this.state.data.table.rows.forEach(element => {
                                    if(element.dbID != row.dbID){
                                        element.selected=false
                                    }else{
                                        element.selected=!element.selected
                                    }
                                    this.setState(this.state)
                                });
                            }}
                />
            </Container>
        )
    }


}
export default PersonSelector
PersonSelector.propTypes={
    data:PropTypes.object.isRequired,       //PersonSelectorDTO,
    recipient:PropTypes.string.isRequired,  //parent thing for messaging
}