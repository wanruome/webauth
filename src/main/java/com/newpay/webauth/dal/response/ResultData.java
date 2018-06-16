/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月16日 上午11:42:28
 */
package com.newpay.webauth.dal.response;

public class ResultData<T> {
	private String code;
	private String msg;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	private T data = null;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
