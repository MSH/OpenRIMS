import React , {Component} from 'react'
import {Container,Row, Col} from 'reactstrap'
import DictLevel from './DictLevel'
import Locales from './utils/Locales'
import Navigator from './utils/Navigator'
import UserLevel from './UserLevel'
import Fetchers from './utils/Fetchers'


/**
 * Add/edit authorities and users
 */
class Authorities extends Component{
    constructor(props){
        super(props)
        this.url="Authorities"
        this.state={
            ready:false,
            labels:{
                authorities:'',
                persons:'',
            },
            parentIds: [0],
        }
        this.content=this.content.bind(this)
        this.onSelect=this.onSelect.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
    }

 
    /**
     * User selected a line in a dictionary
     * @param {sender:dictionary,parentID:parent,selectedId:selected} data 
     */
    onSelect(data){
        let index=this.state.parentIds.indexOf(data.parentId)
        this.state.parentIds=this.state.parentIds.slice(0,index+1)
        if(data.selectedId>0){
            this.state.parentIds.push(data.selectedId)
        }
        this.setState(this.state)
        //and store the state localy
       Fetchers.writeLocaly("authorities",this.state.parentIds)
    }

    eventProcessor(event){
        let data=event.data
        if(data.from != this.url && (data.to=="*" || data.to==this.url)){
            if(data.subject=="onSelect"){
                this.onSelect(data.data)
            }
            if(data.subject=="onEdit"){
                data.data.caller=Navigator.caller()
                let param = JSON.stringify(data.data)
                Navigator.navigate("administrate", "authority",param) 
            }
            if(data.subject=="onUserEdit"){
                data.data.caller=Navigator.caller()
                let param=JSON.stringify(data.data)
                Navigator.navigate("administrate", "userelement",param) 
            }
        }
    }
    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.state.ready=true
        Locales.resolveLabels(this)
        let restoredIds = Fetchers.readLocaly("authorities", this.state.parentIds)
        this.state.parentIds=restoredIds
        this.setState(this.state.parentIds)
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }


    /**
     * Create a row content based on user's selection
     */
    content(){
        let ret=[]
        this.state.parentIds.forEach((element,index)=>{
            let selectedId=this.state.parentIds[index+1]
            if(selectedId==undefined){
                selectedId=0
            }
            ret.push(
                <Row key={index} className="pb-3">
                    <Col xs='12' sm='12' lg='6' xl='6' className="p-0">
                        <DictLevel  identifier={"org_"+element}
                                    url='organization.authority'
                                    parentId={element}
                                    selectedId={selectedId}/>
                    </Col>
                    <Col xs='12' sm='12' lg='6' xl='6' hidden={this.state.parentIds.length<2}>
                        <UserLevel 
                                    identifier={"user_"+element}
                                    conceptId={this.state.parentIds[index+1]}/>
                    </Col>
                </Row>
            )
        })
        return ret
    }

    render(){
        if(this.state.labels.locale == undefined){
            return []
        }
        if(!this.state.ready){
            return []
        }
        return(
            <Container fluid>
                {this.content()}
            </Container>
        )
    }


}
export default Authorities
Authorities.propTypes={
    
}