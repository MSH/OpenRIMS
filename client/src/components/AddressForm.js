import React , {Component} from 'react'
import {Row, Col, Label,Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Dictionary from './Dictionary'
import ProjectMap from './map/ProjectMap'
import ButtonUni from './form/ButtonUni'
import Navigator from './utils/Navigator'

/**
 * Address component with Dictionary, Field and Google Map
 * @example
 * <AddressForm data={this.state.data} />
 * />
 */

class AddressForm extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),  //my address for messages
            showmap:false,
            data:{
            },
            labels:{
                global_save:'',
                global_cancel:'',
                gisLocation:'',
                gisView:'',
                streetaddress:""
            },
            curcenter:undefined,
            curzoom:0,
            map:undefined,
            point:{}
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.loadCenterZoom=this.loadCenterZoom.bind(this)
    }
    /**
     * Return changed style
     * @param {AddressDTO} addr 
     */
    static changed(addr){
        if(addr.changed){
            return "markedbycolor"
        }else{
            return ""
        }
    }

    /**
     * Place an address to Thing.js or any other
     * @returns This component Writable or read only
     */
    static place(addr, index, readOnly,identifier, label){
        let color="info"
                if(addr.strict){
                    color="danger"
                }
        return(
            <Row key={index} className={AddressForm.changed(addr)}>
            <Col>
                <Row>
                    <Col>
                        <h6>{label}</h6>
                    </Col>
                </Row>
                <Row hidden={addr.valid}>
                        <Col>
                            <Alert color={color} className="p-0 m-0">
                                <small>{addr.identifier}</small>
                            </Alert>
                        </Col>
                </Row>
                <Row>
                    <Col>
                        <AddressForm
                                key={identifier+index}
                                data={addr}
                                recipient={identifier}
                                readOnly={readOnly}
                        />
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
        if(event.data.to==this.state.identifier){
            let data=event.data
            if(data.subject=='onSelectionChange'){
                this.state.data.dictionary = data.data
                this.state.data.marker  = {}
                this.setState(this.state.data)
                Navigator.message(this.state.identifier,this.props.recipient, 'onSelectionChange', this.state.data)
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
        this.state.data = this.props.data
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    componentDidUpdate(){
       if(this.props.data.reload){
           delete this.props.data.reload
           this.state.data=this.props.data
           this.state.data.dictionary.reload=true
           this.setState(this.state.data)
       }
    }
    
    loadCenterZoom(){
        if(this.state.data.dictionary.prevSelected.length > 0){
            let index = this.state.data.dictionary.prevSelected.length - 1
            let selKey = this.state.data.dictionary.prevSelected[index]
            let gisloc = this.state.data.homecenter
            gisloc.id = selKey
            Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/application/loadcentermap", gisloc, (query,result)=>{
                gisloc = result
                this.state.curcenter=gisloc.center
                this.state.curzoom=gisloc.zoom
                // сохраним координаты маркера в переменную для кнопки Cancel
                this.state.point = this.state.data.marker
                this.state.showmap=true
                this.setState(this.state)
            })
        }else{
            this.state.curcenter=this.state.data.homecenter.center
            this.state.curzoom=this.state.data.homecenter.zoom

            // сохраним координаты маркера в переменную для кнопки Cancel
            this.state.point = this.state.data.marker
            this.state.showmap=true
            this.setState(this.state)
        }
    }

    markerToString(){
        var lbl = ""
        if(this.state.data.marker != undefined && this.state.data.marker.lat != undefined
            && this.state.data.marker.lat > 0){
            lbl = this.state.labels.gisView + ": "
            lbl += this.state.data.marker.lat + "; " + this.state.data.marker.lng
        }
        return lbl
    }

    render(){
        if(this.state.labels.locale != undefined && this.state.data.dictionary != undefined){
            if(this.state.showmap){
                return(
                <Row>
                    <Col xs='12' sm='12' lg='12' xl='12'>
                        <Row style={{height:'300px'}}>
                            <ProjectMap zoom={this.state.curzoom} center={this.state.curcenter}
                                    marker={this.state.data.marker}
                                    readOnly={this.props.readOnly}/>
                        </Row>
                        <Row >
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <div hidden={this.props.readOnly}>
                                    <ButtonUni
                                        label={this.state.labels.global_save}
                                        onClick={()=>{
                                            this.state.showmap=false
                                            this.setState(this.state)
                                            Navigator.message(this.state.identifier,this.props.recipient, 'onSelectionChange', this.state.data)
                                        }}
                                        color="success"
                                        hidden={this.state.data.readOnly}
                                    />
                                </div>
                            </Col>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <ButtonUni
                                    label={this.state.labels.global_cancel}
                                    onClick={()=>{
                                        // вернем сохраненные координаты маркера
                                        this.state.data.marker = this.state.point
                                        this.state.showmap=false
                                        this.setState(this.state)
                                        Navigator.message(this.state.identifier,this.props.recipient, 'onSelectionChange', this.state.data)
                                    }}
                                    color="info"
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
                )
            }else{
                return (
                <Row>
                    <Col xs='12' sm='12' lg='12' xl='12'>
                        <Row>
                        <Dictionary identifier={this.state.data.dictionary.identifier}
                            data={this.state.data.dictionary}
                            recipient={this.state.identifier}
                            readOnly={this.props.readOnly}
                            display/>
                        </Row>
                        <Row >
                            <Col>
                                <Label style={{fontSize:'0.8rem'}}>{this.markerToString()}</Label>
                            </Col>
                        </Row>
                        <Row>
                            <ButtonUni
                                    label={this.state.labels.gisLocation}
                                    onClick={()=>{
                                        {this.loadCenterZoom()}
                                    }}
                                    color="success"
                                />
                        </Row>
                    </Col>
                </Row>
                )
            }
        }
        return []
    }
}//hidden={!this.props.readOnly}
export default AddressForm
AddressForm.propTypes={
    data:PropTypes.object.isRequired,          //AddressDTO object
    recipient:PropTypes.string.isRequired,      //parent object address for conversation
    readOnly:PropTypes.bool                     //is it read only?
}
