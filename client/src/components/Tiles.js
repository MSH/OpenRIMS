import React , {Component} from 'react'
import {Container, Row, Col,Label,Alert} from 'reactstrap'
import Locales from './utils/Locales'
import Fetchers from './utils/Fetchers'
import ButtonUni from './form/ButtonUni'
import Dictionary from './Dictionary'
import RootNode from './RootNode'
import DictNode from './DictNode'
import Pharmadex from './Pharmadex'
import TileImage from './TileImage'
import Navigator from './utils/Navigator'

/**
 * The form to join to community
 * Allows to fill form and, then, collaborate with NMRA by it
 */
class Tiles extends Component{
    constructor(props){
        super(props)
        this.state={
            alertColor:"info",
            saved:false,
            editdict:false,
            help:false,
            data:{},
            labels:{
                cancel:'',
                global_submit:'',
                save:'',
                global_help:'',
                saved:'',
                titleTiles:''
            }
        }
        this.load=this.load.bind(this)
        this.dictionary=this.dictionary.bind(this)
        this.update=this.update.bind(this)
        this.reloadpage=this.reloadpage.bind(this)
        this.createContent=this.createContent.bind(this)
        this.createFreeTiles=this.createFreeTiles.bind(this)
        this.changeCol=this.changeCol.bind(this)
        this.eventProcessor=this.eventProcessor.bind(this)
    }

     /**
     * listen for askData broadcast and getData only to own address
     */
    eventProcessor(event){
        let data=event.data
        if(data.to=="*" ){
            if(data.subject=="onSelectionChange"){
                this.state.buffer=data.data
                this.state.data.dictionary = this.state.buffer

                //let id = data.data.pathSelected.id
                if(data.data.path.length==1 ){
                    this.state.data.editForm = true
                    this.load()
                }else{
                    this.state.data.editForm = false
                    this.setState(this.state)
                }
            }
            if(data.subject=="onSaveData"){
                this.state.buffer=data.data
                this.state.data.dictionary = this.state.buffer

                //let id = data.data.pathSelected.id
                if(data.data.path.length==1 ){
                    this.state.data.editForm = true
                    this.load()
                }else{
                    this.state.data.editForm = false
                    this.setState(this.state)
                }
            }
        }
        
        
    }

    componentDidMount(){
        window.addEventListener("message",this.eventProcessor)
        this.load()
    }

    componentWillUnmount(){
        window.removeEventListener("message",this.eventProcessor)
    }
    /**
     * place a dictionary to a column
     * @param {object DictionaryDTO} dict 
     */
    dictionary(){
        let dict = this.state.data.dictionary
        if(this.state.editdict){
            return(
                <Col xs='12' sm='12' lg='12' xl='12' key={dict.url}>
                    <RootNode
                        identifier={dict.url}
                        rootId={dict.urlId}
                        onCancel={()=>{
                            this.state.editdict=false
                            this.state.add=false
                            this.load()
                        }}
                    />
                </Col>
            )
        }else{
            return(
                <Col xs='12' sm='12' lg='12' xl='12' key={dict.url}>
                    <Dictionary identifier={dict.url} data={dict} />
                </Col>
            )
        }
    }
    /**
     * перемещает тайл из списка в список
     * @param {*} keyFrom - индексы тайла от куда забирают
     * @param {*} keyTo - индексы тайла куда добавляют
     * @param {*} fromEmpty - true - если перетаскивают из неразмеченной области,
     *      false - если меняют местами уже размеченные тайлы.
     * ПЕРЕТАСКИВАТЬ ИЗ РАЗМЕЧЕННОЙ ОБЛАСТИ В НЕ РАЗМЕЧЕННУЮ НЕЛЬЗЯ!!!
     */
    changeCol(keyFrom, keyTo){
        /*keyFrom = "empty"
        keyFrom = "title"
        keyFrom = "numRow&numCol"
        ----
        keyTo = "numRow&numCol"
        */

        if(keyFrom == "empty"){//keyFrom = "empty"

        }else {
            let masFrom = keyFrom.split("&")
            if(masFrom.length == 2){//keyFrom = "numRow&numCol" && keyTo = "numRow&numCol"
                let masTo = keyTo.split("&")
                if(masTo.length == 2){
                    let layout = this.state.data.layout
                    let titleFrom = layout[masFrom[0]].cells[masFrom[1]].variables[0]
                    let titleTo = layout[masTo[0]].cells[masTo[1]].variables[0]
                    let tileFrom = this.state.data.tiles[titleFrom]
                    let tileTo = this.state.data.tiles[titleTo]

                    if(tileFrom != undefined && tileTo != undefined){
                        let rowFrom = tileFrom.numRow
                        let colFrom = tileFrom.numCol

                        tileFrom.numRow = tileTo.numRow
                        tileFrom.numCol = tileTo.numCol

                        tileTo.numRow = rowFrom
                        tileTo.numCol = colFrom
                    }
                }
            }else{ //keyFrom = "title" && keyTo = "numRow&numCol"
                let masTo = keyTo.split("&")
                if(masTo.length == 2){
                    let tile = this.state.data.tiles[keyFrom]
                    tile.free=false
                    tile.numRow = masTo[0]
                    tile.numCol = masTo[1]
                }
            
            }
        }
    }

