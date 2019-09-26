package com.jiuqi.mt2.dna.mobile.wxapproval.service;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.model.common.define.intf.IFormula;
import com.jiuqi.dna.bap.model.common.runtime.impl.CheckResultInfo;
import com.jiuqi.dna.bap.model.common.runtime.impl.ErrorItem;
import com.jiuqi.dna.bap.model.common.type.ModelState;
import com.jiuqi.dna.bap.workflowmanager.common.parse.Button;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.WorkflowRunUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.ApprovalProperties;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.Service;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.workflow.engine.EnumWorkItemState;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.todo.facade.FApprovalDefine;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.ApprovalPropertieInfo;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.BillCommon;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.MT2Common;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.UserDefineButtonInfo;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXApprovalCommon;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXApprovalData;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXApprovalDataManage;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXPlaintextScramble;
import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;
import com.jiuqi.mt2.dna.mobile.wxapproval.exception.WXHintException;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.DialogInfo;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.FunctionMssageDialog;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.WXMessageDialog;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.WXMessageDialogManage;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.HintMessage;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXErrorPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.task.TodoWXApprovalTask;
import com.jiuqi.mt2.dna.service.bill.MobileBillErrorProcessor;
import com.jiuqi.mt2.spi.bill.metadata.MobileBillDefine;
import com.jiuqi.mt2.spi.bill.model.BillData;
import com.jiuqi.xlib.utils.StringUtil;

public class TodoWXApprovalService extends Service {
	protected TodoWXApprovalService() {
		super("TodoWXApprovalService");
	}

	private void executeFormula(BillModel model, String[] formulaNames,
			boolean saveAfterexecute, String code, Context context,
			IWorkItem iworkItem) {
		String billid = model.getModelData().getMaster().getRECID().toString();
		try {
			String[] arrayOfString;
			if ((formulaNames == null) || (formulaNames.length < 1))
				return;
			int j = (arrayOfString = formulaNames).length;
			for (int i = 0; i < j; ++i) {
				String formulaName = arrayOfString[i];
				for (Iterator<IFormula> localIterator = model.getDefine()
						.getFormulas().iterator(); localIterator.hasNext();) {
					IFormula formula = (IFormula) localIterator.next();
					if ((formulaName == null)
							|| (!(formulaName.equals(formula.getName()))))
						continue;

					FunctionMssageDialog.newDialog(code, billid);
					model.beginUpdate();
					model.executeFormula(formula.getName());
					model.endUpdate();
					List<DialogInfo> list = FunctionMssageDialog.getDialog(
							code, billid);
					WXMessageDialogManage.addDialogList(code, list);
					FunctionMssageDialog.destroyDialog(code, billid);
					WXMessageDialog wxmd = (WXMessageDialog) model.messageDialog;
					if (wxmd.getReturnCode() <= 0)
						continue;
					List<DialogInfo> ldi = wxmd.getListDialog(true);
					WXMessageDialogManage.addDialogList(code, ldi);
				}
			}
			ModelState modelState = model.getModelState();

			if ((!(saveAfterexecute)) || (modelState.equals(ModelState.NEW))
					|| (modelState.equals(ModelState.EDIT)))
				return;
		} finally {
			model.endUpdate();
		}
		model.endUpdate();

		((ContextSPI) model.getContext()).resolveTrans();

		model.load(GUID.valueOf(iworkItem.getProcessInstance().getGUIDRef()));
	}

