app.service("yeeBaopayService",function($http){
	
	this.findOrderByUserId=function(){
		return $http.get("./yeePay/findOrderByUserId");
	}
	 
	this.confirmOrder=function(pd_FrpId){
		return $http.post("./yeePay/confirmOrder/"+pd_FrpId);
	}
	
})