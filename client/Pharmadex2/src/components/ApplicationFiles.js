import React , {Component} from 'react'
import {Container,Row, Col, Card, CardBody, CardHeader, Alert} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import CollectorTable from './utils/CollectorTable'
import FieldUpload from './form/FieldUpload'
import Pharmadex from './Pharmadex'
import ButtonUni from './form/ButtonUni'
import Downloader from './utils/Downloader'
import Navigator from './utils/Navigator'
import Alerts from './utils/Alerts'

/**
 * A set of files for an application
 * @example
 *     <ApplicationFiles data={files}
                        recipient={this.state.identifier}
                        readOnly={this.state.data.readOnly || this.props.readOnly}/>
 */
class ApplicationFiles extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),  //my address for messages
            data:this.props.data,               //ThingDTO   
            labels:{
                upload_file:"",
                newfile:"",
                global_download:"",
                global_save:"",
                global_cancel:"",
                saved:"",
                error_filesize:""
            },           
            file:{}
        }
        this.tableLoader=this.tableLoader.bind(this)
        this.load=this.load.bind(this)
        this.save=this.save.bind(this)
        this.download=this.download.bind(this)
        this.component=this.component.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
        this.hiddenSaveBtn=this.hiddenSaveBtn.bind(this)
        this.hiddenDownloadBtn=this.hiddenDownloadBtn.bind(this)
    }

    /**
     * Return changed style
     * @param {FileDTO} addr 
     */
    static changed(files){
        if(files.changed){
            return "markedbycolor"
        }else{
            return ""
        }
    }

    /**
     * Place this component to ThingDTO
     * @param {FileDTO} files 
     * @param {number} index 
     * @param {boolean} readOnly 
     * @param {string} identifier 
     * @param {string} label 
     * @returns 
     */
    static place(files,index,readOnly,identifier,label){
        if(files!=undefined){
            if(readOnly){
                files.readOnly=true
            }
            files.reload=true
            let color="info"
            if(files.strict){
                color="danger"
            }
            return(
            <Row key={index} className={ApplicationFiles.changed(files)}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{label}</h6>
                        </Col>
                    </Row>
                    <Row hidden={files.valid}>
                        <Col>
                            <Alert color={color} className="p-0 m-0">
                                <small>{files.identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ApplicationFiles data={files} key={identifier+index}
                                            recipient={identifier}
                                            readOnly={readOnly}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
    }

    /**
     * Listen messages from other components. Only to my address
     * @param {Window Event} event 
     */
    eventProcessor(event){
        let data=event.data
        if(data.to==this.state.identifier){
            
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        Locales.resolveLabels(this)
    }
    componentDidUpdate(){
        if(this.props.data.reload){
            delete this.props.data.reload
            this.state.data=this.props.data
            this.setState(this.state.data)
        }
        if(this.props.data.thingNodeId!=this.state.data.thingNodeId){
            this.state.data=this.props.data
            this.tableLoader()
        }
    }
    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    tableLoader(){
        this.state.data.readOnly=this.props.readOnly
        Fetchers.postJSONNoSpinner('/api/'+Navigator.tabSetName() +'/thing/files', this.state.data,(query,result)=>{
            Fetchers.setJustLoaded(result,false)
            this.state.data=result;
            Navigator.message(this.state.identifier,this.props.recipient, "onSelectionChange", this.state.data)
            this.setState(this.state)
        })
    }

    load(){
        Fetchers.postJSONNoSpinner('/api/'+Navigator.tabSetName() +'/thing/file/load', this.state.data,(query,result)=>{
            Fetchers.setJustLoaded(result,false)
            this.state.data=result;
            Navigator.message(this.state.identifier,this.props.recipient, "onSelectionChange", this.state.data)
            this.setState(this.state)
        })
    }

    save(){
        /** 
        additional verification
        suddenly the button SAVE visibility formula does not work correctly
        */
        if(this.state.file != undefined && this.state.file.size < this.state.data.maxFileSize){
            let formData = new FormData()
            formData.append('dto', JSON.stringify(this.state.data))
            formData.append('file', this.state.file);
            Fetchers.postFormJson('/api/'+Navigator.tabSetName() +'/thing/file/save', formData, (formData,result)=>{
                if(result.valid){
                    this.state.data = result;
                    this.state.data.fileName=''
                    this.state.data.editor = false
                    this.state.file = {}
                    this.tableLoader()
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.saved, color:'success'})
                }else{
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                }
            })
        }else{
            Alerts.show(this.state.labels.error_filesize, 3)
        }
        
    }

    download(){
        if(this.state.data.nodeId>0){
            let downloader = new Downloader()
            //downloader.pureGetDownload('/api/guest/application/file/download/id='+this.state.data.nodeId)
            window.open('/api/'+Navigator.tabSetName() +'/application/file/download/id='+this.state.data.nodeId, "_blank")
        }
    }

    /**
     * Error message for a file
     */
    fileError(){
        let fileName = this.state.data.fileName
        let ret=""
        if(fileName !== undefined){
            if(fileName.error){
                ret = fileName.suggest
                return ret
            }
        }
        if(this.state.file !== undefined){
            if(this.state.file.size >= this.state.data.maxFileSize){
                var max = this.state.data.maxFileSize / 1048576
                return this.state.labels.error_filesize + " " + max + " MB"
            }
        }
        return ret;
    }

    hiddenSaveBtn(){
        if(this.state.data.readOnly){
            return true
        }else{//this.state.data.nodeId==0 && 
            var h = (this.state.file.name == undefined) || (this.state.file.size >= this.state.data.maxFileSize)
            return h
        }
    }

    hiddenDownloadBtn(){
        //this.state.data.fileName.length==0
        if(this.state.data.fileName != undefined && this.state.data.fileName.length > 0){
            if(this.state.file.name == undefined){
                return false
            }else if(this.state.data.fileName != this.state.file.name){
                return true
            }
        }else
            return true
    }

    component(){
        if(this.state.data.editor){
            let header=this.state.labels.newfile
            if(this.state.data.fileName.length>0){
                header=this.state.data.fileName
            }
            return (
                <Card>
                    <CardHeader className="p-0 m-0">
                        <b>{header}</b>
                    </CardHeader>
                    <CardBody>
                        <Row className="mb-1">
                            <Col>
                                <div className="text-muted">{this.state.data.fileDescription}</div>
                            </Col>
                        </Row>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                <b>{this.state.labels.upload_file}</b>
                            </Col>
                        </Row>
                        <Row hidden={this.state.data.readOnly}>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                <FieldUpload onChange={(file)=>{
                                                this.state.file=file
                                                this.setState(this.state)
                                            }}
                                            accept={this.state.data.accept}
                                            prompt={this.state.labels.upload_file}
                                            error={this.fileError()}                            
                                    />
                            </Col>
                        </Row>
                        <Row>
                            <Col xs='12' sm='12' lg='4' xl='4' >
                                    <ButtonUni
                                        hidden={this.hiddenDownloadBtn()}
                                        label={this.state.labels.global_download}
                                        onClick={()=>{
                                            this.download()
                                        }}
                                    />
                            </Col>
                            <Col xs='12' sm='12' lg='4' xl='4' className="d-flex justify-content-end">
                                    <ButtonUni
                                        hidden={this.hiddenSaveBtn()}
                                        label={this.state.labels.global_save}
                                        onClick={()=>{
                                            this.state.data.editor = false
                                            this.setState(this.state)
                                            this.save()
                                        }}
                                        color="success"
                                    />
                            </Col>
                            <Col xs='12' sm='12' lg='4' xl='4' className="d-flex justify-content-end">
                                    <ButtonUni
                                        label={this.state.labels.global_cancel}
                                        onClick={()=>{
                                            this.state.data.editor=false
                                            this.state.file = {}
                                            this.setState(this.state)
                                        }}
                                        color="info"
                                    />
                            </Col>
                        </Row>
                        </CardBody>
                </Card>
            )
        }else{
            return (
                <CollectorTable
                                tableData={this.state.data.table}
                                loader={this.tableLoader}
                                linkProcessor={(rowNo,cellNo)=>{
                                        if(this.props.readOnly){
                                            this.state.data.dictNodeId=this.state.data.table.rows[rowNo].dbID
                                            Fetchers.postJSONNoSpinner('/api/'+Navigator.tabSetName() +'/thing/file/load', this.state.data,(query,result)=>{
                                                Fetchers.setJustLoaded(result,false)
                                                this.state.data=result;
                                                this.download()
                                                this.setState(this.state)
                                            })
                                        }else{
                                            this.state.data.editor=true
                                            this.state.data.dictNodeId=this.state.data.table.rows[rowNo].dbID
                                            this.load()
                                        }
                                    }
                                }
                                headBackground={Pharmadex.settings.tableHeaderBackground}
                                styleCorrector={(header)=>{
                                    if(header=='description'){
                                        return {width:'50%'}
                                    }
                                    if(header=='lastupdate'){
                                        return {width:'20%'}
                                    }
                                }}
                            />
            )
        }
    }

    /**
     * Place this component to ThingDTO
     * @param {FileDTO} file
     * @param {number} index 
     * @param {boolean} readOnly 
     * @param {string} identifier 
     * @param {string} label 
     * @returns 
     */
    static onlyform(files,index,readOnly,identifier,label){
        this.state.data = files
        this.state.data.editor = true
        return place(files,index,readOnly,identifier,label)
    }

    render(){
        if(this.state.data.table==undefined){
            return []
        }
        return (
            <Container fluid className={Pharmadex.settings.activeBorder}>
                <Row>
                    <Col></Col>
                </Row>
                <Row className="pb-1">
                    <Col>
                        {this.component()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default ApplicationFiles
ApplicationFiles.propTypes={
        data:PropTypes.object.isRequired,           //FileDTO
        recipient:PropTypes.string.isRequired,      //recipient for messages
        readOnly:PropTypes.bool                             //only download
}