/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月12日 下午4:35:01
 */
package com.newpay.webauth.dal.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "TBL_UUID_KEYPAIR")
public class UuidKeyPair {
	@Id
	@Column(name = "UUID")
	private String uuid;
	@Id
	@Column(name = "KEY_TYPE")
	private String keyType;
	@Column(name = "PUBLIC_KEY")
	private String publicKey;
	@Column(name = "PRIVATE_KEY")
	private String privateKey;
	@Column(name = "KEY_VERSION")
	private String keyVersion;
	@Column(name = "VERSION")
	private Integer version;
}
