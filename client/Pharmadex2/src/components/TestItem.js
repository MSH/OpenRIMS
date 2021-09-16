import React , {Component} from 'react'
import {Container, Row, Col,Label} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import ButtonUni from './form/ButtonUni'

/**
 * admin menu item
 * to debug new components
 */
class TestItem extends Component{
    constructor(props){
        super(props)
        this.identifier="TestItem",
        this.state={
            data:{}
        }
        this.requestSend=this.requestSend.bind(this)
        this.load=this.load.bind(this)
    }

    componentDidMount(){
        
    }

    requestSend(){
        
    }

    load(){
        //let url = "/api/admin/checklist/create"
        
    }//
    
    render(){
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <Label>
                            Test component 
                        </Label>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2'>
                                    <ButtonUni label="Send request" outline={false}
                                            onClick={()=>{
                                                this.requestSend()
                                            }} />
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default TestItem
TestItem.propTypes={
    
}