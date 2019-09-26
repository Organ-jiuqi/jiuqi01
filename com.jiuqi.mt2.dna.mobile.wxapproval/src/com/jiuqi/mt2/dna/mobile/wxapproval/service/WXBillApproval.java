package com.jiuqi.mt2.dna.mobile.wxapproval.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.bill.intf.facade.model.FBillDefine;
import com.jiuqi.dna.bap.bill.intf.model.BillConst;
import com.jiuqi.dna.bap.common.logger.BAPLogger;
import com.jiuqi.dna.bap.common.logger.LoggerBuilder;
import com.jiuqi.dna.bap.model.common.define.base.BusinessObject;
import com.jiuqi.dna.bap.model.common.define.intf.IField;
import com.jiuqi.dna.bap.model.common.define.intf.ITable;
import com.jiuqi.dna.bap.model.common.runtime.base.BusinessModel;
import com.jiuqi.dna.bap.model.common.type.ExecuteMode;
import com.jiuqi.dna.bap.model.common.type.FieldType;
import com.jiuqi.dna.bap.model.common.type.FormulaType;
import com.jiuqi.dna.bap.model.common.type.ModelState;
import com.jiuqi.dna.bap.model.common.type.UIStyle;
import com.jiuqi.dna.bap.model.common.util.FieldUtil;
import com.jiuqi.dna.bap.model.common.util.ModelUtil;
import com.jiuqi.dna.bap.workflowmanager.common.consts.BusinessObjectConsts;
import com.jiuqi.dna.bap.workflowmanager.define.intf.facade.FBusinessInstanceAndWorkItem;
import com.jiuqi.dna.bap.workflowmanager.execute.common.msgprocessor.BusinessMessageProcessor;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.WorkflowRunUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.task.BaseApprovalTask;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.da.DBCommand;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.workflow.engine.EnumProcessState;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.qiyehao.util.QiYEHAOUtil;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.ApprovalPropertieInfo;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.BillCommon;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXApprovalData;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXApprovalDataManage;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXPlaintextScramble;
import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;
import com.jiuqi.mt2.dna.mobile.wxapproval.exception.WXHintException;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.DialogInfo;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.WXMessageDialog;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.WXMessageDialogManage;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.HintMessage;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.SendMsgToWX;
import com.jiuqi.mt2.spi.common2.table.impl.MFieldsList;
import com.jiuqi.mt2.spi.todo.model.ITodoItem;
import com.jiuqi.mt2.spi.todo.model.impl.TodoItem;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * ����ҵ����
 * 
 * @author liuzihao
 */
public class WXBillApproval {
	private HttpServletResponse resp;
	private WXPlaintextScramble scramble;
	private ApprovalPropertieInfo wxap;

	public WXBillApproval(String key, HttpServletResponse resp,
			ApprovalPropertieInfo wxap) {
		this.scramble = new WXPlaintextScramble(key);
		this.resp = resp;
		this.wxap = wxap;
	}

	/**
	 * ������ҵ���߼�
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public void approvalService(Context context) {
		WXApprovalData apldata = WXApprovalDataManage.getApprovalData(scramble
				.getCode());
		BillModel billModel = apldata.getBillModel();
		String actionName = apldata.getAction();

		// ѭ��������ʾ
		int dinub = WXMessageDialogManage.getDialogNumber(scramble.getCode());
		if (dinub > 0) {
			for (int i = 0; i < dinub; i++) {
				// ѡ�������� id��С��DialogInfo
				DialogInfo fundi = WXMessageDialogManage
						.getBasetSuitedDialog(scramble.getCode());
				if (fundi != null) {
					if (DialogInfo.DialogAlertStop
							.equals(fundi.getDialogtype())
							|| DialogInfo.DialogAlertRun.equals(fundi
									.getDialogtype())) {
						SendMsgToWX.sendHint(
								resp,
								HintMessage.toJSONObject(fundi,
										scramble.getResult()));
						return;
					} else if (DialogInfo.DialogConfirmNone.equals(fundi
							.getDialogtype())
							|| DialogInfo.DialogConfirmAction.equals(fundi
									.getDialogtype())
							|| DialogInfo.DialogConfirmFormula.equals(fundi
									.getDialogtype())) {
						SendMsgToWX.sendHint(
								resp,
								HintMessage.toJSONObject(fundi,
										scramble.getResult()));
						return;
					} else if (DialogInfo.DialogDoAction.equals(fundi
							.getDialogtype())) {
						doAction(apldata.getBillModel(), fundi.getActionname());
						WXMessageDialogManage.removerDialogById(
								scramble.getCode(), fundi.getId());
					}
				}
			}
		}

		/**
		 * ----ִ�б���---- ��ͨͬ�⣺ִ�б��棻 ��ͨ���أ����ڷ����仯���ֶ� ���� ��ť��ѡ�˱����߼���
		 * �Զ���ͬ��Ͳ��أ�����ִ��������ʽ���Ҵ���ִ�б��湫ʽ���Ҵ���ִ�к󱣴����� ��
		 */
		try {
			if (Constants.ACCEPT.equals(actionName)) {
				executeSaveBill(context, billModel, actionName,
						apldata.getData());
			} else if (Constants.REJECT.equals(actionName)) {
				if (judgeFiledData(apldata.getData()) && apldata.isSave_check()) {
					executeSaveBill(context, billModel, actionName,
							apldata.getData());
				}
			} else {// �Զ��尴ť
				if (WXApprovalDataManage.getApprovalData(scramble.getCode())
						.getExecuteApproval() > 0) {
					if (judgeFiledData(apldata.getData())
							|| apldata.getUdfbi().isSaveAfterExecute()
							|| WXApprovalDataManage.getApprovalData(
									scramble.getCode()).getExecuteSave() > 0) {
						executeSaveBill(context, billModel, actionName,
								apldata.getData());
					}
				} else {// û��ִ������
					SendMsgToWX.sendHint(resp,
							HintMessage.toError("�Զ��尴ťû������������ʽ", ""));
					return;
				}
			}
		} catch (Exception e) {
			SendMsgToWX.sendException(e, resp);
			return;
		}

