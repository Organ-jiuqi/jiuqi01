package com.jiuqi.mt2.dna.mobile.wxapproval.noused;

import java.util.Date;
import java.util.List;

import com.jiuqi.dna.bap.bill.common.action.IWorkFlowAction;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.bill.common.workflow.actions.AfterBillAbortListener;
import com.jiuqi.dna.bap.bill.common.workflow.actions.AfterBillCommitListener;
import com.jiuqi.dna.bap.bill.common.workflow.actions.AfterBillSubmmitListener;
import com.jiuqi.dna.bap.bill.common.workflow.actions.BeforeBillCommitListener;
import com.jiuqi.dna.bap.bill.common.workflow.consts.BillWorkflowConst;
import com.jiuqi.dna.bap.bill.common.workflow.task.BillApprovalTask;
import com.jiuqi.dna.bap.bill.intf.model.BillConst;
import com.jiuqi.dna.bap.common.logger.BAPLogger;
import com.jiuqi.dna.bap.common.logger.LoggerBuilder;
import com.jiuqi.dna.bap.log.intf.task.AddLogInfoTask;
import com.jiuqi.dna.bap.log.intf.task.AddLogInfoTask.Constant;
import com.jiuqi.dna.bap.model.common.define.intf.IAction;
import com.jiuqi.dna.bap.model.common.type.ModelState;
import com.jiuqi.dna.bap.workflowmanager.common.consts.WorkflowConsts;
import com.jiuqi.dna.bap.workflowmanager.define.common.WorkflowDefineManager;
import com.jiuqi.dna.bap.workflowmanager.execute.common.ApprovalTaskManager;
import com.jiuqi.dna.bap.workflowmanager.execute.common.BusinessProcessManager;
import com.jiuqi.dna.bap.workflowmanager.execute.common.commitor.AbortListener;
import com.jiuqi.dna.bap.workflowmanager.execute.common.commitor.AfterCommitListener;
import com.jiuqi.dna.bap.workflowmanager.execute.common.commitor.BeforCommitListener;
import com.jiuqi.dna.bap.workflowmanager.execute.common.commitor.CommitCommitor;
import com.jiuqi.dna.bap.workflowmanager.execute.common.commitor.CommitOnlySelectUserCommitor;
import com.jiuqi.dna.bap.workflowmanager.execute.common.commitor.SubmmitCommitor;
import com.jiuqi.dna.bap.workflowmanager.execute.common.commitor.WorkflowCommitor;
import com.jiuqi.dna.bap.workflowmanager.execute.common.exception.WorkItemMissingException;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.WorkflowRunUtil;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.ui.wt.InfomationException;
import com.jiuqi.dna.workflow.define.DefaultAction;
import com.jiuqi.dna.workflow.engine.EnumWorkItemState;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;

public class CopyOfWXBillWorkflowUtil {
	private BillModel billModel = null;
	private boolean isSuccess = false;
	private IAction action = null;

	public CopyOfWXBillWorkflowUtil(IAction action, final BillModel billModel) {
		this.action = action;
		this.billModel = billModel;
	}

	/**
	 * 工作流动作————驳回动作
	 */
	public boolean oldworkFlow(final boolean isCheck) {
		// 提交当前界面的数据
//-----------------------------------
		billModel.submitCurrentControl();
		//BAPCommon bapComm = billModel.getContext().find(BAPCommon.class);

		boolean isSaveSucces = false;
		if (isCheck) {
			isSaveSucces = billModel.save();
		} else {
			isSaveSucces = billModel.directSave();
		}
		if (isSaveSucces) {
			isSuccess = olddoWorkFlow(isCheck);
		}
		return isSuccess;
	}
	
	/**
	 * 工作流普通————同意动作
	 * @param action
	 */
	public void onlySelectUserworkFlow() {
		// 提交当前界面的数据
//-----------------------		
		//billModel.submitCurrentControl();
		//BAPCommon bapComm = billModel.getContext().find(BAPCommon.class);
		try {
			billModel.lock();
			setWorkflowTemp();
			if (beforeWorkflowSave(action, billModel)) {
				//onlySelectUserdoWorkFlow(null, null, null);
			}
		} finally {
			billModel.unLock();
		}
	}
	
	
	
	
	
