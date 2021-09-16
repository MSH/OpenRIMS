import React , {Component} from 'react'
import Locales from '../utils/Locales'

/**
 * Marker
 * <ProjectMarker key={1} id={1} loc={this.createLatLng(this.state.data.location)}/>
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
                title:"",
                isDefault:true
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
        this.state.data.title = this.props.title
        this.state.data.isDefault = this.props.isDefault
        this.init()
    }

    componentDidUpdate(){
        if(this.state.data.loc.lat != this.props.loc.lat && this.state.data.loc.lng != this.props.loc.lng){
            //this.state.data.id=this.props.id
            this.state.data.loc = this.props.loc
            this.state.data.title = this.props.title
            this.state.data.isDefault = this.props.isDefault
            this.setState(this.state.data)
        }
    }

    init(){
        if(this.state.data != undefined){
            if(this.state.data.isDefault){
                this.state.mdata = {
                    key:this.state.data.id + "m",
                    id:this.state.data.id,
                    position:new google.maps.LatLng(this.state.data.loc.lat, this.state.data.loc.lng),
                    title:this.state.data.title,
                }
            }else{
                this.state.mdata = {
                    key:this.state.data.id + "m",
                    id:this.state.data.id,
                    position:new google.maps.LatLng(this.state.data.loc.lat, this.state.data.loc.lng),
                    title:this.state.data.title,
                    //name:this.state.data.name,
                    //label:this.state.data.label,
                    icon:{ url: 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDEyOCAxMjgiIGhlaWdodD0iMTI4cHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAxMjggMTI4IiB3aWR0aD0iMTI4cHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxnPjxnPjxwYXRoIGQ9Ik02NCwwQzM5LjY5OSwwLDIwLDE5LjY5OSwyMCw0NHM0NCw4NCw0NCw4NHM0NC01OS42OTksNDQtODRTODguMzAxLDAsNjQsMHogTTI4LDQ0ICAgIEMyOCwyNC4xNDgsNDQuMTQ4LDgsNjQsOHMzNiwxNi4xNDgsMzYsMzZjMCwxMy44MjgtMjAuMDA4LDQ3LjIxMS0zNiw3MC4yMzhDNDguMDA4LDkxLjIxMSwyOCw1Ny44MjgsMjgsNDR6IE02NCwyNCAgICBjLTExLjA0NywwLTIwLDguOTUzLTIwLDIwczguOTUzLDIwLDIwLDIwczIwLTguOTUzLDIwLTIwUzc1LjA0NywyNCw2NCwyNHogTTY0LDU2Yy02LjYxNywwLTEyLTUuMzgzLTEyLTEyczUuMzgzLTEyLDEyLTEyICAgIHMxMiw1LjM4MywxMiwxMlM3MC42MTcsNTYsNjQsNTZ6IiBmaWxsPSIjRTUzOTM1Ii8+PC9nPjwvZz48L3N2Zz4=', scaledSize: new google.maps.Size(40, 40) }
                }
            }
            

            this.state.marker = new google.maps.Marker(this.state.mdata)
        }
    }

    paint(){//&& this.state.data.id >= 0 
        if(this.state.data != undefined && this.state.data.loc.lat > 0){
            if(this.state.marker != undefined){
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