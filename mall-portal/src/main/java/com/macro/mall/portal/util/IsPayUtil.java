package com.macro.mall.portal.util;

import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.*;

/**
 * ISPAY第三方支付工具类  https://www.kancloud.cn/a1244889999/ispay/355733
 * Create by zhuyong on 2019/7/22
 */
public class IsPayUtil {

    public static String generate_sign(SortedMap<Object, Object> parameters, String key) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbkey = new StringBuffer();
        Set es = parameters.entrySet();  //所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            //空值不传递，不参与签名组串
            if (null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
                sbkey.append(v);
            }
        }
        //System.out.println("字符串:"+sb.toString());
        sbkey = sbkey.append(key);
        System.out.println("字符串:" + sbkey.toString());
        //MD5加密,结果转换为大写字符
        String sign = encrypt32(sbkey.toString());
        System.out.println("MD5加密值:" + sign);
        return sign;
    }

    //MD5 32位小写加密
    public static String encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }

    public static void main(String[] args) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("payId", 12672);
        parameters.put("payChannel", "wxpay");
        parameters.put("Subject", "测试标题");
        parameters.put("Money", 1);
        parameters.put("orderNumber", "201907230202000001");
        parameters.put("attachData", "test");
        String sign = generate_sign(parameters, "087fa038a57f2192023c44eb77e14620");
        parameters.put("Sign",sign);
        parameters.put("Notify_url", "http://47.52.130.84:8085/onePage/isPaySuccess");
        parameters.put("Return_url", "http://www.weixinzishan.cn/#/paySucc");
//        parameters.put("payKey", "087fa038a57f2192023c44eb77e14620");
        JSONObject jsonObject = JSONObject.fromObject(parameters);
        String s = jsonObject.toString();
        System.out.println("请求:" + s);
        //统一下单
        String result = doHttpsPost("https://pay88.ispay.cn/core/api/request/pay/", jsonObject);
        System.out.println("结果:" + result);
    }


    public static String doHttpsPost(String url, JSONObject paramIn) {
        try {

            //https 请求忽略证书

            SSLContextBuilder builder = new SSLContextBuilder();

            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);

            Registry registry = RegistryBuilder.create()

                    .register("http", new PlainConnectionSocketFactory())

                    .register("https", sslConnectionSocketFactory)

                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);

            cm.setMaxTotal(100);

            CloseableHttpClient httpClient = HttpClients.custom()

                    .setSSLSocketFactory(sslConnectionSocketFactory)

                    .setConnectionManager(cm)

                    .build();

            //发送post请求

            HttpPost method = new HttpPost(url);

            //处理中文乱码问题

            StringEntity entity = new StringEntity(paramIn.toString(), "utf-8");

            entity.setContentEncoding("UTF-8");

            entity.setContentType("application/json");

            method.setEntity(entity);

            //发送请求

            HttpResponse result = httpClient.execute(method);

            //请求结束，返回结果

            String resData = EntityUtils.toString(result.getEntity());

            System.out.println("RENWOXING result:" + resData);

            //得到返回结果的j'son格式

//            JSONObject resJson = JSONObject.fromObject(resData);

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        } catch (org.apache.http.ParseException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        }
        return null;
    }
}
