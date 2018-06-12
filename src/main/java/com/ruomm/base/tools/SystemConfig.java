/**
 *	@copyright wanruome-2017
 * 	@author wanruome
 * 	@create 2017年3月27日 上午11:44:23
 */
package com.ruomm.base.tools;

public class SystemConfig {
	private static String wwwWoot = null;
	private static String classesRoot = null;

	// private SystemConfig() {
	// super();
	// }
	//
	// private static SystemConfig instance = new SystemConfig();
	//
	// public static SystemConfig getInstance() {
	// return instance;
	// }

	public static String getWwwroot() {
		if (null == wwwWoot) {
			try {
				String dir = SystemConfig.class.getResource("/").getPath();
				int size = dir.length();
				if (dir.toLowerCase().endsWith("web-inf/classes/")
						|| dir.toLowerCase().endsWith("web-inf\\classes\\")) {
					wwwWoot = dir.substring(0, size - 16).replace("%20", " ");
				}
				else {
					String clsName = SystemConfig.class.getName();
					int length = SystemConfig.class.getSimpleName().length();
					String clsPath = "web-inf/classes/"
							+ clsName.substring(0, clsName.length() - length).replace(".", "/");
					int index = dir.toLowerCase().indexOf(clsPath);
					if (index < 0) {
						index = dir.toLowerCase().indexOf(clsPath.replace("/", "\\"));
					}
					if (index > 0) {
						wwwWoot = dir.substring(0, index).replace("%20", " ");
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return wwwWoot;
	}

	public static String getClassesRoot() {
		// if(null==classesRoot&&null!=wwwWoot)
		// {
		// classesRoot=wwwWoot+"WEB-INF/classes/";
		// }
		if (null == classesRoot) {
			try {
				String dir = SystemConfig.class.getResource("/").getPath();
				int size = dir.length();
				String clsName = SystemConfig.class.getName();
				int length = SystemConfig.class.getSimpleName().length();
				String clsPath = clsName.substring(0, clsName.length() - length).replace(".", "/");
				if (dir.endsWith(clsPath) || dir.endsWith(clsPath.replace("/", "\\"))) {
					classesRoot = dir.substring(0, size - clsPath.length()).replace("%20", " ");
				}
				else {
					classesRoot = dir.replace("%20", " ");
				}

			}
			catch (Exception e) {
				e.printStackTrace();
				classesRoot = null;
			}
		}
		return classesRoot;
	}

	public static String getRealUri(String uri) {
		if (StringUtils.isEmpty(uri)) {
			return null;
		}
		String realUri = null;
		int indexW = uri.indexOf("?");
		if (indexW > 0) {
			// 严格匹配
			// realUri = uri.substring(0, indexW);
			// 通用匹配
			realUri = uri.substring(0, indexW).replaceAll("[/]+", "/").replaceFirst(":/", "://");
		}
		else {
			// 严格匹配
			// realUri = uri;
			// 通用匹配
			realUri = uri.replaceAll("[/]+", "/").replaceFirst(":/", "://");
		}
		return realUri;
	}

}
