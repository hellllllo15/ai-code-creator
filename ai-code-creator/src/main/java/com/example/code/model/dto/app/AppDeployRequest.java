package com.example.code.model.dto.app;

import lombok.Data;

import java.io.Serializable;

@Data
/// 部署请求类
public class AppDeployRequest implements Serializable {

    /**
     * 应用 id
     */
    private Long appId;

    private static final long serialVersionUID = 1L;
}
