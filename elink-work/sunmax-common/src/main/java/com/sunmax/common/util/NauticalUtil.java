package com.sunmax.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.StaticParamVo;
import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

import javax.net.ssl.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * 经纬度工具类
 */
@Slf4j
public class NauticalUtil {

    //高德的key
    public static final String key = "5dc77a1370ae5b23494b87f255e5d5fe";

    /**
     * 获取该经纬度附近的最大经纬度以及最小经纬度
     *
     * @param latAndLongitude 经纬度
     * @param dist            距离
     */
    public static Map<String, Double> getNearbyNautical(String latAndLongitude, double dist) {
        List<String> degList = Arrays.asList(latAndLongitude.split(FileUtil.COMMA));
        double latitude = Double.parseDouble(degList.get(0));
        double longitude = Double.parseDouble(degList.get(1));
        Map<String, Double> resultMap = new HashMap<>();
        //先计算查询点的经纬度范围
        double radius = 6371;//地球半径 千米

        //计算出经度
        double dilate = dist / radius * 180 / Math.PI;
        double minLatitude = latitude - dilate; //最小经度
        double maxLatitude = latitude + dilate; //最大经度

        //计算出纬度
        double radian = 2 * Math.asin(Math.sin(dist / (2 * radius)) / Math.cos(latitude * Math.PI / 180)) * 180 / Math.PI;
        double minLongitude = longitude + radian;//最小纬度
        double maxLongitude = longitude - radian;//最大纬度

        resultMap.put(StaticParamVo.MIN_LATITUDE, minLatitude);
        resultMap.put(StaticParamVo.MAX_LATITUDE, maxLatitude);
        resultMap.put(StaticParamVo.MIN_LONGITUDE, minLongitude);
        resultMap.put(StaticParamVo.MAX_LONGITUDE, maxLongitude);
        return resultMap;
    }

