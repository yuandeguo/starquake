package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.pojo.Sort;
import com.yuan.pojo.User;
import com.yuan.utils.R;

import java.util.List;
import java.util.Map;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 0:45
 * @Description null
 */
public interface SortService extends IService<Sort> {

   R getSortInfo();

   R listSortAndLabel();

    R deleteSort(Integer id);

    R saveSort(Sort sort);

    R updateSort(Sort sort);
}
