package com.sunmax.common.config.redis;

import com.google.common.collect.Lists;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RedisGeneralUtil {

    /**
     * 获取网关实时数据
     * 该方法用于根据网关设备编号，从Redis中获取对应的网关实时数据。如果数据不存在，则返回一个空的网关实时数据模型。
     *
     * @param terminalCode 网关设备编号，用于标识特定的网关设备。
     * @return GatewayRealModel 网关实时数据模型，包含了网关的实时信息。如果获取失败，返回null。
     */
    public static GatewayRealModel getGatewayRealModel(String terminalCode) {
        try {
            // 构造设备的键值，前缀为GENERAL_GW_PREFIX，后接设备编号
            String deviceKey = KeyUtil.GENERAL_GW_PREFIX + terminalCode;

            // 检查Redis中是否存在该设备键值
            if (!RedisUtil.hasKey(deviceKey)) {
                // 如果不存在，返回一个新的空的网关实时数据模型
                return new GatewayRealModel();
            }

            // 如果存在，从Redis中获取数据，并转换为网关实时数据模型
            return JsonUtil.objectToEntity(RedisUtil.get(deviceKey), GatewayRealModel.class);
        } catch (RuntimeException e) {
            // 记录获取Redis中通用网关实时数据失败的错误日志
            log.error("获取redis中通用网关实时数据失败", e);
            return null;
        }
    }


    /**
     * 设置通用网关的实时模型到Redis中。
     *
     * @param terminalCode     设备代码，用于标识特定的设备。
     * @param gatewayRealModel 网关的实时模型，包含了设备的实时数据和状态。
     *                         此方法首先尝试获取一个针对该设备代码的锁，以确保在更新Redis中的数据时的线程安全。
     *                         如果成功获取锁，则将网关的实时模型存储到Redis中，使用的是设备代码作为键。
     *                         最后，无论操作成功与否，都会释放之前获取的锁。
     */
    public static void setGatewayRealModel(String terminalCode, GatewayRealModel gatewayRealModel) {
        try {
            // 构造设备数据的存储key
            String deviceKey = KeyUtil.GENERAL_GW_PREFIX + terminalCode;
            // 将网关的实时模型存储到Redis
            gatewayRealModel.setTerminalCode(terminalCode);
            RedisUtil.set(deviceKey, gatewayRealModel);
        } catch (RuntimeException e) {
            // 记录存取数据失败的错误日志
            log.error("存取redis中通用网关实时数据失败", e);
        }
    }


    /**
     * 删除指定设备的实时模型数据
     *
     * @param terminalCode 设备代码，用于标识需要删除数据的设备
     *                     该方法首先尝试获取针对该设备数据操作的锁，确保操作的原子性。
     *                     获取锁成功后，会从Redis中删除与该设备相关联的实时数据。
     *                     如果在操作过程中出现异常，会记录错误日志，并释放锁。
     */
    public static void delGatewayRealModel(String terminalCode) {
        // 构造锁的key
        String lockKey = KeyUtil.GENERAL_LOCK_KEY + terminalCode;
        try {
            // 尝试加锁，保证数据操作的原子性
            boolean lock = RedisLockUtil.lock(lockKey, KeyUtil.WAIT_TIME, KeyUtil.EXPIRE_TIME);
            if (lock) {
                // 构造设备数据的key
                String deviceKey = KeyUtil.GENERAL_GW_PREFIX + terminalCode;
                // 从Redis中删除设备的实时数据
                RedisUtil.delete(deviceKey);
            }
        } catch (RuntimeException e) {
            // 记录删除操作失败的日志
            log.error("删除redis中通用网关实时数据失败", e);
        } finally {
            // 无论如何都释放锁，确保锁的正确释放，避免死锁
            RedisLockUtil.unlock(lockKey);
        }
    }

    /**
     * 获取电桩实时数据
     * 该方法通过设备编号从Redis中获取电桩的实时数据。如果数据不存在，则返回一个空的电桩实时数据模型。
     *
     * @param deviceCode 电桩设备编号，用于标识特定的电桩设备
     * @return 电桩通用实时数据模型，包含电桩的实时状态和数据
     */
    public static PileRealModel getPileRealModel(String deviceCode) {
        try {
            // 生成电桩在Redis中的唯一标识key
            String deviceKey = KeyUtil.GENERAL_PILE_PREFIX + deviceCode;
            // 检查Redis中是否存在该电桩的实时数据
            if (!RedisUtil.hasKey(deviceKey)) {
                // 如果不存在，返回一个空的电桩实时数据模型
                return new PileRealModel();
            }
            // 从Redis获取电桩实时数据，并转换为电桩实时数据模型
            return JsonUtil.objectToEntity(RedisUtil.get(deviceKey), PileRealModel.class);
        } catch (RuntimeException e) {
            // 记录获取电桩实时数据失败的错误日志
            log.error("获取redis中通用电桩实时数据失败", e);
            // 出现异常时返回null
            return null;
        }
    }


    /**
     * 设置特定设备的实时模型到Redis中。
     *
     * @param deviceCode    设备编码，用于标识特定的设备。
     * @param pileRealModel 设备的实时模型，包含了设备当前的实时状态信息。
     *                      此方法首先尝试获取一个针对该设备的锁，以确保在更新Redis中的数据时的线程安全。
     *                      如果成功获取锁，则将设备的实时模型存储到Redis中，存储的键由设备编码拼接通用电桩前缀构成。
     *                      在整个过程中，如果出现异常，会记录日志但不抛出，最后始终会释放锁。
     */
    public static void setPileRealModel(String deviceCode, PileRealModel pileRealModel) {
        try {
            // 构造设备数据存储的key
            String deviceKey = KeyUtil.GENERAL_PILE_PREFIX + deviceCode;
            // 将设备的实时模型存储到Redis
            pileRealModel.setPileCode(deviceCode);
            RedisUtil.set(deviceKey, pileRealModel);
        } catch (RuntimeException e) {
            // 记录存取数据失败的错误日志
            log.error("存取redis中通用电桩实时数据失败", e);
        }
    }

    /**
     * 删除指定电桩设备的实时数据。
     * 本方法首先尝试获取针对该电桩设备的锁，以确保在删除操作期间的并发安全。
     * 获取锁成功后，会从Redis中删除与该电桩设备相关联的实时数据。
     *
     * @param deviceCode 电桩设备的编号，用于标识需要删除实时数据的特定电桩。
     */
    public static void delPileRealModel(String deviceCode) {
        // 构造电桩实时数据的锁Key
        String lockKey = KeyUtil.GENERAL_LOCK_KEY + deviceCode;
        boolean lock = false;
        try {
            // 尝试加锁，保证删除操作的原子性
            lock = RedisLockUtil.lock(lockKey, KeyUtil.WAIT_TIME, KeyUtil.EXPIRE_TIME);
            if (lock) {
                // 构造电桩实时数据的存储Key
                String deviceKey = KeyUtil.GENERAL_PILE_PREFIX + deviceCode;
                // 从Redis中删除电桩实时数据
                RedisUtil.delete(deviceKey);
                log.info("成功从Redis中删除电桩实时数据: {}", deviceCode);
            } else {
                log.warn("尝试获取锁失败，无法删除电桩实时数据: {}", deviceCode);
            }
        } catch (RuntimeException e) {
            // 记录删除操作失败的日志，增加更详细的错误信息
            log.error("删除redis中通用电桩实时数据失败: {}", deviceCode, e);
        } finally {
            if (lock) {
                try {
                    // 无论成功或失败，最后都释放锁
                    RedisLockUtil.unlock(lockKey);
                    log.info("成功释放锁: {}", lockKey);
                } catch (RuntimeException e) {
                    log.error("释放锁失败: {}", lockKey, e);
                }
            }
        }
    }


    /**
     * 根据多个电桩编码获取电桩实时数据
     * 该方法通过设备编号从Redis中获取电桩的实时数据。如果数据不存在，则返回一个空的电桩实时数据模型。
     *
     * @param deviceCodes 多个电桩设备编号，用于标识特定的电桩设备
     * @return 电桩通用实时数据模型，包含电桩的实时状态和数据
     */
    public static List<PileRealModel> getPileRealModelList(List<String> deviceCodes) {
        try {
            List<PileRealModel> pileRealModelList = Lists.newArrayList();
            deviceCodes.forEach(deviceCode -> {
                // 生成电桩在Redis中的唯一标识key
                String deviceKey = KeyUtil.GENERAL_PILE_PREFIX + deviceCode;
                // 检查Redis中是否存在该电桩的实时数据
                if (!RedisUtil.hasKey(deviceKey)) {
                    // 如果不存在，返回一个空的电桩实时数据模型
                    pileRealModelList.add(new PileRealModel());
                } else {
                    pileRealModelList.add(JsonUtil.objectToEntity(RedisUtil.get(deviceKey), PileRealModel.class));
                }
            });
            return pileRealModelList;
        } catch (RuntimeException e) {
            // 记录获取电桩实时数据失败的错误日志
            log.error("获取redis中通用电桩实时数据失败", e);
            // 出现异常时返回null
            return null;
        }
    }

    /**
     * 执行与特定终端相关的任务，通过Redis分布式锁确保任务的线程安全
     * 此方法主要用于防止并发环境下对同一终端的数据操作冲突，通过加锁机制确保数据的一致性
     *
     * @param terminalCode 终端代码，用于区分不同的终端
     * @param task         要执行的任务，通常涉及对特定终端数据的操作
     */
    public static void executeGateway(String terminalCode, Runnable task) {
        // 构造锁的key，基于设备代码
        String lockKey = KeyUtil.GENERAL_LOCK_KEY + terminalCode;
        try {
            // 尝试加锁，保证数据操作的原子性
            boolean lock = RedisLockUtil.lock(lockKey, 6, 10);
            if (lock) {
                // 获取锁成功后执行任务
                task.run();
            } else {
                log.error("获取redis中通用网关实时数据锁失败：{}", lockKey);
            }
        } catch (RuntimeException e) {
            // 记录存取数据失败的错误日志
            log.error("redis中通用网关实时数据加锁异常", e);
        } finally {
            // 无论是否成功，最后都要释放锁
            RedisLockUtil.unlock(lockKey);
        }
    }


    /**
     * 使用Redis分布式锁执行给定的任务
     * 此方法旨在确保在分布式环境中安全地执行任务，避免并发执行导致的数据不一致问题
     *
     * @param pileCode 电桩代码，用于区分不同的终端
     * @param task     要执行的任务，以Runnable接口的形式传入
     */
    public static void executePile(String pileCode, Runnable task) {
        // 构造锁的key，基于设备代码
        String lockKey = KeyUtil.GENERAL_LOCK_KEY + pileCode;
        try {
            // 尝试获取Redis中的分布式锁
            boolean lock = RedisLockUtil.lock(lockKey, KeyUtil.WAIT_TIME, KeyUtil.EXPIRE_TIME);
            // 如果成功获取锁，则执行任务
            if (lock) {
                task.run();
            } else {
                log.error("redis中通用电桩实时数据获取锁失败：{}", lockKey);
            }
        } catch (RuntimeException e) {
            // 日志记录加锁失败的情况
            log.error("redis中通用电桩实时数据加锁异常", e);
        } finally {
            // 释放锁，确保即使在加锁失败或执行任务时抛出异常，锁也能被释放
            RedisLockUtil.unlock(lockKey);
        }
    }

}
