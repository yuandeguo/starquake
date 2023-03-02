package com.yuan.params;

import lombok.Data;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 23:48
 * @Description null
 */
@Data
public class SearchArticleParam extends PageParam{


    private String  searchKey;
    // 是否推荐[0:否，1:是]
    private Boolean recommendStatus;
    private Integer labelId;
    private Integer sortId;
    private Boolean isBossSearch;

}
