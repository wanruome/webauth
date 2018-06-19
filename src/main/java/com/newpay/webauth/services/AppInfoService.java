/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月19日 上午10:00:08
 */
package com.newpay.webauth.services;

import com.newpay.webauth.dal.request.appinfo.AppInfoAddReqDto;
import com.newpay.webauth.dal.request.appinfo.AppInfoModify;

public interface AppInfoService {
	public Object doAddAppInfo(AppInfoAddReqDto appInfoAddReqDto);

	public Object doModifyAppInfo(AppInfoModify appInfoModify);
}
