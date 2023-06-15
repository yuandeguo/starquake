package com.yuan.controller;

import com.yuan.params.PictureHandleParam;
import com.yuan.service.DocService;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public R pictureUpload(@RequestParam(value = "file", required = false) MultipartFile[] file,@RequestParam(value = "token",required = false) String token) throws IOException {
        System.out.println(token);
     for (MultipartFile file1 :file) {
         System.out.println(file1);
         System.out.println(file1.getSize());
     }
        PictureHandleParam param=new PictureHandleParam();
     param.setPictureSizeType(3);
     param.setRate(0.1);
     param.setQuality(0.5);
     param.setFile(file);
     docService.pictureHandle(param);
     return R.success("上传成功啦");
    }



}
