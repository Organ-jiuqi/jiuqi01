package com.jiuqi.mt2.dna.mobile.wxapproval.constants;

/**
 * 微信授权的常量
 * @author liuzihao
 */
public class Constants {
	/**
	 * 主机地址（需启动参数里配置，例："IP"+"："+"port"）不加斜杠的
	 */
	public static String HOST = System.getProperty("com.jiuqi.mt2.wxapproval.host");
	
	//默认系统标识 
	/**
	 * BAP动作名称--自定义动作
	 */
	public static String UserDefine = "UserDefine";
	/**
	 * BAP动作名称--同意
	 */
	public static String ACCEPT = "ACCEPT";
	/**
	 * BAP动作名称--驳回
	 */
	public static String REJECT = "REJECT";
	//自定义按钮的标识
	/**
	 * 自定义按钮的同意标识(不区分大小写)
	 */
	public static String UserDefineAccept = "UserDefineAccept";
	/**
	 * 自定义按钮的驳回标识(不区分大小写)
	 */
	public static String UserDefineReject = "UserDefineReject";

	/**
	 * 登录的URL
	 */
	public static String UrlWXLogin = HOST + "xspi/mt2/session/login";
	/**
	 * 同意、驳回的URL
	 */
	public static String UrlWXApproval = HOST + "xspi/mt2/execute_wxapproval";
	/**
	 * 产生提示后,继续运行的URL
	 */
	public static String UrlWXApprovalHint = HOST + "xspi/mt2/execute_wxapprovalhint";
	/**
	 * 审批单据的页面
	 */
	public static String UrlApprovalBill = HOST + "xspi/mt2/openwxapproval";
	/**
	 * 已审批单据的页面
	 */
	public static String UrlApprovedBill = HOST + "xspi/mt2/openwxapproved";
	/**
	 * 获取待审批单据列表
	 */
	public static String UrlWXPendingBills = HOST + "xspi/mt2/openwxpendingbills";
	/**
	 * 获取我提交的单据列表
	 */
	public static String UrlWXMyBills = HOST + "xspi/mt2/openwxmybills";
	
	public static int MODEL_TYPE = 1;
	
	public static String UrlWXUserContext = HOST + "xspi/mt2/wxuserctx";
	
	/**
	 * 在线预览的页面
	 */
	public static String VIEWATTACHMENT = HOST + "xspi/mt2/wxattachment";
	
	/**
	 * 下载页面
	 */
	public static String VIEWDOWNLOAD= HOST + "xspi/mt2/download";
	
	
}
