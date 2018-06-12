package com.ruomm.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface InjectEntity {
	/**
	 * 对象模型的Key值，对象化默认传参、存储的Key值
	 *
	 * @return
	 */
	public String beanKey();

}
