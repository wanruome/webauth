/**
 *	@copyright wanruome-2017
 * 	@author wanruome
 * 	@create 2017年3月23日 下午4:55:39
 */
package com.ruomm.base.http.okhttp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.ruomm.base.http.HttpConfig;
import com.ruomm.base.http.ResponseData;
import com.ruomm.base.tools.FileUtils;
import com.ruomm.base.tools.StringUtils;
import com.ruomm.base.tools.TimeUtils;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DataOKHttp {
	private SimpleDateFormat debugTimeFormat = null;
	// 网络请求的路径
	private String Url;
	// 是否使用Post模式设置参数进行网络请求
	private boolean isPost;
	// 下载请求的请求Body体，可以自己设置请求的RequestBody;
	private RequestBody mRequestBody;
	// 请求的Call,可以调用cancleCall()来取消网络请求
	private Call mCall;
	// 同步请求响应结果
	private final ResponseData mResponseData;
	// 网络请求的Json回调目标解析对象的类型
	private Class<?> cls;
	// 调试模式
	private boolean isDebug = false;
	private String debugTag;
	// 成功的响应码
	private int sucessCode = 200;
	// Cookie是否存储
	private String cookieSavePath = null;
	private String cookieReadPath = null;
	// 编码方式
	private String charsetName;
	private Map<String, String> headersMap = null;

	public DataOKHttp setHeadersMap(Map<String, String> headersMap) {
		this.headersMap = headersMap;
		return this;
	}

	/**
	 * 构造方法
	 *
	 * @return
	 */
	public DataOKHttp() {
		this.Url = null;
		this.isPost = true;
		mResponseData = new ResponseData();
		mResponseData.setStatus(HttpConfig.Code_NoSend);
	}

	/**
	 * @param url
	 *            请求路径设置
	 * @return
	 */

	public DataOKHttp setUrl(String url) {
		this.Url = url;
		return this;
	}

	public DataOKHttp setCookieSavePath(String cookiePath) {
		this.cookieSavePath = cookiePath;
		return this;
	}

	public DataOKHttp setCookieReadPath(String cookiePath) {
		this.cookieReadPath = cookiePath;
		return this;
	}

	/**
	 * 设置Http请求的相应成功码，默认200，一般不需要修改
	 *
	 * @param sucessCode
	 * @return
	 */
	public DataOKHttp setSucessCode(int sucessCode) {
		this.sucessCode = sucessCode;
		return this;
	}

	/**
	 * 设置是否开启调试
	 *
	 * @param isDebug
	 * @return
	 */
	public DataOKHttp setDebug(boolean isDebug) {
		if (isDebug) {
			this.debugTimeFormat = new SimpleDateFormat("HH:mm:ss");
			this.isDebug = isDebug;
			this.debugTag = "OkHttp";
		}
		else {
			this.debugTimeFormat = null;
			this.isDebug = isDebug;
			this.debugTag = null;
		}
		return this;
	}

	public DataOKHttp setDebug(String debugTag) {
		this.debugTag = debugTag;
		if (null != this.debugTag) {
			this.isDebug = true;
			this.debugTimeFormat = new SimpleDateFormat("HH:mm:ss");
			this.debugTag = "OkHttp";
		}
		else {
			this.isDebug = false;
			this.debugTimeFormat = null;
			this.debugTag = null;
		}
		return this;
	}

	private void logDebug(String message) {
		if (this.isDebug) {
			System.out.println(
					TimeUtils.formatTime(System.currentTimeMillis(), debugTimeFormat) + " " + debugTag + " " + message);
		}
	}

	/**
	 * @param isPost
	 *            是Post请求还是Get请求
	 * @return
	 */
	public DataOKHttp setPost(boolean isPost) {
		this.isPost = isPost;
		return this;
	}

	/**
	 * 设置编码方式
	 *
	 * @param charsetName
	 * @return
	 */
	public DataOKHttp setCharset(String charsetName) {
		this.charsetName = charsetName;
		return this;
	}

	/**
	 * 设置请求RequestBody
	 *
	 * @param mRequestBody
	 *            请求RequestBody
	 * @return
	 */
	public DataOKHttp setRequestBody(RequestBody mRequestBody) {
		this.mRequestBody = mRequestBody;
		return this;
	}

	public DataOKHttp setRequestBody(String postBody) {
		try {
			// MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
			MediaType MEDIA_TYPE_TEXT = MediaType.parse("application/json;charset=utf-8");
			mRequestBody = RequestBody.create(MEDIA_TYPE_TEXT, postBody);
		}
		catch (Exception e) {
			e.printStackTrace();
			mRequestBody = null;
		}

		return this;
	}

	/**
	 * 取消Http请求
	 */
	public void cancleCall() {
		if (null != mCall) {
			mCall.cancel();
		}
	}

	/**
	 * 构建http请求Request
	 *
	 * @return
	 */
	private Request buildRequestByRequestBody() {
		if (isPost) {
			try {
				if (null != mRequestBody) {
					Builder builder = new Request.Builder().url(this.Url);
					if (!StringUtils.isEmpty(cookieReadPath)) {
						String cookieContent = FileUtils.readFile(cookieReadPath);
						builder.addHeader("cookie", cookieContent);
					}
					if (null != headersMap) {
						for (String key : headersMap.keySet()) {
							builder.addHeader(key, headersMap.get(key));
						}
					}
					return builder.post(this.mRequestBody).build();
				}
				else {
					Builder builder = new Request.Builder().url(this.Url);
					if (!StringUtils.isEmpty(cookieReadPath)) {
						String cookieContent = FileUtils.readFile(cookieReadPath);
						builder.addHeader("cookie", cookieContent);
					}
					if (null != headersMap) {
						for (String key : headersMap.keySet()) {
							builder.addHeader(key, headersMap.get(key));
						}
					}
					return builder.build();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}
		else {
			try {
				if (null != mRequestBody) {
					Builder builder = new Request.Builder().url(this.Url);
					if (!StringUtils.isEmpty(cookieReadPath)) {
						String cookieContent = FileUtils.readFile(cookieReadPath);
						builder.addHeader("cookie", cookieContent);
					}
					if (null != headersMap) {
						for (String key : headersMap.keySet()) {
							builder.addHeader(key, headersMap.get(key));
						}
					}

					return builder.put(this.mRequestBody).get().build();

				}
				else {
					Builder builder = new Request.Builder().url(this.Url);
					if (!StringUtils.isEmpty(cookieReadPath)) {
						String cookieContent = FileUtils.readFile(cookieReadPath);
						builder.addHeader("cookie", cookieContent);
					}
					if (null != headersMap) {
						for (String key : headersMap.keySet()) {
							builder.addHeader(key, headersMap.get(key));
						}
					}
					return builder.get().build();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}
	}

	/**
	 * 解析下载结果为回调所需的数据
	 *
	 * @param response
	 */
	private void parseResponse(Response response) {

		if (null == response) {
			createResultData(null, null, HttpConfig.Code_SendError);
			return;
		}
		int responseCode = response.code();
		if (!response.isSuccessful()) {

			createResultData(null, null, responseCode);
		}
		else {
			if (!StringUtils.isEmpty(cookieSavePath)) {

				Headers headers = response.headers();
				List<String> cookies = headers.values("Set-Cookie");
				String session = cookies.get(0);
				FileUtils.writeFile(cookieSavePath, session, false);
			}
			byte[] responseData = null;
			try {
				responseData = response.body().bytes();
			}
			catch (Exception e) {
				e.printStackTrace();
				responseData = null;
			}
			if (null == responseData || responseData.length <= 0) {
				createResultData(null, null, responseCode);
			}
			else {
				parseHttpCallBackSucess(responseData, responseCode);
			}

		}
	}

	/**
	 * 解析网络请求结果为符合回调用的数据
	 *
	 * @param responseString
	 */
	private void parseHttpCallBackSucess(final byte[] responseData, int reponseCode) {

		// 对象解析开始
		Object object = HttpConfig.parseResponseData(responseData, cls, charsetName);
		// 回调开始
		if (null == object) {
			createResultData(null, responseData, reponseCode);
		}
		else {
			createResultData(object, responseData, reponseCode);

		}
		// 回调结束

	}

	/**
	 * 回调网络请求结果
	 *
	 * @param resultObject
	 *            请求结果解析成的Object对象
	 * @param resultString
	 *            请求结果的原始数据
	 * @param status
	 *            请求结果的状态
	 */
	private void createResultData(final Object resultObject, final byte[] resultData, final int status) {

		if (isDebug) {
			if (status != sucessCode) {
				logDebug("请求结果:" + "status = " + status + "; msg = 失败");
			}
			else {
				logDebug("请求结果:" + "status = " + status + "; msg = 成功");
			}
		}

		mCall = null;
		mResponseData.setResultObject(resultObject);
		mResponseData.setResultData(resultData);
		mResponseData.setStatus(status);
	}

	public ResponseData doHttp(Class<?> cls) {
		this.cls = cls;
		if (null == this.Url || this.Url.length() == 0) {
			if (this.isDebug) {
				logDebug("请求路径不正确");
			}
			createResultData(null, null, HttpConfig.Code_NoSend);
			return this.mResponseData;
		}
		if (this.isDebug) {
			logDebug("请求路径:" + this.Url);
		}
		Request mRequest = buildRequestByRequestBody();
		if (null == mRequest) {
			if (isDebug) {
				logDebug("请求参数不正确不正确");
			}
			createResultData(null, null, HttpConfig.Code_NoSend);
			return this.mResponseData;
		}
		mCall = OkHttpConfig.getOkHttpClient().newCall(mRequest);
		if (null == mCall) {
			if (isDebug) {
				logDebug("请求参数不正确不正确");
			}
			createResultData(null, null, HttpConfig.Code_NoSend);
			return this.mResponseData;
		}
		mResponseData.setStatus(HttpConfig.Code_SendError);

		try {
			Response response = mCall.execute();
			parseResponse(response);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (isDebug) {
				logDebug("请求结果:" + "status = " + HttpConfig.Code_SendError + "; msg = 失败");
			}
			createResultData(null, null, HttpConfig.Code_SendError);
		}
		return this.mResponseData;
	}

}
