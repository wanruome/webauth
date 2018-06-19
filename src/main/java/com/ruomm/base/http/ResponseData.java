/**
 *	@copyright 盛炬支付-2016
 * 	@author wanruome
 * 	@create 2016年4月13日 上午9:39:58
 */
package com.ruomm.base.http;

import java.util.Arrays;

public class ResponseData {
	/**
	 * 请求结果的自动解析好的对象
	 */
	private Object resultObject;
	/**
	 * 请求结果的原始数据
	 */
	private byte[] resultData;
	/**
	 *
	 */
	private int status;

	public Object getResultObject() {
		return resultObject;
	}

	public void setResultObject(Object resultObject) {
		this.resultObject = resultObject;
	}

	public byte[] getResultData() {
		return resultData;
	}

	public void setResultData(byte[] resultData) {
		this.resultData = resultData;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ResponseData [resultObject=" + resultObject + ", resultData=" + Arrays.toString(resultData)
		+ ", status=" + status + "]";
	}

}
