import React , {Component} from 'react'
import {Container,Row,Col,Alert, Breadcrumb, BreadcrumbItem, NavItem} from 'reactstrap'
import PropTypes from 'prop-types'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Navigator from './utils/Navigator'
import ViewEdit from './form/ViewEdit'
import ViewEditOption from './form/ViewEditOption'
import ViewEditDate from './form/ViewEditDate'
import Dictionary from './Dictionary'
import ApplicationFiles from './ApplicationFiles'
import AddressForm from './AddressForm'
import Persons from './Persons'
import ResourcesUsage from './ResourcesUsage'
import PersonSelector from './PersonSelector'
import Scheduler from './Scheduler'
import Register from './Register'
import PersonSpecial from './PersonSpecial'
import Amended from './Amended'


/**
 * Responsible for a ThingDTO
 * issue thingUpdated event for each update!
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
        this.state={
            identifier:Date.now().toString(),  //my address for messages
            data:this.props.data,             //ThingDTO         
            labels:{},
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.paintRows=this.paintRows.bind(this)
        this.paintCells=this.paintCells.bind(this)
        this.paintCell=this.paintCell.bind(this)
        this.heading=this.heading.bind(this)
        this.strings=this.strings.bind(this)
        this.literal=this.literal.bind(this)
        this.dictionary=this.dictionary.bind(this)
        this.filesControl=this.filesControl.bind(this)
        this.resourcesControl=this.resourcesControl.bind(this)
        this.dateFiled=this.dateFiled.bind(this)
        this.load=this.load.bind(this)
        this.save=this.save.bind(this)
        this.addressControl=this.addressControl.bind(this)
        this.personsControl=this.personsControl.bind(this)
        this.numberFiled=this.numberFiled.bind(this)
        this.logicalFiled=this.logicalFiled.bind(this)
        this.personSelectorControl=this.personSelectorControl.bind(this)
        this.schedulersControl=this.schedulersControl.bind(this)
        this.registersControl=this.registersControl.bind(this)
        this.saveGuest=this.saveGuest.bind(this)
        this.postLoaded=this.postLoaded.bind(this)
        this.personSpecControl=this.personSpecControl.bind(this)
        this.amendmentsControl=this.amendmentsControl.bind(this)
    }

    /**
     * Guest Applications should be saved by own wayh
     * savedByAction means saved by Save button
     */
         saveGuest(savedByAction){
            Fetchers.postJSONNoSpinner("/api/guest/thing/save/application",this.state.data,(query,result)=>{
                this.state.data=result
                if(this.state.data.valid){
                    if(savedByAction){
                        Navigator.message(this.state.identifier, this.props.recipient, "savedByAction",this.state.data)
                    }else{
                        Navigator.message(this.state.identifier, this.props.recipient, "saved", this.state.data)
                    }
                }else{
                    this.setState(this.state)
                }
                
            })
        }
    /**
     * ask all components for data, then save
     * savedByAction means saved by Save button
     */
    save(savedByAction){
        Fetchers.postJSONNoSpinner("/api/"+Navigator.tabSetName()+"/thing/save",this.state.data,(query,result)=>{
            this.state.data=result
            if(this.state.data.valid){
                if(savedByAction){
                    Navigator.message(this.state.identifier, this.props.recipient, "savedByAction",this.state.data)
                }else{
                    Navigator.message(this.state.identifier, this.props.recipient, "saved", this.state.data)
                }
            }else{
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
                })
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
            if(data.subject=="saveAllGuest"){
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
           if(data.subject=='saveGuest'){
            this.saveGuest()
            }
           //ask to apply auxiliary path. Only for guest applications
           if(data.subject=='auxPath'){
            this.state.data.auxPathVar=data.data.varName
            this.state.data.strict=false
            Fetchers.postJSONNoSpinner("/api/guest/thing/save/application",this.state.data,(query,result)=>{
                this.state.data=result
                if(result.valid){
                    Fetchers.postJSONNoSpinner("/api/guest/activity/auxpath", this.state.data, (query,result)=>{
                        this.state.data=result
                        if(result.valid){
                            Navigator.message(this.state.identifier, this.props.recipient, "auxPath", this.state.data)
                        }else{
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
        this.load()
    }
    componentDidUpdate(){
        Navigator.message(this.state.identifier, this.props.recipient, "thingUpdated", this.state.data)
        if(this.props.data.repaint){
            this.state.data=this.props.data
            this.props.data.repaint=false
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
        Locales.createLabels(this,"personselector")
        Locales.createLabels(this,"schedulers")
        Locales.createLabels(this,'files')
        Locales.createLabels(this,'registers')
        Locales.createLabels(this,'personspec')
        Locales.createLabels(this,'amendments')
        if(this.state.data.activityName != undefined){
            this.state.labels[this.state.data.activityName]=''     //for activity names
        }
        Locales.resolveLabels(this)
        Navigator.message(this.state.identifier, this.props.recipient,"thingLoaded",this.state.data);
    }
    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * 
     * @param {name of heading} name 
     * @param {number to create a key} index 
     */
    heading(name,index){
        let head = this.state.data.heading[name]
        if(head != undefined){
            return(
                <h4 key={name}>{head}</h4>
            )
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
            return(
                <Row key={index}>
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
            return(
                <Row key={index}>
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
            return(
                <Row key={index}>
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
            return(
                <Row key={index}>
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
            return(
                <Row key={index}>
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
     * Responsible for dictionaries
     * @param {string} name 
     * @param {number} index 
     */
    dictionary(name, index){
        let dict=this.state.data.dictionaries[name]
        if(dict == undefined){
            return []
        }
        let readOnly=this.props.readOnly || dict.readOnly
        if(readOnly){
            dict.readOnly=true
        }
        dict.varName=name
        return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{this.state.labels[name]}</h6>
                        </Col>
                    </Row>
                    <Row hidden={this.state.data.dictionaries[name].valid}>
                        <Col>
                            <Alert color="danger" className="p-0 m-0">
                                <small>{this.state.data.dictionaries[name].identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Dictionary identifier={name} recipient={this.state.identifier} data={dict} display/>
                        </Col>
                    </Row>
                    
                </Col>
            </Row>
        )
    }
    /**
     * Responsible for addrress
     * @param {string} name 
     * @param {number} index 
     */
    addressControl(name,index){
        let addr = this.state.data.addresses[name]
        if(addr==undefined){
            return []
        }
    return(
        <Row key={index}>
            <Col>
                <Row>
                    <Col>
                        <h6>{this.state.labels[name]}</h6>
                    </Col>
                </Row>
                <Row hidden={addr.valid}>
                        <Col>
                            <Alert color="danger" className="p-0 m-0">
                                <small>{addr.identifier}</small>
                            </Alert>
                        </Col>
                </Row>
                <Row>
                    <Col>
                        <AddressForm
                                data={addr}
                                recipient={this.state.identifier}
                                readOnly={this.props.readOnly}
                        />
                    </Col>
                </Row>
            </Col>
        </Row>
        )
    }

    /**
     * Responsible for files 
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
    filesControl(name, index){
        let files=this.state.data.documents[name] 
        if(files!=undefined){
            if(this.props.readOnly){
                files.readOnly=true
            }
            return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{this.state.labels[name]}</h6>
                        </Col>
                    </Row>
                    <Row hidden={files.valid}>
                        <Col>
                            <Alert color="danger" className="p-0 m-0">
                                <small>{files.identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ApplicationFiles data={files} key={this.state.identifier+name}
                                            recipient={this.state.identifier}
                                            readOnly={this.props.readOnly}/>
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
    }

    /**
     * Responsible for resources 
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
        resourcesControl(name, index){
        if(this.state.data.readOnly || this.props.readOnly){
            return []
        }
        let res=this.state.data.resources[name] 
        if(res!=undefined){
            if(this.props.readOnly){
                res.readOnly=true
            }
            return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{this.state.labels[name]}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <ResourcesUsage data={res}
                                            thing={this.state.data}
                                            recipient={this.state.identifier}
                                            readOnly={this.props.readOnly}/>
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
    }

    /**
     * Responsible for person selection
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
        personSelectorControl(name, index){
        if(this.state.data.readOnly || this.props.readOnly){
            return []
        }
        let res=this.state.data.personselector[name] 
        if(res!=undefined){
            return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{this.state.labels[name]}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <PersonSelector data={res}
                                            recipient={this.state.identifier}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
    }

    /**
     * Responsible for the special person data
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
        personSpecControl(name, index){
            let res=this.state.data.personspec[name] 
            if(res!=undefined){
                return(
                <Row key={index}>
                    <Col>
                        <Row hidden={this.state.data.personspec[name].valid} className="mb-1">
                            <Col>
                                <Alert color="danger" className="p-0 m-0">
                                    <small>{this.state.data.personspec[name].identifier}</small>
                                </Alert>
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                <PersonSpecial data={res}
                                                recipient={this.state.identifier}
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
                )
            }
        }

    /**
     * Responsible for teh amended object selection
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
        amendmentsControl(name, index){
        let res=this.state.data.amendments[name] 
        if(res!=undefined){
            return(
            <Row key={index}>
                <Col>
                    <Row hidden={this.state.data.amendments[name].valid} className="mb-1">
                        <Col>
                            <Alert color="danger" className="p-0 m-0">
                                <small>{this.state.data.amendments[name].identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                    <Row hidden={this.props.readOnly}>
                        <Col>
                            <Amended data={res}
                                            recipient={this.state.identifier}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
    }


    /**
     * Responsible for schedulers
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
        schedulersControl(name, index){
        let res=this.state.data.schedulers[name] 
        if(res!=undefined){
            return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{this.state.labels[name]}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Scheduler data={res}
                                            recipient={this.state.identifier}
                                            readOnly={this.props.readOnly || this.state.data.readOnly}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
    }

       /**
     * Responsible for registers
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
    registersControl(name, index){
        let res=this.state.data.registers[name] 
        if(res!=undefined){
            return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{this.state.labels[name]}</h6>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Register data={res}
                                            recipient={this.state.identifier}
                                            readOnly={this.props.readOnly || this.state.data.readOnly}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
        }
    }

    /**
     * Responsible for persons
     * @param {string} name 
     * @param {number} index 
     * @returns 
     */
     personsControl(name, index){
        let persons=this.state.data.persons[name] 
        if(persons!=undefined){
            return(
            <Row key={index}>
                <Col>
                    <Row>
                        <Col>
                            <h6>{this.state.labels[name]}</h6>
                        </Col>
                    </Row>
                    <Row hidden={persons.valid}>
                        <Col>
                            <Alert color="danger" className="p-0 m-0">
                                <small>{persons.identifier}</small>
                            </Alert>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Persons data={persons}
                                applicationUrl={this.state.data.applicationUrl}
                                applDictNodeId={this.state.data.applDictNodeId}
                                activityId={this.state.data.activityId} 
                                recipient={this.state.identifier}
                                readOnly={this.state.data.readOnly || this.props.readOnly}/>
                        </Col>
                    </Row>
                </Col>
            </Row>
            )
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
                    this.dictionary(name,index)
                )
            }
        }
        if(this.state.data.addresses.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.addresses[name]=data
            }else{
                return(
                    this.addressControl(name,index)
                )
            }
        }
        if(this.state.data.documents.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.documents[name]=data
            }else{
                return(
                    this.filesControl(name,index)
                )
            }
        }
        if(this.state.data.resources.hasOwnProperty(name)){
            return(
                this.resourcesControl(name,index)
            )
        }
        if(this.state.data.personselector.hasOwnProperty(name)){
            return(
                this.personSelectorControl(name,index)
            )
        }
        if(this.state.data.schedulers.hasOwnProperty(name)){
            return(
                this.schedulersControl(name,index)
            )
        }
        if(this.state.data.registers.hasOwnProperty(name)){
            return(
                this.registersControl(name,index)
            )
        }
        if(this.state.data.persons.hasOwnProperty(name)){
            if(data != undefined){
                this.persons[name]=data
            }else{
                return(
                    this.personsControl(name, index)
                )
            }
        }
        if(this.state.data.personspec.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.personspec[name]=data
            }else{
                return(
                    this.personSpecControl(name, index)
                )
            }
        }
        if(this.state.data.amendments.hasOwnProperty(name)){
            if(data != undefined){
                this.state.data.amendments[name]=data
            }else{
                return(
                    this.amendmentsControl(name, index)
                )
            }
        }
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
            }
            return ret;
        }

    render(){
        if(this.state.data.literals==undefined || this.state.labels.locale==undefined){
            return <div> <i className="blink fas fa-circle-notch fa-spin" style={{color:'#D3D3D3'}}/></div>
            
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
                <Row>
                    <Col>
                        {this.paintRows()}
                    </Col>
                </Row>
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