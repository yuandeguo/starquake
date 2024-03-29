package com.yuan.controller;

import com.yuan.myEnum.CommonConst;
import com.yuan.params.PageParam;
import com.yuan.params.SearchResourcePathParam;
import com.yuan.pojo.ResourcePath;
import com.yuan.pojo.User;
import com.yuan.service.RedisService;
import com.yuan.service.ResourcePathService;
import com.yuan.utils.R;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 21:12
 * @Description null
 */
@RestController
@RequestMapping("/resourcePath")
public class ResourcePathController {
    @Autowired
    private ResourcePathService resourcePathService;
    @Resource
    private RedisService redisService;

    @PostMapping("/admin/listResourcePath")
    public R listResourcePath(@RequestBody SearchResourcePathParam resourcePathParam) {
        return resourcePathService.listResourcePath(resourcePathParam);

    }

    /**
     * 更新状态
     *
     * @param resource
     * @return
     */
    @PostMapping("/admin/updateResourcePath")
    public R updateResourcePath(@RequestBody ResourcePath resource) {
        System.out.println(resource);

        boolean b = resourcePathService.updateById(resource);

        if (!b) {
            return R.fail("资源路径修改状态失败");
        }
        return R.success();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @GetMapping("/admin/deleteResourcePath")
    public R deleteResourcePath(@RequestParam("id") Integer id) {


        boolean b = resourcePathService.removeById(id);

        if (!b) {
            return R.fail("资源路径删除失败");
        }
        return R.success();
    }

    @PostMapping("/admin/saveResourcePath")
    public R saveResourcePath(@RequestBody ResourcePath resource) {
        resource.setCreateTime(LocalDateTime.now());
        boolean b = resourcePathService.save(resource);
        if (!b) {
            return R.fail("资源路径保存失败");
        }
        return R.success();
    }

    /**
     * 前端界面显示友情链接
     *
     * @param pageParam
     * @return
     */
    @PostMapping("/listResourcePathOnFriendUrl")
    public R listResourcePathOnFriendUrl(@RequestBody PageParam pageParam) {

        return resourcePathService.listResourcePathOnFriendUrl(pageParam);


    }

    /**
     * 前端界面显示友情链接
     *
     * @return
     */
    @GetMapping("/listResourcePathOnFavoritesUrl")
    public R listResourcePathOnFavoritesUrl() {

        return resourcePathService.listResourcePathOnFavoritesUrl();


    }


    @PostMapping("/saveFriend")
    public R saveFriend(@RequestBody ResourcePath resourcePath) {
        User user = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        if (!StringUtils.hasText(user.getEmail()))
            return R.fail("请绑定邮箱");


        if (!StringUtils.hasText(resourcePath.getTitle()) || !StringUtils.hasText(resourcePath.getCover()) ||
                !StringUtils.hasText(resourcePath.getUrl()) || !StringUtils.hasText(resourcePath.getIntroduction())) {
            return R.fail("信息不全！");
        }
        ResourcePath friend = new ResourcePath();
        friend.setTitle(resourcePath.getTitle());
        friend.setIntroduction(resourcePath.getIntroduction());
        friend.setCover(resourcePath.getCover());
        friend.setUrl(resourcePath.getUrl());
        friend.setType(CommonConst.RESOURCE_PATH_TYPE_FRIEND);
        friend.setStatus(Boolean.FALSE);
        resourcePathService.save(friend);
        return R.success();
    }

    /**
     * 获取网站指南的所有类别
     *
     * @return
     */
    @GetMapping("/listAllClassifys")
    public R listAllClassifys() {

        return resourcePathService.listAllClassifys();


    }


}
