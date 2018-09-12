app.service("cartService",function($http){
	
	/*this.findCartList=function(){
		return $http.get("./cart/findCartList");
	}
	*/
	this.addGoodsToCartList=function(itemId,num){
		return $http.get("./cart/addGoodsToCartList/"+itemId+"/"+num);
	}
	this.findCartList=function(cartList){
		 return $http({
		 url : '../cart/findCartList',
		 method : 'POST',
		 data : {
		 "cartList": cartList
		 },
		 contentType: "application/json",
		 dataType : 'json' 
		 });
		}
	this.addItemToCartList=function(cartList,itemId,num){
		 return $http({
		 url : '../cart/addItemToCartList',
		 method : 'POST',
		 data : {
		 "cartList":cartList,
		 "itemId": itemId,
		 "num": num
		 },
		 contentType: "application/json",
		 dataType : 'json' 
		 });
	}
	this.sum=function(cartList){
		var totalValue={totalNum:0, totalMoney:0.00 };
		for(var i=0;i<cartList.length;i++){
			var cart=cartList[i];
			for(var j=0;j<cart.orderItemList.length;j++){
				var orderItem=cart.orderItemList[j];
				totalValue.totalNum+=orderItem.num;
				totalValue.totalMoney+= orderItem.totalFee;
				}
			}
		         return totalValue;
	}
			
	
})