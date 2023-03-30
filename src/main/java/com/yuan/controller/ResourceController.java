package com.yuan.controller;

import com.yuan.myEnum.CommonConst;
import com.yuan.params.SearchResourceParam;
import com.yuan.pojo.Resource;
import com.yuan.service.ResourceService;
import com.yuan.utils.QiniuUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;


/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 17:12
 * @Description 文件上传
 */
@Slf4j
@RestController
@RequestMapping("/resource")
public class ResourceController {

@Autowired
    private ResourceService resourceService;
    /**
     * 保存
     */
    @PostMapping("/saveResource")
    public R saveResource(@RequestBody Resource resource, @RequestHeader("Authorization") String authorization) {
     return   resourceService.saveResource(resource,authorization);

    }
    @PostMapping("/admin/listResource")
    public R listResource(@RequestBody SearchResourceParam searchResourceParam)
    {
   return resourceService.listResource(searchResourceParam);

    }
    @GetMapping("/admin/changeResourceStatus")
    public R changeResourceStatus(@RequestParam("id") Integer id,@RequestParam("flag") boolean flag)
    {
        Resource resource=new Resource();
        resource.setStatus(flag);
        resource.setId(id);
        boolean b = resourceService.updateById(resource);
        if(!b) {
            return R.fail("资源修改状态失败");
        }
        return R.success();

    }
    @GetMapping("/admin/deleteResource")
    public R deleteResource(@RequestParam("path") String path) {
        QiniuUtil.deleteFile(Collections.singletonList(path.replace(CommonConst.DOWNLOAD_URL, "")));
        resourceService.lambdaUpdate().eq(Resource::getPath, path).remove();
        return R.success();
    }




}
