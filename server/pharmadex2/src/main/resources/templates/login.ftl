<!DOCTYPE html>
<html>
<head>
	<Cache-Control: no-cache>
	<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
	body {font-family: -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Oxygen,Ubuntu,Cantarell,"Fira Sans","Droid Sans","Helvetica Neue",sans-serif;}

/* Full-width input fields */
input:-webkit-autofill,
input:-webkit-autofill:hover, 
input:-webkit-autofill:focus, 
input:-webkit-autofill:active{
    -webkit-box-shadow: 0 0 0 30px white inset !important;
}
input[type=text], input[type=password]{
  width: 100%;
  padding: 5px 20px;
  margin: 15px 0;
  display: inline-block;
  border-top: 0;
  border-left: 0;
  border-right:0;
  border-bottom: 1px solid #696969;
  box-sizing: border-box;
  background-color: #F2F2F2;
  outline: none;
}

.btnlogin {
  background-color: #4285f4;
  color: white;
  padding: 5px 20px;
  margin: 15px 0;
  border: none;
  cursor: pointer;
  width: 50%;
  float:right;
  border-radius: 5px;
  box-shadow: 0 3px 4px 0 rgba(0,0,0,.25);
  display:block;
}


button:hover {
  opacity: 0.8;
}
.button {
		width: 100%;
		padding: 5px 0;
		margin: 15px 0;
		border: none;
		color: #0066FF;
		font-size: 15px;
		font-weight: 500;
		background-color: none;
		cursor:pointer;
		float:right;
		box-shadow: 0 3px 4px 0 rgba(0,0,0,.25);
  		display:block;
		:hover {
			box-shadow: 0 -1px 0 rgba(0, 0, 0, .04), 0 2px 4px rgba(0, 0, 0, .25);
		}
	}

/* Modal Content/Box */
.modal-content {
  background-color: #FFFFFF;
  margin: 5% auto 15% auto; /* 5% from the top, 15% from the bottom and centered */
  border: 1px solid #DBDBDB;
  border-radius: 5px;
  width: 35%; /* Could be more or less, depending on screen size */
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
  background-color: rgba(255,255,255,0.4);
}

/* Center the image and position the close button */
.imgcontainer {
  text-align: center;
  margin: 24px 0 12px 0;
  position: relative;
}

img.logo {
}

img.google{
	width:20px;
	margin-right: 10px;
}

img.envelopeBlue{
	width:23px;
	margin-right: 10px;
}
.container {
  padding: 10px 30px 60px 30px;
  margin: 0 40px 10px 40px;
  background-color:#F2F2F2; 
  display:block
}

