package com.yuan.controller;

import com.yuan.pojo.Label;
import com.yuan.pojo.Sort;
import com.yuan.service.LabelService;
import com.yuan.service.SortService;
import com.yuan.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping("/sort")
public class SortController {
    @Resource
    private SortService sortService;
@Resource
private LabelService labelService;

@GetMapping("/listSortAndLabel")
    public R listSortAndLabel(){

 List<Sort> sortList=sortService.list();
 List<Label> labelList=labelService.list();
    Map<String, List> map = new HashMap<>();
    map.put("sorts",sortList);
    map.put("labels",labelList);

    return R.success(map);
}

}
