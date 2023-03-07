package com.yuan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.params.*;
import com.yuan.pojo.User;
import com.yuan.utils.R;

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
   IPage<User> userList(SearchUserParam searchUserParam);

    /**
     * 邮箱绑定或者修改邮箱绑定
     * flag=1是手机，2是邮箱
     * @param place
     * @param flag
     * @return
     */
    R emailForBind(String place, Integer flag,String authorization);

    R updateSecretInfo(UserUpdateSecretInfoParam userUpdateSecretInfoParam, User user);

    R getCodeForForgetPasswordOrRegister(String place, Integer flag);

    R updateForForgetPassword(ForForgetPasswordParam forForgetPasswordParam);

    /**
     * 用户注册
     * @param userRegisterParam
     * @return
     */
    R register(UserRegisterParam userRegisterParam);

    R updateUserInfo(User user, String authorization);

    R changeUserStatusOrTypeParam(UserStatusOrTypeParam userStatusOrTypeParam);
}
