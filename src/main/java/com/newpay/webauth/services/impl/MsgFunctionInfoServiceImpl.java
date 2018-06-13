/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午8:52:00
 */
package com.newpay.webauth.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpay.webauth.dal.mapper.MsgFunctionInfoMapper;
import com.newpay.webauth.dal.model.MsgFunctionInfo;
import com.newpay.webauth.services.MsgFunctionInfoService;

@Service
public class MsgFunctionInfoServiceImpl implements MsgFunctionInfoService {
	@Autowired
	MsgFunctionInfoMapper msgFunctionInfoMapper;

	@Override
	public Map<String, MsgFunctionInfo> queryAllEnableMsgFunctionInfos() {
		// TODO Auto-generated method stub
		MsgFunctionInfo queryBean = new MsgFunctionInfo();
		queryBean.setStatus(1);
		List<MsgFunctionInfo> lstResult = msgFunctionInfoMapper.select(queryBean);
		Map<String, MsgFunctionInfo> mapsResult = new HashMap<String, MsgFunctionInfo>();
		for (MsgFunctionInfo tmp : lstResult) {
			mapsResult.put(String.valueOf(tmp.getMsgFunction()), tmp);
		}
		return mapsResult;
	}

}
