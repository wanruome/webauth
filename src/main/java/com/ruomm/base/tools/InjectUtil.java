package com.ruomm.base.tools;

import com.ruomm.base.annotation.InjectEntity;

public class InjectUtil {
	/**
	 * 获取Bean模型的Key
	 *
	 * @param cls
	 *            数据类型
	 * @return
	 */
	public static String getBeanKey(Class<?> cls) {
		InjectEntity injectEntity = cls.getAnnotation(InjectEntity.class);

		if (null == injectEntity || StringUtils.isEmpty(injectEntity.beanKey())) {
			return cls.getSimpleName();
		}
		else {
			return injectEntity.beanKey();
		}
	}

	/**
	 * 获取Bean模型的List的Key
	 *
	 * @param cls
	 *            数据类型
	 * @return
	 */
	public static String getBeanListKey(Class<?> cls) {
		InjectEntity injectEntity = cls.getAnnotation(InjectEntity.class);

		if (null == injectEntity || StringUtils.isEmpty(injectEntity.beanKey())) {
			return cls.getSimpleName() + "List";
		}
		else {
			return injectEntity.beanKey() + "List";
		}
	}

	/**
	 * 获取需要存储读取Bean模型Value值的真正Key
	 *
	 * @param key
	 *            自定义的key；
	 * @param cls
	 *            数据对象模型；
	 * @return 真正的Key值；
	 */
	public static String getRealBeanKey(String key, Class<?> cls) {
		if (!StringUtils.isEmpty(key)) {
			return key;
		}
		else {
			return getBeanKey(cls);
		}
	}

	/**
	 * 判断给出的字符串是否和Bean类型的Key值相同
	 *
	 * @param tag
	 * @param cls
	 * @return
	 */
	public static boolean isBeanKeyEqual(String tag, Class<?> cls) {
		if (StringUtils.isEmpty(tag) || null == cls) {
			return false;
		}
		else {
			return tag.equals(getBeanKey(cls));
		}
	}

}
