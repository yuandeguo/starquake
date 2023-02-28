package com.yuan.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.UserMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.myEnum.ParamsEnum;
import com.yuan.params.LoginParam;
import com.yuan.params.SearchUserParam;
import com.yuan.vo.UserVO;
import com.yuan.pojo.User;
import com.yuan.service.UserService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.tio.core.Tio;

import java.nio.charset.StandardCharsets;
import java.util.List;
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


    @Override
    public R<UserVO> login(LoginParam loginParam) {
        String username=loginParam.getUsername();
        String password=loginParam.getPassword();
        Boolean isAdmin=loginParam.getIsAdmin();


        password = new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(password));//解密处理

        log.info("***UserServiceImpl.login业务，结果:{}",password );

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
            if (DataCacheUtil.get(CommonConst.ADMIN_TOKEN + user.getId()) != null) {
                adminToken = (String) DataCacheUtil.get(CommonConst.ADMIN_TOKEN + user.getId());
            }
        }
        else { //普通用户登录
            if (DataCacheUtil.get(CommonConst.USER_TOKEN + user.getId()) != null) {
                userToken = (String) DataCacheUtil.get(CommonConst.USER_TOKEN + user.getId());
            }
        }


        if (isAdmin && !StringUtils.hasText(adminToken)) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            adminToken = CommonConst.ADMIN_ACCESS_TOKEN + uuid;
            DataCacheUtil.put(adminToken, user, CommonConst.TOKEN_EXPIRE);
            DataCacheUtil.put(CommonConst.ADMIN_TOKEN + user.getId(), adminToken, CommonConst.TOKEN_EXPIRE);
        } else if (!isAdmin && !StringUtils.hasText(userToken)) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            userToken = CommonConst.USER_ACCESS_TOKEN + uuid;
            DataCacheUtil.put(userToken, user, CommonConst.TOKEN_EXPIRE);
            DataCacheUtil.put(CommonConst.USER_TOKEN + user.getId(), userToken, CommonConst.TOKEN_EXPIRE);
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
        User user = (User) DataCacheUtil.get(authorization);
        log.info("***UserServiceImpl.exitLogin业务结束，结果:{}", authorization);
       Integer  userId=user.getId();
            //删除USER_TOKEN+id
        if (authorization.contains(CommonConst.USER_ACCESS_TOKEN)) {
            DataCacheUtil.remove(CommonConst.USER_TOKEN + userId);
        } else if (authorization.contains(CommonConst.ADMIN_ACCESS_TOKEN)) {
            DataCacheUtil.remove(CommonConst.ADMIN_TOKEN + userId);
        }
        DataCacheUtil.remove(authorization);
        return R.success();
    }

    /**
     * 按条件查询用户集合
     *
     * @param searchUserParam
     * @return
     */
    @Override
    public List<User> userList(SearchUserParam searchUserParam) {
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

        log.info("***UserServiceImpl.userList业务结束，结果:{}", records);
        return records;
    }
}
