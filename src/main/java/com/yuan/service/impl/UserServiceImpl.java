package com.yuan.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.UserMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.myEnum.ParamsEnum;
import com.yuan.params.*;
import com.yuan.pojo.WebInfo;
import com.yuan.security.jwt.JWTUtil;
import com.yuan.service.RedisService;
import com.yuan.utils.MailUtil;
import com.yuan.vo.UserVO;
import com.yuan.pojo.User;
import com.yuan.service.UserService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 14:59
 * @Description null
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private RedisService redisService;
    @Value("${user.code.format}")
    private String codeFormat;

    @Resource
    private MailUtil mailUtil;

    @Override
    public R<UserVO> login(LoginParam loginParam) {
        String username = loginParam.getUsername();
        String password = loginParam.getPassword();
        Boolean isAdmin = loginParam.getIsAdmin();
        password = new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(password));//解密处理
        User user = lambdaQuery().and(wrapper -> wrapper
                        .eq(User::getUsername, username)
                        .or()
                        .eq(User::getEmail, username)
                        .or()
                        .eq(User::getPhoneNumber, username))
                .eq(User::getPassword, DigestUtils.md5DigestAsHex(password.getBytes()))
                .one();
        if (user == null) {
            return R.fail("账号/密码错误，请重新输入！");
        }
        if (!user.getUserStatus()) {
            return R.fail("账号被冻结！请联系站长");
        }
        String adminToken = "";
        String userToken = "";
        if (isAdmin) {
            //不等于站长或者是管理员
            if (user.getUserType() != ParamsEnum.USER_TYPE_ADMIN.getCode() && user.getUserType() != ParamsEnum.USER_TYPE_MANAGER.getCode()) {
                return R.fail("请输入管理员账号！");
            }
        }
        Map<String,String> map=new HashMap<>();
        map.put(JWTUtil.USER_INFO, JSON.toJSONString(user));
        String token = JWTUtil.createToken(map, JWTUtil.JWT_SECRET, JWTUtil.EXPIRATION_TIME);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setPassword(null);
        //是否是root用户
        if (isAdmin && user.getUserType() == ParamsEnum.USER_TYPE_ADMIN.getCode()) {
            userVO.setIsBoss(true);
        }
        userVO.setAccessToken(token);
        return R.success(userVO);
    }

    /**
     * 根据请求头的权限鉴定来退出登录
     *
     * @return
     */
    @Override
    public R exitLogin() {
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()) {
            subject.logout();
        }
        return R.success();
    }

    /**
     * 按条件查询用户集合
     *
     * @param searchUserParam
     * @return
     */
    @Override
    public IPage<User> userList(SearchUserParam searchUserParam) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (searchUserParam.getUserStatus() != null) {
            queryWrapper.eq("user_status", searchUserParam.getUserStatus());
        }

        if (searchUserParam.getUserType() != null) {
            queryWrapper.eq("user_type", searchUserParam.getUserType());
        }
        if (StringUtils.hasText(searchUserParam.getSearchKey())) {
            queryWrapper.and(lq -> lq.eq("username", searchUserParam.getSearchKey())
                    .or()
                    .eq("phone_number", searchUserParam.getSearchKey()));
        }

        IPage<User> page = new Page<>(searchUserParam.getCurrent(), searchUserParam.getSize());
        page = baseMapper.selectPage(page, queryWrapper);
        List<User> records = page.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            records.forEach(u -> {
                u.setPassword(null);
                u.setOpenId(null);
            });
        }

        return page;
    }

    /**
     * 邮箱绑定或者修改邮箱绑定
     *
     * @param place
     * @param flag
     * @return
     */
    @Override
    public R emailForBind(String place, Integer flag) {
        int code = new Random().nextInt(900000) + 100000;
        if (flag == 1) {

        } else if (flag == 2) {

            List<String> mail = new ArrayList<>();
            mail.add(place);
//            获取样式
            String text = getCodeMail(code);
            WebInfo webInfo = (WebInfo) DataCacheUtil.get(CommonConst.WEB_INFO);
            mailUtil.sendMailMessage(mail, "您有一封来自" + (webInfo == null ? "yuan010" : webInfo.getWebName()) + "的回执！", text);
        }
        Integer userId = (  (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal()).getId();
        redisService.set(CommonConst.USER_CODE + userId + "_" + place + "_" + flag, Integer.valueOf(code), 300);
        return R.success();
    }

    @Override
    public R updateSecretInfo(UserUpdateSecretInfoParam userUpdateSecretInfoParam) {
        String password = new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(userUpdateSecretInfoParam.getPassword()));
        Integer flag = userUpdateSecretInfoParam.getFlag();
        String place = userUpdateSecretInfoParam.getPlace();
        String code = userUpdateSecretInfoParam.getCode();

        User user = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        redisService.remove(CommonConst.USER_CACHE + user.getId().toString());

        if ((flag == 1 || flag == 2) && !DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
            return R.fail("密码错误！");
        }
        if ((flag == 1 || flag == 2) && !StringUtils.hasText(code)) {
            return R.fail("请输入验证码！");
        }
        //更新邮箱
        if (flag == 2) {
            Integer count = lambdaQuery().eq(User::getEmail, place).count();
            if (count != 0) {
                return R.fail("邮箱重复！");
            }
            Integer codeCache = redisService.get(CommonConst.USER_CODE + user.getId() + "_" + place + "_" + flag, Integer.class);
            if (codeCache != null && codeCache.intValue() == Integer.parseInt(code)) {

                redisService.remove(CommonConst.USER_CODE + user.getId() + "_" + place + "_" + flag);

                user.setEmail(place);
            } else {
                return R.fail("验证码错误！");
            }
        }
        updateById(user);
        Map<String,String> map=new HashMap<>();
        map.put(JWTUtil.USER_INFO, JSON.toJSONString(user));
        String token = JWTUtil.createToken(map, JWTUtil.JWT_SECRET, JWTUtil.EXPIRATION_TIME);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setPassword(null);
        userVO.setAccessToken(token);
        return R.success(userVO);
    }

    @Override
    public R getCodeForForgetPasswordOrRegister(String place, Integer flag) {
        int i = new Random().nextInt(900000) + 100000;
        if (flag == 1) {

        } else if (flag == 2) {
            List<String> mail = new ArrayList<>();
            mail.add(place);
            String text = getCodeMail(i);

            WebInfo webInfo = (WebInfo) DataCacheUtil.get(CommonConst.WEB_INFO);
            mailUtil.sendMailMessage(mail, "您有一封来自" + (webInfo == null ? "starquake" : webInfo.getWebName()) + "的回执！", text);
        }
        redisService.set(CommonConst.FORGET_PASSWORD + place + "_" + flag, Integer.valueOf(i), 300);
        return R.success();
    }

    @Override
    public R updateForForgetPassword(ForForgetPasswordParam forForgetPasswordParam) {
        String password = new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(forForgetPasswordParam.getPassword()));
        Integer flag = forForgetPasswordParam.getFlag();
        String code = forForgetPasswordParam.getCode();
        String place = forForgetPasswordParam.getPlace();
        Integer codeCache = redisService.get(CommonConst.FORGET_PASSWORD + place + "_" + flag, Integer.class);
        if (codeCache == null || codeCache != Integer.parseInt(code)) {
            return R.fail("验证码错误！");
        }
        redisService.remove(CommonConst.FORGET_PASSWORD + place + "_" + flag);
        if (flag == 2) {
            User user = lambdaQuery().eq(User::getEmail, place).one();
            if (user == null) {
                return R.fail("该邮箱未绑定账号！");
            }

            if (!user.getUserStatus()) {
                return R.fail("账号被冻结！");
            }
            lambdaUpdate().eq(User::getEmail, place).set(User::getPassword, DigestUtils.md5DigestAsHex(password.getBytes())).update();
            redisService.remove(CommonConst.USER_CACHE + user.getId().toString());
        }
        return R.success();


    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @Override
    public R register(UserRegisterParam user) {
        String regex = "\\d{11}";
        if (user.getUsername().matches(regex)) {
            return R.fail("用户名不能为11位数字！");
        }

        if (user.getUsername().contains("@")) {
            return R.fail("用户名不能包含@！");
        }

        if (StringUtils.hasText(user.getEmail())) {
            Integer codeCache = redisService.get(CommonConst.FORGET_PASSWORD + user.getEmail() + "_2", Integer.class);
            if (codeCache == null || codeCache != Integer.parseInt(user.getCode())) {
                return R.fail("验证码错误！");
            }
            redisService.remove(CommonConst.FORGET_PASSWORD + user.getEmail() + "_2");
        } else {
            return R.fail("请输入邮箱！");
        }


        user.setPassword(new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(user.getPassword())));

        Integer count = lambdaQuery().eq(User::getUsername, user.getUsername()).count();
        if (count != 0) {
            return R.fail("用户名重复！");
        }
        if (StringUtils.hasText(user.getEmail())) {
            Integer emailCount = lambdaQuery().eq(User::getEmail, user.getEmail()).count();
            if (emailCount != 0) {
                return R.fail("邮箱重复！");
            }
        }

        User u = new User();
        u.setUsername(user.getUsername());
        u.setEmail(user.getEmail());
        u.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
