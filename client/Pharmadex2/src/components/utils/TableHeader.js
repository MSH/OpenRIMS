import React , {Component} from 'react'
import {Popover, PopoverBody, Button, Row, Col,} from 'reactstrap';
import DateFilter from './DateFilter'
import TextFilter from './TextFilter'

/**
 * Header for CollectorTable column
 * Display, Sort, filter, dragDrop
 * @property {Object} header - header data, see org.stoptb.collector.dto.tables.TableHeader
 * @property {function} leftShift - function that process left sheet of columns area, if undefined - no shift in this column
 * @property {function} rigthShift - function that process right shift of columns area, is undefined, no shift in this column
 * @property {Boolen} disabled
 * @property {function} notImportantClasses must return string that represents list of classes for not important like "d-none d-xl-table-cell"
 * @property {function(Header)} action - function action(header) when header asks for sort or filter
 * @property {function(Header)} dragDropCol(header) user drag and drop column
 * @property {function} styleCorrector(header.key) - who will adjust default style
 * @property {String} headBackground
 * @property {function} changeCol(header.key, event.headerKey) - change column this header table
     
 }}
 */
class TableHeader extends Component{
    constructor(props){
        super(props)
        this.state={
           popoverOpened:false,
           headerWithFilter:this.props.header
        }
        this.headerId = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(2, 10);
        this.inlineStyle=this.inlineStyle.bind(this)
        this.headerContent=this.headerContent.bind(this)
        this.togglePopover=this.togglePopover.bind(this)
        this.createSortArrows=this.createSortArrows.bind(this)
        this.noSort=this.noSort.bind(this)
        this.descSort=this.descSort.bind(this)
        this.ascSort=this.ascSort.bind(this)
        this.additionTokens=this.additionTokens.bind(this)
        this.createFilter=this.createFilter.bind(this)
        this.createHeaderId=this.createHeaderId.bind(this)
        this.leftShiftButton=this.leftShiftButton.bind(this)
        this.rightShiftButton=this.rightShiftButton.bind(this)
        //colorizer
        this.headBackground = this.props.headBackground
        this.headColor = this.props.headColor
    }

