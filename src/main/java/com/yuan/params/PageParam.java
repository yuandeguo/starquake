package com.yuan.params;

import lombok.Data;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 20:50
 * @Description null
 */
@Data
public class PageParam {
    private int    current = 1;
    private int    size = 10;
}
