/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午4:44:46
 */
package com.newpay.webauth.services.impl;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.abel533.entity.Example;
import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.mapper.UuidKeyPairMapper;
import com.newpay.webauth.dal.model.UuidKeyPair;
import com.newpay.webauth.dal.request.UuidKeyPairReqDto;
import com.newpay.webauth.dal.response.BaseReturn;
import com.newpay.webauth.services.UuidKeyPairService;
import com.ruomm.base.tools.RSAUtils;
import com.ruomm.base.tools.StringUtils;

@Service
public class UuidKeyPairServiceImpl implements UuidKeyPairService {
	@Autowired
	UuidKeyPairMapper uuidKeyPairMapper;

	@Override
	public Object getPublicKeyByUuid(UuidKeyPairReqDto uuidKeyPairReqDto) {
		if (StringUtils.isBlank(uuidKeyPairReqDto.getUuid())) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		// if (StringUtils.getLength(uuidKeyPairReqDto.getUuid()) < 32) {
		// return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		// }
		UuidKeyPair queryUuidKeyPair = new UuidKeyPair();
		queryUuidKeyPair.setUuid(uuidKeyPairReqDto.getUuid());
		// 获取UUID设置项目
		UuidKeyPair resultUuidKeyPair = uuidKeyPairMapper.selectByPrimaryKey(queryUuidKeyPair);
		UuidKeyPair returnKeyPair = null;
		if (null == resultUuidKeyPair) {

			UuidKeyPair insertBean = new UuidKeyPair();
			insertBean.setUuid(uuidKeyPairReqDto.getUuid());
			String[] keyPair = RSAUtils.generateRSAKeyPairToArrty();
			insertBean.setPublicKey(keyPair[0]);
			insertBean.setPrivateKey(keyPair[1]);
			insertBean.setUuidVersion(AppConfig.SDF_DB_VERSION.format(new Date()));
			insertBean.setVersion(AppConfig.getUpdateVersion(0));
			int dbResult = uuidKeyPairMapper.insert(insertBean);
			returnKeyPair = dbResult > 0 ? insertBean : null;
		}
		else {
			String uuidVersion = resultUuidKeyPair.getUuidVersion();
			long timeSkip = -1000l;
			try {
				timeSkip = Math.abs(new Date().getTime() - AppConfig.SDF_DB_VERSION.parse(uuidVersion).getTime());
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				timeSkip = -1000l;
			}
			if (timeSkip <= AppConfig.getKeyPairPublicKeyGetSkipTime() && timeSkip >= 0) {
				returnKeyPair = resultUuidKeyPair;
			}
			else {
				UuidKeyPair insertBean = new UuidKeyPair();
				insertBean.setUuid(uuidKeyPairReqDto.getUuid());
				String[] keyPair = RSAUtils.generateRSAKeyPairToArrty();
				insertBean.setPublicKey(keyPair[0]);
				insertBean.setPrivateKey(keyPair[1]);
				insertBean.setUuidVersion(AppConfig.SDF_DB_VERSION.format(new Date()));
				insertBean.setVersion(AppConfig.getUpdateVersion(resultUuidKeyPair.getVersion()));
				// 创建Example
				Example example = new Example(UuidKeyPair.class);
				// 创建Criteria
				Example.Criteria criteria = example.createCriteria();
				// 添加条件
				criteria.andEqualTo("uuid", resultUuidKeyPair.getUuid());
				criteria.andEqualTo("version", resultUuidKeyPair.getVersion());

				int dbResult = uuidKeyPairMapper.updateByExample(insertBean, example);
				returnKeyPair = dbResult > 0 ? insertBean : null;

			}
		}
		if (null == returnKeyPair) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_DB);
		}
		else {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("uuid", returnKeyPair.getUuid());
			jsonObject.put("publicKey", returnKeyPair.getPublicKey());
			jsonObject.put("uuidVersion", returnKeyPair.getUuidVersion());
			jsonObject.put("version", returnKeyPair.getVersion());
			return BaseReturn.toSUCESS(jsonObject);
		}

	}

}
