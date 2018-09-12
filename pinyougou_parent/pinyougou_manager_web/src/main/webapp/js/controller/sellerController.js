 //控制层 
app.controller('sellerController' ,function($scope,$controller,uploadService,sellerService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.updateStatus=function(status,info){
		
		var flag = window.confirm("确认"+info+"吗?");
		if(flag){
			sellerService.updateStatus(status,$scope.entity.sellerId).success(function(response){
				if(response.success){
					$scope.reloadList();
				}else{
					alert(response.message);
				}
			})
		}
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		sellerService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		sellerService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		sellerService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=sellerService.update( $scope.entity ); //修改  
		}else{
			serviceObject=sellerService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		sellerService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		sellerService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//Excel导入
	$scope.uploadFile=function(){
		uploadService.inExcel().success(function(response){
			if(response.success){
				alert(response.message);
				$scope.reloadList();// 刷新列表
			}else{
				alert(response.message);
			}
		})
	}
	
	//Excel导出
	$scope.download = function() {
		uploadService.outExcel().success(function (response) {
            var blob = new Blob([response], {type: "application/vnd.ms-excel"});
            var objectUrl = URL.createObjectURL(blob);
            var a = document.createElement('a');
            document.body.appendChild(a);
            a.setAttribute('style', 'display:none');
            a.setAttribute('href', objectUrl);
            var filename="品优购商家信息.xls";
            a.setAttribute('download', filename);
            a.click();
            URL.revokeObjectURL(objectUrl);
        })
    }
    
});	