 //控制层 
//app.controller('orderController' ,function($scope,cartService){	
 app.controller('orderController' ,function($scope,addressService,cartService,orderService,$window){	
	
	$scope.entity={paymentType:'1',sourceType:'2'};
	
	var storage = $window.localStorage;
	$scope.saveOrder=function(){
		  $scope.entity['receiverAreaName']=$scope.selectedAddress.address;
		  $scope.entity['receiverMobile']=$scope.selectedAddress.mobile;
		  $scope.entity['receiver']=$scope.selectedAddress.contact;
		
		  orderService.save( $scope.entity).success(function(response){
			  if(response.success){
				  location.href="http://pay.pinyougou.com/pay.html";
			  }
			  if($scope.entity.paymentType=='2'){
					 // alert("即将跳转到易宝支付页面。。。。")//提交订单成功后去执行易宝支付的方法
					  location.href="http://pay.pinyougou.com/yeeBaoPay.html";
				  }
				  if($scope.entity.paymentType=='3'){
					  alert("可以跳转到一个下单成功，等待收获的提示 页面。。。")
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
	
	
	/* $scope.findCartList=function(){
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
		 
	 }*/
	 
 
	$scope.findCartList=function () {
		 var cartList = storage.getItem("cartList");
		 if(cartList == null){
		 cartList = "[]";
		 }
		 cartService.findCartList(JSON.parse(cartList)).success(function (response) 
		{
		  $scope.cartList = JSON.parse(response.message);
		  if(response.success){
			  storage.clear();
		  }
		 
		 $scope.totalValue=cartService.sum($scope.cartList);
		 });
		 }
	 
    
});	
