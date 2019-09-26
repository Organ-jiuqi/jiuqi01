package com.jiuqi.mt2.dna.mobile.wxapproval.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.bap.bill.common.model.BillCentre;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.bill.intf.facade.model.FBillDefine;
import com.jiuqi.dna.bap.bill.intf.model.BillConst;
import com.jiuqi.dna.bap.log.intf.task.AddLogInfoTask;
import com.jiuqi.dna.bap.workflowmanager.common.consts.WorkflowConsts;
import com.jiuqi.dna.bap.workflowmanager.common.jump.WorkflowFreeJump;
import com.jiuqi.dna.bap.workflowmanager.execute.common.ApprovalTaskManager;
import com.jiuqi.dna.bap.workflowmanager.execute.common.msgprocessor.BusinessMessageProcessor;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.WorkflowRunUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.ApprovalProperties;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.TaskListDefine;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FTaskListDefine;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.task.BaseApprovalTask;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.task.BaseApprovalTask.Method;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.invoke.AsyncTask;
import com.jiuqi.dna.core.service.AsyncInfo;
import com.jiuqi.dna.core.service.AsyncInfo.SessionMode;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.workflow.define.DefaultAction;
import com.jiuqi.dna.workflow.define.Node;
import com.jiuqi.dna.workflow.engine.EnumWorkItemState;
import com.jiuqi.dna.workflow.engine.IJump;
import com.jiuqi.dna.workflow.engine.WorkItem;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.HintMessage;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.SendMsgToWX;
import com.jiuqi.mt2.dna.service.todo.internal.ListenerGatherer;
import com.jiuqi.mt2.dna.service.todo.listener.ITodoResult;
import com.jiuqi.mt2.spi.ICallMonitor;
import com.jiuqi.mt2.spi.common2.table.impl.MField;
import com.jiuqi.mt2.spi.common2.table.impl.MFieldsList;
import com.jiuqi.mt2.spi.log.MobileLog;
import com.jiuqi.mt2.spi.todo.model.ITodoItem;
import com.jiuqi.mt2.spi.todo.model.impl.TodoItem;
import com.jiuqi.mt2.spi.todo.model.impl.TodoNode;
import com.jiuqi.xlib.INamable;
import com.jiuqi.xlib.dna.ContextManager;
import com.jiuqi.xlib.json.JSONException;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * 微信端 审批,节点向下转移
 * 
 * @author liuzihao
 */
public class WXSingleCompleteTodo {

	public static Boolean getBoolean(String o) {
		if (o == null) {
			return false;
		}
		String result = o.toString();
		if (result.equals("是") || result.endsWith("true")) {
			return true;
		} else {
			return false;
		}
	}

	protected TodoItem item;

	/**
	 * 检查是否已经选择节点、需要选择节点
	 * 
	 * @param context
	 * @param task
	 * @return 需要继续设置为false，无需继续设置为true
	 * @throws Exception
	 */
	private boolean checkNodes(Context context) throws Exception {
		BaseApprovalTask task = getTask(context);
		// 无节点
		if (item.getNodes() == null || item.getNodes().size() == 0) {
			// 需要设置节点
			if (ApprovalTaskManager.hasJumps(context, task, false)) {
				List<INamable> nodeList = ApprovalTaskManager.getJumps(context,
						task);
				MFieldsList nodes = new MFieldsList();
				for (INamable nodeNamable : nodeList) {

					TodoNode nodefield = new TodoNode();
					nodefield.setFieldName(nodeNamable.getName());
					nodefield.setStringValue(nodeNamable.getTitle());
					nodefield.setTitle(nodeNamable.getTitle());
					if (task.mp.isEnableSelectUser()) {
						IJump jump = null;
						Node node = (Node) nodeNamable;

						if (task == null)
							jump = new WorkflowFreeJump(null, node);
						else
							jump = new WorkflowFreeJump(
									task.workItem.getAdaptor(WorkItem.class),
									node);

						task.setJump(jump);

						if (task == null)
							ApprovalTaskManager.preSubmmitToWorkflow(context,
									task);
						else
							ApprovalTaskManager.preCommitToWorkflow(context,
									task, false);

						List<GUID> users = task.mp.getEnableSelectedUsers();
						nodefield.setUsers(getUsers(context, users));
					}
					nodes.add(nodefield);
				}
				item.setNodes(nodes);
				return false;
			} else if (task.mp != null && task.mp.isEnableSelectUser()) {
				MFieldsList nodes = new MFieldsList();
				TodoNode nodefield = new TodoNode();
				nodefield.setFieldName("simpleNode");
				nodefield.setStringValue("默认节点");
				List<GUID> users = task.mp.getEnableSelectedUsers();
				nodefield.setUsers(getUsers(context, users));
				nodes.add(nodefield);
				item.setNodes(nodes);
				return false;
			} else {
				return true;
			}

		} else {
			return true;
		}
	}

