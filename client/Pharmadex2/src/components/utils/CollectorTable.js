import React , {Component} from 'react'
import PropTypes from 'prop-types'
import {Table, Input, Button,Row, Pagination, PaginationItem,PaginationLink,Col,Container, FormGroup} from 'reactstrap';
import Fetchers from './Fetchers';
import TableHeader from "./TableHeader"

/**
 * Common table implementation to use for Collector project
 * accepted such properties:
 * @property {Object} tableData - complex table data model, see Java implementation (REQ)
 * @property {function} loader() reload table data (REQ)
 * @property {function} selectRow(rowNumber)  user select row with number rowNumber from 0  by press on checkbox at the leftmost column
 * @property {String} headBackground - color of the header - red by default 
 * @property {function} linkProcessor(rowNo, cell) user click on a link in a row and a cell. Cell is cell itself! Complex object
 * 
 * @property {function} selectCell(row,cell) user click on checkbox or radio in a cell of any column
 * @property {function} selectAll() user click on checkbox at first column header
 * @property {function} [styleCorrector(header.key)] provides explicit important inline style to column with header.key given, i.e. {width:'20%'}
 * @property {function} repaint how to repaint this table
 */
class CollectorTable extends Component{
    constructor(props){
        super();
        this.state={
            check:false
        }
        this.paintHeaders = this.paintHeaders.bind(this)
        this.paintRows = this.paintRows.bind(this)
        this.paintCells = this.paintCells.bind(this)
        this.paintCell = this.paintCell.bind(this)
        this.headerAction=this.headerAction.bind(this)
        this.checkAll=this.checkAll.bind(this)
        this.isColumnVisible=this.isColumnVisible.bind(this)
        this.isLeftShift=this.isLeftShift.bind(this)
        this.isRightShift=this.isRightShift.bind(this)
        this.rightMostColumn=this.rightMostColumn.bind(this)
        this.changeCol=this.changeCol.bind(this)
         //colorizer
         this.headBackground = '#cc0000'
         if(typeof props.headBackground != 'undefined'){
             this.headBackground= props.headBackground
         }
         this.headColor=this.invertColor(this.headBackground,true)
    }
    /**
     * Calculate font color contrast to background
     * Allow calculate only black or white
     * Convert HEX to RGB
     * Invert the R,G and B components
     * Convert each component back to HEX
     * Pad each component with zeros and output.
     * @param {string} hex color 
     * @param {*} bw if true - only black or white
     */
    invertColor(hex, bw) {
        if (hex.indexOf('#') === 0) {
            hex = hex.slice(1);
        }
        // convert 3-digit hex to 6-digits.
        if (hex.length === 3) {
            hex = hex[0] + hex[0] + hex[1] + hex[1] + hex[2] + hex[2];
        }
        if (hex.length !== 6) {
            throw new Error('Invalid HEX color.');
        }
        var r = parseInt(hex.slice(0, 2), 16),
            g = parseInt(hex.slice(2, 4), 16),
            b = parseInt(hex.slice(4, 6), 16);
        if (bw) {
            // http://stackoverflow.com/a/3943023/112731
            return (r * 0.299 + g * 0.587 + b * 0.114) > 186
                ? '#000000'
                : '#FFFFFF';
        }
        // invert color components
        r = (255 - r).toString(16);
        g = (255 - g).toString(16);
        b = (255 - b).toString(16);
        // pad each with zeros and return
        return "#" + padZero(r) + padZero(g) + padZero(b);
    }

    /**
     * Sort or filter action on header given
     * @param {org.stoptb.collector.dto.tables.TableHeader} header 
     */
    headerAction(header){
        let table = this.props.tableData
        let index=-1;
        for(let i=0; i<table.headers.headers.length;i++){
            if(table.headers.headers[i].key== header.key){
                index=i
                break;
            }
        }
        if(index>-1){
            table.headers.headers[index] = header
        }
        this.props.loader()
    }
    /**
     * Chec/uncheck all
     * @param {Event} e
     */
    checkAll(e){
        let s = this.state
        s.check=!s.check
        this.setState(s)
        this.props.selectAll(this.state.check)
    }

