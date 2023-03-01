package com.yuan.controller;

import com.yuan.pojo.Label;
import com.yuan.pojo.Sort;
import com.yuan.service.LabelService;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 13:51
 * @Description null
 */
@Slf4j
@RestController
@RequestMapping("/label")
public class LabelController {
    @Resource
    private LabelService labelService;

    @GetMapping("/admin/deleteLabel")
    public R deleteLabel(@RequestParam("id") Integer id)
    {
        boolean b = labelService.removeById(id);
        if(!b) {
            return R.fail("标签删除失败");
        }
        return R.success();
    }

    @PostMapping("/admin/saveLabel")
    public R saveLabel(@RequestBody Label label)
    {
        boolean b = labelService.save(label);
        if(!b) {
            return R.fail("标签保存失败");
        }
        return R.success();
    }
    @PostMapping("/admin/updateLabel")
    public R updateLabel(@RequestBody Label label)
    {
        boolean b = labelService.updateById(label);
        if(!b) {
            return R.fail("标签修改失败");
        }
        return R.success();
    }




}
