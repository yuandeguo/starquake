package com.yuan.controller;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.annotations.LoginCheck;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.LoginParam;
import com.yuan.params.SearchUserParam;
import com.yuan.params.UserStatusOrTypeParam;
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
    public R login(@RequestBody @Valid LoginParam loginParam, @Validated BindingResult bindingResult) {
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
        log.info("***UserController.changeUserStatus业务结束，结果:{}",userStatusOrTypeParam );
        User user=new User();
        user.setId(userStatusOrTypeParam.getUserId());
        user.setUserStatus(userStatusOrTypeParam.getUserStatus());
        user.setUserType(userStatusOrTypeParam.getUserType());
        boolean b = userService.updateById(user);
     //   该用户退出登录
        if(!b)
        {
            return R.fail("修改失败");
        }
        if(userStatusOrTypeParam.getUserStatus()!=null&&!userStatusOrTypeParam.getUserStatus())
        {     log.info("***UserController.changeUserStatusOrTypeParam业务结束，结果:{}",userStatusOrTypeParam.getUserStatus() );

            //管理员
            if(DataCacheUtil.get(CommonConst.ADMIN_TOKEN + user.getId()) != null)
            {
                String token = (String) DataCacheUtil.get(CommonConst.ADMIN_TOKEN + user.getId());
                DataCacheUtil.remove(CommonConst.ADMIN_TOKEN + user.getId());
                DataCacheUtil.remove(token);
            }//普通用户
            else if (DataCacheUtil.get(CommonConst.USER_TOKEN + user.getId()) != null)
            {
                String token = (String) DataCacheUtil.get(CommonConst.USER_TOKEN + user.getId());
                DataCacheUtil.remove(CommonConst.USER_TOKEN + user.getId());
                DataCacheUtil.remove(token);
            }
        }

        return R.success();
    }



}
