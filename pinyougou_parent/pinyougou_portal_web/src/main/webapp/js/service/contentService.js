//服务层
app.service('contentService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findByCategoryId=function(categoryId){
		return $http.get('../index/findByCategoryId/'+categoryId);		
	}
	
});
