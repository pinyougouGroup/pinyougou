app.service("uploadService",function($http){
	
	this.uploadFile=function(){
		
		var formData = new FormData();  //FormData是html5的对象
		formData.append("file",file.files[0]);
		
		return $http({
			url:'../upload/uploadFile',
			method:'post',
			data:formData,
			// ‘Content-Type’: undefined，这样浏览器会帮我们把 Content-Type 设置为 multipart/form-data
			headers: {'Content-Type':undefined},
//			通过设置 transformRequest: angular.identity ，anjularjs transformRequest function 将序列化
//			我们的formdata object.
			transformRequest: angular.identity
		})
	}
	
	this.inExcel = function() {

		var formData = new FormData();
		formData.append("file", file.files[0]);

		return $http({
			url : '../excel/inExcel',
			method : 'post',
			data : formData,
			headers : {'Content-Type' : undefined},
			transformRequest : angular.identity
		})
	}
	
	
	this.outExcel=function(){
		
		return $http({
			url: '../excel/outExcel',
        	method: "GET",//接口方法
            params: {
                //接口参数
            },
            headers: {
                'Content-type': 'application/json'
            },
            responseType: 'arraybuffer'
        })
	}
})