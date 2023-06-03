package com.yuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.pojo.ResourcePath;

import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 21:15
 * @Description null
 */
public interface ResourcePathMapper extends BaseMapper<ResourcePath> {

    List<String> listAllClassifys();
}
