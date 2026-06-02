package com.sunmax.configure.util;

import com.sunmax.common.util.StringUtil;

/**
 * @Author: yqz
 * @Date: 2023/10/811:45
 * @version: 1.0
 * @注释:
 */
public class DataAccessStringUtil {

    /**
     * 转化省级站点状态
     *
     * @param stationStatus
     * @return
     */
    public static Integer getProvinceStationStatus(Integer stationStatus) {
        int status = 0;
        if (StringUtil.isNotEmpty(stationStatus)) {
            if (stationStatus == 1) {
                status = 50;
            } else if (stationStatus == 2) {
                status = 6;
            } else if (stationStatus == 3) {
                status = 1;
            } else if (stationStatus == 4) {
                status = 5;
            }
        }
        return status;
    }

    /**
     * 转化省级电桩状态
     *
     * @param assetStatus
     * @return
     */
    public static Integer getProvincePileStatus(Integer assetStatus) {
        Integer status = null;
        if (StringUtil.isNotEmpty(assetStatus)) {
            if (assetStatus == 1) {
                status = 50;
            } else if (assetStatus == 2) {
                status = 6;
            } else if (assetStatus == 3) {
                status = 5;
            }
        }
        return status;
    }

    /**
     * 转化省级电枪状态
     *
     * @param pileWorkState
     * @param gunWorkState
     * @param vehicleConnectState
     * @return
     */
    public static Integer getProvinceGunStatus(Integer pileWorkState, Integer gunWorkState, Integer vehicleConnectState) {
        Integer status = null;
        if (StringUtil.isNotEmpty(pileWorkState) && pileWorkState == 88) {
            status = 0;
        } else if (StringUtil.isNotEmpty(pileWorkState) && pileWorkState == 3) {
            status = 255;
        } else if (gunWorkState == 0) {
            if (StringUtil.isNotEmpty(vehicleConnectState) && vehicleConnectState == 2) {
                status = 2;
            } else {
                status = 1;
            }
        } else if (gunWorkState == 1 || gunWorkState == 3) {
            status = 2;
        } else if (gunWorkState == 2) {
            status = 3;
        } else if (gunWorkState == 7) {
            status = 4;
        } else if (gunWorkState == 255) {
            status = 255;
        }
        return status;
    }

    /**
     * 转化省市级电枪车辆连接状态
     *
     * @param vehicleConnectState
     * @return
     */
    public static Integer getVehicleStatus(Integer vehicleConnectState) {
        int status = 0;
        if (StringUtil.isNotEmpty(vehicleConnectState)) {
            if (vehicleConnectState == 0 || vehicleConnectState == 1) {
                status = 10; //空闲
            } else if (vehicleConnectState == 2) {
                status = 50; //占用
            }
        }
        return status;
    }

    /**
     * 转化市级站点状态
     *
     * @param stationStatus
     * @return
     */
    public static Integer getCityStationStatus(Integer stationStatus) {
        Integer status = null;
        if (StringUtil.isNotEmpty(stationStatus)) {
            if (stationStatus == 1) {
                status = 50;
            } else if (stationStatus == 2) {
                status = 6;
            } else if (stationStatus == 3) {
                status = 1;
            } else if (stationStatus == 4) {
                status = 5;
            }
        }
        return status;
    }

    /**
     * 转化市级电枪状态
     *
     * @param pileWorkState
     * @param gunWorkState
     * @param vehicleConnectState
     * @return
     */
    public static Integer getCityGunStatus(Integer pileWorkState, Integer gunWorkState, Integer vehicleConnectState) {
        Integer status = null;
        if (StringUtil.isNotEmpty(pileWorkState) && pileWorkState == 88) {
            status = 0;
        } else if (StringUtil.isNotEmpty(pileWorkState) && pileWorkState == 3) {
            status = 255;
        } else if (gunWorkState == 0) {
            if (StringUtil.isNotEmpty(vehicleConnectState) && vehicleConnectState == 2) {
                status = 2;
            } else {
                status = 1;
            }
        } else if (gunWorkState == 1 || gunWorkState == 3) {
            status = 2;
        } else if (gunWorkState == 2) {
            status = 3;
        } else if (gunWorkState == 7) {
            status = 4;
        } else if (gunWorkState == 255) {
            status = 255;
        }
        return status;
    }

