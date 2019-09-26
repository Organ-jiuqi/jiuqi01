package com.jiuqi.mt2.dna.mobile.wxapproval.hint;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.bap.model.common.runtime.intf.IMessageDialog;
/**
 * ����BillModel�����ʾ����ȡbillModel.messagedialog�ķ���
 * @author liuzihao
 */
public class WXMessageDialog implements IMessageDialog{

	private List<DialogInfo> listdialoginfo=new ArrayList<DialogInfo>();//�õ���ִ���в�������ʾ��Ϣ
	//��ʾ����ͨ���޷���������
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
	 * ��ȡ��ʾ (���֮ǰ�ĵ���ʾ��Ϣ)
	 * @param flag true���  false�����
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
