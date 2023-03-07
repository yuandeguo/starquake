package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.pojo.Label;
import com.yuan.utils.R;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 0:52
 * @Description null
 */
public interface LabelService extends IService<Label> {
    R deleteLabel(Integer id);

    R saveLabel(Label label);

    R updateLabel(Label label);
}
