<!DOCTYPE html>
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/css?family=Open+Sans" />
	<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:500" >
<style>
body {font-family: Arial, Helvetica, sans-serif;}

/* Full-width input fields */
input[type=text], input[type=password] {
  width: 100%;
  padding: 12px 20px;
  margin: 8px 0;
  display: inline-block;
  border: 1px solid #ccc;
  box-sizing: border-box;
}

/* Set a style for all buttons */
button {
  width: 100%;
  height: 42px;
  background-color: #4285f4;
  color: #fff;
  cursor: pointer;
  padding: 14px 20px;
  margin: 8px 0;
  border-radius: 2px;
  border: none;
  box-shadow: 0 3px 4px 0 rgba(0,0,0,.25);
}

button:hover {
  opacity: 0.8;
}

ul {
    display: block;
    list-style-type: disc;
    margin-block-start: 1em;
    margin-block-end: 1em;
    margin-inline-start: 0px;
    margin-inline-end: 0px;
    padding-inline-start: 0px;
}

/* Extra styles for the cancel button */
.cancelbtn {
  width: auto;
  padding: 10px 18px;
  background-color: #f44336;
}

/* Center the image and position the close button */
.imgcontainer {
  text-align: center;
  margin: 24px 0 12px 0;
  position: relative;
}

img.avatar {
  width: 40%;
  border-radius: 50%;
}

.container {
  display : flex;
}
.first_flex{
	 flex: 2;
	 padding: 10px;
}

.languages{
	display: flex;
  	flex-flow: row-reverse wrap;
}
  
.language{
  	width:40px;
  	padding:5px
}

span.psw {
  float: right;
  padding-top: 16px;
}

/* The Modal (background) */
.modal {
  display: block; /* Hidden by default */
  position: fixed; /* Stay in place */
  z-index: 1; /* Sit on top */
  left: 0;
  top: 0;
  width: 100%; /* Full width */
  height: 100%; /* Full height */
  overflow: auto; /* Enable scroll if needed */
  background-color: rgb(0,0,0); /* Fallback color */
  background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
  padding-top: 60px;
}

/* Modal Content/Box */
.modal-content {
  background-color: #fefefe;
  margin: 5% auto 15% auto; /* 5% from the top, 15% from the bottom and centered */
  border: 1px solid #888;
  width: 30%; /* Could be more or less, depending on screen size */
}


/* Add Zoom Animation */
.animate {
  -webkit-animation: animatezoom 0.6s;
  animation: animatezoom 0.6s
}

@-webkit-keyframes animatezoom {
  from {-webkit-transform: scale(0)} 
  to {-webkit-transform: scale(1)}
}
  
@keyframes animatezoom {
  from {transform: scale(0)} 
  to {transform: scale(1)}
}

/* Change styles on extra small screens */
@media screen and (max-width: 300px) {
  span.psw {
     display: block;
     float: none;
  }
  .modal-content {
  	width: 80%; /* Could be more or less, depending on screen size */
  }
}

.google-btn {
  width: 100%;
  height: 42px;
  background-color: #4285f4;;
  cursor: pointer;
  cursor:hand;
  border-radius: 2px;
  box-shadow: 0 3px 4px 0 rgba(0,0,0,.25);
  }
.google-icon-wrapper {
    position: absolute;
    margin-top: 1px;
    margin-left: 1px;
    width: 40px;
    height: 40px;
    border-radius: 2px;
    background-color: #fff;
  }
.google-icon {
    position: absolute;
    margin-top: 11px;
    margin-left: 11px;
    width: 18px;
    height: 18px;
 }
  
 .btn-text {
    float: right;
    margin: 11px 11px 0 0;
    color: #fff;
    font-size: 14px;
    letter-spacing: 0.2px;
    font-family: "Roboto";
  }
  &:hover {
    box-shadow: 0 0 6px #4285f4;;
  }
  &:active {
    background: #1669F2;
  }



</style>
</head>
<body>

<div id="id01" >
	<form class="modal-content animate" >
     <div class="container">
	     <div class="first_flex">
	     	<h2>${application}</h2>
		 	<div class="languages">
			 	<#list languages?keys as key>
			 	   <div class="language">
			 	   		<a href="?lang=${key}">
						  	<img src="/api/public/flag?localeStr=${key}" width="30"/>
						  </a>
					</div> 
				</#list>
			</div>
		 </div>
	 </div>
    <div class="container">
		<div class="first_flex"">   
			<label><b>${oath2}</b></label>
			<ul>
				<#list providers?keys as key>
					<#if "Google"==key>
						<div class="google-btn" onclick="addcookie(); window.location.href='${providers[key]}'">
							<div class="google-icon-wrapper">
							<img class="google-icon" src="/img/google.png"/>
							</div>
							<label class="btn-text">${Google}</label>
						</div>
					<#else>
						<Button type="button" 
								onclick="window.location.href='${providers[key]};">
					  		${key}
						</Button>
					</#if>
				</#list>
			</ul>
			
		</div>
    </div>
    </form>
</div>
	<script type="text/javascript">
    	function addcookie(){
    		var h = window.location.hash;
    		document.cookie = "PDX2_SENDURL=" + encodeURIComponent(h);
	        location.reload();
    	}
    </script>
</html>
