package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import java.util.HashMap;
import java.util.Map;

import com.jiuqi.dna.bap.workflowmanager.common.parse.Button;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.ApprovalProperties;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;

/**
 * 审批属性及按钮
 * @author liuzihao
 */
public class ApprovalPropertieInfo {
	private ApprovalProperties ap;//单据属性
	private Map<String,Button> buttons=new HashMap<String ,Button>();//按钮
	private int buttonnumber;//按钮数量
	private String[] EditField=null;//可编辑字段
	
	private boolean flagSuggestionAgree=false;//同意时  true选填 false必填
	private boolean flagSuggestionReject=false;//驳回时   true选填 false必填
	
	/**
	 * 获取 审批属性、节点绑定按钮、绑定字段
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
	 * 获取 审批属性、节点绑定按钮、绑定字段
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
			return "自定义同意";
		}else if(name.equalsIgnoreCase(Constants.UserDefineReject)){
			return "自定义驳回";
		}else if(name.equals(Constants.ACCEPT)){
			return "同意";
		}else if(name.equals(Constants.REJECT)){
			return "驳回";
		}else{
			return "";
		}
		
	}
	
	/**
	 * 同意时 true选填 false必填
	 */
	public boolean isFlagSuggestionAgree() {
		return flagSuggestionAgree;
	}
	/**
	 * 驳回时 true选填 false必填
	 */
	public boolean isFlagSuggestionReject() {
		return flagSuggestionReject;
	}
	/**
	 * 根据请求参数返回按钮信息
	 */
	public Button getButton(String actionName){
		return buttons.get(actionName);
	}
	/**
	 * 根据请求参数返回该请求的审批意见是否需要填写 false必填  true选填
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
	 * 获取驳回按钮是否需要执行保存逻辑，如果需要返回true;
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