package com.sunmax.crontab.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


@Component
@Slf4j
public class ScheduleTaskUtil {

    public static final String NODE_TASK = "nodeTask";

    public static final String NODE_TABLE = "nodetable";

    public static final String STABLE_NAME = "smnodetable";

    public static final String STRATEGY_TASK = "strategyTask";

    private final Map<String, ScheduledFuture<?>> futuresMap = new ConcurrentHashMap<>();

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 创建ThreadPoolTaskScheduler线程池
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1000);
        threadPoolTaskScheduler.setThreadNamePrefix("taskExecutor-");   // 线程名称
//        threadPoolTaskScheduler.setAwaitTerminationSeconds(60);         // 等待时长
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);  // 调度器shutdown被调用时等待当前被调度的任务完成
        return threadPoolTaskScheduler;
    }

    /**
     * 添加定时任务，如果任务名重复则抛出异常
     * @param task 任务
     * @param period 定时任务周期毫秒
     * @param key 任务名
     * @return
     */
    public boolean addTask(Runnable task, long period, String key) {
        ScheduledFuture<?> future = threadPoolTaskScheduler.scheduleAtFixedRate(task, period);
        ScheduledFuture<?> oldScheduledFuture = futuresMap.put(key, future);
        if (oldScheduledFuture == null) {
            return true;
        } else {
            throw new RuntimeException("添加任务key名： " + key + "重复" );
        }
    }

    /**
     * 添加定时任务，如果任务名重复则抛出异常
     * @param task 任务
     * @param expression 定时任务表达式
     * @param key 任务名
     * @return
     */
    public boolean addTask(Runnable task, String expression, String key) {
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(task, new CronTrigger(expression));
        ScheduledFuture<?> oldScheduledFuture = futuresMap.put(key, future);
        if (oldScheduledFuture == null) {
            return true;
        } else {
            log.error("添加任务key名： " + key + "重复" );
            return false;
        }
    }

    /**
     * 移除定时任务
     * @param key 任务名
     * @return
     */
    public boolean removeTask(String key) {
        ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(key);
        if (toBeRemovedFuture != null) {
            toBeRemovedFuture.cancel(true);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 更新定时任务
     * 有可能会出现：1、旧的任务不存在，此时直接添加新任务；
     * 2、旧的任务存在，先删除旧的任务，再添加新的任务
     * @param task 任务
     * @param period 定时器周期 毫秒
     * @param key 任务名称
     * @return
     */
    public boolean updateTask(Runnable task, long period, String key) {
        ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(key);
        // 存在则删除旧的任务
        if (toBeRemovedFuture != null) {
            toBeRemovedFuture.cancel(true);
        }
        return addTask(task, period, key);
    }

    /**
     * 更新定时任务
     * 有可能会出现：1、旧的任务不存在，此时直接添加新任务；
     * 2、旧的任务存在，先删除旧的任务，再添加新的任务
     * @param task 任务
     * @param expression 定时器周期 毫秒
     * @param key 任务名称
     * @return
     */
    public boolean updateTask(Runnable task, String expression, String key) {
        ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(key);
        // 存在则删除旧的任务
        if (toBeRemovedFuture != null) {
            toBeRemovedFuture.cancel(true);
        }
        return addTask(task, expression, key);
    }

    public void testMain() {
        int activeCount = threadPoolTaskScheduler.getActiveCount();
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread thread : threadSet) {
            if (thread.getName().startsWith("taskExecutor-")) {
                System.out.println(thread.getName() + thread.getState());
                for (StackTraceElement s : thread.getStackTrace()) {
                    System.out.println(s);
                }
            }
        }
        System.out.println(activeCount);
    }
}
