package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jiuqi.dna.bap.authority.intf.facade.FOrgIdentity;
import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.bap.bill.common.model.BillCentre;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.bill.intf.facade.model.FBillDefine;
import com.jiuqi.dna.bap.bill.intf.impl.model.BillDefineImpl;
import com.jiuqi.dna.bap.model.common.define.base.Field;
import com.jiuqi.dna.bap.model.common.define.intf.IFormula;
import com.jiuqi.dna.bap.model.common.type.ModelState;
import com.jiuqi.dna.bap.multorg.common.componentbased.MultOrgComponentBased;
import com.jiuqi.dna.bap.multorg.intf.orgtree.FOrgNode;
import com.jiuqi.dna.bap.workflowmanager.common.parse.Button;
import com.jiuqi.dna.bap.workflowmanager.define.common.WorkflowDefineManager;
import com.jiuqi.dna.bap.workflowmanager.define.intf.facade.FBusinessInstanceAndWorkItem;
import com.jiuqi.dna.bap.workflowmanager.execute.common.BusinessProcessManager;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.WorkflowRunUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.ApprovalProperties;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.BOResultField;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.entity.TaskListDefine;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FRecord;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FTaskListDefine;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.task.BaseApprovalTask;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.def.query.QueryStatementDeclare;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.dna.core.spi.application.Session;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.workflow.engine.WorkItem;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.bill.intf.facade.FBillStyleTemplate;
import com.jiuqi.mt2.dna.mobile.todo.facade.FApprovalDefine;
import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;
import com.jiuqi.mt2.dna.mobile.wxapproval.entity.StaffEntity;
import com.jiuqi.mt2.dna.mobile.wxapproval.service.WXBillApproval;
import com.jiuqi.mt2.dna.mobile.wxapproval.util.BillUtils;
import com.jiuqi.mt2.dna.service.todo.impl.Nameable;
import com.jiuqi.mt2.dna.service.todo.util.MWFUtil;
import com.jiuqi.mt2.dna.service.todo.util.MWorkflowUtil;
import com.jiuqi.mt2.spi.ICallMonitor;
import com.jiuqi.mt2.spi.bill.metadata.MobileBillDefine;
import com.jiuqi.mt2.spi.bill.model.BillData;
import com.jiuqi.mt2.spi.bill.model.DetailData;
import com.jiuqi.mt2.spi.call.IStream;
import com.jiuqi.mt2.spi.common2.impl.MFieldConstraint;
import com.jiuqi.mt2.spi.common2.impl.MInputDataField;
import com.jiuqi.mt2.spi.common2.listener.MInputValueChangedEvent;
import com.jiuqi.mt2.spi.common2.table.IMField;
import com.jiuqi.mt2.spi.common2.table.impl.MField;
import com.jiuqi.mt2.spi.common2.table.impl.MFieldsCollection;
import com.jiuqi.mt2.spi.portal.model.FUserInfo;
import com.jiuqi.mt2.spi.portal.model.impl.UserInfoImpl;
import com.jiuqi.mt2.spi.todo.model.ITodoCategory;
import com.jiuqi.mt2.spi.todo.model.ITodoItem;
import com.jiuqi.mt2.spi.todo.model.impl.BillDefineLink;
import com.jiuqi.mt2.spi.todo.model.impl.TodoCategory;
import com.jiuqi.mt2.spi.todo.model.impl.TodoItem;
import com.jiuqi.mt2.spi.todo.resource.TodoDefineListResource;
import com.jiuqi.xlib.INamable;
import com.jiuqi.xlib.dna.ContextManager;
import com.jiuqi.xlib.json.JSONArray;
import com.jiuqi.xlib.json.JSONException;
import com.jiuqi.xlib.json.JSONObject;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * 获取Bill相关数据的通用方法<br>
 * 用于简化代码中数据查找难度，提高代码编写效率
 * @author liuzihao
 * @通用方法扩展需求 1、使用尽量少的参数、尽量好获取的参数，去获取复杂难取、使用率高的数据 <br>
 *           2、将验证和判断尽量放到servlet里
 */
