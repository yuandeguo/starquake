package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.params.PageParam;
import com.yuan.pojo.Sort;
import com.yuan.pojo.TreeHole;
import com.yuan.utils.R;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/6 16:31
 * @Description null
 */
public interface TreeHoleService extends IService<TreeHole> {
    R listTreeHole();

    R treeHoleListByAdmin(PageParam pageParam);
}
