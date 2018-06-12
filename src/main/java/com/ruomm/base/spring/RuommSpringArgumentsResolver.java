/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月11日 下午7:38:34
 */
package com.ruomm.base.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuommSpringArgumentsResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		log.info("测试132443真假" + parameter.hasParameterAnnotation(RuommParam.class));

		return parameter.hasParameterAnnotation(RuommParam.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		// TODO Auto-generated method stub
		Class<?> paramType = parameter.getParameterType();
		String rootHeaders = "requestHeaders";
		String rootParams = null;
		String rootBodys = null;
		Map<String, String> headerMaps = null;
		Map<String, String> paramMaps = null;
		Map<String, String> bodyMaps = null;
		List<String> forceArrayFields = null;
		boolean isParseBody = true;
		boolean isParseParam = true;
		boolean isParseHeader = false;
		RuommParamClass ruommParamClass = paramType.getAnnotation(RuommParamClass.class);
		if (null != ruommParamClass) {
			rootHeaders = ruommParamClass.rootHeaders();
			rootParams = ruommParamClass.rootParams();
			rootBodys = ruommParamClass.rootBody();
			headerMaps = parseToMap(ruommParamClass.renameHeaders());
			paramMaps = parseToMap(ruommParamClass.renameParams());
			bodyMaps = parseToMap(ruommParamClass.renameBodys());
			forceArrayFields = parseToArray(ruommParamClass.forceArrayFields());
			isParseBody = ruommParamClass.isParseBody();
			isParseParam = ruommParamClass.isParseParam();
			isParseHeader = ruommParamClass.isParseHeader();
		}

		Map<String, Object> mapHeadersResult = readHeaders(webRequest, headerMaps, forceArrayFields, isParseHeader);
		Map<String, Object> mapParamsResult = readParams(webRequest, paramMaps, forceArrayFields, isParseParam);
		JSONObject jsonBodyResult = readRequestBodyToJson(webRequest, bodyMaps, isParseBody);
		System.out.println(parameter.getGenericParameterType().getClass().getName());
		System.out.println(parameter.getParameterType().getName());

		JSONObject resultJson = new JSONObject();
		if (null != jsonBodyResult) {
			if (StringUtil.isEmpty(rootBodys)) {
				resultJson.putAll(jsonBodyResult);
				// for (String key : jsonBodyResult.keySet()) {
				//
				// resultJson.put(key, jsonBodyResult.get(key));
				// }
			}
			else {
				resultJson.put(rootBodys, jsonBodyResult);
			}
		}
		if (null != mapParamsResult && mapParamsResult.size() > 0) {
			if (StringUtil.isEmpty(rootParams)) {
				for (String key : mapParamsResult.keySet()) {
					if (!resultJson.containsKey(key)) {
						resultJson.put(key, mapParamsResult.get(key));
					}
				}
			}
			else {
				resultJson.put(rootParams, mapParamsResult);
			}
		}
		if (null != mapHeadersResult && mapHeadersResult.size() > 0) {
			if (StringUtil.isEmpty(rootHeaders)) {
				for (String key : mapHeadersResult.keySet()) {
					if (!resultJson.containsKey(key)) {
						resultJson.put(key, mapHeadersResult.get(key));
					}
				}
			}
			else {
				resultJson.put(rootHeaders, mapHeadersResult);
			}
		}
		System.out.println(resultJson.toJSONString());
		Object t = null;
		try {

			if (paramType.equals(String.class)) {
				t = resultJson.toJSONString();
			}

			else if (Map.class.isAssignableFrom(paramType)) {
				Map<String, Object> mapResult = new HashMap<String, Object>();
				for (String key : resultJson.keySet()) {
					mapResult.put(key, resultJson.get(key));
				}
				t = mapResult;
			}
			else {
				try {
					t = resultJson.toJavaObject(parameter.getParameterType());
				}
				catch (Exception e) {
					e.printStackTrace();
					t = null;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (binderFactory != null) {
			WebDataBinder binder = binderFactory.createBinder(webRequest, t, parameter.getParameterName());

			if (binder.getTarget() != null) {
				// 进行参数绑定，此方法实现不再赘述，可到上节查看
				this.bindRequestParameters(binder, resultJson);
				// 如果使用了validation校验, 则进行相应校验
				if (parameter.hasParameterAnnotation(Validated.class)
						|| parameter.hasParameterAnnotation(Valid.class)) {
					// 如果有校验报错，会将结果放在binder.bindingResult属性中
					binder.validate();
				}

				// 如果参数中不包含BindingResult参数，直接抛出异常
				if (binder.getBindingResult().hasErrors() && this.isBindExceptionRequired(binder, parameter)) {
					throw new BindException(binder.getBindingResult());
				}
			}
			// 关键,使Controller中接下来的BindingResult参数可以接收异常
			Map<String, Object> bindingResultModel = binder.getBindingResult().getModel();
			mavContainer.removeAttributes(bindingResultModel);
			mavContainer.addAllAttributes(bindingResultModel);
		}
		return t;

	}

	protected Map<String, String> parseToMap(String str) {
		if (null == str || str.length() <= 0) {
			return null;
		}
		try {
			Map<String, String> maps = new HashMap<String, String>();
			String[] values = str.split(",");
			for (String tmp : values) {
				String[] keyValue = tmp.split("=");
				maps.put(keyValue[0], keyValue[1]);
			}
			return maps;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected List<String> parseToArray(String str) {
		if (null == str || str.length() <= 0) {
			return null;
		}
		try {
			String[] strs = str.split(",");
			if (null == strs || strs.length <= 0) {
				return null;
			}
			List<String> strList = new ArrayList<String>();
			for (String tmp : strs) {
				strList.add(tmp);
			}
			return strList;

		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void bindRequestParameters(WebDataBinder binder, JSONObject jsonObject) {
		// 将key-value封装为map，传给bind方法进行参数值绑定
		Map<String, Object> mapResult = new HashMap<String, Object>();
		for (String key : jsonObject.keySet()) {
			mapResult.put(key, jsonObject.get(key));
		}
		PropertyValues propertyValues = new MutablePropertyValues(mapResult);
		// 将K-V绑定到binder.target属性上
		binder.bind(propertyValues);
	}

	/**
	 * 检查参数中是否包含BindingResult参数
	 */
	protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter methodParam) {
		int i = methodParam.getParameterIndex();
		Class[] paramTypes = methodParam.getMethod().getParameterTypes();
		boolean hasBindingResult = paramTypes.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]);
		return !hasBindingResult;
	}

	private Map<String, Object> readHeaders(NativeWebRequest webRequest, Map<String, String> renameMaps,
			List<String> forceArrayFields, boolean isParse) {
		if (!isParse) {
			return null;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Iterator<String> iteratorHeaders = webRequest.getHeaderNames();
			while (null != iteratorHeaders && iteratorHeaders.hasNext()) {
				String key = iteratorHeaders.next();
				String values[] = webRequest.getHeaderValues(key);
				String realKey = null != renameMaps && renameMaps.containsKey(key) ? renameMaps.get(key) : key;
				boolean isForceArray = null != forceArrayFields && forceArrayFields.contains(realKey) ? true : false;
				if (isForceArray) {
					resultMap.put(realKey, values);
				}
				else {

					if (null == values || values.length == 0) {
						resultMap.put(realKey, null);
					}
					else if (values.length == 1) {
						resultMap.put(realKey, values[0]);
					}
					else {
						resultMap.put(realKey, values);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	private Map<String, Object> readParams(NativeWebRequest webRequest, Map<String, String> renameMaps,
			List<String> forceArrayFields, boolean isParse) {
		if (!isParse) {
			return null;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Iterator<String> iteratorParams = webRequest.getParameterNames();
			while (null != iteratorParams && iteratorParams.hasNext()) {
				String key = iteratorParams.next();
				String values[] = webRequest.getParameterValues(key);
				String realKey = null != renameMaps && renameMaps.containsKey(key) ? renameMaps.get(key) : key;
				boolean isForceArray = null != forceArrayFields && forceArrayFields.contains(realKey) ? true : false;
				if (isForceArray) {
					resultMap.put(realKey, values);
				}
				else {

					if (null == values || values.length == 0) {
						resultMap.put(realKey, null);
					}
					else if (values.length == 1) {
						resultMap.put(realKey, values[0]);
					}
					else {
						resultMap.put(realKey, values);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	private JSONObject readRequestBodyToJson(NativeWebRequest webRequest, Map<String, String> renameMaps,
			boolean isParse) {
		if (!isParse) {
			return null;
		}
		JSONObject jsonObject = null;
		String bodyStr = readRequestBody(webRequest);
		try {
			if (null != bodyStr) {
				jsonObject = JSON.parseObject(bodyStr);
				if (null != renameMaps && renameMaps.size() > 0 && null != jsonObject) {
					for (String key : renameMaps.keySet()) {
						if (jsonObject.containsKey(key)) {
							Object tmp = jsonObject.get(key);
							jsonObject.remove(key);
							jsonObject.put(renameMaps.get(key), tmp);
						}
					}
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			jsonObject = null;
		}
		return jsonObject;

	}

	private String readRequestBody(NativeWebRequest webRequest) {
		String data = null;
		try {
			HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
			data = IOUtils.toString(httpServletRequest.getInputStream(), "UTF-8");
		}
		catch (Exception e) {
			e.printStackTrace();
			data = null;
		}
		return data;

	}

}
