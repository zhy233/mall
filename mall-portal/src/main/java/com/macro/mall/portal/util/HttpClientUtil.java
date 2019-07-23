package com.macro.mall.portal.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.macro.mall.portal.component.Namevaluepairforhttp;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

public class HttpClientUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	//post 参数 json ， Map 
	public static String post(String url,String json,Map<Object,Object> paramters)  {
		RequestConfig config = null;
		CloseableHttpClient client = null;
		HttpPost hp = null;
		try {
			config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
			client = HttpClients.custom().setDefaultRequestConfig(config).build();

			hp = new HttpPost(url);
			if(paramters != null && !paramters.isEmpty()){
				List<Namevaluepairforhttp> formparams = new ArrayList<Namevaluepairforhttp>();
				for(Entry<Object,Object> entry : paramters.entrySet()){
					Namevaluepairforhttp formparam = new Namevaluepairforhttp();
					formparam.setName(entry.getKey()+"");
					formparam.setValue(entry.getValue()+"");
					formparams.add(formparam);
				}
				UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
				hp.setEntity(uefEntity);
			}
			if(json != null && json.trim().length() > 0){
				List<Namevaluepairforhttp> formparams = new ArrayList<Namevaluepairforhttp>();
				Namevaluepairforhttp formparam = new Namevaluepairforhttp();
				formparam.setName("data");
				formparam.setValue(json);
				formparams.add(formparam);
				UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
				hp.setEntity(uefEntity);
			}
			HttpResponse response = client.execute(hp);
			String resultjson = HttpClientUtil.paseResponse(response);

			return resultjson;
		}catch(Exception ex) {

			StringWriter stringWriter= new StringWriter();
			PrintWriter writer= new PrintWriter(stringWriter);
			logger.error(writer.toString());
			StringBuffer buffer= stringWriter.getBuffer();
			logger.error(buffer.toString());
		} finally {
			if(hp != null){
				try {
					hp.releaseConnection();
				} catch (Exception e) {
					StringWriter stringWriter= new StringWriter();
					PrintWriter writer= new PrintWriter(stringWriter);
					logger.error(writer.toString());
					StringBuffer buffer= stringWriter.getBuffer();
					logger.error(buffer.toString());
				}
			}
			if(client != null){
				try {
					client.close();
				} catch (IOException e) {
					StringWriter stringWriter= new StringWriter();
					PrintWriter writer= new PrintWriter(stringWriter);
					logger.error(writer.toString());
					StringBuffer buffer= stringWriter.getBuffer();
					logger.error(buffer.toString());
				}
			}
		}
		return "";
	}

	public static String postfile(String url,byte[] filedata)  {
		RequestConfig config = null;
		CloseableHttpClient client = null;
		try {
			config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
			client = HttpClients.custom().setDefaultRequestConfig(config).build();

			HttpPost hp = new HttpPost(url);
			hp.setEntity(new ByteArrayEntity(filedata));
			HttpResponse response = client.execute(hp);
			String resultjson = HttpClientUtil.paseResponse(response);
			return resultjson;
		}catch(Exception ex) {
			logger.error(ex.getMessage());
		} finally {
			if(client != null){
				try {

					client.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
		}
		return "";
	}


	public static String get(String url) {
		CloseableHttpClient client = null;
		RequestConfig config = null;
		try {
			config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
			client = HttpClients.custom().setDefaultRequestConfig(config).build();
			HttpGet hp = new HttpGet(url);
			HttpResponse response = client.execute(hp);
			String resultjson = HttpClientUtil.paseResponse(response);
			return resultjson;
		}catch (Exception e){
			logger.error(e.toString());
		}finally {
			try {
				client.close();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		return "";
	}

	public static String paseResponse(HttpResponse response) {
		//log.info("get response from http server..");
		HttpEntity entity = response.getEntity();

//        log.info("response status: " + response.getStatusLine());
//	   	  String charset = EntityUtils.getContentCharSet(entity);
//        log.info(charset);

		String body = null;
		try {
			body = EntityUtils.toString(entity);
//            log.info(body);
		}catch (IOException e) {
			logger.error(e.toString());
		}

		return body;
	}

	public static <T> Object getRequestdate(byte[] responseBody,Class<T> clazz,Map<String,Class<T>> classMap){

		if(responseBody == null || responseBody.length == 0){
			return null;
		}

		JSONObject jo = JSONObject.fromObject(responseBody);

		Object ccb = JSONObject.toBean(jo, clazz, classMap);
		return ccb;
	}

	public static <T> Object getRequestdate(byte[] responseBody, TypeReference<T> typeref) throws Exception{

		String s3 = new String(responseBody,"utf-8");

		return getRequestdate(s3, typeref);
	}

	public static <T> List<T> getRequestdateList(String s3,
												 TypeReference<List<T>> typeref) throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		// 去掉默认的时间戳格式
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		// 设置为中国北京时区
		objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		// 空值不序列化
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		// 反序列化时，属性不存在的兼容处理
		objectMapper.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		// 单引号处理
		objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		return objectMapper.readValue(s3,typeref);
	}
	public static <T> Object getRequestdate(String s3,
											TypeReference<T> typeref) throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		// 去掉默认的时间戳格式
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		// 设置为中国北京时区
		objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		// 空值不序列化
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		// 反序列化时，属性不存在的兼容处理
		objectMapper.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 单引号处理
		objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		return objectMapper.readValue(s3,typeref);
	}

	public static <T> List<T> getRequestdateList(byte[] responseBody,
												 TypeReference<List<T>> typeref) throws Exception{
		String s3 = "";
		if(responseBody != null){
			s3 = new String(responseBody,"utf-8");
			return getRequestdateList(s3, typeref);
		} else {
			return new ArrayList<>();
		}
	}

	public static String doPostClientforfile(InputStream in) throws Exception{

		RequestConfig config = null;
		CloseableHttpClient client = null;
		HttpPost hp = null;
		try {
			config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
			client = HttpClients.custom().setDefaultRequestConfig(config).build();

			HttpPost httpPost = new HttpPost("http://139.196.200.103:8099/d/if001");
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("file", in, ContentType.MULTIPART_FORM_DATA, "pdffile");// 文件流
			builder.addTextBody("filename", "pdffile");// 类似浏览器表单提交，对应input的name和value
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);// 执行提交
			HttpEntity responseEntity = response.getEntity();
			String resultjson = "";
			if (responseEntity != null) {
				// 将响应内容转换为字符串
				resultjson = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
				logger.error("resultjson%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%:" + resultjson);
			}
			return resultjson;
		}catch(Exception ex) {
			logger.error(ex.getMessage());
		} finally {
			if(hp != null){
				try {
					hp.releaseConnection();
				} catch (Exception e) {
					logger.error(e.toString());
				}
			}
			if(client != null){
				try {
					client.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
		}
		return "";
	}


/*	public static void main(String[] args) throws  Exception {
		InputStream in =  new java.io.FileInputStream("C:\\work\\36.pdf");
		String ddd = doPostClientforfile(in);
		logger.info(ddd);
	}*/

/*	public static String doPostClientforfile(InputStream in) throws Exception{
		HttpClient httpClient = new HttpClient();
		System.setProperty("apache.commons.httpclient.cookiespec","COMPATIBILITY");
		PostMethod postMethod = new PostMethod("http://139.196.200.103:8099/d/if001");
		postMethod.setRequestBody(in);
		HttpClientParams params = new HttpClientParams();
		params.setConnectionManagerTimeout(10000L);
		httpClient.setParams(params);
		try {
			httpClient.executeMethod(postMethod);
			//获取二进制的byte流
			byte[] b = postMethod.getResponseBody();
			String str = new String(b,"UTF-8");
			return str;
		}catch (Exception e) {
			// TODO: handle exception
			logger.info(e.toString()+","+e.getStackTrace());
			return "";
		}finally{
			postMethod.releaseConnection();
			in.close();
		}
	}*/

	public static boolean doUploadClientforfile(byte[] file, File existfile) throws Exception{

		FileOutputStream fos = null;
		BufferedInputStream bis = null;
//		existfile.renameTo(new File(fileNewName));
		try {
			bis = new BufferedInputStream(new ByteArrayInputStream(file));
			fos = new FileOutputStream(existfile);
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1)
				fos.write(buf, 0, size);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(e.toString());
			return false;
		}finally{
			fos.close();
			bis.close();
		}

		logger.info("upload file success======================");
		return true;
	}

	public static byte[] getfile(String url) {
		CloseableHttpClient client = null;
		RequestConfig config = null;
		try {
			config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
			client = HttpClients.custom().setDefaultRequestConfig(config).build();
			HttpGet hp = new HttpGet(url);
			HttpResponse response = client.execute(hp);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			return out.toByteArray();
		}catch (Exception e){
			logger.error(e.toString());
		}finally {
			try {
				client.close();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		return null;
	}


	public static String doPostClientforfileWithHeader(InputStream in, String filename, String url,String token) throws Exception{

		RequestConfig config = null;
		CloseableHttpClient client = null;
		HttpPost hp = null;
		try {
			config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
			client = HttpClients.custom().setDefaultRequestConfig(config).build();

			HttpPost httpPost = new HttpPost(url);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("file", in, ContentType.MULTIPART_FORM_DATA, filename);// 文件流
			builder.addTextBody("filename", "file");// 类似浏览器表单提交，对应input的name和value
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			httpPost.setHeader("Authorization",token); //Security框架需要身份校验
			HttpResponse response = client.execute(httpPost);// 执行提交
			HttpEntity responseEntity = response.getEntity();
			String resultjson = "";
			if (responseEntity != null) {
				// 将响应内容转换为字符串
				resultjson = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
				logger.error("resultjson%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%:" + resultjson);
			}
			return resultjson;
		}catch(Exception ex) {
			logger.error(ex.toString());
		} finally {
			if(hp != null){
				try {
					hp.releaseConnection();
				} catch (Exception e) {
					logger.error(e.toString());
				}
			}
			if(client != null){
				try {
					client.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
		}
		return "";
	}

	public static byte[] getBytes(File file){
		ByteArrayOutputStream out = null;
		try {
			FileInputStream in = new FileInputStream(file);
			out = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = in.read(b)) != -1) {

				out.write(b, 0, i);
			}
			out.close();
			in.close();
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		}
		byte[] s = out.toByteArray();
		return s;

	}

	private static String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};

	public static boolean isMSBrowser(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		for (String signal : IEBrowserSignals) {
			if (userAgent.contains(signal))
				return true;
		}
		return false;
	}

	public static String doPost(String url, JSONObject param) {
		HttpPost httpPost = null;
		String result = null;
		try {
			HttpClient client = new DefaultHttpClient();
			httpPost = new HttpPost(url);
			if (param != null) {
				StringEntity se = new StringEntity(param.toString(), "utf-8");
				httpPost.setEntity(se); // post方法中，加入json数据
				httpPost.setHeader("Content-Type", "application/json");
			}

			HttpResponse response = client.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "utf-8");
				}
			}

		} catch (Exception ex) {
			logger.error("发送到接口出错", ex);
		}
		return result;
	}

}
