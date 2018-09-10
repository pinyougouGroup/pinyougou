 //控制层 
app.controller('indexController' ,function($scope,contentService){	
	
	$scope.findBannerList=function(){
		contentService.findByCategoryId(1).success(function(response){
			$scope.bannerList=response;
		})
		
	}
	
	$scope.search=function(){
//		跳转项目
		location.href="http://search.pinyougou.com/search.html#?keyword="+$scope.keyword;
	
	}
	 
    
});	
