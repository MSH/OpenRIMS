import React , {Component} from 'react'
import {Container, Row, Col,Alert,Breadcrumb,BreadcrumbItem, FormGroup, Label, Input, Card, CardHeader, CardBody} from 'reactstrap'
import PropTypes from 'prop-types'
import Fetchers from './utils/Fetchers'
import Locales from './utils/Locales'
import FieldOption from './form/FieldOption'
import ButtonUni from './form/ButtonUni'
import CollectorTable from './utils/CollectorTable'
import Pharmadex from './Pharmadex'
import FieldsComparator from './form/FieldsComparator'
import DictNode from './DictNode'
import Navigator from'./utils/Navigator'
import SearchControl from './utils/SearchControl'
import FieldUpload from './form/FieldUpload'

/**
 * Allows:
 * - establish/remove relations to the nodes on a level of a dictionary tree.
 * - add nodes to the level if not "display"
 * @event
 * incoming 
 *  askData             ask data user selected
 *  refreshData         refresh data from properties
 * Outgoing
 *  onGetData in reply to askData
 *  onSelectionChange when any selection occurs
 * 
 * data is DictionaryDTO
 * 
 * @example
 * <Dictionary identifier={this.state.data.dictionaries[i].url} recipien={this.state.identifier} data={this.state.data.dictionaries[i]} display />
 *  />
 *      
 * 23.08.2022 khomenska
 * Add sendMessSave:false - используем, когда по клику на Save нужно еще где то что-то сделать, вне словаря
 */