	private MFieldsList getUsers(Context context, List<GUID> users) {
		MFieldsList collection = new MFieldsList();
		for (GUID id : users) {
			FUser user = context.find(FUser.class, id);
			MField userField = new MField();
			userField.setFieldName(id.toString());
			userField.setStringValue(user.getTitle());
			userField.setTitle(user.getTitle());
			collection.add(userField);
		}
		return collection;
	}

	/**
	 * 执行前调用外部接口，判断是否需要终止审批或弹出警示
	 * 
	 * @param item
	 * @param task
	 * @return 终止为false，警示或通过为true
	 */
	private boolean checkConfirm(TodoItem item, BaseApprovalTask task) {
		ITodoResult result;
		if (ListenerGatherer.getiTodoListener() == null) {
			return true;
		}

		// 驳回
		if ((ITodoItem.STATE_REJECT & item.getState()) == ITodoItem.STATE_REJECT) {
			result = ListenerGatherer.getiTodoListener().rejectBefore(
					item.getId());
			// 同意
		} else if ((ITodoItem.STATE_COMMIT & item.getState()) == ITodoItem.STATE_COMMIT) {
			result = ListenerGatherer.getiTodoListener().agreeBefore(
					item.getId());
		} else {
			// 校验通过
			item.setState(item.getState() | ITodoItem.STATE_ENSURED);
			return true;
		}
		item.setAlertMsg(result.getMessage());
		// 确认过程被否决
		if ((!result.canDo()) && (!result.getConfirmEnabled())) {
			item.setAlertMsg(result.getMessage());
			item.setState(item.getState() | ITodoItem.STATE_ALERT
					| ITodoItem.STATE_FAILD_END);
			return false;
		}
		if (result.getConfirmEnabled()) {
			item.setAlertMsg(result.getMessage());
			item.setState(item.getState() | ITodoItem.STATE_ALERT);
		}
		return true;
	}

	/**
	 * 检查是否需要执行后提示
	 * 
	 * @param item
	 * @return 需要提示为true
	 */
	private boolean checkResultConfirm(TodoItem item) {
		if (ListenerGatherer.getiTodoListener() != null) {
			ITodoResult result_after = null;

			// 驳回
			if ((ITodoItem.STATE_REJECT & item.getState()) == ITodoItem.STATE_REJECT) {
				result_after = ListenerGatherer.getiTodoListener().rejectAfter(
						item.getId());
				// 同意
			} else if ((ITodoItem.STATE_COMMIT & item.getState()) == ITodoItem.STATE_COMMIT) {
				result_after = ListenerGatherer.getiTodoListener().agreeAfter(
						item.getId());
				if (result_after != null
						&& StringUtil.isNotEmpty(result_after.getMessage())) {
				}
				item.setState(ITodoItem.STATE_ALERT);
				item.setAlertMsg(result_after.getMessage());
				return true;
			}
		}
		return false;
	}

	private void checkMessage(Context context, TodoItem item) {
		FTaskListDefine fdefine = context.find(FTaskListDefine.class,
				GUID.valueOf(item.getId()));
		TaskListDefine define = fdefine.getTaskListDefine();
		ApprovalProperties ap = define.getApprovalProperties();
	}

	public float approval(Context context, ITodoItem iitem,
			HttpServletResponse resp) {
		float flag = 0;
		this.item = new TodoItem(iitem);
		BaseApprovalTask task = getTask(context);

		// 未进入工作流状态
		if ((ITodoItem.STATE_ENSURED & item.getState()) == 0) {
			if (!checkConfirm(item, task)) {
				return flag;
			}
			boolean needNodes = false;
			try {
				needNodes = checkNodes(context);
			} catch (Exception e) {
				MobileLog.logError(e);
			}
			item.setState(item.getState() | ITodoItem.STATE_ENSURED);
		} else {
			// 确认
			try {
				flag = todoExecute(context, task, true, resp);
				if ((ITodoItem.STATE_REJECT & item.getState()) == ITodoItem.STATE_REJECT) {
					item.setState(item.getState() | ITodoItem.STATE_REJECT);
				} else if ((ITodoItem.STATE_COMMIT & item.getState()) == ITodoItem.STATE_COMMIT) {
					item.setState(item.getState() | ITodoItem.STATE_COMMIT);
				}
			} catch (JSONException e) {
				MobileLog.logError(e);
				item.setState(item.getState() | ITodoItem.STATE_FAILD_END);
				item.setAlertMsg("处理工作项失败！");
			}
			item.setState(item.getState() | ITodoItem.STATE_END);
		}
		return flag;
	}

