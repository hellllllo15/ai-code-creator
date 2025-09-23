package com.example.code.service;

import com.example.code.model.dto.user.UserQueryRequest;
import com.example.code.model.entity.User;
import com.example.code.model.vo.LoginUserVO;
import com.example.code.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


/**
 * 用户 服务层。
 *
 * @author Administrator
 * @since 2025-09-23
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);


    /**
     * 获取加密后的密码
     */
    public String getEncryptPassword(String userPassword) ;

    /**
     * 获取脱敏的已登录用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 获取脱敏后的单个用户
     */
    public UserVO getUserVO(User user);


    /**
     *获取脱敏后的用户列表
     */
    public List<UserVO> getUserVOList(List<User> userList);


    /**
     * 将查询请求转为 QueryWrapper 对象
     */
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);
}
