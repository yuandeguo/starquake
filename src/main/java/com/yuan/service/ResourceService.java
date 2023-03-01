package com.yuan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.params.SearchResourceParam;
import com.yuan.pojo.Resource;
import com.yuan.pojo.WebInfo;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 17:12
 * @Description null
 */
public interface ResourceService  extends IService<Resource> {
    IPage<Resource> listResource(SearchResourceParam searchResourceParam);
}
