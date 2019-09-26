package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理审批过程中的需要临时存储审批数据
 * @author liuzihao
 */
public class WXApprovalDataManage {
	
	private static Map<String,WXApprovalData> ApprovalData=new HashMap<String,WXApprovalData> ();
	
	/**
	 * 新建该用户的审批数据存储,如果已经存在,先移除后放入
	 */
	public static WXApprovalData newApprovalData(String key,WXApprovalData data){
		if(ApprovalData.containsKey(key)){
			ApprovalData.remove(key);
		}
		ApprovalData.put(key, data);
		return data;
	}
	
	/**
	 * 销毁该用户临时存储的审批数据
	 */
	public static void destroyApprovalData(String key){
		ApprovalData.remove(key);
	}
	
	/**
	 * 获取该用户临时存储的审批数据
	 */
	public static WXApprovalData getApprovalData(String username){
		if(ApprovalData.containsKey(username)){
			return ApprovalData.get(username);
		}
		return null;
	}
	
	/**
	 * 获取该用户临时存储数据的 识别码
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
	 * 增加审批次数
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
	 * 增加保存次数
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
