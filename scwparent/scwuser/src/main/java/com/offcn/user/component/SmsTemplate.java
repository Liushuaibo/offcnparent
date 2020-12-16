package com.offcn.user.component;

import com.offcn.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SmsTemplate {

    @Value("${sms.host}")
    private String host;
    @Value("${sms.path}")
    private String path;
    @Value("${sms.method:POST}")
    private String method;
    @Value("${sms.appcode}")
    private String appcode;

    Logger log = LoggerFactory.getLogger(getClass());

    public String sendCode(Map<String,String> querys){
        //1、阿里云 httpClient 工具类的返回值
        HttpResponse response = null;
        //2、设置请求头
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        //3、设置响应体对象
        Map<String, String> bodys = new HashMap<String, String>();
        try {

            if (method.equalsIgnoreCase("get")) {
                response = HttpUtils.doGet(host, path, method, headers, querys);
            } else {
                response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            }
            //4、HttpClient 的返回值 对象规格化
            String string = EntityUtils.toString(response.getEntity());

            log.info("短信发送完成；响应数据是：{}", string);

            return string;
        } catch (Exception e) {
            log.error("短信发送失败；发送参数是：{}", querys);
            return "fail";
        }
    }


}
