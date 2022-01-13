import React , {Component} from 'react'
import {Container, Col, Row} from 'reactstrap'
import Navigator from './utils/Navigator'
import Spinner from './utils/Spinner'
import Alerts from './utils/Alerts'
import CommonHeader from './CommonHeader'
import Content from './Content'
import About from './About'
import AlertFloat from './utils/AlertFloat'


/**
 * The Pharmadex main
 * consists of header, landing content and footer (about) components
 * Contains common settings for constants
 */
class Pharmadex extends Component{
    constructor(props){
        super(props)
        this.state={}
        this.nav= new Navigator(this)
    }
    /**
     * 
     * @returns common wait
     */
    static wait(){
        return <div> <i className="blink fas fa-circle-notch fa-spin" style={{color:'#D3D3D3'}}/></div>
    }

    render(){
        return(
            <Container fluid>
                    
                    <Alerts />
                    <Row>
                        <Col xs='12' sm='12' lg='12' xl='12'>
                            <CommonHeader menu={Navigator.tabSetName()} navigator={this.nav}/>
                        </Col>
                    </Row>
                    <AlertFloat />
                    <Row>
                        <Col xs='12' sm='12' lg='12' xl='12'>
                            <Content menu={Navigator.tabSetName()} navigator={this.nav}/>
                        </Col>
                    </Row>
                    <Row>
                        <Col xs='12' sm='12' lg='12' xl='12'>
                            <About menu={Navigator.tabSetName()} navigator={this.nav}/>
                        </Col>
                    </Row>
            </Container>
        )
    }


}
export default Pharmadex
Pharmadex.propTypes={
    
}
/**
 * Common constants
 */
Pharmadex.settings={
    tableHeaderBackground:"#6c757d",                           //background color for table headers
    activeBorder:"border-5 border-primary shadow m-2",    //border for form currently edit
}