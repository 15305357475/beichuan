package org.fh.util.wx;


import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 微信工具类
 * @Author Qiu
 * @Date 2022/2/21 13:40
 */
public class WxUtil {
        /**
         * 创建缓存器 默认2小时
         */
        private final static TimedCache<String, String> TIMED_CACHE = CacheUtil.newTimedCache(7200 * 1000);
        /**
         * 小程序唯一标识   (在微信小程序管理后台获取)
         */
        private final static  String WX_AppID = "wx9daf503ceeb8a940";
        /**
         * 小程序的  secret (在微信小程序管理后台获取)
         */
        private final static String WX_Secret = "243e30cb51216ac3411fb4dd92b9833d";

        /**
        * 获取access_token
        *@Author Qiu
        *@Date 2022-02-21 16:51
        *@param
        *@return
        */
        public static String getAccessToken(){
                //先从缓存中获取accessToken,如果为空则再去请求获取token的接口来进行获取
                String accessToken = TIMED_CACHE.get("wx_access_token:", false);

                //判断当token不为空时,直接返回token
                if (StrUtil.isNotBlank(accessToken)) {
                        return accessToken;
                }

                //请求接口获取accessToken
                accessToken = getAccessTokenRequest();

                //将最新获取的accessToken放入缓存中
                TIMED_CACHE.put("wx_access_token:", accessToken);
                return accessToken;

        }

        /**
        * 获取accessToken请求
        *@Author Qiu
        *@Date 2022-02-21 16:55
        *@param
        *@return
        */
        private static String getAccessTokenRequest(){
                RestTemplate restTemplate = new RestTemplate();
                Map<String, String> params = new HashMap<>();
                params.put("APPID", WX_AppID);
                params.put("APPSECRET", WX_Secret);
                ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                        "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={APPID}&secret={APPSECRET}", String.class, params);
                String body = responseEntity.getBody();
                JSONObject object = JSON.parseObject(body);
                String Access_Token = object.getString("access_token");
                String expires_in = object.getString("expires_in");
                System.out.println("有效时长expires_in：" + expires_in);
                return Access_Token;

        }

        /**
        * 发送订阅消息
        *@Author Qiu
        *@Date 2022-02-21 17:08
        *@param
        *@return
        */
        public static String pushOneUser(String openid,String insert_time,String describes,String id) {

                RestTemplate restTemplate = new RestTemplate();
                WxMssVo wxMssVo = new WxMssVo();
                //用户的openid（要发送给那个用户，通常这里应该动态传进来的）
                wxMssVo.setTouser(openid);
                //订阅消息模板id
                wxMssVo.setTemplate_id("gb9NEGtAyg_8tNm4MGTkFV7JGrAgI1CJnjU0Wy4Vr9M");
                //点击模板跳转的页面
                wxMssVo.setPage("pages/report/reportDetails?id=" + id);

                Map<String, TemplateData> m = new HashMap<>(3);
                m.put("thing1", new TemplateData(describes));
                m.put("time2", new TemplateData(insert_time));
                m.put("thing3", new TemplateData("中国人"));
                wxMssVo.setData(m);

                String accessToken = WxUtil.getAccessToken();
                String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, wxMssVo, String.class);

                System.out.println(responseEntity);
                return String.valueOf(responseEntity);
        }


}
