package com.cj.wendaplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

//@MapperScan(value = "com.cj.wendaplatform.dao")
@SpringBootApplication
@EnableScheduling
public class WendaPlatformApplication {

    public static void main(String[] args) {

        SpringApplication.run(WendaPlatformApplication.class, args);
    }

    // 启动异步调用
    @EnableAsync
    @Configuration
    class TaskPoolConfig {

        /**
         * 自定义spring提供的线程池，若不自定义spring默认使用自己的SimpleAsyncTaskExecutor线程池
         * @return
         */
        @Bean("taskExecutor")
            public Executor taskExecutor() {
                ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
                // 设置核心线程数(最多同时能创建的线程数)
                executor.setCorePoolSize(10);
                // 设置最大线程数(任务队列满了之后才创建)
                executor.setMaxPoolSize(20);
                // 设置队列容量
                executor.setQueueCapacity(200);
                // 设置核心线程数以外的线程的最大空闲时间
                executor.setKeepAliveSeconds(60);
                // 设置线程池前缀
                executor.setThreadNamePrefix("taskExecutor-");
                // 设置拒绝策略(超过最大线程数后执行)
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
                // 设置线程池等待所有任务执行完才能关闭
                executor.setWaitForTasksToCompleteOnShutdown(true);
                // 设置线程关闭时任务等待的最大时间，达到时间后任务销毁保证线程池关闭
                executor.setAwaitTerminationSeconds(60);
                return executor;
            }
    }



}
