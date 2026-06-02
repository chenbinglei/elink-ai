package com.sunmax.device.task;

import com.sunmax.device.dao.access.DeviceTaskDao;
import com.sunmax.device.dao.access.DeviceTaskRecordDao;
import com.sunmax.device.entity.access.DeviceTaskEntity;
import com.sunmax.device.entity.access.DeviceTaskRecordEntity;
import com.sunmax.device.websocket.DeviceUpdateWebSocket;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class DeviceUpdateTask {

    @Autowired
    private DeviceTaskDao deviceTaskDao;

    @Autowired
    private DeviceTaskRecordDao deviceTaskRecordDao;

    @Scheduled(fixedRate = 3000)
    public void sendDeviceUpdateData() {
        DeviceUpdateWebSocket.externalSendMessage();
    }

    /**
     * 每30秒推送电桩数据
     */
    @Scheduled(fixedRate = 30000)
    public void deviceUpdateTask() {
        //查询设备升级进行中的任务
        List<DeviceTaskEntity> deviceTaskList = deviceTaskDao.findAllByTaskStatusIn(Arrays.asList(1, 2));
        if (CollectionUtils.isNotEmpty(deviceTaskList)) {

            //根据多个任务id查询任务记录数据
            List<String> taskIds = deviceTaskList.stream().map(DeviceTaskEntity::getId).collect(Collectors.toList());
            List<DeviceTaskRecordEntity> deviceTaskRecordList = deviceTaskRecordDao.findAllByTaskIdIn(taskIds);
            //校验是否更新任务状态
            DeviceUpdateWebSocket.updateDeviceTaskStatus(deviceTaskList, deviceTaskRecordList, deviceTaskDao, deviceTaskRecordDao);

        }
    }

}
