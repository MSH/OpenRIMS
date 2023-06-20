import React , {Component} from 'react'
import {Container,Row,Col,Alert, Button} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import FieldsComparator from './form/FieldsComparator'
import Navigator from './utils/Navigator'
import ViewEdit from './form/ViewEdit'
import ViewEditOption from './form/ViewEditOption'
import ViewEditDate from './form/ViewEditDate'
import Dictionary from './Dictionary'
import ApplicationFiles from './ApplicationFiles'
import AddressForm from './AddressForm'
import Persons from './Persons'
import ResourcesUsage from './ResourcesUsage'
import Scheduler from './Scheduler'
import Register from './Register'
import ATCCodes from './ATCCodes'
import LegacyData from './LegacyData'
import Interval from './Interval'
import Links from './Links'
import Pharmadex from './Pharmadex'


/**
 * Responsible for a ThingDTO
 * thingUpdated event for each update!
 * smart auto fill is included
 * @example
 * <Thing key='activity'
            data={thingDTO}
            recipient={this.state.identifier}
            readOnly={this.props.readOnly}
            narrow={this.props.narrow}
            noload                          //no load this thing from the database
            />
 */
class Thing extends Component{
    constructor(props){
        super(props)
        this.autoFillKey=undefined              //prefLabel strings, literals none of above
        this.comparator=undefined               //for auto fill too
        this.state={
            repaint:this.props.data.repaint,
            identifier:Date.now().toString(),  //my address for messages
            data:this.props.data,             //ThingDTO         
            labels:{
                save_app_error:'',
                success:''
            },
        }
        this.state.data.readOnly=this.props.readOnly
        
        this.eventProcessor=this.eventProcessor.bind(this)
        this.paintRows=this.paintRows.bind(this)
        this.paintCells=this.paintCells.bind(this)
        this.paintCell=this.paintCell.bind(this)
        this.heading=this.heading.bind(this)
        this.strings=this.strings.bind(this)
        this.literal=this.literal.bind(this)
        this.dateFiled=this.dateFiled.bind(this)
        this.load=this.load.bind(this)
        this.save=this.save.bind(this)
        this.prefLabel=this.prefLabel.bind(this)
        this.storeLocal=this.storeLocal.bind(this)
        this.autoFillAll=this.autoFillAll.bind(this)
        this.autoFillFormField=this.autoFillFormField.bind(this)
        this.numberFiled=this.numberFiled.bind(this)
        this.logicalFiled=this.logicalFiled.bind(this)
        this.saveGuest=this.saveGuest.bind(this)
        this.postLoaded=this.postLoaded.bind(this)
        this.helpButton=this.helpButton.bind(this)
        this.reloadComponents=this.reloadComponents.bind(this)
        this.droplistFiled=this.droplistFiled.bind(this)
    }
    /**
     * GEt prefLabel
     * @returns Extract prefLabel. Returns undefined in case no prefLabel
     */
    prefLabel(){
        let key=undefined
        if(this.state.data != undefined && this.state.data.literals != undefined){
            if(this.state.data.literals.prefLabel!=undefined){
                key=this.state.data.literals.prefLabel.value
            }
        }
        if(key==undefined && this.state.data.strings != undefined){
            if(this.state.data.strings.prefLabel != undefined){
                key=this.state.data.strings.prefLabel.value
            }
        }
        return key
    }
    /**
     * Common actions after the save
     * Currently only for auto fill feature
     */
    storeLocal(){
        let key=this.prefLabel();
        Fetchers.storeThing(this.state.data, key)
    }

