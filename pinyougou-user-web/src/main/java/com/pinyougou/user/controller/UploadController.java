package com.pinyougou.user.controller;

import entity.Result;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String file_server_url;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file) {

        try {
            //获取文件名
            String originalFilename = file.getOriginalFilename();
            //获得文件的扩展名
            String substring = originalFilename.substring(originalFilename.indexOf(".") + 1);
            //创建工具类
            FastDFSClient client = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            //获得文件点至
            String s = client.uploadFile(file.getBytes(), substring);
            //拼接文件地址
            String path = file_server_url + s;
            return new Result(true, path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "失败");
        }
    }


}