class Dictionary extends Component{
    constructor(props){
        super(props)
        this.state={
            identifier:Date.now().toString(),
            sendMess:false,
            sendMessSave:false,
            activeNode:0,
            isChecked:false,
            level:-1,
            edit:false,
            data:this.props.data,
            showimport:false,
            thing:{},
            file:{},
            labels:{
                global_add:"",
                global_delete:'',
                global_save:"",
                global_cancel:"",
                next:'',
                search:'',
                selectedonly:'',
                global_import_short:"",
                global_export_short:"",
                upload_file:"",
                headerImport:"",
                saved:""
            }
        }
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
        this.reloadData=this.reloadData.bind(this)
        this.tableLoader=this.tableLoader.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
        this.canAdd=this.canAdd.bind(this)
        this.selectRow=this.selectRow.bind(this)
        this.loadForward=this.loadForward.bind(this)
        this.exportClick=this.exportClick.bind(this)
        this.importRunClick=this.importRunClick.bind(this)
        this.importForm=this.importForm.bind(this)
        this.loadImport=this.loadImport.bind(this)
        this.cleareFileNameLabel=this.cleareFileNameLabel.bind(this)
    }
       /**
     * Return changed style
     * @param {DictionaryDTO} dict 
     */
        static changed(dict){
            if(dict.changed){
                return "markedbycolor"
            }else{
                return ""
            }
        }
    /**
     * Place a dictionary to a thing
     * name - variable name
     * dict - ready to use dictionary
     * index - index for key property
     * ro - this.props.readOnly from the thing
     * recipient - this.state.identifier from the thing
     * label - is resolved label for the variable
     */
    static placeDictionary(name,dict, index, ro, recipient, label){
        if(dict == undefined){
            return []
        }
        ro=ro || dict.readOnly
        if(ro){
            dict.readOnly=true
        }
        let color="info"
        if(dict.strict){
            color="danger"
        }
        dict.varName=name
        return(
            <Row key={index} className={Dictionary.changed(dict)}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{label}</h6>
                        </Col>
                    </Row>
                    <Row hidden={dict.valid}>
                        <Col>
                            <Alert color={color} className="p-0 m-0">
                                <small>{dict.identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Dictionary key={name+index} identifier={name} recipient={recipient} data={dict} display/>
                        </Col>
                    </Row>
                    
                </Col>
            </Row>
        )
    }
    /**
     * listen for askData broadcast and getData only to own address
     */
    eventProcessor(event){
        let data=event.data
        if(data.from == this.props.recipient){
            if(data.subject=="askData"){
                Navigator.message(this.props.identifier,data.from,"onGetData",this.state.data)
            }
            if(data.subject=="refreshData"){
               this.state.data=this.props.data
               this.setState(this.state)
            }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.state.data=this.props.data             //for the first time
        Locales.createLabels(this)
        Locales.resolveLabels(this)
        this.comparator = new FieldsComparator(this)
        this.setState(this.state)
        if(!this.state.data.mult){
            Fetchers.postJSONNoSpinner('/api/common/dictionary/load/next', this.state.data, (query,result)=>{
                //this.state.data=result
                if(result.table.rows.length>0){
                    this.state.hasnext=true
                }else{
                    this.state.hasnext=false
                }
                this.setState(this.state)
            })
        }
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }

    reloadData(){
        Fetchers.postJSONNoSpinner("/api/common/dictionary/load/next",this.state.data,(query,result)=>{
            this.state.data=result
            this.comparator = new FieldsComparator(this)
            this.setState(this.state)
        })
    }

    componentDidUpdate(){
        if(this.props.data.reload){
            this.state.data=this.props.data
            this.state.data.reload=false
            this.reloadData()
        }
        if(this.state.sendMess){
            let to="*"
            if(this.props.recipient != undefined){
                to=this.props.recipient
            }
            Navigator.message(this.props.identifier,to,"onSelectionChange",this.state.data)
            this.state.sendMess=false
        }
        if(this.state.sendMessSave){
            let to="*"
            if(this.props.recipient != undefined){
                to=this.props.recipient
            }
            Navigator.message(this.props.identifier,to,"onSaveData",this.state.data)
            this.state.sendMessSave=false
        }
        if(this.props.data.url != this.state.data.url){
            this.state.data=this.props.data
            this.state.edit=false
            this.state.showimport=false
            this.setState(this.state)
        }
        let fld=this.comparator.checkChanges()
        if(fld.includes("selection")){
            this.reloadData(); 
        }
        this.comparator = new FieldsComparator(this)
    }


    createBreadCrumb(){
        let ret = []
        let className="btn btn-link p-0 border-0"
        if(this.state.data.readOnly|| this.props.readOnly){
            className='d-inline-flex'
        }
        ret.push(
            <BreadcrumbItem key={this.state.data.home}>
                <div hidden={this.state.edit} className={className} style={{fontSize:'0.8rem'}}
                             onClick={()=>{
                            if(this.state.data.readOnly || this.props.readOnly){
                                return
                            }
                           Fetchers.postJSONNoSpinner("/api/common/dictionary/load/root", this.state.data, (query,result)=>{
                            this.state.data=result
                            this.state.hasnext=false
                            this.comparator = new FieldsComparator(this)
                            this.state.sendMess=true
                            this.setState(this.state)
                            //Navigator.message(this.props.identifier,this.props.recipient,"onSelectionChange",this.state.data)
                           })
                        }}>
                    {this.state.data.home}
                </div>
                <div hidden={!this.state.edit}>
                    {this.state.data.home}
                </div>
            </BreadcrumbItem>
        )
        let fields = this.state.data.path
        if(Fetchers.isGoodArray(fields)){
            fields.forEach((field,index)=>{
                if(index<fields.length-1){
                    if(field.code.length>0){
                        ret.push(
                            <BreadcrumbItem  key={index}>
                                <div hidden={this.state.edit} className={className} style={{fontSize:'0.8rem'}}
                                    onClick={()=>{
                                        if(this.state.data.readOnly){
                                            return
                                        }
                                        this.state.data.pathSelected=field   //selection in breadcrumb
                                        Fetchers.postJSONNoSpinner("/api/common/dictionary/load/path", this.state.data, (query,result)=>{
                                            this.state.data=result
                                            this.state.hasnext=false
                                            this.comparator = new FieldsComparator(this)
                                            this.state.sendMess=true
                                            this.setState(this.state)
                                            //Navigator.message(this.props.identifier,this.props.recipient,"onSelectionChange",this.state.data)
                                        })
                                }}>
                                    {field.code}
                                </div>
                                <span hidden={!this.state.edit}>
                                    {field.code}
                                </span>
                            </BreadcrumbItem>
                        )
                    }
                }else{
                    ret.push(
                        <BreadcrumbItem  key={index}>
                            <span>
                                {field.code}
                            </span>
                        </BreadcrumbItem>
                    )
                }
            })
        }
        
        let sumSelected=this.state.data.prevSelected.reduce((accum,element)=>{
            if(element>0){
                return accum+1
            }else{
                return accum
            }
        },0)
        /*let hasnext = this.state.hasnext//(this.props.display && sumSelected==1) || (!this.props.display && sumSelected>0)
        if(hasnext && !this.state.data.readOnly && !this.state.data.mult){
            ret.push(
                <BreadcrumbItem hidden={this.state.edit} key='next'>
                    <div className={className} style={{fontSize:'0.8rem'}}
                                onClick={()=>{
                                    if(this.state.data.readOnly){
                                        return
                                    }
                                    let varName=this.state.data.varName
                                    Fetchers.postJSONNoSpinner("/api/common/dictionary/load/next", this.state.data, (query,result)=>{
                                        this.state.data=result
                                        this.state.hasnext=false
                                        this.state.data.varName=varName
                                        this.comparator = new FieldsComparator(this)
                                        this.setState(this.state)
                                       })
                            }}>
                        <b>{this.state.labels.next}</b>
                    </div>
                </BreadcrumbItem>
            )
        }*/
        return ret
    }
    /**
     * Reload table only
     */
    tableLoader(){
        Fetchers.postJSONNoSpinner("/api/common/dictionary/load/table", this.state.data, (query, result)=>{
            this.state.data=result
            this.comparator = new FieldsComparator(this)
            this.setState(this.state)
        })
    }
    /**
     * Can user add new items?
     */
    canAdd(){
        return(
            !this.props.display && !this.state.data.system &&(
            this.state.data.table.rows.length>0
            || this.state.data.selection.value.id>0
            || this.state.data.path.length==0
            )

        )
    }
    /**
     * Select a row in the table
     * @param {number} rowNumber 
     */
    selectRow(rowNumber){
        let row = this.state.data.table.rows[rowNumber]
        row.selected=!row.selected
        let index = this.state.data.prevSelected.indexOf(row.dbID)
        if(row.selected){
         if(index==-1){
             this.state.data.prevSelected.push(row.dbID)
         } 
         if((!this.state.data.mult) || (this.state.data.path.length!=this.state.data.maxDepth)){
             //rest of selected should be de-selected if one
             this.state.data.table.rows.forEach((r)=>{
                 if(r.selected && r.dbID != row.dbID){
                    let ind = this.state.data.prevSelected.indexOf(r.dbID)
                    this.state.data.prevSelected[ind]=0
                    r.selected=false
                 }
             })
         }  
        }else{  //deselected
            if(index!=-1){
                this.state.data.prevSelected[index]=0
            }
        }
        this.setState(this.state)
    }

    loadForward(){
        //if(!this.state.data.mult){
            let varName=this.state.data.varName//
            Fetchers.postJSON('/api/common/dictionary/load/next', this.state.data, (query,result)=>{
                if(result.table.rows.length > 0 || !this.props.display){
                    this.state.hasnext=true
                    this.state.data=result//
                    this.state.data.varName=varName//
                    this.state.file = {}
                    //document.getElementById("fileinputidinactivity").value = "";
                    this.state.showimport=false
                    this.comparator = new FieldsComparator(this)//
                }else{
                    this.state.hasnext=false
                }
                this.state.sendMess=true
                this.setState(this.state)
            })
        //}
    }

    exportClick(){
        var api = '/api/admin/dictionary/export/dicturl=' + this.state.data.url
        if(this.state.data.urlId == 0){
            api += '&curid=' + this.state.data.path[this.state.data.path.length - 1].id
        }else{
            api += '&curid=' + this.state.data.urlId
        }
        window.open(api, "_blank").focus()
    }

    importRunClick(){
        if(this.state.file != null && this.state.file.name != undefined){
            let formData = new FormData()
            formData.append('dict', JSON.stringify(this.state.data))
            formData.append('file', this.state.file);
            Fetchers.postFormJson('/api/admin/dictionary/import/run', formData, (formData,result)=>{
                this.state.file = {}
                this.cleareFileNameLabel()
                if(result.valid){
                    this.state.showimport=false
                    this.tableLoader()
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.saved, color:'success'})
                }else{
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:result.identifier, color:'danger'})
                    window.open('/api/admin/dictionary/import/errorfile', "_blank").focus()
                    this.state.showimport=false
                    this.tableLoader()
                }
            })
        }else{
            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.upload_file, color:'danger'})
        }
    }

    loadImport(){
        Fetchers.postJSON("/api/admin/dictionary/import/load", this.state.thing, (query,result)=>{
            this.state.thing=result
            this.state.showimport=true;
            this.setState(this.state)
        })
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
            }
        }
        return ret;
    }

    cleareFileNameLabel(){
        var lbl = document.getElementsByClassName("custom-file-label");
        if(lbl != null && lbl.length > 0){
            lbl[0].textContent=this.state.labels.upload_file
        }
        document.getElementById("fileinputidinactivity").value=""
    }

    importForm(){
        if(this.state.thing != undefined && this.state.thing.documents != undefined){
            return (
                <Card style={{width:"100%"}} >
                    <CardHeader >
                        <b>{this.state.labels.headerImport}</b>
                    </CardHeader>
                    <CardBody>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                <b>{this.state.labels.upload_file}</b>
                            </Col>
                        </Row>
                        <Row>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                <FieldUpload onChange={(file)=>{
                                                this.state.file=file
                                                var lbl = document.getElementsByClassName("custom-file-label");
                                                if(lbl != null && lbl.length > 0){
                                                    lbl[0].textContent=file.name
                                                }
                                                this.setState(this.state)
                                            }}
                                            accept={".xlsx"}
                                            prompt={this.state.labels.upload_file}
                                            error={this.fileError()}                            
                                        />
                            </Col>
                        </Row>
                        <Row>
                            <Col xs='12' sm='12' lg='4' xl='4' >
                            </Col>
                            <Col xs='12' sm='12' lg='4' xl='4' className="d-flex justify-content-end">
                                    <ButtonUni
                                        label={this.state.labels.global_save}
                                        onClick={()=>{
                                            this.importRunClick()
                                        }}
                                        color="success"
                                    />
                            </Col>
                            <Col xs='12' sm='12' lg='4' xl='4' className="d-flex justify-content-end">
                                    <ButtonUni
                                        label={this.state.labels.global_cancel}
                                        onClick={()=>{
                                            this.state.file = {}
                                            this.cleareFileNameLabel()
                                            this.state.showimport = false
                                            this.setState(this.state)
                                        }}
                                        color="info"
                                    />
                            </Col>
                        </Row>
                        </CardBody>
                </Card>
            )
        }
        return []
    }

    render(){
       let myStyle=Pharmadex.settings.activeBorder
       if(this.props.noborder){
        myStyle=''
       }
       if(this.state.data.table==undefined){
           return []
       }
       if(this.state.labels.locale==undefined){
           return []
       }
       if(this.state.edit){
           return(
            <Container fluid className={myStyle}>
                <Row>
                    <Col>
                        <Breadcrumb>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                </Row>

                <Row>
                   <Col>
                        <DictNode identifier={this.props.identifier+".node"}
                                    nodeId={this.state.activeNode}
                                    parentId={this.state.data.selection.value.id}
                                    url={this.state.data.url}          
                                    onCancel={()=>{this.state.edit=false
                                                    this.state.sendMessSave = true
                                                   this.setState(this.state)
                                                   this.tableLoader()}
                                    }
                        />
                   </Col>
                </Row>
            </Container>
           )
       }else{
		   let hideControls = (this.state.data.table.rows==0 || this.state.data.table.headers.pages<2) && 
                            !this.state.data.selectedOnly && !this.state.data.table.headers.filtered
        return(
            <Container fluid className={myStyle}>
                <Row>
                    <Col>
                        <Breadcrumb xs='12' sm='12' lg='10' xl='10'>
                            {this.createBreadCrumb()}
                        </Breadcrumb>
                    </Col>
                   
                </Row>
                <Row >
                    <Col xs='12' sm='12' lg='12' xl='12' >
                        <div hidden={this.state.data.selection.value.options.length<=1}>
                            <FieldOption attribute="selection" component={this} />
                        </div>
                    </Col>
                </Row>
                <Row hidden={this.state.data.readOnly}>
                    <Col xs='12' sm='12' lg='12' xl='4'>
                        <div hidden={hideControls}>
                        <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.tableLoader}/>
                        </div>
                        <div hidden={!this.canAdd()}>
                            <ButtonUni
                                onClick={()=>{
                                    this.exportClick()
                                }}
                                label={this.state.labels.global_export_short}
                                color="success"
                            />
                        </div>
                    </Col>
                    <Col xs='12' sm='12' lg='12' xl='4'>
                        <FormGroup hidden={hideControls} check className="form-control-sm">
                            <Label check>
                            <Input 
                                type="checkbox"
                                value={this.state.data.selectedOnly}
                                checked={this.state.data.selectedOnly}
                                onChange={()=>{
                                    this.state.data.selectedOnly=!this.state.data.selectedOnly
                                    this.setState(this.state)
                                    this.tableLoader()
                                }} 
                                />
                                {this.state.labels.selectedonly}
                            </Label>
                        </FormGroup>
                        <div hidden={!this.canAdd()}>
                            <ButtonUni
                                onClick={()=>{
                                    this.loadImport()
                                }}
                                label={this.state.labels.global_import_short}
                                color="info"
                            />
                        </div>
                    </Col>
                    <Col xs='12' sm='12' lg='12' xl='4' className="pb-2" >
                        <div hidden={!this.canAdd()}>
                            <ButtonUni
                                onClick={()=>{
                                    this.state.activeNode=0;
                                    this.state.edit=true
                                    this.setState(this.state)
                                }}
                                label={this.state.labels.global_add}
                                color="primary"
                            />
                        </div>
                    </Col>
                </Row>
                <Row hidden={!this.canAdd()}>
                    <Col xs='12' sm='12' lg='12' xl='12' >
                        <Row className="pb-1" hidden={!this.state.showimport}>
                            {this.importForm()}
                        </Row>
                    </Col>
                </Row>
                <Row className="pb-1">
                    <Col>
                        <CollectorTable
                            tableData={this.state.data.table}
                            loader={this.tableLoader}
                            linkProcessor={(rowNo,cellNo)=>{
                                if(this.state.data.readOnly || this.props.readOnly){
                                    return
                                }
                                if(this.props.display){
                                    this.selectRow(rowNo)
                                    this.loadForward()
                                }else{
                                    this.state.activeNode=this.state.data.table.rows[rowNo].dbID;
                                    this.state.edit=true
                                    this.state.sendMess=true
                                    this.setState(this.state)
                                }
                               
                            }}
                            selectRow={(rowNo)=>{
                                if(this.state.data.readOnly || this.props.readOnly){
                                    return
                                }
                                this.selectRow(rowNo)
                                this.loadForward()
                            }}
                            headBackground={Pharmadex.settings.tableHeaderBackground}
                            styleCorrector={(header)=>{
                                if(header=='pref'){
                                    return {width:'30%'}
                                }
                            }}
                        />
                    </Col>
                </Row>
            </Container>
            )
            }
        }   
    }

export default Dictionary
Dictionary.propTypes={
    identifier:PropTypes.string.isRequired,     //for messages
    data:PropTypes.object.isRequired,           //DictionaryDTO
    recipient:PropTypes.string,                 //recipient of messages
    display:PropTypes.bool,                     //display only
    noborder:PropTypes.bool,                    //will not display a border
}