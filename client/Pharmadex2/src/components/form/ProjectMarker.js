import React , {Component} from 'react'
import Locales from '../utils/Locales'

/**
 * Marker
 * <Marker position={{lat: 48.670, lng: 35.125}}
              name={'Test 3'} onClick={this.onMarkerClick.bind(this)}/>
 */
class ProjectMarker extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{
                id:0,
                loc:{
                    lat:0,
                    lng:0
                },
                //x:0,
               // y:0,
                title:"",
                name:"",
                label:""
            },
            mdata:{},
            labels:{},
            marker:undefined
        }
        this.init=this.init.bind(this)
        this.paint=this.paint.bind(this)
    }

    componentDidMount(){
        Locales.resolveLabels(this)
        this.state.data.id=this.props.id
        this.state.data.loc = this.props.loc
        //this.state.data.x=this.props.x
        //this.state.data.y=this.props.y
        this.init()
    }

    componentDidUpdate(){
        //if(this.state.data.x != this.props.x && this.state.data.y != this.props.y){
        if(this.state.data.loc.lat != this.props.loc.lat && this.state.data.loc.lng != this.props.loc.lng){
            this.state.data.id=this.props.id
            this.state.data.loc = this.props.loc
            //this.state.data.x=this.props.x
            //this.state.data.y=this.props.y
        }
    }

    init(){
        if(this.state.data != undefined && this.state.data.id >= 0){
            this.state.mdata = {
                key:this.state.data.id + "m",
                id:this.state.data.id,
                //position:new google.maps.LatLng(this.state.data.x, this.state.data.y),
                position:new google.maps.LatLng(this.state.data.loc.lat, this.state.data.loc.lng),
                title:this.state.data.title,
                name:this.state.data.name,
                label:this.state.data.label
            }

            this.state.marker = new google.maps.Marker(this.state.mdata)
        }
    }

    paint(){
        if(this.state.data != undefined && this.state.data.id >= 0 && this.state.data.loc.lat > 0){
            if(this.state.marker != undefined){
                //this.state.marker.setPosition(new google.maps.LatLng(this.state.data.x, this.state.data.y))
                this.state.marker.setPosition(new google.maps.LatLng(this.state.data.loc.lat, this.state.data.loc.lng))
                this.state.marker.setMap(this.props.map)
            }
        }else{
            if(this.state.marker != undefined){
                this.state.marker.setMap(null)
            }
        }
    }

    render(){
        this.paint()
        return []
    }

}
export default ProjectMarker
ProjectMarker.propTypes={
}