    /**
     * Return true when this column should be on screen
     * @param {*} colNo column number from 0 
     */
    isColumnVisible(colNo){
        let headers = this.props.tableData.headers
        if(colNo<=headers.fixedLeft){
            if(colNo<headers.columns){
                return true
            }else{
                return false
            }
        }else{
            if(colNo>=headers.currentLeft && colNo<=this.rightMostColumn()){
                return true
            }else{
                return false
            }
        }
    }
    /**
     * should this column contain left shift function
     * @param colNo number of column
     */
    isLeftShift(colNo){
        let headers = this.props.tableData.headers
        if(colNo==headers.currentLeft && colNo != (headers.fixedLeft+1)){
            return (
                ()=>{
                    if(typeof this.props.repaint == 'function'){
                        let headers = this .props.tableData.headers
                        headers.currentLeft--
                        this.props.repaint()
                    }
                }
            )
        }else{
            return "";
        }
    }
    /**
     * Number of last right column
     */
    rightMostColumn(){
        let headers = this.props.tableData.headers
        if(headers.fixedLeft<headers.headers.length){
            let windowSize=headers.columns-1-headers.fixedLeft
            return headers.currentLeft+windowSize-1
        }else{
            return headers.headers.length-1
        }
    }

    /**
     * should this column contain right shift function
     * @param {number} colNo number of column
     */
    isRightShift(colNo){
        let headers = this.props.tableData.headers
        if(headers.length-1 < headers.fixedLeft){
            return "" //no shift at all
        }
        if(colNo == (headers.headers.length-1)){
            return "" //it is the last header
        }
        if(colNo==this.rightMostColumn()){
            return(
                ()=>{
                    if(typeof this.props.repaint == 'function'){
                        let headers = this .props.tableData.headers
                        headers.currentLeft++
                        this.props.repaint()
                    }
                }
            )
        }else{
            return ""
        }
    }

    /**
 * change table column places
 * @param {*} keyTo - id column, where to insert the draggable column
 * @param {*} keyFrom - id columns, which we rearrange
 */
changeCol(keyTo, keyFrom){
    //let s = this.state
    let indexTo=-1;
    let indexFrom=-1;
    let objTo = {};
    let objFrom = {};
    for(let i=0; i<this.props.tableData.headers.headers.length;i++){
        if(this.props.tableData.headers.headers[i].key== keyTo){
            indexTo=i;
        }
        if(this.props.tableData.headers.headers[i].key== keyFrom){
           indexFrom=i;
            objFrom = Object.assign({}, this.props.tableData.headers.headers[i]);
        }
    }
    
let j=-1
    if(indexFrom>indexTo){
        for(let i=indexFrom; i>indexTo;i--){
            j=i
            objTo=Object.assign({}, this.props.tableData.headers.headers[j-1]);
            this.props.tableData.headers.headers[i]=objTo;
        }
    }else{
        for(let i=indexFrom; i<indexTo;i++){
            j=i
            objTo=Object.assign({}, this.props.tableData.headers.headers[j+1]);
            this.props.tableData.headers.headers[i]=objTo;
        }
    }
    this.props.tableData.headers.headers[indexTo]=objFrom;
}

    /**
     * Paint headers with filter/sort capabilities
     */
    paintHeaders(){
        var ret =[];
        let i=0;
        if(this.props.tableData.selectable){
            if(typeof this.props.selectAll=="function"){
                ret.push(<th style={{width:'2em'}} className={"p-0 font-weight-light text-center align-middle"} key={'select'} >
                            <Input type="checkbox" className="m-0 p-0" value={this.state.check} onChange={this.checkAll}  />
                        </th>)
            }else{
                ret.push(<th style={{width:'2em'}}  key={'select'}></th>)
            }
        }
        this.props.tableData.headers.headers.forEach(element => {
                if(this.isColumnVisible(i)){
                    ret.push(<TableHeader
                        key={element.key} 
                        header={element}
                        leftShift={this.isLeftShift(i)} 
                        rightShift={this.isRightShift(i)}
                        styleCorrector={this.props.styleCorrector}
                        notImportantClasses={this.showUnImportantWhen}
                        action={this.headerAction}
                        headBackground={this.headBackground}
                        headColor={this.headColor}
                        loader={this.props.loader}
                        changeCol={this.changeCol}
                         />)
                }
                i++
            }
        );
        return ret;
    }
    
