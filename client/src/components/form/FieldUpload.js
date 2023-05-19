import React , {Component} from 'react'
import {CustomInput, FormGroup, FormFeedback, Label} from 'reactstrap'
import PropTypes from 'prop-types'

/**
 * Select a file to upload 
 * @property {function} onChange(file) - file is complex JS file object (see https://developer.mozilla.org/en-US/docs/Web/API/File/Using_files_from_web_applications)
 * @property {string} prompt - propmpt to select a file
 * @property {string} error - error message
 * @example <FieldUpload accept=".docx, image/*, .pdf" onChange={this.onFileChange} prompt={this.state.labels.download_attach} error={this.state.data.fileSize.suggest}/>
 * Broadcast event 'cleanUpAllFileUploaders' will clean up this component and others unmounted uploaders   
*/
class FieldUpload extends Component{
    constructor(props){
        super(props)
        
    }

    render(){
        return(
            <FormGroup>
                {/*<Label for={this.props.id}>{this.props.prompt}</Label>*/}
                <CustomInput id='fileinputidinactivity' bsSize="sm" type="file" accept={this.props.accept}
                            label={this.props.prompt}
                            onChange={(e)=>{this.props.onChange(e.target.files[0])}}
                            invalid={this.props.error.length>0}
                            valid={this.props.error.length==0}/>
                <FormFeedback valid={false} style={{display:'inline'}}>{this.props.error}</FormFeedback>
            </FormGroup>
        )
    }


}
export default FieldUpload
FieldUpload.propTypes={
    accept:PropTypes.string.isRequired,         //acceptible file types, see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/accept
    onChange:PropTypes.func.isRequired,
    prompt:PropTypes.string.isRequired,
    error:PropTypes.string.isRequired,
    id:PropTypes.string
}