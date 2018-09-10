app.service("payService",function($http){
	
	this.createNative=function(){
		return $http.get("./pay/createNative");
	}
	 
	this.queryOrder=function(out_trade_no){
		return $http.get("./pay/queryOrder/"+out_trade_no);
	}
	
})