    /**
     * 
     * @param {string} key - column header key
     */
    inlineStyle(key){
        let style={}
        if(typeof this.props.styleCorrector == 'function'){
            style = this.props.styleCorrector(key);
            
        }
        return Object.assign({position:"sticky",top:0},style)
    }
    noSort(){
        let header = this.state.headerWithFilter
        header.sortValue=0
        this.togglePopover()
        this.props.action(header)
    }
    descSort(){
        let header = this.state.headerWithFilter
        header.sortValue=1
        this.togglePopover()
        this.props.action(header)
    }
    ascSort(){
        let header = this.state.headerWithFilter
        header.sortValue=2
        this.togglePopover()
        this.props.action(header)
    }
    /**
     * create group of sort arrows
     */
    createSortArrows(){
        let ret=[]
        switch(this.props.header.sortValue){
            case 0:
            ret.push(
                <small key="1" className="p-0 m-0 mr-2 fas fa-times"/>)
                ret.push(<Button key="2" color='link' onClick={this.descSort} className="p-0 m-0 mr-2"><i className="fas fa-arrow-down"/></Button>)
                ret.push(<Button key="3" color='link' onClick={this.ascSort} className="p-0 m-0 mr-2"><i className="fas fa-arrow-up"/></Button>)
                break;
            case 1:
                ret.push(<Button key="1" color='link' onClick={this.noSort} className="p-0 m-0 mr-2"><i className="fas fa-times"/></Button>)
                ret.push(<small key="2" className="p-0 m-0 mr-2 fa fa-arrow-down"/>)
                ret.push(<Button key="3" color='link' onClick={this.ascSort} className="p-0 m-0 mr-2"><i className="fas fa-arrow-up"/></Button>)
                break;
            case 2:
                ret.push(<Button key="1" color='link' onClick={this.noSort} className="p-0 m-0 mr-2"><i className="fas fa-times"/></Button>)
                ret.push(<Button key="2" color='link' onClick={this.descSort} className="p-0 m-0 mr-2"><i className="fas fa-arrow-down"/></Button>)
                ret.push(<small key="3" className="p-0 m-0 mr-2 fas fa-arrow-up"/>)
            default:
        }

        return ret
    }
    /**
     * Additional tokens to header text - is this sorted, is this filtered
     */
    additionTokens(){
        let ret=[]

        if(this.props.header.sortValue==2){
            ret.push(<span key="2" style={{fontSize:'0.5rem'}} className="fas fa-arrow-up"/>)
        }
        if(this.props.header.sortValue==1){
            ret.push(<span key="1" style={{fontSize:'0.5rem'}} className="fas fa-arrow-down"/>)
        }
        if(this.props.header.filterActive){
            ret.push(<span key="3" style={{fontSize:'0.5rem'}} className="fas fa-filter"/>)
        }


        return ret
    }
    /**
     * Create a filter
     */
    createFilter(){
        if(this.props.header.filterAllowed){
            switch(this.props.header.columnType){
                case 4:
                case 6:
                let dates={
                    from:this.state.headerWithFilter.from,
                    to:this.state.headerWithFilter.to,
                    on:false
                }
                return(
                    <DateFilter dates={dates} onChange={(retDates)=>{
                        let s = this.state
                        s.headerWithFilter.from=retDates.from
                        s.headerWithFilter.to=retDates.to
                        s.headerWithFilter.filterActive=retDates.on
                        s.popoverOpened=false
                        this.setState(s)
                        this.props.action(s.headerWithFilter)
                    }}/>
                )
                case 0:
                case 5:
                    return(
                        <TextFilter
                            value={this.state.headerWithFilter.conditionS}
                            onChange={(value)=>{
                                let s = this.state
                                s.headerWithFilter.conditionS=value
                                s.headerWithFilter.filterActive=value.length>0
                                s.popoverOpened=false
                                this.setState(s)
                                this.props.action(s.headerWithFilter)
                            }
                            }
                        />
                    )
                    
                default:
                    return [];
            }
        }else{
            return []
        }
    }
    /**
     * Create unique id for this header
     * @param {String} key 
     */
    createHeaderId(key){
        let pre=key
        pre = this.replaceWrong(pre)
        return pre+this.headerId
    }

    replaceWrong(key){
        let len=key.length
        let pre = key.replace(".","")
        pre = pre.replace("(","")
        pre = pre.replace(")","")
        pre = pre.replace(",","")
        pre = pre.replace("-","")
        pre = pre.replace(" ","")
        pre = pre.replace("_","")
        if(pre.length==len){
            return pre
        }else{
            this.replaceWrong(pre)
        }
    }

    /**
     * Paint right shift button for a column
     */
    rightShiftButton(){
        let ret=[]
        if(typeof this.props.rightShift =='function' ){
            ret.push(
                <Button color="link" key="4"
                style={{backgroundColor: this.headBackground,  color: this.headColor, fontSize:'0.7rem'}}
                className={"p-0 m-0"}
                onClick={this.props.rightShift}>
                    <i className="fa fa-angle-double-right fa-lg"></i>
                </Button>
            )
        }
        return ret
    }

    /**
     * Paint left shift button for a column
     */
    leftShiftButton(){
        let ret=[]
        if(typeof this.props.leftShift =='function' ){
            ret.push(
                <Button color="link" key="0"
                style={{backgroundColor: this.headBackground,  color: this.headColor, fontSize:'0.7rem'}}
                className={"p-0 m-0"}
                onClick={this.props.leftShift}>
                    <i className="fas fa-angle-double-left fa-lg"></i>
                </Button>
            )
        }
       return ret;
    }