@SuppressWarnings("restriction")
public class BillCommon {

	/**
	 * 根据工作流节点 获取代办节点
	 * @param iworkItem
	 * @param context
	 * @return
	 */
	public static ITodoItem getTodoDetail(IWorkItem iworkItem, Context context) {
		GUID tasklistDefineID = getTaskListDefineID(context, GUID.valueOf(iworkItem.getWorkCategory()));
		String cate = tasklistDefineID.toString();
		String businessObjectID = MWorkflowUtil.getBusinessObjectIDByTaskDefineID(context, GUID.valueOf(cate));
		Button[] buttons = WorkflowDefineManager.getBindingUIButtonsByNode(
				iworkItem.getActiveNode(), GUID.valueOf(iworkItem.getWorkCategory()));
		
		INamable[] actions = new INamable[buttons.length];
		if (buttons != null && buttons.length >= 0){
			for (int i = 0; i < buttons.length; i++) {
				INamable namable = new Nameable(buttons[i].name, buttons[i].title);
				actions[i] = namable;
			}
		}

		TodoItem todoItem = new TodoItem();
		todoItem.setId(iworkItem.getGuid().toString());
		GUID billDataID = GUID.valueOf((String) iworkItem.getProcessInstance().getGUIDRef()); // 单据数据的guid
		GUID billDefineID = GUID.valueOf(iworkItem.getWorkCategory());

		FBillDefine billDefine = BillCentre.findBillDefine(ContextManager.getCurrentContext(), billDefineID);
		BillModel model = BillCentre.createBillModel(ContextManager.getCurrentContext(), billDefine);
		model.load(billDataID);

		Map<String, Field> map = model.getData().getMaster().getTable().getFieldMap();
		List<BOResultField> fieldList = MWorkflowUtil.getQueryFields(context,cate);
		if (fieldList.isEmpty()) {
			todoItem.setState(ITodoItem.STATE_FAILD);
			return todoItem;
		}
		for (BOResultField resultField : fieldList) {
			String fieldName = MWFUtil.getFieldName(resultField);
			String fieldValue = MWFUtil.format(context, resultField,map.get(fieldName) == null ? "" : map.get(fieldName));
			if(StringUtil.isEmpty(fieldValue)){
				fieldValue=MWFUtil.format(context, resultField,	map.get(resultField.getTableName()+resultField.getFieldName()) == null ? "" : map.get(resultField.getTableName()+resultField.getFieldName()));
			}
			todoItem.setProperty(fieldName, fieldValue);
		}

			todoItem.setBillDefineID(businessObjectID);
			todoItem.setBillDataID( billDataID.toString());
		return todoItem;
	}
	
