//服务层
app.service('orderService',function($http){
	    	
	
	this.findOrderList=function(){
		return $http.get('../order/findOrderList');
	}
	this.findOrderBySelectStatus=function(orderstat){
		return $http.post('../order/findOrderBySelectStatus',orderstat);
	
	}
	this.findOrderByDataSelect=function(date){
		return $http.get('../order/findOrderByDataSelect/'+date);
	}
	
});

		
		