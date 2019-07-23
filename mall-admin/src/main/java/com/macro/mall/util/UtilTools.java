package com.macro.mall.util;


import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class UtilTools {

	@Value("${pay-info.signKey}")
	private static String PAY_SIGNKEY;
	
	/**
	 * 验证签名
	 * @param $data     返回的数据
	 * @param $signKey  签名密钥
	 * @return bool
	 * @throws Error
	 */
	public static boolean check_sign(String sr, String signKey) {
		TreeMap<String, Object> map2 = new TreeMap<String, Object>(new Comparator<String>() {
            public int compare(String obj1, String obj2) {
                // 按照key升序排序
                return obj1.compareTo(obj2);
            }
        });		
		JSONObject jsonObject = JSONObject.fromObject(sr);
		
		Iterator<String> it = jsonObject.keys();
		while(it.hasNext()){
			// 获得key
			String key = it.next();
			String value = jsonObject.getString(key);
			
			//如果是key是data 且里面含有postData结构，则需要把value里面的postData结构都加上转义字符
			if(key.equals("data") && value.indexOf("postData")!=-1) {
				int index = value.indexOf("postData");
				String pre = value.substring(0, index+10);
				String last = value.substring(index+10);
				int in = last.indexOf("}");
				String target = last.substring(0, in+1);
				String left = last.substring(in+1);
				
				String result = "";
				String pre_char = String.valueOf(target.charAt(0));
				for (int i = 0; i < target.length(); i++) { 					 
					 String item =  String.valueOf(target.charAt(i));
					 if(item.equals("\"")) {
						 result += "\\"+"\"";
					 }else {
						 result += item;
					 }
					 pre_char = item;
				 } 
				 String m = pre+"\""+result+"\""+left;
				 
				 map2.put(key, m);
			}else {
				map2.put(key, value);
			}			
		}
				
        String key_sort = "";
 
        Set<String> keySet = map2.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
        		
            String key = iter.next();
            
            //过滤掉sign或者value为空的
            if(key.equals("sign") || map2.get(key).toString().equals("")) continue;
            
            key_sort = key_sort + key + "=" + map2.get(key).toString() + "&";
            
        }
        System.out.println(key_sort);

        String sign = generate_sign(key_sort.toLowerCase().substring(0, key_sort.length() - 1),getSignKey());
        
        if(!map2.containsKey("code") || !map2.get("code").equals("00000")) {
        		return false;
        }

        if(!map2.containsKey("sign") || !map2.get("sign").equals(sign)) {
        		return false;
        }
		return true;
	}
	
	/**
	 * 生成签名
	 * @param $data     提交的数据
	 * @param $signKey  签名密钥
	 * @return string   签名结果
	 */
	public static String generate_sign(String data, String signKey){
		System.out.println(data.toLowerCase()+signKey);
		return stringToMD5(data.toLowerCase()+signKey);
	}

	public static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        int len = md5code.length();
        for (int i = 0; i < 32 - len; i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
	
	//此处填写你的Key
	public static String getSignKey() {
		return PAY_SIGNKEY;
	}
	//支付宝支付
	public static String getAlipayUrl() {
		return "http://www.yppay.info/payapi/api/alipay";
	}
	//快捷支付
	public static String getQuickPayUrl() {
		return "http://www.yppay.info/payapi/api/quickpay";
	}
	//代付
	public static String getPayoutUrl() {
		return "http://www.yppay.info/payapi/api/payout";
	}
	//网银支付
	public static String getGatewayPayUrl() {
		return "http://www.yppay.info/payapi/api/gateway";
	}
	//微信支付
	public static String getWxPayUrl(){ return "http://www.yppay.info/payapi/api/wechat";};

	public static String getQueryUrl() {
		return "http://www.yppay.info/payapi/api/query";
	}
	

}
