//服务层
app.service('seckillOrderService',function($http){
	    	
	
//	this.updateIsMarketAble=function(status,selectIds){
//		return $http.get('../goods/updateIsMarketAble/'+status+"/"+selectIds);
//	}
//	
//	//读取列表数据绑定到表单中
//	this.findAll=function(){
//		return $http.get('../goods/findAll.do');		
//	}
	
	//分页 
//	this.findPage=function(pageNum,pageSize){
//		return $http.get('../seckillOrder/findPage/'+pageNum+"/"+pageSize);
//	}
	
	//查询实体
//	this.findOne=function(id){
//		return $http.get('../goods/findOne/'+id);
//	}
//	//增加 
//	this.add=function(entity){
//		return  $http.post('../goods/add.do',entity );
//	}
//	//修改 
//	this.update=function(entity){
//		return  $http.post('../goods/update.do',entity );
//	}
//	//删除
//	this.dele=function(ids){
//		return $http.get('../goods/delete/'+ids);
//	}
//	//搜索
	this.search=function(pageNum,pageSize){
		return $http.get('../seckillOrder/findPage/'+pageNum+"/"+pageSize);
	}    	
});