	/**
	 * 工作流动作 
	 */
	public void workFlow() {
		// 提交当前界面的数据
		// -------------------------------------
		// billModel.submitCurrentControl();
		// BAPCommon bapComm = billModel.getContext().find(BAPCommon.class);

		setWorkflowTemp();
		if (beforeWorkflowSave(action, billModel)) {
			isSuccess = doWorkFlow(action, billModel);
		}
	}

	/**
	 * 在单据做action操作前，保存单据数据。
	 * @param action
	 * @param billModel
	 * @return
	 */
	private boolean beforeWorkflowSave(IAction action, BillModel billModel) {
		if (null == billModel)
			return false;
		boolean saveSuccess = true;
		if (isNeedSave(billModel)) {
			boolean isReject = false;
			if (null != action && action.getTitle().equals(DefaultAction.REJECT.title())) {
				isReject = true;
			}
			if (isReject) {
				saveSuccess = billModel.directSave();
			} else {
			}
			saveSuccess = billModel.save();
		}
		return saveSuccess;
	}

	/**
	 * 单据数据是否需要保存
	 * @param billModel
	 * @return
	 */
	private boolean isNeedSave(BillModel billModel) {
		if (null == billModel)
			return false;
		return billModel.getModelData().getMaster().isModified();
	}

	

	/**
	 * 工作流动作,校验公式
	 * @deprecated
	 * @param action
	 */
	@Deprecated
	public boolean oldworkFlow() {
		return oldworkFlow(true);
	}

	

	/**
	 * 执行工作流动作 同意时走这个
	 */
	public boolean doWorkFlow(IAction action, BillModel billModel) {
		Context context = billModel.getContext();
		try {
			// 执行工作流动作任务
			BillApprovalTask task = new BillApprovalTask(billModel.getWorkItem(), ((IWorkFlowAction) action).getWorkFlowActionID(), billModel.getApprovalIdea(), billModel);
			task.setAction(action);
			if (null != billModel.getTemValue(BillConst.f_recver)) {
				task.setRecver((Long) billModel.getTemValue(BillConst.f_recver));
			}

			GUID billDefineGuid = billModel.getDefine().getBillInfo().getRecID();
			task.businessInstanceID = task.model.getData().getMaster().getRECID();
			task.businessObjectID = billDefineGuid;
			if (billModel.save()) {
				if (billModel.getWorkItem() != null) {
					if (billModel.getUI() == null) {
						if (ApprovalTaskManager.hasJumps(context, task, false)) {
							throw new InfomationException("");
						}

						if (ApprovalTaskManager.canSelectUser(context, task, false)) {
							throw new InfomationException("");
						}
						// 在回滚之后，BillMode中的版本号(用于保存时检查数据)没有回滚，再次保存的时候，两次数据的版本号不一致会报错，故在回滚后，需要将版本
						// 后重新设置一下
						// 所以在调用该方法前需要将版本号设置在billmode中
						billModel.getModelData().getMaster().setFieldValue(BillConst.f_recver, billModel.getTemValue(BillConst.f_recver));

						billModel.save();
						ApprovalTaskManager.approveTask(billModel.getContext(), task, false);
						AfterBillCommitListener listenr = new AfterBillCommitListener(billModel, action);
						if (WorkflowDefineManager.getBindingUIType(task.workItem.getActiveNode()) == 1) {
							listenr.setSubmit(true);
						}
						listenr.doListener();
					} else {
						CommitCommitor commitor = new CommitCommitor(billModel.getUI(), billModel.getUI().getContext(), task);
						commitor.addAfterCommitListener(new AfterBillCommitListener(billModel, action));
						commitor.addBeforCommitListener(new BeforeBillCommitListener(billModel, action));
						commitor.addAbortListener(new AfterBillAbortListener(billModel, action));
						ApprovalTaskManager.approvalByCommitor(commitor);

					}
				}
			}
		}catch (Exception ex) {
			BAPLogger logger = LoggerBuilder.getLogger(LoggerBuilder.BAP_MODULE, LoggerBuilder.BILL_BUSINESS);
			logger.logError(null, "执行工作流时发生异常：", ex);
		}
		if (billModel.isNeedSaveLog()) {
			// 系统日志
			AddLogInfoTask sysTask = new AddLogInfoTask(Constant.INFOMATION, billModel.getDefine().getBillInfo().getTitle(), "审批" + action.getTitle() //$NON-NLS-1$
					+ "成功！", "单据编号为：" + billModel.getModelData().getMaster().getFieldValue(BillConst.f_billCode)); //$NON-NLS-1$ //$NON-NLS-2$
			context.asyncHandle(sysTask);
		}
		return true;
	}

