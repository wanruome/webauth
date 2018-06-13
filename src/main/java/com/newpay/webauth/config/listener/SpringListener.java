/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月13日 下午9:23:51
 */
package com.newpay.webauth.config.listener;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.newpay.webauth.dal.model.MsgFunctionInfo;
import com.newpay.webauth.services.MsgFunctionInfoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringListener implements ApplicationListener<ContextRefreshedEvent> {
	@Autowired
	MsgFunctionInfoService msgFuntionInfoService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			// 只在初始化“根上下文”的时候执行
			Map<String, MsgFunctionInfo> mapResult = msgFuntionInfoService.queryAllEnableMsgFunctionInfos();
			System.out.println(mapResult);
			// if (null == app.getParent()
			// && ("Root WebApplicationContext".equals(app.getDisplayName())
			// || app.getDisplayName().contains("AnnotationConfigEmbeddedWebApplicationContext"))
			// && "/xweb".equals(app.getApplicationName())) { // 当存在父子容器时，此判断很有用
			// log.info("*************:" + event.getSource());
			// log.info("*************:" + app.getDisplayName());
			// log.info("*************:" + app.getApplicationName());
			// log.info("*************:" + app.getBeanDefinitionCount());
			// log.info("*************:" + app.getEnvironment());
			// log.info("*************:" + app.getParent());
			// log.info("*************:" + app.getParentBeanFactory());
			// log.info("*************:" + app.getId());
			// log.info("*************:" + app.toString());
			// log.info("*************:" + app);
			//
			// }
		}
		catch (Exception e) {
			log.error("((XmlWebApplicationContext) event.getSource()).getDisplayName() 执行失败，请检查Spring版本是否支持");
		}

	}

}