span.psw {
  float: right;
  padding-top: 16px;
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


.googlebtn {
		width: 100%;
		padding: 12px 16px 12px 42px;
		border: none;
		border-radius: 3px;
		box-shadow: 0 -1px 0 rgba(0, 0, 0, .04), 0 1px 1px rgba(0, 0, 0, .25);
		  
		color: #0066FF;
		font-size: 15px;
		font-weight: 500;
		font-family: -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Oxygen,Ubuntu,Cantarell,"Fira Sans","Droid Sans","Helvetica Neue",sans-serif;
		background-color: #D9E1F2;
		align-items: center;
		cursor:pointer;
		
		&:hover {
			box-shadow: 0 -1px 0 rgba(0, 0, 0, .04), 0 2px 4px rgba(0, 0, 0, .25);
		}
	}
.lineor {
	margin: 10px 70px 10px 70px;
	display: flex;
}

	.line {
		top: 0.45em;
		height: 1px;
		flex-shrink: 1;
		position: relative;
		flex-grow: 1;
		background-color: #696969;
	}
	
	.or {
		padding-top: 0;
		margin-top: 0;
		margin-bottom: 0;
		border-bottom-width: 0;
		padding-bottom: 0;
		text-transform: uppercase;
		margin-right: 18px;
		border-left-width: 0;
		padding-left: 0;
		flex-direction: column;
		border-right-width: 0;
		box-sizing: border-box;
		border-top-width: 0;
		display: flex;
		padding-right: 0;
		flex-shrink: 0;
		margin-left: 18px;
		font-weight: 600;
		color: #737373;
		align-items: stretch;
		font-size: .8125rem;
		position: relative;
		line-height: 1.1538;
		flex-grow: 0;
		vertical-align: baseline;
	}
.success{
	color:#696969;
}
.failure{
	color:red;
}

.messbox{
	padding:15px 0;
	height:75px;
}

/* Change styles for span and cancel button on extra small screens */
@media screen and (max-width: 500px) {
  span.psw {
     display: block;
     float: none;
  }
  
  .modal-content {
	  background-color: #FFFFFF;
	  margin: 5% auto 15% auto; /* 5% from the top, 15% from the bottom and centered */
	  border: 1px solid #DBDBDB;
	  border-radius: 5px;
	  width: 80%; /* Could be more or less, depending on screen size */
	}
}
</style>
</head>
<body>
	<script>
		window.onsubmit = function(){
			addcookie('username');
		}
		window.onload = function(){
			setTimeout(function()
					{
						document.getElementById("password").value=''
					}, 500);
		}
		
		const createpincode = function(){
			let data={
				email:document.getElementById('username').value,
				tp:''
			}
				
				fetch('/api/public/temporary/password' , {
						credentials: 'include',
						method: 'POST',          
						headers: {
							'Accept': 'application/json',
							'Content-Type': 'application/json'              
						},           
					body:JSON.stringify(data)
					})      
				  .then((response)=>{            
						if (response.ok) { 
							let resp = response.json()
							return(resp);              
						}else{
							document.getElementById("message").innerHTML="<span class='failure' >Unrecognized system error"+ error+"</span>"
						}
				  })
				  .then((resp)=>{
							let clazz='success'
							if(!resp.valid){
								clazz='failure'
							}
							 document.getElementById("message").innerHTML="<span class="+clazz+">"+resp.identifier+"</span>"
							}								
				  )
				  .catch((error)=>{
							document.getElementById("message").innerHTML="<span class='failure'>bad responce.identifier "+ error+"</span>"
				} ) 
			}
		
   </script>  
	<script type="text/javascript">
    	function addcookie(cookiename){
			if(cookiename == 'username'){
				document.cookie = cookiename + "=" + document.getElementById('username').value + ";max-age=8640";
			}else{
				var h = window.location.hash;
				document.cookie = cookiename + "=" + encodeURIComponent(h);
			}
		}
    </script>

<div id="id01" class="modal">
  <form class="modal-content animate" action="login" method="post">
	<div class="imgcontainer">
	  <img src="/img/LogoOpenRIMS.svg" class="logo" />
    </div>
	<div style="margin: 20px 40px 0 40px;" >
		<Button type="button" class="googlebtn"
				onclick="window.location.href = '${googlelink}'">
        	<img src="/img/google.svg" class="google"/>
            ${google}
         </Button>   
    </div>
	<div class="lineor">
		<div class="line"></div>
		<div class="or">${lblor}</div>
		<div class="line"></div>
	</div>
	<div class="container">
		<input type="text" id="username"  placeholder="${emailplaceholder}" name="username" required value=${useremail}>

		<input type="password" id="password" class="inputfld" placeholder="${pswdplaceholder}" name="password" >
        
		<Button type="submit" class="button">
			<img src="/img/lock.svg" class="envelopeBlue" />
			${login}
		</Button>
	<br/>
		<Button type="button" class="button" onClick="createpincode()" >
			<img src="/img/email2.svg" class="google" />
			${get_password}
		</Button>
        <br/>
        <div id="message" class="messbox"/>
		<br/>
    </div>
  </form>
</div>

<script>
			var h = window.location.hash;
    		window.document.cookie = "PDX2_SENDURL=" + encodeURIComponent(h)+";path=/"
	 </script>
</body>

</html>
