package com.pinyougou.manager.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {
	
	@Value("${file_server_url}")
	private String file_server_url;
	
	
	@RequestMapping("/uploadFile")
	public Result uploadFile(MultipartFile file) { //MultiPartFile类型接收  参数名称要和input中的name值保持一致
		try {
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			String originalFilename = file.getOriginalFilename(); //文件的真实名称
//		s.ds.sds.jpg
			String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
//		sdssds.jpg   jpeg  gif   png
			String uploadFile = fastDFSClient.uploadFile(file.getBytes(), extName);
//			uploadFile  = group1/M00/00/00/wKgZhVt2QTqARv8gAACuI4TeyLI446.jpg
			return new Result(true, file_server_url+uploadFile);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败");
		}

		
		
		

	}

}