	public void setWorkflowTemp() {
		long recver = billModel.getModelData().getMaster().getRECVER();
		billModel.setTemValue(BillConst.f_recver, recver);
	}

	public static boolean isEnableSubmit(BillModel model) {
		if (model.getModelState() == ModelState.NEW || model.getModelState() == ModelState.EDIT) {
			return false;
		}
		int workflowState = model.getModelData().getMaster().getValueAsInt(BillConst.f_workflowState);

		boolean isConmmit = false;
		GUID dataID = model.getModelData().getMaster().getRECID();
		IWorkItem workItem = BusinessProcessManager.getFirstWorkItem(model.getContext(), dataID);

		if (workItem != null && workItem.getState() == EnumWorkItemState.ACTIVE) {
			if (!WorkflowRunUtil.isCurrentWorkItemUser(model.getContext(), workItem, model.getContext().getLogin().getUser().getID())) {
				return false;
			}
			isConmmit = true;
		}
		return ((BillConst.WORKFLOWSTATE_NONE == workflowState || BillConst.WORKFLOWSTATE_BACK_COMMIT == workflowState || (isConmmit && BillConst.WORKFLOWSTATE_PROGRESS == workflowState)));
	}

	public boolean olddoWorkFlow(boolean isCheck) {
		boolean flag=false;
		try {
			// 执行工作流动作任务
			BillApprovalTask task = new BillApprovalTask(this.billModel.getWorkItem(), ((IWorkFlowAction) action).getWorkFlowActionID(), this.billModel.getApprovalIdea(), this.billModel);
			task.setAction(action);
			if (null != billModel.getTemValue(BillConst.f_recver)) {
				task.setRecver((Long) billModel.getTemValue(BillConst.f_recver));
			}

			GUID billDefineGuid = this.billModel.getDefine().getBillInfo().getRecID();
			task.businessInstanceID = task.model.getData().getMaster().getRECID();
			task.businessObjectID = billDefineGuid;
			boolean isSaveSucces = false;
			if (billModel.dataHasChanged()) {
				if (isCheck) {
					isSaveSucces = billModel.save();
				} else {
					isSaveSucces = billModel.directSave();
				}
			} else {
				isSaveSucces = true;
			}
			if (isSaveSucces) {
				if (this.billModel.getWorkItem() != null) {
					try {
						this.billModel.lock();
						if (ApprovalTaskManager.approveTask(getContext(), task, false)) {
							AfterBillCommitListener listenr = new AfterBillCommitListener(billModel, action);
							if (WorkflowDefineManager.getBindingUIType(task.workItem.getActiveNode()) == 1) {
								listenr.setSubmit(true);
							}
							listenr.doListener();
							this.billModel.beginUpdate();
							this.billModel.endUpdate();
						}
					} finally {
						this.billModel.unLock();
					}
					flag= true;
				} 
			}
			flag= false;
		} catch (WorkItemMissingException e) {
			BAPLogger logger = LoggerBuilder.getLogger(LoggerBuilder.BAP_MODULE, LoggerBuilder.BILL_BUSINESS);
			logger.logError(null, "工作流项缺失异常：", e);
		} catch (Exception ex) {
			BAPLogger logger = LoggerBuilder.getLogger(LoggerBuilder.BAP_MODULE, LoggerBuilder.BILL_BUSINESS);
			logger.logError(null, "执行工作流时发生异常：", ex);
		} finally {
			if (billModel.isNeedSaveLog()) {
				// 系统日志
				AddLogInfoTask sysTask = new AddLogInfoTask(Constant.INFOMATION, this.billModel.getDefine().getBillInfo().getTitle(), "审批" + action.getTitle() //$NON-NLS-1$
						+ "成功！", "单据编号为：" + billModel.getModelData().getMaster().getFieldValue(BillConst.f_billCode)); //$NON-NLS-1$ //$NON-NLS-2$
				getContext().asyncHandle(sysTask);
			}
		}
		return flag;
	}

