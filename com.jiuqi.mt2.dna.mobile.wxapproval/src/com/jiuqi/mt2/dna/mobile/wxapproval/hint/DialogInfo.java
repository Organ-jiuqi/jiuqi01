package com.jiuqi.mt2.dna.mobile.wxapproval.hint;

import com.jiuqi.dna.core.User;

/**
 * ��Ϣ��Ϣ��
 * @author liuzihao
 */
public class DialogInfo {
	
	//����
	private int id;//id
	private String dialogtype;//����
	private String functionname;//������
	private User user; //�û�
	
	//����Alert ��Confirm
	private String title;//����
	private String context;//����
	//����Confirm
	private String selectYes;//ѡ��ͬ��ִ�еĺ�����ʽ
	private String selectNo;//ѡ��ȡ��ִ�еĺ�����ʽ
	//����DoAction
	private String actionname;//��������
	
	private int verticalDepth=1;//�������
	
	/**
	 * alert��ʾ��ֹͣ
	 */
	public static String DialogAlertStop="Alert_Stop";
	/**
	 * alert��ʾ�Ҽ�������
	 */
	public static String DialogAlertRun="Alert_Run";
	/**
	 * confirm��ʾ��ȷ�ϼ������У�ȡ��ֹͣ
	 */
	public static String DialogConfirmNone="ConFirm_None";
	/**
	 * confirm��ʾ��ȷ��ִ��ȷ�϶�����ȡ��ִ��ȡ������
	 */
	public static String DialogConfirmAction="Confirm_Action";
	/**
	 * confirm��ʾ��ȷ��ִ��ȷ�Ϲ�ʽ��ȡ��ִ��ȡ����ʽ
	 */
	public static String DialogConfirmFormula="Confirm_Formula";
	/**
	 * ִ�ж���
	 */
	public static String DialogDoAction="Do_Action";
	
	public DialogInfo() {
	}
	
	/**
	 * alert
	 * @param title
	 * @param context
	 * @param functionname
	 */
	public DialogInfo(String title,String context,String functionname){
		this.title=title;
		this.context=context;
		this.functionname=functionname;
	}
	
	/**
	 * confirm
	 * @param title
	 * @param context
	 * @param functionname
	 * @param selectYes
	 * @param selectNo
	 */
	public DialogInfo(String title,String context,String functionname,String selectYes,String selectNo,String dialogtype){
		this.title=title;
		this.context=context;
		this.functionname=functionname;
		this.selectYes=selectYes;
		this.selectNo=selectNo;
		this.dialogtype=dialogtype;
	}
	/**
	 * DoAction
	 */
	public DialogInfo(String functionname,String actionname){
		this.dialogtype=DialogDoAction;
		this.functionname=functionname;
		this.actionname=actionname;
	}
	
	public void addAlert(String message){
		this.dialogtype=DialogAlertStop;
		this.context=message;
	}
	public void addAlert(String title,String message){
		this.dialogtype=DialogAlertStop;
		this.title=title;
		this.context=message;
	}
	public void addConfirm(String message){
		this.dialogtype=DialogConfirmNone;
		this.context=message;
	}
	public void addConfirm(String title,String message){
		this.dialogtype=DialogConfirmNone;
		this.title=title;
		this.context=message;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getDialogtype() {
		return dialogtype;
	}
	public void setDialogtype(String dialogtype) {
		this.dialogtype = dialogtype;
	}
	public String getFunctionname() {
		return functionname;
	}
	public void setFunctionname(String functionname) {
		this.functionname = functionname;
	}
	public String getSelectYes() {
		return selectYes;
	}
	public void setSelectYes(String selectYes) {
		this.selectYes = selectYes;
	}
	public String getSelectNo() {
		return selectNo;
	}
	public void setSelectNo(String selectNo) {
		this.selectNo = selectNo;
	}
	public int getVerticalDepth() {
		return verticalDepth;
	}
	public void setVerticalDepth(int depth) {
		this.verticalDepth=depth;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id =id;
	}
	public String getActionname() {
		return actionname;
	}
	public void setActionname(String actionname) {
		this.actionname = actionname;
	}

}
