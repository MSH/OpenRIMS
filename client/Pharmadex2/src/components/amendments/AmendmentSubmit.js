import React , {Component} from 'react'
import {Container, Row, Col, Card, CardHeader, CardBody, CardFooter} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import Thing from '../Thing'
import CollectorTable from '../utils/CollectorTable'
import Pharmadex from '../Pharmadex'
import ViewEdit from '../form/ViewEdit'

/**
 * Submit an amendment to NMRA
 */
class AmendmentSubmit extends Component{
    constructor(props){
        super(props)
        this.state={
            data:this.props.data,                            //AmendmentDTO
            identifier:Date.now().toString(),
            labels:{
                global_curr_status:'',
                amendment:'',
                amendment_details:'',
                amended:'',
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.chapter=this.chapter.bind(this)
        this.amendment=this.amendment.bind(this)
        this.dataForm=this.dataForm.bind(this)
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
        this.loader()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    loader(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/amendment/chapter/variables", this.state.data, (query,result)=>{
            this.state.data=result
            Locales.createLabels(this)
            Locales.resolveLabels(this)
            this.setState(this.state)
        })
    }

       /**
     * Chapter for editand/or display
     */
    chapter(readOnly){
        let data={
            nodeId:this.state.data.chapter.nodeId,
            parentId:this.state.data.appl.nodeId,
        }
        return(
            <Card>
                <CardHeader>
                    {this.state.labels.global_curr_status}
                </CardHeader>
                <CardBody>
                    <Thing key="curr_status"
                    data={data}
                    recipient={this.state.identifier}
                    readOnly={true}
                    narrow
                    />
                </CardBody>
                <CardFooter>
                    {this.state.labels.global_curr_status}
                </CardFooter>
            </Card>
        )
    
    }
    /**
     * Print amended data
     */
    amendment(){
        let data = this.state.data
        data.chapter.readOnly=true
        if(Fetchers.isGoodArray(data.path)){
            let ret=[]
            data.path.forEach((element,index) => {
                ret.push(
                    <Thing
                    key={index}
                    data={element}
                    recipient={this.state.identifier}
                    readOnly
                    narrow
                    noload
                />
                )
            });
            return ret
        }else{
            return(
                <Thing
                    data={data.chapter}
                    recipient={this.state.identifier}
                    readOnly
                    narrow
                    noload
                />
                )
        
        }
    }
    /**
     * Form on the top of the screen
     * @returns 
     */
    dataForm(){
        return(
            <Row>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <Row>
                        <Col>
                            <h6>{this.state.labels.amended}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <CollectorTable
                                tableData={this.state.data.variables}
                                loader={this.loader}
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                                selectRow={(rowNo)=>{
                                    this.state.data.variables.rows[rowNo].selected = !this.state.data.variables.rows[rowNo].selected
                                    this.setState(this.state)
                                }}
                                linkProcessor={(rowNo)=>{
                                    this.state.data.variables.rows[rowNo].selected = !this.state.data.variables.rows[rowNo].selected
                                    this.setState(this.state)
                                }}
                            />
                        </Col>
                    </Row>
                </Col>
                <Col xs='12' sm='12' lg='6' xl='6'>
                    <ViewEdit  mode='textarea' attribute='description' component={this} edit/>
                </Col>
            </Row>
        )
    }

    render(){
        if(this.state.labels.locale ==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <Card>
                            <CardHeader>
                                {this.state.labels.amendment}
                            </CardHeader>
                        </Card>
                        <CardBody>
                            {this.dataForm()}
                        </CardBody>
                    </Col>
                </Row>
                <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        <Card>
                            <CardHeader>
                                {this.state.labels.amendment_details}
                            </CardHeader>
                            <CardBody>
                                {this.amendment()}    
                            </CardBody>
                            <CardFooter>
                                {this.state.labels.amendment_details}
                            </CardFooter>
                        </Card>   
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.chapter(true)}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default AmendmentSubmit
AmendmentSubmit.propTypes={
    data:PropTypes.object.isRequired,       //AmendmentDTO
    recipient:PropTypes.string.isRequired,  //the recipient of messages
    
}