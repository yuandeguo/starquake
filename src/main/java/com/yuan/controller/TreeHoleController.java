package com.yuan.controller;

import com.yuan.annotations.OperationLogAnnotation;
import com.yuan.params.PageParam;
import com.yuan.pojo.TreeHole;
import com.yuan.service.TreeHoleService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/6 16:30
 * @Description null
 */
@RestController
@RequestMapping("/treeHole")
public class TreeHoleController {
    @Resource
    private TreeHoleService treeHoleService;


    @PostMapping("/admin/treeHoleList")
    public R listBossTreeHole(@RequestBody PageParam pageParam) {

        return treeHoleService.treeHoleListByAdmin(pageParam);
    }

    @GetMapping("/admin/deleteTreeHole")
    public R deleteTreeHole(@RequestParam("id") Integer id) {

        boolean b = treeHoleService.removeById(id);
        if (!b) {
            return R.fail("弹幕删除失败");
        }
        return R.success();
    }

    /**
     * 查询List
     */
    @GetMapping("/listTreeHole")
    public R listTreeHole() {

        return treeHoleService.listTreeHole();
    }

    /**
     * 保存
     */
    @OperationLogAnnotation(operModul = "saveTreeHole", operType = "保存", operDesc = "保存留言")
    @PostMapping("/saveTreeHole")
    public R saveTreeHole(@RequestBody TreeHole treeHole) {
        if (!StringUtils.hasText(treeHole.getMessage())) {
            return R.fail("留言不能为空！");
        }
        treeHole.setCreateTime(LocalDateTime.now());
        if (!StringUtils.hasText(treeHole.getAvatar())) {
            //随机头像
            treeHole.setAvatar(DataCacheUtil.getRandomAvatar());
        }
        boolean save = treeHoleService.save(treeHole);
        if (!save) {
            return R.fail("弹幕保存失败");
        }
        return R.success(treeHole);

    }


}
