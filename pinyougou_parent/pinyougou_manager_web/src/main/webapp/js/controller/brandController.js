app.controller("brandController",function($scope,$controller,brandService){  //直接把brandService注入到了当前的brandController中
	
//	 controller的继承
	$controller('baseController',{$scope:$scope});//继承  本质就是共用一个$scope
	 
	 $scope.findPage=function(pageNo,pageSize){
// 		 $http.get("../brand/findPage?page="+$scope.paginationConf.currentPage+"&pageSize="+$scope.paginationConf.itemsPerPage)
		 
// 		 restful风格
		 brandService.findPage(pageNo,pageSize).success(function(response){
// 			 response:{total:100,rows:[{},{},{}]}
			 $scope.paginationConf.totalItems =response.total; //给分页组件赋总条数
			 $scope.list =response.rows; //当前页的数据
			 
		 })
		 
	 }
	 
	 
	 $scope.findAll=function(){
		 brandService.findAll().success(function(response){
			 $scope.list=response;
		 })
	 }
	 
	 $scope.save=function(){
		  var resultObj=null;
		  
		  
		 if($scope.entity.id!=null){
			 resultObj = brandService.update($scope.entity);
		 }else{
			 resultObj =  brandService.add($scope.entity);
		 }
		 
		 resultObj.success(function(response){
// 			 response：{success:true|false,message:"保存成功"|"保存失败"}
				if(response.success){
//	     			 如果成功  刷新页面
					$scope.reloadList();
				}else{
//	     			 如果失败  alert提示
					alert(response.message);
				}
		 })
	 }
	 
	 $scope.findOne=function(id){
		 brandService.findOne(id).success(function(response){
			 $scope.entity=response;
		 })
	 }
	 

	 
// 	 删除
	 $scope.dele=function(){
			if($scope.selectIds.length==0){
				return;
			}	
			
			var flag = window.confirm("确定删除您选择的数据吗?");
			if(flag){
				brandService.dele($scope.selectIds).success(function(response){
    				if(response.success){
						$scope.reloadList();
// 						清空数组
						 $scope.selectIds=[];
					}else{
//		     			 如果失败  alert提示
						alert(response.message);
					}
    			})
			}
	 }
	 
	 $scope.searchEntity={};// 为了在刷新页面或者第一次访问页面时不报错（因为searchEntity是 undefined）
	 
	 $scope.search=function(pageNo,pageSize){
// 		当前页码
// 		 每页显示的条数
//			searchEntity
		 	brandService.search(pageNo,pageSize,$scope.searchEntity).success(function(response){
// 			 response:{total:100,rows:[{},{},{}]}
			 $scope.paginationConf.totalItems =response.total; //给分页组件赋总条数
			 $scope.list =response.rows; //当前页的数据
		 })
	 }
	 
	
	
	
	
})