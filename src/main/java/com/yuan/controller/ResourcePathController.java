package com.yuan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuan.params.SearchResourcePathParam;
import com.yuan.pojo.ResourcePath;
import com.yuan.service.ResourcePathService;
import com.yuan.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/admin/listResourcePath")
    public R listResourcePath(@RequestBody SearchResourcePathParam resourcePathParam){
        IPage<ResourcePath> page= resourcePathService.listResourcePath(resourcePathParam);
        return R.success(page);
    }

    /**
     * 更新状态
     * @param resource
     * @return
     */
    @PostMapping("/admin/updateResourcePath")
    public R updateResourcePath(@RequestBody ResourcePath resource){
        System.out.println(resource);

        boolean b = resourcePathService.updateById(resource);

        if(!b) {
            return R.fail("资源路径修改状态失败");
        }
        return R.success();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/admin/deleteResourcePath")
    public R deleteResourcePath(@RequestParam ("id") Integer id){


        boolean b = resourcePathService.removeById(id);

        if(!b) {
            return R.fail("资源路径删除失败");
        }
        return R.success();
    }

    @PostMapping("/admin/saveResourcePath")
    public R saveResourcePath(@RequestBody ResourcePath resource){
        boolean b = resourcePathService.save(resource);
        if(!b) {
            return R.fail("资源路径保存失败");
        }
        return R.success();
    }




}
