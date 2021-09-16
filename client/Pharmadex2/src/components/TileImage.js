import React , {Component} from 'react'
import {Row, Col, Container} from 'reactstrap'
import PropTypes from 'prop-types'
import Fetchers from './utils/Fetchers'
import FeatureState from './FeatureState'

/**
 * Tile для страницы констуктора страниц пользователей
 * Показывает только картинку и заголовок
 */
class TileImage extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},
        }
    }

    createContent(){
        if(this.props.content.empty){
            return (
                <figure style={{cursor:'hand'}}>
                    <img src={this.props.content.image}/>
                </figure>
            )
        }else{
            return (
                <figure style={{cursor:'hand'}}>
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
            )
        }
    }

    render(){
        if(this.props.content.title == undefined){
            return []
        }
        return(
        <Container fluid
            draggable={typeof this.props.loader =='function'}
            onDragStart={(event)=>{
                let keyFrom = "empty"
                if(this.props.content.free){
                    keyFrom = this.props.content.title
                }else if(!this.props.content.empty){
                    keyFrom = this.props.content.numRow + "&" + this.props.content.numCol
                }
                event.dataTransfer.setData("text/plain", keyFrom)
            }}
            onDragOver={(event)=>{
                    event.preventDefault();
            }}
            onDrop={(event)=>{
                if(typeof this.props.loader =='function'){
                    event.preventDefault();
                    let keyFrom=event.dataTransfer.getData("text/plain")
                    let keyTo = this.props.content.numRow + "&" + this.props.content.numCol
                    this.props.changeCol(keyFrom, keyTo)
                    this.props.loader();
                }
            }}>
                
            <Row>
                <Col>
                    {this.createContent()}
                </Col>
            </Row>
        </Container>
        )
    }


}
export default TileImage
TileImage.propTypes={
    content:PropTypes.object.isRequired,
    labels:PropTypes.object.isRequired
}