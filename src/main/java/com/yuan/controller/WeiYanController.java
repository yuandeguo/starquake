package com.yuan.controller;

import com.yuan.annotations.LoginCheck;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.PageParam;
import com.yuan.pojo.User;
import com.yuan.pojo.WeiYan;
import com.yuan.service.WeiYanService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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
    @LoginCheck
    public R saveWeiYan(@RequestBody WeiYan weiYanVO,@RequestHeader("Authorization") String authorization) {

        return weiYanService.saveWeiYan(weiYanVO,authorization);

    }

    /**
     * 删除
     */
    @GetMapping("/deleteWeiYan")
    @LoginCheck
    public R deleteWeiYan(@RequestParam("id") Integer id,@RequestHeader("Authorization") String authorization) {
        return weiYanService.deleteWeiYan(id,authorization);


    }
    /**
     * 查询List
     */
    @PostMapping("/listWeiYan")
    public R listWeiYan(@RequestBody PageParam pageParam) {
        return  weiYanService.listWeiYan(pageParam);

    }
}