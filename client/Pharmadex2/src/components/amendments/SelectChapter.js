import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import CollectorTable from '../utils/CollectorTable'
import SearchControl from '../utils/SearchControl'
import Pharmadex from '../Pharmadex'

/**
 * Allows select a chapter inside a thing
 * pass onChapterSelected event to the recipient with {nodeId, varName} of the selected Thing-chapter.
 * In case of de-selection nodeId=0. In case of main chapter - varname is empty
 * 
 */
class SelectChapter extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{
                nodeId:this.props.nodeId,
                varNodeId:this.props.chapterId,

            },                            //DataConfigDTO.java
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

    componentDidUpdate(){
        if(this.props.nodeId != this.state.data.nodeId){
            this.state.data.nodeId = this.props.nodeId
            this.loader()
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    loader(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/thing/chapters/load", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        } )
    }
    select(rowNo){
        let rows=this.state.data.table.rows
        let selected= rows[rowNo].selected
        rows.forEach(row => {
            row.selected=false
        });
        rows[rowNo].selected=!selected
        if(rows[rowNo].selected){
            let data={
                id:rows[rowNo].dbID,
                title:rows[rowNo].row[0].value,
            }
           Navigator.message(this.state.identifier, this.props.recipient,"onChapterSelected", data)
        }else{
            Navigator.message(this.state.identifier, this.props.recipient,"onChapterSelected", 0)
        }
        this.setState(this.state)
    }
    render(){
        if(this.state.data.table == undefined || this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
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
export default SelectChapter
SelectChapter.propTypes={
    nodeId:PropTypes.number.isRequired, //ID of the thing
    chapterId:PropTypes.number.isRequired,  //ID of chapter
    recipient:PropTypes.string.isRequired,  //recipient for message
}