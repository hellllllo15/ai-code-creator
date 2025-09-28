package com.example.code.service;

import com.example.code.model.dto.chathistory.ChatHistoryQueryRequest;
import com.example.code.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.example.code.model.entity.ChatHistory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author Administrator
 * @since 2025-09-28
 */
public interface ChatHistoryService extends IService<ChatHistory> {


    /**
     *添加对话历史
     */
    public boolean addChatMessage(Long appId, String message, String messageType, Long userId);




    /**
     *根据应用id删除所有历史
     */
    public boolean deleteByAppId(Long appId);



    /**
     * 获取查询包装类
     *
     * @param chatHistoryQueryRequest
     * @return
     */
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);


    /**
     *游标查询历史记录
     */
    public Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                                      LocalDateTime lastCreateTime,
                                                      User loginUser);





}





























