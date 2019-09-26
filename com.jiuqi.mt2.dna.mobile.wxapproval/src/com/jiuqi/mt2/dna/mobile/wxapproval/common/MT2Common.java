package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.bap.bill.common.model.BillCentre;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.bill.intf.facade.model.FBillDefine;
import com.jiuqi.dna.bap.workflowmanager.common.parse.Button;
import com.jiuqi.dna.bap.workflowmanager.define.common.WorkflowDefineManager;
import com.jiuqi.dna.bap.workflowmanager.execute.common.BusinessProcessManager;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.WorkflowRunUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.consts.ApprovalState;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.BOConditionField;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.BOResultField;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.TaskListDefine;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.WFBusinessObjectXML;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FTaskListDefine;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.workflow.intf.facade.IWorkCategory;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.bill.intf.MobileBillDefineOpenConfigUtils;
import com.jiuqi.mt2.dna.mobile.bill.intf.OpenConfigInfo;
import com.jiuqi.mt2.dna.mobile.common.custombutton.meta.ButtonInfoContainer;
import com.jiuqi.mt2.dna.mobile.common.custombutton.meta.ButtonInfoList;
import com.jiuqi.mt2.dna.mobile.common.service.TemplateImagesTask;
import com.jiuqi.mt2.dna.mobile.todo.facade.FApprovalDefine;
import com.jiuqi.mt2.dna.service.common.intf.action.Action;
import com.jiuqi.mt2.dna.service.todo.action.TodoActionGather;
import com.jiuqi.mt2.dna.service.todo.impl.Nameable;
import com.jiuqi.mt2.dna.service.todo.impl.TodoCategoryStream;
import com.jiuqi.mt2.dna.service.todo.internal.ListenerGatherer;
import com.jiuqi.mt2.dna.service.todo.util.MWFUtil;
import com.jiuqi.mt2.dna.service.todo.util.MWorkflowUtil;
import com.jiuqi.mt2.spi.call.IStream;
import com.jiuqi.mt2.spi.common2.table.IMTableCell;
import com.jiuqi.mt2.spi.common2.table.IMStyle.MSCALE;
import com.jiuqi.mt2.spi.common2.table.impl.MLength;
import com.jiuqi.mt2.spi.common2.table.impl.MShowTemplate;
import com.jiuqi.mt2.spi.common2.table.impl.MStyleObject;
import com.jiuqi.mt2.spi.common2.table.impl.MTable;
import com.jiuqi.mt2.spi.common2.table.impl.MTableCell;
import com.jiuqi.mt2.spi.common2.table.impl.MTableRow;
import com.jiuqi.mt2.spi.common2.table.impl.MTemplateUtil;
import com.jiuqi.mt2.spi.custombutton.MCustomButton;
import com.jiuqi.mt2.spi.log.MobileLog;
import com.jiuqi.mt2.spi.todo.model.ITodoCategory;
import com.jiuqi.mt2.spi.todo.model.ITodoItem;
import com.jiuqi.mt2.spi.todo.model.impl.BillDefineLink;
import com.jiuqi.mt2.spi.todo.model.impl.TodoCategory;
import com.jiuqi.mt2.spi.todo.model.impl.TodoItem;
import com.jiuqi.util.json.JSONException;
import com.jiuqi.util.json.JSONObject;
import com.jiuqi.xlib.INamable;
import com.jiuqi.xlib.dna.ContextManager;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * MT2里面方法，微信开发也需要，但代码发生了变化，所以提取出来相关的东西
 * @author liuzihao
 *
 */
public class MT2Common {
	