    /**
     * 转化市级订单状态
     *
     * @param orderLogo
     * @return
     */
    public static Integer getCityOrderStatus(Integer orderLogo) {
        int status;
        if (StringUtil.isNotEmpty(orderLogo)) {
            if (orderLogo == 1) {
                status = 2;
            } else if (orderLogo == 3 || orderLogo == 9) {
                status = 4;
            } else {
                status = 5;
            }
        } else {
            status = 5;
        }
        return status;
    }

    /**
     * 转化省市级云快充电枪状态
     *
     * @param gunWorkState
     * @param vehicleConnectState
     * @return
     */
    public static Integer getYKCGunStatus(Integer gunWorkState, Integer vehicleConnectState) {
        Integer status = null;
        if (gunWorkState == 0) { //离线
            status = 0;
        } else if (gunWorkState == 1) { //故障
            status = 255;
        } else if (gunWorkState == 2 && vehicleConnectState == 1) {
            status = 2;
        } else if (gunWorkState == 2) { //空闲
            status = 1;
        } else if (gunWorkState == 3) { //充电
            status = 3;
        }
        return status;
    }

    /**
     * 转化省市级电枪云快充车辆连接状态
     *
     * @param vehicleConnectState
     * @return
     */
    public static Integer getYKCVehicleStatus(Integer vehicleConnectState) {
        int status = 0;
        if (vehicleConnectState == 0) {
            status = 10; //空闲
        } else if (vehicleConnectState == 1) {
            status = 50; //占用
        }
        return status;
    }

    /**
     * 转化市级晟曼V2G电桩停止原因
     *
     * @param troubleCode 故障码
     * @return
     */
    public static Integer getSMV2GStopReason(Long featureCode, Integer troubleCode) {
        int status;
        //故障码
//        if (featureCode == 43605) {
            switch (troubleCode) {
                case 4098:
                case 4099:
                case 4100:
                case 4101:
                case 4113:
                    status = 0;
                    break;
                case 1:
                case 2:
                case 3:
                case 4096:
                case 4097:
                case 4102:
                case 4103:
                case 4104:
                case 4105:
                case 4106:
                case 4107:
                case 4108:
                case 4109:
                case 4110:
                case 4111:
                case 8192:
                case 8193:
                case 16384:
                case 16385:
                case 16386:
                case 4112:
                case 8194:
                    status = 1;
                    break;
                case 256:
                case 257:
                case 258:
                case 259:
                case 260:
                case 261:
                case 262:
                case 20736:
                    status = 3;
                    break;
                case 4608:
                case 4609:
                case 8704:
                case 8705:
                case 8706:
                case 8707:
                case 8708:
                case 8709:
                case 8710:
                case 8711:
                case 8712:
                case 8713:
                case 8714:
                case 8715:
                case 8716:
                case 8717:
                case 12800:
                case 16896:
                case 16897:
                case 16898:
                case 16899:
                case 16900:
                case 16901:
                    status = 4;
                    break;
                default:
                    status = 2;
                    break;
            }
//        }
        if (featureCode == 26626) {
            switch (troubleCode) {
                case 64:
                case 69:
                    status = 0;
                    break;
                case 65:
                case 66:
                case 67:
                case 68:
                    status = 1;
                    break;
            }
        }
        return status;
    }

    /**
     * 转化市级电枪云快充停止原因
     *
     * @param stopReason 云快充停止原因
     * @return
     */
    public static Integer getYKCStopReason(Integer stopReason) {
        int status = 5;
        if (StringUtil.isNotEmpty(stopReason)) {
            switch (stopReason) {
                case 0x40:
                case 0x41:
                case 0x42:
                case 0x43:
                case 0x44:
                case 0x45:
                    status = 0;
                    break;
                case 0x6A:
                case 0x6B:
                case 0x70:
                case 0x6F:
                case 0x71:
                case 0x73:
                case 0x77:
                case 0x6C:
                case 0x75:
                case 0x7A:
                case 0x81:
                case 0x6D:
                case 0x6E:
                case 0x72:
                case 0x74:
                case 0x76:
                case 0x78:
                case 0x79:
                case 0x7B:
                case 0x7C:
                case 0x7D:
                case 0x7E:
                case 0x7F:
                case 0x80:
                case 0x82:
                case 0x83:
                case 0x84:
                case 0x85:
                case 0x86:
                case 0x87:
                case 0x88:
                case 0x89:
                    status = 2;
                    break;
                case 0x90:
                    break;

            }
        }
        return status;
    }

