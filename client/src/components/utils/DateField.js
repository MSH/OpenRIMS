import React , {Component} from 'react'
import {Container, Row,Input, Col, Button} from 'reactstrap';
import FilterControl from './FilterControl' 

/**
 * Date field
 * @property {datesMonth:,datesYear:} dates
  */
class DateField extends Component{
    constructor(props){
        super(props)
        let dt = new Date()
        let year= dt.getFullYear()
        let month=dt.getMonth()+1
        this.state={
           year:year,
           month:month,
            fromYear:'-',
            fromMonth:'-',
        }
    }

    componentDidMount(){
        this.createDates()
    }
    /**
     * Create init dates
     */
    createDates(){
        let s = this.state
        s.fromYear=this.props.datesYear
      if(s.fromYear=="1990"){
        s.fromMonth="-"
      }else{
        s.fromMonth=this.props.datesMonth
      }
    }
    /**
     * Create list of years for "from" field
     */
    createYearsFrom(){
        let ret=[]
        let year= this.state.year
        ret.push(
            <option key={"-"}>{"-"}</option>
        )
        for(let i=-5;i<=5;i++){
            ret.push(
                <option key={year+i}>{year+i}</option>
            )
        }
        return ret
    }
    /**
     * Inform about changes if ones occured
     */
    checkChanges(){
        this.props.onChange(this.state.fromYear, this.state.fromMonth)
    }

    render(){
        return(
            <Container className="m-0 p-0">
            <Row className="m-0 p-0">
                        <Col xs="auto" className="m-0 p-0">
                            <Input className="m-1 p-0" style={{fontSize:'1.0rem', height:'2.0rem', width:'auto'}} type="select"
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
                            <Input className="m-1 p-0" style={{fontSize:'1.0rem', height:'2.0rem', width:'auto'}} type="select"
                            value={this.state.fromMonth} onChange={(e)=>{
                                let s = this.state;
                                s.fromMonth=e.target.value
                                this.setState(s)
                                this.checkChanges()
                            }}>
                                {/* <option>-</option>
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
                                <option>12</option> */}
                                <option>-</option>
                                <option>January</option>
                                <option>February</option>
                                <option>March</option>
                                <option>April</option>
                                <option>May</option>
                                <option>June</option>
                                <option>July</option>
                                <option>August</option>
                                <option>September</option>
                                <option>October</option>
                                <option>November</option>
                                <option>December</option>
                            </Input>
                        </Col>
            </Row>
            </Container>
        )
    }
}
export default DateField