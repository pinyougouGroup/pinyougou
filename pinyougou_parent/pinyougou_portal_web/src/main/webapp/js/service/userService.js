app.service("userService",function($http){
	
	this.showName=function(){
		return  $http.get("../user/showName");
	}
	
	
})