package com.sunmax.configure.util;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.io.IOException;

@ClientEndpoint
@Slf4j
public class WebSocketClientUtil {

    //获取到的数据
    public static String data = null;

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        log.info("websocket测试连接成功");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("获取到数据字节长度: {}", message.length());
        // 在这里处理接收到的数据
        data = DataHandleUtil.getResponseData(message);
        session.close();
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Disconnected from server. Reason: " + closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("websocket推送错误信息");
    }


//    public static void main(String[] args) throws UnsupportedEncodingException {
//        System.out.println(URLEncoder.encode("}", "UTF-8"));
//    }
    public static void main(String[] args) {
//        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//        try {
//            Map<String, Object> paramMap = Maps.newHashMap();
//            paramMap.put("userId","2c9e90d98fdd8f01019029fab10f013b");
//            paramMap.put("deviceIds","2c9e90d98f184a59018f190a52560017");
//            String jsonString = "\"userId\":\"2c9e90d98fdd8f01019029fab10f013b\",\"deviceIds\":\"2c9e90d98f184a59018f190a52560017\"";
//            System.out.println(jsonString);
//            String encode = URLEncoder.encode(jsonString, "UTF-8").replace("%", "@");
//            System.out.println(encode);
//            URI uri = URI.create("wss://sunos.dpcd.tech/scrontab/configFuncPointWebSocket/" + encode);
//            container.connectToServer(WebSocketClientUtil.class, uri);
//            do {
//                Thread.sleep(1000);
//            }
//            while (WebSocketClientUtil.data == null);
//            // 等待数据
//            Object result = WebSocketClientUtil.data;
//            System.out.println(result);
//            WebSocketClientUtil.data = null;
//            System.out.println(WebSocketClientUtil.data);
//            // 注意：由于WebSocket是异步的，这里main方法会立即返回。
//            // 但是由于我们在onOpen方法中调用了session.close()，所以连接会很快关闭。
//            // 为了简单起见，我们可以不等待任何输入或延迟。
//        } catch (Exception e) {
//            log.error("连接websocket报错", e);
//        }

        System.out.println(DataHandleUtil.getResponseData("{\"code\":20000,\"data\":{\"dataMap\":{\"2c99698b927f794001928514af130042\":{\"chName\":\"ems20241012001\",\"enName\":\"2c99698b927f794001928514af130042\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"},\"2c99698b927f7940019285158f990043\":{\"chName\":\"30kW/60kWh储能一体柜\",\"enName\":\"2c99698b927f7940019285158f990043\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"},\"2c99698b927f794001928515fab10044\":{\"chName\":\"电池簇\",\"enName\":\"2c99698b927f794001928515fab10044\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"},\"2c99698b927f79400192851697890045\":{\"chName\":\"储能空调\",\"enName\":\"2c99698b927f79400192851697890045\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"},\"2c99698b92d78d620192f6a44d5300d9\":{\"chName\":\"18kW锦浪逆变器\",\"enName\":\"2c99698b92d78d620192f6a44d5300d9\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"},\"2c99698b92fb99950192ff6701a30043\":{\"chName\":\"关口总计量表\",\"enName\":\"2c99698b92fb99950192ff6701a30043\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"},{\"chName\":\"峰时正向无功电能\",\"enName\":\"peak_sup_kvar\",\"fieldData\":\"0.000\",\"fieldType\":\"String\"}],\"fieldType\":\"ArrayList\"},\"2c99698b93237fe301933e127677005c\":{\"chName\":\"并网点电能表\",\"enName\":\"2c99698b93237fe301933e127677005c\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"}},\"desc\":\"sunos平台推送设备功能点数据接口\"},\"message\":\"操作成功\",\"success\":true}"));

    }

}
