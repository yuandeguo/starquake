package com.yuan.params;

import lombok.Data;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 21:23
 * @Description null
 */
@Data
public class SearchResourcePathParam extends PageParam{
    private String resourceType;
    private Boolean status;
}