    /**
     * paint table's rows
     */
    paintRows(){
        var ret=[];
        if(Fetchers.isGoodArray(this.props.tableData.rows)){
            let rowNo=0;
            this.props.tableData.rows.forEach(row=>{
                if(Fetchers.isGoodArray(row.row)){
                    // since 2019-10-21 ret.push(<tr key={row.dbID} >{this.paintCells(row, rowNo, this.props.tableData.headers.headers)}</tr>);
                    ret.push(<tr key={rowNo} >{this.paintCells(row, rowNo, this.props.tableData.headers.headers)}</tr>);
                    rowNo++;
                }
            })
        }
        return ret;
    }
    /**
     * select the rightmost checkbox in a row
     * @param {event} e 
     * @param {number} rowNo 
     */
    onSelectRow(e,rowNo){
        this.props.selectRow(rowNo)
    }
    /**
     * Click on a cell
     * @param {event} e 
     * @param {number} rowNo 
     * @param {any} cell 
     */
    onSelectCell(e,rowNo,cell){
        if(typeof this.props.selectCell == 'function'){
            this.props.selectCell(rowNo, cell)
        }
    }
    /**
     * 
     * @param {event} e 
     * @param {number} rowNo 
     * @param {any} cell 
     */
    onLink(e, rowNo, cell){
        this.props.linkProcessor(rowNo, cell)
    }

    /**
     * Paint a cell depends on datatype and importance
     * @param {any} cell
     * @param {numeric} rowNo
     * @param {any} header 
     */
    paintCell(cell, rowNo, header){
        switch(header.columnType){ 
            case 2: //check
                return (
                    <Input className="m-auto p-auto"
                    type="checkbox" 
                    value={cell.originalValue}
                    checked={cell.originalValue} 
                    onChange={(e) => { this.onSelectCell(e,rowNo, cell.key) }} />
                )
            case 3: //radio
            return (
                <Input className="m-2 p-2"
                type="radio" 
                value={cell.originalValue}
                checked={cell.originalValue} 
                onChange={(e) => { this.onSelectCell(e,rowNo, cell) }} />
            )
            case 5:  //link
            case 10: //i18 link
            return(<Button style={{fontSize:'0.8rem', whiteSpace:'normal', textAlign:'left'}} className="btn-block  p-0 m-0" color="link" onClick={(e)=>{this.onLink(e,rowNo,cell)}}>{cell.value}</Button>)
            
            case 7: //number long
            return <div style={{textAlign:'right',width:'100%'}}>{cell.value}</div>
            default: //text, dates etc
                return cell.value

        }
    }
    /**
     * Paint a particular row
     * @param {Object} row
     * @param {number} rowNo 
     * @param {[]} headers 
     */
    paintCells(row, rowNo, headers){
        var ret=[];
        if(Fetchers.isGoodArray(row.row)){
            let i=0;
            if(this.props.tableData.selectable){
                ret.push(<td key={'select'}>
                <Input className="m-0 p-0" 
                    type="checkbox" 
                    value={row.selected}
                    checked={row.selected} 
                    onChange={(e) => { this.onSelectRow(e,rowNo) }} />
                </td>);
            }
            let index=0
            row.row.forEach(cell=>{
                if(this.isColumnVisible(i)){
                    if(headers[i].important){
                        ret.push(<td className={"p-1 m-0 "+ cell.styleClass} key={cell.key}>{this.paintCell(cell,rowNo,headers[i])}</td>);
                    }else{
                        ret.push(<td className= {this.showUnImportantWhen()+" "+cell.styleClass} key={cell.key}>{this.paintCell(cell,rowNo,headers[i])}</td>);
                    }
                }
                i++;
            })
        }
        return ret;
    }
    /**
     * When show unimportant columns
     */
    showUnImportantWhen(){
        return "d-none d-xl-table-cell p-1 m-0"
    }
    /**
     * ask to load page number pageNo
     * @param {Event} event 
     * @param {number} pageNo 
     */
    setPage(event, pageNo){
        let table = this.props.tableData
        table.headers.page=pageNo
        this.props.loader()
    }    
    paintPagination(){
        let ret=[]
        let pages = this.props.tableData.headers.pages;
        let page =  this.props.tableData.headers.page
        if(pages>1){
            ret.push(
                <PaginationItem key={"prev"}>
                    <PaginationLink previous onClick={(e)=>this.setPage(e,1)} />
                </PaginationItem>
            )

            if(pages<=12){
                for(let i = 1; i <= pages; i++){
                    ret.push(
                        <PaginationItem active={i==page} key={i}>
                            <PaginationLink onClick={(e)=>this.setPage(e,i)}>
                            {i}
                            </PaginationLink>
                        </PaginationItem>
                    )
                }
            }else{
                let startPage=page-3;
                if(startPage<1){
                    startPage=1
                }
                let endPage=page+3;
                if(endPage>pages){
                    endPage=pages;
                }
                for(let i = startPage; i <= endPage; i++){
                    ret.push(
                        <PaginationItem active={i==page} key={i}>
                            <PaginationLink onClick={(e)=>this.setPage(e,i)}>
                            {i}
                            </PaginationLink>
                        </PaginationItem>
                    )
                }
                if(endPage<pages){
                    ret.push(
                        <PaginationItem disabled key={".."}>
                                <PaginationLink>
                                {"..."}
                                </PaginationLink>
                            </PaginationItem>
                    )
                    ret.push(
                        <PaginationItem active={page==pages} key={pages}>
                            <PaginationLink onClick={(e)=>this.setPage(e,pages)}>
                                {pages}
                            </PaginationLink>
                        </PaginationItem>
                    )
                }
            }
            ret.push(
                <PaginationItem disabled={page==pages}key={"next"}>
                    <PaginationLink next onClick={(e)=>this.setPage(e,pages)} />
                </PaginationItem>
            )
        }
        
        
        return ret;
    }

