import React , {Component} from 'react'
import {Row, Col, Label,Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Dictionary from './Dictionary'
import GoogleMaps  from './map/GoogleMaps'
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
            data:this.props.data,
            labels:{
                global_close:'',
                gisLocation:'',
                gisView:'',
                streetaddress:""
            },
            curcenter:undefined,
            curzoom:0,
            map:undefined,
            point:{}
        }
        if(window.location.hostname.toUpperCase()=='LOCALHOST'){
            this.state.data.googleMapApiKey="AIzaSyBBpXaRXiSfpkr01F1mCYQtHrcYOS1C_80" //we don't need the extrnal key while development 
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
                this.state.data.marker  = {lat:0.000000,lng:0.000000}
                this.setState(this.state)
                Navigator.message(this.state.identifier,this.props.recipient, 'onSelectionChange', this.state.data)
            }
            if(data.subject=='gis_position_changed'){
                this.state.data.marker.lat=data.data.lat
                this.state.data.marker.lng=data.data.lng
                this.setState(this.state)
                Navigator.message(this.state.identifier,this.props.recipient, 'onSelectionChange', this.state.data)
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    componentDidUpdate(){
       if(this.props.data.reload){
           delete this.props.data.reload
           this.state.data=this.props.data
           this.state.data.dictionary.reload=true
           this.setState(this.state)
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
    /**
     * 
     * @returns true, if the marker is defined
     */
    isMarkerEmpty(){
        let marker=this.state.data.marker
        if(marker==undefined){
            return false
        }else{
            if(marker.lat!=undefined && marker.lng!=undefined){
                return this.state.data.marker.lat+this.state.data.marker.lng < 0.000001
            }else{
                return false
            }
        }
    }

    markerToString(){
        var lbl = ""
        if(this.isMarkerEmpty){
            lbl = this.state.labels.gisView + ": "
            lbl += this.state.data.marker.lat + "; " + this.state.data.marker.lng
        }
        return lbl
    }
    passMarker(){
        if(this.isMarkerEmpty() ){
            return undefined
        }else{
            return ({lat: this.state.data.marker.lat, lng: this.state.data.marker.lng})
        }
    }
    /**
     * place a map or button if is is allowed
     */
    placeMap(){
        if(this.state.data.googleMapApiKey.length==0){
            return []
        }
        //map or button?
        if(this.state.showmap){
            return(
                <Row>
                    <Col xs='12' sm='12' lg='12' xl='12'>
                        <Row style={{height:'300px'}}>
                            <GoogleMaps 
                                    recipient={this.state.identifier}
                                    center={{lat:this.state.curcenter.lat,lng:this.state.curcenter.lng}} 
                                    marker={this.passMarker()}
                                    zoom={this.state.curzoom}
                                    apiKey={this.state.data.googleMapApiKey}/>
                        </Row>
                        <Row >
                            <Col xs='12' sm='12' lg='6' xl='6'/>
                            <Col xs='12' sm='12' lg='6' xl='6'>
                                <ButtonUni
                                    label={this.state.labels.global_close}
                                    outline
                                    onClick={()=>{
                                        this.state.showmap=false
                                        this.setState(this.state)
                                    }}
                                    color="info"
                                />
                            </Col>
                        </Row>
                    </Col>
            </Row>
            )

        }else{
            return(
                <Row>
                    <Col>
                        <ButtonUni
                                label={this.state.labels.gisLocation}
                                onClick={()=>{
                                    {this.loadCenterZoom()}
                                }}
                                color="success"
                        />
                    </Col>
                </Row>
            )
        }
    }

    render(){
        if(this.state.labels.locale != undefined && this.state.data.dictionary != undefined){
            if(this.state.showmap){
                return(
                    this.placeMap()
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
                       {this.placeMap()}
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
