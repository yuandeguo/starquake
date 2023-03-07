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

       return labelService.deleteLabel(id);

    }

    @PostMapping("/admin/saveLabel")
    public R saveLabel(@RequestBody Label label)
    {
        return labelService.saveLabel(label);

    }
    @PostMapping("/admin/updateLabel")
    public R updateLabel(@RequestBody Label label)
    {
      return  labelService.updateLabel(label);

    }




}
