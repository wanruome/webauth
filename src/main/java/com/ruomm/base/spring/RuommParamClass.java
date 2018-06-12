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

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RuommParamClass {
	public boolean isParseBody() default true;

	public boolean isParseParam() default true;

	public boolean isParseHeader() default false;

	public String rootHeaders() default "requestHeaders";

	public String rootParams() default "";

	public String rootBody() default "";

	public String renameHeaders() default "";

	public String renameParams() default "";

	public String renameBodys() default "";

	public String forceArrayFields() default "";

}
