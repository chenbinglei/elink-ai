package com.sunmax.crontab.ruuner;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.dto.system.DataForwardDto;
import com.sunmax.common.dto.system.dynamic.MqttForwardDto;
import com.sunmax.common.vo.system.MqttClientVo;
import com.sunmax.crontab.mapper.tdengine.ComputeNodeMapper;
import com.sunmax.crontab.service.ComputeNodeService;
import com.sunmax.crontab.service.NodeTaskService;
import com.sunmax.crontab.service.SystemFeignService;
import com.sunmax.crontab.service.TogetherFeignService;
import com.sunmax.crontab.service.feign.SystemService;
import com.sunmax.crontab.service.feign.TogetherService;
import com.sunmax.crontab.vo.ComputeNodeTaskVo;
import com.sunmax.crontab.websocket.ConfigFuncPointWebSocket;
import com.sunmax.crontab.websocket.ConfigFuncVarWebSocket;
import com.sunmax.crontab.websocket.VarRealDataWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sunmax.crontab.util.ScheduleTaskUtil.NODE_TABLE;
import static com.sunmax.crontab.util.ScheduleTaskUtil.STABLE_NAME;

@Configuration
@Slf4j
public class TaskSchedulerRunner implements ApplicationRunner {

    @Autowired
    private ComputeNodeService computeNodeService;

    @Autowired
    private NodeTaskService nodeTaskService;

    @Autowired
    private ComputeNodeMapper computeNodeMapper;

    @Autowired
    private SystemService systemService;

    @Autowired
    private SystemFeignService systemFeignService;

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private TogetherFeignService togetherFeignService;

    @Override
    public void run(ApplicationArguments args) {
        //把所有节点加入定时任务
        addAllComputeNodeTask();
        //创建数据转发mqtt客户端
        createMqttClient();

        //创建平台自动策略任务
        togetherFeignService.batchUpdateStrategyTask(togetherService.findStrategyHandTaskList(null).getData());
    }

    /**
     * 每30秒推送实时数据
     */
    @Scheduled(cron = "0/30 * * * * ? ")
    public void sendData() {
        ConfigFuncPointWebSocket.sendAllMessage();
        ConfigFuncVarWebSocket.sendAllMessage();
        VarRealDataWebSocket.sendAllMessage();
    }

    private void addAllComputeNodeTask() {
        //重新创建计算节点定时任务
        List<ComputeNodeTaskVo> computeNodeTaskVoList = computeNodeService.findAllComputeNodeList();
        if (CollectionUtils.isNotEmpty(computeNodeTaskVoList)) {
            //查询超级表在不在
            Map<String, Object> stableIfExists = computeNodeMapper.findSTableIfExists(STABLE_NAME);
            if (stableIfExists == null || stableIfExists.isEmpty()) {
                computeNodeMapper.createSuperTable(STABLE_NAME);
            }
            //循环查询每个节点taos表存不存在，不存在则创建
            computeNodeTaskVoList.forEach(computeNodeTaskVo -> {
                Map<String, Object> tableIfExists = computeNodeMapper.findTableIfExists(NODE_TABLE + computeNodeTaskVo.getStorageId());
                if (tableIfExists == null || tableIfExists.isEmpty()) {
                    computeNodeMapper.createTaosTable(NODE_TABLE + computeNodeTaskVo.getStorageId(), STABLE_NAME, computeNodeTaskVo.getSiteId(), computeNodeTaskVo.getStorageId());
                }
            });
//            computeNodeTaskVoList = computeNodeTaskVoList.stream().filter(c -> "2c99698b93e35e4b0193e3c2678a731e".equals(c.getId())).collect(Collectors.toList());
            //把计算节点全部加入任务中
            nodeTaskService.addAllComputeNodeTask(computeNodeTaskVoList);
        }
    }

    private void createMqttClient() {
        //获取所有站点转发数据
        List<DataForwardDto> dataForwardList = systemService.getDataForwardList(1, 1).getData();
        if (CollectionUtils.isNotEmpty(dataForwardList)) {
            try {
                //2.创建线程池
                final ExecutorService executors = Executors.newFixedThreadPool(dataForwardList.size());
                //3.执行充电桩命令，提交到任务里面
                dataForwardList.forEach(dataForward -> {
                    MqttForwardDto mqttForward = JSON.parseObject(dataForward.getDynamicFields(), MqttForwardDto.class);
                    executors.submit(() -> systemFeignService.createMqttClient(MqttClientVo.builder()
                            .protocolCode(dataForward.getProtocolCode())
                            .clientId(mqttForward.getClientId())
                            .vendor(mqttForward.getVendor())
                            .gwSn(mqttForward.getGwSn())
                            .address(dataForward.getAddress())
                            .username(mqttForward.getUsername())
                            .password(mqttForward.getPassword()).build())

                    );
                });
                // 关闭线程池
                executors.shutdown();
                do {
                    Thread.sleep(500);
                } while (!executors.isTerminated());
            } catch (Exception e) {
                log.error("执行数据转发mqtt任务报错", e);
            }
        }
    }

}
