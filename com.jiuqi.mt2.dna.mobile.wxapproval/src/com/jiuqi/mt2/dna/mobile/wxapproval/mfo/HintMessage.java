package com.jiuqi.mt2.dna.mobile.wxapproval.mfo;

import org.json.JSONException;
import org.json.JSONObject;

import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.DialogInfo;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * ���ɵ���ʾ��ϢJOSNObject�ͽ�������ֵJOSNObject
 * @author liuzihao
 */
public class HintMessage {
	public static String Hint_Typt="Hint_Typt";//����
	public static String Hint_Title="Hint_Title";//����
	public static String Hint_Context="Hint_Context";//����
	public static String Hint_Exception="Hint_Exception";//�쳣��Ϣ
	public static String Hint_Url="Hint_Url";//ǰ��λ��    �ǿգ���������  �գ�ֹͣ
	
	/**
	 * �û�ѡ����Yes
	 */
	public static String UserSelectYes="Yes";
	/**
	 * �û�ѡ����NO
	 */
	public static String UserSelectNo="No";
	/**
	 * �ɹ�
	 */
	public static String HintType_Success="success";
	/**
	 * ��������
	 */
	public static String HintType_Error="error";
	//���漸��������vacomm�������DialogInfo��ļ������ͱ���һ�£���������Ҳһ����Ϊ�˲���������vacomm��,������vacomm��Ҳ�ܵ������иò��ִ���
	/**
	 * alert��ʾ��ֹͣ
	 */
	public static String HintType_AlertStop="Alert_Stop";
	/**
	 * alert��ʾ�Ҽ�������
	 */
	public static String HintType_AlertRun="Alert_Run";
	/**
	 * confirm��ʾ��ȷ�ϼ������У�ȡ��ֹͣ
	 */
	public static String HintType_ConfirmNone="ConFirm_None";
	/**
	 * confirm��ʾ��ȷ��ִ��ȷ�϶�����ȡ��ִ��ȡ������
	 */
	public static String HintType_ConfirmAction="Confirm_Action";
	/**
	 * confirm��ʾ��ȷ��ִ��ȷ�Ϲ�ʽ��ȡ��ִ��ȡ����ʽ
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
	 * ���ɴ���
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
	 * ���ɴ�����Ϣ
	 * @param context ��ʾ����
	 * @param exception �쳣��Ϣ
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
	 * �ɹ�Ĭ�ϳɹ���ʾ��
	 */
	public static String toSuccess(){
		JSONObject json=new JSONObject();
		try {
			json.put(Hint_Typt,HintType_Success);
			json.put(Hint_Title,"");
			json.put(Hint_Context,"����ɹ���");
			json.put(Hint_Exception, "");
			json.put(Hint_Url,"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	/**
	 * �ɹ�Ĭ�ϳɹ���ʾ��
	 */
	public static String toSuccess(String Context,String exception){
		JSONObject json=new JSONObject();
		try {
			json.put(Hint_Typt,HintType_Success);
			json.put(Hint_Title,"");
			json.put(Hint_Context,StringUtil.isNotEmpty(Context)?Context:"����ɹ���");
			json.put(Hint_Exception, StringUtil.isNotEmpty(exception)?exception:"");
			json.put(Hint_Url,"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	/**
	 * �ɹ������ɳɹ���Ϣ
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
