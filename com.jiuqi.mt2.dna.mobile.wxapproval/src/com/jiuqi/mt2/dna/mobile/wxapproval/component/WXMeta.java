package com.jiuqi.mt2.dna.mobile.wxapproval.component;
/**
 * ҳ���Meta
 * @author liuzihao
 *
 */
public class WXMeta {
	//ҳ������ʽ
	
	public static String Coded_UTF_8="<meta charset='UTF-8'>";
	public static String Coded_GBK="<meta charset='GBK'>";
	public static String DefineCoded=WXMeta.Coded_UTF_8;
	//��ͼ�ߴ�
	public static String Viewport_1_1="<meta name='viewport' content='width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0'>";
	public static String DefineViewport=WXMeta.Viewport_1_1;
	public static String createMeta(String meta){
		StringBuffer javascript=new StringBuffer("<meta \n").append(meta).append(">\n");
		return javascript.toString();
	}
	
	
}
