package com.jiuqi.mt2.dna.mobile.wxapproval.component;

/**
 * �ⲿ����CSS��JS
 * 
 * @author liuzihao
 */
public class WXLink {
	/**
	 * ����CSS�ⲿ����
	 */
	public static String createLinkCSS(String css) {
		StringBuffer javascript = new StringBuffer(
				"<link rel='stylesheet' type='text/css' href='").append(css)
				.append("'/>");
		return javascript.toString();
	}

	/**
	 * ����js�ⲿ����
	 */
	public static String createLinkJavaScript(String js) {
		StringBuffer javascript = new StringBuffer(
				"<script type='text/javascript' src='").append(js).append(
				"'></script>");
		return javascript.toString();
	}

	public static String createLinkJavaScript1(String js) {
		StringBuffer javascript = new StringBuffer(
				"<script type='text/javascript'   charset='gb2312'  src='")
				.append(js).append("'></script>");
		return javascript.toString();
	}
}
