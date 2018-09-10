 //控制层 
app.controller('homeController' ,function($scope,userService){	
	
	 $scope.showName=function(){
		 userService.showName().success(function(response){
			 $scope.username=JSON.parse(response);
			 
		 })
		 
	 }
	 
    
});	
