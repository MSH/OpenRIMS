import React , {Component} from 'react'
import {Button, Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import ViewEdit from './ViewEdit'

/**
 * Field guarded from accidental changes
 *  mode: PropTypes.oneOf(['text','textarea','number']).isRequired, //type of data
 *   attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
 *   component   :PropTypes.object.isRequired,                        //caller component
 * @example
 *  
*/
class FieldGuarded extends Component{
    constructor(props){
        super(props)
        this.state={
            edit:false,
            identifier:Date.now().toString(),
            labels:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
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
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    render(){
        return(
            <div>
<Row>
                    <Col xs='12' sm='12' lg='10' xl='10'>
                        <ViewEdit mode={this.props.mode} attribute={this.props.attribute} component={this.props.component} edit={this.state.edit}/>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='2' className="align-self-center">
                    <Button
                        style={{color:'green'}}
                        hidden={this.state.edit}
                        onClick={()=>{
                            this.state.edit=true
                            this.setState(this.state)
                        }}
                        outline
                    >
                        <i className="fas fa-lock"></i>
                    </Button>
                    <Button
                        style={{color:'red'}}
                        hidden={!this.state.edit}
                        onClick={()=>{
                            this.state.edit=false
                            this.setState(this.state)
                        }}
                        outline
                    >
                        <i className="fas fa-lock-open"></i>
                    </Button>
                    </Col>
                </Row>
            </div>
        )
    }


}
export default FieldGuarded
FieldGuarded.propTypes={
    mode: PropTypes.oneOf(['text','textarea','number']).isRequired, //type of data
    attribute  :PropTypes.string.isRequired,                        //should be component.state.labels[attribute] and component.state.data[attribute]
    component   :PropTypes.object.isRequired,                        //caller component
}