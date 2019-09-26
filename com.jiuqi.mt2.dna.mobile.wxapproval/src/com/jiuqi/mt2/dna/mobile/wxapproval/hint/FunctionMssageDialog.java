package com.jiuqi.mt2.dna.mobile.wxapproval.hint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jiuqi.dna.bap.model.common.runtime.base.BusinessModel;
import com.jiuqi.dna.core.User;

/**
 * 函数的消息处理类
 * @author liuzihao
 */
public class FunctionMssageDialog {
	private static Map<String, List<DialogInfo>> Dialog = new HashMap<String, List<DialogInfo>>();// 提示消息
	private static Map<String ,String> billflog=new HashMap<String ,String >();//单据标识 key：username value：billid
	
	/**
	 * 创建该用户的消息处理(如果存在将删除原有的)
	 */
	public static void newDialog(String key,String billid) {
		if (Dialog.containsKey(key)) {
			Dialog.remove(key);
		}
		List<DialogInfo> ldi = new ArrayList<DialogInfo>();
		Dialog.put(key, ldi);
		billflog.put(key, billid);
	}
	
	/**
	 * 增加某用户的一条信息
	 */
	public static void addDialog(String key, String billid,DialogInfo info){
		if (!Dialog.containsKey(key)) {
			newDialog(key,billid);
		}else{
			if(billflog.get(key).equals(billid)){
				List<DialogInfo> ldi=Dialog.get(key);
				ldi.add(info);
				Dialog.put(key, ldi);
			}
			
		}
	}
	
	/**
	 * 销毁某用户的消息处理类
	 * @param key
	 */
	public static void destroyDialog(String key,String billid) {
		if(billflog.get(key).equals(billid)){
			Dialog.remove(key);
			billflog.remove(key);
		}
	}
	
	/**
	 * 获取某用户端的消息列表
	 * @param key
	 * @return
	 */
	public static List<DialogInfo> getDialog(String key,String billid) {
		if (Dialog.containsKey(key) && billflog.get(key).equals(billid)) {
			return Dialog.get(key);
		}
		return null;
	}

	/**
	 * 增加Alert信息
	 * @param title 标题
	 * @param context 内容
	 * @param functionname 函数名
	 * @param model true 存入, false 没有存入
	 */
	public static boolean addAlert(String title, String context, String functionname, BusinessModel model) {
		User user = model.getContext().getLogin().getUser();
		String billid=model.getModelData().getMaster().getRECID().toString();
		if (Dialog.containsKey(user.getName())&& billflog.get(user.getName()).equals(billid)) {
			DialogInfo info = new DialogInfo(title, context, functionname);
			info.setDialogtype(DialogInfo.DialogAlertRun);
			info.setUser(user);
			putDialogInfo(user.getName(),billid,info);
			return true;
		}
		return false;
	}

	/**
	 * 增加Confirm的信息
	 * @param title 标题
	 * @param context 内容
	 * @param functionname 函数名
	 * @param selectYes 点击确认时执行的方法
	 * @param selectNo 点击取消是时执行的方法
	 * @param model true 存入, false 没有存入
	 */
	public static boolean addConfirm(String title, String context, String functionname, String selectYes, String selectNo, BusinessModel model,String dialogType) {
		User user = model.getContext().getLogin().getUser();
		String billid=model.getModelData().getMaster().getRECID().toString();
		if (Dialog.containsKey(user.getName()) && billflog.get(user.getName()).equals(billid)) {
			DialogInfo info = new DialogInfo(title, context, functionname, selectYes, selectNo,dialogType);
			info.setUser(user);
			putDialogInfo(user.getName(),billid,info);
			return true;
		}
		return false;
	}
	
	/**
	 * 增加DoAction
	 * @param functionname
	 * @param actionname
	 * @param type
	 * @param model
	 * @return true 存入, false 没有存入
	 */
	public static boolean addDoAction(String functionname,String actionname,String type,BusinessModel model){
		User user = model.getContext().getLogin().getUser();
		String billid=model.getModelData().getMaster().getRECID().toString();
		if (Dialog.containsKey(user.getName())&& billflog.get(user.getName()).equals(billid)) {
			if(actionname.equals("ACCEPT") || actionname.equals("REJECT") ||actionname.equals("SAVE")){
				DialogInfo info = new DialogInfo(functionname,actionname);
				info.setUser(user);
				putDialogInfo(user.getName(),billid,info);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 在原有基础上增加一条信息
	 */
	public static List<DialogInfo> putDialogInfo(String username,String billid,DialogInfo di){
		List<DialogInfo> ldi = null;
		if (Dialog.containsKey(username) && billflog.get(username).equals(billid) ) {
			ldi=Dialog.get(username);
			ldi.add(di);
			Dialog.remove(username);
			Dialog.put(username, ldi);
		}
		return ldi;
	}
	
	/**
	 * 移除用户的某条提示信息
	 * @param username
	 * @param id
	 * @return
	 */
	public static List<DialogInfo> remover(String username,String billid,int id){
		List<DialogInfo> ldi=null;
		if (Dialog.containsKey(username)&& billflog.get(username).equals(billid)) {
			ldi= Dialog.get(username);
		}
		if(ldi!=null){
			for(DialogInfo di : ldi){
				if(di.getId()==id){
					ldi.remove(di);
				}
			}
			Dialog.remove(username);
			Dialog.put(username, ldi);
		}
		return null;
	}

}
