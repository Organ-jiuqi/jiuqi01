package com.jiuqi.mt2.dna.mobile.wxapproval.hint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ִ�к���ʱ��������ʾ,�����û��ķ�ʽ ��¼����
 * @author liuzihao
 */
public class WXMessageDialogManage {
	
	private static int counter=1;
	private static int maxCounter=1000000000;//������
	
	private static Map<String, List<DialogInfo>> WXDialogList = new HashMap<String, List<DialogInfo>>();// ��ʾ��Ϣ

	/**
	 * ������ĳ������Ϣ����(������ڽ�ɾ��ԭ�е�)
	 * @param key
	 */
	public static void newDialogList(String key) {
		if (WXDialogList.containsKey(key)) {
			WXDialogList.remove(key);
		}
		List<DialogInfo> ldi = new ArrayList<DialogInfo>();
		WXDialogList.put(key, ldi);
	}
	/**
	 * ����ĳ�û�����Ϣ������
	 * @param key
	 */
	public static void destroyDialogList(String key) {
		if (WXDialogList.containsKey(key)) {
			WXDialogList.remove(key);
		}
	}
	/**
	 * ��ȡĳ�û��˵���Ϣ�б� �����б���ɾ������ʾ��Ϣ
	 * @param key
	 * @return
	 */
	public static DialogInfo getDialogById(String key,int id) {
		if (WXDialogList.containsKey(key)) {
			List<DialogInfo> ldi=WXDialogList.get(key);
			for(DialogInfo l: ldi){
				if(l.getId()==id){
					return l;
				}
			}
		}
		return null;
	}
	
	/**
	 * ��ȡĳ�û��˵���Ϣ�б�
	 * @param key
	 * @return
	 */
	public static List<DialogInfo> getDialogList(String key) {
		if (WXDialogList.containsKey(key)) {
			return WXDialogList.get(key);
		}
		return null;
	}
	/**
	 * ��ȡ�û���ʾ��Ϣ������
	 * @param key
	 * @return
	 */
	public static int getDialogNumber(String key){
		if (WXDialogList.containsKey(key)) {
			return WXDialogList.get(key).size();
		}
		return 0;
	}
	/**
	 * Ѱ����������Id��С��Dialog
	 * @param key
	 * @return
	 */
	public static DialogInfo getBasetSuitedDialog(String key){
		if (!WXDialogList.containsKey(key)) {
			return null;
		}else{
			List<DialogInfo> ldi=getDialogList(key);
			if(ldi==null ||ldi.size()==0){
				return null;
			}
			int depth=0;
			for(DialogInfo di :ldi){
				if(di.getVerticalDepth()>depth){
					depth=di.getVerticalDepth();
				}
			}
			int id=ldi.get(0).getId();
			for(DialogInfo di :ldi){
				if(di.getVerticalDepth()==depth&&di.getId()<id){
					id=di.getId();
				}
			}
			DialogInfo diloginfo=getDialogById(key, id);
			return diloginfo;
		}
	}
	
	/**
	 * ����ĳ�û���һ����Ϣ
	 * @param key
	 * @param info
	 */
	public static List<DialogInfo> addDialog(String key, DialogInfo info){
		info.setId(getCounter());
		if (WXDialogList.containsKey(key)) {
			List<DialogInfo> ldi=WXDialogList.get(key);
			ldi.add(info);
			WXDialogList.put(key, ldi);
			return ldi;
		}else{
			List<DialogInfo> ldi = new ArrayList<DialogInfo>();
			ldi.add(info);
			WXDialogList.put(key, ldi);
			return ldi;
		}
	}
	/**
	 * ���Ӷ���û���¼
	 * @param key
	 * @param list
	 * @return
	 */
	public static List<DialogInfo> addDialogList(String key,List<DialogInfo> list){
		if(counter+list.size()>maxCounter){
			counter=0;
		}
		List<DialogInfo> ldi=null;
		if (WXDialogList.containsKey(key)) {
			ldi=WXDialogList.get(key);
		}else{
			ldi=new ArrayList<DialogInfo>();
		}
		for(DialogInfo di : list){
			di.setId(getCounter());
			ldi.add(di);
		}
		WXDialogList.put(key, ldi);
		return ldi;
	}
	/**
	 * ����������У�ͬʱ������Щ��Ϣ�����
	 * @param key
	 * @param list
	 * @param depth ���
	 * @return
	 */
	public static List<DialogInfo> addDialogList(String key,List<DialogInfo> list,int depth){
		if(counter+list.size()>maxCounter){
			counter=0;
		}
		List<DialogInfo> ldi=null;
		if (WXDialogList.containsKey(key)) {
			ldi=WXDialogList.get(key);
		}else{
			ldi=new ArrayList<DialogInfo>();
		}
		for(DialogInfo di : list){
			di.setId(getCounter());
			di.setVerticalDepth(depth);
			ldi.add(di);
		}
		WXDialogList.put(key, ldi);
		return ldi;
	}
	/**
	 * �Ƴ�ĳ�û���ĳ����ʾ��Ϣ
	 * @param username
	 * @param id
	 * @return
	 */
	public static List<DialogInfo> removerDialogById(String key,int id){
		List<DialogInfo> ldi=null;
		if (WXDialogList.containsKey(key)) {
			ldi= WXDialogList.get(key);
		}
		if(ldi!=null){
			for(int i=0;i<ldi.size();i++){
				if(ldi.get(i).getId()==id){
					ldi.remove(ldi.get(i));
				}
			}
			
			WXDialogList.remove(key);
			WXDialogList.put(key, ldi);
		}
		return null;
	}
	
	public static int getCounter() {
		if(counter>maxCounter){
			counter=0;
		}
		return counter++;
	}

}
