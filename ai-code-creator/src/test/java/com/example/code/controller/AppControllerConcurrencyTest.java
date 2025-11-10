package com.example.code.controller;

import com.example.code.model.entity.App;
import com.example.code.model.entity.User;
import com.example.code.service.AppService;
import com.example.code.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 测试AI对话的并发执行情况
 * 
 * 测试目的：验证是否存在"接口并发接收，但AI回复串行"的问题
 * 
 * 关键验证点：
 * 1. 接口层：两个请求是否同时被接收和处理
 * 2. AI响应层：两个请求的AI响应是否同时开始返回（并发）还是串行返回
 * 3. 流式数据处理：流式数据是否同时处理
 * 
 * 问题场景：
 * - 如果ChatModel采用单例模式，虽然StreamingChatModel返回Flux响应式流，
 *   但底层SpringRestClient.execute()方法内部可能是同步解析数据流的，
 *   导致虽然接口可以并发接收请求，但AI回复时是串行的
 * 
 * 判断标准：
 * - 如果两个请求的首块响应时间差 < 5秒：AI响应并发，不存在串行问题
 * - 如果两个请求的首块响应时间差 >= 5秒：AI响应可能串行，存在性能瓶颈
 * 
 * 注意：本测试使用两个不同的appId，确保对话历史相互独立
 */
@Slf4j
@SpringBootTest
class AppControllerConcurrencyTest {

    @Resource
    private AppService appService;

    @Resource
    private UserService userService;

