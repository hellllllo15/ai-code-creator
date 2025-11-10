package com.example.code.core;

import java.util.regex.Pattern;

/**
 * 聊天历史过滤测试类
 * 用于测试过滤工具调用文本的方法
 */
public class ChatHistoryFilterTest {

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

    /**
     * 主方法用于测试
     */
    public static void main(String[] args) {
        // 测试数据1：包含工具调用的完整消息（来自终端信息）
        String testMessage1 = """
            我将修改登录页面，将"用户登录"改为"学生登录"。

            [工具调用] 写入文件 src/pages/Login.vue
            ```vue
            <template>
              <div class="login-container">
                <div class="login-box">
                  <h2>学生登录</h2>
                  <form @submit.prevent="handleLogin">
                    <div class="input-group">
                      <input 
                        v-model="username" 
                        type="text" 
                        placeholder="学号" 
                        required
                      >
                    </div>
                    <div class="input-group">
                      <input 
                        v-model="password" 
                        type="password" 
                        placeholder="密码" 
                        required
                      >
                    </div>
                    <button type="submit">登录</button>
                  </form>
                </div>
              </div>
            </template>

            <script setup>
            import { ref } from 'vue'
            import { useRouter } from 'vue-router'

            const router = useRouter()
            const username = ref('')
            const password = ref('')

            const handleLogin = () => {
              // 直接跳转到主页，无需验证
              router.push('/home')
            }
            </script>

            <style scoped>
            .login-container {
              display: flex;
              justify-content: center;
              align-items: center;
              min-height: 100vh;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            }

            .login-box {
              background: white;
              padding: 2rem;
              border-radius: 8px;
              box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
              width: 300px;
            }

            h2 {
              text-align: center;
              margin-bottom: 1.5rem;
              color: #333;
            }

            .input-group {
              margin-bottom: 1rem;
            }

            input {
              width: 100%;
              padding: 0.75rem;
              border: 1px solid #ddd;
              border-radius: 4px;
              font-size: 1rem;
            }

            input:focus {
              outline: none;
              border-color: #667eea;
            }

            button {
              width: 100%;
              padding: 0.75rem;
              background: #667eea;
              color: white;
              border: none;
              border-radius: 4px;
              font-size: 1rem;
              cursor: pointer;
            }

            button:hover {
              background: #5a6fd8;
            }
            </style>
            ```


            修改完成！已将登录页面的标题从"用户登录"改为"学生登录"，同时将用户名输入框的占位符改为"学号"。
            """;

        System.out.println("========== 测试1：包含工具调用的消息 ==========");
        System.out.println("\n【原始消息】：");
        System.out.println("----------------------------------------");
        System.out.println(testMessage1);
        System.out.println("----------------------------------------");
        String filtered1 = filterToolCallText(testMessage1);
        System.out.println("\n【过滤后】：");
        System.out.println("========================================");
        System.out.println(filtered1);
        System.out.println("========================================");
        System.out.println("\n");

        // 测试数据2：包含多个工具调用的消息
        String testMessage2 = """
            我将为您创建一个极简的登录页面，包含最基础的登录功能。

            **网站生成计划：**
            - 创建基础项目结构和配置文件
            - 实现极简登录页面
            - 包含用户名和密码输入框
            - 添加登录按钮和基础样式

            现在开始创建文件：

            [工具调用] 写入文件 package.json
            ```json
            {
              "name": "simple-login",
              "version": "1.0.0"
            }
            ```


            [工具调用] 写入文件 src/main.js
            ```js
            import { createApp } from 'vue'
            createApp(App).mount('#app')
            ```


            **生成完毕提示：**
            极简登录页面项目已创建完成。
            """;

        System.out.println("========== 测试2：包含多个工具调用的消息 ==========");
        System.out.println("\n【原始消息】：");
        System.out.println("----------------------------------------");
        System.out.println(testMessage2);
        System.out.println("----------------------------------------");
        String filtered2 = filterToolCallText(testMessage2);
        System.out.println("\n【过滤后】：");
        System.out.println("========================================");
        System.out.println(filtered2);
        System.out.println("========================================");
        System.out.println("\n");

        // 测试数据3：包含[选择工具]的消息
        String testMessage3 = """
            我将修改登录页面的标题。


            [选择工具] 写入文件


            [工具调用] 写入文件 src/pages/Login.vue
            ```vue
            <h2>学生登录</h2>
            ```


            修改完成！
            """;

        System.out.println("========== 测试3：包含[选择工具]的消息 ==========");
        System.out.println("\n【原始消息】：");
        System.out.println("----------------------------------------");
        System.out.println(testMessage3);
        System.out.println("----------------------------------------");
        String filtered3 = filterToolCallText(testMessage3);
        System.out.println("\n【过滤后】：");
        System.out.println("========================================");
        System.out.println(filtered3);
        System.out.println("========================================");
        System.out.println("\n");

        // 测试数据4：不包含工具调用的纯文本消息
        String testMessage4 = """
            你好！欢迎使用这个极简登录页面项目。

            这个项目包含了：
            - 一个简洁的登录界面
            - 用户名和密码输入框
            - 登录按钮
            - 响应式设计
            """;

        System.out.println("========== 测试4：不包含工具调用的纯文本消息 ==========");
        System.out.println("\n【原始消息】：");
        System.out.println("----------------------------------------");
        System.out.println(testMessage4);
        System.out.println("----------------------------------------");
        String filtered4 = filterToolCallText(testMessage4);
        System.out.println("\n【过滤后】：");
        System.out.println("========================================");
        System.out.println(filtered4);
        System.out.println("========================================");
        System.out.println("\n");

        // 测试数据5：FileModifyTool格式（包含替换前后对比）
        String testMessage5Modify = """
            我将修改登录页面的标题。

            [工具调用] 修改文件 src/pages/Login.vue
            
            替换前：
            ```
            <h2>学生登录</h2>
            ```
            
            替换后：
            ```
            <h2>登录测试主要页面</h2>
            ```


            修改完成！
            """;

        System.out.println("========== 测试5：FileModifyTool格式 ==========");
        System.out.println("\n【原始消息】：");
        System.out.println("----------------------------------------");
        System.out.println(testMessage5Modify);
        System.out.println("----------------------------------------");
        String filtered5Modify = filterToolCallText(testMessage5Modify);
        System.out.println("\n【过滤后】：");
        System.out.println("========================================");
        System.out.println(filtered5Modify);
        System.out.println("========================================");
        System.out.println("\n");

        // 测试数据6：来自终端信息的实际数据
        String testMessage5 = """
            我将修改登录页面的标题为"登录测试主要页面"。

            [工具调用] 写入文件 src/pages/Login.vue
            ```vue
            <template>
              <div class="login-container">
                <div class="login-box">
                  <h2>登录测试主要页面</h2>
                  <form @submit.prevent="handleLogin">
                    <div class="input-group">
                      <input 
                        v-model="username" 
                        type="text" 
                        placeholder="学号" 
                        required
                      >
                    </div>
                    <div class="input-group">
                      <input 
                        v-model="password" 
                        type="password" 
                        placeholder="密码" 
                        required
                      >
                    </div>
                    <button type="submit">登录</button>
                  </form>
                </div>
              </div>
            </template>

            <script setup>
            import { ref } from 'vue'
            import { useRouter } from 'vue-router'

            const router = useRouter()
            const username = ref('')
            const password = ref('')

            const handleLogin = () => {
              // 直接跳转到主页，无需验证
              router.push('/home')
            }
            </script>

            <style scoped>
            .login-container {
              display: flex;
              justify-content: center;
              align-items: center;
              min-height: 100vh;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            }

            .login-box {
              background: white;
              padding: 2rem;
              border-radius: 8px;
              box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
              width: 300px;
            }

            h2 {
              text-align: center;
              margin-bottom: 1.5rem;
              color: #333;
            }

            .input-group {
              margin-bottom: 1rem;
            }

            input {
              width: 100%;
              padding: 0.75rem;
              border: 1px solid #ddd;
              border-radius: 4px;
              font-size: 1rem;
            }

            input:focus {
              outline: none;
              border-color: #667eea;
            }

            button {
              width: 100%;
              padding: 0.75rem;
              background: #667eea;
              color: white;
              border: none;
              border-radius: 4px;
              font-size: 1rem;
              cursor: pointer;
            }

            button:hover {
              background: #5a6fd8;
            }
            </style>
            ```


            修改完成！已将登录页面的标题从"学生登录"改为"登录测试主要页面"。
            """;

        System.out.println("========== 测试6：终端信息中的实际数据 ==========");
        System.out.println("\n【原始消息】：");
        System.out.println("----------------------------------------");
        System.out.println(testMessage5);
        System.out.println("----------------------------------------");
        String filtered5 = filterToolCallText(testMessage5);
        System.out.println("\n【过滤后】：");
        System.out.println("========================================");
        System.out.println(filtered5);
        System.out.println("========================================");
        System.out.println("\n");

        // 验证结果
        System.out.println("========== 验证结果 ==========");
        System.out.println("测试1 - 是否包含[工具调用]: " + filtered1.contains("[工具调用]"));
        System.out.println("测试1 - 是否包含代码块: " + filtered1.contains("```vue"));
        System.out.println("测试1 - 是否保留纯文本: " + filtered1.contains("我将修改登录页面"));
        System.out.println("测试1 - 是否保留结尾文本: " + filtered1.contains("修改完成"));
        
        System.out.println("\n测试5(Modify) - 是否包含[工具调用]: " + filtered5Modify.contains("[工具调用]"));
        System.out.println("测试5(Modify) - 是否包含代码块: " + filtered5Modify.contains("```"));
        System.out.println("测试5(Modify) - 是否保留纯文本: " + filtered5Modify.contains("我将修改登录页面的标题"));
        System.out.println("测试5(Modify) - 是否保留结尾文本: " + filtered5Modify.contains("修改完成"));
        
        System.out.println("\n测试6(终端数据) - 是否包含[工具调用]: " + filtered5.contains("[工具调用]"));
        System.out.println("测试6(终端数据) - 是否包含代码块: " + filtered5.contains("```vue"));
        System.out.println("测试6(终端数据) - 是否保留纯文本: " + filtered5.contains("我将修改登录页面的标题"));
        System.out.println("测试6(终端数据) - 是否保留结尾文本: " + filtered5.contains("修改完成"));
    }
}

