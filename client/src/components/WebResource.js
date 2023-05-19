import React , {Component} from 'react'
import {Row, Col} from 'reactstrap'
import PropTypes from 'prop-types'
import FieldUpload from './form/FieldUpload'
import Fetchers from './utils/Fetchers'

/**
 * Web resource definition
 * @example
 * <WebResource data={this.state.logoImage} />
 */
class WebResource extends Component{
    constructor(props){
        super(props)
        this.state={
            file:{}
        }
        this.fileError=this.fileError.bind(this)
        this.postForm=this.postForm.bind(this)
    }
   /**
     * Error message for a file
     */
    fileError(){
        let fileName = this.props.data.fileName
        let ret=""
        if(fileName !== undefined){
            if(fileName.error){
                ret = fileName.suggest
            }
        }
        return ret;
    }

        /**
     * Post form data with or without a file
     */
    postForm(){
        let formData = new FormData()
        formData.append('dto', JSON.stringify(this.props.data))
        formData.append('file', this.state.file);
        Fetchers.postFormJson(this.props.data.apiUpload.value,formData, (formData,result)=>{
            if(Fetchers.isGoodArray(Object.keys(this.props.data))){
                Object.keys(this.props.data).forEach((key)=>{
                    this.props.data[key]=result[key]
                })
            }
        })
    }

    render(){
        switch(this.props.data.resourceType){
            case 0:
                return(
                    <Row>
                        <Col xs='8' sm='8' lg='8' xl='8'>   
                        <FieldUpload id="logouploadid" onChange={(file)=>{
                            this.state.file=file
                            if(this.props.data.fileSize !== undefined && this.props.data.fileName !== undefined){
                                this.props.data.fileSize.value=file.size/1024   //KBytes
                                this.props.data.fileName.value=file.name
                                this.postForm()
                            }
                        }}
                        prompt={this.props.data.title.value}
                        error={this.fileError()}                            
                        />
                        </Col>
                        <Col hidden={this.props.data.id==0} xs='8' sm='8' lg='8' xl='8'>
                            <img src={this.props.data.urlDownload.value} width='80%' />
                        </Col>
                    </Row> 
                )
            default:
                return(
                 []
                )
        }
    }


}
export default WebResource
WebResource.propTypes={
    data:PropTypes.object.isRequired    //WebResourceDTO.java
}