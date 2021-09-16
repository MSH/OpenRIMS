import React , {Component} from 'react'
import {Container, Row, Col, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import Thing from './Thing'
import Spinner from './utils/Spinner'

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
            data:{                                      //ThingDTO.java path to fill out things                   
                historyId:this.props.data.historyId,
                applDictNodeId:this.props.data.applDictNodeId,
                applicationUrl:this.props.data.url,
                readOnly:true,
            },                                            
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
        this.state.data.historyId=this.props.data.historyId
        this.state.data.applDictNodeId=this.props.data.applDictNodeId
        this.state.data.applicationUrl=this.props.data.url
        
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/activity/printprev", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        } )
    }

    /**
     * Paint things from this.state.things
     */
    paintThings(){
        let ret = []
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
                        narrow={this.props.narrow}
                    />
                )
            })
        }
        return ret
    }
    
    render(){
        if(this.state.data.path == undefined){
            Spinner.show()
            return []
        }
        
        return(
            <Container fluid>
                <Row>
                    <Col>
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
            url:PropTypes.string.isRequired,            //url of an application, i.e. application.guest
            applDictNodeId:PropTypes.number,            //id of dictionary node that describes an application
            historyId:PropTypes.number.isRequired,      //id of the histry record to determine activity and data. Zero means new   
        }
    ).isRequired,
    narrow:PropTypes.bool,                          // single or double columns layout, default double
}