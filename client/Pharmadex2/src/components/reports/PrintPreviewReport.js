import React , {Component} from 'react'
import {Container, Row, Col, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from '../utils/Locales'
import Fetchers from '../utils/Fetchers'
import Navigator from '../utils/Navigator'
import Thing from '../Thing'
import Spinner from '../utils/Spinner'
import CollectorTable from '../utils/CollectorTable'
import Pharmadex from '../Pharmadex'

/**
 * Print on scrin all Things where nodeId > 0
 * 
 *  <PrintPreview data={data} narrow/>
 * 
 */
class PrintPreview extends Component{
    constructor(props){
        super(props)
        this.state={
            data:this.props.data,                                      //ThingDTO.java path to fill out things                                               
            identifier:Date.now().toString(),           //address for messages for this object
            recipient:this.props.recipient,             //recipient of messages from this object
            labels:{
                global_print:""
            }
        }
        this.paintThings=this.paintThings.bind(this)
        this.loadPath=this.loadPath.bind(this)
    } 

    componentDidMount(){
        Locales.resolveLabels(this)
        this.loadPath()
    }

    /**
     * load a path until submit for the current application/activity
     */
    loadPath(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/report/print/preview", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        } )
    }

    /**
     * Paint things from this.state.things
     */
    paintThings(){
        let ret = []
        if(this.state.data.regTable.rows.length>0){
            ret.push(
                <CollectorTable key="registersthings"
                    tableData={this.state.data.regTable}
                    loader={()=>{}}
                    headBackground={Pharmadex.settings.tableHeaderBackground}
                />
            )
        }
        if(Fetchers.isGoodArray(this.state.data.path)){
            this.state.data.path.forEach((thing, index)=>{
                thing.readOnly=true
                if(index == 0){
                    ret.push(
                        <h4 key={index+1000}>
                            {this.state.data.applName}
                        </h4>
                    )
                }else{
                    ret.push(
                        <h4 key={index+1000}>
                            {thing.title}
                        </h4>
                    )
                }

                let data={
                    nodeId:thing.nodeId,
                    repaint:true
                }
                ret.push(
                    <Thing key={index}
                        data={data}
                        recipient={this.state.identifier}
                        readOnly={true}
                        narrow
                    />
                )
            })
        }
        return ret
    }
    
    render(){
        if(this.state.data.path == undefined){
            return <div> <i className="blink fas fa-circle-notch fa-spin" style={{color:'#D3D3D3'}}/></div>
        }
        
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='10' xl='10'>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                        <div className="mb-1 d-flex justify-content-end">
                            <Button size="sm" className="mr-1 d-print-none" color="info"
                                    onClick={()=>{
                                            window.print()
                                    }}>
                                {this.state.labels.global_print}
                            </Button>{' '}
                        </div>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        {this.paintThings()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default PrintPreview
PrintPreview.propTypes={
    data:PropTypes.shape(
        {
            nodeId:PropTypes.number.isRequired,      //id of the histry record to determine activity and data. Zero means new   
        }
    ).isRequired,
}