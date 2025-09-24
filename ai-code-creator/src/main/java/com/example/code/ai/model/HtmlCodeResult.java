package com.example.code.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
@Description("生成 HTML 代码文件的结果")
@Data
/// 单页面ai回复封装
public class HtmlCodeResult {


    @Description("HTML代码")
    private String htmlCode;


    @Description("生成代码的描述")
    private String description;
}
