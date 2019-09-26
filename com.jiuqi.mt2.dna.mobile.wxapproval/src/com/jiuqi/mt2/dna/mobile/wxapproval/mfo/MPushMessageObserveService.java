package com.jiuqi.mt2.dna.mobile.wxapproval.mfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.bap.bill.common.model.BillCentre;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.bill.intf.facade.model.FBillDefine;
import com.jiuqi.dna.bap.workflowmanager.common.event.WorkflowManagerEvent;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.WorkflowRunUtil;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.Service;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.workflow.engine.EnumWorkItemState;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.dna.workflow.engine.object.ParticipantObject;
import com.jiuqi.mt2.dna.mobile.qiyehao.util.QiYEHAOUtil;
import com.jiuqi.mt2.dna.mobile.todo.facade.FApprovalDefine;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.BillCommon;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.MT2Common;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXPlaintextScramble;
import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;
import com.jiuqi.mt2.dna.mobile.wxapproval.util.WXFieldUtil;
import com.jiuqi.mt2.spi.bill.metadata.MobileBillDefine;
import com.jiuqi.mt2.spi.common2.table.IMShowTemplate;
import com.jiuqi.mt2.spi.common2.table.IMTable;
import com.jiuqi.mt2.spi.common2.table.IMTableCell;
import com.jiuqi.mt2.spi.todo.model.ITodoCategory;
import com.jiuqi.mt2.spi.todo.model.impl.TodoItem;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * 发送消息的服务类
 * 
 * @author liuzihao
 */
public class MPushMessageObserveService extends Service {

	protected MPushMessageObserveService() {
		super("MPushMessageObserveService");
	}

	/**
	 * 工作项激活完成事件，产生邮件短信通知
	 */
	@Publish
	protected final class WorkflowManagerEventFinishListener extends EventListener<WorkflowManagerEvent> {
		public WorkflowManagerEventFinishListener() {
			super(1.2f);
		}

		@SuppressWarnings("restriction")
		protected void occur(Context context, WorkflowManagerEvent event) throws Throwable {
			MT2Common mt2common =new MT2Common();
			try {
				if (event.eventType == WorkflowManagerEvent.EventType.WorkItemActiveFinish) {
					IWorkItem workItem = WorkflowRunUtil.loadWorkItem(context, event.itemID);
					if (workItem == null || workItem.getState() != EnumWorkItemState.ACTIVE) {
						return;
					}
					String dataId = workItem.getProcessInstance().getGUIDRef();
					if (StringUtil.isEmpty(dataId)) {
						return;
					}
					String category = workItem.getWorkCategory();
					List<GUID> tasklistDefineID = mt2common.getTaskListDefineID(context, GUID.valueOf(category));
					FApprovalDefine fDefine = mt2common.getWorkFlowDefineByCategoryID(context, tasklistDefineID);
					if (fDefine == null) { 
						return;
					}
					
					FBillDefine billDefine = context.get(FBillDefine.class, GUID.valueOf(category));
					BillModel billModel = BillCentre.createBillModel(context, billDefine);

					if (!billModel.load(GUID.valueOf(dataId))) {
						return;
					}
					MobileBillDefine mbd = BillCommon.getMobileBillDefine(context, workItem);
					if(mbd==null){ 
						return;
					}
					
					FApprovalDefine fad=new MT2Common().getFApprovalDefine(workItem.getGuid().toString(),context);
					String typeName = "EIP待审批事项";
					typeName=fad!=null?fad.getTitle():mbd.getMasterPage().getReferenceTable().getTitle();
					String tablecontext=getTableContext(workItem.getGuid().toString(),context);
					// 所有相关的审批人
					List<ParticipantObject> participants = workItem.getParticipants();
					for (ParticipantObject participantObject : participants) {
						FUser user = context.find(FUser.class, GUID.valueOf(participantObject.getUserguid()));
						StringBuilder msgBuilder = new StringBuilder()
							.append("「").append(typeName).append("」\n")
							.append(tablecontext)
							.append("<a href='").append(Constants.UrlApprovalBill).append("?key=")
							.append(new WXPlaintextScramble(user.getName(), workItem.getGuid().toString()).getResult()).append("'>点击此处进行审批...</a>");
						QiYEHAOUtil.sendMessage(user.getName(), msgBuilder.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getTableContext(String workItemId,Context context) {
		StringBuffer buffer = new StringBuffer("");
		ITodoCategory entity = new MT2Common().getTodoCategory(workItemId,context);	
		TodoItem collection = (TodoItem) new MT2Common().getTodoDetail(workItemId, context);
		if(entity==null){
			return buffer.toString();
		}
		IMShowTemplate showTemplate = entity.getListTemplate();
		if (showTemplate == null) {
			return buffer.toString();
		}
		IMTable table = showTemplate.getTable();
		int row = 0;
		int column = 0;
		if (table != null) {
			row = table.getRowCount();
			column = table.getColCount();
		}
		if (row <= 0 || column <= 0) {
			return buffer.toString();
		}

		for (int i = 0; i < 6; i++) {
			if (row < 6 && i >= row) {
				buffer.append("");
				continue;
			}
			// 列
			int colspan = 1;
			String td1_text = "";
			String td2_text = "";
			for (int j = 0; j < column; j++) {
				if (j > 1) {
					continue;
				}
				IMTableCell cell = table.getCell(i, j);
				String text = cell.getTitle();
				if (collection != null && text != null) {
					Pattern p = Pattern.compile("\\{#([^\\}]+)\\}");
					Matcher m = p.matcher(text);
					while (m.find()) {
						String value = WXFieldUtil.getFieldValueByName(m.group(1), collection);
						text = (StringUtil.isNotEmpty(value) ? text.replace(m.group(), value) : "");
					}
				}

				if (StringUtil.isEmpty(text)) {
					text = "";
				}
				if (j == 0) {
					if (cell.getRowSpan() > 1) {
						colspan = 2;
					}
					if (StringUtil.isNotEmpty(text)) {
						td1_text = text;
					}
				} else if (j == 1) {
					if (colspan == 2) {
						continue;
					} else {
						if (StringUtil.isNotEmpty(text)) {
							td2_text = text;
						}
					}
				}
			}
			if (colspan == 2) {
				buffer.append(StringUtil.isNotEmpty(td1_text) ? (td1_text + "\n") : "");
			} else if (colspan == 1) {
				buffer.append(td1_text).append(StringUtil.isNotEmpty(td1_text)?":":"").append(td2_text).append("\n");
			}
		}
		return buffer.toString();
	}
}