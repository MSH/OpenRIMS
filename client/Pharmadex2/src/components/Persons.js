import React , {Component} from 'react'
import {Container, Row, Col, Breadcrumb, BreadcrumbItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Pharmadex from './Pharmadex'
import CollectorTable from './utils/CollectorTable'
import ButtonUni from './form/ButtonUni'
import SearchControl from './utils/SearchControl'
import Thing from './Thing'

/**
 * List of persons that should be included to an application. Necessary for pharamcy site, etc
 */
class Persons extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),           //address for messages for this object
            data:this.props.data,                       //PersonDTO
            labels:{
                global_add:'',
                search:'',
                next:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
        this.paintPerson=this.paintPerson.bind(this)
        this.showList=this.showList.bind(this)
    }

    /**
     * Listen messages from other components
     * @param {Window Event} event 
     */
        eventProcessor(event){
            let data=event.data
            if(data.from==this.props.recipient){
                
            }
        }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * load/reload the table
     */
    loader(){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/person/table/load",this.state.data,(query,result)=>{
            this.state.data.table=result.table
            this.setState(this.state)
        })
    }
    /**
     * Button "Show list"
     * @param {react key property} key 
     * @returns 
     */
    showList(key){
        let thing=this.state.thing.path[0]
        return(
            <Breadcrumb key={key} className="p-2">
                 <BreadcrumbItem className="d-inline"  key={key+1}>
                    <h6 className="d-inline">{thing.title}</h6>
                </BreadcrumbItem>
                <BreadcrumbItem className="d-inline"  key={key+2}>
                    <div className="btn btn-link p-0 border-0"
                        onClick={()=>{
                            this.state.showLink=false
                            this.setState(this.state)
                        }}
                    >
                        <h6 className="d-inline">{this.state.labels.next}</h6>
                    </div>
                </BreadcrumbItem>
            </Breadcrumb>
        )
    }
    paintPerson(){
        let ret = []
            if(Fetchers.isGoodArray(this.state.thing.path)){
                ret.push(
                    this.showList("header")
                )
                this.state.thing.path.forEach((thing, index)=>{
                    thing.readOnly=true
                    ret.push(
                        <Thing key={index}
                        data={thing}
                        recipient={this.state.identifier}
                        readOnly={true}
                        narrow
                    />
                    )
                })
            }
            ret.push(
                this.showList("footer")
            )
        return ret
    }

    render(){
        if(this.state.showLink){
            return(
                <Container fluid className={Pharmadex.settings.activeBorder}>
                    {this.paintPerson()}
                </Container>
            )
        }else{
            return(
                <Container fluid>
                    <Row className="mb-1" hidden={this.state.data.readOnly || this.props.readOnly}>
                        <Col xs='12' sm='12' lg='8' xl='8' >
                            <div hidden={this.state.data.table.rows==0 || this.state.data.table.headers.pages<2}>
                                <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.loader}/>
                            </div>
                        </Col>
                        <Col xs='12' sm='12' lg='4' xl='4'>
                            <ButtonUni
                                onClick={()=>{
                                    this.state.data.nodeId=0
                                    Navigator.message(this.state.identifier, this.props.recipient, "auxPath", this.state.data)
                                }}
                                label={this.state.labels.global_add}
                                color="primary"
                            />
                        </Col>
                    </Row>
                    <Row>
                        <CollectorTable
                            tableData={this.state.data.table}
                            loader={this.loader}
                            linkProcessor={(rowNo, cell)=>{
                                let nodeId=this.state.data.table.rows[rowNo].dbID
                                this.state.data.nodeId=nodeId
                                if(this.state.data.readOnly || this.props.readOnly){
                                    let data={
                                        nodeId:nodeId,
                                        activityId:this.props.activityId,
                                        applDictNodeId:this.props.applDictNodeId,
                                        applicationUrl:this.props.applicationUrl,

                                    }
                                    Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/activity/path", data, (query,result)=>{
                                        this.state.thing=result
                                        this.state.showLink=true
                                        this.setState(this.state)
                                    })
                                }else{
                                    Navigator.message(this.state.identifier, this.props.recipient, "auxPath", this.state.data)
                                }
                            }}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                        />
                    </Row>
                </Container>
        )
        }
    }


}
export default Persons
Persons.propTypes={
    data:PropTypes.object.isRequired,               //PersonDTO
    applicationUrl:PropTypes.string.isRequired,     //application url for read only mode
    applDictNodeId:PropTypes.number.isRequired,     //application dict node for read only mode
    activityId:PropTypes.number.isRequired,         //activity id for read only mode
    recipient:PropTypes.string.isRequired,           //identifier of the recepient for messaging
    readOnly:PropTypes.bool
    
}