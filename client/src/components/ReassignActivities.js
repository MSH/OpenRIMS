import React , {Component} from 'react'
import {Container, Row, Col,Input,FormGroup, Label} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import Pharmadex from './Pharmadex'
import ButtonUni from './form/ButtonUni'
import TableSearch from './utils/TableSearch'
import Navigator from './utils/Navigator'
/**
 * Reassigning actions to another employee or employees
 */
class ReassignActivities extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{},            //ReassignActivitiesDTO.java  
            labels:{
                reassign_employee:'',
                select_employee_other:'',
                select_employee:'',
                global_cancel:'',
                route_action:'',
                search:'',
                employeeactivities:'',
                currentworkfload:'',
                global_help:'',
                availableactivities:'',
                selectall:'',
                deselectall:'',
                selectedonly:''
            }
        }
        this.eventProcessor=this.eventProcessor.bind(this)
        this.placeInputForm=this.placeInputForm.bind(this)
        this.left=this.left.bind(this)
        this.load=this.load.bind(this)
        this.right=this.right.bind(this)
        this.selectRow=this.selectRow.bind(this)
       this.hideControls=this.hideControls.bind(this)
    }
/**
     * Listen messages from other components
     * @param {Window Event} event 
     */
eventProcessor(event){
    let data=event.data
   
}

componentDidMount(){
window.addEventListener("message",this.eventProcessor)
this.load()
Locales.createLabels(this)
Locales.resolveLabels(this)
this.setState(this.state)
}

componentWillUnmount(){
window.removeEventListener("message",this.eventProcessor)
}
 
/**
     * laod all table this tools
*/     
 load(){
    Fetchers.postJSON("/api/admin/reassign/employee/load", this.state.data, (query, result)=>{
        this.state.data=result
        Locales.createLabels(this)
        Locales.resolveLabels(this)
        this.setState(this.state)
    })
}

/**
     * Select a row in the table
     * @param {number} rowNumber 
     */
