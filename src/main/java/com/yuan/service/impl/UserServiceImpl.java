package com.yuan.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.UserMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.myEnum.ParamsEnum;
import com.yuan.params.*;
import com.yuan.pojo.WebInfo;
import com.yuan.service.RedisService;
import com.yuan.utils.MailUtil;
import com.yuan.vo.UserVO;
import com.yuan.pojo.User;
import com.yuan.service.UserService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.tio.core.Tio;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
        String username=loginParam.getUsername();
        String password=loginParam.getPassword();
        Boolean isAdmin=loginParam.getIsAdmin();
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
            if (user.getUserType() != ParamsEnum.USER_TYPE_ADMIN.getCode() && user.getUserType() != ParamsEnum.USER_TYPE_DEV.getCode()) {
                return R.fail("请输入管理员账号！");
            }
            //判断缓存中是否存在用户id
            if (redisService.get(CommonConst.ADMIN_TOKEN + user.getId(),String.class) != null) {
                adminToken =  redisService.get(CommonConst.ADMIN_TOKEN + user.getId(),String.class);
            }
        }
        else { //普通用户登录
            if (DataCacheUtil.get(CommonConst.USER_TOKEN + user.getId()) != null) {
                userToken = (String) redisService.get(CommonConst.USER_TOKEN + user.getId(),String.class);
            }
        }


        if (isAdmin && !StringUtils.hasText(adminToken)) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            adminToken = CommonConst.ADMIN_ACCESS_TOKEN + uuid;
            redisService.set(adminToken, user, CommonConst.TOKEN_EXPIRE);
            redisService.set(CommonConst.ADMIN_TOKEN + user.getId(), adminToken, CommonConst.TOKEN_EXPIRE);
        } else if (!isAdmin && !StringUtils.hasText(userToken)) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            userToken = CommonConst.USER_ACCESS_TOKEN + uuid;
            redisService.set(userToken, user, CommonConst.TOKEN_EXPIRE);
            redisService.set(CommonConst.USER_TOKEN + user.getId(), userToken, CommonConst.TOKEN_EXPIRE);
        }


        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setPassword(null);
        //是否是root用户
        if (isAdmin && user.getUserType() == ParamsEnum.USER_TYPE_ADMIN.getCode()) {
            userVO.setIsBoss(true);
        }

        if (isAdmin) {
            userVO.setAccessToken(adminToken);
        } else {
            userVO.setAccessToken(userToken);
        }
        return R.success(userVO);
    }

    /**
     * 根据请求头的权限鉴定来退出登录
     *
     * @param authorization
     * @return
     */
    @Override
    public R exitLogin(String authorization) {
        User user =redisService.get(authorization, User.class);
        log.info("***UserServiceImpl.exitLogin业务结束，结果:{}", authorization);
        if(user==null)
            return R.success();
       Integer  userId=user.getId();
            //删除USER_TOKEN+id
        if (authorization.contains(CommonConst.USER_ACCESS_TOKEN)) {
            redisService.remove(CommonConst.USER_TOKEN + userId);
        } else if (authorization.contains(CommonConst.ADMIN_ACCESS_TOKEN)) {
            redisService.remove(CommonConst.ADMIN_TOKEN + userId);
        }
        redisService.remove(authorization);
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
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
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

        IPage<User> page=new Page<>(searchUserParam.getCurrent(),searchUserParam.getSize());
        page= baseMapper.selectPage(page, queryWrapper);
        List<User> records = page.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            records.forEach(u -> {
                u.setPassword(null);
                u.setOpenId(null);
            });
        }
        log.info("***UserServiceImpl.userList业务结束，结果:{}",page.getRecords() );
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
    public R emailForBind(String place, Integer flag,String authorization) {
        int code = new Random().nextInt(900000) + 100000;
        if (flag == 1) {
            log.info(place + "---" + "手机验证码---" + code);
        } else if (flag == 2) {
            log.info(place + "---" + "邮箱验证码---" + code);
            List<String> mail = new ArrayList<>();
            mail.add(place);
//            获取样式
            String text = getCodeMail(code);
            WebInfo webInfo = (WebInfo) DataCacheUtil.get(CommonConst.WEB_INFO);
            mailUtil.sendMailMessage(mail, "您有一封来自" + (webInfo == null ? "yuan010" : webInfo.getWebName()) + "的回执！", text);
        }
        Integer userId=(redisService.get(authorization,User.class)).getId();
        redisService.set(CommonConst.USER_CODE + userId + "_" + place + "_" + flag, Integer.valueOf(code), 300);
        return R.success();
    }

    @Override
    public R updateSecretInfo(UserUpdateSecretInfoParam userUpdateSecretInfoParam, String authorization) {
      String  password = new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(userUpdateSecretInfoParam.getPassword()));
        Integer flag = userUpdateSecretInfoParam.getFlag();
        String place = userUpdateSecretInfoParam.getPlace();
        String code = userUpdateSecretInfoParam.getCode();

        User user=redisService.get(authorization,User.class);
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
            Integer codeCache =redisService.get(CommonConst.USER_CODE + user.getId() + "_" + place + "_" + flag,Integer.class);
            if (codeCache != null && codeCache.intValue() == Integer.parseInt(code)) {

                redisService.remove(CommonConst.USER_CODE + user.getId() + "_" + place + "_" + flag);

                user.setEmail(place);
            } else {
                return R.fail("验证码错误！");
            }
        }
        updateById(user);
        String Token=  redisService.get(CommonConst.USER_TOKEN+user.getId(),String.class);
        redisService.set(Token, user, CommonConst.TOKEN_EXPIRE);
        redisService.set(CommonConst.USER_TOKEN + user.getId(), Token, CommonConst.TOKEN_EXPIRE);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setPassword(null);
        log.info("***UserServiceImpl.updateSecretInfo业务结束，结果:{}",userVO );
        return R.success(userVO);
    }

    @Override
    public R getCodeForForgetPasswordOrRegister(String place, Integer flag) {
        int i = new Random().nextInt(900000) + 100000;
        if (flag == 1) {
            log.info(place + "---" + "手机验证码---" + i);
        } else if (flag == 2) {
            log.info(place + "---" + "邮箱验证码---" + i);

            List<String> mail = new ArrayList<>();
            mail.add(place);
            String text = getCodeMail(i);
            WebInfo webInfo = (WebInfo) DataCacheUtil.get(CommonConst.WEB_INFO);
            mailUtil.sendMailMessage(mail, "您有一封来自" + (webInfo == null ? "寻国记" : webInfo.getWebName()) + "的回执！", text);
        }
        redisService.set(CommonConst.FORGET_PASSWORD + place + "_" + flag, Integer.valueOf(i), 300);
        return R.success();
    }

    @Override
    public R updateForForgetPassword(ForForgetPasswordParam forForgetPasswordParam) {

      String  password = new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(forForgetPasswordParam.getPassword()));
        Integer flag = forForgetPasswordParam.getFlag();
        String code = forForgetPasswordParam.getCode();
        String place = forForgetPasswordParam.getPlace();
        Integer codeCache =  redisService.get(CommonConst.FORGET_PASSWORD + place + "_" + flag,Integer.class);
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
            Integer codeCache =  redisService.get(CommonConst.FORGET_PASSWORD + user.getEmail() + "_2",Integer.class);
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

        String userToken = CommonConst.USER_ACCESS_TOKEN + UUID.randomUUID().toString().replaceAll("-", "");
        redisService.set(userToken, one, CommonConst.TOKEN_EXPIRE);
        redisService.set(CommonConst.USER_TOKEN + one.getId(), userToken, CommonConst.TOKEN_EXPIRE);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(one, userVO);
        userVO.setPassword(null);
        userVO.setAccessToken(userToken);


        return R.success(userVO);
    }

    @Override
    public R updateUserInfo(User user, String authorization) {
        Integer userId=(redisService.get(authorization,User.class)).getId();
        if (StringUtils.hasText(user.getUsername())) {
            String regex = "\\d{11}";
            if (user.getUsername().matches(regex)) {
                return R.fail("用户名不能为11位数字！");
            }

            if (user.getUsername().contains("@")) {
                return R.fail("用户名不能包含@！");
            }
            Integer count = lambdaQuery().eq(User::getUsername, user.getUsername()).ne(User::getId,userId).count();
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
        redisService.set(authorization, one, CommonConst.TOKEN_EXPIRE);
        redisService.set(CommonConst.USER_TOKEN + one.getId(), authorization, CommonConst.TOKEN_EXPIRE);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(one, userVO);
        userVO.setPassword(null);
        return R.success(userVO);
    }

    @Override
    public R changeUserStatusOrTypeParam(UserStatusOrTypeParam userStatusOrTypeParam) {
        User user=new User();
        user.setId(userStatusOrTypeParam.getUserId());
        user.setUserStatus(userStatusOrTypeParam.getUserStatus());
        user.setUserType(userStatusOrTypeParam.getUserType());
        boolean b = updateById(user);
        //   该用户退出登录
        if(!b)
        {
            return R.fail("修改失败");
        }
        if(userStatusOrTypeParam.getUserStatus()!=null&&!userStatusOrTypeParam.getUserStatus())
        {     log.info("***UserController.changeUserStatusOrTypeParam业务结束，结果:{}",userStatusOrTypeParam.getUserStatus() );

            //管理员
            if(redisService.get(CommonConst.ADMIN_TOKEN + user.getId(),String.class) != null)
            {
                String token = redisService.get(CommonConst.ADMIN_TOKEN + user.getId(),String.class);
                redisService.remove(CommonConst.ADMIN_TOKEN + user.getId());
                redisService .remove(token);
            }//普通用户
            else if (redisService.get(CommonConst.USER_TOKEN + user.getId(),String.class) != null)
            {
                String token = (String) redisService.get(CommonConst.USER_TOKEN + user.getId(),String.class);
                redisService.remove(CommonConst.USER_TOKEN + user.getId());
                redisService.remove(token);
            }
        }

        return R.success();
    }

    /**
     * 设置邮件内容
     * @param code
     * @return
     */
    private String getCodeMail(int code) {
        WebInfo webInfo = (WebInfo) DataCacheUtil.get(CommonConst.WEB_INFO);
        String webName = (webInfo == null ? "starQuake" : webInfo.getWebName());
        return String.format(MailUtil.mailText,
                webName,
                "yuan",
                "yuan",
                String.format(codeFormat, code),
                "",
                webName);
    }

}