	private static GUID getTaskListDefineID(Context context, GUID category) {
		List<FTaskListDefine> list = context.getList(FTaskListDefine.class);
		for (int i = 0; i < list.size(); i++) {
			TaskListDefine taskListDefine = list.get(i).getTaskListDefine();
			if (null != taskListDefine
					&& taskListDefine.businessObjectList != null) {
				for (int j = 0; j < taskListDefine.businessObjectList.size(); j++) {
					if (taskListDefine.businessObjectList.get(j).getID()
							.equals(category)) {
						return taskListDefine.getID();

					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取单据模型定义外观
	 * @param callMonitor
	 * @param workItemId
	 * @return
	 */
	public static FBillDefine getFBillDefine(Context context, IWorkItem workItem) {
		FBillDefine fbillDefine = context.get(FBillDefine.class, GUID.valueOf(workItem.getWorkCategory()));
		return fbillDefine;
	}

	/**
	 * 获取单据模型类
	 * @param callMonitor
	 * @param workItemId
	 * @return
	 */
	public static BillModel getBillModel(Context context, IWorkItem workItem) {
		FBillDefine fbillDefine = getFBillDefine(context, workItem);
		BillModel billModel = BillCentre.createBillModel(context, fbillDefine);
		return billModel;
	}

	/**
	 * 获取单据模型类
	 * @param callMonitor
	 * @param workItemId
	 * @return
	 */
	public static BillModel getBillModel2(Context context, IWorkItem iworkItem) {
		FBillDefine fbillDefine = getFBillDefine(context, iworkItem);
		BillModel billModel = (BillModel) ((BillDefineImpl) fbillDefine).createModel(context,GUID.valueOf(iworkItem.getProcessInstance().getGUIDRef()));
		return billModel;
	}
	
	
	/**
	 * 获取审批属性
	 * @param context
	 * @param workItemId
	 * @return
	 */
	public static ApprovalProperties getApprovalProperties(Context context, IWorkItem iworkItem) {
		WorkItem workitem = iworkItem.getAdaptor(WorkItem.class);

		GUID tasklistdefineID = null;
		List<FTaskListDefine> list = context.getList(FTaskListDefine.class);
		for (int i = 0; i < list.size(); i++) {
			TaskListDefine taskListDefine = list.get(i).getTaskListDefine();
			if (null != taskListDefine
					&& taskListDefine.businessObjectList != null) {
				for (int j = 0; j < taskListDefine.businessObjectList.size(); j++) {
					if (taskListDefine.businessObjectList.get(j).getID()
							.equals(GUID.valueOf(workitem.getWorkCategory()))) {
						tasklistdefineID = taskListDefine.getID();
					}
				}
			}
		}
		if (tasklistdefineID == null) {
			return null;
		}
		FTaskListDefine ftaskdefine = context.find(FTaskListDefine.class, tasklistdefineID);
		TaskListDefine tasklistdefine = ftaskdefine.getTaskListDefine();
		ApprovalProperties ap = tasklistdefine.getApprovalProperties();
		return ap;
	}
	
	/**
	 * 获取工作流当前节点绑定的按钮
	 * @param context
	 * @param workItemId
	 * @return
	 */
	public static Button[] getButton(Context context,IWorkItem iworkItem){
		WorkItem workitem = iworkItem.getAdaptor(WorkItem.class);
		Button[] buttons = WorkflowDefineManager.getBindingUIButtonsByNode(iworkItem.getActiveNode(), GUID.valueOf(workitem.getWorkCategory()));
		return buttons;
	}
	
	/**
	 * 获取工作流当前节点绑定的可修改字段
	 * @param context
	 * @param workItemId
	 * @return
	 */
	public static String[] getEditFields(Context context,IWorkItem iworkItem){
		WorkItem workitem = iworkItem.getAdaptor(WorkItem.class);
		String [] fields =WorkflowDefineManager.getBindingUIFields(iworkItem.getActiveNode(), GUID.valueOf(workitem.getWorkCategory()));
		return fields;
	}
	/**
	 * 获取审批定义
	 * @param context
	 * @param workItemId
	 * @return
	 */
	public TaskListDefine getTaskListDefine(Context context,IWorkItem iworkItem){
		List<FTaskListDefine> list = context.getList(FTaskListDefine.class);
		for (int i = 0; i < list.size(); i++) {
			TaskListDefine taskListDefine = list.get(i).getTaskListDefine();
			if (null != taskListDefine
					&& taskListDefine.businessObjectList != null) {
				for (int j = 0; j < taskListDefine.businessObjectList.size(); j++) {
					if (taskListDefine.businessObjectList.get(j).getID()
							.equals(iworkItem.getWorkCategory())) {
						return taskListDefine;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取某工作项的所有公式
	 * @param callMonitor
	 * @param workItemId
	 * @return
	 */
	public List<IFormula> getListIFormula(Context context,  IWorkItem workItem){
		BillModel billModel=getBillModel(context,workItem);
		List<IFormula> listIFormula=billModel.getDefine().getFormulas();
		return listIFormula;
	}
	
	/**
	 * 获取待审批事项
	 * @param context
	 * @param workItemId
	 * @deprecated
	 */
	public List<FRecord> getListFRecord(Context context,String workItemId){
		com.jiuqi.dna.workflow.intf.facade.IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context, workItemId);
		User user =context.getLogin().getUser();
		List<FRecord> list=BusinessProcessManager.get(context,GUID.valueOf(iworkItem.getWorkCategory()),user.getID()). getRecords();
		return list;
	}
	
	
	public static MobileBillDefine getMobileBillDefineInfo(Context context, IWorkItem workItem){
		MobileBillDefine mbd=null;
		FApprovalDefine fad=new MT2Common().getFApprovalDefine(workItem.getGuid().toString(),context);
		String jarray= new MT2Common().getShowMTemplateInfo(fad);
		String fbstid="";
		try {
			JSONArray ja=new JSONArray(jarray);
			for(int i=0;i<ja.length();i++){
				JSONObject jo=ja.getJSONObject(i);
				if(jo.has("bill_ID")){
					if(jo.get("bill_ID").equals(workItem.getWorkCategory())){
						if(jo.has("tpl_ID")){
							fbstid=jo.getString("tpl_ID");
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		
		if(StringUtil.isNotEmpty(fbstid)){
			FBillStyleTemplate fbst=context.find(FBillStyleTemplate.class,GUID.valueOf(fbstid));
			if(fbst!=null){
				mbd=fbst.getMobileBillDefine();
			}
		}
		return mbd;
	}
	
	/**
	 * 获取移动单据定义
	 * @param callMonitor
	 * @param workItem 
	 * @return
	 */
	public static MobileBillDefine getMobileBillDefine(Context context, IWorkItem workItem) {
		MobileBillDefine mbd=null;
		try {
			IStream<ITodoCategory> e = new MT2Common().getCategorysRoughCount(context);
			ITodoCategory[] itdcy = e.nexts();
			if(itdcy==null||itdcy.length==0){
				return null;
			}
			List<String> mbds=new ArrayList<String> ();
			for(ITodoCategory i : itdcy){
				BillDefineLink[] links =i.getBillDefineLinks();
				for(BillDefineLink l : links){
					if (l.getBillID().equals(workItem.getWorkCategory())) {
						mbds.add(l.getShowDefineID());
					}
				}
			}
			
			if(mbds==null||mbds.size()==0){
				return null;
			}
			List<MobileBillDefine> listtpl =new ArrayList<MobileBillDefine>();
			for(String m : mbds){
				FBillStyleTemplate fbst=context.find(FBillStyleTemplate.class,GUID.valueOf(m));
				if(fbst!=null){
					listtpl.add(fbst.getMobileBillDefine());
				}
			}
			if(listtpl==null || listtpl.size()==0){
				return null;
			}else if(listtpl.size()==2){
				mbd=listtpl.get(1);
			}else{
				mbd=listtpl.get(0);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mbd;
	}
	
	/**
	 * 获取MobileBillDefine
	 * @param billId
	 * @param monitor
	 * @return
	 */
	public static MobileBillDefine getBillDefine(String billId,Context context) {
		return getBillDefine(billId, context, -1);
	}
	
	/**
	 * 获取MobileBillDefine
	 * @param billId
	 * @param monitor
	 * @param updateTime
	 * @return
	 */
	public static MobileBillDefine getBillDefine(String billId, Context context, long updateTime) {
		// 查询单据模板
		FBillStyleTemplate tpl = context.find(FBillStyleTemplate.class, GUID.valueOf(billId));
		if (tpl == null) {
			return null;
		}
		MobileBillDefine billDefine = tpl.getMobileBillDefine();
		return billDefine;
	}
	
	//BillData--------------------------------
	
	/**
	 * 获取移动单据数据模型，包括主表、子表数据<br>
	 * 使用的mt2移动的包里的方法
	 * @param callMonitor
	 * @param workItemId
	 * @return
	 */
	public static BillData getBillData(Context context, IWorkItem workItem) {
		MobileBillDefine mobileBillDefine = getMobileBillDefine(context, workItem);
		if (mobileBillDefine == null) {
			return null;
		}
		TodoItem item = (TodoItem) new MT2Common().getTodoDetail(workItem.getGuid().toString(), context);
		BillData billData = getBillData(mobileBillDefine.getId(), item.getBillDataID(), context);
		return billData;
	}

	/**
	 * 获取移动单据数据模型，包括主表、子表数据<br>
	 * 使用的mt2移动的包里的方法
	 * @param callMonitor
	 * @param workItemId
	 * @param mobileBillDefine 必须保证MobileBillDefine不能空
	 * @return
	 */
	public static BillData getBillData(Context context, IWorkItem iworkItem, MobileBillDefine mobileBillDefine) {
		TodoItem item = (TodoItem)getTodoDetail(iworkItem, context);
		BillData billData = getBillData(mobileBillDefine.getId(), item.getBillDataID(), context);
		return billData;
	}
	
	/**
	 * 获取BillData
	 * @param billId
	 * @param dataId
	 * @param monitor
	 * @return
	 */
	public static BillData getBillData(String billId, String dataId, Context context) {

		MobileBillDefine mBillDefine = getBillDefine(billId, context);
		GUID tempId = GUID.valueOf(mBillDefine.getTempId());

		FBillDefine billDefine = context.get(FBillDefine.class, tempId);
		BillModel billModel = BillCentre.createBillModel(context, billDefine);
		
		String tableName = mBillDefine.getMasterPage().getReferenceTable().getName();
		BillData billData = new BillData(billId, tableName);

		if (StringUtil.isEmpty(dataId)) { // 新增，返回空BillData
			billModel.add();

			BillUtils.addID(billData.getMasterData(), GUID.randomID());
			BillUtils.loadBillData(context, billData, billModel);
			if (billModel.getModelState() == ModelState.EDIT || billModel.getModelState() == ModelState.NEW) {
				billData.setEditable(true);
			} else {
				billData.setEditable(false);
			}
			// 设置新增记录ID
			// 返回的billData中，如果有公式驱动字段，那么通知客户端监听此字段
			BillData driveFields = getDriveField(billDefine);
			addEvent(billData, driveFields);
			return billData;
		}
		boolean exist = billModel.load(GUID.valueOf(dataId));
		if (!exist) {
			billModel.add();

			BillUtils.addID(billData.getMasterData(), dataId);
			BillUtils.loadBillData(context, billData, billModel);
			if (billModel.getModelState() == ModelState.EDIT || billModel.getModelState() == ModelState.NEW) {
				billData.setEditable(true);
			} else {
				billData.setEditable(false);
				billData.clearEditableFieldNames();
			}
			// 设置新增记录ID
			// 返回的billData中，如果有公式驱动字段，那么通知客户端监听此字段
			BillData driveFields = getDriveField(billDefine);
			addEvent(billData, driveFields);
			return billData;
		}

		// 修改回显
		BillUtils.loadBillData(context, billData, billModel);
		if (billModel.getModelState() == ModelState.EDIT || billModel.getModelState() == ModelState.NEW) {
			billData.setEditable(true);
		} else {
			billData.setEditable(false);
			billData.clearEditableFieldNames();
		}
		// 返回的billData中，如果有公式驱动字段，那么通知客户端监听此字段
		BillData driveFields = getDriveField(billDefine);
		addEvent(billData, driveFields);
		return billData;
	}

	private static BillData getDriveField(FBillDefine billDefine) {
		BillData driveField = new BillData(GUID.emptyID.toString(), billDefine.getMasterTable().getName());
		driveField.setDetailDatas(new ArrayList<DetailData>());
		List<IFormula> formulas = billDefine.getFormulas();
		for (IFormula formula : formulas) {
			if (!formula.isUsed())
				continue;
			List<String> listFields = formula.getDriverFields();
			for (String tableAndField : listFields) {
				String tableName = getTableName(tableAndField);
				String fieldName = getFieldName(tableAndField);
				if (tableName == null || fieldName == null)
					continue;

				IMField field = new MField();

				if (tableName.equals(driveField.getTableName())) {// 主表
					driveField.getMasterData().addField(fieldName, field);
				} else {// 从子表中搜
					List<DetailData> detailDatas = driveField.getDetailDatas();
					DetailData detail = findInList(detailDatas, tableName);
					if (detail == null) {
						detail = new DetailData(tableName);
						detailDatas.add(detail);
					}
					if (detail.getDefaultValue() == null)
						detail.setDefaultValue(new MFieldsCollection());
					detail.getDefaultValue().addField(fieldName, field);
				}
			}
		}
		return driveField;
	}

	private static DetailData findInList(List<DetailData> detailDatas, String tableName) {
		if (StringUtil.isEmpty(tableName))
			return null;
		if (detailDatas == null || detailDatas.size() <= 0)
			return null;
		for (DetailData detail : detailDatas) {
			if (tableName.equalsIgnoreCase(detail.getTableName())) {
				return detail;
			}
		}
		return null;
	}

	private static String getFieldName(String tableAndField) {
		if (StringUtil.isEmpty(tableAndField))
			return null;
		String[] texts = tableAndField.split("\\.");
		if (texts == null || texts.length < 2)
			return null;
		return texts[1];
	}

	private static String getTableName(String tableAndField) {
		if (StringUtil.isEmpty(tableAndField))
			return null;
		String[] texts = tableAndField.split("\\.");
		if (texts == null || texts.length < 1)
			return null;
		return texts[0];
	}

	private static void addEvent(BillData billData, BillData driveFields) {
		// 主表
		for (String key : billData.getMasterData().getKeySet()) {

			if (driveFields.getMasterData().getField(key) == null)
				continue;

			MInputDataField field = (MInputDataField) billData.getMasterData().getField(key);
			MFieldConstraint constraint = field.getFieldConstraint();
			constraint.addEvents(new MInputValueChangedEvent());
		}
		List<DetailData> detailDatas = billData.getDetailDatas();
		for (DetailData detail : detailDatas) {
			String tableName = detail.getTableName();
			DetailData driveDetail = findInList(detailDatas, tableName);
			for (String key : detail.getDefaultValue().getKeySet()) {
				if (driveDetail.getDefaultValue().getField(key) == null || "RECID".equals(key))
					continue;
				MInputDataField input = (MInputDataField) driveDetail.getDefaultValue().getField(key);
				MFieldConstraint constraint = input.getFieldConstraint();// (MFieldConstraint)
																			// driveDetail.getDefaultValue().getField(
																			// key
																			// );
				constraint.addEvents(new MInputValueChangedEvent());
			}

		}

	}
	
	/**
	 * 获取Context同时设置用户默认组织机构
	 * @param userName
	 * @param callMonitor
	 * @return
	 */
	public static Context getContext(String userName,ICallMonitor callMonitor){
		Context context=callMonitor.getAdaptor(Context.class);
		FUser user = context.find(FUser.class, userName);
		if (user!=null&&user.getBelongedUnit() != null) {
			((Session) context.getLogin()).setUserCurrentOrg(user.getBelongedUnit());
		}
		return context;
	}
	
	
	
	//职员信息-----------------------------------
	
	/**
	 * 获取用户信息
	 * @param userName
	 * @param monitor
	 * @return UserInfoImpl
	 */
	public FUserInfo getFUserInfo(String userName, ICallMonitor cmonitor) {
		Context context = cmonitor.getAdaptor(Context.class);
		FUser user = context.find(FUser.class, userName);

		if (user != null) {
			UserInfoImpl userInfoImpl = new UserInfoImpl();
			userInfoImpl.setName(user.getName());
			userInfoImpl.setTitle(user.getTitle());
			if (user.getDefaultUnit() != null) {
				FOrgIdentity fOrgIdentity = context.find(FOrgIdentity.class,
						user.getGuid(), user.getDefaultUnit());
				if (fOrgIdentity != null) {
					((Session) context.getLogin()).setUserCurrentOrg(user
							.getDefaultUnit());
					userInfoImpl
							.setDefaultOrg(user.getDefaultUnit().toString());
					FOrgNode org = MultOrgComponentBased.getNodeByObjId(
							context, "MD_ORG", user.getDefaultUnit());
					userInfoImpl.setDefaultOrgTitle(org.getTitle());
				} else {
					FOrgNode node = context.find(FOrgNode.class,
							user.getDefaultUnit());
					if (node != null) {
						((Session) context.getLogin()).setUserCurrentOrg(user
								.getDefaultUnit());
						userInfoImpl.setDefaultOrg(user.getDefaultUnit().toString());
						FOrgNode org = MultOrgComponentBased.getNodeByObjId(
								context, "MD_ORG", user.getDefaultUnit());
						userInfoImpl.setDefaultOrgTitle(org.getTitle());
					}
				}
			}
			return userInfoImpl;
		}
		return null;
	}
	
	/**
	 * 根据用户ID查询职员信息
	 * @param context
	 * @param userid
	 * @return
	 */
	public static StaffEntity getStaffEntityByCode(Context context,GUID userid){
		StaffEntity sta=new StaffEntity();
		String staffSql = " define query stafftelQuery() begin \n select s.recid,s.stdname,s.tel,s.email  from md_staff as s where s.linkuser=guid'"+userid+"' \n end ";
		RecordSet staffSet = context.openQuery((QueryStatementDeclare)  context.parseStatement(staffSql));
		while (staffSet.next()) {
			sta.setRecid(staffSet.getFields().get(0).getString());
			sta.setName(staffSet.getFields().get(1).getString());
			sta.setTel(staffSet.getFields().get(2).getString());
			sta.setEmail(staffSet.getFields().get(3).getString());
		}
		sta.setLinkuser(userid.toString());
		
		
		return sta;
	}
	
	
	
	/**
	 * 判断节点是否为会签节点
	 * @throws Exception 
	 */
	public static boolean judgeSuggest(MobileBillDefine billDefine,IWorkItem iworkItem ,Context context,String code) throws Exception{
		BaseApprovalTask task = new BaseApprovalTask();
		task.businessInstanceID = GUID.valueOf(iworkItem.getProcessInstance().getGUIDRef());
		task.businessObjectID = GUID.valueOf(billDefine.getTempId());
		List<FBusinessInstanceAndWorkItem> lfbiaw=WXBillApproval.getFBApprovalHistoryList(context, task);
		String xml="";
		for(int i=0;i<lfbiaw.size();i++){
			if(lfbiaw.get(i).getWorkItemID().toString().equals(iworkItem.getGuid().toString())){
				xml=lfbiaw.get(i).getAllsuggest();
			}
		}
		boolean flag=false;
		if(StringUtil.isNotEmpty(xml)){
			DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();
			DocumentBuilder builder=factory.newDocumentBuilder();
			StringReader read = new StringReader(xml);
			InputSource source = new InputSource(read);
			Document document=builder.parse(source);
			Element root=document.getDocumentElement();
			NodeList list=root.getElementsByTagName("suggest");
			for(int i=0;i<list.getLength();i++){
				Element sug=(Element) list.item(i);
				String userid=sug.getAttribute("userid");
				StaffEntity staff=new WXWorkflowCommon().getStaff(userid,context);
				if(code.equals(staff.getNamecode())){
					flag=true;
				}
			}
		}
		return flag;
	}
	
}
