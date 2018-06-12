/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月11日 下午10:56:30
 */
package com.ruomm.base.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RuommParamField {
	public enum ParamType {
		BODY, PARAM, HEADER
	}

	public ParamType paramType() default ParamType.BODY;

	public String paramName() default "";

	public boolean isForceArray() default false;
}