	/**
	 * 执行工作流动作
	 * 
	 * @deprecated
	 */
	@Deprecated
	public boolean olddoWorkFlow() {
		return olddoWorkFlow(true);
	}

	/**
	 * 执行工作流动作
	 * 
	 */
	public void onlySelectUserdoWorkFlow(List<BeforCommitListener> beforListeners, List<AfterCommitListener> afterListeners, List<AbortListener> abortListeners) {
		try {
			// 执行工作流动作任务
			BillApprovalTask task = new BillApprovalTask(this.billModel.getWorkItem(), ((IWorkFlowAction) action).getWorkFlowActionID(), this.billModel.getApprovalIdea(), this.billModel);
			task.setAction(action);
			if (null != billModel.getTemValue(BillConst.f_recver)) {
				task.setRecver((Long) billModel.getTemValue(BillConst.f_recver));
			}

			GUID billDefineGuid = this.billModel.getDefine().getBillInfo().getRecID();
			task.businessInstanceID = task.model.getData().getMaster().getRECID();
			task.businessObjectID = billDefineGuid;
			if (WorkflowRunUtil.isFreeFlow(this.billModel.getWorkItem())) {
				ApprovalTaskManager.preCommitToWorkflowOnlyJudeSelectUser(getContext(), task, false);
				doAfterAbort(abortListeners);
				// 在回滚之后，BillMode中的版本号(用于保存时检查数据)没有回滚，再次保存的时候，两次数据的版本号不一致会报错，故在回滚后，需要将版本
				// 后重新设置一下
				// 所以在调用该方法前需要将版本号设置在billmode中
				this.billModel.getModelData().getMaster().setFieldValue(BillConst.f_recver, billModel.getTemValue(BillConst.f_recver));
				boolean isCanSelectUser = task.mp.isEnableSelectUser();
				boolean saveSuccess = true;
				if (isNeedSave(billModel)) {
					saveSuccess = this.billModel.save();
				}
				if (saveSuccess) {
					CommitOnlySelectUserCommitor commitor = new CommitOnlySelectUserCommitor(billModel.getUI(), billModel.getUI().getContext(), task);
					AfterBillCommitListener listenr = new AfterBillCommitListener(billModel, action);
					if (WorkflowDefineManager.getBindingUIType(task.workItem.getActiveNode()) == 1) {
						listenr.setSubmit(true);
					}
					commitor.addBeforCommitListener(new BeforeBillCommitListener(billModel, action));
					commitor.addAfterCommitListener(listenr);
					commitor.addAbortListener(new AfterBillAbortListener(billModel, action));
					addListener(beforListeners, afterListeners, abortListeners, commitor);
					ApprovalTaskManager.approvalByCommitorOnlySelectUser(commitor);
				}
			} else {
				AfterBillCommitListener listenr = new AfterBillCommitListener(billModel, action);
				if (WorkflowDefineManager.getBindingUIType(task.workItem.getActiveNode()) == 1) {
					listenr.setSubmit(true);
				}
				ApprovalTaskManager.approveTask(getContext(), task, false);
				listenr.doListener();
				// 在工作流过程中，在转移线以及自动任务上可能更改字段的值，故此处判断一下是否更改了值，如果更改了，则调用update，否则，值不能保存上
				if (isNeedSave(billModel)) {
					this.billModel.update();
				}
			}
		} catch (WorkItemMissingException e) {
			BAPLogger logger = LoggerBuilder.getLogger(LoggerBuilder.BAP_MODULE, LoggerBuilder.BILL_BUSINESS);
			logger.logError(null, "工作流项缺失异常：", e);
		} catch (Exception ex) {
			BAPLogger logger = LoggerBuilder.getLogger(LoggerBuilder.BAP_MODULE, LoggerBuilder.BILL_BUSINESS);
			logger.logError(null, "执行工作流时发生异常：", ex);
		} finally {
			if (billModel.isNeedSaveLog()) {
				AddLogInfoTask sysTask = new AddLogInfoTask(Constant.INFOMATION, this.billModel.getDefine().getBillInfo().getTitle(), "审批" + action.getTitle() //$NON-NLS-1$
						+ "成功！", "单据编号为：" + billModel.getModelData().getMaster().getFieldValue(BillConst.f_billCode)); //$NON-NLS-1$ //$NON-NLS-2$
				getContext().asyncHandle(sysTask);
			}
		}
	}