	/**
	 * 获取ITodoCategory
	 * 
	 * @param workitemId
	 * @param monitor
	 * @return
	 */
	public ITodoCategory getTodoCategory(String workitemId, Context context) {
		IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context, workitemId);
		List<GUID> tasklistDefineID = getTaskListDefineID(context, GUID.valueOf(iworkItem.getWorkCategory()));
		FApprovalDefine approvalDefine = getWorkFlowDefineByCategoryID(context, tasklistDefineID);
		if(approvalDefine==null){
			return null;
		}
		try {
			ITodoCategory entity = getTodoCategoryByApprovalDefine(context, approvalDefine);
			return entity;
		} catch (Exception e) {
			MobileLog.logError(e);
		}
		return null;
	}

	/**
	 * 获取ITodoCategory
	 * 
	 * @param workitemId
	 * @param monitor
	 * @return
	 */
	public FApprovalDefine getFApprovalDefine(String workitemId, Context context) {
		IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context, workitemId);
		List<GUID> tasklistDefineID = getTaskListDefineID(context, GUID.valueOf(iworkItem.getWorkCategory()));
		FApprovalDefine approvalDefine = getWorkFlowDefineByCategoryID(context, tasklistDefineID);
		return approvalDefine;
	}
	
	public ITodoCategory getTodoCategoryRoughCount(String workitemId,Context context) {
		IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context, workitemId);
		List<GUID> tasklistDefineID = getTaskListDefineID(context, GUID.valueOf(iworkItem.getWorkCategory()));
		FApprovalDefine approvalDefine = getWorkFlowDefineByCategoryID(context, tasklistDefineID);
		try {
			ITodoCategory entity = getTodoCategoryByApprovalDefineRoughCount(context, approvalDefine);
			return entity;
		} catch (Exception e) {
			MobileLog.logError(e);
		}
		return null;
	}

	public List<GUID> getTaskListDefineID(Context context, GUID category) {
		List<GUID> lguid = new ArrayList<GUID>();
		List<FTaskListDefine> list = context.getList(FTaskListDefine.class);
		for (int i = 0; i < list.size(); i++) {
			TaskListDefine taskListDefine = list.get(i).getTaskListDefine();
			if (null != taskListDefine && taskListDefine.businessObjectList != null) {
				for (int j = 0; j < taskListDefine.businessObjectList.size(); j++) {
					if (taskListDefine.businessObjectList.get(j).getID().equals(category)) {
						lguid.add(taskListDefine.getID());
					}
				}
			}
		}
		return lguid;
	}

	/**
	 * 根据分组待办ID取移动待办定义
	 * 
	 * @param context
	 * @param tasklistDefineID
	 * @return
	 */
	public FApprovalDefine getWorkFlowDefineByCategoryID(Context context, List<GUID> tasklistDefineID) {
		for (FApprovalDefine tdefine : context.getList(FApprovalDefine.class)) {
			for (GUID g : tasklistDefineID) {
				if (tdefine.getTaskDefineID().equals(g)) {
					return tdefine;
				}
			}
		}
		return null;
	}

	private ITodoCategory getTodoCategoryByApprovalDefine(Context context, FApprovalDefine define) {
		GUID userID = context.getLogin().getUser().getID();
		FTaskListDefine ftaskDefine = context.get(FTaskListDefine.class, define.getTaskDefineID());
		TaskListDefine taskDefine = ftaskDefine.getTaskListDefine();
		MWorkflowUtil.clearConditionField(taskDefine);

		int count = 0;
		for (WFBusinessObjectXML obj : taskDefine.businessObjectList) {
			try {
				int objCount = BusinessProcessManager.getApprovalCountByState(context, obj.getID(), taskDefine, userID, ApprovalState.WAIT);
				if (objCount > 0) {
					count += objCount;
				}
			} catch (Exception e) {
				MobileLog.logError(e);
			}
		}

		INamable[] actions = null;
		List<INamable> namableList = new ArrayList<INamable>();
		if (taskDefine.actionList.size() > 0) {
			for (int j = 0; j < taskDefine.actionList.size(); j++) {
				if ("BatchApproveAction".equalsIgnoreCase(taskDefine.actionList.get(j).getName()) || "BatchWFRejectAction".equalsIgnoreCase(taskDefine.actionList.get(j).getName())) {
					INamable action = new Nameable(taskDefine.actionList.get(j).getName(), taskDefine.actionList.get(j).getTitle());

					namableList.add(action);
				}
			}

			actions = namableList.toArray(new INamable[namableList.size()]);
		}
		// 获取自定义按钮
		MCustomButton[] custombuttons = null;
		try {
			String customButtonInfo = getButtonInfo(define);
			ButtonInfoList bil = new ButtonInfoList(customButtonInfo);
			;
			ArrayList<ButtonInfoContainer> templist = bil.getSimpleList();
			custombuttons = new MCustomButton[templist.size()];
			for (int m = 0; m < templist.size(); m++) {
				if (templist.get(m).info != null) {
					String buttonName = templist.get(m).info.getName();
					String buttonTitle = templist.get(m).info.getTitle();
					if (StringUtil.isEmpty((buttonTitle))) {
						TodoActionGather gather = getActionGather();
						for (Action a : gather.getList()) {
							if (a.getName().equals(templist.get(m).info.getActionName())) {
								buttonTitle = a.getTitle();
							}
						}
					}
					custombuttons[m] = new MCustomButton(buttonName, templist.get(m).info.getTitle(), templist.get(m).info.getImage());
				}
			}
		} catch (JSONException e) {
			MobileLog.logError(e);
		}
		// 获取单据关联
		INamable[] BOInfos = null;
		try {
			String boInfoString = getBOInfo(define);
			List<OpenConfigInfo> infoList = MobileBillDefineOpenConfigUtils.toList(boInfoString);
			BOInfos = new INamable[infoList.size()];
			for (int m = 0; m < infoList.size(); m++) {
				if (infoList.get(m) != null) {
					String buttonName = infoList.get(m).getBillModelID().toString();
					String buttonTitle = infoList.get(m).getMobileBillDefineID().toString();
					BOInfos[m] = new Nameable(buttonName, buttonTitle);
				}
			}
		} catch (Exception e) {
			MobileLog.logError(e);
		}
		// 获取列表界面模板
		String templateInfo = getListMTemplateInfo(define);
		MShowTemplate template = new MShowTemplate();
		com.jiuqi.xlib.json.JSONObject templateJSON = null;
		try {
			if (templateInfo == null) {
				template = new MShowTemplate();
			} else {
				templateJSON = new com.jiuqi.xlib.json.JSONObject(templateInfo);
				template.deserialize(templateJSON);
			}

		} catch (Exception e) {
			MobileLog.logError(e);
		}
		String enCodedCate = MWFUtil.encodeCate(define.getTaskDefineID().toString(), context);
		TodoCategory category = new TodoCategory(enCodedCate);
		category.setTitle(define.getTitle());
		// 获取图标
		String iconID = getIconId(define);
		// ////
		List<String> imgs = MTemplateUtil.findImagesIn(template);
		if (StringUtil.isNotEmpty(iconID))
			imgs.add(iconID);
		TemplateImagesTask task = new TemplateImagesTask();

		task.setImages(imgs);

		category.setListTemplate(template);
		category.setTodoCount(count);
		category.setActions(actions);
		category.setBoInfo(BOInfos);
		category.setIcon(iconID);
		category.setCustomButton(custombuttons);
		category.setDefaultOpen(true);
		category.setQueryTemplate(getConditionData(context, category.getID()));
		/* zhangjin 2012-08-30 start */
		if (ListenerGatherer.getiTodoListener() != null) {
			boolean resultIsMulti = ListenerGatherer.getiTodoListener().isMulti(category.getID());
			category.setMulti(resultIsMulti);
		} else {
			category.setMulti(true);
		}

		return category;
	}

	protected String getButtonInfo(FApprovalDefine define) throws JSONException {
		byte[] configBytes = define.getConfigBytes();
		if (configBytes != null) {
			String config = define.getConfig();
			JSONObject jo = new JSONObject(config);
			if (jo.has(FApprovalDefine.ATT_BUTTONINFO)) {
				String buttonInfo = jo.getString(FApprovalDefine.ATT_BUTTONINFO);
				return buttonInfo;
			}
		}
		return null;
	}

	private TodoActionGather getActionGather() {
		return new TodoActionGather();
	}

	protected String getBOInfo(FApprovalDefine define) throws JSONException {
		byte[] configBytes = define.getConfigBytes();
		if (configBytes != null) {
			String config = define.getConfig();
			JSONObject jo = new JSONObject(config);
			if (jo.has(FApprovalDefine.ATT_BO_SHOW_INFO)) {
				String boInfo = jo.getString(FApprovalDefine.ATT_BO_SHOW_INFO);
				return boInfo;
			}
		}
		return null;
	}
	
	/**
	 * 移动审批设置-列表界面
	 * @param define
	 * @return
	 */
	protected String getListMTemplateInfo(FApprovalDefine define) {
		try {
			byte[] configBytes = define.getConfigBytes();
			if (configBytes != null) {
				String config = define.getConfig();
				JSONObject jo = new JSONObject(config);
				if (jo.has(FApprovalDefine.ATT_LIST_MTEMPLATE)) {
					String templateInfo = jo.getString(FApprovalDefine.ATT_LIST_MTEMPLATE);
					return templateInfo;
				}
			}
		} catch (Exception e) {
			MobileLog.logError(e);
		}
		return null;
	}
	
	/**
	 * 移动审批设置-展示模版
	 * @param define
	 * @return
	 */
	protected String getShowMTemplateInfo(FApprovalDefine define) {
		try {
			byte[] configBytes = define.getConfigBytes();
			if (configBytes != null) {
				String config = define.getConfig();
				JSONObject jo = new JSONObject(config);
				if (jo.has(FApprovalDefine. ATT_BO_SHOW_INFO)) {
					String templateInfo = jo.getString(FApprovalDefine. ATT_BO_SHOW_INFO);
					return templateInfo;
				}
			}
		} catch (Exception e) {
			MobileLog.logError(e);
		}
		return null;
	}
	
	/**
	 * 移动审批设置-编辑模版
	 * @param define
	 * @return
	 */
	protected String getEditMTemplateInfo(FApprovalDefine define) {
		try {
			byte[] configBytes = define.getConfigBytes();
			if (configBytes != null) {
				String config = define.getConfig();
				JSONObject jo = new JSONObject(config);
				if (jo.has(FApprovalDefine. ATT_BO_EDIT_INFO )) {
					String templateInfo = jo.getString(FApprovalDefine. ATT_BO_EDIT_INFO );
					return templateInfo;
				}
			}
		} catch (Exception e) {
			MobileLog.logError(e);
		}
		return null;
	}

	protected String getIconId(FApprovalDefine define) {
		try {
			byte[] configBytes = define.getConfigBytes();
			if (configBytes != null) {
				String config = define.getConfig();
				JSONObject jo = new JSONObject(config);
				if (jo.has(FApprovalDefine.ATT_ICON_ID)) {
					String iconId = jo.optString(FApprovalDefine.ATT_ICON_ID);
					return iconId;
				}
			}
		} catch (Exception e) {
			MobileLog.logError(e);
		}
		return null;
	}

	private MShowTemplate getConditionData(Context context, String taskDefineID) {
		FTaskListDefine fdefine = context.find(FTaskListDefine.class, GUID.valueOf(taskDefineID));
		TaskListDefine define = fdefine.getTaskListDefine();
		if (define.conditionFieldList.size() == 0) {
			return null;
		}
		// MGridData gridData = new MGridData();
		// gridData.setDefaultHeight(60);
		MShowTemplate templ = new MShowTemplate(1, 2);
		MTable table = (MTable) templ.getTable();
		int rowCount = 0;
		table.setColumnCount(2);
		int maxNum = 0;

		for (BOConditionField bf : define.conditionFieldList) {
			if (!bf.isVisible())
				continue;
			if (bf.getFieldTitle().length() > maxNum)
				maxNum = bf.getFieldTitle().length();
			if (rowCount >= table.getRowCount())
				table.insertRow(rowCount);
			MTableRow row2 = (MTableRow) table.getRow(rowCount);
			row2.setRowHeight(2);
			rowCount++;
			MStyleObject rowStyle = (MStyleObject) row2.getStyle();
			rowStyle.setWidth(6);

			MTableCell cell1 = (MTableCell) row2.getCol(0);
			MStyleObject cell1Style = (MStyleObject) cell1.getStyle();
			cell1Style.setTextColor(0xff444444);
			cell1Style.setWidth(7);
			cell1Style.setFontSize(0);
			cell1.setTitle(bf.getTitle());
			cell1.setProperty("readOnly", "true");
			// row2.insertCell(0, cell1);

			MTableCell cell2 = (MTableCell) row2.getCol(1);// new MTableCell();
			MStyleObject cell2Style = (MStyleObject) cell2.getStyle();
			cell2Style.setTextColor(0xff444444);
			cell2Style.setWidth(8);
			cell2Style.setFontSize(0);
			cell2.setTitle("{#" + bf.getFieldName() + "}");
			cell2.setProperty(IMTableCell.FIELD_NAME, bf.getFieldName() + "mt_operatorAs" + bf.getOperator());
			cell2.setProperty(IMTableCell.VALUE_TYPE, MWorkflowUtil.getDataTypeFromBo(context, bf).name());
			if (MWorkflowUtil.getDataTypeFromBo(context, bf) == com.jiuqi.mt2.spi.common2.table.IMField.MDataType.BASEDATA_MULTI
					|| MWorkflowUtil.getDataTypeFromBo(context, bf) == com.jiuqi.mt2.spi.common2.table.IMField.MDataType.BASEDATA_SINGLE) {
				cell2.setProperty("baseDataID", bf.getRelateTableName());
			}
			cell2.setProperty("readOnly", "false");
			// row2.insertCell(1, cell2);
		}
		if (maxNum < 7)
			maxNum = 7;
		table.setColumnWidth(0, new MLength(MSCALE.LETTER_WIDTH, maxNum - 1));
		table.setColumnWidth(1, new MLength(MSCALE.LETTER_WIDTH, 6));
		return templ;
	}

	public IStream<ITodoCategory> getCategorysRoughCount(Context context) {
		List<FApprovalDefine> list = context.getList(FApprovalDefine.class);
		if (list == null || list.isEmpty())
			return new TodoCategoryStream(null);

		List<ITodoCategory> categoryList = new ArrayList<ITodoCategory>();
		for (int i = 0; i < list.size(); i++) {
		//	List<String> imageIDS = new ArrayList<String>();
			FApprovalDefine define = list.get(i);
			FTaskListDefine ftaskDefine = context.get(FTaskListDefine.class, define.getTaskDefineID());
			TaskListDefine taskDefine = ftaskDefine.getTaskListDefine();
			MWorkflowUtil.clearConditionField(taskDefine);

			if (taskDefine.businessObjectList.size() == 0)
				continue;

			BillDefineLink[] defineLinks = null;
			// 获取单据关联
			try {
				String boInfoString = getBOInfo(define);
				List<OpenConfigInfo> infoList = MobileBillDefineOpenConfigUtils.toList(boInfoString);
				String boEditInfoString = getBOInfo(define);
				List<OpenConfigInfo> editInfoList = MobileBillDefineOpenConfigUtils.toList(boEditInfoString);
				defineLinks = new BillDefineLink[infoList.size()];
				for (int m = 0; m < infoList.size(); m++) {
					if (infoList.get(m) != null) {
						String billModel = infoList.get(m).getBillModelID().toString();
						String billDefine = infoList.get(m).getMobileBillDefineID().toString();
						String billEditDefine = null;
						for (OpenConfigInfo editInfo : editInfoList) {
							if (StringUtil.equals(editInfo.getBillModelID().toString(), billModel)) {
								billEditDefine = editInfo.getMobileBillDefineID().toString();
							}
						}
						defineLinks[m] = new BillDefineLink(billModel, billEditDefine, billDefine);

					}
				}
			} catch (Exception e) {
				MobileLog.logError(e);
			}
			// 获取单据关联
			INamable[] BOInfos = null;
			try {
				String boInfoString = getBOInfo(define);
				List<OpenConfigInfo> infoList = MobileBillDefineOpenConfigUtils.toList(boInfoString);
				BOInfos = new INamable[infoList.size()];
				for (int m = 0; m < infoList.size(); m++) {
					if (infoList.get(m) != null) {
						String buttonName = infoList.get(m).getBillModelID().toString();
						String buttonTitle = infoList.get(m).getMobileBillDefineID().toString();
						BOInfos[m] = new Nameable(buttonName, buttonTitle);
					}
				}
			} catch (Exception e) {
				MobileLog.logError(e);
			}
			// 获取列表界面模板
			String templateInfo = getListMTemplateInfo(define);
			MShowTemplate template = new MShowTemplate();
			com.jiuqi.xlib.json.JSONObject templateJSON = null;
			try {
				if (templateInfo == null) {
					template = new MShowTemplate();
				} else {
					templateJSON = new com.jiuqi.xlib.json.JSONObject(templateInfo);
					template.deserialize(templateJSON);
				}

			} catch (Exception e) {
				MobileLog.logError(e);
			}
			String enCodedCate = MWFUtil.encodeCate(define.getTaskDefineID().toString(), context);
			TodoCategory category = new TodoCategory(enCodedCate);
			category.setTitle(define.getTitle());
			// 设置审批结束进入列表
			category.setCloseAfterComplete(isCloseAfterComplete(define));
			category.setListTemplate(template);
			category.setBoInfo(BOInfos);
			category.setUpdateTime(define.getModifyDate());
			category.setDefaultOpen(true);
			if (defineLinks != null) {
				category.setBillDefineLinks(defineLinks);
			}
			category.setQueryTemplate(getConditionData(context, category.getID()));
			if (ListenerGatherer.getiTodoListener() != null) {
				boolean resultIsMulti = ListenerGatherer.getiTodoListener().isMulti(category.getID());
				category.setMulti(resultIsMulti);
			} else {
				category.setMulti(true);
			}

			categoryList.add(category);
		}

		ITodoCategory[] categoryArray = new ITodoCategory[categoryList.size()];
		return new TodoCategoryStream(categoryList.toArray(categoryArray));
	}

	private boolean isCloseAfterComplete(FApprovalDefine define) {
		// TODO Auto-generated method stub
		try {
			byte[] configBytes = define.getConfigBytes();
			if (configBytes != null) {
				String config = define.getConfig();
				JSONObject jo = new JSONObject(config);
				if (jo.has(FApprovalDefine.ATT_CLOSEAFTERCOMPLETE)) {
					return jo.optBoolean(FApprovalDefine.ATT_CLOSEAFTERCOMPLETE, false);
				}
			}
		} catch (Exception e) {
			MobileLog.logError(e);
		}
		return false;
	}

	private ITodoCategory getTodoCategoryByApprovalDefineRoughCount(Context context, FApprovalDefine define) {
		FTaskListDefine ftaskDefine = context.get(FTaskListDefine.class, define.getTaskDefineID());
		TaskListDefine taskDefine = ftaskDefine.getTaskListDefine();
		MWorkflowUtil.clearConditionField(taskDefine);
		List<IWorkCategory> workCategories = WorkflowDefineManager.getIworkCategory(context);
		int count = 0;
		for (WFBusinessObjectXML obj : taskDefine.businessObjectList) {
			if (obj == null) {
				continue;
			}
			for (IWorkCategory category : workCategories) {
				if (category.getCategory().equals(obj.getID().toString())) {
					count += category.getWorkCount();
				}
			}
		}

		INamable[] actions = null;
		List<INamable> namableList = new ArrayList<INamable>();
		if (taskDefine.actionList.size() > 0) {
			for (int j = 0; j < taskDefine.actionList.size(); j++) {
				if ("BatchApproveAction".equalsIgnoreCase(taskDefine.actionList.get(j).getName()) || "BatchWFRejectAction".equalsIgnoreCase(taskDefine.actionList.get(j).getName())) {
					INamable action = new Nameable(taskDefine.actionList.get(j).getName(), taskDefine.actionList.get(j).getTitle());

					namableList.add(action);
				}
			}

			actions = namableList.toArray(new INamable[namableList.size()]);
		}
		// 获取自定义按钮
		MCustomButton[] custombuttons = null;
		try {
			String customButtonInfo = getButtonInfo(define);
			ButtonInfoList bil = new ButtonInfoList(customButtonInfo);
			;
			ArrayList<ButtonInfoContainer> templist = bil.getSimpleList();
			custombuttons = new MCustomButton[templist.size()];
			for (int m = 0; m < templist.size(); m++) {
				if (templist.get(m).info != null) {
					String buttonName = templist.get(m).info.getName();
					String buttonTitle = templist.get(m).info.getTitle();
					if (StringUtil.isEmpty((buttonTitle))) {
						TodoActionGather gather = getActionGather();
						for (Action a : gather.getList()) {
							if (a.getName().equals(templist.get(m).info.getActionName())) {
								buttonTitle = a.getTitle();
							}
						}
					}
					custombuttons[m] = new MCustomButton(buttonName, templist.get(m).info.getTitle(), templist.get(m).info.getImage());
				}
			}
		} catch (JSONException e) {
			MobileLog.logError(e);
		}
		// 获取单据关联
		INamable[] BOInfos = null;
		try {
			String boInfoString = getBOInfo(define);
			List<OpenConfigInfo> infoList = MobileBillDefineOpenConfigUtils.toList(boInfoString);
			BOInfos = new INamable[infoList.size()];
			for (int m = 0; m < infoList.size(); m++) {
				if (infoList.get(m) != null) {
					String buttonName = infoList.get(m).getBillModelID().toString();
					String buttonTitle = infoList.get(m).getMobileBillDefineID().toString();
					BOInfos[m] = new Nameable(buttonName, buttonTitle);
				}
			}
		} catch (Exception e) {
			MobileLog.logError(e);
		}
		// 获取列表界面模板
		String templateInfo = getListMTemplateInfo(define);
		MShowTemplate template = new MShowTemplate();
		com.jiuqi.xlib.json.JSONObject templateJSON = null;
		try {
			if (templateInfo == null) {
				template = new MShowTemplate();
			} else {
				templateJSON = new com.jiuqi.xlib.json.JSONObject(templateInfo);
				template.deserialize(templateJSON);
			}

		} catch (Exception e) {
			MobileLog.logError(e);
		}
		String enCodedCate = MWFUtil.encodeCate(define.getTaskDefineID().toString(), context);
		TodoCategory category = new TodoCategory(enCodedCate);
		category.setTitle(define.getTitle());

		category.setListTemplate(template);
		category.setTodoCount(count);
		category.setActions(actions);
		category.setBoInfo(BOInfos);
		category.setCustomButton(custombuttons);
		category.setDefaultOpen(true);
		category.setQueryTemplate(getConditionData(context, category.getID()));
		if (ListenerGatherer.getiTodoListener() != null) {
			boolean resultIsMulti = ListenerGatherer.getiTodoListener().isMulti(category.getID());
			category.setMulti(resultIsMulti);
		} else {
			category.setMulti(true);
		}

		return category;
	}

	public ITodoItem getTodoDetail(String id, Context context ) {
	//	GUID userID = context.getLogin().getUser().getID();
		IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context, id);

		List<GUID> tasklistDefineID = getTaskListDefineID(context, GUID.valueOf(iworkItem.getWorkCategory()));
		FApprovalDefine approvalDefine = getWorkFlowDefineByCategoryID(context, tasklistDefineID);
		String cate = approvalDefine.getTaskDefineID().toString();
		String businessObjectID = MWorkflowUtil.getBusinessObjectIDByTaskDefineID(context, GUID.valueOf(cate));

		//String encoded = MWFUtil.encodeCate(cate, context);

		Button[] buttons = WorkflowDefineManager.getBindingUIButtonsByNode(iworkItem.getActiveNode(), GUID.valueOf(iworkItem.getWorkCategory()));

		if (buttons == null || buttons.length == 0)
			return null;

		INamable[] actions = new INamable[buttons.length];

		for (int i = 0; i < buttons.length; i++) {
			INamable namable = new Nameable(buttons[i].name, buttons[i].title);
			actions[i] = namable;
		}

		TodoItem todoItem = new TodoItem();
		todoItem.setId(id);
		
		GUID billDataID = GUID.valueOf((String) iworkItem.getProcessInstance().getGUIDRef()); // 单据数据的guid
		GUID billDefineID = GUID.valueOf(iworkItem.getWorkCategory());

		FBillDefine billDefine = BillCentre.findBillDefine(ContextManager.getCurrentContext(), billDefineID);
		BillModel model = BillCentre.createBillModel(ContextManager.getCurrentContext(), billDefine);
		model.load(billDataID);

		//Map<String, Field> map = model.getData().getMaster().getTable().getFieldMap();
		List<BOResultField> fieldList = MWorkflowUtil.getQueryFields(context, cate);

		if (fieldList.isEmpty()) {
			todoItem.setState(ITodoItem.STATE_FAILD);
			return todoItem;
		}

		for (BOResultField resultField : fieldList) {
			String fieldName = MWFUtil.getFieldName(resultField);
			if(model.getData().getMaster().getTable().find(resultField.getFieldName())!=null){
				Object ob=model.getData().getMaster().getFieldValue(resultField.getFieldName());
				String fieldValue = MWFUtil.format(context, resultField, ob== null ? "" : ob);
				todoItem.setProperty(fieldName, fieldValue);
			}
			
		}

		todoItem.setBillDefineID(businessObjectID);
		todoItem.setBillDataID(billDataID.toString());
		return todoItem;
	}
}
