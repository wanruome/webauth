/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午3:04:24
 */
package com.newpay.webauth.dal.mapper;

import org.apache.ibatis.annotations.Param;

import com.newpay.webauth.dal.model.DbSequence;

import tk.mybatis.mapper.common.Mapper;

public interface DbSequenceMapper extends Mapper<DbSequence> {
	public Long getLoginUserNewPK();

	public Long getSeqNextval(@Param("seqName") String seqName);

}
