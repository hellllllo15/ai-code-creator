package com.example.code.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建应用请求
 */
@Data
public class AppAddRequest implements Serializable {

    /*
    应用名称需要填写,没有就按照提示词前10个字符，其他参数暂时不填
    代码类型暂时为多文件生成
     */




    /**
     * 应用名称
     */
    private String appName;

    /**
     * 初始化 Prompt（必填）
     */
    private String initPrompt;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 代码生成类型（可选）
     */
    private String codeGenType;
}





