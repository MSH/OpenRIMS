<!DOCTYPE html>
<html>
<head>
<#if ""!= ga4>
	<!-- Google tag (gtag.js) --> 
	<script async src="https://www.googletagmanager.com/gtag/js?id=${ga4}"></script> 
	<script> 
	window.dataLayer = window.dataLayer || [];
	 function gtag(){dataLayer.push(arguments);} gtag('js', new Date()); 
	 gtag('config', '${ga4}'); 
	 </script>
 </#if>
	<title>${title}</title>
  	<meta charset="UTF-8">
  	<meta content="width=device-width, initial-scale=1, shrink-to-fit=no"  name="viewport">
</head>
  
<body>  
   <div id="app"></div>  
   	<script src=${scriptBundle}></script>
  </body>
</html>