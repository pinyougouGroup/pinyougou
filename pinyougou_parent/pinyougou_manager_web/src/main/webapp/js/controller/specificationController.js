app.controller("specificationController",function($scope,$controller,specificationService){  //直接把specificationService注入到了当前的specificationController中
	
	 $scope.paginationConf = {
			 currentPage: 1,  //当前页码   是angular控制
			 totalItems: 10,  //总条数     应该从后台查询 赋值
			 itemsPerPage: 10, //每页显示的条数  是angular控制
			 perPageOptions: [10, 20, 30, 40, 50],  //每页显示的条数的选择  是angular控制
			 onChange: function(){
			     $scope.reloadList();//重新加载
			 }
	};
	 
// 	 把需要删除数据的id放到数组中
	 $scope.selectIds=[];
	 
	 $scope.updateSelection=function($event,id){
		 if($event.target.checked){
			 $scope.selectIds.push(id);
		 }else{
// 			 把id从数据中移除
// 			 $scope.selectIds.splice(索引值,数量);    "qwer".indexOf("e")
			var index =  $scope.selectIds.indexOf(id);
			 $scope.selectIds.splice(index,1);
		 }
		
	 }
	 
	 $scope.reloadList=function(){
// 		 当前页码  每页显示条数
// 		 $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
		 $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
	 }
	
	
//	$scope.entity={tbSpecificationOptionList:[]};
	 //动态添加行
	$scope.addSpecificationOptionList=function(){
		$scope.entity.tbSpecificationOptionList.push({});
	}
	 //动态删除行
	$scope.deleSpecificationOptionList=function(index){
		$scope.entity.tbSpecificationOptionList.splice(index,1);
	}
	
	
	
	
//	 controller的继承
	$controller('baseController',{$scope:$scope});//继承  本质就是共用一个$scope
	 
	 $scope.findPage=function(pageNo,pageSize){
// 		 $http.get("../specification/findPage?page="+$scope.paginationConf.currentPage+"&pageSize="+$scope.paginationConf.itemsPerPage)
		 
// 		 restful风格
		 specificationService.findPage(pageNo,pageSize).success(function(response){
// 			 response:{total:100,rows:[{},{},{}]}
			 $scope.paginationConf.totalItems =response.total; //给分页组件赋总条数
			 $scope.list =response.rows; //当前页的数据
			 
		 })
		 
	 }
	 
	 
	 $scope.findAll=function(){
		 specificationService.findAll().success(function(response){
			 $scope.list=response;
		 })
	 }
	 
	 $scope.save=function(){
		  var resultObj=null;
		 if($scope.entity.tbSpecification.id!=null){
			 resultObj = specificationService.update($scope.entity);
		 }else{
			 resultObj =  specificationService.add($scope.entity);
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
		 specificationService.findOne(id).success(function(response){
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
				specificationService.dele($scope.selectIds).success(function(response){
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
		 	specificationService.search(pageNo,pageSize,$scope.searchEntity).success(function(response){
// 			 response:{total:100,rows:[{},{},{}]}
			 $scope.paginationConf.totalItems =response.total; //给分页组件赋总条数
			 $scope.list =response.rows; //当前页的数据
			 
		 })
	 }

	
	
	
})