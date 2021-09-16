import React , {Component} from 'react'
import {Container, Row, Col} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import ButtonUni from './form/ButtonUni'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'


/**
 * Responsible Amendments
 * Just copy it
 */
class Amendments extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            labels:{
                search:'',
                global_add:'',
            },
            data:{},
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loader=this.loader.bind(this)
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

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    loader(){
        Fetchers.postJSONNoSpinner("/api/guest/amendments", this.state.data, (query, result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    render(){
        if(this.state.labels.locale == undefined || this.state.data.table==undefined){
            return []
        }
        return(
            <Container fluid>
               <Row className="mb-1">
                   <Col xs='12' sm='12' lg='10' xl='10'/>
                   <Col xs='12' sm='12' lg='2' xl='2'>
                        <ButtonUni
                            label={this.state.labels.global_add}
                            onClick={()=>{
                                let data={
                                    nodeId:0
                                }
                                let param=JSON.stringify(data)
                                Navigator.navigate(Navigator.tabName(),"amendmentstart",param)
                            }}
                            color="primary"
                        />
                   </Col>
               </Row>
               <Row>
                   <Col>
                        <CollectorTable
                            tableData={this.state.data.table}
                            loader={this.loader}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                        />
                   </Col>
               </Row>
            </Container>
        )
    }


}
export default Amendments
Amendments.propTypes={
    
}