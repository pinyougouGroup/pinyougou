 //控制层 
app.controller('cartController',function($scope,cartService,$window,$location){	
	
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
		 
	 }
	 */

	 $scope.addGoodsToCartList=function(itemId,num){
		 cartService.addGoodsToCartList(itemId,num).success(function(response){
			 if(response.success){
				 $scope.findCartList();
			 }else{
				 alert(response.message);
			 }
			 
		 })
	 }
	 var storage = $window.localStorage;
	$scope.init=function(){
	 var itemId = $location.search()['itemId'];
	 var num = $location.search()['num'];
	 if(itemId == null){
	 $scope.findCartList();
	     return ;
	 }
	 var cartList = storage.getItem("cartList");
	 if(cartList == null){
	 cartList = "[]";
	 }
	cartService.addItemToCartList(JSON.parse(cartList),itemId,num).success(function(response){
	 
	 if(response.success){
	 storage.setItem("cartList",response.message);
	 $scope.findCartList();
	 }
	 });
	 
	}
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
	
	$scope.addOrderItemToCart=function(itemId,num){
		 var cartList = storage.getItem("cartList");
		 if(cartList == null){
		 cartList = "[]";
		 }
		 
		cartService.addItemToCartList(JSON.parse(cartList),itemId,num).success(function(response){
		 if(response.success){
		 //把购物车集合放在 LocalStorage 中
		 storage.setItem("cartList",response.message);
		 //刷新购物车列表页面
		 $scope.findCartList();
		 }
		 });
		}
});	
