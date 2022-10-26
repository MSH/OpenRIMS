import React , {Component} from 'react'
import {Container, Row, Col,Alert,Breadcrumb,BreadcrumbItem, FormGroup, Label, Input, Nav} from 'reactstrap'
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
            sendMess:false,
            sendMessSave:false,
            activeNode:0,
            isChecked:false,
            level:-1,
            edit:false,
            data:this.props.data,
            labels:{
                global_add:"",
                global_delete:'',
                next:'',
                search:'',
                selectedonly:'',
            }
        }
        this.createBreadCrumb=this.createBreadCrumb.bind(this)
        this.reloadData=this.reloadData.bind(this)
        this.tableLoader=this.tableLoader.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
        this.canAdd=this.canAdd.bind(this)
        this.selectRow=this.selectRow.bind(this)
        this.loadForward=this.loadForward.bind(this)
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
                    this.comparator = new FieldsComparator(this)//
                }else{
                    this.state.hasnext=false
                }
                this.state.sendMess=true
                this.setState(this.state)
            })
        //}
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
                    <Col xs='12' sm='12' lg='5' xl='5'>
                        <div hidden={hideControls}>
                        <SearchControl label={this.state.labels.search} table={this.state.data.table} loader={this.tableLoader}/>
                        </div>
                    </Col>
                    <Col xs='12' sm='12' lg='4' xl='4'>
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
                    </Col>
                    <Col xs='12' sm='12' lg='3' xl='3' className="pb-2" >
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