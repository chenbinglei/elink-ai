package com.sunmax.common.constant;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Set;

/**
 * 主题常量路径
 */
@Data
public class TopicConstant {

    public static final String TOPIC_VERSION = "v1";

    public static final String GATEWAY_REQUEST_VERSION = "platform/request";

    public static final String GATEWAY_RESPONSE_VERSION = "from";

    /**
     * 主题前缀
     */
    public static final String TOPIC_PREFIX = "/v1/";

    /**
     * 网关查询主题前缀
     */
    public static final String GATEWAY_REQUEST_PREFIX = "platform/request";

    /**
     * 主题前缀
     */
    public static final String GATEWAY_RESPONSE_PREFIX = "from/";

    /**
     * 设备管理
     * 用于物联管理平台下发由设备执行的控制命令，如设备升级、应用控制等
     */
    public static final String DEVICE_COMMAND = "/device/command";

    /**
     * 设备管理
     * 用于边设备向物联管理平台发送请求，如主动请求更新配置
     */
    public static final String DEVICE_REQUEST = "/device/request";

    /**
     * 设备管理
     * 用于边设备上报设备状态、事件等
     */
    public static final String DEVICE_DATA = "/device/data";

    /**
     * 设备管理
     * 用于对请求的响应。包括control、request等的响应
     */
    public static final String DEVICE_RESPONSE = "/device/response";

    /**
     * 设备管理
     * 用于对设备的请求
     */
    public static final String TO_PO_REQUEST = "/topo/request";

    /**
     * 设备管理
     * 边设备添加子设备
     */
    public static final String TO_PO_COMMAND = "/topo/command";

    /**
     * 设备管理
     * 物联网平台返回的添加子设备的响应
     */
    public static final String TO_PO_RESPONSE = "/topo/response";

    /**
     * 容器管理
     * 用于物联管理平台下发由设备执行的容器控制命令，如容器安装、启动、停止等
     */
    public static final String CONTAINER_COMMAND = "/container/command";

    /**
     * 容器管理
     * 用于边设备上报设备状态、事件等
     */
    public static final String CONTAINER_DATA = "/container/data";

    /**
     * 容器管理
     * 用于对请求的响应。包括control、request等的响应
     */
    public static final String CONTAINER_RESPONSE = "/container/response";

    /**
     * 应用管理
     * 用于物联管理平台对应用管理app的控制
     */
    public static final String APP_COMMAND = "/app/command";

    /**
     * 应用管理
     * 用于应用管理app对物联管理平台响应
     */
    public static final String APP_RESPONSE = "/app/response";

    /**
     * 应用管理
     * 用于上报运行在边设备上的应用版本和运行状态至物联管理平台
     */
    public static final String APP_DATA = "/app/data";

    /**
     * 业务交互
     * 边设备上报业务数据
     */
    public static final String SERVICE_DATA = "/service/data";

    /**
     * 业务交互
     * 物联网平台给设备或边设备下发命令
     */
    public static final String SERVICE_COMMAND = "/service/command";

    /**
     * 业务交互
     * 边设备返回给物联网平台的命令响应
     */
    public static final String SERVICE_RESPONSE = "/service/response";

    /**
     * 网关平台请求主题
     */
    public static final String PLATFORM_REQUEST = "platform/request/#";

    /**
     * 网关平台响应主题
     */
    public static final String PLATFORM_RESPONSE = "from/+/response/#";

    /**
     * 网关状态查询主题
     */
    public static final String GATEWAY_STATUS_REQUEST = "/get/gatewayStatus/";

    /**
     * 网关状态查询响应主题
     */
    public static final String GATEWAY_STATUS_RESPONSE = "/response/get/gatewayStatus";

    /**
     * 服务列表查询主题
     */
    public static final String SERVICE_LIST_REQUEST = "/get/serviceList/";

    /**
     * 服务列表查询响应主题
     */
    public static final String SERVICE_LIST_RESPONSE = "/response/get/serviceList";

    /**
     * 同步时钟主题
     */
    public static final String SYNC_CLOCK_REQUEST = "/set/syncClock/";

    /**
     * 同步时钟响应主题
     */
    public static final String SYNC_CLOCK_RESPONSE = "/response/set/syncClock";

    /**
     * 网关重启主题
     */
    public static final String REBOOT_REQUEST = "/set/reboot/";

    /**
     * 网关重启响应主题
     */
    public static final String REBOOT_RESPONSE = "/response/set/reboot";

    /**
     * 许可状态查询主题
     */
    public static final String LICENSE_STATUS_REQUEST = "/get/licenseStatus/";

    /**
     * 许可状态查询响应主题
     */
    public static final String LICENSE_STATUS_RESPONSE = "/response/get/licenseStatus";

    /**
     * 获取设备key主题
     */
    public static final String LICENSE_KET_REQUEST = "/get/licenseKey/";

    /**
     * 获取设备key响应主题
     */
    public static final String LICENSE_KET_RESPONSE = "/response/get/licenseKey";

    /**
     * 下发许可主题
     */
    public static final String LICENSE_REQUEST = "/set/license/";

    /**
     * 下发许可响应主题
     */
    public static final String LICENSE_RESPONSE = "/response/set/license";


    public static Set<String> getAllTopics(String deviceCode){
        Set<String> resultList = Sets.newHashSet();
        resultList.add(TopicConstant.TOPIC_PREFIX + deviceCode + TopicConstant.DEVICE_COMMAND);
        resultList.add(TopicConstant.TOPIC_PREFIX + deviceCode + TopicConstant.DEVICE_REQUEST);
        resultList.add(TopicConstant.TOPIC_PREFIX + deviceCode + TopicConstant.DEVICE_DATA);
        //resultList.add(TopicConstant.TOPIC_PREFIX + terminalCode + TopicConstant.DEVICE_RESPONSE);
        resultList.add(TopicConstant.TOPIC_PREFIX + deviceCode + TopicConstant.TO_PO_COMMAND);
        resultList.add(TopicConstant.TOPIC_PREFIX + deviceCode + TopicConstant.TO_PO_RESPONSE);
        resultList.add(TopicConstant.TOPIC_PREFIX + deviceCode + TopicConstant.SERVICE_DATA);
        //resultList.add(TopicConstant.TOPIC_PREFIX + terminalCode + TopicConstant.SERVICE_COMMAND);
        resultList.add(TopicConstant.TOPIC_PREFIX + deviceCode + TopicConstant.SERVICE_RESPONSE);
//        resultList.add(TopicConstant.PLATFORM_REQUEST);
//        resultList.add(TopicConstant.PLATFORM_RESPONSE);
        return resultList;
    }

}
