import React , {Component} from 'react'
import {Container,Input,Row} from 'reactstrap';
import PropTypes from 'prop-types'
import FilterControl from './FilterControl' 

/**
 * @property value - string
 * @property onChange - function(newValue) 
 */
class TextFilter extends Component{
    constructor(props){
        super(props)
        this.state={
            value:this.props.value
        }
    }

    render(){
        return(
            <Container className="m-0 p-0">
                <Row className="m-0 p-0">
                    <Input
                        className="m-1 p-0" style={{fontSize:'0.7rem', height:'2.0rem', width:'auto'}}
                        value={this.state.value}
                        onChange={(e)=>{
                            let s=this.state
                            s.value=e.target.value
                            this.setState(s)
                        }}
                    />
                </Row>
                <FilterControl
                    runFilter={()=>{
                        this.props.onChange(this.state.value)
                    }}
                    cancelFilter={()=>{
                        this.props.onChange(this.state.value)
                    }}
                    offFilter={()=>{
                        let s=this.state
                        s.value=""
                        this.props.onChange(s.value)
                    }}
                />

            </Container>
        )
       
    }


}
TextFilter.propTypes={
    value:PropTypes.string.isRequired,
    onChange:PropTypes.func.isRequired
}
export default TextFilter