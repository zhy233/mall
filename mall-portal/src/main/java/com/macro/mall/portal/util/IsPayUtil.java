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
import java.security.NoSuchAlgorithmException;
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
        System.out.println("key字符串:"+sb.toString());
        sbkey = sbkey.append(key);
        System.out.println("拼接value:" + sbkey.toString());
        //MD5加密,结果转换为大写字符
        String sign = MD5Utils.getMD5(sbkey.toString());
        System.out.println("MD5加密值:" + sign);
        return sign;
    }
    public static String md5(String str){
        String result = "";

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update((str).getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte b[] = md5.digest();

        int i;
        StringBuffer buf = new StringBuffer("");

        for(int offset=0; offset<b.length; offset++){
            i = b[offset];
            if(i<0){
                i+=256;
            }
            if(i<16){
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }

        result = buf.toString();
        System.out.println("result = " + result);
        return result;
    }



    //MD5 32位小写加密
    public static String encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes("UTF-8"));
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

    public static void main(String[] args) throws IOException {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("payId", 10000);
        parameters.put("payChannel", "alipay");
        parameters.put("Subject", "测试标题");
        parameters.put("Money", 500);
        parameters.put("orderNumber", "201702080118441263011007");
        parameters.put("attachData", "test");
        parameters.put("Notify_url", "https://www.ispay.cn/notify/");
        parameters.put("Return_url", "https://www.ispay.cn/return/");
        String sign = generate_sign(parameters, "");
        System.out.println("签名:"+ sign);
//        parameters.put("Sign",sign);

//        parameters.put("payKey", "087fa038a57f2192023c44eb77e14620");
//
//        Map<Object,Object> stringMap = new HashMap<>();
//        stringMap.putAll(parameters);
//
////        JSONObject jsonObject = JSONObject.fromObject(parameters);
////        String s = jsonObject.toString();
////        System.out.println("请求:" + s);
//        //统一下单
//        String result = HttpClientUtil.doPostForm("https://pay11.ispay.cn/core/api/request/pay/", stringMap,"UTF-8");
//        System.out.println("结果:" + result);
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
