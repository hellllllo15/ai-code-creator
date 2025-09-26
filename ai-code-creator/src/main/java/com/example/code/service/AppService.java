package com.example.code.service;

import com.mybatisflex.core.service.IService;
import com.example.code.model.entity.App;
import com.example.code.model.dto.app.*;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.example.code.model.entity.User;
import com.example.code.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author Administrator
 * @since 2025-09-25
 */
public interface AppService extends IService<App> {
    /**
     * 用户创建应用（必须提供 initPrompt）
     * @param addRequest   创建请求
     * @param loginUser    当前登录用户
     * @return 新应用 id
     */
    Long createApp(AppAddRequest addRequest, User loginUser);

    /**
     * 用户更新自己的应用（目前仅支持名称）
     * @param updateRequest 更新请求
     * @param loginUser     当前登录用户
     * @return 是否成功
     */
    boolean updateMyApp(AppUpdateMyRequest updateRequest, User loginUser);

    /**
     * 用户删除自己的应用
     * @param id        应用 id
     * @param loginUser 当前登录用户
     * @return 是否成功
     */
    boolean deleteMyApp(Long id, User loginUser);

    /**
     * 用户查看自己的应用详情
     * @param id        应用 id
     * @param loginUser 当前登录用户
     * @return 应用实体
     */
    App getMyAppById(Long id, User loginUser);

    /**
     * 用户分页查询自己的应用列表（每页最多 20）
     * @param queryRequest 查询请求（支持名称）
     * @param loginUser    当前登录用户
     * @return 应用分页
     */
    Page<App> listMyAppByPage(AppQueryRequest queryRequest, User loginUser);


    /**
     *聊天生成代码
     */
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser);


    /**
     *部署服务
     */
    public String deployApp(Long appId, User loginUser);


    /**
     * 构建查询条件（不包含时间区间字段）
     * @param queryRequest 查询请求
     * @return QueryWrapper
     */
    QueryWrapper getQueryWrapper(AppQueryRequest queryRequest);

    /**
     * 获取脱敏后的 AppVO
     * @param app App 实体
     * @return AppVO
     */
    AppVO getAppVO(App app);

    /**
     * 获取脱敏后的 AppVO 列表
     * @param appList App 实体列表
     * @return AppVO 列表
     */
    List<AppVO> getAppVOList(List<App> appList);
}
