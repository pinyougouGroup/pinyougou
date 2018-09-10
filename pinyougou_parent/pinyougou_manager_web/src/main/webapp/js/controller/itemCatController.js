 //控制层 
app.controller('itemCatController' ,function($scope,typeTemplateService,itemCatService){	
	
	$scope.parentId=0; //记录的是即将保存对象的父id
	
	$scope.findAllTypeTemplate=function(){
		typeTemplateService.findAll().success(function(response){
			$scope.typeTemplateList = response;
		})
		
	}
	
	$scope.grade=1;//代表数据的级别  默认显示的是一级分类数据
	
	$scope.entity1=null; //面包屑上显示的一级分类
	$scope.entity2=null; //面包屑上显示的二级分类
	
	$scope.setGrade=function(grade,pojo){
		
		$scope.parentId=pojo.id;
		
		$scope.grade=grade;
		if($scope.grade==1){ //列表显示的是一级分类
			$scope.entity1=null; //面包屑上显示的一级分类
			$scope.entity2=null; //面包屑上显示的二级分类
		}
		
		if($scope.grade==2){ //列表显示的是二级分类    entity2 置成null
			$scope.entity1=pojo;
			$scope.entity2=null;
		}
		if($scope.grade==3){ //列表显示的是三级分类
			$scope.entity2=pojo;
		}
		
		
	}
	
	$scope.findByParent=function(parentId){
		itemCatService.findByParent(parentId).success(function(response){
			$scope.list=response;
		})
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
//			{name:"xxx",typeId:1}------>{name:"xxx",typeId:1,parentId:1}
			$scope.entity['parentId']=$scope.parentId;
//			$scope.entity.parentId=$scope.parentId;
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParent($scope.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
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
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
