 //控制层 
app.controller('searchController' ,function($scope,$location,searchService){	
	$scope.paramMap={keyword:'手机',category:'',brand:'',price:'',order:'asc',pageNo:1,spec:{}};
//	$scope.paramMap={keyword:'三星',category:'',brand:'',price:'',spec:{网络："移动3G","机身内存":"32G"}};
	
	
//	点击页面上搜索按钮  要清空之前的查询条件
	$scope.searchBykeyword=function(){
		$scope.paramMap={keyword:'手机',category:'',brand:'',price:'',order:'asc',pageNo:1,spec:{}};
		
		if($scope.keyword!=null&&$scope.keyword!=''){
			$scope.paramMap.keyword=$scope.keyword;
		}
		
		$scope.search();
	}
	
	
	$scope.initSearch=function(){
//		获取带过来的参数
		$scope.keyword = $location.search()['keyword'];
		if($scope.keyword!=null&&$scope.keyword!=''){
			$scope.paramMap.keyword=$scope.keyword;
		}
			$scope.search();
		
	}
	
//	brand 联想
	$scope.addParamToMap=function(key,value){
		$scope.paramMap[key]=value;
		$scope.search();
	}
	$scope.addParamToSpecMap=function(key,value){
		$scope.paramMap.spec[key]=value;
		$scope.search();
	}
	
	
	$scope.deleParamToMap=function(key){
		$scope.paramMap[key]='';
		$scope.search();
	}
	
	
	$scope.deleteParamToSpecMap=function(key){
		delete $scope.paramMap.spec[key];
		$scope.search();
	}
	
	
	
	
	
	
	$scope.search=function(){
		searchService.search($scope.paramMap).success(function(response){
			$scope.resultMap=response;
			
//			response.totalPages=3
//			$scope.pageLable=[];
//			for (var i = 1; i <= response.totalPages; i++) {
//				$scope.pageLable.push(i);
//			}
			buildPageLabel();
			
			
		})
	}
	 
	function buildPageLabel() {
        $scope.pageLable = [];//新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;//得到最后页码
        var firstPage = 1;//开始页码
        var lastPage = maxPageNo;//截止页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后边有点
        if ($scope.resultMap.totalPages > 5) { //如果总页数大于 5 页,显示部分页码
            if ($scope.paramMap.pageNo <= 3) {//如果当前页小于等于 3
                lastPage = 5; //前 5 页
                $scope.firstDot = false;//前面没点
            } else if ($scope.paramMap.pageNo >= lastPage - 2) {//如果当前页大于等于最大页码-2
                firstPage = maxPageNo - 4;  //后 5 页
                $scope.lastDot = false;//后边没点
            } else { //显示当前页为中心的 5 页
                firstPage = $scope.paramMap.pageNo - 2;
                lastPage = $scope.paramMap.pageNo + 2;
            }
        } else {
            $scope.firstDot = false;//前面无点
            $scope.lastDot = false;//后边无点
        }
        //循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLable.push(i);
        }
    }
    
});	
