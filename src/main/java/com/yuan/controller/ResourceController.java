package com.yuan.controller;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuan.annotations.LoginCheck;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.SearchResourceParam;
import com.yuan.pojo.Resource;
import com.yuan.pojo.User;
import com.yuan.service.ResourceService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.QiniuUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.Collections;
import java.util.Date;


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
        if (!StringUtils.hasText(resource.getType()) || !StringUtils.hasText(resource.getPath())) {
            return R.fail("资源类型和资源路径不能为空！");
        }
        Resource re = new Resource();
        re.setPath(resource.getPath());
        re.setType(resource.getType());
        re.setSize(resource.getSize());
        re.setMimeType(resource.getMimeType());
        re.setUserId(((User)DataCacheUtil.get(authorization)).getId());
        re.setCreateTime( LocalDateTimeUtil.now());
        resourceService.save(re);
        log.info("***ResourceController.saveResource业务结束，结果:{}",re );
        return R.success();
    }
    @PostMapping("/admin/listResource")
    public R listResource(@RequestBody SearchResourceParam searchResourceParam)
    {
    IPage<Resource> page= resourceService.listResource(searchResourceParam);
        return R.success(page);
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
    @PostMapping("/admin/deleteResource")
    public R deleteResource(@RequestParam("path") String path) {
        QiniuUtil.deleteFile(Collections.singletonList(path.replace(CommonConst.DOWNLOAD_URL, "")));
        resourceService.lambdaUpdate().eq(Resource::getPath, path).remove();
        return R.success();
    }




}
