import React from 'react'
import ReactDom from 'react-dom'
import Pharmadex from '../components/Pharmadex'
import '../sass/custom.scss'
import '@fortawesome/fontawesome-free/css/all.min.css'
/**
 * Visual component URI should be represented as /tabset#tab,component,params
 * tabset is a security domain, like /admin, /moder, etc. Controlled by the server, typically WebSecurity.java
 *   the security is enforsed by API calls like /api/tabset
 * tab is a main menu item (historically it is a tab on the tabset)
 * component is a ReactJS component that in this particular case shpuld be loaded on the tab (for the menu item)
 * params is a string that the component can interpretate, e.g. ID in the database 
 */ 

// place the tabset
ReactDom.render(<div>                                 
                   <Pharmadex />
                </div>, document.getElementById('app'));