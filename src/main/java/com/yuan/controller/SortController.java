package com.yuan.controller;
import com.yuan.pojo.Sort;
import com.yuan.service.SortService;
import com.yuan.utils.DataCacheUtil2;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
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
  return sortService.listSortAndLabel();


}
@GetMapping("/getSortInfo")
public R getSortInfo(){
    return sortService.getSortInfo();

}





    /**
     * 删除标签
     * @param id
     * @return
     */
    @GetMapping("/admin/deleteSort")
    public R deleteSort(@RequestParam("id") Integer id)
{
    return sortService.deleteSort(id);

}


    @PostMapping("/admin/saveSort")
    public R saveSort(@RequestBody Sort sort)
    {
        return sortService.saveSort(sort);

    }
    @PostMapping("/admin/updateSort")
    public R updateSort(@RequestBody Sort sort)
    {
        return sortService.updateSort(sort);

    }


    @Resource
    private DataCacheUtil2 redisService;

    @PostMapping("/redis")
    public R updateSort()
    {   redisService.SetString("ceshi","123");

        String last= redisService.getString("ceshi");
return R.success(last);
    }



}
