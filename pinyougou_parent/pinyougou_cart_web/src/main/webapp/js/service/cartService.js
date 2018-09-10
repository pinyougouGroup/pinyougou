app.service("cartService",function($http){
	
	this.findCartList=function(){
		return $http.get("./cart/findCartList");
	}
	
	this.addGoodsToCartList=function(itemId,num){
		return $http.get("./cart/addGoodsToCartList/"+itemId+"/"+num);
	}
	
})