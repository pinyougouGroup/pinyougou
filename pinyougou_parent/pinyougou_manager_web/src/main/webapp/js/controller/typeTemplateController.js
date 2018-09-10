 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller,specificationService,brandService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
//	[{"id":25,"text":"希乐"},{"id":26,"text":"富光"}]  或 [{"text":"带盖"},{"text":"双层"}]
	
	$scope.arrayToString=function(array){
		array = JSON.parse(array);
		var str=""
		for (var i = 0; i < array.length; i++) {
			if(i==(array.length-1)){
				str+= array[i].text;
			}else{
				str+= array[i].text+",";
			}
		}
		return str;
	}
	
//	查询所有的规格数据
	$scope.findSpecList=function(){
		specificationService.findSpecList().success(function(response){
//			response---[{id:1,text:"联想"},{id:2,text:"中兴"},{id:3,text:"华为"}]
			$scope.specList={data:response};
		})
	}
//	查询所有的品牌数据
	$scope.findBrandList=function(){
		brandService.findBrandList().success(function(response){
//			response---[{id:1,text:"联想"},{id:2,text:"中兴"},{id:3,text:"华为"}]
			$scope.brandList={data:response};
		})
	}
//	select2组件需要格式
//	$scope.brandList={data:[{id:1,text:"AAAA"},{id:2,text:"BBBB"},{id:3,text:"CCCC"}]}
	
	$scope.entity={customAttributeItems:[]};//初始化模板对象 
//	动态添加扩展属性
	$scope.addCustomAttributeItems=function(){
		$scope.entity.customAttributeItems.push({});
	}
//	动态删除扩展属性
	$scope.deleCustomAttributeItems=function(index){
		$scope.entity.customAttributeItems.splice(index,1);
	}
	
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				response.brandIds = JSON.parse(response.brandIds);
				response.specIds = JSON.parse(response.specIds);
				response.customAttributeItems = JSON.parse(response.customAttributeItems);
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  	
		
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
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
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
