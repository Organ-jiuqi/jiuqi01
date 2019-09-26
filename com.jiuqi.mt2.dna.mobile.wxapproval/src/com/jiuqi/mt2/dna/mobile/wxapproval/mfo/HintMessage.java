package com.jiuqi.mt2.dna.mobile.wxapproval.mfo;

import org.json.JSONException;
import org.json.JSONObject;

import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.DialogInfo;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * 生成的提示消息JOSNObject和解析返回值JOSNObject
 * @author liuzihao
 */
public class HintMessage {
	public static String Hint_Typt="Hint_Typt";//类型
	public static String Hint_Title="Hint_Title";//标题
	public static String Hint_Context="Hint_Context";//内容
	public static String Hint_Exception="Hint_Exception";//异常信息
	public static String Hint_Url="Hint_Url";//前往位置    非空：继续运行  空：停止
	
	/**
	 * 用户选择了Yes
	 */
	public static String UserSelectYes="Yes";
	/**
	 * 用户选择了NO
	 */
	public static String UserSelectNo="No";
	/**
	 * 成功
	 */
	public static String HintType_Success="success";
	/**
	 * 发生错误
	 */
	public static String HintType_Error="error";
	//下面几种类型与vacomm包里面的DialogInfo里的几种类型必须一致，这里重新也一次是为了不过分依赖vacomm包,当脱离vacomm包也能单独运行该部分代码
	/**
	 * alert提示且停止
	 */
	public static String HintType_AlertStop="Alert_Stop";
	/**
	 * alert提示且继续运行
	 */
	public static String HintType_AlertRun="Alert_Run";
	/**
	 * confirm提示且确认继续进行，取消停止
	 */
	public static String HintType_ConfirmNone="ConFirm_None";
	/**
	 * confirm提示且确认执行确认动作，取消执行取消动作
	 */
	public static String HintType_ConfirmAction="Confirm_Action";
	/**
	 * confirm提示且确认执行确认公式，取消执行取消公式
	 */
	public static String HintType_ConfirmFormula="Confirm_Formula";
	
	public static String toJSONObject(DialogInfo di,String key){
		JSONObject json=new JSONObject();
		try {
			json.put(Hint_Typt,di.getDialogtype());
			json.put(Hint_Title,di.getTitle());
			json.put(Hint_Context,di.getContext());
			if(DialogInfo.DialogAlertStop.equals(di.getDialogtype())) {
				json.put(Hint_Url, "");
			}else if (DialogInfo.DialogAlertRun.equals(di.getDialogtype())
					||DialogInfo.DialogConfirmNone.equals(di.getDialogtype())
					||DialogInfo.DialogConfirmAction.equals(di.getDialogtype())
					||DialogInfo.DialogConfirmFormula.equals(di.getDialogtype())){
				json.put(Hint_Url, Constants.UrlWXApprovalHint+"?key="+key+"&hintId="+di.getId()+"&userSelect=");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString(); 
	}
	/**
	 * 生成错误
	 */
	public static String toError(String context){
		JSONObject json=new JSONObject();
		try {
			json.put(Hint_Typt,HintType_Error);
			json.put(Hint_Title,"");
			json.put(Hint_Context,context);
			json.put(Hint_Exception, "");
			json.put(Hint_Url,"");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	/**
	 * 生成错误消息
	 * @param context 提示内容
	 * @param exception 异常信息
	 * @return
	 */
	public static String toError(String context,String exception){
		JSONObject json=new JSONObject();
		try {
			json.put(Hint_Typt,HintType_Error);
			json.put(Hint_Title,"");
			json.put(Hint_Context,context);
			json.put(Hint_Exception, exception);
			json.put(Hint_Url,"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	/**
	 * 成功默认成功提示！
	 */
	public static String toSuccess(){
		JSONObject json=new JSONObject();
		try {
			json.put(Hint_Typt,HintType_Success);
			json.put(Hint_Title,"");
			json.put(Hint_Context,"处理成功！");
			json.put(Hint_Exception, "");
			json.put(Hint_Url,"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	/**
	 * 成功默认成功提示！
	 */
	public static String toSuccess(String Context,String exception){
		JSONObject json=new JSONObject();
		try {
			json.put(Hint_Typt,HintType_Success);
			json.put(Hint_Title,"");
			json.put(Hint_Context,StringUtil.isNotEmpty(Context)?Context:"处理成功！");
			json.put(Hint_Exception, StringUtil.isNotEmpty(exception)?exception:"");
			json.put(Hint_Url,"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	/**
	 * 成功并生成成功消息
	 * @param context
	 */
	public static String toSuccess(String context){
		JSONObject json=new JSONObject();
		try {
			json.put(Hint_Typt,HintType_Success);
			json.put(Hint_Title,"");
			json.put(Hint_Context,context);
			json.put(Hint_Url,"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString(); 
	}
	
}
