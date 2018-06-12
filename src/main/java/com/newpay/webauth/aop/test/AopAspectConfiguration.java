/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月10日 上午10:48:01
 */
package com.newpay.webauth.aop.test;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

//用于定义配置类，可替换xml配置文件
//@Configuration
// 开启AspectJ 自动代理模式,如果不填proxyTargetClass=true，默认为false，
//@EnableAspectJAutoProxy(proxyTargetClass = true)
// 扫描注入类
//@ComponentScan(basePackages = "com.ruomm.webstudy.services.*")
//@Component
//@Order(3)
//@Aspect
public class AopAspectConfiguration {
	// 声明切入点
	// 第一个*表示 方法 返回值（例如public int）
	// 第二个* 表示方法的全限定名（即包名+类名）
	// perform表示目标方法参数括号两个.表示任意类型参数
	// 方法表达式以“*”号开始，表明了我们不关心方法返回值的类型。然后，我们指定了全限定类名和方法名。对于方法参数列表，
	// 我们使用两个点号（..）表明切点要选择任意的perform()方法，无论该方法的入参是什么
	// execution表示执行的时候触发
	@Pointcut("execution(* com.ruomm.webstudy.services..*.*(..))")
	public void point() {
		// 该方法就是一个标识方法，为pointcut提供一个依附的地方
	}

	@Before("point()")
	public void before() {
		System.out.println("Before");
	}

	@After("point()")
	public void after() {
		System.out.println("After");
	}
}
