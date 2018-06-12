/**
 *	@copyright wanruome-2017
 * 	@author wanruome
 * 	@create 2017年3月29日 上午9:13:46
 */
package com.ruomm.base.tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LockFactory {
	/** 单实例 */
	private static LockFactory instance = new LockFactory();
	protected Map<String, Object> lockMap = new ConcurrentHashMap<String, Object>();

	/**
	 * 私有构造函数
	 */
	private LockFactory() {
	}

	/**
	 * 取得工厂单实例对象
	 *
	 * @return [参数说明]
	 * @return LockFactory [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static LockFactory getInstance() {
		return instance;
	}

	public Object getStringLock(String key) {
		Object lock;
		lock = lockMap.get(key);
		if (lock == null) {
			lock = new Object();
			lockMap.put(key, lock);
		}
		return lock;

	}

	public void removeStringLock(String key) {
		if (null != key && key.length() > 0 && lockMap.containsKey(key)) {
			lockMap.remove(key);
		}
	}
}
