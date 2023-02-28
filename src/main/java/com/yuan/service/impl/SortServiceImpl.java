package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ResourceMapper;
import com.yuan.mapper.SortMapper;
import com.yuan.pojo.Label;
import com.yuan.pojo.Sort;
import com.yuan.service.LabelService;
import com.yuan.service.ResourceService;
import com.yuan.service.SortService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 0:46
 * @Description null
 */
@Slf4j
@Service
public class SortServiceImpl extends ServiceImpl<SortMapper, Sort> implements SortService {

    @Resource
    LabelService labelService;
}
