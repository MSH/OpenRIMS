import React , {Component} from 'react'
import {GoogleApiWrapper,  Map} from 'google-maps-react'
import {Container, Row, Col,Label} from 'reactstrap'
import Navigator from '../utils/Navigator'
import ProjectMarker from './ProjectMarker'
import Fetchers from '../utils/Fetchers'

/**
 * component Goggle Map
 */
const mapStyles = {
    width: '98%',
    height: '300px'
  };
class ProjectMap extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{
                l:{
                    lat:0, lng:0
                }
            },
            map:undefined,
            mapsapi:undefined,
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        
    }

    /**
     * listen for askData broadcast and getData only to own address
     */
    eventProcessor(event){
        let data=event.data
        if(data.subject == 'updatemap'){
            this.state.data = data.data
            this.setState(this.state.data)
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
    }

    componentDidUpdate(){

    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    paintLocation(){
        return(
            <ProjectMarker key={1} id={1} loc={this.props.marker} isDefault={true}/>
        )
    }

    /*paintListOther(){
        let array = []
        if(this.props.listother != undefined){
            if(Fetchers.isGoodArray(this.props.listother)){
                this.props.listother.forEach(sitedto => {
                    array.push(
                        <ProjectMarker key={sitedto.id} id={sitedto.id} loc={sitedto.center}
                                        title={sitedto.name} isDefault={false}/>
                    )
                });
            }
        }
        return array
    }*/

    /**
     * По клику на карте рисуем в указаных координатах маркер (на карте ВСЕГДА ОДИН маркер)
     * сохраняем координаты
     */
    mapClicked(mapProps, map, event) {
        if(!this.component.props.readOnly){
            this.component.props.marker.lat = event.latLng.lat()
            this.component.props.marker.lng = event.latLng.lng()
            this.component.setState(this.component.state)
        }
    }

    /**
     * сохраняем уже полностью загруженную и созданную карту в переменную для дальнейшей работы с ней
     * можно будет использовать только методы работы с картой (вставлять в визуальный компонент в таком виде нельзя)
     */
    mapReady(mapProps, map) {
        mapProps.component.state.map=map
        mapProps.component.state.mapsapi=mapProps.google.maps
    }

    render(){
        if(this.props.zoom != undefined){
            let onClick=this.mapClicked
            if(this.props.readOnly){
                onClick=()=>{}
            }
            return (
                <Map ref="map"
                        google={this.props.google}
                        zoom={this.props.zoom}
                        style={mapStyles}
                        gestureHandling={'greedy'}
                        mapTypeId={"roadmap"}
                        initialCenter={this.props.center}
                        center={this.props.center}
                        onClick={onClick}
                        onReady={this.mapReady}
                        component={this}
                        visible={true}
                        >
                            {this.paintLocation()}
                </Map>
            )
        }
        return []
    }

}

export default GoogleApiWrapper({
    apiKey: 'AIzaSyDqaSM_NhQjdvN5bL9iYdFqWREFEkeQLuY'
  })(ProjectMap)