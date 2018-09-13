 app.controller("seckillController",function($scope,seckillService,uploadService){
	 
	 $scope.addItemImages=function(){
			$scope.entity.smallPic=$scope.image.url;
		}
	
	 $scope.uploadFile=function(){
		 uploadService.uploadFile().success(function(response){
				if(response.success){  //response={success:true,message:图片地址}
					$scope.image.url = response.message;
				}else{
					alert(response.message);
				}
				
			})
			
		}
	// 重新加载列表 数据
	$scope.findAll = function() {
		seckillService.findAll().success(function(response) {
			$scope.list = response;
		 
			
		});
	}
	
	$scope.save = function() {
		seckillService.add($scope.entity).success(function(response){
			if(response.success){
				$scope.findAll();
			}else{
				alert(response.message);
			}
		});
	}
 
	

});
