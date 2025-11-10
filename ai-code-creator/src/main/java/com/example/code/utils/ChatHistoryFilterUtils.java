package com.example.code.utils;

import java.util.regex.Pattern;

/**
 * 聊天历史过滤工具类
 * 用于过滤历史记录中的工具调用文本，只保留AI的纯文本回复
 */
public class ChatHistoryFilterUtils {

    /**
     * 过滤工具调用相关的文本
     * 保留AI的纯文本回复，移除工具调用的格式化文本
     * 
     * @param originalMessage 原始消息（包含工具调用文本）
     * @return 过滤后的消息（只包含AI的纯文本回复）
     */
    public static String filterToolCallText(String originalMessage) {
        if (originalMessage == null || originalMessage.isEmpty()) {
            return originalMessage;
        }

        String result = originalMessage;

        // 1. 过滤 [选择工具] 格式：\n\n[选择工具] xxx\n\n
        Pattern selectToolPattern = Pattern.compile(
            "\n\n\\[选择工具\\] .+?\n\n",
            Pattern.DOTALL
        );
        result = selectToolPattern.matcher(result).replaceAll("");

        // 2. 分步过滤 [工具调用] 格式（更可靠的方法）
        // 第一步：过滤 [工具调用] 标记及其后面的内容（包含代码块的情况）
        // 匹配从 \n\n[工具调用] 开始，到最后一个代码块结束后的 \n\n
        // 使用贪婪匹配，匹配到最后一个代码块结束后的 \n\n
        Pattern toolCallWithCodeBlockPattern = Pattern.compile(
            "\n\n\\[工具调用\\] [\\s\\S]*?```[\\s\\S]*?```\n\n",
            Pattern.DOTALL
        );
        // 需要多次匹配，因为可能有多个代码块（FileModifyTool的情况）
        String previousResult;
        do {
            previousResult = result;
            result = toolCallWithCodeBlockPattern.matcher(result).replaceAll("");
        } while (!result.equals(previousResult));
        
        // 第一步补充：过滤没有代码块的简单格式（FileReadTool等）
        Pattern toolCallSimplePattern = Pattern.compile(
            "\n\n\\[工具调用\\] [^\\n]+\\n\\n",
            Pattern.DOTALL
        );
        result = toolCallSimplePattern.matcher(result).replaceAll("");
        
        // 第二步：单独过滤残留的代码块（```xxx\n...\n```）
        // 只过滤前后有空行的独立代码块（工具调用产生的代码块通常是独立的）
        // 匹配：\n\n```xxx\n...\n```\n\n 或 \n```xxx\n...\n```\n
        Pattern codeBlockPattern = Pattern.compile(
            "\n+```[\\w]*\\n[\\s\\S]*?```\n+",
            Pattern.DOTALL
        );
        result = codeBlockPattern.matcher(result).replaceAll("");

        // 第三步：处理消息末尾的情况（如果工具调用在消息末尾）
        Pattern toolCallAtEndPattern = Pattern.compile(
            "\n\n\\[工具调用\\] [\\s\\S]+$",
            Pattern.DOTALL
        );
        result = toolCallAtEndPattern.matcher(result).replaceAll("");

        // 4. 清理多余的连续换行（保留最多2个连续换行）
        result = result.replaceAll("\n{3,}", "\n\n");

        // 5. 清理开头和结尾的换行
        result = result.trim();

        return result;
    }
}


