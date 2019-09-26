package com.jiuqi.mt2.dna.mobile.wxapproval.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jiuqi.dna.bap.workflowmanager.common.BusinessObjectLoader;
import com.jiuqi.dna.bap.workflowmanager.common.FBusinessObject;
import com.jiuqi.dna.bap.workflowmanager.common.consts.BusinessObjectConsts;
import com.jiuqi.dna.bap.workflowmanager.common.formula.WorkFlowParser;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.TaskListQueryUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.internal.WorkFlowParserUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.consts.ApplyState;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.consts.ApprovalState;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.consts.Indicator;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.BOConditionField;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.TaskListDefine;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FApplyRecord;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FRecord;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FRecordModel;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FRecordModel2;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FTaskListDefine;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.impl.RecordModelImpl;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.impl.RecordNavigatorImpl;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.task.ExecuteQueryTask;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.xlib.utils.StringUtil;

public class WXBusinessProcessManager {

	public static List<FApplyRecord> getFApplyRecord(Context context, GUID defineID, GUID submitUserID, ApplyState state) {
		TaskListDefine tasklistDefine = context.find(FTaskListDefine.class, defineID).getTaskListDefine();
		ExecuteQueryTask task = generateTask(tasklistDefine, context, submitUserID, state, null);
		FRecordModel2<FApplyRecord> model;
		try {
			model = TaskListQueryUtil.providerApplyRecord(context, null, task);
			
			return model.getRecords();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ExecuteQueryTask generateTask(TaskListDefine taskListDefine, Context context, GUID submitUserID, ApplyState state, List<BOConditionField> conditionList) {
		List<TableDefine> tableList = null;
		if (taskListDefine.businessObjectList != null && taskListDefine.businessObjectList.size() != 0)
			tableList = getTableList(context, taskListDefine.businessObjectList.get(0).getID());
		HashMap<String, Object> queryContidionMap = new HashMap<String, Object>();

		queryContidionMap.put("ApplyState", state);

		ExecuteQueryTask task = new ExecuteQueryTask(taskListDefine.resultFieldList, tableList, queryContidionMap, conditionList);
		task.userID = submitUserID;
		task.taskListDefine = taskListDefine;
		return task;
	}

	/**
	 * 根据单据定义ID获取某种状态的单据列表
	 * @param context
	 * @param defineID	单据定义id
	 * @param userID	用户ID
	 * @param state		审批状态
	 * @return
	 */
	public static List<FRecord> getFRecordsByBilldefineidAndState(Context context, GUID defineID, GUID userID, ApprovalState state) {
		if (null == defineID || null == userID) {
			return new ArrayList<FRecord>();
		}
		TaskListDefine taskListDefine = null;
		FRecordModel recordModel = null;
		FTaskListDefine fdefine = context.get(FTaskListDefine.class, defineID);
		taskListDefine = fdefine.getTaskListDefine();
		GUID businessObjectID = null;
		if (taskListDefine.businessObjectList.size() > 0) {
			businessObjectID = taskListDefine.businessObjectList.get(0).getID();
		} else {
			return new ArrayList<FRecord>();
		}
		// 业务对象不存在
		if (null == BusinessObjectLoader.findBusinessObject(context, businessObjectID)) {
			return new ArrayList<FRecord>();
		}
		recordModel = new RecordModelImpl();
		ExecuteQueryTask task = generateTask(taskListDefine, context, businessObjectID, state);
		task.userID = userID;
		// 条件在构造task中读取过滤条件进行设置，此处不应该清空条件
		// task.otherQueryContidionList = null;
		RecordNavigatorImpl navigator = new RecordNavigatorImpl();
		if (null != recordModel) {
			navigator = (RecordNavigatorImpl) recordModel.getNavigator();
		}

		navigator.setIndicator(Indicator.FIRST);
		// navigator.setSinglePageCount(10000);
		recordModel = context.find(FRecordModel.class, navigator, task);
		if(recordModel==null){
			return null;
		}
		return recordModel.getRecords();
	}

	private static ExecuteQueryTask generateTask(TaskListDefine taskListDefine, Context context, GUID businessObjectID, ApprovalState state) {
		List<TableDefine> tableList = getTableList(context, businessObjectID);
		HashMap<String, Object> queryContidionMap = getQueryContidionMap(state);
		ExecuteQueryTask task = new ExecuteQueryTask(taskListDefine.resultFieldList, tableList, queryContidionMap, getConditionList(context, taskListDefine, businessObjectID));
		task.taskListDefine = taskListDefine;
		return task;
	}

	private static List<TableDefine> getTableList(Context context, GUID businessObjectID) {
		List<TableDefine> tableList = new ArrayList<TableDefine>();
		FBusinessObject businessObject = BusinessObjectLoader.findBusinessObject(context, businessObjectID);
		tableList.addAll(businessObject.getWFTables(context));

		return tableList;
	}

	private static List<BOConditionField> getConditionList(Context context, TaskListDefine taskListDefine, GUID BOID) {
		List<BOConditionField> otherQueryContidionList = new ArrayList<BOConditionField>();
		if (null == taskListDefine)
			return otherQueryContidionList;
		// otherQueryContidionList.addAll(taskListDefine.conditionFieldList);
		for (BOConditionField conditionField : taskListDefine.conditionFieldList) {
			// 如果该字段配置隐藏，则作为过滤条件，否则不作为过滤条件
			if (conditionField.isVisible())
				continue;
			BOConditionField conditionField2 = new BOConditionField();
			conditionField2.copy(conditionField);
			otherQueryContidionList.add(conditionField2);
		}
		String BOType = null;
		if (null != taskListDefine.businessObjectList && taskListDefine.businessObjectList.size() > 0)
			BOType = taskListDefine.businessObjectList.get(0).getType();
		initConditionList(context, BOID, BOType, otherQueryContidionList);
		return otherQueryContidionList;
	}

	private static HashMap<String, Object> getQueryContidionMap(ApprovalState state) {
		HashMap<String, Object> queryContidionMap = new HashMap<String, Object>();
		// 审批状态
		queryContidionMap.put(BusinessObjectConsts.WORKFLOWSTATE, state);

		return queryContidionMap;
	}

	/**
	 * 初始化查询条件列表. 查询条件列表中有不显示的查询条件,将这些查询条件初始化.
	 */
	private static void initConditionList(Context context, GUID BOID, String BOType, List<BOConditionField> contidionList) {
		if (contidionList == null) {
			contidionList = new ArrayList<BOConditionField>();
			return;
		}
		WorkFlowParser parser = null;
		if (!GUID.emptyID.equals(BOID)) {
			parser = new WorkFlowParser(context, BOID);
			parser.registerBusinessFunctions(BOType);
		} else {
			parser = new WorkFlowParser(context, null);
		}
		for (BOConditionField condition : contidionList) {
			if (condition.isVisible() || StringUtil.isNotEmpty(condition.getDefaultResult())) {
				Object value = WorkFlowParserUtil.getValueByExpression(context, parser, condition);
				if (null != value) {
					condition.setValue(value);
				}
			}
		}
	}
}
