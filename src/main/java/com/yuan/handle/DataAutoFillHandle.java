package com.yuan.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/26 23:57
 * @Description 数据自动填充的功能
 */
@Component
public class DataAutoFillHandle  {
    /**
     * 插入元对象字段填充（用于插入时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
//    @Override
//    public void insertFill(MetaObject metaObject) {
//        this.strictInsertFill(metaObject, "createBy", String.class, !StringUtils.hasText(GetRequestParamsUtil.getUsername()) ? "yuan" : GetRequestParamsUtil.getUsername());
//    }


    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
//    @Override
//    public void updateFill(MetaObject metaObject) {
//        this.strictInsertFill(metaObject, "updateBy", String.class, !StringUtils.hasText(GetRequestParamsUtil.getUsername()) ? "yuan" : GetRequestParamsUtil.getUsername());
//    }



}
