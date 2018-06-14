/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午4:44:46
 */
package com.newpay.webauth.services.impl;

import java.security.PublicKey;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.newpay.webauth.config.AppConfig;
import com.newpay.webauth.dal.mapper.UuidKeyPairMapper;
import com.newpay.webauth.dal.model.UuidKeyPair;
import com.newpay.webauth.dal.request.keypair.UuidKeyPairReqDto;
import com.newpay.webauth.dal.response.BaseReturn;
import com.newpay.webauth.services.UuidKeyPairService;
import com.ruomm.base.tools.Base64;
import com.ruomm.base.tools.DesUtil;
import com.ruomm.base.tools.RSAUtils;
import com.ruomm.base.tools.StringUtils;
import com.ruomm.base.tools.TimeUtils;

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
		// 转换默认秘钥
		if (StringUtils.isBlank(uuidKeyPairReqDto.getKeyType())) {
			uuidKeyPairReqDto.setKeyType(AppConfig.UserPwdEncryptDefault);
		}
		String uuid = uuidKeyPairReqDto.getUuid();
		String keyType = null;
		if (uuidKeyPairReqDto.getKeyType().equals(AppConfig.PWD_ENCRYPT_RSA)
				|| uuidKeyPairReqDto.getKeyType().equals(AppConfig.PWD_ENCRYPT_RSAMD5)) {
			keyType = AppConfig.PWD_ENCRYPT_RSA;
		}
		else if (uuidKeyPairReqDto.getKeyType().equals(AppConfig.PWD_ENCRYPT_3DES)
				|| uuidKeyPairReqDto.getKeyType().equals(AppConfig.PWD_ENCRYPT_3DESMD5)) {
			keyType = AppConfig.PWD_ENCRYPT_3DES;
		}
		else {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_PRARM);
		}
		UuidKeyPair queryUuidKeyPair = new UuidKeyPair();
		queryUuidKeyPair.setUuid(uuid);
		queryUuidKeyPair.setKeyType(keyType);
		// 获取UUID设置项目
		UuidKeyPair resultUuidKeyPair = uuidKeyPairMapper.selectByPrimaryKey(queryUuidKeyPair);
		UuidKeyPair returnKeyPair = null;
		if (null == resultUuidKeyPair) {
			UuidKeyPair insertBean = new UuidKeyPair();
			insertBean.setUuid(uuid);
			String[] keyPair = createKeyPairString(keyType);
			insertBean.setKeyType(keyType);
			insertBean.setPublicKey(keyPair[0]);
			insertBean.setPrivateKey(keyPair[1]);
			insertBean.setKeyVersion(AppConfig.SDF_DB_VERSION.format(new Date()));
			insertBean.setVersion(1);
			int dbResult = uuidKeyPairMapper.insert(insertBean);
			returnKeyPair = dbResult > 0 ? insertBean : null;
		}
		else {
			String keyVersion = resultUuidKeyPair.getKeyVersion();
			boolean versionCacheFlag = TimeUtils.isCacheOk(keyVersion, AppConfig.SDF_DB_VERSION,
					AppConfig.KeyPairPublicKeyGetSkipTime);
			// long timeSkip = -1000l;
			// try {
			// timeSkip = Math.abs(new Date().getTime() -
			// AppConfig.SDF_DB_VERSION.parse(keyVersion).getTime());
			// }
			// catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// timeSkip = -1000l;
			// }
			// if (timeSkip <= AppConfig.KeyPairPublicKeyGetSkipTime && timeSkip >= 0) {
			// returnKeyPair = resultUuidKeyPair;
			// }
			if (versionCacheFlag) {
				returnKeyPair = resultUuidKeyPair;
			}
			else {
				// UuidKeyPair updateBean = new UuidKeyPair();
				// updateBean.setUuid(uuid);
				// String[] keyPair = createKeyPairString(keyType);
				// updateBean.setKeyType(keyType);
				// updateBean.setPublicKey(keyPair[0]);
				// updateBean.setPrivateKey(keyPair[1]);
				// updateBean.setKeyVersion(AppConfig.SDF_DB_VERSION.format(new Date()));
				// updateBean.setVersion(AppConfig.getUpdateVersion(resultUuidKeyPair.getVersion()));
				// // 创建Example
				// Example example = new Example(UuidKeyPair.class);
				// // 创建Criteria
				// Example.Criteria criteria = example.createCriteria();
				// // 添加条件
				// criteria.andEqualTo("uuid", resultUuidKeyPair.getUuid());
				// criteria.andEqualTo("keyType", resultUuidKeyPair.getKeyType());
				// criteria.andEqualTo("version", resultUuidKeyPair.getVersion());

				UuidKeyPair updateBean = new UuidKeyPair();
				updateBean.setUuid(uuid);
				String[] keyPair = createKeyPairString(keyType);
				updateBean.setKeyType(keyType);
				updateBean.setPublicKey(keyPair[0]);
				updateBean.setPrivateKey(keyPair[1]);
				updateBean.setKeyVersion(AppConfig.SDF_DB_VERSION.format(new Date()));
				updateBean.setVersion(resultUuidKeyPair.getVersion());
				int dbResult = uuidKeyPairMapper.updateByPrimaryKey(updateBean);
				returnKeyPair = dbResult > 0 ? updateBean : null;
			}
		}
		if (null == returnKeyPair) {
			return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_DB);
		}
		else {
			// 加入传输安全选项
			String tmpString = null;
			if (!StringUtils.isBlank(uuidKeyPairReqDto.getRasPublicKey())
					&& keyType.equals(AppConfig.PWD_ENCRYPT_3DES)) {
				PublicKey publicKey = RSAUtils.loadPublicKey(uuidKeyPairReqDto.getRasPublicKey());
				tmpString = Base64.encode(RSAUtils.encryptData(returnKeyPair.getPublicKey().getBytes(), publicKey));
			}
			else {
				tmpString = returnKeyPair.getPublicKey();
			}
			if (StringUtils.isBlank(tmpString)) {
				return BaseReturn.toFAIL(BaseReturn.ERROR_CODE_DB);
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("uuid", returnKeyPair.getUuid());
			jsonObject.put("publicKey", tmpString);
			jsonObject.put("keyVersion", returnKeyPair.getKeyVersion());
			jsonObject.put("version", returnKeyPair.getVersion());
			return BaseReturn.toSUCESS(jsonObject);
		}

	}

	private String[] createKeyPairString(String keyType) {
		if (keyType.equals(AppConfig.PWD_ENCRYPT_RSA) || keyType.equals(AppConfig.PWD_ENCRYPT_RSAMD5)) {
			return RSAUtils.generateRSAKeyPairToArrty(1024);
		}
		else if (keyType.equals(AppConfig.PWD_ENCRYPT_3DES) || keyType.equals(AppConfig.PWD_ENCRYPT_3DESMD5)) {
			String data = DesUtil.initKeyStr();
			return new String[] { data, data };
		}
		else {
			return null;
		}
	}

}
