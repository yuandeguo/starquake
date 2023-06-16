package com.yuan.controller;

import com.yuan.params.PictureHandleParam;
import com.yuan.service.DocService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @author yuanguozhi
 * @version V1.0
 * @date 2023/6/15 14:07
 * @Description null
 */
@Slf4j
@RestController
@RequestMapping("/doc")
public class DocController {
    @Autowired
    DocService docService;
    @RequestMapping(value = "/picture", method = RequestMethod.POST)
    @ResponseBody
    public R pictureUpload(@RequestParam(value = "file", required = false) MultipartFile file,@RequestParam(value = "token",required = false) String token) throws IOException {
     PictureHandleParam param=new PictureHandleParam();
     param.setPictureSizeType(3);
     param.setRate(1.0);
     param.setQuality(0.5);
     param.setFile(file);
     String fileName = docService.pictureHandle(param);
     return R.success(fileName);
    }

    @RequestMapping(value = "/docDown", method = RequestMethod.GET)
    @ResponseBody
    public void  downloadLocal(@RequestParam("path") String path, HttpServletResponse response) throws IOException {
        path="D:\\my_java_project\\yuan_blog\\yuan_blog\\src\\inputPic\\"+path;
        // 读到流中
        InputStream inputStream = new FileInputStream(path);// 文件的存放路径
        response.reset();
        response.setContentType("application/x-msdownload");
        String filename = new File(path).getName();
        response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.addHeader("Access-control-Allow-Origin", DataCacheUtil.getRequest().getHeader("Origin"));
        response.addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        response.addHeader("Access-Control-Allow-Headers",  DataCacheUtil.getRequest().getHeader("Access-Control-Request-Headers"));

        ServletOutputStream outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int len;
        //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
        while ((len = inputStream.read(b)) > 0) {
            outputStream.write(b, 0, len);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }


}
