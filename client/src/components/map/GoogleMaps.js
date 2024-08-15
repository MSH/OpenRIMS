import React,{useState, useRef} from 'react';
import PropTypes from 'prop-types';
import {
  APIProvider,
  Map,
  AdvancedMarker
} from '@vis.gl/react-google-maps'
import Navigator from '../utils/Navigator';

/**
 * Show goole map and allow mark a position on it
 * The position can be stored
 */
function GoogleMaps({recipient, center, zoom, marker, apiKey}){
    const [position,setPosition] = useState(marker)
    let identifier = useRef(Date.now().toString()+Math.random())          //Who am I in messages identifier.current
    /**
     * 
     * Show or not the marker
     */
    function showMarker(){
      if(position==undefined){
        return []
      }else{
        return <AdvancedMarker position={{lat: position.lat, lng: position.lng}} />
      }
    }
  return (
    <APIProvider apiKey={apiKey}>
      <Map 
      defaultZoom={zoom} defaultCenter={{lat: center.lat, lng:center.lng}}
      style={{width: '98%', height: '300px'}} mapId='DEMO_MAP_ID'
      onClick={(event)=>{
        event.stop()
        let newPosition=event.detail.latLng
        setPosition({lat:newPosition.lat,lng:newPosition.lng})
        Navigator.message(identifier,recipient,"gis_position_changed",newPosition) //inform position change
      }}
      >
        {showMarker()}
        
      </Map>
    </APIProvider>
  );
}
GoogleMaps.propTypes={
    recipient:PropTypes.string.isRequired,
    center: PropTypes.exact({
      lat: PropTypes.number.isRequired,
      lng: PropTypes.number.isRequired,
    }).isRequired,
    marker:PropTypes.exact({
      lat: PropTypes.number.isRequired,
      lng: PropTypes.number.isRequired,
    }),
    zoom: PropTypes.number.isRequired,
    apiKey:PropTypes.string.isRequired,
  }
export default GoogleMaps