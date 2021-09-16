import React , {Component} from 'react'
import {Container, Row,Input, Col, Button} from 'reactstrap';
import FilterControl from './FilterControl' 

/**
 * Date filter for table header
 * @property {from:,to:} dates
 * @property {function} onChange({from:,to:})
 * This function will be called only when both "to" and "from" are valid
 */
class DateFilter extends Component{
    constructor(props){
        super(props)
        let dt = new Date()
        let year= dt.getFullYear()
        let month=dt.getMonth()+1
        this.state={
            dates :{
                from:this.props.dates.from,
                to:this.props.dates.to,
                on:true
            },
            year:year,
            month:month,
            fromYear:'-',
            fromMonth:'-',
            toYear:'-',
            toMonth:'-'
        }
        this.createDates=this.createDates.bind(this)
        this.createYearsFrom=this.createYearsFrom.bind(this)
        this.createYearsTo=this.createYearsTo.bind(this)
        this.runFilter=this.runFilter.bind(this)
        this.cancelFilter=this.cancelFilter.bind(this)
        this.offFilter=this.offFilter.bind(this)
    }

    componentDidMount(){
        this.createDates()
    }

    /**
     * Create init dates
     */
    createDates(){
        let s = this.state
        let d = this.props.dates
        let from = new Date(d.from)
        let to = new Date(d.to)
        s.fromYear=this.validYear(from.getFullYear(),s.year-5, s.year+5, s.year)
        s.toYear=this.validYear(to.getFullYear(),from.getFullYear(),s.year+5,s.year)
        s.fromMonth=from.getMonth()+1
        s.toMonth=to.getMonth()+1
        this.checkChanges()
    }
    /**
     * Create list of years for "from" field
     */
    createYearsFrom(){
        let ret=[]
        let year= this.state.year
        for(let i=-5;i<=5;i++){
            ret.push(
                <option key={year+i}>{year+i}</option>
            )
        }
        return ret
    }

    /**
     * Create list of years for "to" field
     */
    createYearsTo(){
        return this.createYearsFrom()
 
    }

    /**
     * returns test if one between high and low or pref otherwise
     * @param {number} test 
     * @param {number} low 
     * @param {number} high 
     * @param {number} pref 
     */
    validYear(test, low, high, pref){
        let ret = pref
        if(test>=low && test<=high){
            ret=test
        }
        return ret;
    }
    /**
     * Inform about changes if ones occured
     */
    checkChanges(){
        let s=this.state
        let str = ""
        if(s.fromYear != '-' && this.state.fromMonth !='-'){
            str = this.state.fromMonth + ""
            if(str.length == 1){
                s.dates.from=this.state.fromYear+'-0'+this.state.fromMonth+'-01'
            }else{
                s.dates.from=this.state.fromYear+'-'+this.state.fromMonth+'-01'
            }   
        }
        if(s.toYear != '-' && this.state.toMonth != '-'){
            str = this.state.toMonth + ""
            if(str.length == 1){
                s.dates.to=this.state.toYear+'-0'+this.state.toMonth+'-01'
            }else{
                s.dates.to=this.state.toYear+'-'+this.state.toMonth+'-01'
            }
        }
        s.dates.on=true
        this.setState(s)
    }
    /**
     * Pass filter value as selected by a user
     */
    runFilter(){
        this.checkChanges();
        this.props.onChange(this.state.dates)
    }
    /**
     * Cancel, so old values
     */
    cancelFilter(){
        this.props.onChange(this.props.dates)
    }
    /**
     * turn this filter off
     */
    offFilter(){
        let s= this.state
        s.dates.on=false
        this.setState(s)
        this.props.onChange(this.state.dates)
    }

    render(){
        return(
            <Container className="m-0 p-0">
            <Row className="m-0 p-0">
                        <Col xs="auto" className="m-0 p-0">
                            <Input className="m-1 p-0" style={{fontSize:'0.7rem', height:'2.0rem', width:'auto'}} type="select"
                            value={this.state.fromYear} onChange={(e)=>{
                                let s = this.state;
                                s.fromYear=e.target.value
                                this.setState(s)
                                this.checkChanges()
                            }}>
                                {this.createYearsFrom()}
                            </Input>
                        </Col>
                        <Col  xs="auto" className="m-0 p-0">
                            <Input className="m-1 p-0" style={{fontSize:'0.7rem', height:'2.0rem', width:'auto'}} type="select"
                            value={this.state.fromMonth} onChange={(e)=>{
                                let s = this.state;
                                s.fromMonth=e.target.value
                                this.setState(s)
                                this.checkChanges()
                            }}>
                                <option>-</option>
                                <option>1</option>
                                <option>2</option>
                                <option>3</option>
                                <option>4</option>
                                <option>5</option>
                                <option>6</option>
                                <option>7</option>
                                <option>8</option>
                                <option>9</option>
                                <option>10</option>
                                <option>11</option>
                                <option>12</option>
                            </Input>
                        </Col>
            </Row>
            <Row className="m-0 p-0">
                <Col xs="auto" className="m-0 p-0">
                    <Input className="m-1 p-0" style={{fontSize:'0.7rem', height:'2.0rem', width:'auto'}} type="select"
                     value={this.state.toYear}onChange={(e)=>{
                        let s = this.state;
                        s.toYear=e.target.value
                        this.setState(s)
                        this.checkChanges()
                    }}>
                       {this.createYearsTo()}
                    </Input>
                </Col>
                <Col  xs="auto" className="m-0 p-0">
                    <Input className="m-1 p-0" style={{fontSize:'0.7rem', height:'2.0rem', width:'auto'}} type="select"
                    value={this.state.toMonth}onChange={(e)=>{
                        let s = this.state;
                        s.toMonth=e.target.value
                        this.setState(s)
                        this.checkChanges()
                    }}>
                        <option>-</option>
                        <option>1</option>
                        <option>2</option>
                        <option>3</option>
                        <option>4</option>
                        <option>5</option>
                        <option>6</option>
                        <option>7</option>
                        <option>8</option>
                        <option>9</option>
                        <option>10</option>
                        <option>11</option>
                        <option>12</option>
                    </Input>
                </Col>
            </Row>
            <FilterControl
                runFilter={this.runFilter}
                cancelFilter={this.cancelFilter}
                offFilter={this.offFilter}
            />
            </Container>
        )
    }
}
export default DateFilter