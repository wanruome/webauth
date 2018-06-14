/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午11:30:41
 */
package com.newpay.webauth.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.newpay.webauth.dal.mapper.DbSequenceMapper;
import com.newpay.webauth.dal.model.DbSequence;
import com.newpay.webauth.services.DbSeqService;
import com.ruomm.base.tools.StringUtils;

import tk.mybatis.mapper.entity.Example;

@Service
public class DbSeqServiceImpl implements DbSeqService {
	@Autowired
	DbSequenceMapper dbSequenceMapper;
	@Value("${MyBatis.DBTYPE}")
	private String dbType;

	@Override
	public String getLoginUserNewPK() {

		if (null == dbType || !dbType.toLowerCase().equals("oracle")) {
			System.out.println("数据库类型1：" + dbType);
			return getSeqByNameLong("tbl_login_user_new_pk", 100000) + "";
		}
		else {
			System.out.println("数据库类型2：" + dbType);
			return dbSequenceMapper.getLoginUserNewPK() + "";
		}
	}

	public Long getSeqByNameLong(String name, int insertValue) {
		// TODO Auto-generated method stub
		int resultValue = getSeqByName(name, insertValue);
		return resultValue > 0 ? (long) resultValue : -9999l;
	}

	public int getSeqByName(String name, int insertValue) {
		// TODO Auto-generated method stub
		String seqName = StringUtils.isEmpty(name) ? "default_seq_key" : name;
		DbSequence querySeq = new DbSequence();
		querySeq.setSeqName(seqName);
		DbSequence resultDbSeq = dbSequenceMapper.selectByPrimaryKey(querySeq);
		int dbResult = 0;
		DbSequence updateDbSeq = new DbSequence();
		if (null == resultDbSeq) {
			updateDbSeq.setSeqName(seqName);
			if (insertValue > 0) {
				updateDbSeq.setSeqValue(insertValue);
			}
			else {
				updateDbSeq.setSeqValue(1);
			}
			dbResult = dbSequenceMapper.insertSelective(updateDbSeq);
		}
		else {

			updateDbSeq.setSeqName(resultDbSeq.getSeqName());
			updateDbSeq.setSeqValue(resultDbSeq.getSeqValue() + 1);
			Example example = new Example(DbSequence.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo(resultDbSeq);
			dbResult = dbSequenceMapper.updateByExampleSelective(updateDbSeq, example);
		}
		return dbResult > 0 ? updateDbSeq.getSeqValue() : -9999;
	}

}