    /**
     * 测试并发执行两个AI对话请求（使用不同的appId）
     * 验证是否存在"接口并发接收，但AI回复串行"的问题
     * 
     * 关键验证点：
     * 1. 两个请求是否同时发送HTTP请求到AI API
     * 2. AI响应是否同时开始返回（并发）还是串行返回
     * 3. 流式数据是否同时处理
     */
    @Test
    void testConcurrentAiChat() throws InterruptedException {
        // 1. 准备测试数据：获取一个测试用户和两个不同的应用
        User testUser = prepareTestUser();
        App testApp1 = prepareTestApp1(testUser);
        App testApp2 = prepareTestApp2(testUser);
        
        if (testUser == null || testApp1 == null || testApp2 == null) {
            log.warn("测试数据准备失败，跳过并发测试");
            return;
        }

        log.info("使用不同的appId进行并发测试：appId1={}, appId2={}", testApp1.getId(), testApp2.getId());
        log.info("测试目标：验证是否存在'接口并发但AI回复串行'的问题");
        log.info("问题描述：ChatModel采用单例模式，即使不同的AI Service实例，只要共享同一个ChatModel，也会出现阻塞");
        log.info("验证点：1)接口是否并发接收 2)AI响应是否串行 3)是否因为共享ChatModel导致");

        // 2. 创建线程池来模拟并发请求
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        
        // 3. 用于记录每个请求的执行时间
        List<RequestTiming> timings = new CopyOnWriteArrayList<>();
        
        // 4. 第一个请求（使用appId1）
        executorService.submit(() -> {
            RequestTiming timing = new RequestTiming("请求1(appId=" + testApp1.getId() + ")");
            timing.startTime = System.currentTimeMillis();
            log.info("【请求1】开始执行，appId={}, 时间戳: {}", testApp1.getId(), timing.startTime);
            
            try {
                Flux<String> flux = appService.chatToGenCode(
                    testApp1.getId(), 
                    "生成一个简单的Hello World页面", 
                    testUser
                );
                
                // 记录第一个数据块到达的时间（AI开始响应的时间）
                AtomicBoolean firstChunkReceived = new AtomicBoolean(false);
                
                flux = flux.doOnNext(chunk -> {
                    if (!firstChunkReceived.get()) {
                        firstChunkReceived.set(true);
                        timing.firstChunkTime = System.currentTimeMillis();
                        long timeToFirstChunk = timing.firstChunkTime - timing.startTime;
                        log.info("【请求1】收到第一个AI响应数据块，appId={}, 距离开始: {}ms, 时间戳: {}", 
                            testApp1.getId(), timeToFirstChunk, timing.firstChunkTime);
                    }
                });
                
                // 等待流完成，最多60秒
                flux.blockLast(Duration.ofSeconds(60));
                
                timing.endTime = System.currentTimeMillis();
                timing.duration = timing.endTime - timing.startTime;
                log.info("【请求1】执行完成，appId={}, 时间戳: {}, 总耗时: {}ms, 首块耗时: {}ms", 
                    testApp1.getId(), timing.endTime, timing.duration,
                    timing.firstChunkTime > 0 ? (timing.firstChunkTime - timing.startTime) : -1);
                
            } catch (Exception e) {
                timing.endTime = System.currentTimeMillis();
                timing.duration = timing.endTime - timing.startTime;
                timing.error = e.getMessage();
                log.error("【请求1】执行异常，appId={}: {}", testApp1.getId(), e.getMessage(), e);
            } finally {
                timings.add(timing);
                latch.countDown();
            }
        });

        // 5. 第二个请求（使用appId2，几乎同时启动）
        executorService.submit(() -> {
            // 稍微延迟一点启动，确保能看到并发效果
            try {
                Thread.sleep(100); // 延迟100ms启动
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            RequestTiming timing = new RequestTiming("请求2(appId=" + testApp2.getId() + ")");
            timing.startTime = System.currentTimeMillis();
            log.info("【请求2】开始执行，appId={}, 时间戳: {}", testApp2.getId(), timing.startTime);
            
            try {
                Flux<String> flux = appService.chatToGenCode(
                    testApp2.getId(), 
                    "生成一个简单的计数器组件", 
                    testUser
                );
                
                // 记录第一个数据块到达的时间（AI开始响应的时间）
                AtomicBoolean firstChunkReceived = new AtomicBoolean(false);
                
                flux = flux.doOnNext(chunk -> {
                    if (!firstChunkReceived.get()) {
                        firstChunkReceived.set(true);
                        timing.firstChunkTime = System.currentTimeMillis();
                        long timeToFirstChunk = timing.firstChunkTime - timing.startTime;
                        log.info("【请求2】收到第一个AI响应数据块，appId={}, 距离开始: {}ms, 时间戳: {}", 
                            testApp2.getId(), timeToFirstChunk, timing.firstChunkTime);
                    }
                });
                
                // 等待流完成，最多60秒
                flux.blockLast(Duration.ofSeconds(60));
                
                timing.endTime = System.currentTimeMillis();
                timing.duration = timing.endTime - timing.startTime;
                log.info("【请求2】执行完成，appId={}, 时间戳: {}, 总耗时: {}ms, 首块耗时: {}ms", 
                    testApp2.getId(), timing.endTime, timing.duration,
                    timing.firstChunkTime > 0 ? (timing.firstChunkTime - timing.startTime) : -1);
                
            } catch (Exception e) {
                timing.endTime = System.currentTimeMillis();
                timing.duration = timing.endTime - timing.startTime;
                timing.error = e.getMessage();
                log.error("【请求2】执行异常，appId={}: {}", testApp2.getId(), e.getMessage(), e);
            } finally {
                timings.add(timing);
                latch.countDown();
            }
        });

        // 6. 等待两个请求都完成
        boolean finished = latch.await(120, TimeUnit.SECONDS);
        executorService.shutdown();

        // 7. 分析结果
        log.info("\n========== 并发测试结果分析 ==========");
        if (!finished) {
            log.warn("测试超时，部分请求可能未完成");
        }

        for (RequestTiming timing : timings) {
            log.info("{}: 开始时间={}, 结束时间={}, 耗时={}ms, 错误={}", 
                timing.requestName, 
                timing.startTime, 
                timing.endTime, 
                timing.duration,
                timing.error != null ? timing.error : "无");
        }

        // 8. 判断是否并发执行，特别关注AI回复是否串行
        if (timings.size() == 2) {
            RequestTiming timing1 = timings.get(0);
            RequestTiming timing2 = timings.get(1);
            
            // 如果两个请求的开始时间接近，且执行时间有重叠，说明是并发执行
            long startTimeDiff = Math.abs(timing1.startTime - timing2.startTime);
            boolean hasOverlap = !(timing1.endTime < timing2.startTime || timing2.endTime < timing1.startTime);
            
            log.info("\n========== 并发性分析 ==========");
            log.info("两个请求开始时间差: {}ms", startTimeDiff);
            log.info("执行时间是否有重叠: {}", hasOverlap);
            
            // 关键验证：AI响应是否串行
            if (timing1.firstChunkTime > 0 && timing2.firstChunkTime > 0) {
                long firstChunkTimeDiff = Math.abs(timing1.firstChunkTime - timing2.firstChunkTime);
                
                log.info("\n========== AI响应时间分析（关键验证） ==========");
                log.info("请求1首块响应时间: {}ms (距离开始)", timing1.firstChunkTime - timing1.startTime);
                log.info("请求2首块响应时间: {}ms (距离开始)", timing2.firstChunkTime - timing2.startTime);
                log.info("两个请求首块响应时间差: {}ms", firstChunkTimeDiff);
                
                // 判断是否存在"接口并发但AI回复串行"的问题
                if (startTimeDiff < 1000) {
                    // 接口几乎同时接收请求
                    if (firstChunkTimeDiff < 5000) {
                        // 两个请求的首块响应时间差小于5秒，认为是并发响应
                        log.info("✅ AI响应并发: 两个请求的AI响应几乎同时开始（时间差{}ms）", firstChunkTimeDiff);
                        log.info("✅ 结论: 接口并发接收，AI回复也是并发的，不存在串行问题");
                        log.info("✅ 说明: 即使ChatModel是单例，底层也能正确处理并发请求");
                    } else {
                        // 两个请求的首块响应时间差较大，可能是串行响应
                        log.warn("⚠️  AI响应可能串行: 两个请求的AI响应时间差较大（{}ms，约{}秒）", 
                            firstChunkTimeDiff, firstChunkTimeDiff / 1000);
                        log.warn("⚠️  结论: 接口并发接收，但AI回复是串行的（一个回复完后另一个才开始回复）");
                        log.warn("⚠️  问题根源: ChatModel采用了单例模式，虽然返回Flux响应式流，");
                        log.warn("⚠️           但底层SpringRestClient.execute()方法内部是同步解析数据流的");
                        log.warn("⚠️  影响: 即使不同的AI Service实例，只要共享同一个ChatModel，也会阻塞");
                        log.warn("⚠️  建议: 需要修改ChatModel的配置，使其支持真正的并发处理");
                        
                        // 计算阻塞时间
                        long blockingTime = firstChunkTimeDiff - (timing2.firstChunkTime - timing2.startTime);
                        if (blockingTime > 0) {
                            log.warn("⚠️  阻塞时间: 请求2被阻塞了约{}ms（{}秒）", 
                                blockingTime, blockingTime / 1000);
                        }
                    }
                }
            } else {
                log.warn("⚠️  无法获取首块响应时间，无法验证AI响应是否串行");
            }
            
            // 总体执行时间分析
            log.info("\n========== 总体结论 ==========");
            if (startTimeDiff < 1000 && hasOverlap) {
                log.info("✅ 接口层: 两个不同appId的请求是并发接收的（时间重叠）");
            } else if (startTimeDiff < 1000 && !hasOverlap) {
                log.warn("⚠️  接口层: 两个不同appId的请求几乎同时开始，但执行时间没有重叠（串行执行）");
            } else {
                log.info("ℹ️  接口层: 两个请求开始时间差异较大，无法判断并发性");
            }
            
            // 总结验证结果
            if (timing1.firstChunkTime > 0 && timing2.firstChunkTime > 0) {
                long firstChunkTimeDiff = Math.abs(timing1.firstChunkTime - timing2.firstChunkTime);
                if (firstChunkTimeDiff >= 5000) {
                    log.warn("\n🔴 验证结果: 确认存在'接口并发但AI回复串行'的问题");
                    log.warn("   这与'后端信息'中描述的问题一致：");
                    log.warn("   - ChatModel采用单例模式");
                    log.warn("   - 底层SpringRestClient.execute()同步解析数据流");
                    log.warn("   - 导致即使不同的AI Service实例，只要共享同一个ChatModel，也会阻塞");
                } else {
                    log.info("\n🟢 验证结果: 未发现'接口并发但AI回复串行'的问题");
                    log.info("   AI响应是并发的，系统可以正常处理并发请求");
                }
            }
        }
    }

    /**
     * 准备测试用户
     * 注意：需要根据实际情况调整，可以从数据库查询或创建测试用户
     */
    private User prepareTestUser() {
        try {
            // 尝试获取第一个用户作为测试用户
            // 如果数据库为空，可能需要先创建测试数据
            List<User> users = userService.list();
            if (users != null && !users.isEmpty()) {
                return users.get(0);
            }
            log.warn("数据库中没有用户，无法进行测试");
            return null;
        } catch (Exception e) {
            log.error("获取测试用户失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 准备第一个测试应用
     * 注意：需要根据实际情况调整，可以从数据库查询或创建测试应用
     */
    private App prepareTestApp1(User user) {
        try {
            if (user == null) {
                return null;
            }
            // 尝试获取该用户的应用
            List<App> apps = appService.list();
            if (apps != null && !apps.isEmpty()) {
                // 查找属于该用户的应用
                for (App app : apps) {
                    if (app.getUserId().equals(user.getId())) {
                        return app;
                    }
                }
            }
            log.warn("用户 {} 没有应用，无法进行测试", user.getId());
            return null;
        } catch (Exception e) {
            log.error("获取测试应用1失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 准备第二个测试应用（必须与第一个不同）
     * 注意：需要根据实际情况调整，可以从数据库查询或创建测试应用
     */
    private App prepareTestApp2(User user) {
        try {
            if (user == null) {
                return null;
            }
            // 尝试获取该用户的应用
            List<App> apps = appService.list();
            if (apps != null && !apps.isEmpty()) {
                // 查找属于该用户的应用，找到第二个不同的应用
                App firstApp = null;
                for (App app : apps) {
                    if (app.getUserId().equals(user.getId())) {
                        if (firstApp == null) {
                            firstApp = app;
                        } else if (!app.getId().equals(firstApp.getId())) {
                            // 找到第二个不同的应用
                            return app;
                        }
                    }
                }
                // 如果只有一个应用，返回null，测试会跳过
                if (firstApp != null) {
                    log.warn("用户 {} 只有一个应用（appId={}），需要至少两个不同的应用才能进行并发测试", 
                        user.getId(), firstApp.getId());
                }
            }
            log.warn("用户 {} 没有足够的应用（需要至少2个），无法进行测试", user.getId());
            return null;
        } catch (Exception e) {
            log.error("获取测试应用2失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 请求时间记录类
     */
    private static class RequestTiming {
        String requestName;
        long startTime;           // 请求开始时间
        long firstChunkTime;       // 收到第一个AI响应数据块的时间（关键指标）
        long endTime;             // 请求结束时间
        long duration;            // 总耗时
        String error;

        RequestTiming(String requestName) {
            this.requestName = requestName;
            this.firstChunkTime = 0; // 初始化为0，表示还未收到
        }
    }
}

