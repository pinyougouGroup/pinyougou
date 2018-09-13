//服务层
app.service('seckillService',function($http){
	    	
	
	this.findAll=function(){
		return $http.get('../seckill/findAll');
	}
	
	this.add=function(entity){
		return  $http.post('../seckill/add',entity );
	}
});
