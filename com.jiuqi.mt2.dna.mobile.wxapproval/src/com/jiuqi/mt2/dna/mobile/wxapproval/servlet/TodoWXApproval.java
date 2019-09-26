package com.jiuqi.mt2.dna.mobile.wxapproval.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.model.common.define.intf.IFormula;
import com.jiuqi.dna.bap.model.common.type.ModelState;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.http.DNAHttpServlet;
import com.jiuqi.dna.core.invoke.AsyncHandle;
import com.jiuqi.dna.core.invoke.AsyncTask;
import com.jiuqi.dna.core.service.AsyncInfo;
import com.jiuqi.dna.core.service.AsyncInfo.SessionMode;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXPlaintextScramble;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.DialogInfo;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.FunctionMssageDialog;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.WXMessageDialog;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.WXMessageDialogManage;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.HintMessage;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.SendMsgToWX;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXErrorPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.task.TodoWXApprovalTask;
import com.jiuqi.mt2.dna.mobile.wxapproval.task.TodoWXApprovalTask.Method;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * 执行按钮(同意或驳回或自定义的同意或自定义的驳回)
 * @author liuzihao
 */
@SuppressWarnings("restriction")
public class TodoWXApproval extends DNAHttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp){
		this.doPost(req, resp);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		String key = req.getParameter("key");// key扰码后的明文
		if(key==null || key.length()<32){
			SendMsgToWX.sendHint(resp, HintMessage.toError("提交信息有误,请联系管理员!"));
			return;
		}
		WXPlaintextScramble scramble = new WXPlaintextScramble(key);
		String code = scramble.getCode();//用户名
		String workItemId = scramble.getWorkitemid();//节点ID
		String actionName = req.getParameter("action");// 方法名
		String comment = req.getParameter("comment");// 审批意见
		String data = req.getParameter("datas");// 发生变化的单据数据
		
		//判断提交信息的key是否正确
		if (StringUtil.isEmpty(code) || StringUtil.isEmpty(workItemId) || StringUtil.isEmpty(actionName)) {
			SendMsgToWX.sendHint(resp, HintMessage.toError("提交信息有误,请联系管理员!"));
			return;
		}
		
		ContextSPI context = AppUtil.getDefaultApp().getSystemSession().newContext(false);
		AsyncInfo e = new AsyncInfo();
		e.setSessionMode(SessionMode.INDIVIDUAL_ANONYMOUS);
		TodoWXApprovalTask task = new TodoWXApprovalTask();
		task.setCode(code);
		task.setWorkItemId(workItemId);
		task.setActionName(actionName);
		task.setComment(comment);
		task.setData(data);
		task.setKey(key);
		task.setResp(resp);
		task.setScramble(scramble);
		AsyncTask<TodoWXApprovalTask, Method> asynTask = context.asyncHandle(task,
				Method.DoWXApproval, e);
		try {
			context.waitFor(asynTask, new AsyncHandle[0]);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String errorInfo = task.getErrorInfo();
		WXErrorPage errorPage = task.getErrorPage();
		if (!StringUtil.isEmpty(errorInfo)) {
			SendMsgToWX.sendHint(resp, errorInfo);
		} else if (errorPage != null) {
			SendMsgToWX
					.sendHtml(context, resp, errorPage.getPage());
		}
	}
	
	/**
	 * 执行公式
	 */
	private void executeFormula(BillModel model, String[] formulaNames,boolean saveAfterexecute,String code,Context context,IWorkItem iworkItem ){
		String billid = model.getModelData().getMaster().getRECID().toString();
		model.beginUpdate();
		try {
			if (formulaNames == null || formulaNames.length < 1)
				return;
			for (String formulaName : formulaNames) {
				for (IFormula formula : model.getDefine().getFormulas()) {
					if (formulaName != null	&& formulaName.equals(formula.getName())) {
						FunctionMssageDialog.newDialog(code, billid);
						model.execute(formula);
						List<DialogInfo> list = FunctionMssageDialog.getDialog(code, billid);
						WXMessageDialogManage.addDialogList(code, list);
						FunctionMssageDialog.destroyDialog(code, billid);
						WXMessageDialog wxmd=(WXMessageDialog) model.messageDialog;
						if(wxmd.getReturnCode()>0){
							List<DialogInfo> ldi=wxmd.getListDialog(true);
							WXMessageDialogManage.addDialogList(code, ldi);
						}
					}
				}
			}
			ModelState modelState = model.getModelState();
			if (saveAfterexecute) {
				if (!(modelState.equals(ModelState.NEW) || modelState.equals(ModelState.EDIT))) {
					model.setModelState(ModelState.EDIT);
				}
			}
		} finally {
			model.endUpdate();
		}
		
		((ContextSPI)model.getContext()).resolveTrans();
		((ContextSPI)model.getContext()).dispose();
		//model.load(GUID.valueOf(iworkItem.getProcessInstance().getGUIDRef()));
	}
	
	
}



//BillModel billmodel2=billModel;
//执行自定义按钮绑定的公式 
//将 confirmAction confrimFormula doAction 记录
//String billid = billModel.getModelData().getMaster().getRECID().toString();
//billModel.beginUpdate();
//
//for (String f : bpr.getFormuals()) {
//	for (IFormula formula : billModel.getDefine().getFormulas()) {
//		if (formula.getName().equals(f)) {
			
//			FunctionMssageDialog.newDialog(code, billid);
//			billmodel2=billModel;
//			billmodel2.beginUpdate();
//			try{
//				billmodel2.execute(formula);
//			}catch(Exception e){
//				//事物回滚
//				((ContextSPI)billmodel2.getContext()).resolveTrans();
//				e.printStackTrace();
//				StringBuffer error=new StringBuffer();
//				for(StackTraceElement einfo: e.getStackTrace()){
//					error.append(einfo).append("<br>");
//				}
//				if(StringUtil.isNotEmpty(error.toString())){
//					SendMsgToWX.sendHint(resp, HintMessage.toError(StringUtil.isNotEmpty(e.getMessage())?e.getMessage():"发生异常",error.toString()));
//				}
//				return;
//			}finally{
//				billmodel2.endUpdate(); 
//				List<DialogInfo> list = FunctionMssageDialog.getDialog(code, billid);
//				WXMessageDialogManage.addDialogList(code, list);
//				FunctionMssageDialog.destroyDialog(code, billid);
//				billModel.setModelData(billmodel2.getModelData());
//				context=BillCommon.contextCommit(context,billmodel2);
//			}
//				FunctionMssageDialog.newDialog(code, billid);
//				billmodel2=billModel;
//				billmodel2.beginUpdate();
//				billmodel2.execute(formula);
//				billmodel2.endUpdate(); 
//				List<DialogInfo> list = FunctionMssageDialog.getDialog(code, billid);
//				WXMessageDialogManage.addDialogList(code, list);
//				FunctionMssageDialog.destroyDialog(code, billid);
//				context=BillCommon.contextCommit(context,billmodel2);
//				
//				BillModel billModel3=BillCommon.getBillModel(context, iworkItem);
//				billModel3.load(GUID.valueOf(iworkItem.getProcessInstance().getGUIDRef()));
//				billModel.getData().getMaster().setRECVER(billModel3.getData().getMaster().getRECVER());
			
//		}
//	}
//}









//Session session=getApplication().newSession(null, null);
//ContextSPI context = session.newContext(true);
//FUser fuser = context.get(FUser.class,code);
//User user = context.get(User.class,fuser.getGuid());
//context.changeLoginUser(user);


