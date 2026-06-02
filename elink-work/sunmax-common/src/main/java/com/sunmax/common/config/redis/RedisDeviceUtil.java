package com.sunmax.common.config.redis;

import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Slf4j
public class RedisDeviceUtil {

    public static DeviceModel getDevice(String deviceNumber) {
        try {
            String deviceKey = KeyUtil.DEVICE_KEY + deviceNumber;
            if (!RedisUtil.hasKey(deviceKey)) {
                return new DeviceModel();
            }
            return JsonUtil.objectToEntity(RedisUtil.get(deviceKey), DeviceModel.class);
        } catch (Exception e) {
            log.error("获取redis中设备实时数据失败", e);
            return null;
        }
    }

    public static List<DeviceModel> getDeviceList(List<String> deviceNumberList) {
        try {
            List<DeviceModel> resultList = Lists.newArrayList();
            deviceNumberList.forEach(deviceNumber -> {
                String deviceKey = KeyUtil.DEVICE_KEY + deviceNumber;
                if (!RedisUtil.hasKey(deviceKey)) {
                    resultList.add(new DeviceModel());
                } else {
                    resultList.add(JsonUtil.objectToEntity(RedisUtil.get(deviceKey), DeviceModel.class));
                }
            });
            return resultList;
        } catch (Exception e) {
            log.error("获取redis中设备实时数据失败", e);
            return null;
        }
    }

    /**
     * 设置设备的实时模型到Redis中。
     *
     * @param deviceNumber 设备序列号，用于标识特定的设备。
     * @param deviceModel 设备的实时模型，包含了设备的实时数据和状态。
     * 此方法首先尝试获取一个针对该设备代码的锁，以确保在更新Redis中的数据时的线程安全。
     * 如果成功获取锁，则将网关的实时模型存储到Redis中，使用的是设备代码作为键。
     * 最后，无论操作成功与否，都会释放之前获取的锁。
     */
    public static void setDevice(String deviceNumber, DeviceModel deviceModel) {
        // 构造锁的key，基于设备代码
        String lockKey = KeyUtil.LOCK_KEY + deviceNumber;
        try {
            // 尝试加锁，保证数据操作的原子性
            boolean lock = RedisLockUtil.lock(lockKey, KeyUtil.WAIT_TIME, KeyUtil.EXPIRE_TIME);
            if (lock) {
                // 构造设备数据的存储key
                String deviceKey = KeyUtil.DEVICE_KEY + deviceNumber;
                // 将网关的实时模型存储到Redis
                deviceModel.setDeviceNumber(deviceNumber);
                RedisUtil.set(deviceKey, deviceModel);
            }
        } catch (Exception e) {
            // 记录存取数据失败的错误日志
            log.error("存取redis中设备实时数据失败", e);
        } finally {
            // 无论是否成功，最后都要释放锁
            RedisLockUtil.unlock(lockKey);
        }
    }

    /**
     * 删除redis中设备的实时模型
     *
     * @param deviceNumber 设备序列号，用于标识特定的设备。
     */
    public static void delDevice(String deviceNumber) {
        //删除旧设备redisKey
        String lockKey = KeyUtil.LOCK_KEY + deviceNumber;
        try {
            boolean lock = RedisLockUtil.lock(lockKey, KeyUtil.WAIT_TIME, KeyUtil.EXPIRE_TIME);
            if (lock) {
                String deviceKey = KeyUtil.DEVICE_KEY + deviceNumber;
                RedisUtil.delete(deviceKey);
            }
        } catch (Exception e) {
            log.error("删除设备实时数据报错", e);
        } finally {
            RedisLockUtil.unlock(lockKey);
        }
    }

}
