package com.yuan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuan.pojo.Article;
import com.yuan.pojo.Label;
import com.yuan.pojo.Sort;
import com.yuan.service.ArticleService;
import com.yuan.service.LabelService;
import com.yuan.service.SortService;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 0:43
 * @Description null
 */
@Slf4j
@RestController
@RequestMapping("/sort")
public class SortController {
    @Resource
    private SortService sortService;



@GetMapping("/listSortAndLabel")
    public R listSortAndLabel(){
    Map<String, List> map=sortService.listSortAndLabel();

    return R.success(map);
}
@GetMapping("/getSortInfo")
public R getSortInfo(){

    List<Sort> list =sortService.getSortInfo();
    return R.success(list);
}

    /**
     * 删除标签
     * @param id
     * @return
     */
    @GetMapping("/admin/deleteSort")
    public R deleteSort(@RequestParam("id") Integer id)
{
    boolean b = sortService.removeById(id);
    if(!b) {
        return R.fail("分类删除失败");
        }

    return R.success();
}


    @PostMapping("/admin/saveSort")
    public R saveSort(@RequestBody Sort sort)
    {
        boolean b = sortService.save(sort);
        if(!b) {
            return R.fail("分类保存失败");
        }
        return R.success();
    }
    @PostMapping("/admin/updateSort")
    public R updateSort(@RequestBody Sort sort)
    {
        boolean b = sortService.updateById(sort);
        if(!b) {
            return R.fail("分类修改失败");
        }
        return R.success();
    }




}
