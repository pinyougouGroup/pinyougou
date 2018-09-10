app.controller("goodsEditController",function($scope,goodsService,uploadService,itemCatService,typeTemplateService){
	
//	$scope.spec={"网络":"移动3G","机身内存":"16G"};
	
//	alert($scope.spec.网络);
//	alert($scope.spec['网络']);
	
	
	$scope.entity={tbGoods:{isEnableSpec:"1"},tbGoodsDesc:{itemImages:[],specificationItems:[]}}; //初始化组合类
	
//	更新即将保存的规格数据 
//	[{"attributeName":"网络",attributeValue:["移动3G"]},"attributeName":"机身内存",attributeValue:["16G","32G"]}]
	$scope.updateSpecificationItems=function($event,key,value){
		if($event.target.checked){//勾选
	//		判断 $scope.entity.tbGoodsDesc.specificationItems 是否有已存在的对象 根据key判断
			var flag = false;//记录是否找到已存在对象的标记 false代表没找到
			for (var i = 0; i < $scope.entity.tbGoodsDesc.specificationItems.length; i++) {
				if($scope.entity.tbGoodsDesc.specificationItems[i].attributeName==key){
					$scope.entity.tbGoodsDesc.specificationItems[i].attributeValue.push(value);
					flag=true;
					break;
				}
			}
			if(!flag){ //代表没找到 需要追加新对象
				$scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":key,"attributeValue":[value]});	
			}
		}else{ //取消勾选
			for (var i = 0; i < $scope.entity.tbGoodsDesc.specificationItems.length; i++) {
				if($scope.entity.tbGoodsDesc.specificationItems[i].attributeName==key){
					var index = $scope.entity.tbGoodsDesc.specificationItems[i].attributeValue.indexOf(value);
					$scope.entity.tbGoodsDesc.specificationItems[i].attributeValue.splice(index,1);
//					判断此对象中的attributeValue的内容是否为空
					if($scope.entity.tbGoodsDesc.specificationItems[i].attributeValue.length==0){
//						应该把此对象从  $scope.entity.tbGoodsDesc.specificationItems中移除
						$scope.entity.tbGoodsDesc.specificationItems.splice(i,1);
					}
					
					break;
				}
			}
		}
		
//		$scope.entity.tbGoodsDesc.specificationItems
//		[{"attributeName":"网络","attributeValue":["移动3G","移动4G"]},{"attributeName":"机身内存","attributeValue":["16G","32G","64G","128G"]}]
		
		createItemList(); //构建sku列表
		
	}
	
	function createItemList(){
		$scope.entity.itemList=[{spec:{},price:0,num:9999,status:'1',isDefault:'0'}];
		var specItems  = $scope.entity.tbGoodsDesc.specificationItems;
		for (var i = 0; i < specItems.length; i++) {
			$scope.entity.itemList = addColumn($scope.entity.itemList,specItems[i].attributeName,specItems[i].attributeValue)
		}
		
	}
//	spec:{网络:移动3G}
	function addColumn(itemList,attributeName,attributeValue){
		var newItemList=[];
		for (var i = 0; i < itemList.length; i++) {
			for (var j = 0; j < attributeValue.length; j++) {
				var newRow = JSON.parse(JSON.stringify( itemList[i]));//深克隆
				newRow.spec[attributeName]  = attributeValue[j];
				newItemList.push(newRow);
			}
		}
		return newItemList;
	}
	
	 
	
//	动态添加图片
	$scope.addItemImages=function(){
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image);
	}
//	动态删除图片
	$scope.deleItemImages=function(index){
		$scope.entity.tbGoodsDesc.itemImages.splice(index,1);
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
	
	
	
	$scope.findCategory1List=function(){
		itemCatService.findByParent(0).success(function(response){
			$scope.category1List=response;
		})
	}
	
//	如果entity.tbGoods.category1Id一旦发生改变 触发查询二级分类的方法
	$scope.$watch("entity.tbGoods.category1Id",function( newValue,oldValue){// function可以有两个参数 newValue和oldValue 也可以有一个参数 newValue
		itemCatService.findByParent(newValue).success(function(response){
			$scope.category2List=response;
//			清空三级分类列表数据
			$scope.category3List=[];
//			清空模板id
			$scope.entity.tbGoods.typeTemplateId=null;
//			清空第三个tab页的扩展属性
			$scope.entity.tbGoodsDesc.customAttributeItems=[];
		})
	})
	
	//	如果entity.tbGoods.category2Id一旦发生改变 触发查询三级分类的方法
	$scope.$watch("entity.tbGoods.category2Id",function( newValue){// function可以有两个参数 newValue和oldValue 也可以有一个参数 newValue
		itemCatService.findByParent(newValue).success(function(response){
			$scope.category3List=response;
		})
	})
	
	//	如果entity.tbGoods.category3Id一旦发生改变 触发查询分类方法
	$scope.$watch("entity.tbGoods.category3Id",function( newValue,oldValue){// function可以有两个参数 newValue和oldValue 也可以有一个参数 newValue
//		newValue  三级分类ID
		itemCatService.findOne(newValue).success(function(response){
//			把查询到的分类id直接绑定到组合类中的TBGoods中的typeTemplateId属性上
			$scope.entity.tbGoods.typeTemplateId=response.typeId;
		})
	})
	
		//	如果entity.tbGoods.typeTemplateId一旦发生改变 触发查询模板方法
	$scope.$watch("entity.tbGoods.typeTemplateId",function( newValue,oldValue){// function可以有两个参数 newValue和oldValue 也可以有一个参数 newValue
//		newValue  三级分类ID
		typeTemplateService.findOne(newValue).success(function(response){
			$scope.brandList = JSON.parse(response.brandIds);// "[{id:1,text:'联想'}]";
//			response:模板对象
			$scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);// "[{"text":"快递方式"},{"text":"满赠"}]";
		});
//		根据模板id查询规格  但是规格要求的数据格式是：[{id:1,text:"",options:[{},{},{}]}]
		typeTemplateService.findSpecList(newValue).success(function(response){
//			response=[{id:1,text:"",options:[{},{},{}]}]
//			凑出来的规格数据格式是：
//			[{"options":[{"id":98,"optionName":"移动3G","orders":1,"specId":27},
//		           {"id":99,"optionName":"移动4G","orders":2,"specId":27},
//		           {"id":100,"optionName":"联通3G","orders":3,"specId":27},
//		           "id":27,"text":"网络"},
//		 {"options":[{"id":118,"optionName":"16G","orders":1,"specId":32},
//			      {"id":119,"optionName":"32G","orders":2,"specId":32},
//			      {"id":120,"optionName":"64G","orders":3,"specId":32},
//			       {"id":121,"optionName":"128G","orders":4,"specId":32}],
//			    "id":32,"text":"机身内存"}]
				$scope.specList = response;
			
		})
	})
	
//	商品新增
	$scope.save=function(){
//		保存之前需要把富文本编辑器中的内容放到 $scope.entity.tbGoodsDesc.introduction
//		从富文本编辑器中取值的方式：editor.html();
		$scope.entity.tbGoodsDesc['introduction']=editor.html();
//		alert(editor.html());
		goodsService.add($scope.entity).success(function(response){
			if(response.success){
				location.href="goods.html";
			}else{
				alert(response.message);
			}
		})
	}
	
})