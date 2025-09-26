package com.example.code.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新自己的应用（目前仅支持名称）
 */
@Data
public class AppUpdateMyRequest implements Serializable {

    /**
     * 应用 id
     */
    private Long id;

    /**
     * 新应用名称
     */
    private String appName;
}




