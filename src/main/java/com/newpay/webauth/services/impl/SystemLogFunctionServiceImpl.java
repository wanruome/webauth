/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月20日 下午5:15:52
 */
package com.newpay.webauth.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpay.webauth.dal.mapper.SystemLogFunctionMapper;
import com.newpay.webauth.dal.model.SystemLogFunction;
import com.newpay.webauth.services.SystemLogFunctionService;

@Service
public class SystemLogFunctionServiceImpl implements SystemLogFunctionService {
	@Autowired
	SystemLogFunctionMapper SystemLogFunction;

	@Override
	public Map<String, SystemLogFunction> getAllEnableSystemLogFunction() {
		// TODO Auto-generated method stub
		SystemLogFunction queryBean = new SystemLogFunction();
		queryBean.setStatus(1);
		List<SystemLogFunction> lstResult = SystemLogFunction.select(queryBean);
		Map<String, SystemLogFunction> mapsResult = new HashMap<String, SystemLogFunction>();
		for (SystemLogFunction tmp : lstResult) {
			mapsResult.put(tmp.getFunctionId(), tmp);
		}
		return mapsResult;
	}

}
