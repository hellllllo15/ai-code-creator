package com.example.code.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/// ai服务创建工厂
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;


    @Resource
    private StreamingChatModel streamingChatModel;


    /**
     *普通ai调用
     */
    @Bean
    public AiCodeGeneratorService OrdinaryAiCodeGeneratorService() {
        return AiServices.create(AiCodeGeneratorService.class, chatModel);
    }



    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }
}