selectRow(rowNumber){
    let row = this.state.data.available.rows[rowNumber]
    row.selected=!row.selected
    let index = this.state.data.prevSelected.indexOf(row.dbID)
    if(row.selected){
     if(index==-1){
         this.state.data.prevSelected.push(row.dbID)
     } 
    }else{  //deselected
        if(index!=-1){
            this.state.data.prevSelected[index]=0
        }
    }
    this.setState(this.state)
}
hideControls(){
let hideControls = (this.state.data.available.rows==0 || this.state.data.available.headers.pages<2) && 
                            !this.state.data.selectedOnly && !this.state.data.available.headers.filtered
                            return hideControls
}
//data employee1
left(){
    return(
<Row className={Pharmadex.settings.activeBorder}>
    <Col>
        <Row className='mb-3'>
            <Col xs='12' sm='12' lg='4' xl='3'>
                <b>{this.state.labels.select_employee}</b>
            </Col>  
        </Row>
        <Row>
            <Col>
                <TableSearch
                    label={this.state.labels.search}
                    tableData={this.state.data.employee}
                    loader={this.load}
                    selectRow={(rowno)=>{
                        let row= this.state.data.employee.rows[rowno]
                        if(row.selected){
                            this.state.data.selectedEmployee=''
                        }else{
                        this.state.data.selectedEmployee=row.row[2].value
                        }
                        this.load()
                    }}
                />
            </Col>
        </Row>
         <Row hidden={!this.state.data.availableActivities || !this.state.data.selectedEmployee}>
            <Col>
                <TableSearch 
                    label={this.state.labels.search}
                    tableData={this.state.data.activities} 
                    loader={this.load}
                    title={this.state.labels.employeeactivities}
                />
            </Col>
        </Row>
        <Row hidden={!this.state.data.selectedEmployee || !this.state.data.selectedEmployeeOther ||
        this.state.data.selectedEmployee==this.state.data.selectedEmployeeOther|| this.state.data.available.rows==0}>
        <Col xs='12' sm='12' lg='3' xl='3' >
            <ButtonUni
                label={this.state.labels.selectall}
                color='primary'
                onClick={()=>{
                    Fetchers.postJSON("/api/admin/reassign/employee/selectall", this.state.data, (query,result)=>{
                        this.state.data=result
                        this.setState(this.state)
                    })
                }}
            />
        </Col>
        <Col xs='12' sm='12' lg='3' xl='3'>
            <ButtonUni
                label={this.state.labels.deselectall}
                color='primary'
                outline
                onClick={()=>{
                    Fetchers.postJSON("/api/admin/reassign/employee/deselectall", this.state.data, (query,result)=>{
                        this.state.data=result
                        this.setState(this.state)
                    })
                }}
            />
        </Col>
        {/* <Col xs='12' sm='12' lg='3' xl='3'>
            <ButtonUni
                label={this.state.labels.selectonly}
                color='primary'
                onClick={()=>{
                    Fetchers.postJSON("/api/admin/reassign/employee/selectonly", this.state.data, (query,result)=>{
                        this.state.data=result
                        this.setState(this.state)
                    })
                }}
            />
        </Col> */}
        <Col xs='12' sm='12' lg='3' xl='3'>
                        <FormGroup hidden={this.hideControls()} check className="form-control-sm">
                            <Label check>
                            <Input 
                                type="checkbox"
                                value={this.state.data.selectedOnly}
                                checked={this.state.data.selectedOnly}
                                onChange={()=>{
                                    this.state.data.selectedOnly=!this.state.data.selectedOnly
                                    Fetchers.postJSON("/api/admin/reassign/employee/selectonly", this.state.data, (query,result)=>{
                                        this.state.data=result
                                        this.setState(this.state)
                                    })
                                }} 
                                />
                                {this.state.labels.selectedonly}
                            </Label>
                        </FormGroup>
                    </Col>
        </Row>
        <Row hidden={!this.state.data.selectedEmployee || !this.state.data.selectedEmployeeOther||
        this.state.data.selectedEmployee==this.state.data.selectedEmployeeOther}>
            <Col>
                <TableSearch 
                    label={this.state.labels.search}
                    tableData={this.state.data.available} 
                    loader={this.load}
                    title={this.state.labels.availableactivities}
                    selectRow={(rowNo)=>{
                        this.selectRow(rowNo)
                    }}
                />
            </Col>
        </Row>
    </Col>
</Row>
    )
}
//data employee2
right(){
    return(
<Row className={Pharmadex.settings.activeBorder}>
    <Col>
        <Row className='mb-3'>
            <Col xs='12' sm='12' lg='4' xl='3'>
                <b>{this.state.labels.select_employee_other}</b>
            </Col>
        </Row>
        <Row>
            <Col>
                <TableSearch
                    tableData={this.state.data.employeeother}
                    loader={this.load}
                    selectRow={(rowno)=>{
                        let row= this.state.data.employeeother.rows[rowno]
                        if(row.selected){
                            this.state.data.selectedEmployeeOther=''
                        }else{
                        this.state.data.selectedEmployeeOther=row.row[2].value
                        }
                        this.load()
                    }}
                />
            </Col>
        </Row>
        <Row hidden={!this.state.data.selectedEmployeeOther}> 
            <Col>
                <TableSearch 
                    label={this.state.labels.search}
                    tableData={this.state.data.currentworkfload} 
                    loader={this.load}
                    title={this.state.labels.currentworkfload}
                />
            </Col>
        </Row>
    </Col>
</Row>
    )
}
//buttons and place data employees
placeInputForm(){
    return(
<Container fluid>
        <Row className='mb-5'>
                    <Col xs='12' sm='12' lg='6' xl='9'>
                        <h4>{this.state.labels.reassign_employee}</h4>
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_help}
                            color='info'
                            onClick={()=>{
                                window.open('/api/admin/help/reassign/activities','_blank').focus()
                            }}
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='1'>
                        <ButtonUni
                            label={this.state.labels.global_cancel}
                            outline
                            color='info'
                            onClick={()=>{
                                Navigator.navigate("administrate")
                            }}
                        />
                    </Col>
                    <Col xs='12' sm='12' lg='2' xl='1' hidden={this.state.data.availableActivities}>
                        <ButtonUni
                            label={this.state.labels.route_action}
                            color='primary'
                            onClick={()=>{
                                Fetchers.postJSON("/api/admin/reassign/employee/run", this.state.data, (query,result)=>{
                                    this.state.data=result
                                    if(!this.state.data.valid){
                                        Navigator.message('*', '*', 'show.alert.pharmadex.2', {mess:this.state.data.identifier, color:'danger'})
                                    }
                                    this.load()
                                    this.setState(this.state)
                                })
                            }}
                        />
                    </Col>
                    </Row>
                    <Row>
                    <Col xs='12' sm='12' lg='6' xl='6'>
                        {this.left()}
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6' >
                    {this.right()}
                    </Col>  
                </Row>
</Container>
    )
}

render(){
    if(this.state.data.employee == undefined || this.state.labels.locale == undefined){
        return Pharmadex.wait()
    }else{
        return(this.placeInputForm())
    }
}

}
export default ReassignActivities
ReassignActivities.propTypes={
}