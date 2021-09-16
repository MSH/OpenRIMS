import React , {Component} from 'react'
import {Card, CardTitle,CardText, CardBody,Row, Col, Button, Container, Label} from 'reactstrap'
import PropTypes from 'prop-types'
import Fetchers from './utils/Fetchers'
import FeatureState from './FeatureState'

/**
 * Represent a tile on the screen
 * @example <Tile content={tileDto} labels={this.state.labels}/>
 * Requires lables 'more", "download" 
 */
class Tile extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},
            picture:true,
        }
        this.featureStates = this.featureStates.bind(this)
    }

    featureStates(){
        if(this.props.content.description != undefined && this.props.content.description.length > 0){
            return (
                <Label>{this.props.content.description}</Label>
            )
        }else{
            return <h5>---------------</h5>
        }
    }

    render(){
        if(this.props.content == undefined){
            return []
        }
        if(this.props.content.title == undefined){
            return []
        }
        let hasMore = false
        if(this.props.content.more.length > 0){
            hasMore = true
        }
        return(
        <Container fluid
            onMouseEnter={()=>{
                this.state.picture=false
                this.setState(this.state)
                }
            } 
            onMouseLeave={()=>{
                this.state.picture=true
                this.setState(this.state) 
                }
            }
        >
        <Row hidden={!this.state.picture}>
                <Col className='pr-5 pl-5 pt-1 pb-1'>
                <a href="#" key="1">
                <figure>
                    <img src={this.props.content.image}/>
                    <figcaption className={"bg-dark"}>
                        <Row>
                            <Col xs='1' sm='1' lg='1' xl='1'>
                                {FeatureState.markColor(this.props.content.color)}
                            </Col>
                            <Col xs='11' sm='11' lg='11' xl='11'>
                                {this.props.content.title}
                            </Col>
                        </Row>
                    </figcaption>
                </figure>
                </a>
                </Col>
        </Row>
            <Row hidden={this.state.picture}>
                <Col className='pr-5 pl-5 pt-1 pb-1'>
                <Card className={"tile"} >
                    <CardBody >
                        <CardTitle tag="h5">{this.props.content.title}</CardTitle>
                        <Row className="pl-2 ml-1">
                            <Col xs='10' sm='10' lg='11' xl='11' style={{lineHeight:'0.8em'}}>
                                <small>{this.props.content.description}</small>
                            </Col>
                        </Row>
                    </CardBody>
                        <Button hidden={this.props.content.download.length==0} color="secondary" size="sm">
                            {this.props.labels.download}
                        </Button>
                        <Button hidden={!hasMore} color={hasMore?"success":"info"} size="sm"
                            onClick={()=>{
                                window.location=this.props.content.more
                                }
                            }>
                                {hasMore?
                                    this.props.content.moreLbl:this.props.labels.more}
                        </Button>{' '}
                        
                </Card>
                </Col>
            </Row>
        </Container>
        )
    }


}
export default Tile
Tile.propTypes={
    content:PropTypes.object.isRequired,
    labels:PropTypes.object.isRequired
}