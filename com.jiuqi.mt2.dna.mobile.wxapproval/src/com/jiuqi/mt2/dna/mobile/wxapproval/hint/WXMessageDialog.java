package com.jiuqi.mt2.dna.mobile.wxapproval.hint;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.bap.model.common.runtime.intf.IMessageDialog;
/**
 * 处理BillModel里的提示，获取billModel.messagedialog的方法
 * @author liuzihao
 */
public class WXMessageDialog implements IMessageDialog{

	private List<DialogInfo> listdialoginfo=new ArrayList<DialogInfo>();//该单据执行中产生的提示信息
	//提示，不通过无法继续进行
	public IMessageDialog alert(String message) {
		DialogInfo info = new DialogInfo();
		info.addAlert(message);
		listdialoginfo.add(info);
		return this;
	}
	public IMessageDialog alert(String title, String message) {
		DialogInfo info = new DialogInfo();
		info.addAlert(title,message);
		listdialoginfo.add(info);
		return this;
	}

	public IMessageDialog confirm(String message) {
		DialogInfo info = new DialogInfo();
		info.addConfirm(message);
		listdialoginfo.add(info);
		return this;
	}

	public IMessageDialog confirm(String title, String message) {
		DialogInfo info = new DialogInfo();
		info.addConfirm(title,message);
		listdialoginfo.add(info);
		return this;
	}

	public int getReturnCode() {
		return listdialoginfo.size();
	}

	/**
	 * 获取提示 (清空之前的的提示信息)
	 * @param flag true清除  false不清除
	 * @return
	 */
	public List<DialogInfo> getListDialog(boolean flag){
		List<DialogInfo> ldi=new ArrayList<DialogInfo>();
		
		if(flag){
			for(int i=0;i<listdialoginfo.size();i++){
				ldi.add(listdialoginfo.get(i));
				listdialoginfo.remove(i);
			}
		}
		return ldi;
	}
	
}
