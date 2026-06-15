package com.sunmax.common.config.redis;

import com.sunmax.common.model.EventModel;
import com.sunmax.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Slf4j
public class RedisModelEventUtil {

    public static List<EventModel> getModelEvent(String modelEventId) {
        try {
            String modelEventKey = KeyUtil.MODEL_EVENT_KEY + modelEventId;
            if (!RedisUtil.hasKey(modelEventKey)) {
                return Lists.newArrayList();
            }
            return JsonUtil.objectToList(RedisUtil.get(modelEventKey), EventModel.class);
        } catch (RuntimeException e) {
            log.error("获取redis中模型事件数据失败", e);
            return Lists.newArrayList();
        }
    }

    /**
     * 设置设备的实时模型到Redis中。
     *
     * @param modelEventId 模型设备实时数据，用于标识特定的设备。
     * @param eventList 模型事件的实时模型，包含了模型的实时数据和状态。
     * 此方法首先尝试获取一个针对该设备代码的锁，以确保在更新Redis中的数据时的线程安全。
     * 如果成功获取锁，则将网关的实时模型存储到Redis中，使用的是设备代码作为键。
     * 最后，无论操作成功与否，都会释放之前获取的锁。
     */
    public static void setModelEvent(String modelEventId, List<EventModel> eventList) {
        // 构造锁的key，基于设备代码
        String lockKey = KeyUtil.LOCK_KEY + modelEventId;
        try {
            // 尝试加锁，保证数据操作的原子性
            boolean lock = RedisLockUtil.lock(lockKey, KeyUtil.WAIT_TIME, KeyUtil.EXPIRE_TIME);
            if (lock) {
                // 构造设备数据的存储key
                String modelEventKey = KeyUtil.MODEL_EVENT_KEY + modelEventId;
                // 将模型的实时模型存储到Redis
                RedisUtil.set(modelEventKey, eventList);
            }
        } catch (RuntimeException e) {
            // 记录存取数据失败的错误日志
            log.error("存取redis中模型事件实时数据失败", e);
        } finally {
            // 无论是否成功，最后都要释放锁
            RedisLockUtil.unlock(lockKey);
        }
    }

    /**
     * 删除redis中模型事件的实时模型
     *
     * @param modelEventId 模型事件id，用于标识特定的设备。
     */
    public static void delModelEvent(String modelEventId) {
        //删除旧设备redisKey
        String lockKey = KeyUtil.LOCK_KEY + modelEventId;
        try {
            boolean lock = RedisLockUtil.lock(lockKey, KeyUtil.WAIT_TIME, KeyUtil.EXPIRE_TIME);
            if (lock) {
                String deviceKey = KeyUtil.MODEL_EVENT_KEY + modelEventId;
                RedisUtil.delete(deviceKey);
            }
        } catch (RuntimeException e) {
            log.error("删除模型事件数据报错", e);
        } finally {
            RedisLockUtil.unlock(lockKey);
        }
    }

}
