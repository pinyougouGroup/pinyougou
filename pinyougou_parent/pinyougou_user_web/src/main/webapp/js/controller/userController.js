 //控制层 
app.controller('userController' ,function($scope,userService){	
	
	$scope.sendSmsCode=function(){
//		判断手机号的格式  正则表达式  /^1[3|4|5|6|7|8|9][0-9]{9}$/
//		/^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7,9]))\d{8}$/;
		var reg =  /^1[3|4|5|6|7|8|9][0-9]{9}$/;
		
		if(!reg.test($scope.entity.phone)){
			alert("手机号格式不正确！");
		}
		userService.sendSmsCode($scope.entity.phone).success(function(response){
			alert(response.message);
		})
		
	}
	
	$scope.register=function(){
//		判断两次密码是否一致
		if($scope.entity.password!=$scope.password2){
			alert("两次密码输入不一致");
			return;
		}
		
//		判断手机号的格式  正则表达式  /^1[3|4|5|6|7|8|9][0-9]{9}$/
//		/^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7,9]))\d{8}$/;
		var reg =  /^1[3|4|5|6|7|8|9][0-9]{9}$/;
		
		if(!reg.test($scope.entity.phone)){
			alert("手机号格式不正确！");
		}
		
		userService.add($scope.entity,$scope.code).success(function(response){
			if(response.success){
				location.href="home-index.html";
			}else{
				alert(response.message);
			}
			
		})
		
	}
	 
    
});	
