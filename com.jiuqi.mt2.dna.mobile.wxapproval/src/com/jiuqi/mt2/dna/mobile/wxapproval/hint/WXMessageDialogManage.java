package com.jiuqi.mt2.dna.mobile.wxapproval.hint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行函数时产生的提示,按照用户的方式 记录下来
 * @author liuzihao
 */
public class WXMessageDialogManage {
	
	private static int counter=1;
	private static int maxCounter=1000000000;//最大计数
	
	private static Map<String, List<DialogInfo>> WXDialogList = new HashMap<String, List<DialogInfo>>();// 提示消息

	/**
	 * 创建该某户的消息处理(如果存在将删除原有的)
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
	 * 销毁某用户的消息处理类
	 * @param key
	 */
	public static void destroyDialogList(String key) {
		if (WXDialogList.containsKey(key)) {
			WXDialogList.remove(key);
		}
	}
	/**
	 * 获取某用户端的消息列表 并在列表里删除该提示信息
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
	 * 获取某用户端的消息列表
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
	 * 获取用户提示信息的数量
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
	 * 寻找深度最大且Id最小的Dialog
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
	 * 增加某用户的一条信息
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
	 * 增加多个用户记录
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
	 * 批量放入队列，同时增加这些信息的深度
	 * @param key
	 * @param list
	 * @param depth 深度
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
	 * 移除某用户的某条提示信息
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
