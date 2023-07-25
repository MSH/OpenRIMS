import React , {Component} from 'react'
import {Container, Row, Col,Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Navigator from './utils/Navigator'
import ViewEditDate from './form/ViewEditDate'
import FieldsComparator from './form/FieldsComparator'

/**
 * Dates interval component
 * Allows validation based on from and to dates
 * Issues event onSelectionChange
 */
class Interval extends Component{
    constructor(props){
        super(props)
        delete this.props.data.reload
        this.state={
            data: this.props.data,            //IntervalDTO
            identifier:Date.now().toString(),
            labels:{
                to:'',
                from:'',      
            }
        }
        this.comparator = new FieldsComparator(this)
        this.eventProcessor=this.eventProcessor.bind(this)
    }

    /**
     * Place the interval to Thing
     * @param {IntervalDTO} data 
     * @param {the caller} recipient 
     * @param {is readonly} readonly 
     * @param {ReactJS key } key
     * @param {label above the component} label
     */
    static place(data, recipient, readonly, key, label){
        let ro= readonly || data.readonly
        let color="info"
        if(data.strict){
            color="danger"
        }
        let mark=""
        if(data.changed){
            mark= "markedbycolor"
        }
        return(
            <Row key={key} className={mark}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{label}</h6>
                        </Col>
                    </Row>
                    <Row hidden={data.valid}>
                        <Col>
                            <Alert color={color} className="p-0 m-0">
                                <small>{data.identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Interval data={data} recipient={recipient} readonly={ro}  />
                        </Col>
                    </Row>
                </Col>
            </Row>
        )
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
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    componentDidUpdate(){
        if(this.state.data.varname != undefined){
            let varname=this.state.data.varname
            const fld = this.comparator.checkChanges()
            if(fld.includes(varname+"_to") || fld.includes(varname+"_from")){
                Navigator.message(this.state.identifier, this.props.recipient, "onSelectionChange", this.state.data)
            }
        }
        if(this.props.data.reload){
            delete this.props.data.reload
            this.state.data=this.props.data
            this.setState(this.state.data)
        }
    }

    render(){
        if(this.state.labels.locale==undefined){
            return []
        }
        return(
            <Container fluid>
                <Row>
                    <Col>
                        <ViewEditDate attribute={"from"} component={this} edit={!this.props.readonly}/>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <ViewEditDate attribute={"to"} component={this} edit={!this.props.readonly}/>
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default Interval
Interval.propTypes={
    data:PropTypes.object.isRequired,         //IntervalDTO
    recipient:PropTypes.string.isRequired,   //recipient for messaging
    readonly:PropTypes.bool,                //is it read only?
}