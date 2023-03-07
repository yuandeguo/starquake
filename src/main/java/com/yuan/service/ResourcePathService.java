package com.yuan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.params.PageParam;
import com.yuan.params.SearchResourcePathParam;
import com.yuan.pojo.ResourcePath;
import com.yuan.utils.R;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 21:14
 * @Description null
 */
public interface ResourcePathService extends IService<ResourcePath> {
   R listResourcePath(SearchResourcePathParam resourcePathParam);

    R listResourcePathOnFront(PageParam pageParam);
}
