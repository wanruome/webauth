/**
 *	@copyright 盛炬支付-2016
 * 	@author wanruome
 * 	@create 2016年4月13日 上午9:41:05
 */
package com.ruomm.base.http;

import java.io.File;

public class ResponseFile {
	/**
	 * 下载的文件
	 */
	private File file;
	/**
	 * 下载文件的结果状态
	 */
	private int status;
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