    render(){
        let ret =[]
        if(typeof this.props.tableData != 'undefined' && Fetchers.isGoodArray(this.props.tableData.headers.headers)){
            ret =
            <Container style={{fontSize:'0.8rem'}} fluid  className="m-0 p-0"> 
                <Row className="m-0 p-0">
                    <Col xs="12" sm="12" lg="12" xl="12" className="m-0 p-0">
                    <Table style={{wordWrap:'break-word', tableLayout:'fixed'}} bordered hover>
                    <thead style={{backgroundColor: this.headBackground,  color: this.headColor,  textDecoration: 'underline', fontWeight: 'normal'}}>
                        <tr>
                            {this.paintHeaders()}
                        </tr>
                    </thead>
                    <tbody>
                        {this.paintRows()}
                    </tbody>
                    </Table>
                    </Col>
                </Row>
                <Row  className="m-0 p-0 d-print-none" >
                    <Col>
                    <Pagination className={"justify-content-center"}  style={{fontSize:"0.8rem"}} size="sm">
                    {this.paintPagination()}
                    </Pagination>
                    </Col>
                    <Col xs="auto">
                    <Row  className="m-0 p-0" className={"justify-content-center"} >
                    <Col xs="auto" style={{fontSize:'1rem', paddingRight:'0px', paddingLeft:'15px',color:'#007bff'}}>
                    <i className="fa fa-list"  aria-hidden="true"></i></Col>
                    <Col xs="auto">
                    <Input
                            style={{fontSize:"0.6rem"}}
                            bsSize="sm" type="select"
                            value={this.props.tableData.headers.pageSize}
                            onChange={(event)=>{
                                let headers = this.props.tableData.headers
                                headers.pageSize=event.target.value
                                this.headerAction(headers)
                            }}
                        >
                            <option>4</option>
                            <option>10</option>
                            <option>20</option>
                            <option>50</option>
                            <option>100</option>
                            <option>200</option>
                        </Input>
                        </Col>
                        </Row>
                    </Col>
                    </Row>
                
        </Container>
        }
        return(ret)
    }
}
export default CollectorTable

CollectorTable.propTypes={
    tableData: PropTypes.object.isRequired, //complex table data model, see Java implementation
    repaint:PropTypes.func,             // how to repaint this table
    selectRow:PropTypes.func,           //(rowNumber)  user select row with number rowNumber from 0  by press on checkbox at the leftmost column
    selectCell:PropTypes.func,          //(row,cell) user click on checkbox or radio in a cell of any column
    selectAll:PropTypes.func,           //user click on checkbox at first column header
    linkProcessor:PropTypes.func,       //(rowNo, cell) user click on a link in a row and a cell. Cell is cell itself! Complex object
    loader:PropTypes.func.isRequired,    // reload table data
    styleCorrector: PropTypes.func,     //(header.key) provides explicit important inline style to column with header.key given, i.e. {width:'20%'}
    headBackground:PropTypes.string       // - color of the header - red by default 
}