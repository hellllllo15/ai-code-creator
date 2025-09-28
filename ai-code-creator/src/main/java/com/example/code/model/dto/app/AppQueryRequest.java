package com.example.code.model.dto.app;

import com.example.code.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 应用查询请求
 */
@Data
public class AppQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String appName;


    private String initPrompt;

    private String cover;

    private String codeGenType;

    private String deployKey;

    private Integer priority;

    private Long userId;
}





