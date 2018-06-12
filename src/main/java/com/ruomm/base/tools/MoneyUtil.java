/**
 *	@copyright wanruome-2017
 * 	@author wanruome
 * 	@create 2017年5月2日 下午5:06:41
 */
package com.ruomm.base.tools;

import java.text.DecimalFormat;

public class MoneyUtil {
	public static String convertFenToYuan(Long moneyValue) {

		if (null == moneyValue) {
			return null;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(moneyValue / 100.00);
	}

	public static Long convertYuanToFen(String showValue) {
		Double resultValue = null;
		if (null != showValue && showValue.length() > 0) {

			try {
				resultValue = Double.valueOf(showValue) * 100;
			}
			catch (Exception e) {
				e.printStackTrace();
				resultValue = null;

			}
		}
		if (null == resultValue) {
			return null;
		}
		return Math.round(resultValue);
	}

}
