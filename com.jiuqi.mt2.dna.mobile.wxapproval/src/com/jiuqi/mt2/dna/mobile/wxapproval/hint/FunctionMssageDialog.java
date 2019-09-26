package com.jiuqi.mt2.dna.mobile.wxapproval.hint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jiuqi.dna.bap.model.common.runtime.base.BusinessModel;
import com.jiuqi.dna.core.User;

/**
 * ��������Ϣ������
 * @author liuzihao
 */
public class FunctionMssageDialog {
	private static Map<String, List<DialogInfo>> Dialog = new HashMap<String, List<DialogInfo>>();// ��ʾ��Ϣ
	private static Map<String ,String> billflog=new HashMap<String ,String >();//���ݱ�ʶ key��username value��billid
	
	/**
	 * �������û�����Ϣ����(������ڽ�ɾ��ԭ�е�)
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
	 * ����ĳ�û���һ����Ϣ
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
	 * ����ĳ�û�����Ϣ������
	 * @param key
	 */
	public static void destroyDialog(String key,String billid) {
		if(billflog.get(key).equals(billid)){
			Dialog.remove(key);
			billflog.remove(key);
		}
	}
	
	/**
	 * ��ȡĳ�û��˵���Ϣ�б�
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
	 * ����Alert��Ϣ
	 * @param title ����
	 * @param context ����
	 * @param functionname ������
	 * @param model true ����, false û�д���
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
	 * ����Confirm����Ϣ
	 * @param title ����
	 * @param context ����
	 * @param functionname ������
	 * @param selectYes ���ȷ��ʱִ�еķ���
	 * @param selectNo ���ȡ����ʱִ�еķ���
	 * @param model true ����, false û�д���
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
	 * ����DoAction
	 * @param functionname
	 * @param actionname
	 * @param type
	 * @param model
	 * @return true ����, false û�д���
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
	 * ��ԭ�л���������һ����Ϣ
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
	 * �Ƴ��û���ĳ����ʾ��Ϣ
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
