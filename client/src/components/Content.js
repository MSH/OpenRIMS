import React , {Component} from 'react'
import {Container, Row, Col, Label} from 'reactstrap'
import PropTypes from 'prop-types'
import Fetchers from './utils/Fetchers'
import Tile from './Tile'
import Locales from './utils/Locales'
import Navigator from './utils/Navigator'
import Administrate from './Administrate'
import Applications from './Applications'
import ToDoList from './ToDoList'
import Monitoring from './Monitoring'
import Reports from './Reports'
import PrintPreview from './PrintPreview'
import Spinner from './utils/Spinner'
import SpinnerMain from './utils/SpinnerMain'
import PublicPermitData from './PublicPermitData'

/**
 * Uniform content component
 * It displays any content regardless of tabset and tab
 */
class Content extends Component{
    constructor(props){
        super(props)
        this.state={
            data:{}, //ContentDTO.java
            labels:{    //for tiles
                download:"",
                more:"",
                welcomeaboard:''
            }
        }
        this.paintRows=this.paintRows.bind(this)
        this.placeContent=this.placeContent.bind(this)
        this.adminTab=this.adminTab.bind(this)
        this.guestTab=this.guestTab.bind(this)
        this.screenerTab=this.screenerTab.bind(this)
        this.moderatorTab=this.moderatorTab.bind(this)
        this.reviewerTab=this.reviewerTab.bind(this)
        this.inspectorTab=this.inspectorTab.bind(this)
        this.accountantTab=this.accountantTab.bind(this)
        this.secretaryTab=this.secretaryTab.bind(this)
        this.landingTab=this.landingTab.bind(this)
        this.publicTab=this.publicTab.bind(this)
    }
    /**
     * Initial load tiles from the server
     */
    componentDidMount(){
        if(this.props.menu=='public'){
            this.state.data.layout={}
            this.state.labels.more="defined_to_pass_render"
            this.setState(this.state)
            return;     //we don't need initial tiles for public get API
        }
        let api="/api/public/landing/content"
        switch(this.props.menu){
            case("guest"):
                api="/api/guest/content"
                break
            case("admin"):
                api="/api/admin/content"
                break
            case("moderator"):
                api="/api/moderator/content"
                break
            case("screener"):
                api="/api/screener/content"
                break
            case("reviewer"):
                api="/api/reviewer/content"
                break
            case("accountant"):
                api="/api/accountant/content"
                break
            case("inspector"):
                api="/api/inspector/content"
                break
            case("secretary"):
                api="/api/secretary/content"
                break
            }
            
        Fetchers.postJSON(api, this.state.data, (query,result)=>{
            this.state.data=result
            delete this.state.data.tiles.justloaded
            this.setState(this.state.data)
            Locales.resolveLabels(this)
        })
    }
    /**
     * Paint tiles to allow select a tab from the current tabset
     * @returns
     */
    paintRows(){
        let ret=[]
        let layout = this.state.data.layout
        let tiles = this.state.data.tiles

        layout.forEach((row, index)=>{
            let cells = row.cells
            if(Fetchers.isGoodArray(cells)){
                let columns=[]
                cells.forEach((cell, j)=>{
                    let tile = tiles[cell.variables[0]]
                    if(tile != undefined){
                        columns.push(
                            <Col key={j} xs='12' sm='12' lg='6' xl='4'>
                                <Tile content={tile} labels={this.state.labels}/>
                            </Col>
                        )
                    }    
                    if(cells.length - 1 == j){
                        ret.push(
                            <Row key={index}>
                                {columns}
                            </Row>
                        )
                    }
                })
            }
        })
            
        return ret
    }
    /**
     * tab for administrator
     */
    adminTab(){
        switch(Navigator.tabName().toLowerCase()){
            case "administrate":
                return <Administrate />
            case "todolist":
                return <ToDoList />
            case "monitor":
                return <Monitoring />
            case "reports":
                return <Reports />
            default:
                return this.paintRows()
        }
    }
    /**
     * "Tab" for a guest 
     */
    guestTab(){
        let parStr=""
        let data={}
        switch(Navigator.tabName().toLowerCase()){
            case "applications":
                return <Applications />
            case "todolist":
                return <ToDoList />
            case "monitor":
                return <Monitoring />
            case "reports":
                return <Reports />
            case "printprev":
                parStr = Navigator.parameterValue();
                data = JSON.parse(parStr)
                return <PrintPreview data={data} narrow/>
            default:
                return this.paintRows()
        }
    }

