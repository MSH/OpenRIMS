import React , {Component} from 'react'
import {Container,Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'

/**
 * Displays the state of a feature. Typically as rows in a tile content
 * @example
 * <FeatureState key={i} data={this.state.data.content[i]} />   //content[i] is FeatureStateDTO.java
 */
class FeatureState extends Component{
    constructor(props){
        super(props)
        this.state={
            labels:{}
        }
    }
    componentDidMount(){
        Locales.resolveLabels(this)
    }

    static markColor(color){
        switch(color){
            case 0:
                return <i className="fas fa-circle" style={{color:'green'}}></i>
            case 1:
                return <i className="fas fa-circle" style={{color:'yellow'}}></i>
            case 2:
                return <i className="fas fa-circle" style={{color:'red'}}></i>
        }
        return <i ></i>//className="fas fa-circle" style={{color:'green'}}
    }

    render(){
        return(
            <Container fluid>
                <Row className="ml-1">
                    <Col>
                        <h6 className="p-0 m-0">{this.props.data.featureName}</h6>
                    </Col>
                </Row>
                <Row className="pl-2 ml-1">
                    <Col xs='10' sm='10' lg='11' xl='11' style={{lineHeight:'0.8em'}}>
                        <small>{this.props.data.featureState}</small>
                    </Col>
                    <Col xs='2' sm='2' lg='1' xl='1'>
                        {FeatureState.markColor(this.props.data.color)}
                    </Col>
                </Row>
               
                
            </Container>
                
        )
    }


}
export default FeatureState
FeatureState.propTypes={
    data:PropTypes.object.isRequired,   //FeatureStateDTO.java
    
}