		/**
		 * ��ȡbillModel����Ĵ�����Ϣ������ڴ���ֱ�ӵ���
		 */
		WXMessageDialog wxmd = (WXMessageDialog) billModel.messageDialog;
		if (wxmd.getReturnCode() > 0) {
			List<DialogInfo> ldi = wxmd.getListDialog(true);
			for (DialogInfo di : ldi) {
				if (DialogInfo.DialogAlertStop.equals(di.getDialogtype())) {
					SendMsgToWX.sendHint(resp,
							HintMessage.toJSONObject(di, scramble.getResult()));
					return;
				} else if (DialogInfo.DialogConfirmNone.equals(di
						.getDialogtype())) {
					SendMsgToWX.sendHint(resp,
							HintMessage.toJSONObject(di, scramble.getResult()));
					return;
				}
			}
		}

		/**
		 * ִ������ ��ͨͬ��Ͳ��أ�ֱ��ִ�������� �Զ���ͬ��Ͳ��أ�����ִ�������Ĺ�ʽ��
		 */
		HttpServletResponse resp = apldata.getResp();
		String billDataId = apldata.getBillDataId();
		if (Constants.ACCEPT.equals(actionName)
				|| Constants.REJECT.equals(actionName)) {
			if (approvalBill(context, billModel, actionName, resp, billDataId) == 1) {
				SendMsgToWX.sendHint(resp, HintMessage.toSuccess());
			} else {
				SendMsgToWX.sendHint(resp,
						HintMessage.toError("����������û�гɹ�������", ""));
			}
			;
		} else if (Constants.UserDefineAccept.equals(actionName)
				|| Constants.UserDefineReject.equals(actionName)) {
			if (apldata.getExecuteApproval() > 0) {
				if (approvalBill(context, billModel, actionName, resp,
						billDataId) == 1) {
					SendMsgToWX.sendHint(resp, HintMessage.toSuccess());
				} else {
					SendMsgToWX.sendHint(resp,
							HintMessage.toError("����������û�гɹ�������", ""));
				}
				;
			} else {
				SendMsgToWX.sendHint(resp,
						HintMessage.toError("��ʽû������ִ������", ""));
			}
		}

