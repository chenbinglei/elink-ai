package com.sunmax.together.vo.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统曲线查询参数实体类")
public class SystemQueryVo {

    /**
     * 数据id
     */
    @Schema(description = "数据id")
    private String dataId;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "开始时间(yyyy-MM-dd HH:mm:ss)")
    private String startTime;

    /**
     * 结束时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    private String endTime;

    /**
     * 类型 1-光伏系统功率 2-光伏系统发电量 3-光伏逆变器功率 4-光伏逆变器发电量 5-光伏气象站辐照度 6-光伏气象站温度 7-光伏气象站辐照累积量 8-储能系统功率 9-储能系统发电量 10-储能PCS功率 11-储能PCS充放电量 12-储能电池簇SOC 13-储能电池簇总电压 14-储能辅助设备温度 15-储能辅助设备湿度 16-电桩系统功率 17-电桩系统充放电量 18-电桩功率 19-电桩充放电量 20-储能电池簇电芯电压 21-储能电池簇电芯温度 22-换电系统功率 23-换电系统充电量 24-电表功率因数 25-电表有功功率 26-电表无功功率 27-电表分时电量
     */
    @Schema(description = "类型 1-光伏系统功率 2-光伏系统发电量 3-光伏逆变器功率 4-光伏逆变器发电量 5-光伏气象站辐照度 6-光伏气象站温度 7-光伏气象站辐照累积量 8-储能系统功率 9-储能系统发电量 10-储能PCS功率 11-储能PCS充放电量 12-储能电池簇SOC 13-储能电池簇总电压 14-储能辅助设备温度 15-储能辅助设备湿度 16-电桩系统功率 17-电桩系统充放电量 18-电桩功率 19-电桩充放电量 20-储能电池簇电芯电压 21-储能电池簇电芯温度 22-换电系统功率 23-换电系统充电量 24-电表功率因数 25-电表有功功率 26-电表无功功率 27-电表分时电量")
    private Integer type;

    /**
     * 数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年
     */
    @Schema(description = "数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年")
    private String timeInterval;

    /**
     * 时间格式间隔 1-(yyyy-MM-dd HH:mm:ss) 2-(yyyy-MM-dd) 3-(yyyy-MM) 4-(yyyy) 5-(HH:mm:ss) 6-(HH:mm)
     */
    @Schema(description = "时间格式间隔 1-(yyyy-MM-dd HH:mm:ss) 2-(yyyy-MM-dd) 3-(yyyy-MM) 4-(yyyy) 5-(HH:mm:ss) 6-(HH:mm)")
    private Integer formatInterval;

    /**
     * 数据下标 例如0
     */
    @Schema(description = "数据下标 例如0")
    private Integer dataIndex;

}
