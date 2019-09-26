package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.jiuqi.dna.bap.workflowmanager.common.WFrecord;
import com.jiuqi.dna.bap.workflowmanager.execute.common.BusinessProcessManager;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.consts.ApprovalState;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.BOResultField;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.TaskListDefine;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FRecord;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FTaskListDefine;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.mt2.dna.service.todo.impl.MetadataImpl;
import com.jiuqi.mt2.dna.service.todo.impl.TodoListStream;
import com.jiuqi.mt2.dna.service.todo.internal.ListenerGatherer;
import com.jiuqi.mt2.dna.service.todo.util.FRecordComparator;
import com.jiuqi.mt2.dna.service.todo.util.MWFUtil;
import com.jiuqi.mt2.dna.service.todo.util.MWorkflowUtil;
import com.jiuqi.mt2.spi.call.IStream;
import com.jiuqi.mt2.spi.todo.SPITodo;
import com.jiuqi.mt2.spi.todo.model.ITodoItem;
import com.jiuqi.mt2.spi.todo.model.impl.TodoItem;
import com.jiuqi.xlib.utils.StringUtil;
/**
 * ITodoItem 的相关Common
 * @author liuzihao
 *
 */
public class ITodoItemCommon{
	
	/**
	 * 根据状态分页获取审批任务列表
	 */
	public IStream<ITodoItem> pagingApprovalListByState(String cate, ApprovalState state, int pageSize, Context context) {
		GUID userID = context.getLogin().getUser().getID();
		cate = MWFUtil.decodeCate(cate, context);
		FTaskListDefine fdefine = context.find(FTaskListDefine.class, GUID.valueOf(cate));
		TaskListDefine define = fdefine.getTaskListDefine();
		MWorkflowUtil.clearConditionField(define);
		List<FRecord> recordList = BusinessProcessManager.getFRecords(context, GUID.valueOf(cate), userID, state);
		if (recordList == null || recordList.isEmpty())
			return new TodoListStream(null, new MetadataImpl("todos", "data"), 5);
		return getItemListStream(cate, context, recordList, pageSize);
	}
	
	private IStream<ITodoItem> getItemListStream(String cate, Context context, List<FRecord> recordList, int pagesize) {
		List<ITodoItem> itemList = new ArrayList<ITodoItem>();
		boolean sucess = fillItemList(cate, context, recordList, itemList);
		if (pagesize == SPITodo.ARGUMENT_SIZE_UNLIMITED) {
			if (!sucess)
				return new TodoListStream(itemList, new MetadataImpl("todos", "data"), 5);
			return new TodoListStream(itemList, new MetadataImpl("todos", MWorkflowUtil.getMetadata(context, GUID.valueOf(cate))), itemList.size());
		}
		if (!sucess)
			return new TodoListStream(itemList, new MetadataImpl("todos", "data"), pagesize);
		return new TodoListStream(itemList, new MetadataImpl("todos", MWorkflowUtil.getMetadata(context, GUID.valueOf(cate))), pagesize);
	}

	private boolean fillItemList(String cate, Context context, List<FRecord> recordList, List<ITodoItem> itemList) {
		String businessObjectID = MWorkflowUtil.getBusinessObjectIDByTaskDefineID(context, GUID.valueOf(cate));

		Collections.sort(recordList, new FRecordComparator(MWorkflowUtil.getTableName(context, businessObjectID)));
		boolean sucess = true;
		for (int i = 0; i < recordList.size(); i++) {
			FRecord record = recordList.get(i);
			if (!GUID.emptyID.equals(businessObjectID)) {
				TodoItem todoItem = new TodoItem();
				todoItem.setId(record.getWorkItemID().toString());
				todoItem.setState(0);

				Map<String, Object> map = record.getFieldMap();
				List<BOResultField> fieldList = MWorkflowUtil.getQueryFields(context, cate);
				if (fieldList.isEmpty()) {
					sucess = false;
					break;
				}

				for (BOResultField resultField : fieldList) {
					String fieldName = MWFUtil.getFieldName(resultField);
					String fieldValue = MWFUtil.format(context, resultField, map.get(fieldName) == null ? "" : map.get(fieldName));
					if (StringUtil.isEmpty(fieldValue)) {
						fieldValue = MWFUtil.format(context, resultField,
								map.get(resultField.getTableName() + resultField.getFieldName()) == null ? "" : map.get(resultField.getTableName() + resultField.getFieldName()));
					}
					todoItem.setProperty(fieldName, fieldValue);
				}

				todoItem.setBillDefineID(MWorkflowUtil.getBusinessObjectIDByWorkItemID(context, record.getWorkItemID()));
				if (ListenerGatherer.getiTodoListener() != null) {
					boolean isUrgent = ListenerGatherer.getiTodoListener().isUrgent(todoItem.getId());
					todoItem.setUrgent(isUrgent);
				}
				if (StringUtil.isEmpty(todoItem.getBillDefineID())) {
				}
				if (map.containsKey("GUIDREF"))
					todoItem.setBillDataID(map.get("GUIDREF").toString());
				if (todoItem != null)
					itemList.add(todoItem);
			}
		}
		return sucess;
	}
	/**
	 * 
	 * @param record WFrecord
	 * @param context Context
	 * @param cate String
	 * @param billdefineid String
	 * @return
	 */
	public TodoItem createTodoItem(WFrecord record,Context context,String cate,String billdefineid){
		TodoItem todoItem = new TodoItem();
		todoItem.setId(record.getWorkItemID().toString());
		todoItem.setState(0);
		Map<String, Object> map = record.getFieldMap();
		List<BOResultField> fieldList = MWorkflowUtil.getQueryFields(context, cate);
		if (fieldList.isEmpty()) {
			return null;
		}

		for (BOResultField resultField : fieldList) {
			String fieldName = MWFUtil.getFieldName(resultField);
			String fieldValue = MWFUtil.format(context, resultField, map.get(fieldName) == null ? "" : map.get(fieldName));
			if (StringUtil.isEmpty(fieldValue)) {
				fieldValue = MWFUtil.format(context, resultField,
						map.get(resultField.getTableName() + resultField.getFieldName()) == null ? "" : map.get(resultField.getTableName() + resultField.getFieldName()));
			}
			todoItem.setProperty(fieldName, fieldValue);
		}

		todoItem.setBillDefineID(billdefineid);
		if (ListenerGatherer.getiTodoListener() != null) {
			boolean isUrgent = ListenerGatherer.getiTodoListener().isUrgent(todoItem.getId());
			todoItem.setUrgent(isUrgent);
		}
		if (StringUtil.isEmpty(todoItem.getBillDefineID())) {
		}
		if (map.containsKey("GUIDREF"))
			todoItem.setBillDataID(map.get("GUIDREF").toString());

		return 	todoItem;
	}

}