	private void doAfterCommit(List<AfterCommitListener> afterListeners) {
		if (null != afterListeners && afterListeners.size() > 0) {
			for (AfterCommitListener listener : afterListeners) {
				if (listener != null) {
					listener.doListener();
				}
			}
		}
	}

	private void doAfterAbort(List<AbortListener> abortListeners) {
		if (null != abortListeners && abortListeners.size() > 0) {
			for (AbortListener listener : abortListeners) {
				if (listener != null) {
					listener.doListener();
				}
			}
		}
	}

	private void addListener(List<BeforCommitListener> beforListeners, List<AfterCommitListener> afterListeners, List<AbortListener> abortListeners, WorkflowCommitor commitor) {
		if (beforListeners != null && beforListeners.size() > 0) {
			for (BeforCommitListener listener : beforListeners) {
				if (null != listener) {
					commitor.addBeforCommitListener(listener);
				}
			}
		}
		if (afterListeners != null && afterListeners.size() > 0) {
			for (AfterCommitListener listener : afterListeners) {
				if (null != listener) {
					commitor.addAfterCommitListener(listener);
				}
			}
		}
		if (beforListeners != null && beforListeners.size() > 0) {
			for (AbortListener listener : abortListeners) {
				if (null != listener) {
					commitor.addAbortListener(listener);
				}
			}
		}
	}
	protected String getSysLogBillName() {
		if (billModel.getModelData().getMaster().getTable().find(BillConst.f_billCode) == null) {
			return "(单据ID：" + billModel.getModelData().getMaster().getRECID() + ")"; //$NON-NLS-1$
		}
		return "(单据编号：" //$NON-NLS-1$
				+ billModel.getModelData().getMaster().getValueAsString(BillConst.f_billCode) + ")";
	}

	/**
	 * 
	 * @param workItem
	 * @throws Exception
	 *             void
	 */
	private void approvalWorkflow(IWorkItem workItem, BillApprovalTask task, boolean isNeedPreExecute) throws Exception {
		task.actionId = 3;
		task.workItem = workItem;
		task.setSubmit(true);
		setWorkflowTemp();
		task.setRecver(this.billModel.getModelData().getMaster().getRECVER());
		if (isNeedPreExecute) {
			if (this.billModel.getUI() == null) {
				// ApprovalTaskManager.preCommitToWorkflow(getContext(), task,
				// false);
				// boolean isCanSelectUser = task.mp.isEnableSelectUser();
				// boolean isEnableJump = task.mp.getJumpNodes().size()>0;
				if (ApprovalTaskManager.hasJumps(getContext(), task, false) || ApprovalTaskManager.canSelectUser(getContext(), task, false)) {
					throw new InfomationException("");
				}
				// 在预执行前设置预执行参数
				if (null != task.workItem) {
					task.workItem.getProcessInstance().setParam(WorkflowConsts.procommit, WorkflowConsts.procommit, true);
				}
				if (ApprovalTaskManager.approveTask(getContext(), task, false)) {
					AfterBillCommitListener listenr = new AfterBillCommitListener(billModel, action);
					listenr.setSubmit(true);
					listenr.doListener();
				}
			} else {
				CommitCommitor commitor = new CommitCommitor(billModel.getUI(), billModel.getUI().getContext(), task);
				AfterBillCommitListener listenr = new AfterBillCommitListener(billModel, action);
				listenr.setSubmit(true);
				commitor.addAfterCommitListener(listenr);

				ApprovalTaskManager.approvalByCommitor(commitor);
			}
		} else {
			// 在正式执行前，将预执行参数设置为null
			if (null != task.workItem) {
				task.workItem.getProcessInstance().setParam(WorkflowConsts.procommit, null, true);
			}
			if (ApprovalTaskManager.approveTask(getContext(), task, false)) {
				AfterBillCommitListener listenr = new AfterBillCommitListener(billModel, action);
				listenr.setSubmit(true);
				listenr.doListener();
			}
		}
	}

