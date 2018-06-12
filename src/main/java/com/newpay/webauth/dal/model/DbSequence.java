/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 下午3:00:54
 */
package com.newpay.webauth.dal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "tbl_util_seq")
public class DbSequence {
	@Id
	@Column(name = "SEQ_NAME")
	private String seqName;
	@Column(name = "SEQ_VALUE")
	private Integer seqValue;
}
