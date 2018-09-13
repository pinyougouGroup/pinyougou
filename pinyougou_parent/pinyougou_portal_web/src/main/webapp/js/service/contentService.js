//服务层
app.service('contentService',function($http){
	    
	this.showUserName=function(){
		return  $http.get("http://192.168.181.70:9980/sinalogin/showUserName",{'withCredentials':true});
	}
	//读取列表数据绑定到表单中
	this.findByCategoryId=function(categoryId){
		return $http.get('../index/findByCategoryId/'+categoryId);		
	}
	
});
