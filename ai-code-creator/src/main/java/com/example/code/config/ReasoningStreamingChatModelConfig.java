package com.example.code.config;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReasoningStreamingChatModelConfig {

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String reasoningBaseUrl;
    
    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String reasoningApiKey;
    
    @Value("${langchain4j.open-ai.streaming-chat-model.base-url}")
    private String streamingBaseUrl;
    
    @Value("${langchain4j.open-ai.streaming-chat-model.api-key}")
    private String streamingApiKey;

    /**
     * 推理流式模型（用于 Vue 项目生成，带工具调用）
     */
    @Bean(name = "reasoningStreamingChatModel")
    public StreamingChatModel reasoningStreamingChatModel() {
        // 为了测试方便临时修改
        final String modelName = "deepseek-chat";
        final int maxTokens = 8192;
        // 生产环境使用：
        // final String modelName = "deepseek-reasoner";
        // final int maxTokens = 32768;
        return OpenAiStreamingChatModel.builder()
                .apiKey(reasoningApiKey)
                .baseUrl(reasoningBaseUrl)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
    
    /**
     * 默认流式模型（用于 HTML 和多文件生成）
     */
    @Bean(name = "streamingChatModel")
    public StreamingChatModel streamingChatModel() {
        final String modelName = "deepseek-chat";
        final int maxTokens = 8192;
        return OpenAiStreamingChatModel.builder()
                .apiKey(streamingApiKey)
                .baseUrl(streamingBaseUrl)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
