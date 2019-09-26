package com.jiuqi.mt2.dna.mobile.wxapproval.service;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.workflowmanager.define.intf.facade.FBusinessInstanceAndWorkItem;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.task.BaseApprovalTask;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.util.WorkflowRunUtil;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.da.DBCommand;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.Service;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.workflow.engine.EnumWorkItemState;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.todo.facade.FApprovalDefine;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.ApprovalPropertieInfo;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.BillCommon;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.MT2Common;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXWorkflowCommon;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXTable;
import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;
import com.jiuqi.mt2.dna.mobile.wxapproval.entity.StaffEntity;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXApprovalPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXApprovedPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXErrorPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.task.ObtainApprovalBillTask;
import com.jiuqi.mt2.spi.bill.metadata.MobileBillDefine;
import com.jiuqi.mt2.spi.bill.model.BillData;
import com.jiuqi.xlib.utils.StringUtil;

public class ObtainApprovalBillService extends Service {
	protected ObtainApprovalBillService() {
		super("ObtainApprovalBillService");
	}

	private void openApprovedPage(Context context, IWorkItem iworkItem,
			ObtainApprovalBillTask task) throws ParseException {
		String workItemId = task.getWorkItemId();
		String key = task.getKey();
		MobileBillDefine billDefine = BillCommon.getMobileBillDefine(context,
				iworkItem);
		if (billDefine == null) {
			task.setErrorPage(new WXErrorPage("错误提示", "未找到对应待办或未配置详情界面!", ""));
			return;
		}
		BillData billData = BillCommon.getBillData(context, iworkItem,
				billDefine);
		FApprovalDefine fad = new MT2Common().getFApprovalDefine(iworkItem
				.getGuid().toString(), context);
		GUID billID = GUID.tryValueOf(billData.getMasterData().getId());
		Map<String, GUID> enclosureMap = new HashMap<String, GUID>();
		getEnclosureList(context, billID, enclosureMap);
		BillModel billModel = BillCommon.getBillModel(context, iworkItem);
		billModel.getDefine().getEditUI2().toString().contains("url");
		Map<String, String> enclosureUrl = null;
		if (enclosureMap.size() != 0) {
			enclosureUrl = new HashMap<String, String>();
			for (String enclosureName : enclosureMap.keySet()) {
				String url = openViewFile(workItemId,
						enclosureMap.get(enclosureName));
				enclosureUrl.put(enclosureName, url);
			}
		}
		Map<String, List<String>> enclosureMap1 = new HashMap<String, List<String>>();
		getEnclosureList1(context, billID, enclosureMap1);
		String flag = "flase";
		Map<String, String> enclosureUrl1 = null;
		if (enclosureMap1.size() != 0) {
			enclosureUrl1 = new HashMap<String, String>();
			for (String enclosureName : enclosureMap1.keySet()) {
				// 获取当前登录人员ID
				GUID loginUser = context.getLogin().getUser().getID();
				// 申请人
				String sqr = billData.getMasterData().getField("SQR")
						.getStringValue();
				// 制单人
				String createuser = billData.getMasterData()
						.getField("CREATEUSERID").getStringValue();
				GUID sqr1 = getLinkUser(sqr);
				List<String> list = enclosureMap1.get(enclosureName);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
				// 开始日期
				String startdate = list.get(1);
				// 结束日期
				String enddate = list.get(2);
				Date d = new Date();
				// 当前日期
				String nowDate = sdf.format(d);
				// 工作流状态
				String workflowstate = list.get(3);
				if ((sqr1.equals(loginUser) || createuser.equals(loginUser
						.toString()))
						&& (sdf.parse(startdate).before(sdf.parse(nowDate)) || sdf
								.parse(startdate).getTime() == sdf.parse(
								nowDate).getTime())
						&& (sdf.parse(enddate).after(sdf.parse(nowDate)) || sdf
								.parse(enddate).getTime() == sdf.parse(nowDate)
								.getTime()) && workflowstate.equals("2")) {
					flag = "true";

				}
				String url = openViewFile1(workItemId, enclosureName, flag);
				enclosureUrl1.put(enclosureName, url);
			}
		}
		
		WXApprovedPage page = new WXApprovedPage();
		page.addTable(new WXTable(billData, billDefine));
		page.setBillDefine(billDefine);
		if (enclosureUrl != null) {
			page.setEnclosureMap(enclosureUrl);
		} else {
			page.setEnclosureMap(enclosureUrl1);
			page.setFlag(flag);
		}
		page.setWxbillTitle((fad != null) ? fad.getTitle() : billDefine
				.getMasterPage().getReferenceTable().getTitle());
		page.setWorkflowhistory(new WXWorkflowCommon()
				.getWorkflowHistoryToPage(iworkItem, billDefine.getTempId(),
						context));
		page.setKey(key);
		task.setApprovaledPage(page);
	}

