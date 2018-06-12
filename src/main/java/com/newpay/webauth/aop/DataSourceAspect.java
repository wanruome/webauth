/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 上午9:47:51
 */
package com.newpay.webauth.aop;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.ruomm.base.datasource.DataSource;
import com.ruomm.base.datasource.DataSourceTypeManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 切换数据源(不同方法调用不同数据源)
 */
// @Configuration
// @EnableAspectJAutoProxy(proxyTargetClass = true)
//// 扫描注入类
// @ComponentScan(basePackages = "com.ruomm.*")
// @Component
// @Order(1) //
// 请注意：这里order一定要小于tx:annotation-driven的order，即先执行DataSourceAspect切面，再执行事务切面，才能获取到最终的数据源
// @Aspect
@Slf4j
public class DataSourceAspect {

	@Pointcut("execution(* com.ruomm.webstudy.services..*.*(..))")
	public void dbSourceChangePoint() {
		// 该方法就是一个标识方法，为pointcut提供一个依附的地方
	}

	/**
	 * 配置前置通知,使用在方法aspect()上注册的切入点
	 */
	@Before("dbSourceChangePoint()")
	public void before(JoinPoint point) {
		log.info("dbSourceChangePoint Before");
		setDataSource(point);
	}

	@After("dbSourceChangePoint()")
	public void after(JoinPoint point) {
		log.info("dbSourceChangePoint After");
		// 使用完记得清空
		DataSourceTypeManager.setDataSourceKey(null);
	}

	/**
	 * 设置切入点的DataSource
	 *
	 * @param point
	 */
	public void setDataSource(JoinPoint point) {
		Class<?> target = point.getTarget().getClass();
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();
		DataSource dataSource = null;
		// 从类初始化
		dataSource = this.getDataSource(target, method);
		// 从接口初始化
		if (dataSource == null) {
			for (Class<?> clazz : target.getInterfaces()) {
				dataSource = getDataSource(clazz, method);
				if (dataSource != null) {
					break;// 从某个接口中一旦发现注解，不再循环
				}
			}
		}

		if (dataSource != null && !isEmptyString(dataSource.value())) {
			DataSourceTypeManager.setDataSourceKey(dataSource.value());
		}
	}

	/**
	 * 获取方法或类的注解对象DataSource
	 *
	 * @param target
	 *            类class
	 * @param method
	 *            方法
	 * @return DataSource
	 */
	public DataSource getDataSource(Class<?> target, Method method) {
		try {
			// 1.优先方法注解
			Class<?>[] types = method.getParameterTypes();
			Method m = target.getMethod(method.getName(), types);
			if (m != null && m.isAnnotationPresent(DataSource.class)) {
				return m.getAnnotation(DataSource.class);
			}
			// 2.其次类注解
			if (target.isAnnotationPresent(DataSource.class)) {
				return target.getAnnotation(DataSource.class);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			log.error(MessageFormat.format("通过注解切换数据源时发生异常[class={0},method={1}]：", target.getName(), method.getName()),
					e);
		}
		return null;
	}

	private boolean isEmptyString(String str) {
		return null == str || str.trim().length() == 0 ? true : false;
	}

}