		/**
		 * ���Context�ͻ����ͷ���Դ
		 */
		WXMessageDialogManage.destroyDialogList(scramble.getCode());
		WXApprovalDataManage.destroyApprovalData(scramble.getCode());
	}

	/**
	 * ִ�б���
	 */
	public void executeSaveBill(Context context, BillModel billModel,
			String actionName, String data) throws WXHintException {
		IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context,
				scramble.getWorkitemid());
		updateBill(context, billModel,
				BillCommon.getFBillDefine(context, iworkItem), data);
		billModel.beginUpdate();
		// ͬ��ʱ�򱣴�����
		if (Constants.REJECT.equals(actionName)
				|| Constants.UserDefineReject.equals(actionName)) {
			if (StringUtil.isNotEmpty(billModel.getApprovalIdea())) {
				billModel.setApprovalIdea(billModel.getApprovalIdea());
			} else {
				billModel.setApprovalIdea("����");
			}
		} else if (Constants.ACCEPT.equals(actionName)
				|| Constants.UserDefineAccept.equals(actionName)) {
			if (StringUtil.isNotEmpty(billModel.getApprovalIdea())) {
				billModel.setApprovalIdea(billModel.getApprovalIdea());
			} else {
				billModel.setApprovalIdea("ͬ��");
			}
		}
		ModelState modelState = billModel.getModelState();
		if (!(modelState.equals(ModelState.NEW) || modelState
				.equals(ModelState.EDIT))) {
			billModel.setModelState(ModelState.EDIT);
		}
		billModel.save();
		billModel.endUpdate();
		/**
		 * �ύ����
		 */
		((ContextSPI) billModel.getContext()).resolveTrans();
	}

	/**
	 * ִ������
	 * 
	 * @return 0 ���ɹ���1�ɹ���2������ʾ
	 */
	public float approvalBill(Context context, BillModel billModel,
			String actionName, HttpServletResponse resp, String billDataId) {
		// �������ڵ�����ת��
		TodoItem item = new TodoItem();
		item.setId(scramble.getWorkitemid());
		item.setAlertMsg(billModel.getApprovalIdea());
		item.setBillDataID(billDataId);
		item.setBillDefineID("");
		MFieldsList fieldsList = new MFieldsList();
		item.setNodes(fieldsList);
		if (Constants.ACCEPT.equals(actionName)
				|| Constants.UserDefineAccept.equals(actionName)) {
			item.setState(ITodoItem.STATE_COMMIT | ITodoItem.STATE_ENSURED);
		} else if (Constants.REJECT.equals(actionName)
				|| Constants.UserDefineReject.equals(actionName)) {
			item.setState(ITodoItem.STATE_REJECT | ITodoItem.STATE_ENSURED);
		}
		// �������ڵ������ƶ�
		float flog = new WXSingleCompleteTodo().approval(context, item, resp);
		if (flog == 1) {
			pushMsg(context);
		}
		return flog;
	}

	/**
	 * ���ݱ�����ʱ ΢�ŷ�����ʾ��Ϣ
	 * 
	 * @param context
	 */
	@SuppressWarnings("restriction")
	private void pushMsg(Context context) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String currentTime = format
		// .format(new Date(System.currentTimeMillis()));
		WXApprovalData apldata = WXApprovalDataManage.getApprovalData(scramble
				.getCode());
		// ���ݴ�����
		String createUserId = apldata.getBillModel().getModelData().getMaster()
				.getValueAsString(BillConst.f_createUserID);
		// �ύ����
		String submitTime = format.format(apldata.getBillModel().getModelData()
				.getMaster().getValueAsDate("SUBMITTIME"));
		FUser createUser = null;
		if (context == null) {
			context = apldata.getContext();
		}
		createUser = context.find(FUser.class, GUID.valueOf(createUserId));

		IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context,
				apldata.getWorkitemid());

		WXPlaintextScramble scramble1 = new WXPlaintextScramble(
				createUser.getName(), iworkItem.getGuid().toString());

		if (Constants.REJECT.equals(apldata.getAction())
				|| Constants.UserDefineReject.equals(apldata.getAction())) {
			StringBuffer msgBuilder = new StringBuffer().append("���� ")
					.append(submitTime).append(" �ύ�ġ�")
					.append(apldata.getWxbillTitle()).append("��������")
					.append("\n").append("<a href='")
					.append(Constants.UrlApprovedBill).append("?key=")
					.append(scramble1.getResult()).append("'>����˴����в鿴...</a>");
			QiYEHAOUtil
					.sendMessage(createUser.getName(), msgBuilder.toString());
		} else if (Constants.ACCEPT.equals(apldata.getAction())
				|| Constants.UserDefineAccept.equals(apldata.getAction())) {

			BillModel billmodel = apldata.getBillModel();
			billmodel.load(GUID.valueOf(iworkItem.getProcessInstance()
					.getGUIDRef()));
			Object o = billmodel.getData().getMaster()
					.getFieldValue(BillConst.f_workflowState);
			if (Integer.parseInt(o.toString()) == BusinessObjectConsts.WORKFLOWSTATE_DONE) {
				StringBuffer msgBuilder = new StringBuffer().append("���� ")
						.append(submitTime).append(" �ύ�ġ�")
						.append(apldata.getWxbillTitle()).append("����ͨ�����")
						.append("\n").append("<a href='")
						.append(Constants.UrlApprovedBill).append("?key=")
						.append(scramble1.getResult())
						.append("'>����˴����в鿴...</a>");

				QiYEHAOUtil.sendMessage(createUser.getName(),
						msgBuilder.toString());
			}

		}
	}

	/**
	 * �ж��ֶ���Ϣ�Ƿ����˱仯
	 * 
	 * @param data
	 * @return
	 */
	public boolean judgeFiledData(String data) {
		if (StringUtil.isNotEmpty(data)) {
			if (data.indexOf(";.,;") > -1 && data.indexOf(";,.;") > -1
					&& data.indexOf(";..;") > -1) {
				if (data.length() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	private void getIField(BillModel billModel, FBillDefine fbilldefine,
			BusinessObject ooo, String da) throws WXHintException {
		String da_tname = da.substring(0, da.indexOf("."));
		String da_name = da.substring(da.indexOf(".") + 1, da.indexOf(";.,;"));
		String recid = da.substring(da.indexOf(";.,;") + 4, da.indexOf(";,.;"));
		String da_value = da.substring(da.indexOf(";,.;") + 4,
				da.indexOf(";..;"));
		String da_type = da.substring(da.indexOf(";..;") + 4, da.length());

		for (ITable table : fbilldefine.getDetailTables()) {
			if (da_tname.equals(table.getName())) {
				for (IField field : table.getFields()) {
					if (da_name.equals(field.getName())) {
						if (field.getType() == FieldType.STRING) {
							DataType type = field.getField().getType();
							if (!(type instanceof SequenceDataType)) {
								continue;
							}
							int maxLength = ((SequenceDataType) type)
									.getMaxLength();
							if (da_value.length() < maxLength) {
								ooo.setFieldValue(da_name, da_value);
								return;
							} else {
								throw new WXHintException("(��:"
										+ table.getTitle() + ")(��:"
										+ field.getTitle() + ")(ֵ:" + da_value
										+ ")��������󳤶ȣ�", null);
							}
						} else if (field.getType() == FieldType.INT
								|| field.getType() == FieldType.NUMERIC
								|| field.getType() == FieldType.LONG) {
							if (field.getType() == FieldType.INT) {
								if (FieldUtil.getFieldMaxValue(field) < Integer
										.parseInt(da_value.replaceAll(",", ""))) {
									throw new WXHintException(
											"(��:"
													+ table.getTitle()
													+ ")(��:"
													+ field.getTitle()
													+ ")(ֵ:"
													+ da_value
													+ ")¼��ֵӦС�� "
													+ FieldUtil.getFieldMaxValueText(field)
													+ " ��", null);
								} else {
									try {
										int value = Integer.parseInt(da_value
												.replaceAll(",", ""));
										ooo.setFieldValue(da_name, value);
									} catch (Exception e) {
										throw new WXHintException("(��:"
												+ da_tname + ")(��:" + da_name
												+ ")(ֵ:" + da_value + ")Ӧ��Ϊ����",
												e.getStackTrace());
									}
								}
							} else if (field.getType() == FieldType.NUMERIC) {
								if (FieldUtil.getFieldMaxValue(field) < Double
										.parseDouble(da_value.replaceAll(",",
												""))) {
									throw new WXHintException(
											"(��:"
													+ table.getTitle()
													+ ")(��:"
													+ field.getTitle()
													+ ")(ֵ:"
													+ da_value
													+ ")¼��ֵӦС�� "
													+ FieldUtil.getFieldMaxValueText(field)
													+ " ��", null);
								} else {
									try {
										double value = Double
												.parseDouble(da_value
														.replaceAll(",", ""));
										ooo.setFieldValue(da_name, value);
									} catch (Exception e) {
										throw new WXHintException("(��:"
												+ da_tname + ")(��:" + da_name
												+ ")(ֵ:" + da_value + ")Ӧ��Ϊ����",
												e.getStackTrace());
									}
								}
							} else if (field.getType() == FieldType.LONG) {
								if (FieldUtil.getFieldMaxValue(field) < Long
										.parseLong(da_value.replaceAll(",", ""))) {
									throw new WXHintException(
											"(��:"
													+ table.getTitle()
													+ ")(��:"
													+ field.getTitle()
													+ ")(ֵ:"
													+ da_value
													+ ")¼��ֵӦС�� "
													+ FieldUtil.getFieldMaxValueText(field)
													+ " ��", null);
								} else {
									try {
										long value = Long.parseLong(da_value
												.replaceAll(",", ""));
										ooo.setFieldValue(da_name, value);
									} catch (Exception e) {
										throw new WXHintException("(��:"
												+ da_tname + ")(��:" + da_name
												+ ")(ֵ:" + da_value + ")Ӧ��Ϊ����",
												e.getStackTrace());
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ��ȡ������ʷ��¼�б�
	 * 
	 * @param context
	 * @param task
	 * @return
	 */
	public static List<FBusinessInstanceAndWorkItem> getFBApprovalHistoryList(
			Context context, BaseApprovalTask task) {
		BusinessMessageProcessor mp = new BusinessMessageProcessor();
		mp.setBaseApprovalTask(task);
		if (null != task.mp) {
			Collection<String> keys = task.mp.getPropertyKeys();
			for (String key : keys) {
				mp.setConfig(key, task.mp.getConfig(key));
			}
		}
		mp.info("begine to query suggest dataid:" + task.businessInstanceID,
				task);
		List<FBusinessInstanceAndWorkItem> wrs = context.getList(
				FBusinessInstanceAndWorkItem.class, task.businessInstanceID);
		Iterator<FBusinessInstanceAndWorkItem> it = wrs.iterator();
		while (it.hasNext()) {
			FBusinessInstanceAndWorkItem item = it.next();
			IWorkItem wi = WorkflowRunUtil.loadWorkItem(context, item
					.getWorkItemID().toString());
			if (null == wi
					|| wi.getProcessInstance().getState()
							.equals(EnumProcessState.TERMINIATED)) {
				it.remove();
			}
		}
		mp.info("end to query suggest dataid:" + task.businessInstanceID, task);
		return wrs;
	}

	/**
	 * ִ�ж���
	 * 
	 * @param billModel
	 * @param actionname
	 */
	public boolean doAction(BillModel billModel, String actionname) {
		if (actionname.equalsIgnoreCase("ACCEPT")
				|| actionname.equalsIgnoreCase("REJECT")) {
			WXApprovalDataManage.addApprovalTimes(scramble.getCode());
			return false;
		} else if (actionname.equalsIgnoreCase("SAVE")) {
			WXApprovalDataManage.addSaveTimes(scramble.getCode());
			return false;
		} else {
			billModel.executeAction(actionname, "");
			return true;
		}
	}

	/**
	 * �������仯��ֵ����BillModel
	 * 
	 * @param billModel
	 * @param fbilldefine
	 * @param data
	 * @return
	 * @throws WXHintException
	 */
	public BillModel updateBill(Context context, BillModel billModel,
			FBillDefine fbilldefine, String data) throws WXHintException {
		setUpdateField(billModel);
		if (!judgeFiledData(data)) {
			return billModel;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String[] dat = data.split(";,,;");

		// �����޸�״̬
		billModel.setUIStyle(UIStyle.UPDATE);
		billModel.setModelState(ModelState.EDIT);
		billModel.setIsModified(true);
		BusinessObject mastbo = billModel.getData().getMaster();
		ModelUtil.setMasterFieldValue(billModel, mastbo.getTable().getName(),
				"MODIFYTIME", new Timestamp(new Date().getTime()));
		ModelUtil.setMasterFieldValue(billModel, mastbo.getTable().getName(),
				"MODIFIER", context.getLogin().getUser().getTitle());
		List<String> liststr = new ArrayList<String>();
		for (String da : dat) {
			String da_tname = da.substring(0, da.indexOf("."));
			String da_name = da.substring(da.indexOf(".") + 1,
					da.indexOf(";.,;"));
			String da_value = da.substring(da.indexOf(";,.;") + 4,
					da.indexOf(";..;"));
			if (mastbo.getTable().getName().equals(da_tname)
					&& da_value != "undefined") {
				FieldType fieldtype = mastbo.getFieldType(da_name);
				if (fieldtype == FieldType.STRING
						|| fieldtype == FieldType.TEXT) {
					setMoValue(billModel, da_tname, da_name, da_value);
				} else if (fieldtype == FieldType.INT
						|| fieldtype == FieldType.NUMERIC
						|| fieldtype == FieldType.LONG) {
					try {
						if (fieldtype == FieldType.INT) {
							int value = Integer.parseInt(da_value.replaceAll(
									",", ""));
							setMoValue(billModel, da_tname, da_name, value);
						}
						if (fieldtype == FieldType.NUMERIC) {
							double value = Double.parseDouble(da_value
									.replaceAll(",", ""));
							setMoValue(billModel, da_tname, da_name, value);

						}
						if (fieldtype == FieldType.LONG) {
							long value = Long.parseLong(da_value.replaceAll(
									",", ""));
							setMoValue(billModel, da_tname, da_name, value);

						}
					} catch (Exception e) {
						throw new WXHintException("(��:" + da_tname + ")(��:"
								+ da_name + ")(ֵ:" + da_value + ")Ӧ��Ϊ����",
								e.getStackTrace());
					}
				} else if (fieldtype == FieldType.DATE) {
					try {
						Date value = sdf.parse(da_value);
						setMoValue(billModel, da_tname, da_name, value);

					} catch (Exception e) {
						throw new WXHintException("(��:" + da_tname + ")(��:"
								+ da_name + ")(ֵ:" + da_value
								+ ")Ӧ��Ϊyyyy-MM-dd��ʽ", e.getStackTrace());
					}
				} else if (fieldtype == FieldType.GUID
						&& !da_value.trim().isEmpty()) {
					// basetable ���������ݵı��� da_value ҳ��������ݵ�stdname-stdcode
					/**
					 * Ҫ����basetable���������ݵı�������da_value��ҳ��������ݵ�stdname-stdcode��
					 * ��ѯ���ݿ�õ��������ݵ�recid�����������ݵ�recid���浽���ݶ�Ӧ���ֶ���
					 * 
					 * */
					String basetable = da.substring(da.indexOf(";,;;") + 4);
					String stdcode = null;

					if (da_value.contains("-")) {
						if (basetable.equals("MD_STAFF"))
							stdcode = da_value.substring(
									da_value.lastIndexOf("-") + 1).trim();
						else if (basetable.equals("MD_GANGWNLSZJB"))
							stdcode = da_value.substring(
									da_value.indexOf("-") + 1,
									da_value.lastIndexOf("-")).trim();
						else
							stdcode = da_value.substring(
									da_value.indexOf("-") + 1).trim();
					} else {
						stdcode = da_value.trim();
					}
					if (!stdcode.equals("")) {
						List<String> lx = new ArrayList<String>();
						getBaseData(context, basetable, "'" + stdcode + "'", lx);
						if (lx.size() > 0)
							setMoValue(billModel, da_tname, da_name,
									GUID.valueOf((String) lx.get(0)));
					}

				} else if (fieldtype == FieldType.BOOLEAN) {
					if (da_value.trim().equals("��")) {
						setMoValue(billModel, da_tname, da_name, 1);
					} else {
						setMoValue(billModel, da_tname, da_name, 0);
					}
				} else if (fieldtype == FieldType.VARBINARY
						&& !da_value.trim().isEmpty() && da_value.contains("-")) {

					String basetable = da.substring(da.indexOf(";,;;") + 4);
					String st[] = da_value.split(",");
					String canshu = "";
					for (String s : st) {
						canshu = canshu + "'" + s.substring(s.indexOf("-") + 1)
								+ "'" + ",";
					}
					List<String> lx = new ArrayList<>();
					getBaseData(
							context,
							basetable,
							canshu.substring(0, canshu.lastIndexOf(",")).trim(),
							lx);
					String value = "";
					if (lx.size() > 0) {
						for (int i = 0; i < lx.size(); i++) {
							value = value + lx.get(i);
						}
						setMoValue(billModel, da_tname, da_name, value);
					}
				} else if (fieldtype == FieldType.BYTES) {

					// ��ʱ��ִ��
				} else {
					// ��������Ҳ��������
				}
			} else {
				liststr.add(da);
			}
		}

		if (dat.length > 0) {
			setMoValue(billModel, mastbo.getTable().getName(), "MODIFYUSER",
					context.getLogin().getUser().getTitle());
			// ModelUtil.setMasterFieldValue(billModel,mastbo.getTable().getName(),"MODIFYUSER",context.getLogin().getUser().getTitle());
			billModel.executeFmlByTable(FormulaType.EXECUTE,
					EnumSet.of(ExecuteMode.SAVE), mastbo.getTable().getName());
		}
		// �޸��ӱ�����
		List<List<BusinessObject>> subbo = billModel.getData().getDetailsList();
		if (subbo != null) {
			for (int i = 0; i < subbo.size(); i++) {
				List<BusinessObject> lbo = subbo.get(i);
				for (int j = 0; j < lbo.size(); j++) {
					for (int l = 0; l < liststr.size(); l++) {
						String da = liststr.get(l);
						String da_tname = da.substring(0, da.indexOf("."));
						String da_name = da.substring(da.indexOf(".") + 1,
								da.indexOf(";.,;"));
						String recid = da.substring(da.indexOf(";.,;") + 4,
								da.indexOf(";,.;"));
						String da_value = da.substring(da.indexOf(";,.;") + 4,
								da.indexOf(";..;"));
						if (lbo.get(j).getTable().getName().equals(da_tname)
								&& da_value != "undefined"
								&& recid.equals(lbo.get(j).getRECID()
										.toString())) {
							BusinessObject ooo = lbo.get(j);
							FieldType fieldtype = ooo.getFieldType(da_name);
							int ss = 0;
							if (fieldtype == FieldType.INT
									|| fieldtype == FieldType.NUMERIC
									|| fieldtype == FieldType.LONG
									|| fieldtype == FieldType.STRING) {
								ss++;
								getIField(billModel, fbilldefine, ooo, da);
								// setMoValue(billModel, da_tname, da_name,
								// da_value);
							} else if (fieldtype == FieldType.TEXT
									|| fieldtype == FieldType.VARBINARY) {
								ss++;
								ooo.setFieldValue(da_name, da_value);
								// setMoValue(billModel, da_tname, da_name,
								// da_value);
							} else if (fieldtype == FieldType.DATE) {
								ss++;
								try {
									if (StringUtil.isNotEmpty(da_value)) {
										Date value = sdf.parse(da_value);
										ooo.setFieldValue(da_name, value);
										// setMoValue(billModel, da_tname,
										// da_name, value);
									} else {
										ooo.setFieldValue(da_name, null);
										// setMoValue(billModel, da_tname,
										// da_name, da_value);
									}
								} catch (Exception e) {
									throw new WXHintException("(��:" + da_tname
											+ ")(��:" + da_name + ")(ֵ:"
											+ da_value + ")Ӧ��Ϊyyyy-MM-dd��ʽ",
											e.getStackTrace());
								}
							} else if (fieldtype == FieldType.BOOLEAN
									|| fieldtype == FieldType.BYTES
									|| fieldtype == FieldType.GUID) {
								// ������ʱ֧��
								continue;
							} else {
								// ��������Ҳ��������
								continue;
							}
							if (ss > 0) {
								billModel.getData().getDetailsList().get(i)
										.set(j, ooo);
							}
							liststr.remove(l);
						}
					}
					// ִ�и�ֵ��ʽ
					billModel.executeFmlByTable(FormulaType.EXECUTE,
							EnumSet.of(ExecuteMode.SAVE), billModel.getData()
									.getDetailsList().get(i).get(j).getTable()
									.getName());
				}
			}
		}

		return billModel;
	}

	/**
	 * ���ÿɱ༭�ֶ�
	 * 
	 * @param billModel
	 */
	public void setUpdateField(BillModel billModel) {
		for (String f : wxap.getEditField()) {
			String table = f.substring(0, f.indexOf("."));
			String field = f.substring(f.indexOf(".") + 1, f.length());
			billModel.setFieldEnabled(table, field, true);
		}
		// billModel.setFieldEnabled();
		// wxap.getEditField()
	}

	/**
	 * ���ӱ�������뽻����
	 */
	public void setCursor(BillModel model) {
		List<List<BusinessObject>> subbo = model.getData().getDetailsList();
		if (subbo != null && subbo.size() > 0) {
			for (int i = 0; i < subbo.size(); i++) {
				List<BusinessObject> lbo = subbo.get(i);
				if (lbo.size() > 0
						&& model.getCursor(lbo.get(0).getTable().getName()) == -1) {
					model.setCursor(lbo.get(0).getTable().getName(), i);
				}
			}
		}
	}

	/**
	 * ���õ��ݵ�ֵ
	 * 
	 * @param exeModel
	 * @param modelName
	 * @param modelAttrName
	 * @param val
	 * @return
	 */
	public boolean setMoValue(BusinessModel exeModel, String modelName,
			String modelAttrName, Object val) {
		if (exeModel.getModelState() == ModelState.NONE
				|| exeModel.getModelState() == ModelState.BROWSE) {
			return true;
		}
		try {
			if (null == modelAttrName || null == modelName) {
				return false;
			}
			// ����ֶβ���дֱ�ӷ���true
			if (!exeModel.isFieldEnable(modelName, modelAttrName)) {
				return true;
			}
			Object o = exeModel.getFieldVisitor().getFieldValue(modelName,
					modelAttrName);
			// �ж����ǰ��Ҫ�����ֶ�����ת�����ݣ�DNATEST-14715
			IField field = exeModel.getDefine().findField(modelName,
					modelAttrName);
			if (null != field) {
				val = BusinessObject.convertData(field.getType(), val);
			}
			if (equalsobj(o, val)) {
				return true;
			}
			exeModel.getFieldVisitor().setFieldValue(modelName, modelAttrName,
					val);

			exeModel.setIsModified(true);// ����ģ���޸�״̬
			return true;
		} catch (ClassCastException ccex) {// ֻ��������ת���쳣
			BAPLogger logger = LoggerBuilder.getLogger(
					LoggerBuilder.BAP_MODULE, LoggerBuilder.MODEL_BUSINESS);
			logger.logError(null, "����ǿת����", ccex);
			return false;
		}
	}

	private static boolean equalsobj(Object left, Object right) {
		if (left != null) {
			return left.equals(right);
		} else if (right != null) {
			if (right.equals("") && left == null) {
				return true;
			}
			return right.equals(left);
		} else {
			return true;
		}
	}

	/**
	 * ��ʱ��WXApprovalDataManage�����ǿյģ�������WXApprovalDataManage �޸ĵ���������Ϣ
	 * 
	 * @param billModel
	 * @param data
	 * @throws ParseException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws WXHintException
	 */
	public BillModel updateBill2(Context context, BillModel billModel,
			FBillDefine fbilldefine, String data) throws WXHintException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String[] dat = data.split(";,,;");
		BusinessObject mastbo = billModel.getData().getMaster();
		// �����޸�״̬
		billModel.setModelState(ModelState.EDIT);
		billModel.setIsModified(true);
		ModelUtil.setMasterFieldValue(billModel, mastbo.getTable().getName(),
				"MODIFYTIME", new Timestamp(new Date().getTime()));
		ModelUtil.setMasterFieldValue(billModel, mastbo.getTable().getName(),
				"MODIFIER", context.getLogin().getUser().getTitle());
		// billModel.getFieldVisitor().setFieldValue(mastbo.getTable().getName(),
		// "MODIFYTIME",new Timestamp(new Date().getTime()));
		// billModel.getFieldVisitor().setFieldValue(mastbo.getTable().getName(),
		// "MODIFIER",context.getLogin().getUser().getTitle());

		// List<IFormula> listformula = billModel.getDefine().getFormulas();
		List<String> liststr = new ArrayList<String>();
		for (String da : dat) {
			String da_tname = da.substring(0, da.indexOf("."));
			String da_name = da.substring(da.indexOf(".") + 1,
					da.indexOf(";.,;"));
			// String
			// recid=da.substring(da.indexOf(";.,;")+4,da.indexOf(";,.;"));
			String da_value = da.substring(da.indexOf(";,.;") + 4,
					da.indexOf(";..;"));
			// String da_type=da.substring(da.indexOf(";..;")+4,da.length());

			if (mastbo.getTable().getName().equals(da_tname)
					&& da_value != "undefined") {
				FieldType fieldtype = mastbo.getFieldType(da_name);
				if (fieldtype == FieldType.STRING
						|| fieldtype == FieldType.TEXT
						|| fieldtype == FieldType.VARBINARY) {
					ModelUtil.setMasterFieldValue(billModel, da_tname, da_name,
							da_value);
				} else if (fieldtype == FieldType.INT
						|| fieldtype == FieldType.NUMERIC
						|| fieldtype == FieldType.LONG) {
					try {
						if (fieldtype == FieldType.INT) {
							int value = Integer.parseInt(da_value.replaceAll(
									",", ""));
							ModelUtil.setMasterFieldValue(billModel, da_tname,
									da_name, value);
						}
						if (fieldtype == FieldType.NUMERIC) {
							double value = Double.parseDouble(da_value
									.replaceAll(",", ""));
							ModelUtil.setMasterFieldValue(billModel, da_tname,
									da_name, value);
						}
						if (fieldtype == FieldType.LONG) {
							long value = Long.parseLong(da_value.replaceAll(
									",", ""));
							ModelUtil.setMasterFieldValue(billModel, da_tname,
									da_name, value);
						}
					} catch (Exception e) {
						throw new WXHintException("(��:" + da_tname + ")(��:"
								+ da_name + ")(ֵ:" + da_value + ")Ӧ��Ϊ����",
								e.getStackTrace());
					}
				} else if (fieldtype == FieldType.DATE) {
					try {
						Date value = sdf.parse(da_value);
						ModelUtil.setMasterFieldValue(billModel, da_tname,
								da_name, value);
					} catch (Exception e) {
						throw new WXHintException("(��:" + da_tname + ")(��:"
								+ da_name + ")(ֵ:" + da_value
								+ ")Ӧ��Ϊyyyy-MM-dd��ʽ", e.getStackTrace());
					}
				} else if (fieldtype == FieldType.BOOLEAN
						|| fieldtype == FieldType.BYTES
						|| fieldtype == FieldType.GUID) {
					// ������ʱ��ִ��
				} else {
					// ��������Ҳ��������
				}
			} else {
				liststr.add(da);
			}
		}
		Set<ExecuteMode> sem = new HashSet<ExecuteMode>();
		sem.add(ExecuteMode.SAVE);

		if (dat.length > 0) {
			billModel
					.getData()
					.getMaster()
					.setFieldValue("MODIFYUSER",
							context.getLogin().getUser().getTitle());
		}
		List<List<BusinessObject>> subbo = billModel.getData().getDetailsList();
		if (subbo != null) {
			for (int i = 0; i < subbo.size(); i++) {
				List<BusinessObject> lbo = subbo.get(i);
				for (int j = 0; j < lbo.size(); j++) {
					for (int l = 0; l < liststr.size(); l++) {
						String da = liststr.get(l);
						String da_tname = da.substring(0, da.indexOf("."));
						String da_name = da.substring(da.indexOf(".") + 1,
								da.indexOf(";.,;"));
						String recid = da.substring(da.indexOf(";.,;") + 4,
								da.indexOf(";,.;"));
						String da_value = da.substring(da.indexOf(";,.;") + 4,
								da.indexOf(";..;"));
						String da_type = da.substring(da.indexOf(";..;") + 4,
								da.length());
						if (lbo.get(j).getTable().getName().equals(da_tname)
								&& da_value != "undefined"
								&& recid.equals(lbo.get(j).getRECID()
										.toString())) {
							BusinessObject ooo = lbo.get(j);
							FieldType fieldtype = ooo.getFieldType(da_name);
							int ss = 0;

							if (fieldtype == FieldType.INT
									|| fieldtype == FieldType.NUMERIC
									|| fieldtype == FieldType.LONG
									|| fieldtype == FieldType.STRING) {
								ss++;
								getIField(billModel, fbilldefine, ooo, da);
							} else if (fieldtype == FieldType.TEXT
									|| fieldtype == FieldType.VARBINARY) {
								ss++;
								ooo.setFieldValue(da_name, da_value);
							} else if (fieldtype == FieldType.DATE) {
								ss++;
								try {
									if (StringUtil.isNotEmpty(da_value)) {
										Date value = sdf.parse(da_value);
										ooo.setFieldValue(da_name, value);
									} else {
										ooo.setFieldValue(da_name, null);
									}
								} catch (Exception e) {
									throw new WXHintException("(��:" + da_tname
											+ ")(��:" + da_name + ")(ֵ:"
											+ da_value + ")Ӧ��Ϊyyyy-MM-dd��ʽ",
											e.getStackTrace());
								}
							} else if (fieldtype == FieldType.BOOLEAN
									|| fieldtype == FieldType.BYTES
									|| fieldtype == FieldType.GUID) {
								// ������ʱ֧��
								continue;
							} else {
								// ��������Ҳ��������
								continue;
							}
							if (ss > 0) {
								billModel.getData().getDetailsList().get(i)
										.set(j, ooo);
							}
							liststr.remove(l);
						}
					}
					// ִ�и�ֵ��ʽ
					billModel.executeFmlByTable(FormulaType.EXECUTE, sem,
							billModel.getData().getDetailsList().get(i).get(j)
									.getTable().getName());
				}
			}
		}
		billModel.executeFmlByTable(FormulaType.EXECUTE, sem, billModel
				.getData().getMaster().getTable().getName());
		return billModel;
	}

	/**
	 * 2018-09-13 LQ ���� context ��tablename(�������ݱ���)��param(������stdcode)
	 * 
	 * ���ݱ��stdcode�õ�recid
	 * */
	private void getBaseData(Context context, String tablename, String param,
			List<String> paramlist) {
		StringBuffer getEnclosureSql = new StringBuffer();
		getEnclosureSql
				.append("define query getEnclosures(@stdcode string) \n");
		getEnclosureSql.append("begin \n");
		getEnclosureSql.append("  select t.recid as recid  from  \n");

		getEnclosureSql.append(tablename);
		getEnclosureSql.append(" as t \n");
		getEnclosureSql.append("where 1 = 1      and t.stdcode   in ("
				+ param.trim() + ")   \n");
		getEnclosureSql
				.append(" or t.stdname   in (" + param.trim() + ")   \n");

		getEnclosureSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(getEnclosureSql);
		// dbCommand.setArgumentValues(param);
		RecordSet recordSet = dbCommand.executeQuery();
		while (recordSet.next()) {
			paramlist.add(recordSet.getFields().get(0).getGUID().toString());
		}
		dbCommand.unuse();
	}
}
