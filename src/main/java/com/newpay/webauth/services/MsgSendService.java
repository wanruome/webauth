/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午8:24:04
 */
package com.newpay.webauth.services;

import com.newpay.webauth.dal.request.functionmsg.MsgSendReqDto;
import com.newpay.webauth.dal.request.functionmsg.MsgTokenGetReqDto;

public interface MsgSendService {
	public Object doSendMsg(MsgSendReqDto msgSendReqDto);

	public Object doGetMsgToken(MsgTokenGetReqDto msgTokenGetReqDto);
}