    /**
     * return header content inside the cell
     * @param {Object} header - see org.stoptb.collector.dto.tables.TableHeader
     */
    headerContent(header){
        if(header.sort || header.filterAllowed){
            return(
                <div id={this.createHeaderId(header.key)}
                //onDrop={this.drop}// onDragOver={this.allowDrop(event)}
                draggable={typeof this.props.loader =='function'}
                onDragStart={(event)=>{
                    event.headerKey=header.key
                    event.dataTransfer.setData("text/plain",header.key)
                }}
                onDragOver={(event)=>{
                    event.preventDefault();
                }}
                onDrop={(event)=>{
                    if(typeof this.props.loader =='function'){
                    event.preventDefault();
                    //this.props.changeCol(header.key, event.headerKey)
                    let headerKey=event.dataTransfer.getData("text/plain")
                    this.props.changeCol(header.key,headerKey)
                    this.props.loader();
                    }
                }}
                >
                <div style={{display:'flex',flexWrap:'noWrap'}}>
                    {this.leftShiftButton()}
                    <Button id={this.createHeaderId(header.key)} color="link"
                    style={{backgroundColor: this.headBackground,  color: this.headColor,  whiteSpace:'normal', fontSize:'0.7rem'}}
                    onClick={this.togglePopover} className={"btn-block p-0 m-0"}>
                        {header.displayValue} 
                    </Button>
                    {this.rightShiftButton()}
                </div>
                {this.additionTokens()}
                <Popover placement="right" 
                trigger="legacy"
                isOpen={this.state.popoverOpened}
                target={this.createHeaderId(header.key)} 
                toggle={this.togglePopover}
                style={{backgroundColor:'lightgoldenrodyellow'}} >
                    <PopoverBody className="ml-4 mr-4">
                        <Row>
                            <Col className="m-0 p-0" className="text-center">
                            {this.createSortArrows()}
                            </Col>
                        </Row>
                        {/* <Row>
                            <hr className="m-0 p-0 col-xs-12" style={{width: '100%', color: 'gray', height: '0.01rem', backgroundColor:'gray'}} />
                        </Row> */}
                       <Row style={{marginLeft:'1rem', marginRight:'1rem', marginTop:'1rem', borderTop: '0.01rem solid gray',borderBottom: '0.01rem solid gray'}}>
                            {this.createFilter()}
                       </Row>
                       {/* <Row >
                            <hr className="m-0 p-0 col-xs-12" style={{width: '100%', color: 'gray', height: '0.01rem', backgroundColor:'gray'}} />
                        </Row> */}
                    </PopoverBody>
                </Popover>
                </div>
            )
        }else{
            return (
                <div style={{display:'flex',flexWrap:'noWrap'}}>
                    {this.leftShiftButton()}
                    <Button id={this.createHeaderId(header.key)} color="link"
                    style={{backgroundColor: this.headBackground,  color: this.headColor,  whiteSpace:'normal', fontSize:'0.7rem'}}
                    className={"btn-block p-0 m-0"}>
                        {header.displayValue} 
                    </Button>
                    {this.rightShiftButton()}
                </div>
                );
        }
    }
    /**
     * Ask sort or filter action 
     */
    togglePopover(){
        let s = this.state
        s.popoverOpened= !s.popoverOpened
        this.setState(s)
    }

    paintHeader(){
        let element = this.props.header
        if(element.important){
            return(<th style={this.inlineStyle(element.key)} key={element.key} className={"p-0 font-weight-light text-center align-middle"}>{this.headerContent(element)}</th>);
        }else{
            return(<th style={this.inlineStyle(element.key)} className={"p-0 font-weight-light text-center align-middle " + this.props.notImportantClasses()} key={element.key}>{this.headerContent(element)}</th>);
        }
    }
    render(){
        return(
                this.paintHeader()
        )
    }
}

export default TableHeader