import React , {Component} from 'react'
import MDSpinner from 'react-md-spinner'
import '../../css/cover.css'
/**
 * Show yet another Ajax spinner on screen. Place it as first component on the page
 * To show/hide please use static methods show and hide
 */
class SpinnerMain extends Component{
    constructor( props, context){
        super( props, context);
        this.state={
            show:false,                     //show or hide the spinner
        }
        this.displayStyle = this.displayStyle.bind(this)
        this.controlSpinner = this.controlSpinner.bind(this)
    }

    /**
     * Show the spinner
     */
    static show(){
        SpinnerMain.createEvent(true)
    }
    /**
     * hide the spinner
     */
    static hide(spinnerControl){
        SpinnerMain.createEvent(false)
    }
    /**
     * generate an approprite event
     * @param {boolean} show
     */
    static createEvent(show){
        var event = new CustomEvent("ua.com.theta.spinner.main", {
            detail: {
                show: show     
            }
          });     
        window.dispatchEvent(event); 
    }


    /**
     * set the listener
     */
    componentDidMount(){
        window.addEventListener("ua.com.theta.spinner.main", event => this.controlSpinner(event.detail.show));  
    }

    /**
     * Spinner event listener
     * @param {boolean} show
     */
    controlSpinner(show){
        let s = this.state
        let oldVal=s.show
        s.show=show
        if(oldVal!=s.show){
            this.setState(s)
        }
    }


    /**
     * Generate show/hide style, depends on state.show value
     */
    displayStyle(){
        let ret ={  
            display:'none'
            }
        if(this.state.show){
            ret.display="flex"
        }
        return ret;
    }

    render(){
        return(
            <div className="cover align-middle align-items-center" style={this.displayStyle()}>
                <MDSpinner size={30} className={"mx-auto"}/>
            </div>
        )

    }
}
export default SpinnerMain