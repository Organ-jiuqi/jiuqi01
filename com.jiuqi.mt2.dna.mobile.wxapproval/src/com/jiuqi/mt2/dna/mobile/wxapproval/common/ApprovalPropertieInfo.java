package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import java.util.HashMap;
import java.util.Map;

import com.jiuqi.dna.bap.workflowmanager.common.parse.Button;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.ApprovalProperties;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;

/**
 * �������Լ���ť
 * @author liuzihao
 */
public class ApprovalPropertieInfo {
	private ApprovalProperties ap;//��������
	private Map<String,Button> buttons=new HashMap<String ,Button>();//��ť
	private int buttonnumber;//��ť����
	private String[] EditField=null;//�ɱ༭�ֶ�
	
	private boolean flagSuggestionAgree=false;//ͬ��ʱ  trueѡ�� false����
	private boolean flagSuggestionReject=false;//����ʱ   trueѡ�� false����
	
	/**
	 * ��ȡ �������ԡ��ڵ�󶨰�ť�����ֶ�
	 * @param context
	 * @param workItemId
	 */
	public ApprovalPropertieInfo(Context context,IWorkItem workItem,ApprovalProperties ap){
		this.ap=ap;
		if(ap!=null){
			this.flagSuggestionAgree=ap.isSuggestionAgreeIsEnableNull();
			this.flagSuggestionReject=ap.isSuggestionIsNull();
		}
		Button[] bs = BillCommon.getButton(context, workItem);
		int number=0;
		for(Button b : bs){
			if(b.name.equals(Constants.UserDefine)&&b.description.equalsIgnoreCase(Constants.UserDefineAccept)){
				this.buttons.put(Constants.UserDefineAccept, b);
				number++;
			}else if(b.name.equals(Constants.UserDefine)&&b.description.equalsIgnoreCase(Constants.UserDefineReject)){
				this.buttons.put(Constants.UserDefineReject, b);
				number++;
			}else if(b.name.equals(Constants.ACCEPT)){
				this.buttons.put(Constants.ACCEPT, b);
				number++;
			}else if(b.name.equals(Constants.REJECT)){
				this.buttons.put(Constants.REJECT, b);
				number++;
			}
		}
		this.buttonnumber=number;
		this.EditField=BillCommon.getEditFields(context, workItem);
	}
	
	/**
	 * ��ȡ �������ԡ��ڵ�󶨰�ť�����ֶ�
	 * @param context
	 * @param workItemId
	 */
	public ApprovalPropertieInfo(Context context,IWorkItem workItem){
		this.ap=BillCommon.getApprovalProperties(context, workItem);
		if(ap!=null){
			this.flagSuggestionAgree=ap.isSuggestionAgreeIsEnableNull();
			this.flagSuggestionReject=ap.isSuggestionIsNull();
		}
		Button[] bs = BillCommon.getButton(context, workItem);
		int number=0;
		for(Button b : bs){
			if(b.name.equals(Constants.UserDefine)&&b.description.equalsIgnoreCase(Constants.UserDefineAccept)){
				this.buttons.put(Constants.UserDefineAccept, b);
				number++;
			}else if(b.name.equals(Constants.UserDefine)&&b.description.equalsIgnoreCase(Constants.UserDefineReject)){
				this.buttons.put(Constants.UserDefineReject, b);
				number++;
			}else if(b.name.equals(Constants.ACCEPT)){
				this.buttons.put(Constants.ACCEPT, b);
				number++;
			}else if(b.name.equals(Constants.REJECT)){
				this.buttons.put(Constants.REJECT, b);
				number++;
			}
		}
		this.buttonnumber=number;
		this.EditField=BillCommon.getEditFields(context, workItem);
	}

	public int getButtonNumber(){
		return this.buttonnumber;
	}
	
	public String getButtonNameZH(String name){
		if(name.equalsIgnoreCase(Constants.UserDefineAccept)){
			return "�Զ���ͬ��";
		}else if(name.equalsIgnoreCase(Constants.UserDefineReject)){
			return "�Զ��岵��";
		}else if(name.equals(Constants.ACCEPT)){
			return "ͬ��";
		}else if(name.equals(Constants.REJECT)){
			return "����";
		}else{
			return "";
		}
		
	}
	
	/**
	 * ͬ��ʱ trueѡ�� false����
	 */
	public boolean isFlagSuggestionAgree() {
		return flagSuggestionAgree;
	}
	/**
	 * ����ʱ trueѡ�� false����
	 */
	public boolean isFlagSuggestionReject() {
		return flagSuggestionReject;
	}
	/**
	 * ��������������ذ�ť��Ϣ
	 */
	public Button getButton(String actionName){
		return buttons.get(actionName);
	}
	/**
	 * ��������������ظ��������������Ƿ���Ҫ��д false����  trueѡ��
	 */
	public boolean getFlag(String actionName){
		if(Constants.UserDefineAccept.equals(actionName)||Constants.ACCEPT.equals(actionName)){
			return flagSuggestionAgree;
		}else if(Constants.UserDefineReject.equals(actionName)||Constants.REJECT.equals(actionName)){
			return flagSuggestionReject;
		}else{
			return false;
		}
		
	}
	/**
	 * ��ȡ���ذ�ť�Ƿ���Ҫִ�б����߼��������Ҫ����true;
	 */
	public boolean getSaveCheck(String actionName){
		Button button= getButton(actionName);
		String rfalg="";
		if(button.name.equals("REJECT")){
			rfalg=button.param.replace("@v1 oldParam:save_check:","");
			if(rfalg.equals("false")||rfalg.equals("")){
				return false;
			}
		}
		
		return true;
	}
	
	
	
	public Button getUserDefineAccept() {
		return buttons.get(Constants.UserDefineAccept);
	}
	public Button getUserDefineReject() {
		return buttons.get(Constants.UserDefineReject);
	}
	public Button getAccept() {
		return buttons.get(Constants.ACCEPT);
	}
	public Button getReject() {
		return buttons.get(Constants.REJECT);
	}
	public String[] getEditField() {
		return EditField;
	}
	public ApprovalProperties getAp() {
		return ap;
	}
	
}