	/**
	 * 执行
	 */
	protected float todoExecute(Context context, BaseApprovalTask task,
			boolean confirmed, HttpServletResponse resp) throws JSONException {
		float flag = 0;
		if (task.workItem != null
				&& task.workItem.getState() == EnumWorkItemState.COMPLETE)
			task.workItem = WorkflowRunUtil.loadWorkItem(context, task.workItem
					.getGuid().toString());
		BaseApprovalTask bask = task.clone();
		BusinessMessageProcessor mp = ApprovalTaskManager.getBMP(task);

		if (bask.paras != null)
			bask.paras.remove(WorkflowConsts.procommit);
		bask.mp = mp;
		// 设置节点
		try {
			if (ApprovalTaskManager.hasJumps(context, task, false)) {
				Node node = new Node();
				if (item.getNodes().size() != 0) {
					node.setName(item.getNodes().get(0).getFieldName());
					node.setTitle(item.getNodes().get(0).getFieldTitle());
				}
				// 设置节点
				// IJump jump = new
				// WorkflowFreeJump(bask.workItem.getAdaptor(WorkItem.class),
				// node);
				// bask.setJump(jump);
			}
		} catch (Exception e) {
			MobileLog.logError(e);
		}
		// 设置审批人
		if (task.mp != null && task.mp.isEnableSelectUser()) {
			if (item.getNodes().size() != 0) {
				TodoNode todoNode = (TodoNode) item.getNodes().get(0);
				MFieldsList todoUsers = todoNode.getUsers();
				ArrayList<GUID> users = new ArrayList<GUID>();
				for (int i = 0; i < todoUsers.size(); i++) {
					users.add(GUID.valueOf(todoUsers.get(i).getFieldName()));
				}
				// 设置审批人
				bask.setAppointUserIDs(users);
			}
		}

		// 设置审批意见
		bask.approveMessage = item.getAlertMsg();

		MobileLog.logInfo("doComplete");

		try {
			AsyncInfo info = new AsyncInfo();
			info.setSessionMode(SessionMode.INDIVIDUAL);
			// 异步保存审批任务及节点
			AsyncTask<BaseApprovalTask, Method> a = context.asyncHandle(bask,
					BaseApprovalTask.Method.COMMIT_WORKFLOW, info);
			context.waitFor(a);
			flag = a.getProgress();
		} catch (Exception e) {
			SendMsgToWX.sendException(e, resp);
			return 0;
		}
		// context.handle(bask, BaseApprovalTask.Method.COMMIT_WORKFLOW);

		// 向日志功能发送消息，增加单据处理日志
		GUID billDefineID = GUID.valueOf(task.workItem.getWorkCategory());
		GUID billDataID = GUID.valueOf((String) task.workItem
				.getProcessInstance().getWorkRef()); // 单据数据的guid
		FBillDefine billDefine = BillCentre.findBillDefine(
				ContextManager.getCurrentContext(), billDefineID);
		BillModel model = BillCentre.createBillModel(
				ContextManager.getCurrentContext(), billDefine);
		model.load(billDataID);
		String actionTitle = "处理";
		if ((ITodoItem.STATE_REJECT & item.getState()) == ITodoItem.STATE_REJECT) {
			actionTitle = "驳回";
		} else if ((ITodoItem.STATE_COMMIT & item.getState()) == ITodoItem.STATE_COMMIT) {
			actionTitle = "同意";
		}
		if (model.isNeedSaveLog()) {
			// 系统日志
			AddLogInfoTask sysTask = new AddLogInfoTask(
					AddLogInfoTask.Constant.INFOMATION,
					model.getDefine().getBillInfo().getTitle(),
					" 微信移动终端审批" + actionTitle //$NON-NLS-1$
							+ "成功！", "单据编号为：" + model.getModelData().getMaster().getFieldValue(BillConst.f_billCode)); //$NON-NLS-1$ //$NON-NLS-2$
			context.asyncHandle(sysTask);
		}
		checkResultConfirm(item);
		return flag;
	}

	private BaseApprovalTask getTask(Context context) {
		IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context,
				item.getId());
		BaseApprovalTask task = new BaseApprovalTask(iworkItem,
				DefaultAction.ACCEPT.value(), getSuggest(item), null);
		if ((ITodoItem.STATE_REJECT & item.getState()) == ITodoItem.STATE_REJECT) {
			task.actionId = ITodoItem.STATE_REJECT;
			// 需要注意同意状态字节数最短判断优先级最低
		} else if ((ITodoItem.STATE_COMMIT & item.getState()) == ITodoItem.STATE_COMMIT) {
			task.actionId = ITodoItem.STATE_COMMIT;
		}
		if (iworkItem == null) {
			MobileLog.logInfo("无法找到指定的待办");
		}
		task.businessInstanceID = GUID.valueOf(iworkItem.getProcessInstance()
				.getGUIDRef());
		WorkItem workitem = iworkItem.getAdaptor(WorkItem.class);
		task.businessObjectID = GUID.valueOf(workitem.getWorkCategory());

		return task;
	}

	private String getSuggest(TodoItem item2) {
		String suggest = item2.getAlertMsg();
		if (StringUtil.isNotEmpty(suggest)) {
			return suggest;
		}
		if ((ITodoItem.STATE_REJECT & item.getState()) == ITodoItem.STATE_REJECT) {
			suggest = "驳回";
		} else if ((ITodoItem.STATE_COMMIT & item.getState()) == ITodoItem.STATE_COMMIT) {
			suggest = "同意";
		}
		return suggest;
	}
}
