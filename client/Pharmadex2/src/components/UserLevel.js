import React , {Component} from 'react'
import {Row, Col, Container,Breadcrumb, BreadcrumbItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import CollectorTable from './utils/CollectorTable'
import ButtonUni from './form/ButtonUni'
import SearchControl from './utils/SearchControl'
import Pharmadex from './Pharmadex'
import Navigator from './utils/Navigator'

/** 
* This class represents a list of users for a selected authority or applicant
*
* @example
*    <UserLevel identifier={this.props.identifier+".user"} conceptID={this.props.conceptID}/>
    />

 */
class UserLevel extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},                //UserLevelDTO.java
            labels:{
                global_add:'',
                search:''
            }
        }
        this.load=this.load.bind(this)
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
    }
    componentDidMount(){
        Locales.resolveLabels(this)
        this.load()
    }

    componentDidUpdate(){
        if(this.props.conceptId != undefined){
            if(this.state.data.conceptId != this.props.conceptId){
                this.load()
            }
        }
    }

    /**
     * Load the list of users
     */
     load(){
        this.state.data.conceptId=this.props.conceptId
        this.state.conceptId=this.props.conceptId
        Fetchers.postJSONNoSpinner("/api/admin/list/users", this.state.data, (query, result)=>{
            this.state.data=result
            this.setState(this.state.data)
        })
    }
    createBreadCrumb(){
        let ret=[]
        if(Fetchers.isGoodArray(this.state.data.node.title)){
            this.state.data.node.title.forEach((title,index)=>{
                ret.push(
                    <BreadcrumbItem key={index}>
                        {title}
                    </BreadcrumbItem>
                )
            })
        }
        return ret
    }
    render(){
        if(this.props.conceptId == undefined){
            return []
        }
        if(this.state.labels.locale == undefined || this.state.data.node == undefined){
            return Pharmadex.wait()
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <Breadcrumb>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                </Row>
            <Row>
                <Col xs='6' sm='6' lg='8' xl='8'>
                    <SearchControl label={this.state.labels.search} table={this.state.data.node.table} loader={this.load} />
                </Col>
                <Col xs='6' sm='6' lg='4' xl='4' className="d-flex justify-content-end">
                    <ButtonUni 
                        outline
                        label={this.state.labels.global_add}
                        onClick={()=>{
                            Navigator.message(this.props.identifier, "*", "onUserEdit",{conceptId:this.props.conceptId,userId:0})
                        }}
                    />
                </Col>
            </Row>
            <Row>
                <Col>
                    <CollectorTable
                        tableData={this.state.data.node.table}
                        loader={this.load}
                        linkProcessor={(rowNo,cell)=>{
                            let rows=this.state.data.node.table.rows
                            Navigator.message(this.props.identifier, "*", "onUserEdit",{conceptId:this.props.conceptId,userId:rows[rowNo].dbID})
                        }}
                        headBackground={Pharmadex.settings.tableHeaderBackground}
                        styleCorrector={(header)=>{
                            if(header=='pref'){
                                return {width:'30%'}
                            }
                        }}
                    />
                </Col>
            </Row>
        </Container>
        )
    }


}
export default UserLevel
UserLevel.propTypes={
    identifier: PropTypes.string.isRequired,        //unique name of the instance of component for messages.
    conceptId: PropTypes.number,    //ID of concept (auxData) for an Authority or an Applicant 
}