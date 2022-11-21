import React , {Component} from 'react'
import PropTypes from 'prop-types'
import {Container, Row, Col} from 'reactstrap'
import UserNotAuthMenu from './UserNotAuthMenu'
import UserAuthMenu from './UserAuthMenu'
import UserGuestMenu from './UserGuestMenu'

/**
 * The header. Contains common for any page actions
 * 
 */
class CommonHeader extends Component{
    constructor(props){
        super(props)
        this.userMenu=this.userMenu.bind(this)
    }

    /**
     * return user's menu depends on property "menu"
     */
    userMenu(){
        switch(this.props.menu){
            case "landing":
                return (<UserNotAuthMenu />)
            case "guest":
                return (<UserGuestMenu />)
            case "admin":
                return (<UserAuthMenu />)
            case "moderator":
                return (<UserAuthMenu />)
            case "screener":
                return (<UserAuthMenu />)
            case "reviewer":
                return (<UserAuthMenu />)
            case "inspector":
                return (<UserAuthMenu />)
            case "accountant":
                return (<UserAuthMenu />)
            case "secretary":
                return (<UserAuthMenu />)
        }
    }
    render(){

        return(
            <Container fluid className="bg-dark">
                <Row>
                    <Col xs='10' sm='10' lg='2' xl='2' className="d-flex justify-content-start p-0 d-print-none">
                        <img src="api/public/headerlogo" height={60}/>
                    </Col>
                    <Col xs='2' sm='2' lg='10' xl='10' className="d-flex justify-content-end">
                        {this.userMenu()}
                    </Col>
                </Row>
            </Container>
        )
    }


}
export default CommonHeader
CommonHeader.propTypes={
    menu:PropTypes.oneOf(["landing","guest","admin", "moderator", "screener","reviewer","accountant","inspector","secretary"]).isRequired,      //tabset
    navigator:PropTypes.object.isRequired                                           //Navigator
}