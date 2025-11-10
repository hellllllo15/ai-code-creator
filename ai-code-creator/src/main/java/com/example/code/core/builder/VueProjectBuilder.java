package com.example.code.core.builder;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class VueProjectBuilder {

















    /**
     * 执行命令
     *
     * @param workingDir     工作目录
     * @param command        命令字符串
     * @param timeoutSeconds 超时时间（秒）
     * @return 是否执行成功
     */
    private boolean executeCommand(File workingDir, String command, int timeoutSeconds) {
        try {
            log.info("在目录 {} 中执行命令: {}", workingDir.getAbsolutePath(), command);
            Process process = RuntimeUtil.exec(
                    null,
                    workingDir,
                    command.split("\\s+") // 命令分割为数组
            );
            // 等待进程完成，设置超时
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                log.error("命令执行超时（{}秒），强制终止进程", timeoutSeconds);
                process.destroyForcibly();
                return false;
            }
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                log.info("命令执行成功: {}", command);
                return true;
            } else {
                log.error("命令执行失败，退出码: {}", exitCode);
                return false;
            }
        } catch (Exception e) {
            log.error("执行命令失败: {}, 错误信息: {}", command, e.getMessage());
            return false;
        }
    }





/// 检查是否是windows电脑
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }


    private String buildCommand(String baseCommand) {
        if (isWindows()) {
            return baseCommand + ".cmd";
        }
        return baseCommand;
    }
    /**
     * 执行 npm install 命令
     */
    private boolean executeNpmInstall(File projectDir) {
        log.info("执行 npm install...");
        String command = String.format("%s install", buildCommand("npm"));
        return executeCommand(projectDir, command, 300); // 5分钟超时
    }

    /**
     * 执行 npm run build 命令
     */
    private boolean executeNpmBuild(File projectDir) {
        log.info("执行 npm run build...");
        String command = String.format("%s run build", buildCommand("npm"));
        return executeCommand(projectDir, command, 180); // 3分钟超时
    }



    /**
     * 构建 Vue 项目
     *
     * @param projectPath 项目根目录路径
     * @return 是否构建成功
     */
    public boolean buildProject(String projectPath) {
        File projectDir = new File(projectPath);
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            log.error("项目目录不存在: {}", projectPath);
            return false;
        }
        // 检查 package.json 是否存在
        File packageJson = new File(projectDir, "package.json");
        if (!packageJson.exists()) {
            log.error("package.json 文件不存在: {}", packageJson.getAbsolutePath());
            return false;
        }
        log.info("开始构建 Vue 项目: {}", projectPath);
        // 执行 npm install
        if (!executeNpmInstall(projectDir)) {
            log.error("npm install 执行失败");
            return false;
        }
        // 执行 npm run build
        if (!executeNpmBuild(projectDir)) {
            log.error("npm run build 执行失败");
            return false;
        }
        // 验证 dist 目录是否生成
        File distDir = new File(projectDir, "dist");
        if (!distDir.exists()) {
            log.error("构建完成但 dist 目录未生成: {}", distDir.getAbsolutePath());
            return false;
        }
        log.info("Vue 项目构建成功，dist 目录: {}", distDir.getAbsolutePath());
        return true;
    }

    /**
     * 异步构建项目（不阻塞主流程）
     *
     * @param projectPath 项目路径
     */
    public void buildProjectAsync(String projectPath) {
        // 在单独的线程中执行构建，避免阻塞主流程
        Thread.ofVirtual().name("vue-builder-" + System.currentTimeMillis()).start(() -> {
            try {
                buildProject(projectPath);
            } catch (Exception e) {
                log.error("异步构建 Vue 项目时发生异常: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * 构建 Vue 项目并推送进度（返回Flux用于SSE推送）
     *
     * @param projectPath 项目根目录路径
     * @return 构建进度流
     */
    public Flux<String> buildProjectWithProgress(String projectPath) {
        return Flux.<String>create(sink -> {
            try {
                File projectDir = new File(projectPath);
                if (!projectDir.exists() || !projectDir.isDirectory()) {
                    sink.error(new RuntimeException("项目目录不存在: " + projectPath));
                    return;
                }
                // 检查 package.json 是否存在
                File packageJson = new File(projectDir, "package.json");
                if (!packageJson.exists()) {
                    sink.error(new RuntimeException("package.json 文件不存在: " + packageJson.getAbsolutePath()));
                    return;
                }
                
                sink.next("\n\n🔨 开始构建 Vue 项目...\n\n");
                
                // 执行 npm install
                sink.next("📦 正在安装依赖 (npm install)...\n");
                boolean installSuccess = executeNpmInstall(projectDir);
                if (!installSuccess) {
                    sink.error(new RuntimeException("npm install 执行失败"));
                    return;
                }
                sink.next("✅ 依赖安装完成\n");
                
                // 执行 npm run build
                sink.next("🏗️ 正在构建项目 (npm run build)...\n");
                boolean buildSuccess = executeNpmBuild(projectDir);
                if (!buildSuccess) {
                    sink.error(new RuntimeException("npm run build 执行失败"));
                    return;
                }
                
                // 验证 dist 目录是否生成
                File distDir = new File(projectDir, "dist");
                if (!distDir.exists()) {
                    sink.error(new RuntimeException("构建完成但 dist 目录未生成"));
                    return;
                }
                
                sink.next("✅ 构建完成！dist 目录已生成\n\n");
                sink.complete();
            } catch (Exception e) {
                log.error("构建 Vue 项目时发生异常: {}", e.getMessage(), e);
                sink.error(e);
            }
        })
        .subscribeOn(Schedulers.boundedElastic()); // 在后台线程执行，避免阻塞
    }

}
