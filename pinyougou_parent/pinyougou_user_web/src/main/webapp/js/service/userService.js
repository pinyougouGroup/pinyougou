app.service("userService",function($http){
	
	this.sendSmsCode=function(phone){
		return $http.get("./user/sendSmsCode/"+phone);
	}
	
	this.add=function(user,code){
		return $http.post("./user/add/"+code,user);
	}
	
	this.showName=function(){
		return $http.post("./user/showName");
	}
})