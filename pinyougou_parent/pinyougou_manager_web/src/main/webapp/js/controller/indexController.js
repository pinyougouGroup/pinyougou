app.controller("indexController",function($scope,userService){
	$scope.showName=function(){
		userService.showName().success(function(response){
			$scope.username=JSON.parse(response);
		})
		
	}
	
	
	
})