//        获取随机头像
        if (!StringUtils.hasText(u.getAvatar())) {
            u.setAvatar(DataCacheUtil.getRandomAvatar());
        }
        save(u);
        User one = lambdaQuery().eq(User::getId, u.getId()).one();

        Map<String,String> map=new HashMap<>();
        map.put(JWTUtil.USER_INFO, JSON.toJSONString(one));
        String token = JWTUtil.createToken(map, JWTUtil.JWT_SECRET, JWTUtil.EXPIRATION_TIME);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(one, userVO);
        userVO.setPassword(null);
        userVO.setAccessToken(token);
        return R.success(userVO);
    }

    @Override
    public R updateUserInfo(User user) {
        Integer userId = ( (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal()).getId();
        if (StringUtils.hasText(user.getUsername())) {
            String regex = "\\d{11}";
            if (user.getUsername().matches(regex)) {
                return R.fail("用户名不能为11位数字！");
            }

            if (user.getUsername().contains("@")) {
                return R.fail("用户名不能包含@！");
            }
            Integer count = lambdaQuery().eq(User::getUsername, user.getUsername()).ne(User::getId, userId).count();
            if (count != 0) {
                return R.fail("用户名重复！");
            }
        }
        User u = new User();
        u.setId(userId);
        u.setUsername(user.getUsername());
        u.setAvatar(user.getAvatar());
        u.setGender(user.getGender());
        u.setIntroduction(user.getIntroduction());
        updateById(u);
        User one = lambdaQuery().eq(User::getId, u.getId()).one();

        Map<String,String> map=new HashMap<>();
        map.put(JWTUtil.USER_INFO, JSON.toJSONString(one));
        String token = JWTUtil.createToken(map, JWTUtil.JWT_SECRET, JWTUtil.EXPIRATION_TIME);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(one, userVO);
        userVO.setPassword(null);
        userVO.setAccessToken(token);
        return R.success(userVO);
    }

    @Override
    public R changeUserStatusOrTypeParam(UserStatusOrTypeParam userStatusOrTypeParam) {
        User user = new User();
        user.setId(userStatusOrTypeParam.getUserId());
        user.setUserStatus(userStatusOrTypeParam.getUserStatus());
        user.setUserType(userStatusOrTypeParam.getUserType());
        boolean b = updateById(user);
        //   该用户退出登录
        if (!b) {
            return R.fail("修改失败");
        }
        if (userStatusOrTypeParam.getUserStatus() != null && !userStatusOrTypeParam.getUserStatus()) {

//            //管理员
//            if (redisService.get(CommonConst.ADMIN_TOKEN + user.getId(), String.class) != null) {
//                String token = redisService.get(CommonConst.ADMIN_TOKEN + user.getId(), String.class);
//                redisService.remove(CommonConst.ADMIN_TOKEN + user.getId());
//                redisService.remove(token);
//            }//普通用户
//            else if (redisService.get(CommonConst.USER_TOKEN + user.getId(), String.class) != null) {
//                String token = (String) redisService.get(CommonConst.USER_TOKEN + user.getId(), String.class);
//                redisService.remove(CommonConst.USER_TOKEN + user.getId());
//                redisService.remove(token);
//            }
        }

        return R.success();
    }

    /**
     * 设置邮件内容
     *
     * @param code
     * @return
     */
    private String getCodeMail(int code) {
        WebInfo webInfo = (WebInfo) DataCacheUtil.get(CommonConst.WEB_INFO);
        String webName = (webInfo == null ? "starQuake" : webInfo.getWebName());
        return String.format(MailUtil.mailText,
                webName,
                "你收到来自 圆的果 的消息",
                " Hi, 朋友  ",
                String.format(codeFormat, code),
                "",
                webName);
    }

}
