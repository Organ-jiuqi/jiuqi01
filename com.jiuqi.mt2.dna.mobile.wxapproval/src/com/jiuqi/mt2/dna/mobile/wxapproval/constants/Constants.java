package com.jiuqi.mt2.dna.mobile.wxapproval.constants;

/**
 * ΢����Ȩ�ĳ���
 * @author liuzihao
 */
public class Constants {
	/**
	 * ������ַ�����������������ã�����"IP"+"��"+"port"������б�ܵ�
	 */
	public static String HOST = System.getProperty("com.jiuqi.mt2.wxapproval.host");
	
	//Ĭ��ϵͳ��ʶ 
	/**
	 * BAP��������--�Զ��嶯��
	 */
	public static String UserDefine = "UserDefine";
	/**
	 * BAP��������--ͬ��
	 */
	public static String ACCEPT = "ACCEPT";
	/**
	 * BAP��������--����
	 */
	public static String REJECT = "REJECT";
	//�Զ��尴ť�ı�ʶ
	/**
	 * �Զ��尴ť��ͬ���ʶ(�����ִ�Сд)
	 */
	public static String UserDefineAccept = "UserDefineAccept";
	/**
	 * �Զ��尴ť�Ĳ��ر�ʶ(�����ִ�Сд)
	 */
	public static String UserDefineReject = "UserDefineReject";

	/**
	 * ��¼��URL
	 */
	public static String UrlWXLogin = HOST + "xspi/mt2/session/login";
	/**
	 * ͬ�⡢���ص�URL
	 */
	public static String UrlWXApproval = HOST + "xspi/mt2/execute_wxapproval";
	/**
	 * ������ʾ��,�������е�URL
	 */
	public static String UrlWXApprovalHint = HOST + "xspi/mt2/execute_wxapprovalhint";
	/**
	 * �������ݵ�ҳ��
	 */
	public static String UrlApprovalBill = HOST + "xspi/mt2/openwxapproval";
	/**
	 * ���������ݵ�ҳ��
	 */
	public static String UrlApprovedBill = HOST + "xspi/mt2/openwxapproved";
	/**
	 * ��ȡ�����������б�
	 */
	public static String UrlWXPendingBills = HOST + "xspi/mt2/openwxpendingbills";
	/**
	 * ��ȡ���ύ�ĵ����б�
	 */
	public static String UrlWXMyBills = HOST + "xspi/mt2/openwxmybills";
	
	public static int MODEL_TYPE = 1;
	
	public static String UrlWXUserContext = HOST + "xspi/mt2/wxuserctx";
	
	/**
	 * ����Ԥ����ҳ��
	 */
	public static String VIEWATTACHMENT = HOST + "xspi/mt2/wxattachment";
	
	/**
	 * ����ҳ��
	 */
	public static String VIEWDOWNLOAD= HOST + "xspi/mt2/download";
	
	
}