	/**
	 * 
	 * @param task
	 * @throws Exception
	 *             void
	 */
	private void subWorkflow(BillApprovalTask task, boolean isNeedPreExecute) throws Exception {
		task.actionId = 3;
		task.setRecver(this.billModel.getModelData().getMaster().getRECVER());
		task.setSubmit(true);
		setWorkflowTemp();
		if (isNeedPreExecute) {
			if (null == billModel.getUI()) {
				if (ApprovalTaskManager.hasJumps(getContext(), task, false) || ApprovalTaskManager.canSelectUser(getContext(), task, false)) {
					throw new InfomationException("该单据配置的工作流中下个工作节点需要选择节点，请打开单据页面进行操作！"); //$NON-NLS-1$
				}
				if (ApprovalTaskManager.canSelectUser(getContext(), task, false)) {
					throw new InfomationException("该单据配置的工作流中下个工作节点需要选择审批人，请打开单据页面进行操作！"); //$NON-NLS-1$
				}
				// 在预执行前在task中设置预执行的参数，不能在doSubmit()方法中增加，因为正式执行也会调用该方法
				if (null != task.paras) {
					task.paras.put(WorkflowConsts.procommit, WorkflowConsts.procommit);
				}
				doSubmit(task);
			} else {
				SubmmitCommitor submmitor = new SubmmitCommitor(billModel.getUI(), billModel.getUI().getContext(), task);
				submmitor.addAfterCommitListener(new AfterBillSubmmitListener(billModel, action));
				submmitor.addBeforCommitListener(new BeforeBillCommitListener(billModel, action));
				ApprovalTaskManager.submitByCommitor(submmitor);
			}
		} else {
			// 在正式执行前在task中移除预执行的参数
			if (null != task.paras) {
				task.paras.remove(WorkflowConsts.procommit);
			}
			doSubmit(task);
		}

	}

	private void doSubmit(BillApprovalTask task) throws Exception {
		ApprovalTaskManager.submitTask(getContext(), task);
		billModel.getData().getMaster().setFieldValue(BillWorkflowConst.f_SUMBITTIME, new Date());
		billModel.getData().getMaster().setFieldValue(BillWorkflowConst.f_SUMBITUSER, billModel.getCurrentUserTitle(billModel.getContext()));
		billModel.getData().getMaster().setFieldValue(BillWorkflowConst.f_SUMBITUNIT, billModel.getCurrentUnitID(billModel.getContext(), billModel));
		// 将单据审批状态改为进入工作流
		int workflowState = Integer.valueOf(String.valueOf(billModel.getData().getMaster().getFieldValue(BillConst.f_workflowState)));
		// 如果流程是提交-》自动任务-》结束，则会在自动任务中修改workflowstate的值，故先先判断一下，否则会被覆盖
		if (workflowState == BillConst.WORKFLOWSTATE_NONE || workflowState == BillConst.WORKFLOWSTATE_BACK_COMMIT)
			billModel.getData().getMaster().setFieldValue(BillConst.f_workflowState, BillConst.WORKFLOWSTATE_PROGRESS);

		billModel.update();
		billModel.setModelState(ModelState.BROWSE);
	}

	private Context getContext() {
		return this.billModel.getContext();
	}



	
}