    /**
     * 获取经纬度间的距离
     *
     * @param startLatAndLon 首端经纬度
     * @param endLatAndLon   末端经纬度
     * @return 距离
     */
    public static String getDistance(String startLatAndLon, String endLatAndLon) {
        List<String> startList = Arrays.asList(startLatAndLon.split(FileUtil.COMMA));
        double startLat = Double.parseDouble(startList.get(0));
        double startLon = Double.parseDouble(startList.get(1));

        List<String> endList = Arrays.asList(endLatAndLon.split(FileUtil.COMMA));
        double endLat = Double.parseDouble(endList.get(0));
        double endLon = Double.parseDouble(endList.get(1));
        //出发地经纬度
        GlobalCoordinates source = new GlobalCoordinates(startLon, startLat);
        //目的地经纬度
        GlobalCoordinates target = new GlobalCoordinates(endLon, endLat);
        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.Sphere, source, target);
        double distance = geoCurve.getEllipsoidalDistance();
        if (distance < 1000) {
            return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_EVEN) + "m";
        } else {
            return BigDecimal.valueOf(distance / 1000).setScale(2, RoundingMode.HALF_EVEN) + "km";
        }
    }


    /**
     * 高德地图经纬度转换成百度地图经纬度
     *
     * @param gdLat 经度
     * @param gdLon 纬度
     * @return
     */
    public static String gaoDeToBaidu(double gdLat, double gdLon) {
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gdLon, y = gdLat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        double bdLat = z * Math.cos(theta) + 0.0065;
        double bdLon = z * Math.sin(theta) + 0.006;
        return bdLat + FileUtil.COMMA + bdLon;
    }

    /**
     * 百度地图经纬度转换成高德地图经纬度
     *
     * @param bdLat 经度
     * @param bdLon 纬度
     * @return
     */
    public static String bdToGaoDe(double bdLat, double bdLon) {
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bdLon - 0.0065, y = bdLat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        double gdLat = z * Math.sin(theta);
        double gdLon = z * Math.cos(theta);
        return gdLat + FileUtil.COMMA + gdLon;
    }

    /**
     * 根据经纬度获取城市名称
     */
    public static String getCityByLatAndLongitude(String latAndLongitude) {
        String cityName = null;
        //参数解释: 纬度,经度 采用高德API可参考高德文档https://lbs.amap.com/
        try {
            String result = sendHttpGet("https://restapi.amap.com/v3/geocode/regeo?location=" + latAndLongitude + "&key=" + key);
            //解析结果
            if (StringUtil.isNotEmpty(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.get("status").equals("1")) {
                    cityName = jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent").getString("city");
                }
            }
        } catch (Exception e) {
            log.error("获取地址信息异常: ", e);
            return null;
        }
        return cityName;
    }

    /**
     * 根据经纬度获取省市区名称
     */
    public static Map<String, String> getAddressByLatAndLongitude(String latAndLongitude) {
        Map<String, String> resultMap = new HashMap<>();
        //参数解释: 纬度,经度 采用高德API可参考高德文档https://lbs.amap.com/
        try {
            String result = sendHttpGet("https://restapi.amap.com/v3/geocode/regeo?location=" + latAndLongitude + "&key=" + key);
            //解析结果
            if (StringUtil.isNotEmpty(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.get("status").equals("1")) {
                    resultMap.put(StaticParamVo.PROVINCE_NAME, jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent").getString("province"));
                    resultMap.put(StaticParamVo.CITY_NAME, jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent").getString("city"));
                    resultMap.put(StaticParamVo.AREA_NAME, jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent").getString("district"));
                    resultMap.put(StaticParamVo.TOWNS_NAME, jsonObject.getJSONObject("regeocode").getJSONObject("addressComponent").getString("township"));
                    resultMap.put(StaticParamVo.ADDRESS_NAME,jsonObject.getJSONObject("regeocode").getString("formatted_address")
                            .replace(resultMap.get(StaticParamVo.PROVINCE_NAME), FileUtil.separator).replace(resultMap.get(StaticParamVo.CITY_NAME), FileUtil.separator)
                            .replace(resultMap.get(StaticParamVo.AREA_NAME), FileUtil.separator));
                }
            }
        } catch (Exception e) {
            log.error("获取地址信息异常{}", e.getMessage());
        }
        return resultMap;
    }


    public static void main(String[] args) {
        Map<String, String> addressMap = NauticalUtil.getAddressByLatAndLongitude("121.84279217184178,29.91191751380522");
        if (!addressMap.isEmpty()) {
            System.out.println("省:"+addressMap.get(StaticParamVo.PROVINCE_NAME));
            System.out.println("市:"+addressMap.get(StaticParamVo.CITY_NAME));
            System.out.println("区:"+addressMap.get(StaticParamVo.AREA_NAME));
            System.out.println("镇:"+addressMap.get(StaticParamVo.TOWNS_NAME));
            System.out.println("地址:"+addressMap.get(StaticParamVo.ADDRESS_NAME));
        }
    }

    /**
     * 根据城市名称获取经纬度
     */
    public static String getLatAndLongitudeByCity(String cityName) {
        String latAndLongitude = null;
        //参数解释: 纬度,经度 采用高德API可参考高德文档https://lbs.amap.com/
        try {
            // 请求高德接口
            String result = sendHttpGet("http://restapi.amap.com/v3/geocode/geo?address=" + cityName + "&output=JSON&key=" + key);
            if (StringUtil.isNotEmpty(result)) {
                JSONObject resultJOSN = JSONObject.parseObject(result);
                JSONArray geocodesArray = resultJOSN.getJSONArray("geocodes");
                if (geocodesArray.size() > 0) {
                    String position = geocodesArray.getJSONObject(0).getString("location");
                    String[] lngAndLat = position.split(FileUtil.COMMA);
                    String latitude = lngAndLat[0];
                    String longitude = lngAndLat[1];
                    latAndLongitude = latitude + FileUtil.COMMA + longitude;
                }
            }
        } catch (Exception e) {
            log.error("根据市名称查询经纬度失败", e);
        }
        return latAndLongitude;
    }

    /**
     * 根据经纬度获取驾车行驶路线(经纬度)
     * @param startLatAndLon 出发地经纬度
     * @param endLatAndLon   目的地经纬度
     * @return 路线列表
     */
    public static List<String> getDrivePathList(String startLatAndLon, String endLatAndLon) {
        List<String> resultList = new ArrayList<>();
        //请求高德驾车路线接口
        String result = sendHttpGet("https://restapi.amap.com/v3/direction/driving?key=" + key
                + "&origin=" + startLatAndLon + "&destination=" + endLatAndLon);
        if (StringUtil.isNotEmpty(result)) {
            JSONObject drivePath = JSON.parseObject(result);
            if (drivePath.getString("info").equals("OK")) {
                JSONObject route = drivePath.getJSONObject("route");
                if (route != null) {
                    JSONArray paths = route.getJSONArray("paths");
                    paths.forEach(p -> {
                        JSONArray steps = JSONObject.parseObject(p.toString()).getJSONArray("steps");
                        steps.forEach(s -> {
                            resultList.add(JSONObject.parseObject(s.toString()).getString("polyline"));
                        });
                    });
                }
            }
        }
        return resultList;
    }

    /**
     * 地址转换为经纬度
     *
     * @param address 地址
     * @return 经纬度
     */
    public static List<Map<String,String>> getLonAndLatByAddress(String address) {
        List<Map<String, String>> resultList = Lists.newArrayList();
        // 返回输入地址address的经纬度信息, 格式是 经度,纬度
        String queryUrl = "http://restapi.amap.com/v3/geocode/geo?key=" + key + "&address=" + address;
        // 高德接口返回的是JSON格式的字符串
        String result = sendHttpGet(queryUrl);
        if (StringUtil.isNotEmpty(result)) {
            Map<String, String> dataMap = JSONObject.parseObject(result, new TypeReference<Map<String, String>>() {});
            if (dataMap.get("status").equals("1")) {
                JSONArray geocodes = JSON.parseArray(dataMap.get("geocodes"));
                geocodes.forEach(geocode -> {
                    String[] lonAndLat = JSONObject.parseObject(geocode.toString()).getString("location").split(FileUtil.COMMA);
                    if (lonAndLat.length == 2) {
                        Map<String, String> resultMap = Maps.newHashMap();
                        resultMap.put("lng", lonAndLat[0]);
                        resultMap.put("lat", lonAndLat[1]);
                        resultList.add(resultMap);
                    }

                });
            } else {
                throw new RuntimeException("地址转换经纬度失败，错误码：" + dataMap.get("infocode"));
            }
        }
        return resultList;
    }

    /**
     * 发送Get请求
     */
    public static String sendHttpGet(String url) {
        StringBuilder result = new StringBuilder();
        try {
            // 请求高德接口
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            // HTTPS请求设置跳过SSL证书验证
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                httpsConn.setSSLSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY);
                httpsConn.setHostnameVerifier(TRUST_ALL_HOSTNAME_VERIFIER);
            }
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append("\n");
            }
            in.close();
            conn.disconnect();
            if (StringUtil.isNotEmpty(result)) {
                return result.toString();
            }
        } catch (Exception e) {
            log.error("发送请求失败", e);
        }
        return null;
    }

    /**
     * 信任所有证书的SSLSocketFactory
     */
    private static final SSLSocketFactory TRUST_ALL_SSL_SOCKET_FACTORY;

    /**
     * 信任所有主机名的HostnameVerifier
     */
    private static final HostnameVerifier TRUST_ALL_HOSTNAME_VERIFIER = (hostname, session) -> true;

    static {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());
            TRUST_ALL_SSL_SOCKET_FACTORY = sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("初始化SSL上下文失败", e);
        }
    }

    /**
     * 将高德地图的经纬度坐标转换为腾讯地图的经纬度坐标
     *
     * @param lngAndLat 经纬度
     * @return 腾讯地图经纬度数组，第一个元素为腾讯地图经度，第二个元素为腾讯地图纬度
     */
    public static String gaoDeToTencent(String lngAndLat) {
        String[] split = lngAndLat.split(FileUtil.COMMA);
        double lng = Double.parseDouble(split[0]);
        double lat = Double.parseDouble(split[1]);
        double tx_lng = lng + 0.0065, tx_lat = lat + 0.006;
        double x = tx_lng, y = tx_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        tx_lng = z * Math.cos(theta);
        tx_lat = z * Math.sin(theta);
        return tx_lng + FileUtil.COMMA + tx_lat;
    }

    /**
     * 将腾讯地图的经纬度坐标转换为高德地图的经纬度坐标
     *
     * @param lngAndLat 腾讯经纬度
     * @return 高德地图经纬度数组，第一个元素为高德地图经度，第二个元素为高德地图纬度
     */
    public static String tencentToGaoDe(String lngAndLat) {
        String[] split = lngAndLat.split(FileUtil.COMMA);
        double lng = Double.parseDouble(split[0]);
        double lat = Double.parseDouble(split[1]);
        double gd_lng = lng - 0.0065, gd_lat = lat - 0.006;
        double x = gd_lng, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        gd_lng = z * Math.cos(theta);
        gd_lat = z * Math.sin(theta);
        return gd_lng + FileUtil.COMMA + gd_lat;
    }

}
