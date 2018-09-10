 //控制层 
app.controller('seckillController' ,function($scope,seckillService,$location,$interval){	
	
	 $scope.findAllSeckillGoods=function(){
		 seckillService.findAllSeckillGoods().success(function(response){
			 $scope.list=response;
		 })
		 
	 }
	 
	 $scope.findOne=function(){
		var id = $location.search()['id'];
		seckillService.findOne(id).success(function(response){
			 $scope.entity=response;
			 
//				XX天 01:56:78
//				结束时间和当前时间相差的秒数
				var betweenSeconds = (new Date($scope.entity.endTime).getTime()-new Date().getTime())/1000;

				var aa = $interval(function(){
					betweenSeconds--;
					if(betweenSeconds<=0){
						$interval.cancel(aa);
					}
//					相差的天数
					var days =Math.floor( betweenSeconds/60/60/24);
//					去除天数后相差的小时
					var hours =Math.floor( (betweenSeconds-(days*24*60*60))/60/60);
//					去除天数和小时后相差的分钟数
					var minuts =Math.floor(  (betweenSeconds- (days*24*60*60) - hours*60*60)/60);
			 //    去除天数和小时和分钟后的秒数
			        var seconds =Math.floor(  (betweenSeconds- (days*24*60*60) - hours*60*60 -minuts*60));
			 
			        $scope.secondsStr="";
			        if(days!=0){
			        	$scope.secondsStr+=days+"天 ";
			        }
			        if(hours<10){
			        	hours="0"+hours;
			        }
			        if(minuts<10){
			        	minuts="0"+minuts;
			        }
			        if(seconds<10){
			        	seconds="0"+seconds;
			        }
			       
			        $scope.secondsStr+=hours+":"+minuts+":"+seconds;

				 },1000);
			 
			 
		 });
		 
	 }
	 
	 $scope.saveSeckillOrder=function(){
		 seckillService.saveSeckillOrder($scope.entity.id).success(function(response){
			 if(response.success){
				 alert("订单保存成功,跳转到支付页面");
			 }else{
				 alert(response.message); 
			 }
			 
		 })
	 }
	 
	 
	 
});	
