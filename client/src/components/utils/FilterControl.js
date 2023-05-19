import React , {Component} from 'react'
import PropTypes from 'prop-types'
import {Row,Col, Button} from 'reactstrap';
/**
 * Control that should be at a bottom of each filter
 * @property runFilter - apply a filter function
 * @property cancelFilter - cancel a filter/default filter function
 * @property offFilter 
 */
class FilterControl extends Component{

    render(){
        return(
            <Row className="m-0 p-0">
            <Col className="m-0 p-0" xs="3" style={{textAlign:'right'}}>
                <Button color='link'
                onClick={this.props.runFilter} ><i className="fas fa-check"/></Button>
            </Col>
            <Col className="m-0 p-0" xs="3" style={{textAlign:'right'}}>
                <Button color='link'
                onClick={this.props.cancelFilter} ><i className="fas fa-undo"/></Button>
            </Col> 
            <Col className="m-0 p-0" xs="3" style={{textAlign:'right'}}>
                <Button color='link'style={{color:'red'}}
                onClick={this.props.offFilter} ><i className="fas fa-power-off"/></Button>
            </Col> 
        </Row>
        )
    }
}

FilterControl.propTypes={
    runFilter:PropTypes.func.isRequired,
    cancelFilter:PropTypes.func.isRequired,
    offFilter:PropTypes.func.isRequired
}
export default FilterControl