    /**
     * "Tab" for a screener 
     */
    screenerTab(){
        switch(Navigator.tabName().toLowerCase()){
            case "todolist":
                return <ToDoList />
            case "monitor":
                return <Monitoring />
            case "reports":
                return <Reports />
            default:
                return this.paintRows()
        }
    }

    /**
     * "Tab" for a moderator 
     */
    moderatorTab(){
        switch(Navigator.tabName().toLowerCase()){
            case "todolist":
                return <ToDoList />
            case "monitor":
                return <Monitoring />
            case "reports":
                return <Reports />
            default:
                return this.paintRows()
        }
    }

    /**
     * "Tab" for an inspector 
     */
    inspectorTab(){
        switch(Navigator.tabName().toLowerCase()){
            case "todolist":
                return <ToDoList />
            case "monitor":
                return <Monitoring />
            case "reports":
                return <Reports />
            default:
                return this.paintRows()
        }
    }

    /**
     * "Tab" for a reviwer 
     */
    reviewerTab(){
        switch(Navigator.tabName().toLowerCase()){
            case "todolist":
                return <ToDoList />
            case "reports":
                    return <Reports />
            case "monitor":
                return <Monitoring />
            default:
                return this.paintRows()
        }
    }
    /**
     * "Tab" for an accountant 
     */
     accountantTab(){
        switch(Navigator.tabName().toLowerCase()){
            case "todolist":
                return <ToDoList />
            case "reports":
                return <Reports />
            case "monitor":
                return <Monitoring />
            default:
                return this.paintRows()
        }
    }

    /**
     * "Tab" for a secretary 
     */
         secretaryTab(){
            switch(Navigator.tabName().toLowerCase()){
                case "todolist":
                    return <ToDoList />
                case "monitor":
                    return <Monitoring />
                case "reports":
                    return <Reports />
                default:
                    return this.paintRows()
            }
        }
    landingTab(){
        switch(Navigator.tabName().toLowerCase()){
            case "reports":
                return <Reports />
            default:
                return this.paintRows()
        }
        
    }

    publicTab(){
        let parStr=""
        let data={}
        switch(Navigator.tabName().toLowerCase()){
            case "publicpermitdata":
                parStr = Navigator.parameterValue();
                data = JSON.parse(parStr)
                return <PublicPermitData  data={data}/>
            default:
                return this.paintRows()
        }
    }

    /**
     * palce tiles to select a tabset or tabset itself, if defined
     */
    placeContent(){
        switch(Navigator.tabSetName().toLowerCase()){
            case "landing":
                return this.landingTab(); 
            case "guest":
                return(this.guestTab())
            case "moderator":
                return (this.moderatorTab());
            case "admin":
                return(this.adminTab())
            case "screener":
                return (this.screenerTab());
            case "inspector":
                return (this.inspectorTab());
            case "reviewer":
                return (this.reviewerTab());
            case "accountant":
                return (this.accountantTab());
            case "secretary":
                return (this.secretaryTab());
            case "public":
                return (this.publicTab());
            default:
                return this.paintRows();
        }
    }

    render(){
        if(this.state.data.layout == undefined){
            return []
        }
        if(this.state.labels.more.length==0){
            return []
        }
        return(
            <Container fluid className={"shadow mb-2 mt-1 bg-white"} style={{ minHeight:'80vh'}}>
                <Spinner />
                <SpinnerMain />
              {this.placeContent()}
            </Container>
        )
    }


}
export default Content
Content.propTypes={
    menu:PropTypes.oneOf(["landing","guest","admin", "moderator", "screener","reviewer","accountant","inspector","secretary", "public"]).isRequired,
    navigator:PropTypes.object.isRequired                                           //Navigator
}