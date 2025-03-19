import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.open.app.api.GenericEventListener;
import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import com.dingtalk.open.app.api.security.AuthClientCredential;
import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;
import shade.com.alibaba.fastjson2.JSON;
import shade.com.alibaba.fastjson2.JSONArray;
import shade.com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
/**
 * 长连接holder, 维护和钉钉开放平台外联网关的Stream长连接
 */
public class PersistenceConnectionHolder {
    public static Set<String> InstanceIdSet = new java.util.HashSet<String>();
    public static Map<String, List<String>> UserList = new HashMap<>();
    public static void main(String[] args) throws Exception {


        List<String> temp1 = new ArrayList<>();
        temp1.add("rnajroSqeal_dXLZhnp5lLKjqGCBqYdmi4aX3oJ8f52vnLt0");
        temp1.add("583920");
        UserList.put("邹文",temp1);

        List<String> temp2 = new ArrayList<>();
        temp2.add("rnajroSqeal_dXLZhnqTlbOAtZqNtnutfnl14I6ifmqviad0");
        temp2.add("583921");
        UserList.put("俞海涛",temp2);


        List<String> temp3 = new ArrayList<>();
        temp3.add("rnajroSqeal_dXLZhnqTlbOAtZqNtnutfnl14I6ifmqviad0");
        temp3.add("583907");
        UserList.put("虞浩澜",temp3);


        //检测时间功率
        try {
            // 1. 配置请求参数
            String webhookUrl = "https://oapi.dingtalk.com/robot/send?access_token=e4a3979b262d94b5820464920a511086c1fee3ef4e0cb43f8d81a7a24047d601";
            String warningMsg = "温馨提醒：下班时间已经到了，节能小助手发现您办公室的空调似乎还没有关闭，请使用OA审批-关闭空调小助申请关闭空调，或者小助手将在5分钟之后为您关闭空调。如仍需保持空调开启状态，请在关闭空调小助手界面申请延时关闭，感谢您对企业绿色发展的支持！";

            // 2. 构建JSON请求体
            JSONObject markdownContent = new JSONObject();
            markdownContent.put("title", "Warings");
            markdownContent.put("text", warningMsg);

            JSONObject requestBody = new JSONObject();
            requestBody.put("msgtype", "markdown");
            requestBody.put("markdown", markdownContent);

            // 3. 创建HTTP连接
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 4. 发送请求
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 5. 验证响应
            int responseCode = conn.getResponseCode();
            //System.out.println("钉钉机器人响应状态码: " + responseCode);

            // 6. 清理连接
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLaterClosed("2025-03-10 17:00",UserList.get("虞浩澜").get(0),UserList.get("虞浩澜").get(1));








        try {
            OpenDingTalkStreamClientBuilder
                    .custom()
                    .credential(new AuthClientCredential("ding8jlgj2qd8b7hdcnf", "-N_n8XNjhAlAqdoeGujkeo8KaJdiZFlIWiuXezWObafbW79NHhdhSnIvs0tt9T3n"))
                    //注册事件监听
                    .registerAllEventListener(new GenericEventListener() {
                        public EventAckStatus onEvent(GenericOpenDingTalkEvent event) {
                            try {
                                //事件唯一Id
                                String eventId = event.getEventId();
                                //事件类型
                                String eventType = event.getEventType();
                                //事件产生时间
                                Long bornTime = event.getEventBornTime();
                                //获取事件体

                                JSONObject bizData = event.getData();
                                //System.out.println("event id: " + eventId + ", event type: " + eventType + ", born time: " + bornTime + ", biz data: " + bizData);
                                String cur = bizData.get("title").toString();
                                int len = cur.length();
                                String name = cur.substring(0,len - 14);

                                if(UserList.containsKey(name)){
                                    getOA(bizData.get("processInstanceId").toString(),getAccessToken());
                                    //debug信息
                                    //getClosed(UserList.get(name).get(0),UserList.get(name).get(1));
                                }else{
                                    System.out.println("当前用户未进行注册！");
                                }

                                //处理事

                                //消费成功
                                return EventAckStatus.SUCCESS;
                            } catch (Exception e) {
                                //消费失败
                                return EventAckStatus.LATER;
                            }
                        }
                    })
                    .build().start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    //getAccessToken
    public static String getAccessToken(){
        try {

            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest req = new OapiGettokenRequest();
            req.setAppkey("ding8jlgj2qd8b7hdcnf");
            req.setAppsecret("-N_n8XNjhAlAqdoeGujkeo8KaJdiZFlIWiuXezWObafbW79NHhdhSnIvs0tt9T3n");
            req.setHttpMethod("GET");
            OapiGettokenResponse rsp = client.execute(req);
            JSONObject jsonObject = new JSONObject();
            jsonObject = JSON.parseObject(rsp.getBody());

            return jsonObject.get("access_token").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
    }

    //获取OA的信息
    public static void getOA(String processInstanceId,String accessToken) {
        try {
            // 设置请求URL和参数
            //String processInstanceId = "your_process_instance_id"; // 替换为实际的processInstanceId
            String apiUrl = "https://api.dingtalk.com/v1.0/workflow/processInstances?processInstanceId=" + processInstanceId;

            // 创建URL对象
            URL url = new URL(apiUrl);

            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为GET
            connection.setRequestMethod("GET");

            // 设置请求头
            connection.setRequestProperty("x-acs-dingtalk-access-token", accessToken);
            connection.setRequestProperty("Content-Type", "application/json");

            // 获取响应代码
            int responseCode = connection.getResponseCode();
            //System.out.println("Response Code: " + responseCode);

            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject jsonObject = new JSONObject();
                jsonObject = JSON.parseObject(response.toString()).getJSONObject("result");

                //user
                String user = jsonObject.get("title").toString();
                user = user.substring(0,user.length() - 14);

                //System.out.println(user);

                String businessId = jsonObject.get("businessId").toString();
                if(InstanceIdSet.contains(businessId)){
                    return;
                }
                InstanceIdSet.add(businessId);
                // 打印JSON响应

                JSONArray formComponentValues = jsonObject.getJSONArray("formComponentValues");

                for (Object formComponentValue : formComponentValues) {
                    JSONObject temp = (JSONObject) formComponentValue; // 获取数组中的每个元素
                    String cur = temp.get("value").toString();
                    if(cur.equals("立即关闭空调")){
                        getClosed(UserList.get(user).get(0),UserList.get(user).get(1));
                        return;
                    }else if(cur.equals("稍后关闭空调")){
                        continue;
                    }
                    getLaterClosed(temp.get("value").toString(),UserList.get(user).get(0),UserList.get(user).get(1));


                }

            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getLaterClosed(String dateTimeString,String url,String deviceID) throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 将字符串解析为 LocalDateTime
        LocalDateTime futureDateTime = LocalDateTime.parse(dateTimeString, formatter);

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 计算时间差
        Duration duration = Duration.between(now, futureDateTime);

        // 获取时间差的秒数
        long secondsDifference = duration.getSeconds() * 1000;

        try {
            // 请求URL
            //String urlString = "https://iot-api.unisoft.cn/ptyVWcgo3p/device/control/?debug=" + url;
            String urlString = "https://iot-api.unisoft.cn/qtyVWcgo4q/device/control/?debug=" + url;
            HttpURLConnection connection = getHttpURLConnectionLater(urlString,deviceID,secondsDifference);

            // 获取响应
            int responseCode = connection.getResponseCode();

            // 关闭连接
            connection.disconnect();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    //关闭url的插座
    public static void getClosed(String url,String deviceID) throws Exception{
        try {
            // 请求URL
            //String urlString = "https://iot-api.unisoft.cn/ptyVWcgo3p/device/control/?debug=" + url;
            String urlString = "https://iot-api.unisoft.cn/qtyVWcgo4q/device/control/?debug=" + url;
            HttpURLConnection connection = getHttpURLConnection(urlString,deviceID);

            // 获取响应
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);



            // 关闭连接
            connection.disconnect();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //closed
    private static HttpURLConnection getHttpURLConnection(String urlString,String deviceID) throws IOException {
        URL url = new URL(urlString);

        // 创建HTTP连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // 请求体中的JSON数据
        String jsonInputString = "{\"device\":" + deviceID +",\"order\":{\"power\":\"0\"}}";


        // 发送请求
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }


    //Laterclosed
    private static HttpURLConnection getHttpURLConnectionLater(String urlString,String deviceID,long time) throws IOException {
        URL url = new URL(urlString);

        // 创建HTTP连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // 请求体中的JSON数据
        String jsonInputString = "{\"device\":" + deviceID +",\"order\":{\"point\":\" " + time + "\"}}";


        // 发送请求
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }







}




