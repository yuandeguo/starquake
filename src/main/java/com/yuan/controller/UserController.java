package com.yuan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.annotations.LoginCheck;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.*;
import com.yuan.pojo.User;
import com.yuan.utils.DataCacheUtil;
import com.yuan.vo.UserVO;
import com.yuan.service.UserService;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 14:44
 * @Description null
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户名、邮箱、手机号/密码登录
     */
    @PostMapping("/login")
    public R login(@RequestBody @Validated LoginParam loginParam,BindingResult bindingResult) {
        if(bindingResult.hasErrors())
        {
            R.fail("账号/密码不能为空，请重新输入！");
        }
        return userService.login(loginParam);
    }
    /**
     * 退出登录
     */
    @GetMapping("/logout")
    @LoginCheck
    public R exitLogin(@RequestHeader("Authorization") String authorization) {
        return userService.exitLogin(authorization);
    }

    /**
     * 按条件查询用户
     * @param searchUserParam
     * @return
     */
    @PostMapping("/admin/list")
    @LoginCheck(0)
    public R listUser(@RequestBody SearchUserParam searchUserParam) {
      return R.success( userService.userList(searchUserParam));
    }

    /**
     * 修改用户状态
     * <p>
     * flag = true：解禁
     * flag = false：封禁
     */
    @PostMapping("/admin/changeUserStatusOrTypeParam")
    @LoginCheck(0)
    public R changeUserStatusOrTypeParam(@RequestBody UserStatusOrTypeParam userStatusOrTypeParam) {
        return userService.changeUserStatusOrTypeParam(userStatusOrTypeParam);

    }

    /**
     * @param user
     * @param authorization
     * @return
     */
    @PostMapping("/updateUserInfo")
    public R updateUserInfo(@RequestBody User user,@RequestHeader("Authorization") String authorization) {
return userService.updateUserInfo(user,authorization);
    }

    /**
     * 邮箱绑定或者修改
     * @param place
     * @param flag
     * @return
     */
    @GetMapping("/getCodeForBind")
    public R getCodeForBind(@RequestParam("place") String place, @RequestParam("flag") Integer flag,@RequestHeader("Authorization") String authorization) {
        return userService.emailForBind(place, flag,authorization);
    }
    /**
     * 更新邮箱、手机号
     * <p>
     * 1 手机号
     * 2 邮箱
     * 3 密码：place=老密码&password=新密码
     */
    @PostMapping("/updateSecretInfo")
    @LoginCheck
    public R updateSecretInfo(@RequestBody UserUpdateSecretInfoParam userUpdateSecretInfoParam,@RequestHeader("Authorization") String authorization) {
        User user=(User)DataCacheUtil.get(authorization);
        DataCacheUtil.remove(CommonConst.USER_CACHE + user.getId().toString());
        return userService.updateSecretInfo(userUpdateSecretInfoParam,user);
    }
    /**
     * 注册或者忘记密码获取邮箱验证码
     * @param place
     * @param flag
     * @return
     */
    @GetMapping("/getCodeForForgetPasswordOrRegister")
    public R getCodeForForgetPasswordOrRegister(@RequestParam("place") String place, @RequestParam("flag") Integer flag) {

        return userService.getCodeForForgetPasswordOrRegister(place, flag);
    }

    /**
     * 忘记密码 更新密码
     * <p>
     * 1 手机号
     * 2 邮箱
     */
    @PostMapping("/updateForForgetPassword")
    public R updateForForgetPassword(@RequestBody ForForgetPasswordParam forForgetPasswordParam) {

        return userService.updateForForgetPassword(forForgetPasswordParam);
    }

    /**
     * 用户名/密码注册
     */
    @PostMapping("/register")
    public R register(@Validated @RequestBody UserRegisterParam userRegisterParam) {
        return userService.register(userRegisterParam);
    }



}
