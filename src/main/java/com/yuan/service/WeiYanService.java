package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.params.PageParam;
import com.yuan.pojo.WeiYan;
import com.yuan.utils.R;

public interface WeiYanService extends IService<WeiYan> {
    R listWeiYan(PageParam pageParam);

    R saveWeiYan(WeiYan weiYanVO );

    R deleteWeiYan(Integer id);
}
