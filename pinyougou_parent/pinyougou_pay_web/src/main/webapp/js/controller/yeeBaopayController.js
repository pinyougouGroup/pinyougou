 //控制层 
app.controller('yeeBaopayController' ,function($scope,yeeBaopayService,$location){	
	
	$scope.findOrderByUserId=function(){
		yeeBaopayService.findOrderByUserId().success(function(response){
			$scope.resultMap = response;
		})
	}
	
	$scope.formData={};
	$scope.confirmOrder=function(){
		
		var pd_FrpId = $scope.formData.pd_FrpId;
		yeeBaopayService.confirmOrder($scope.formData.pd_FrpId).success(function(response){
			var s= JSON.parse(response);
			alert(s);
			location.href="https://www.yeepay.com/app-merchant-proxy/node?"+s;
		})
	}
	
	 $scope.showMoney=function(){
		 $scope.totalFee = $location.search()['r3_Amt'];
	 }
	 
});	
