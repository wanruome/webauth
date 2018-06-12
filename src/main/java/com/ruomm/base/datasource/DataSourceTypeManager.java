/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 上午8:47:42
 */
package com.ruomm.base.datasource;

public class DataSourceTypeManager {
	private static final ThreadLocal<String> dataSourceKey = new ThreadLocal<String>();

	public static void setDataSourceKey(String dataSource) {
		dataSourceKey.set(dataSource);
	}

	public static String get() {
		return dataSourceKey.get();
	}

	public static void set(String dataSourceType) {
		dataSourceKey.set(dataSourceType);
	}

	public static void reset() {
		dataSourceKey.set(null);
	}

	public static void cleanDataSource() {
		dataSourceKey.remove();
	}
}
