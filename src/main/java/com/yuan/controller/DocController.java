package com.yuan.controller;

import com.yuan.exception.MyRuntimeException;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.PictureHandleParam;
import com.yuan.pojo.User;
import com.yuan.service.DocService;
import com.yuan.service.RedisService;
import com.yuan.task.DelayedTaskManager;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.QiniuUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

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
    @Autowired
    RedisService redisService;

    @GetMapping("/getUpToken")
    public R<String> getUpToken() {
     User user=   (User)SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
     String token=user.getUsername()+System.currentTimeMillis();
     return R.success(token);
    }
    @GetMapping("/delete")
    public R<String> deleteFile(String token) {

        return R.success(token);
    }


    @RequestMapping(value = "/picture", method = RequestMethod.POST)
    @ResponseBody
    public R pictureUpload(@RequestParam(value = "file", required = false) MultipartFile file,
                           @RequestParam(value = "token") String token,
                           @RequestParam(value = "format", required = false) String format,
                           @RequestParam(value = "pictureSizeType") Integer pictureSizeType,
                           @RequestParam(value = "width", required = false) Integer width,
                           @RequestParam(value = "height", required = false) Integer height,
                           @RequestParam(value = "rate", required = false) Double rate,
                           @RequestParam(value = "quality", required = false) Double quality,
                           @RequestParam(value = "angle", required = false) Integer angle,
                           HttpServletRequest request)  throws IOException {

        PictureHandleParam param = new PictureHandleParam();
        param.setToken(token);
        param.setFormat(format);
        param.setPictureSizeType(pictureSizeType);
        param.setWidth(width);
        param.setHeight(height);
        param.setRate(rate);
        param.setQuality(quality);
        param.setAngle(angle);
        param.setFile(file);
        String fileName = docService.pictureHandle(param);
        return R.success(fileName);
    }

    @RequestMapping(value = "/docDown", method = RequestMethod.GET)
    @ResponseBody
    public void downloadLocal(@RequestParam("path") String path, HttpServletResponse response) throws IOException {
        path = CommonConst.TEMP_DIR_PATh + path;
        // 读到流中
        InputStream inputStream = new FileInputStream(path);// 文件的存放路径
        response.reset();
        response.setContentType("application/x-msdownload");
        String filename = new File(path).getName();
        response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.addHeader("Access-control-Allow-Origin", DataCacheUtil.getRequest().getHeader("Origin"));
        response.addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        response.addHeader("Access-Control-Allow-Headers", DataCacheUtil.getRequest().getHeader("Access-Control-Request-Headers"));

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
