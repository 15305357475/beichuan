package org.fh.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import com.alibaba.fastjson.JSONObject;

// Http的Get请求和Post请求
public class HttpRequest {

	// HttpPost请求:HttpPost方式
	public static String post(JSONObject json, String url) {
		String result = "";
		HttpPost post = new HttpPost(url);
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			post.setHeader("Content-Type", "application/json;charset=utf-8");
			StringEntity postingString = new StringEntity(json.toString(), "utf-8");
			post.setEntity(postingString);
			HttpResponse response = httpClient.execute(post);
			InputStream in = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			StringBuilder strber = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				strber.append(line + '\n');
			}
			br.close();
			in.close();
			result = strber.toString();
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				result = "服务器异常";
			}
		} catch (Exception e) {
			System.out.println("请求异常");
			throw new RuntimeException(e);
		} finally {
			post.abort();
		}
		return result;
	}

	// HttpPost请求：HttpURLConnection方式
	public static String HttpPost(JSONObject json,String url) throws IOException {
		OutputStreamWriter out = null;
		BufferedReader reader = null;
		String response = "";
		try {
			URL httpUrl = null; // HTTP URL类 用这个类来创建连接
			// 创建URL
			httpUrl = new URL(url);
			// 建立连接
			HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setUseCaches(false);// 设置不要缓存
			conn.setInstanceFollowRedirects(true);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.connect();
			// POST请求
			out = new OutputStreamWriter(conn.getOutputStream());
			out.write(json.toString());// 请求参数
			out.flush();
			// 读取响应
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String lines;
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				response += lines;
			}
			reader.close();
			// 断开连接
			conn.disconnect();
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return response;
	}

	// HttpGet请求，无参
	public static String get(String url) {
		String result = "";
		HttpGet get = new HttpGet(url);
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpResponse response = httpClient.execute(get);
			result = getHttpEntityContent(response);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				result = "服务器异常";
			}
		} catch (Exception e) {
			System.out.println("请求异常");
			throw new RuntimeException(e);
		} finally {
			get.abort();
		}
		return result;
	}

	// 获取响应对象
	public static String getHttpEntityContent(HttpResponse response) throws UnsupportedOperationException, IOException {
		String result = "";
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream in = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			StringBuilder strber = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				strber.append(line + '\n');
			}
			br.close();
			in.close();
			result = strber.toString();
		}
		return result;
	}

	// HttpGet请求，带参
	public static String get(Map<String, String> paramMap, String url) {
		String result = "";
		HttpGet get = new HttpGet(url);
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			List<NameValuePair> params = setHttpParams(paramMap);
			String param = URLEncodedUtils.format(params, "UTF-8");
			get.setURI(URI.create(url + "?" + param));
			HttpResponse response = httpClient.execute(get);
			result = getHttpEntityContent(response);

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				result = "服务器异常";
			}
		} catch (Exception e) {
			System.out.println("请求异常");
			throw new RuntimeException(e);
		} finally {
			get.abort();
		}
		return result;
	}

	// 设置参数
	public static List<NameValuePair> setHttpParams(Map<String, String> paramMap) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Set<Map.Entry<String, String>> set = paramMap.entrySet();
		for (Map.Entry<String, String> entry : set) {
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return params;
	}
}
