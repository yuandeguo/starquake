package com.yuan.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @RequestMapping(value = "/picture", method = RequestMethod.POST)
    @ResponseBody
    public String pictureUpload(@RequestParam(value = "file", required = false) MultipartFile file) {
        System.out.println(file);

        System.out.println(1);
     return null;
    }



}
