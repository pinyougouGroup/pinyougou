//控制层 
app.controller('orderController',function($scope, orderService) {
	//1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
	 $scope.statusList=["","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];
	 $scope.orderSelect=[{id:"0",name:"全部"},{id:"1",name:"未付款"},{id:"2",name:"已付款"},{id:"3",name:"未发货"},{id:"4",name:"已发货"},{id:"5",name:"交易成功"},{id:"6",name:"交易失败"},{id:"7",name:"待评价"}];
	 $scope.orderSelectDate=[{id:"1",name:"日订单"},{id:"2",name:"周订单"},{id:"3",name:"月订单 "}];   
	 

	 
	 //	 订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
	 $scope.sourceTypeList=["","app端","pc端","M端","微信端","手机QQ端"];
//			$scope.findOrderList=function(){
//				orderService.findOrderList().success(function(response){  
//					$scope.entity=response;
//					
//				})
//			}
			
		$scope.tbOrder={status:"0",date:"3"};
		$scope.$watch('tbOrder',function(){
			orderService.findOrderBySelectStatus($scope.tbOrder).success(function(response){
				$scope.entity=response;
			})
		},true);
		
		
		$scope.findOrderByDataSelect=function(){
			
			orderService.findOrderByDataSelect(_date).success(function(response){
				$scope.entity=response;
			})
		}
			

		});


