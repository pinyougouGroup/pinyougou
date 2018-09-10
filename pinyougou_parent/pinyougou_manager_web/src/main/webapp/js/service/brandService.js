app.service("brandService",function($http){
	
	
//	添加模板数据时需要的品牌数据 要求返回的格式：[{id:1,text:"SSS"},{}...]
	this.findBrandList=function(){
		return $http.get("../brand/findBrandList");
	}
	
	 this.findPage=function(pageNo,pageSize){
		 return $http.get("../brand/findPage/"+pageNo+"/"+pageSize);
	 }
	 
	 this.findAll=function(){
		 return $http.get("../brand/findAll");
	 }
	 
	 
	 this.add=function(entity){
		 return $http.post("../brand/add",entity);
	 }
	 this.update=function(entity){
		 return  $http.post("../brand/update",entity);
	 }
	 
	 
	 this.findOne=function(id){
		 return $http.get("../brand/findOne/"+id);
	 }
	 
	 this.dele=function(selectIds){
		 return $http.get("../brand/dele/"+selectIds);
	 }
	 
	 this.search=function(pageNo,pageSize,searchEntity){
 		return $http.post("../brand/search/"+pageNo+"/"+pageSize,searchEntity);
 	 }
	
})