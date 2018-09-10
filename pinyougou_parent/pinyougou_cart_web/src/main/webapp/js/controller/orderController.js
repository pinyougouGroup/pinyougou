 //控制层 
//app.controller('orderController' ,function($scope,cartService){	
 app.controller('orderController' ,function($scope,addressService,cartService,orderService){	
	
	$scope.entity={paymentType:'1',sourceType:'2'};
	
	
	$scope.saveOrder=function(){
		  $scope.entity['receiverAreaName']=$scope.selectedAddress.address;
		  $scope.entity['receiverMobile']=$scope.selectedAddress.mobile;
		  $scope.entity['receiver']=$scope.selectedAddress.contact;
		
		  orderService.save( $scope.entity).success(function(response){
			  if(response.success){
				  location.href="http://pay.pinyougou.com/pay.html";
			  }else{
				  alert(response.message);
			  }
		  })
	}
	
	$scope.selectedAddress = null;
	$scope.findAddressList=function(){
		addressService.findAddressList().success(function(response){
			$scope.addressList = response;
			for (var i = 0; i < response.length; i++) {
				if(response[i].isDefault=='1'){
					$scope.selectedAddress = response[i];
					break;
				}
			}
//			如果没有默认地址
			if($scope.selectedAddress==null&&response.length>0){
				$scope.selectedAddress = response[0];
			}
			
		})
	}
	
	$scope.isSelectedAddress=function(pojo){
		return $scope.selectedAddress.id == pojo.id;
		
	}
	
	$scope.updateSelectedAddress=function(pojo){
		$scope.selectedAddress=pojo;
	}
	
	
	 $scope.findCartList=function(){
		 cartService.findCartList().success(function(response){
			 $scope.cartList=response;
			 
			 $scope.totalNum=0;
			 $scope.totalMoney=0.00;
			 for (var i = 0; i < response.length; i++) {
				var  orderItemList = response[i].orderItemList ;
				for (var j = 0; j < orderItemList.length; j++) {
					$scope.totalNum+=orderItemList[j].num;
					$scope.totalMoney+=orderItemList[j].totalFee;
				}
			}
			 
		 })
		 
	 }
	 
 
	 
	 
    
});	
