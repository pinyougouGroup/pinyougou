//服务层
app.service('contentService',function($http){
	    
	this.showUserName=function(){
		return  $http.get("http://localhost:9980/sns/showUserName");
	}
	//读取列表数据绑定到表单中
	this.findByCategoryId=function(categoryId){
		return $http.get('../index/findByCategoryId/'+categoryId);		
	}
	
});
