package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.params.LoginParam;
import com.yuan.params.SearchUserParam;
import com.yuan.pojo.User;
import com.yuan.pojo.WebInfo;
import com.yuan.vo.UserVO;
import com.yuan.utils.R;

import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 14:45
 * @Description null
 */
public interface UserService extends IService<User> {
    R login(LoginParam loginParam);

    /**
     * 根据请求头的权限鉴定来退出登录
     * @param authorization
     * @return
     */
    R exitLogin(String authorization);

    /**
     * 查询用户集合
     * @param searchUserParam
     * @return
     */
   List<User> userList(SearchUserParam searchUserParam);
}
