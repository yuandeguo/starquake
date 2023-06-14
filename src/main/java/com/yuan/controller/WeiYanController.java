package com.yuan.controller;

import com.yuan.params.PageParam;
import com.yuan.pojo.WeiYan;
import com.yuan.service.WeiYanService;
import com.yuan.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/7 0:41
 * @Description null
 */
@RestController
@RequestMapping("/weiYan")
public class WeiYanController {

    @Resource
    private WeiYanService weiYanService;

    /**
     * 保存
     */
    @PostMapping("/saveWeiYan")
    public R saveWeiYan(@RequestBody WeiYan weiYanVO) {

        return weiYanService.saveWeiYan(weiYanVO);

    }

    /**
     * 删除
     */
    @GetMapping("/deleteWeiYan")
    public R deleteWeiYan(@RequestParam("id") Integer id) {
        return weiYanService.deleteWeiYan(id);


    }

    /**
     * 查询List
     */
    @PostMapping("/listWeiYan")
    @RequiresPermissions("permission")
    public R listWeiYan(@RequestBody PageParam pageParam) {
        return weiYanService.listWeiYan(pageParam);

    }
}