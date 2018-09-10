app.service("orderService",function($http){
	
	this.save=function(order){
		return $http.post("./order/save",order);
	}
	
})