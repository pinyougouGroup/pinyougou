app.service("specificationService",function($http){
	
	this.findSpecList=function(){
		return $http.get("../specification/findSpecList");
	}
	
	 this.findPage=function(pageNo,pageSize){
		 return $http.get("../specification/findPage/"+pageNo+"/"+pageSize);
	 }
	 
	 this.findAll=function(){
		 return $http.get("../specification/findAll");
	 }
	 
	 
	 this.add=function(entity){
		 return $http.post("../specification/add",entity);
	 }
	 this.update=function(entity){
		 return  $http.post("../specification/update",entity);
	 }
	 
	 
	 this.findOne=function(id){
		 return $http.get("../specification/findOne/"+id);
	 }
	 
	 this.dele=function(selectIds){
		 return $http.get("../specification/dele/"+selectIds);
	 }
	 
	 this.search=function(pageNo,pageSize,searchEntity){
 		return $http.post("../specification/search/"+pageNo+"/"+pageSize,searchEntity);
 	 }
	
})