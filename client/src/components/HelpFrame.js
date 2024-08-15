import React, { useState, useEffect, useRef } from 'react'
import PropTypes from 'prop-types'
import {Row, Col, Button} from 'reactstrap'
import Locales from './utils/Locales'

/**
 * The component
 */
function HelpFrame({recipient,showIt}) {
  const ref = useRef(null)
  const [zoom,setZoom]= useState(140)
  const [showHelp, setShowHelp] = useState(showIt)
  const[labels,setLabels] = useState({
    global_help:'',
  })

  //handle PDF zoom
  function handleResize() {
    if(window.innerWidth>1200){
      setZoom(140)
    }else{
      if(window.innerWidth>900)
        setZoom(100)
      else{
        setZoom(70)
      }
    }
  }
  /**
   * Handle messages from other components
   * ~~~
   * data:{
   *  from:from,to:to,subject:subject,data:data
   * }
   * ~~~
   */
  function handleMessages(event){
    let data=event.data
    
  }

  useEffect(() => {
    window.addEventListener('resize', handleResize);
    window.addEventListener("message",handleMessages)
    Locales.resolveLabelsFunctional(labels, setLabels)
  }, []);

  if(showHelp){
    return (
      <div ref={ref} className='d-flex justify-content-center' style={{ minHeight:'80vh'}}>
        <iframe key={zoom} 
          style={{height:'80vh', width:'90vw', frameborder:'0', scrolling:'no'}} 
          src={'/api/admin/help/administrate#top&navpanes=0&toolbar=0&zoom='+zoom+"'"}
        >
        </iframe>
      </div>
    )
  }else{
    return(
      <Row className='mt-5'>
        <Col className='d-flex justify-content-end'>
            <Button
              color="link"
              onClick={()=>{
                setShowHelp(true)
              }}
            >
              <i className="far fa-question-circle fa-2x"></i>
            </Button>
        </Col>
      </Row>
    )
  }
}
HelpFrame.propTypes={
  recipient:PropTypes.string.isRequired,  //the top component identifier for messages
  showIt:PropTypes.bool.isRequired          //show or hide the help
}
export default HelpFrame