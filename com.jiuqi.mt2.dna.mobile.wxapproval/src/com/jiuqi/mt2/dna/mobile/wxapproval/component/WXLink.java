package com.jiuqi.mt2.dna.mobile.wxapproval.component;

/**
 * 外部引入CSS或JS
 * 
 * @author liuzihao
 */
public class WXLink {
	/**
	 * 生成CSS外部引用
	 */
	public static String createLinkCSS(String css) {
		StringBuffer javascript = new StringBuffer(
				"<link rel='stylesheet' type='text/css' href='").append(css)
				.append("'/>");
		return javascript.toString();
	}

	/**
	 * 生成js外部引用
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