    createContent(){
        if(this.state.data.editForm){
            let ret=[]
            let layout = this.state.data.layout
            let tiles = this.state.data.tiles
            delete tiles.justloaded
                layout.forEach((row, index)=>{
                    let cells = row.cells
                    if(Fetchers.isGoodArray(cells)){
                        let columns=[]
                        cells.forEach((cell, j)=>{
                            columns.push(
                                <Col key={j} xs='12' sm='12' lg='6' xl='4'>
                                    <TileImage content={tiles[cell.variables[0]]} labels={this.state.labels}
                                        changeCol={this.changeCol} loader={this.update}/>
                                </Col>
                            )
    
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
        return;
    }

    createFreeTiles(){
        if(this.state.data.editForm){
            let ret=[]
            let layout = this.state.data.layoutByFree
            let tiles = this.state.data.tiles
            delete tiles.justloaded
                layout.forEach((row, index)=>{
                    let cells = row.cells
                    if(Fetchers.isGoodArray(cells)){
                        let columns=[]
                        cells.forEach((cell, j)=>{
                            columns.push(
                                <Col key={j} xs='12' sm='12' lg='12' xl='6'>
                                    <TileImage content={tiles[cell.variables[0]]} labels={this.state.labels}
                                        changeCol={this.changeCol} loader={this.update}/>
                                </Col>
                            )

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

            return(
                <Container fluid className={Pharmadex.settings.activeBorder}>
                    {ret}
                </Container>
            )
        }
        return ;
    }

    load(){
        Fetchers.postJSON("/api/admin/tiles", this.state.data, (query,responce)=>{
            this.state.data=responce
            delete this.state.data.tiles.justloaded
            this.setState(this.state)
            Locales.resolveLabels(this)
        })
    }

    update(reloadDict){
        if(reloadDict == undefined)
            reloadDict = true
        Fetchers.postJSON("/api/admin/tiles/update", this.state.data,(query,result)=>{
            this.state.data=result
            if(reloadDict){
                this.state.data.dictionary.reload = true
            }
            this.setState(this.state)
        } )
    }

    reloadpage(){
        this.state.data.editForm = false
        this.state.buffer.path = []
        this.state.data.dictionary = this.state.buffer
        this.update(true)
    }
    
    render(){
        if(this.state.labels.locale == undefined){
            return []
        }
        let showForm = this.state.data.editForm && Object.keys(this.state.data.tiles).length > 0

        DictNode.rewriteLabels(this.state.data.node, this)
        return(
            <Container fluid>
                <Row>
                    <Col xs='12' sm='12' lg='4' xl='4'>
                        <Row>
                            {this.dictionary()}
                        </Row>
                        <Row hidden={!showForm}>
                            <Col xs='12' sm='12' lg='12' xl='12'>
                                {this.createFreeTiles()}
                            </Col>
                        </Row>
                    </Col>
                    <Col xs='12' sm='12' lg='8' xl='8'>
                        <Container fluid className={Pharmadex.settings.activeBorder} hidden={!showForm}>
                            <Row>
                                <Col xs='12' sm='12' lg='6' xl='4'>
                                    <h4>
                                        {this.state.labels.titleTiles}
                                    </h4>
                                </Col>
                                <Col xs='12' sm='12' lg='4' xl='4'>
                                    <ButtonUni 
                                        label={this.state.labels.save}
                                        onClick={()=>{
                                            Fetchers.postJSONNoSpinner("/api/admin/tiles/save", this.state.data, (query,result)=>{
                                                Navigator.message('*', '*', 'show.alert.pharmadex.2', this.state.labels.saved)
                                                this.reloadpage()
                                            })
                                        }}
                                        color="primary"
                                    />
                                </Col>
                                <Col xs='12' sm='12' lg='4' xl='4'>
                                    <ButtonUni
                                        label={this.state.labels.cancel}
                                        color='secondary'
                                        onClick={()=>{
                                            this.reloadpage()
                                        }}
                                    />
                                </Col>
                            </Row>
                            <Row>
                                <Col xs='12' sm='12' lg='12' xl='12'>
                                    <Alert hidden={!this.state.saved} color={this.state.alertColor} className="p-0 m-0">
                                        <small>{this.state.labels.saved}</small>
                                    </Alert>
                                </Col>
                            </Row>
                            <br/>
                            {this.createContent()}
                        </Container>
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default Tiles
Tiles.propTypes={
    
}