	@Publish
	protected class ObtApprovalBill
			extends
			TaskMethodHandler<ObtainApprovalBillTask, ObtainApprovalBillTask.Method> {
		protected ObtApprovalBill() {
			super(ObtainApprovalBillTask.Method.ObtainApprovalBill,
					new ObtainApprovalBillTask.Method[0]);
		}

		@SuppressWarnings("restriction")
		protected void handle(Context context, ObtainApprovalBillTask task) {
			String workItemId;
			try {
				workItemId = task.getWorkItemId();
				String userName = task.getCode();
				String key = task.getKey();
				FUser fuser = (FUser) context.find(FUser.class,
						userName.toUpperCase());
				User user = (User) context.find(User.class, fuser.getGuid());
				context.changeLoginUser(user);
				context.getLogin().setUserCurrentOrg(fuser.getBelongedUnit());
				IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context,
						workItemId);
				if (iworkItem == null) {
					task.setErrorPage(new WXErrorPage("单据已取回", "该待办已被取回", ""));
					return;
				}
				if (iworkItem.getState() == EnumWorkItemState.COMPLETE) {
					openApprovedPage(context, iworkItem, task);
					return;
				}
				MobileBillDefine billDefine = BillCommon
						.getMobileBillDefineInfo(context, iworkItem);

				if (billDefine == null) {
					task.setErrorPage(new WXErrorPage("错误提示",
							"未找到对应待办或未配置详情界面!", ""));
					return;
				}
				if (iworkItem.getActiveNode().getMultiSubscriptAction() >= 1) {
					BaseApprovalTask baseApprTask = new BaseApprovalTask();
					baseApprTask.businessInstanceID = GUID.valueOf(iworkItem
							.getProcessInstance().getGUIDRef());
					baseApprTask.businessObjectID = GUID.valueOf(billDefine
							.getTempId());
					List<FBusinessInstanceAndWorkItem> lfbiaw = WXBillApproval
							.getFBApprovalHistoryList(context, baseApprTask);
					String xml = "";
					for (int i = 0; i < lfbiaw.size(); ++i)
						if (((FBusinessInstanceAndWorkItem) lfbiaw.get(i))
								.getWorkItemID().toString()
								.equals(iworkItem.getGuid().toString()))
							xml = ((FBusinessInstanceAndWorkItem) lfbiaw.get(i))
									.getAllsuggest();
					boolean flags = false;
					if (StringUtil.isNotEmpty(xml)) {
						DocumentBuilderFactory factory = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder = factory.newDocumentBuilder();
						StringReader read = new StringReader(xml);
						InputSource source = new InputSource(read);
						Document document = builder.parse(source);
						Element root = document.getDocumentElement();
						org.w3c.dom.NodeList list = root
								.getElementsByTagName("suggest");
						for (int i = 0; i < list.getLength(); ++i) {
							Element sug = (Element) list.item(i);
							String userid = sug.getAttribute("userid");
							StaffEntity staff = new WXWorkflowCommon()
									.getStaff(userid, context);
							if (userName.equals(staff.getNamecode()))
								flags = true;
						}
					}
					if (flags) {
						openApprovedPage(context, iworkItem, task);
						return;
					}
				}
				FApprovalDefine fad = new MT2Common().getFApprovalDefine(
						iworkItem.getGuid().toString(), context);
				BillData billData = BillCommon.getBillData(context, iworkItem,
						billDefine);
				GUID billID = GUID.tryValueOf(billData.getMasterData().getId());

				Map<String, GUID> enclosureMap = new HashMap<String, GUID>();
				getEnclosureList(context, billID, enclosureMap);
				BillModel billModel = BillCommon.getBillModel(context,
						iworkItem);
				billModel.getDefine().getEditUI2().toString().contains("url");
				Map<String, String> enclosureUrl = null;
				if (enclosureMap.size() != 0) {
					enclosureUrl = new HashMap<String, String>();
					for (String enclosureName : enclosureMap.keySet()) {
						String url = openViewFile(workItemId,
								enclosureMap.get(enclosureName));
						enclosureUrl.put(enclosureName, url);
					}
				}
				Map<String, List<String>> enclosureMap1 = new HashMap<String, List<String>>();
				getEnclosureList1(context, billID, enclosureMap1);
				String flag = "flase";
				Map<String, String> enclosureUrl1 = null;
				if (enclosureMap1.size() != 0) {
					enclosureUrl1 = new HashMap<String, String>();
					for (String enclosureName : enclosureMap1.keySet()) {
						// 获取当前登录人员ID
						GUID loginUser = context.getLogin().getUser().getID();
						// 申请人
						String sqr = billData.getMasterData().getField("SQR")
								.getStringValue();
						// 制单人
						String createuser = billData.getMasterData()
								.getField("CREATEUSERID").getStringValue();
						GUID sqr1 = getLinkUser(sqr);
						List<String> list = enclosureMap1.get(enclosureName);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd ");
						// 开始日期
						String startdate = list.get(1);
						// 结束日期
						String enddate = list.get(2);
						Date d = new Date();
						// 当前日期
						String nowDate = sdf.format(d);
						// 工作流状态
						String workflowstate = list.get(3);
						if ((sqr1.equals(loginUser) || createuser
								.equals(loginUser.toString()))
								&& (sdf.parse(startdate).before(
										sdf.parse(nowDate)) || sdf.parse(
										startdate).getTime() == sdf.parse(
										nowDate).getTime())
								&& (sdf.parse(enddate)
										.after(sdf.parse(nowDate)) || sdf
										.parse(enddate).getTime() == sdf.parse(
										nowDate).getTime())
								&& workflowstate.equals("2")) {
							flag = "true";
						}
						String url = openViewFile1(workItemId, enclosureName,
								flag);
						enclosureUrl1.put(enclosureName, url);
					}
				}
				// WXPlaintextScramble scramble = new WXPlaintextScramble(
				// userName, workItemId);
				WXApprovalPage page = new WXApprovalPage();
				page.addTable(new WXTable(billData, billDefine, context));
				page.setApab(new ApprovalPropertieInfo(context, iworkItem));
				page.setKey(key);
				page.setContext(context);
				if (enclosureUrl != null) {
					page.setEnclosureMap(enclosureUrl);
				} else {
					page.setEnclosureMap(enclosureUrl1);
					page.setFlag(flag);
				}
				page.setIworkItem(iworkItem);
				page.setBillDefine(billDefine);
				page.setWxbillTitle((fad != null) ? fad.getTitle() : billDefine
						.getMasterPage().getReferenceTable().getTitle());
				page.setWorkflowhistory(new WXWorkflowCommon()
						.getWorkflowHistoryToPage(iworkItem,
								billDefine.getTempId(), context));
				task.setApprpvalPage(page);
			} catch (Exception e) {
				e.printStackTrace();
				task.setErrorPage(new WXErrorPage(
						"异常提示",
						"ObtainApprovalBillTask.Method.ObtainApprovalBill捕获未知异常信息",
						""));
			}
		}
	}

	@Publish
	protected class ObtApprovaledBill
			extends
			Service.TaskMethodHandler<ObtainApprovalBillTask, ObtainApprovalBillTask.Method> {
		protected ObtApprovaledBill() {
			super(ObtainApprovalBillTask.Method.ObtainApprovaledBill,
					new ObtainApprovalBillTask.Method[0]);
		}

		protected void handle(Context context, ObtainApprovalBillTask task) {
			String workItemId;
			try {
				workItemId = task.getWorkItemId();
				String key = task.getKey();
				String userName = task.getCode();
				IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context,
						workItemId);
				if (iworkItem == null) {
					task.setErrorPage(new WXErrorPage("单据已取回", "该待办已被取回", ""));
					return;
				}
				MobileBillDefine billDefine = BillCommon
						.getMobileBillDefineInfo(context, iworkItem);
				if (billDefine == null) {
					task.setErrorPage(new WXErrorPage("错误提示",
							"未找到对应待办或未配置详情界面!", ""));
					return;
				}
				BillData billData = BillCommon.getBillData(context, iworkItem,
						billDefine);
				FApprovalDefine fad = new MT2Common().getFApprovalDefine(
						iworkItem.getGuid().toString(), context);

				// WXPlaintextScramble scramble = new WXPlaintextScramble(
				// userName, workItemId);

				FUser fuser = (FUser) context.find(FUser.class,
						userName.toUpperCase());
				User user = (User) context.find(User.class, fuser.getGuid());
				context.changeLoginUser(user);
				context.getLogin().setUserCurrentOrg(fuser.getBelongedUnit());

				WXApprovedPage page = new WXApprovedPage();
				page.addTable(new WXTable(billData, billDefine));
				page.setBillDefine(billDefine);
				page.setWxbillTitle((fad != null) ? fad.getTitle() : billDefine
						.getMasterPage().getReferenceTable().getTitle());
				page.setKey(key);
				page.setWorkflowhistory(new WXWorkflowCommon()
						.getWorkflowHistoryToPage(iworkItem,
								billDefine.getTempId(), context));
				task.setApprovaledPage(page);
			} catch (Exception e) {
				e.printStackTrace();
				task.setErrorPage(new WXErrorPage(
						"异常提示",
						"ObtainApprovalBillTask.Method.ObtainApprovaledBill捕获未知异常信息",
						""));
			}
		}
	}

	protected String openViewFile(String workItemId, GUID recid) {
		String urlStr = null;
		urlStr = String.format(Constants.VIEWDOWNLOAD + "?recid=" + recid
				+ "&workItemId=" + workItemId);
		return urlStr;
	}

	protected String openViewFile1(String workItemId, String fileName,
			String flag) {
		String urlStr = null;
		urlStr = String.format(Constants.VIEWDOWNLOAD + "?fileName=" + fileName
				+ "&workItemId=" + workItemId + "&flag=" + flag);
		return urlStr;
	}

	private void getEnclosureList(Context context, GUID billID,
			Map<String, GUID> enclosureMap) {
		StringBuffer getEnclosureSql = new StringBuffer();
		getEnclosureSql.append("define query getEnclosures(@billID guid) \n");
		getEnclosureSql.append("begin \n");
		getEnclosureSql.append("  select t.ENCLOSURENAME as ename, \n");
		getEnclosureSql
				.append(" t.RECID as recid from G1038_ENCLOSURE as t \n");
		getEnclosureSql.append("  where 1 = 1 \n");
		getEnclosureSql.append("    and t.BILLID = @billID \n");
		getEnclosureSql.append("    and t.ISBILLSAVE = 1 \n");
		getEnclosureSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(getEnclosureSql);
		dbCommand.setArgumentValues(billID);
		RecordSet recordSet = dbCommand.executeQuery();
		while (recordSet.next()) {
			enclosureMap.put(recordSet.getFields().get(0).getString(),
					recordSet.getFields().get(1).getGUID());
		}
		dbCommand.unuse();
	}

	private void getEnclosureList1(Context context, GUID billID,
			Map<String, List<String>> enclosureMap) {
		StringBuffer getEnclosureSql = new StringBuffer();
		getEnclosureSql.append("define query getEnclosures(@billID guid) \n");
		getEnclosureSql.append("begin \n");
		getEnclosureSql
				.append("  select  case when  t.zsmc is null   then    b.stdname      when t.htmc is null   then a.stdname  end  as ename, \n");
		getEnclosureSql
				.append(" t.savepath as recid,t.STARTDAY as startdate,t.ENDDAY as enddate , sqd.WORKFLOWSTATE as WORKFLOWSTATE       from HT_JYDYSQD as sqd left join    HT_JYDYSQD_ITEM as t  on sqd.recid=t.mrecid left join ZX_GSZZRYDJB as a  on a.recid =t.zsmc left join MD_CONTRACT as b on b.recid =t.HTMC \n");
		getEnclosureSql.append("  where 1 = 1 \n");
		getEnclosureSql.append("    and sqd.recid  = @billID \n");
		getEnclosureSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(getEnclosureSql);
		dbCommand.setArgumentValues(billID);
		RecordSet recordSet = dbCommand.executeQuery();
		while (recordSet.next()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
			List<String> list = new ArrayList<>();
			list.add(recordSet.getFields().get(1).getString());
			list.add(sdf.format(recordSet.getFields().get(2).getDate()));
			list.add(sdf.format(recordSet.getFields().get(3).getDate()));
			list.add(String.valueOf(recordSet.getFields().get(4).getInt()));
			enclosureMap.put(recordSet.getFields().get(0).getString(), list);
		}
		dbCommand.unuse();
	}

	private GUID getLinkUser(String recid) {
		ContextSPI context = AppUtil.getDefaultApp().getSystemSession()
				.newContext(false);
		GUID user = null;
		StringBuffer getEnclosureSql = new StringBuffer();
		getEnclosureSql.append("define query getEnclosures(@recid guid) \n");
		getEnclosureSql.append("begin \n");
		getEnclosureSql.append("  select t.linkuser as ename  \n");
		getEnclosureSql.append("  from  md_staff as t \n");
		getEnclosureSql.append("  where 1 = 1 \n");
		getEnclosureSql.append("    and t.recid = @recid \n");
		getEnclosureSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(getEnclosureSql);
		dbCommand.setArgumentValues(GUID.valueOf(recid));
		RecordSet recordSet = dbCommand.executeQuery();
		while (recordSet.next()) {
			user = recordSet.getFields().get(0).getGUID();
		}
		dbCommand.unuse();
		return user;
	}

}