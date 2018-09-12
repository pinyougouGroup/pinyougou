<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html xmlns:wb="http://open.weibo.com/wb">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script src="//tjs.sjs.sinajs.cn/open/api/js/wb.js?appkey=2042476782" type="text/javascript" charset="utf-8"></script>
<title>重定向页面</title>
<script type="text/javascript">

	function redirect() {
		var retUrl = "${retUrl}";
		//var retUrl=http://localhost:9980/jsp/sns/redirect.jsp?state=register&code=df6ec661f5fbb4e152ca9209708d2bd4
		if (retUrl != null && retUrl != "-1") {
			document.getElementById("msgDiv").style.display = "block";
			
			 if("${retUrl}"!=""){
					location.href ="http://192.168.181.70:8083/index.html";
					window.close();
			}	 
			 //window.opener.g_snsCallback();
			var flag=${empty param.state};
			//var addr="";
			if(!flag){ 
				window.opener.location.href ="http://192.168.181.70:9980/sinalogin/sns?t=sina";
				window.close();
			} else{
			/* 	http://localhost:9980/jsp/sns/redirect.jsp?code=06fe140ccfdd4fe44cd4174046d5d808 */
				location.href ="http://192.168.181.70:9980/sinalogin/callback/sina?code=${param.code}";
				
			}
			
		} else {
			//document.getElementById("failDiv").style.display = "block";
			location.href ="http://192.168.181.70:8083/index.html";
		}
	}

	</script>
</head>

<body onload="redirect()">
	<div id="msgDiv" style="display: none">
		页面长时间未跳转，<a href="index.jsp" onclick="redirect()">点击此处</a>
	</div>
	<div id="failDiv" style="display: none;">
		登录失败，请关闭此页面重试，<a href="javascript:window.open('','_self').close();">关闭</a>
	</div>
	
</body>
</html> 