    public static Integer getFaultType(Integer faultCode) {
        Integer faultType = null;
        switch (faultCode) {
            case 8463:
                faultType = 1;
                break;
            case 16644:
                faultType = 4;
                break;
            case 16645:
                faultType = 5;
                break;
            case 2:
                faultType = 6;
                break;
            case 20736:
                faultType = 9;
                break;
            case 8452:
                faultType = 10;
                break;
            case 257:
                faultType = 11;
                break;
            case 8467:
                faultType = 12;
                break;
            case 8468:
                faultType = 13;
                break;
            case 12562:
                faultType = 17;
                break;
            case 12553:
                faultType = 18;
                break;
            case 12554:
                faultType = 19;
                break;
            case 12547:
                faultType = 20;
                break;
            case 12549:
                faultType = 21;
                break;
            case 12550:
                faultType = 23;
                break;
            case 12544:
                faultType = 27;
                break;
            case 12560:
                faultType = 28;
                break;
            case 261:
                faultType = 33;
                break;
            case 258:
                faultType = 34;
                break;
            case 9460:
                faultType = 35;
                break;
            case 12545:
                faultType = 36;
                break;
            case 12546:
                faultType = 37;
                break;
        }
        return faultType;
    }

    /**
     * 转化市级科大智能停止原因
     *
     * @param stopReason 科大智能停止原因
     * @return 市停止原因
     */
    public static Integer getKDZNStopReason(Integer stopReason) {
        int status = 5;
        if (StringUtil.isNotEmpty(stopReason)) {
            //停止原因 0-正常结算终止 1-急停 2-其他故障终止 3-余额不足 4-BMS通信故障
            switch (stopReason) {
                case 0:
                    status = 0;
                    break;
                case 1:
                    status = 3;
                    break;
                case 2:
                    status = 4;
                    break;
                case 3:
                    status = 1;
                    break;
                case 4:
                    status = 2;
                    break;
            }
        }
        return status;
    }

    /**
     * 转化市级爱充网停止原因
     *
     * @param stopReason 爱充网停止原因
     * @return 市停止原因
     */
    public static Integer getACWStopReason(Integer stopReason) {
        //0：用户手动停止充电；
        //1：客户归属地运营商平台停止
        //充电；
        //2： BMS 停止充电；
        //3：充电机设备故障；
        //4：连接器断开；
        int status = 5;
        if (StringUtil.isNotEmpty(stopReason)) {
            //爱充网停止原因
            switch (stopReason) {
                case 0:
                case 1:
                case 2:
                    status = 0;
                    break;
                case 3:
                case 12:
                case 10:
                    status = 1;
                    break;
                case 4:
                case 18:
                case 14:
                case 15:
                case 17:
                case 22:
                case 20:
                case 21:
                case 16:
                    status = 4;
                    break;
                case 5:
                case 11:
                case 9:
                case 8:
                    status = 3;
                    break;
                case 6:
                case 19:
                case 13:
                case 7:
                    status = 2;
                    break;
                case 23:
                    status = 23;
                    break;
            }
        }
        return status;
    }

    /**
     * 转化市级万马电桩停止原因
     *
     * @param stopReason 万马停止原因
     * @return
     */
    public static Integer getWMAStopReason(Integer stopReason) {
        int status = 5;
        //0：用户手动停止充电；
        //1：客户归属地运营商平台停止
        //充电；
        //2： BMS 停止充电；
        //3：充电机设备故障；
        //4：连接器断开；
        if (StringUtil.isNotEmpty(stopReason)) {
            switch (stopReason) {
                case 0x0107:
                case 0x0201:
                case 0x0202:
                    status = 0;
                    break;
                case 0x0203:
                    status = 1;
                    break;
                case 0x2001:
                case 0x2002:
                case 0x2003:
                case 0x2005:
                case 0x2006:
                case 0x2007:
                case 0x2008:
                case 0x2009:
                    status = 2;
                    break;
                case 0xFE00:
                case 0xFE01:
                    status = 4;
                    break;
                default:
                    status = 3;
                    break;
            }
        }
        return status;
    }

}
