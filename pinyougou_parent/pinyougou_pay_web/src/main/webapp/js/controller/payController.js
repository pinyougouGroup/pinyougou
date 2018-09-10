 //控制层 
app.controller('payController' ,function($scope,payService,$location){	
	
	$scope.flag=false;
	 $scope.createNative=function(){
		 $scope.payMessage = "";
		 $scope.flag=false;
		 
		 payService.createNative().success(function(response){
			 $scope.resultMap = response;
			 new QRious({
			      element: document.getElementById('payImage'),
			      size: 300,
			      value: response.code_url,
				  level:'L'
			    });
			 
//			 调用查询订单的方法
			 $scope.queryOrder(response.out_trade_no);
		 })
	 }
	 
	 $scope.queryOrder=function(out_trade_no){
		 payService.queryOrder(out_trade_no).success(function(response){
			 if(response.success){
				 location.href="paysuccess.html#?totalFee="+ $scope.resultMap.total_fee;
			 }else{
				 if(response.message=='支付超时'){
					 $scope.payMessage = "二维码已过期，刷新页面重新获取二维码。";
					 $scope.flag=true;
				 }
				 
			 }
			 
			 
		 })
	 }
	 
	 $scope.showMoney=function(){
		 $scope.total_fee = $location.search()['totalFee'];
	 }
    
});	
