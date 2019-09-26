package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import java.util.HashMap;
import java.util.Map;

/**
 * �������������е���Ҫ��ʱ�洢��������
 * @author liuzihao
 */
public class WXApprovalDataManage {
	
	private static Map<String,WXApprovalData> ApprovalData=new HashMap<String,WXApprovalData> ();
	
	/**
	 * �½����û����������ݴ洢,����Ѿ�����,���Ƴ������
	 */
	public static WXApprovalData newApprovalData(String key,WXApprovalData data){
		if(ApprovalData.containsKey(key)){
			ApprovalData.remove(key);
		}
		ApprovalData.put(key, data);
		return data;
	}
	
	/**
	 * ���ٸ��û���ʱ�洢����������
	 */
	public static void destroyApprovalData(String key){
		ApprovalData.remove(key);
	}
	
	/**
	 * ��ȡ���û���ʱ�洢����������
	 */
	public static WXApprovalData getApprovalData(String username){
		if(ApprovalData.containsKey(username)){
			return ApprovalData.get(username);
		}
		return null;
	}
	
	/**
	 * ��ȡ���û���ʱ�洢���ݵ� ʶ����
	 */
	public static String getApprovalDataKey(String username){
		if(ApprovalData.containsKey(username)){
			WXApprovalData wxad= ApprovalData.get(username);
			if(wxad!=null){
				return wxad.getKey();
			}
		}
		return null;
	}
	
	/**
	 * ������������
	 * @param username
	 * @param data
	 * @return
	 */
	public static void addApprovalTimes(String username){
		if(ApprovalData.containsKey(username)){
			WXApprovalData wxad= ApprovalData.get(username);
			wxad.addExecuteApprovalTimes();
		}
	}
	
	/**
	 * ���ӱ������
	 * @param username
	 * @param data
	 * @return
	 */
	public static void addSaveTimes(String username){
		if(ApprovalData.containsKey(username)){
			WXApprovalData wxad= ApprovalData.get(username);
			wxad.addExecuteSave();
		}
	}
	
}