	@Publish
	protected class TodoWXQYHApproval extends
			TaskMethodHandler<TodoWXApprovalTask, TodoWXApprovalTask.Method> {
		protected TodoWXQYHApproval() {
			super(TodoWXApprovalTask.Method.DoWXApproval,
					new TodoWXApprovalTask.Method[0]);
		}

		protected void handle(Context context, TodoWXApprovalTask task) {
			String workItemId;
			try {
				workItemId = task.getWorkItemId();
				String userName = task.getCode();
				String key = task.getKey();
				String actionName = task.getActionName();
				String comment = task.getComment();
				String data = task.getData();

				WXPlaintextScramble scramble = task.getScramble();
				HttpServletResponse resp = task.getResp();
				FUser fuser = (FUser) context.find(FUser.class,
						(Object) userName.toUpperCase());
				User user = (User) context.find(User.class,
						(Object) fuser.getGuid());
				context.changeLoginUser(user);
				context.getLogin().setUserCurrentOrg(fuser.getBelongedUnit());
				IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(
						(Context) context, (String) workItemId);
				if (iworkItem == null) {
					task.setErrorInfo(HintMessage
							.toError((String) "没有找到该审批项,或已被取回"));
					return;
				}
				if (iworkItem.getState() == EnumWorkItemState.COMPLETE) {
					task.setErrorInfo(HintMessage
							.toError((String) WXApprovalCommon.stateComplete(
									(Context) context, (IWorkItem) iworkItem)));
					return;
				}
				MobileBillDefine billDefine = BillCommon
						.getMobileBillDefineInfo((Context) context,
								(IWorkItem) iworkItem);
				if (billDefine == null) {
					task.setErrorPage(new WXErrorPage("错误提示",
							"未找到对应待办或未配置详情界面!", ""));
					return;
				}
				BillData billData = BillCommon.getBillData((Context) context,
						(IWorkItem) iworkItem, (MobileBillDefine) billDefine);
				if (billData == null) {
					task.setErrorPage(new WXErrorPage("错误提示", "没有到BillData!",
							""));
					return;
				}
				FApprovalDefine fad = new MT2Common().getFApprovalDefine(
						iworkItem.getGuid().toString(), context);
				try {
					boolean flags = BillCommon.judgeSuggest(
							(MobileBillDefine) billDefine,
							(IWorkItem) iworkItem, (Context) context,
							(String) userName);
					if (flags) {
						task.setErrorInfo(HintMessage
								.toError((String) "该节点已完成审批!"));
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
					task.setErrorPage(new WXErrorPage(
							"异常提示",
							"TodoWXApprovalTask.BillCommon.judgeSuggest捕获未知异常信息",
							""));
					return;
				}
				ApprovalProperties ap = BillCommon.getApprovalProperties(
						(Context) context, (IWorkItem) iworkItem);
				ApprovalPropertieInfo apab = new ApprovalPropertieInfo(context,
						iworkItem, ap);
				Button button = apab.getButton(actionName);
				Boolean suggestion = apab.getFlag(actionName);
				if (button == null) {
					task.setErrorInfo(HintMessage.toError((String) ("该节点没有配置"
							+ apab.getButtonNameZH(actionName) + "按钮,请联系管理员")));
					return;
				}
				if (!suggestion.booleanValue()
						&& StringUtil.isEmpty((String) comment)) {
					task.setErrorInfo(HintMessage.toError((String) "需要填写审批意见"));
					return;
				}
				if (comment.getBytes().length > 500) {
					task.setErrorInfo(HintMessage
							.toError((String) "审批意见，超出系统允许范围,请重新输入。"));
					return;
				}
				WXMessageDialogManage.newDialogList((String) userName);
				WXBillApproval approvalService = new WXBillApproval(key, resp,
						apab);
				BillModel billModel = BillCommon.getBillModel(context,
						iworkItem);
				billModel.load(GUID.valueOf(iworkItem.getProcessInstance()
						.getGUIDRef()));
				billModel.messageDialog = new WXMessageDialog();
				billModel.setApprovalIdea(comment);
				billModel.setApprovalProperties(ap);
				billModel.setWorkItem(iworkItem);
				billModel.setErrorMsgProcessor(new MobileBillErrorProcessor());
				billModel.setModelState(ModelState.NONE);
				approvalService.setCursor(billModel);
				try {
					approvalService.updateBill(context, billModel, BillCommon
							.getFBillDefine((Context) context,
									(IWorkItem) iworkItem), data);
					WXMessageDialog messagedialog = (WXMessageDialog) billModel.messageDialog;
					if (messagedialog.getReturnCode() > 0) {
						List<DialogInfo> ldi = messagedialog
								.getListDialog(true);
						for (DialogInfo di : ldi) {
							if (DialogInfo.DialogAlertStop.equals(di
									.getDialogtype())) {
								task.setErrorInfo(HintMessage.toJSONObject(
										(DialogInfo) di, (String) key));
								return;
							}
							WXMessageDialogManage.addDialog((String) userName,
									(DialogInfo) di);
						}
					}
				} catch (WXHintException e) {
					e.printStackTrace();
					task.setErrorPage(new WXErrorPage("异常提示",
							"TodoWXApprovalTask捕获WXHintException异常信息", ""));
					return;
				} catch (Exception e) {
					e.printStackTrace();
					task.setErrorPage(new WXErrorPage(
							"异常提示",
							"TodoWXApprovalTask.approvalService.updateBill捕获未知异常信息",
							""));
					return;
				}
				if (apab.getSaveCheck(actionName)) {
					CheckResultInfo result = billModel.checkBeforeSave();
					List<ErrorItem> errors = result.getAuditErrors();
					if (errors != null && errors.size() > 0) {
						task.setErrorInfo(HintMessage
								.toError((String) ((ErrorItem) errors.get(0))
										.getErrMsg()));
						return;
					}
					List<ErrorItem> hints = result.getAuditHints();
					if (hints != null && hints.size() > 0) {
						for (ErrorItem h : hints) {
							DialogInfo info = new DialogInfo();
							info.setDialogtype(DialogInfo.DialogConfirmNone);
							info.setTitle("");
							info.setContext(h.getErrMsg());
							info.setUser(billModel.getContext().getLogin()
									.getUser());
							info.setVerticalDepth(1);
							WXMessageDialogManage.addDialog((String) userName,
									(DialogInfo) info);
						}
					}
				}
				UserDefineButtonInfo bpr = new UserDefineButtonInfo(billModel,
						button.param, resp);
				if ((Constants.UserDefineAccept.equals(actionName) || Constants.UserDefineReject
						.equals(actionName)) && bpr.isState()) {
					if (bpr.isConfirm()) {
						DialogInfo info = new DialogInfo();
						info.setDialogtype(DialogInfo.DialogConfirmNone);
						info.setTitle("");
						info.setContext(bpr.getShowInfo());
						info.setUser(billModel.getContext().getLogin()
								.getUser());
						info.setVerticalDepth(1);
						WXMessageDialogManage.addDialog((String) userName,
								(DialogInfo) info);
					}
					TodoWXApprovalService.this.executeFormula(billModel,
							bpr.getFormuals(), bpr.isSaveAfterExecute(),
							userName, context, iworkItem);
					WXMessageDialog wxmd = (WXMessageDialog) billModel.messageDialog;
					if (wxmd.getReturnCode() > 0) {
						WXMessageDialogManage.addDialogList(userName,
								wxmd.getListDialog(true));
					}
				}
				WXApprovalData wad = new WXApprovalData(actionName, billModel,
						data, scramble, resp, context);
				wad.setUdfbi(bpr);
				wad.setSave_check(apab.getSaveCheck(actionName));
				wad.setBillDataId(billData.getBillId());
				wad.setWxbillTitle(fad != null ? fad.getTitle() : billDefine
						.getMasterPage().getReferenceTable().getTitle());
				WXApprovalDataManage.newApprovalData((String) userName,
						(WXApprovalData) wad);
				approvalService.approvalService(context);
			} catch (Exception e) {
				e.printStackTrace();
				task.setErrorPage(new WXErrorPage("异常提示",
						"TodoWXApprovalTask.Method.DoWXApproval捕获未知异常信息", ""));
			}
		}
	}

	@Publish
	protected class TodoWXQYHApprovalHint extends
			TaskMethodHandler<TodoWXApprovalTask, TodoWXApprovalTask.Method> {
		protected TodoWXQYHApprovalHint() {
			super(TodoWXApprovalTask.Method.DoWXApprovalHint,
					new TodoWXApprovalTask.Method[0]);
		}

		protected void handle(Context context, TodoWXApprovalTask task) {
			String workItemId;
			try {
				workItemId = task.getWorkItemId();
				String userName = task.getCode();
				String key = task.getKey();
				String hintId = task.getHintId();
				String userSelect = task.getUserSelect();
				HttpServletResponse resp = task.getResp();
				FUser fuser = (FUser) context.find(FUser.class,
						userName.toUpperCase());
				User user = (User) context.find(User.class, fuser.getGuid());
				context.changeLoginUser(user);
				context.getLogin().setUserCurrentOrg(fuser.getBelongedUnit());

				IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context,
						workItemId);

				if (iworkItem == null) {
					task.setErrorInfo(HintMessage.toError("没有找到该审批项,或已被取回"));
					return;
				}
				if (iworkItem.getState() == EnumWorkItemState.COMPLETE) {
					task.setErrorInfo(HintMessage.toError(WXApprovalCommon
							.stateComplete(context, iworkItem)));
					return;
				}

				MobileBillDefine billDefine = BillCommon
						.getMobileBillDefineInfo(context, iworkItem);
				if (billDefine == null) {
					task.setErrorPage(new WXErrorPage("错误提示",
							"未找到对应待办或未配置详情界面!", ""));
					return;
				}

				try {
					boolean flags = BillCommon.judgeSuggest(billDefine,
							iworkItem, context, userName);
					if (flags) {
						task.setErrorInfo(HintMessage.toError("该节点已完成审批!"));
						return;
					}
				} catch (Exception localException1) {
					task.setErrorPage(new WXErrorPage(
							"异常提示",
							"TodoWXApprovalTask.DoWXApprovalHint.BillCommon.judgeSuggest捕获未知异常信息",
							""));
				}

				/*
				 * 
				 * 
				 * try { boolean flags = BillCommon.judgeSuggest(billDefine,
				 * iworkItem, context, userName); if (!(flags)) break label226;
				 * task.setErrorInfo(HintMessage.toError("该节点已完成审批!")); return;
				 * } catch (Exception localException1) { task.setErrorPage(new
				 * WXErrorPage("异常提示",
				 * "TodoWXApprovalTask.DoWXApprovalHint.BillCommon.judgeSuggest捕获未知异常信息"
				 * , "")); return; }
				 */

				//

				WXApprovalData wxad = WXApprovalDataManage
						.getApprovalData(userName);
				if (wxad == null) {
					task.setErrorInfo(HintMessage
							.toError("微信存储信息中没有找到该单据的相关信息!"));
					return;
				}
				if (System.currentTimeMillis() - wxad.getCreatetime() > 600000L) {
					task.setErrorInfo(HintMessage.toError("已超时,请重新打开单据进行审批!"));
					return;
				}

				BillModel billModel = wxad.getBillModel();

				ApprovalPropertieInfo apab = new ApprovalPropertieInfo(context,
						iworkItem, billModel.getApprovalProperties());
				WXBillApproval approvalService = new WXBillApproval(key, resp,
						apab);

				DialogInfo currentDI = WXMessageDialogManage.getDialogById(
						userName, Integer.parseInt(hintId));
				WXMessageDialogManage.removerDialogById(userName,
						Integer.parseInt(hintId));

				if ((DialogInfo.DialogConfirmAction.equals(currentDI
						.getDialogtype()))
						|| (DialogInfo.DialogConfirmFormula.equals(currentDI
								.getDialogtype()))
						|| (DialogInfo.DialogDoAction.equals(currentDI
								.getDialogtype()))) {
					String billid = billModel.getModelData().getMaster()
							.getRECID().toString();
					FunctionMssageDialog.newDialog(userName, billid);
					if (DialogInfo.DialogConfirmAction.equals(currentDI
							.getDialogtype()))
						if (HintMessage.UserSelectYes.equals(userSelect))
							approvalService.doAction(billModel,
									currentDI.getSelectYes());
						else
							approvalService.doAction(billModel,
									currentDI.getSelectNo());

					else if (DialogInfo.DialogConfirmFormula.equals(currentDI
							.getDialogtype())) {
						if (HintMessage.UserSelectYes.equals(userSelect))
							billModel.executeFormula(currentDI.getSelectYes());
						else {
							billModel.executeFormula(currentDI.getSelectNo());
						}

					}

					WXMessageDialogManage.addDialogList(userName,
							FunctionMssageDialog.getDialog(userName, billid),
							currentDI.getVerticalDepth() + 1);
					FunctionMssageDialog.destroyDialog(userName, billid);
				}

				approvalService.approvalService(context);
			} catch (Exception e) {
				e.printStackTrace();
				task.setErrorPage(new WXErrorPage("异常提示",
						"TodoWXApprovalTask.Method.DoWXApprovalHint捕获未知异常信息",
						""));
			}
		}
	}
}