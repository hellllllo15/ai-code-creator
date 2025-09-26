package com.example.code.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用脱敏返回对象
 */
@Data
public class AppVO {

    private Long id;

    private String appName;

    private String cover;


    private String initPrompt;



    private String codeGenType;

    private String deployKey;

    private LocalDateTime deployedTime;

    private Integer priority;

    private Long userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 创建用户信息
     */
    private UserVO user;
}




