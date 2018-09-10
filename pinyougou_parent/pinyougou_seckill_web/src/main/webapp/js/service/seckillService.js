app.service("seckillService",function($http){
	
	this.findAllSeckillGoods=function(){
		return $http.get("./seckill/findAll");
	}
	this.findOne=function(id){
		return $http.get("./seckill/findOne?id="+id);
	}
	this.saveSeckillOrder=function(id){
		return $http.get("./seckill/saveSeckillOrder?id="+id);
	}
	
})