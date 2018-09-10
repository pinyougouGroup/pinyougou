app.controller("baseController",function($scope){  //公共的controller
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
			 if( $scope.selectIds.indexOf(id)==-1){
				 $scope.selectIds.push(id);
			 }
			
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
	 
	 
	 
	 $scope.ifChecked=function(id){
//		 如果id出现在selectIds数组中需要返回true
		 var index =  $scope.selectIds.indexOf(id);
		 if(index==-1){
			 return false;
		 }else{
			 return true;
		 }
//		 如果id没有出现在selectIds数组中需要返回false
	 }
	 
	 
	 $scope.ifCheckedAll=function(){
//		 $scope.list中的对象的id 是否都在 $scope.selectIds中
//		 如果都在返回true
//		 如果但凡有一个不存在返回false
		 
		 for (var i = 0; i < $scope.list.length; i++) {
			if ( $scope.selectIds.indexOf( $scope.list[i].id)==-1){
				return false;
			}
		}
		 return true;
		 
	 }
	 
//	 全选
	 $scope.selectAll=function($event){
		 if($event.target.checked){
//			当前页面的数据的id放到数组中
			 for (var i = 0; i < $scope.list.length; i++) {
				 if( $scope.selectIds.indexOf($scope.list[i].id)==-1){
					 $scope.selectIds.push( $scope.list[i].id);
				 }
			}
		 }else{
			 for (var i = 0; i < $scope.list.length; i++) {
				 var index =  $scope.selectIds.indexOf($scope.list[i].id);
				 $scope.selectIds.splice(index,1);
			}
		 }
	 }
	 
	 
	
})