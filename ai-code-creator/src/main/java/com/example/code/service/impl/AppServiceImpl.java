package com.example.code.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.example.code.ai.model.enums.CodeGenTypeEnum;
import com.example.code.constant.AppConstant;
import com.example.code.core.AiCodeGeneratorFacade;
import com.example.code.core.builder.VueProjectBuilder;
import com.example.code.core.handler.StreamHandlerExecutor;
import com.example.code.exception.ThrowUtils;
import com.example.code.model.enums.ChatHistoryMessageTypeEnum;
import com.example.code.model.vo.UserVO;
import com.example.code.service.ChatHistoryService;
import com.example.code.service.UserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.example.code.model.entity.App;
import com.example.code.mapper.AppMapper;
import com.example.code.service.AppService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.example.code.model.dto.app.*;
import com.example.code.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.example.code.exception.BusinessException;
import com.example.code.exception.ErrorCode;
import cn.hutool.core.util.StrUtil;
import com.example.code.model.vo.AppVO;
import cn.hutool.core.bean.BeanUtil;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author Administrator
 * @since 2025-09-25
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{



    @Resource
    UserService userService;

    @Resource
    AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Resource
    ChatHistoryService chatHistoryService;


    @Resource
    StreamHandlerExecutor streamHandlerExecutor;


    @Resource
    VueProjectBuilder vueProjectBuilder;





    /**
     * 用户创建应用
     */
    @Override
    public Long createApp(AppAddRequest addRequest, User loginUser) {
        if (addRequest == null || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StrUtil.isBlank(addRequest.getInitPrompt())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "initPrompt 必填");
        }
        App app = new App();

        app.setAppName(addRequest.getAppName());
        app.setInitPrompt(addRequest.getInitPrompt());
        app.setCover(addRequest.getCover());
       // app.setCodeGenType(addRequest.getCodeGenType());

        //暂时为多文件生成
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());
        app.setUserId(loginUser.getId());
        boolean saved = this.save(app);
        if (!saved) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"数据库操作失败");
        }
        return app.getId();
    }

    /**
     * 用户更新自己的应用（仅名称）
     */
    @Override
    public boolean updateMyApp(AppUpdateMyRequest updateRequest, User loginUser) {
        if (updateRequest == null || updateRequest.getId() == null || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        App db = this.getById(updateRequest.getId());
        if (db == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"应用不存在");
        }
        if (!db.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"没有权限");
        }
        App update = new App();
        // 设置编辑时间
        update.setEditTime(LocalDateTime.now());

        update.setId(updateRequest.getId());
        update.setAppName(updateRequest.getAppName());
        return this.updateById(update);
    }

    /**
     * 用户删除自己的应用
     */
    @Override
    public boolean deleteMyApp(Long id, User loginUser) {
        if (id == null || id <= 0 || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        App db = this.getById(id);
        if (db == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"应用不存在");
        }
        if (!db.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"没有权限");
        }
        return this.removeById(id);
    }

    /**
     * 用户查看自己的应用详情
     */
    @Override
    public App getMyAppById(Long id, User loginUser) {
        if (id == null || id <= 0 || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        App db = this.getById(id);
        if (db == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"没有应用");
        }
        if (!db.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"没有权限");
        }
        return db;
    }

    /**
     * 用户分页查询自己的应用
     */
    @Override
    public Page<App> listMyAppByPage(AppQueryRequest appQueryRequest, User loginUser) {
        if (appQueryRequest == null || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");

        }



        appQueryRequest.setUserId(loginUser.getId());
        int pageNum = Math.max(1, appQueryRequest.getPageNum());
        int pageSize = Math.min(20, Math.max(1, appQueryRequest.getPageSize()));
        return this.page(Page.of(pageNum, pageSize), getQueryWrapper(appQueryRequest));
    }


    /**
     *聊天生成代码
     */
    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限访问该应用，仅本人可以生成代码
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }
        // 4. 获取应用的代码生成类型
        String codeGenTypeStr = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        }
        // 5. 通过校验后，添加用户消息到对话历史
        chatHistoryService.addChatMessage(appId, message, ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());
// 6. 调用 AI 生成代码（流式）
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
// 7. 收集 AI 响应内容并在完成后记录到对话历史
        return streamHandlerExecutor.doExecute(codeStream, chatHistoryService, appId, loginUser, codeGenTypeEnum);

    }



    /**
     * 删除应用时关联删除对话历史
     *
     * @param id 应用ID
     * @return 是否成功
     */
    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }
        // 转换为 Long 类型
        Long appId = Long.valueOf(id.toString());
        if (appId <= 0) {
            return false;
        }
        // 先删除关联的对话历史
        try {
            chatHistoryService.deleteByAppId(appId);
        } catch (Exception e) {
            // 记录日志但不阻止应用删除
           // log.error("删除应用关联对话历史失败: {}", e.getMessage());
        }
        // 删除应用
        return super.removeById(id);
    }






    /**
     *部署服务
     */
    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限部署该应用，仅本人可以部署
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");
        }
        // 4. 检查是否已有 deployKey
        String deployKey = app.getDeployKey();
        // 没有则生成 6 位 deployKey（大小写字母 + 数字）
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        // 5. 获取代码生成类型，构建源目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 6. 检查源目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
        }
      // 7. Vue 项目特殊处理：执行构建
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {
            // Vue 项目需要构建
            boolean buildSuccess = vueProjectBuilder.buildProject(sourceDirPath);
            ThrowUtils.throwIf(!buildSuccess, ErrorCode.SYSTEM_ERROR, "Vue 项目构建失败，请检查代码和依赖");
            // 检查 dist 目录是否存在
            File distDir = new File(sourceDirPath, "dist");
            ThrowUtils.throwIf(!distDir.exists(), ErrorCode.SYSTEM_ERROR, "Vue 项目构建完成但未生成 dist 目录");
            // 将 dist 目录作为部署源
            sourceDir = distDir;
           // log.info("Vue 项目构建成功，将部署 dist 目录: {}", distDir.getAbsolutePath());
        }


        // 8. 复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败：" + e.getMessage());
        }
        // 9. 更新应用的 deployKey 和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 10. 返回可访问的 URL
        return String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }




    /**
     * 构建查询条件
     */
    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    /**
     * 获取脱敏 VO
     */
    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    /**
     * 获取脱敏 VO 列表
     */
    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }
}