    /**
     * Guest Applications should be saved by own way
     * savedByAction means saved by Save button
     */
         saveGuest(savedByAction){
            Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/thing/save/application",this.state.data,(query,result)=>{
                this.state.data=result
                if(this.state.data.valid){
                    this.storeLocal()
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.labels.success, color:'success'})
                }else{
                    let err_mess=this.state.labels.save_app_error
                    if(this.state.data.identifier){
                        err_mess=this.state.data.identifier
                    }
                    Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:err_mess, color:'danger'})
                    this.setState(this.state)
                }
                if(savedByAction){
                    Navigator.message(this.state.identifier, this.props.recipient, "savedByAction",this.state.data)
                }else{
                    Navigator.message(this.state.identifier, this.props.recipient, "saved", this.state.data)
                }
            })
        }
    /**
     * ask all components for data, then save
     * savedByAction means saved by Save button
     */
    save(savedByAction){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/thing/save",this.state.data,(query,result)=>{
            this.state.data=result
            if(this.state.data.valid){
                this.storeLocal()
                if(savedByAction){
                    Navigator.message(this.state.identifier, this.props.recipient, "savedByAction",this.state.data)
                }else{
                    Navigator.message(this.state.identifier, this.props.recipient, "saved", this.state.data)
                }
            }else{
                Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                this.setState(this.state)
            }
            
        })
    }


    /**
     * Listen messages from other components. Only to my address
     * @param {Window Event} event 
     */
    eventProcessor(event){
        if(this.props.readOnly){
            return
        }
        let data=event.data
        if(data.from==this.props.recipient){
            if(data.subject=='validateThing'){
                Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/thing/validate", this.state.data, (query,result)=>{
                    this.state.data=result
                    this.setState(this.state)
                    Navigator.message(this.state.identifier, this.props.recipient, "thingValidated", result)
                    if(!this.state.data.valid){

                    }
                })
            }
            if(data.subject=='thingReload'){
                if(this.props.noload){
                    this.state.data=data.data
                    this.setState(this.state)
                }else{
                    this.load()
                }
            }
            if(data.subject=='saveResource'){
                Fetchers.postJSON("/api/admin/resource/save", this.state.data, (query,result)=>{
                    this.state.data=result
                    if(this.state.data.valid){
                        Navigator.message(this.state.identifier, this.props.recipient, "resourceSaved", this.state.data)
                    }else{
                        this.setState(this.state)
                    }
                })
            }
        }
        if(data.from==this.props.recipient && !this.state.data.readOnly && !this.props.readOnly){
            if(data.subject=="saveAll"){
                this.save(true);
            }
        }

        if(data.from==this.props.recipient && !this.state.data.readOnly && !this.props.readOnly){
            if(data.subject=="saveAllGuest" || data.subject=="saveAllUsersData"){                       //file new data under the user
                this.saveGuest(true);
            }
        }
        if(data.to==this.state.identifier){
            if(data.subject=='changeNodeId'){
               this.state.data.nodeId=data.data
               this.load()
               return
            }
            if(data.subject=='onSelectionChange'){
                let name=data.data.varName
                this.takeComponent(name,0,data.data)
                if(this.state.data.dictionaries.hasOwnProperty(name)){
                    this.reloadComponents();
                }
                this.setState(this.state)
                Navigator.message(this.state.identifier, this.props.recipient, "thingUpdated", this.state.data)
            }
           if(data.subject=='runAction'){
               switch(data.data){
                   case 'save_action':
                       this.save(true);
               }
           }
           if(data.subject=='save'){
               this.save()
           }
           if(data.subject=='saveGuest' || data.subject=="saveUsersData"){
            this.saveGuest()
            }
           //ask to apply auxiliary path. Only for guest applications
           if(data.subject=='auxPath'){
            this.state.data.auxPathVar=data.data.varName
            this.state.data.strict=false
            Fetchers.postJSON("/api/guest/thing/save/application",this.state.data,(query,result)=>{
                this.state.data=result
                if(result.valid){
                    Fetchers.postJSON("/api/guest/activity/auxpath", this.state.data, (query,result)=>{
                        this.state.data=result
                        if(result.valid){
                            Navigator.message(this.state.identifier, this.props.recipient, "auxPath", this.state.data)
                        }else{
                            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                            this.setState(this.state)
                        }
                    })
                }else{
                    this.setState(this.state)
                }
            })
               
           }
        }
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.state.data.repaint=false
        this.load()
    }
    /**
     * Re-load components that should be reloaded after onSelectionChange
     */
    reloadComponents(){
        Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/thing/refresh", this.state.data, (query,result)=>{
            this.state.data=result
            this.setState(this.state)
        })
    }

    /**
     * Fill out a filed in the form
     * @example
     * autoFillFor
     */
    autoFillFormField(fields, key, value){
        if(fields && key){
            if(fields[key]){
                fields[key].value=value
            }
        }
    }
    /**
     * Auto fill strings, litrals, numbers, dates, dictionaries
     * @param {ThingDTO} data 
     */
    autoFillAll(data){
        if(data.strings != undefined && this.state.data.strings != undefined){
            let keys = Object.keys(data.strings)
            keys.forEach(key => {
               this.autoFillFormField(this.state.data.strings, key,data.strings[key].value)
            });
        }
        if(data.literals!=undefined && this.state.data.literals != undefined){
            let keys = Object.keys(data.literals)
            keys.forEach(key => {
                this.autoFillFormField(this.state.data.literals, key,data.literals[key].value)
             });
        }
        if(data.dates!=undefined && this.state.data.dates != undefined){
            let keys = Object.keys(data.dates)
            keys.forEach(key => {
                this.autoFillFormField(this.state.data.dates, key,data.dates[key].value)
             });
        }
        if(data.numbers!=undefined && this.state.data.numbers != undefined){
            let keys = Object.keys(data.numbers)
            keys.forEach(key => {
                this.autoFillFormField(this.state.data.numbers, key,data.numbers[key].value)
            });
        }
        if(data.dictionaries!=undefined && this.state.data.dictionaries != undefined){
            let keys = Object.keys(data.dictionaries)
            keys.forEach(key => {
                let dict=this.state.data.dictionaries[key]
                if(dict){
                    this.state.data.dictionaries[key]=data.dictionaries[key]
                    this.state.data.dictionaries[key].reload=true
                }
            })
        }
        if(data.addresses!=undefined && this.state.data.addresses != undefined){
            let keys = Object.keys(data.addresses)
            keys.forEach(key => {
                let addr=this.state.data.addresses[key]
                if(addr){
                    data.addresses.nodeId=0                             //it will be a new address, not linked one
                    this.state.data.addresses[key]=data.addresses[key]
                    this.state.data.addresses[key].reload=true
                }
            })
        }
        this.setState(this.state.data)
    }

    componentDidUpdate(){
        if(this.comparator!= undefined && this.state.data.nodeId==0){
            let changed = this.comparator.checkChanges()
            this.comparator= new FieldsComparator(this,this.autoFillKey)
            if( Fetchers.isGoodArray(changed)){
                changed.forEach((element)=>{
                    if(element=='prefLabel'){
                        let data=Fetchers.readThing(this.prefLabel())
                        if(data != undefined){
                            this.comparator=undefined           //the latch!
                            this.autoFillAll(data);
                        }
                    }
                })
            }
        }
        Navigator.message(this.state.identifier, this.props.recipient, "thingUpdated", this.state.data)
        this.state.data.repaint=this.state.data.nodeId != this.props.data.nodeId
        if(this.state.data.repaint){
            this.state.data=this.props.data
            this.state.data.repaint=false
            this.load()
        }
    }

    load(){
        if(this.props.noload){
            this.postLoaded()
            
        }else{
        this.state.data.readOnly=this.props.readOnly
            Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/thing/load", this.state.data, (query,result)=>{
                Fetchers.setJustLoaded(result,false)
                this.state.data=result;
                this.setState(this.state);
                this.postLoaded()
            })
        }
    }
    /**
     * Common action after thing has been loaded or accepted
     */
    postLoaded(){
        let err_mess=this.state.data.identifier
        this.state.data.identifier=this.state.identifier
        Navigator.message(this.state.identifier, "*","refreshData",this.state.data);
        Locales.createLabels(this, "strings")
        Locales.createLabels(this, "literals")
        Locales.createLabels(this, "dates")
        Locales.createLabels(this, "numbers")
        Locales.createLabels(this,"logical")
        Locales.createLabels(this,"dictionaries")
        Locales.createLabels(this,"documents")
        Locales.createLabels(this,"resources")
        Locales.createLabels(this,"addresses")
        Locales.createLabels(this,"persons")
        Locales.createLabels(this,"schedulers")
        Locales.createLabels(this,'files')
        Locales.createLabels(this,'registers')
        Locales.createLabels(this,'atc')
        Locales.createLabels(this, 'legacy');
        Locales.createLabels(this, 'intervals');
        Locales.createLabels(this,"links");
        Locales.createLabels(this,"droplist")
        if(this.state.data.activityName != undefined){
            this.state.labels[this.state.data.activityName]=''     //for activity names
        }
        Locales.resolveLabels(this)
        //set field comparator for auto-fill feature
         this.autoFillKey=undefined
         if(this.state.data.nodeId==0){
            if(this.state.data && this.state.data.literals){
                if(this.state.data.literals.prefLabel){
                    this.autoFillKey="literals"
                }
            }
            if(!this.autoFillKey && this.state.data.strings){
                if(this.state.data.strings.prefLabel){
                    this.autoFillKey="strings"
                }
            }
            if(this.autoFillKey){
                this.comparator= new FieldsComparator(this,this.autoFillKey);
            }
        }else{
            this.comparator=undefined       //the latch!
        }
        Navigator.message(this.state.identifier, this.props.recipient,"thingLoaded",this.state.data);
        if(!this.state.data.valid){
            Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:err_mess, color:'danger'})
        }
    }
    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * Heading or link
     * @param {name of heading} name 
     * @param {number to create a key} index 
     */
    heading(name,index){
        let head = this.state.data.heading[name]
        if(head != undefined){
            if(head.url==undefined || head.url.length==0){
                return(
                    <h4 key={index}>{head.value}</h4>
                )
            }else{
                return (
                    <Button key={index} color="link" size='sm'
                    onClick={()=>{
                        if(head.url.includes('shablon')){
                            window.open(head.url)
                        }else{
                            window.open(head.url,'_blank').focus()
                        }
                        
                    }}>
                        {head.value}
                    </Button>
                )
            }
        }
    }

    /**
     * Place a literal in a row 
     * @param {name (label of a literal)} name 
     * @param {number to create a key} index 
     */
    strings(name, index){
        let fieldDTO = this.state.data.strings[name]
        if(fieldDTO != undefined){
            let mode="text"
            if(fieldDTO.textArea){
                mode="textarea"
            }
            let readOnly=this.props.readOnly || fieldDTO.readOnly
            let mark=""
            if(fieldDTO.mark){
                mark= "markedbycolor"
            }
            return(
                <Row key={index} className={mark}>
                    <Col>
                        <ViewEdit edit={!readOnly} mode={mode} attribute={name}
                        component={this} 
                        data={this.state.data.strings}
                        rows="6"
                        hideEmpty
                        />
                    </Col>
                </Row>
            )
        }else{
            return[]
        }
    }

    /**
     * Place a literal in a row 
     * @param {name (label of a literal)} name 
     * @param {number to create a key} index 
     */
     literal(name, index){
        let fieldDTO = this.state.data.literals[name]
        if(fieldDTO != undefined){
            let mode="text"
            if(fieldDTO.textArea){
                mode="textarea"
            }
            let readOnly=this.props.readOnly || fieldDTO.readOnly
            let mark=""
            if(fieldDTO.mark){
                mark= "markedbycolor"
            }
            return(
                <Row key={index} className={mark}>
                    <Col>
                        <ViewEdit edit={!readOnly} mode={mode} attribute={name}
                        component={this} 
                        data={this.state.data.literals}
                        rows="6"
                        hideEmpty
                        />
                    </Col>
                </Row>
            )
        }else{
            return[]
        }
    }
    /**
     * Responsible for dates
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
    dateFiled(name, index){
        let fieldDTO = this.state.data.dates[name]
        if(fieldDTO != undefined){
            let readOnly=this.props.readOnly || fieldDTO.readOnly
            let mark=""
            if(fieldDTO.mark){
                mark= "markedbycolor"
            }
            return(
                <Row key={index} className={mark}>
                    <Col>
                        <ViewEditDate 
                            edit={!readOnly} 
                            attribute={name}
                            component={this} 
                            data={this.state.data.dates}
                        />
                    </Col>
                </Row>
            )
        }else{
            return[]
        }
    }

    /**
     * Responsible for numbers
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
     numberFiled(name, index){
        let fieldDTO = this.state.data.numbers[name]
        if(fieldDTO != undefined){
            let readOnly=this.props.readOnly || fieldDTO.readOnly
            let mark=""
            if(fieldDTO.mark){
                mark= "markedbycolor"
            }
            return(
                <Row key={index} className={mark}>
                    <Col>
                        <ViewEdit
                            mode="number" 
                            edit={!readOnly} 
                            attribute={name}
                            component={this} 
                            data={this.state.data.numbers}
                        />
                    </Col>
                </Row>
            )
        }else{
            return[]
        }
    }

    /**
     * Responsible for logical
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
    logicalFiled(name, index){
        let fieldDTO = this.state.data.logical[name]
        if(fieldDTO != undefined){
            let readOnly=this.props.readOnly || fieldDTO.readOnly
            let mark=""
            if(fieldDTO.mark){
                mark= "markedbycolor"
            }
            return(
                <Row key={index} className={mark}>
                    <Col>
                        <ViewEditOption
                            edit={!readOnly} 
                            attribute={name}
                            component={this} 
                            data={this.state.data.logical}
                        />
                    </Col>
                </Row>
            )
        }else{
            return[]
        }
    }
/**
     * Responsible for droplist
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
 droplistFiled(name, index){
    let fieldDTO = this.state.data.droplist[name]
    if(fieldDTO != undefined){
        let readOnly=this.props.readOnly || fieldDTO.readOnly
        let mark=""
        if(fieldDTO.mark){
            mark= "markedbycolor"
        }
        return(
            <Row key={index} className={mark}>
                <Col>
                    <ViewEditOption
                        edit={!readOnly} 
                        attribute={name}
                        component={this} 
                        data={this.state.data.droplist}
                    />
                </Col>
            </Row>
        )
    }else{
        return[]
    }
}


    /**
     * Take component by variable name and then create it or replace data
     * @param {name of component in literals, dictionaries, files, etc} name 
     * @param {number to create a key} index
     * @parem {data to replace component's data} data not mandatory! 
     */
    takeComponent(name, index, data){
        if(this.state.data.heading.hasOwnProperty(name)){
            return (
                this.heading(name,index)
            )
        }
        if(this.state.data.strings.hasOwnProperty(name)){
            return (
                this.strings(name,index)
            )
        }
        if(this.state.data.literals.hasOwnProperty(name)){
            return (
                this.literal(name,index)
            )
        }
        if(this.state.data.dates.hasOwnProperty(name)){
            return (
                this.dateFiled(name,index)
            )
        }
        if(this.state.data.numbers.hasOwnProperty(name)){
            return (
                this.numberFiled(name,index)
            )
        }
        if(this.state.data.logical.hasOwnProperty(name)){
            return (
                this.logicalFiled(name,index)
            )
        }
        if(this.state.data.dictionaries.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.dictionaries[name]=data
            }else{
                return(
                   // this.dictionary(name,index)
                   Dictionary.placeDictionary(name,this.state.data.dictionaries[name],index,
                    this.props.readOnly,this.state.identifier,this.state.labels[name])
                )
            }
        }
        //
        if(this.state.data.droplist.hasOwnProperty(name)){
            return (
                this.droplistFiled(name,index)
            )
        }

        if(this.state.data.addresses.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.addresses[name]=data
            }else{
                return(
                    //this.addressControl(name,index)
                    AddressForm.place(this.state.data.addresses[name],index,
                        this.props.readOnly,this.state.identifier,this.state.labels[name])
                )
            }
        }
        if(this.state.data.documents.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.documents[name]=data
            }else{
                return(
                    //this.filesControl(name,index)
                    ApplicationFiles.place(this.state.data.documents[name],index,
                        this.props.readOnly,this.state.identifier,this.state.labels[name])
                )
            }
        }
        if(this.state.data.resources.hasOwnProperty(name)){
            let data=this.state.data.resources[name]
            return(
                ResourcesUsage.place(data, index, this.props.readOnly, this.state.identifier, this.state.labels[name], this.state.data)
            )
        }

        if(this.state.data.schedulers.hasOwnProperty(name)){
            return(
                //this.schedulersControl(name,index)
                Scheduler.place(this.state.data.schedulers[name],index,
                    this.props.readOnly,this.state.identifier,this.state.labels[name])
            )
        }
        if(this.state.data.registers.hasOwnProperty(name)){
            return(
                //this.registersControl(name,index)
                Register.place(this.state.data.registers[name],index,
                    this.props.readOnly,this.state.identifier,this.state.labels[name])
            )
        }
        if(this.state.data.persons.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.persons[name]=data
            }else{
                let data=this.state.data.persons[name]
                let readOnly=this.props.readOnly || data.readOnly
                return(
                    //this.personsControl(name, index)
                    Persons.place(data, this.state.data.applicationUrl,
                        this.state.data.applDictNodeId, this.state.data.activityId, 
                                    this.state.identifier, readOnly,this.state.labels[name],index)
                )
            }
        }

        if(this.state.data.atc.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.atc[name]=data
            }else{
                return(
                    ATCCodes.thingControl(
                            this.state.labels[name],
                            this.state.data.atc[name],
                            this.state.identifier,
                            this.props.readOnly,
                            index
                    )
                )
            }
        }
        if(this.state.data.legacy.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.legacy[name]=data
            }else{
                return(
                    LegacyData.place(
                            this.state.data.legacy[name],
                            index,
                            this.props.readOnly,
                            this.state.identifier,
                            this.state.labels[name],
                            this.props.readOnly || this.state.data.readOnly
                    )
                )
            }
        }
        if(this.state.data.intervals.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.intervals[name]=data
            }else{
                let interval= this.state.data.intervals[name]
                interval.reload=true
                return(
                    Interval.place(interval, this.state.identifier, this.props.readOnly, index, this.state.labels[name])
                )
            }
        }
        if(this.state.data.links.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.links[name]=data
            }else{
                let links= this.state.data.links[name]
                links.reload=true
                return(
                    Links.place(links, this.state.identifier, this.props.readOnly, index, this.state.labels[name])
                )
            }
        }
        return([])
    }
     /**
     * Place components inside a cell in rows
     * @param {LayoutCellDTO} cell 
     */
         paintCell(cell){
            let ret=[]
            if(Fetchers.isGoodArray(cell.variables)){
                cell.variables.forEach((variable, index)=>{
                    ret.push(
                        this.takeComponent(variable, index)
                    )
                })
            }
            return ret
        }
        /**
         * Paint cells inside row
         * @param {LayoutRowDTO} row 
         */
        paintCells(row){
            let ret = []
            let lg='6'
            let xl='6'
            if(this.props.narrow || this.state.data.narrowLayout){
                lg='12'
                xl='12'
            }
            if(Fetchers.isGoodArray(row.cells)){
                row.cells.forEach((cell,index)=>{
                    ret.push(
                        <Col xs='12' sm='12' lg={lg} xl={xl} key={index}>
                            {this.paintCell(cell)}
                        </Col>
                    )
                })
            }else{
                //ret.push(<h6>PaintCells. Cells are undefined</h6>)
            }
            return ret;
        }
        /**
         * Paint the content in rows and cols
         * row is LayoutRowDTO
         */
        paintRows(){
            let ret=[]
            if(Fetchers.isGoodArray(this.state.data.layout)){
                this.state.data.layout.forEach((row,index) => {
                    ret.push(
                        <Row key={index}>
                            {this.paintCells(row)}
                        </Row>
                    )
                });
            }else{
                //ret.push(<h6>PaintRows. Layout is undefined</h6>)
            }
            return ret;
        }
        helpButton(){
            return (
                <Row hidden={this.props.readOnly || this.state.data.readOnly} className='p-0 m-0'>
                <Col className="d-flex justify-content-end p-0 m-0">
                    <Button
                        size='lg'
                        className="p-0 m-0"
                        color="link"
                        onClick={()=>{
                            Fetchers.postJSON("/api/"+Navigator.tabSetName()+"/common/thing/help", this.state.data, (query,result)=>{
                                this.state.data=result
                                this.setState(this.state)
                                if(this.state.data.helpDocumentID>0){
                                    window.open('/api/common/thing/help/open/'+this.state.data.helpDocumentID,'_blank').focus()
                                }
                            })
                        }}
                    >
                        <i className="far fa-question-circle"></i>
                    </Button>
                </Col>
            </Row>
            )
        }
    render(){
        if(this.state.data.literals==undefined || this.state.labels.locale==undefined){
            return Pharmadex.wait()
            
        }
        let mainLabels=this.state.data.mainLabels
        if(mainLabels.prefLabel != undefined){
            this.state.labels.prefLabel=mainLabels.prefLabel
        }
        if(mainLabels.description != undefined){
            this.state.labels.description=mainLabels.description
        }
        let activityName=''
        if(this.state.data.activityName != undefined){
            activityName=this.state.labels[this.state.data.activityName]
        }
       
        return(
            <Container fluid>
                {this.helpButton()}
                <Row>
                    <Col>
                        {this.paintRows()}
                    </Col>
                </Row>
                {this.helpButton()}
            </Container>
        )
    }


}
export default Thing
Thing.propTypes={
    data:PropTypes.object.isRequired,          //ThingDTO object
    recipient:PropTypes.string.isRequired,      //recipient of messages from this thing
    readOnly:PropTypes.bool,                   //read only mode
    narrow:PropTypes.bool,                     //one column mode
    noload:PropTypes.bool,                      //thing not needed to load

}