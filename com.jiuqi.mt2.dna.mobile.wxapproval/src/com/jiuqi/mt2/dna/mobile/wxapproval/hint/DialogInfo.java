package com.jiuqi.mt2.dna.mobile.wxapproval.hint;

import com.jiuqi.dna.core.User;

/**
 * 消息信息类
 * @author liuzihao
 */
public class DialogInfo {
	
	//都有
	private int id;//id
	private String dialogtype;//类型
	private String functionname;//函数名
	private User user; //用户
	
	//仅限Alert 和Confirm
	private String title;//标题
	private String context;//内容
	//仅限Confirm
	private String selectYes;//选择同意执行的函数或公式
	private String selectNo;//选择取消执行的函数或公式
	//仅限DoAction
	private String actionname;//动作名称
	
	private int verticalDepth=1;//纵向深度
	
	/**
	 * alert提示且停止
	 */
	public static String DialogAlertStop="Alert_Stop";
	/**
	 * alert提示且继续运行
	 */
	public static String DialogAlertRun="Alert_Run";
	/**
	 * confirm提示且确认继续进行，取消停止
	 */
	public static String DialogConfirmNone="ConFirm_None";
	/**
	 * confirm提示且确认执行确认动作，取消执行取消动作
	 */
	public static String DialogConfirmAction="Confirm_Action";
	/**
	 * confirm提示且确认执行确认公式，取消执行取消公式
	 */
	public static String DialogConfirmFormula="Confirm_Formula";
	/**
	 * 执行动作
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
