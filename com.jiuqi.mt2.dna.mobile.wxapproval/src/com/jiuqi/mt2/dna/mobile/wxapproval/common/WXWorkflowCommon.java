package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import java.io.StringReader;
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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jiuqi.dna.bap.workflowmanager.define.intf.facade.FBusinessInstanceAndWorkItem;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.ActionUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.WorkflowRunUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.task.BaseApprovalTask;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.def.query.QueryStatementDeclare;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.workflow.define.Node;
import com.jiuqi.dna.workflow.engine.BaseWorkItem;
import com.jiuqi.dna.workflow.engine.EnumWorkItemState;
import com.jiuqi.dna.workflow.engine.object.ParticipantObject;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.wxapproval.entity.StaffEntity;
import com.jiuqi.mt2.dna.mobile.wxapproval.service.WXBillApproval;
import com.jiuqi.xlib.utils.StringUtil;
/**
 * 工作流里用到的方法
 * @author liuzihao
 */
public class WXWorkflowCommon {
	/**
	 * @param lfbiaw
	 * @param context
	 * @return
	 */
	public String getWorkflowHistoryToPage(IWorkItem iworkItem,String billDefineID,Context context){
		BaseApprovalTask task = new BaseApprovalTask();
		task.businessInstanceID = GUID.valueOf(iworkItem.getProcessInstance().getGUIDRef());
		task.businessObjectID = GUID.valueOf(billDefineID);
		List<FBusinessInstanceAndWorkItem> lfbiaw=WXBillApproval.getFBApprovalHistoryList(context, task);
		List<FBusinessInstanceAndWorkItem> listfb=new ArrayList<FBusinessInstanceAndWorkItem>();
		FBusinessInstanceAndWorkItem fbiaw3=null;
		if(lfbiaw!=null && lfbiaw.size()>0){
			for(int i=0;i<lfbiaw.size();i++){
				IWorkItem workItem = WorkflowRunUtil.loadWorkItem(context, lfbiaw.get(i).getWorkItemID().toString());
				if(workItem!=null && workItem.getState()!=EnumWorkItemState.ACTIVE){
					listfb.add(lfbiaw.get(i));
				}else{
					fbiaw3=lfbiaw.get(i);
				}
			}
		}

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer workflow=new StringBuffer("<table cellspacing='0' cellpadding='0' width='100%;'>");

		if(listfb!=null && listfb.size()>0){
			for(int i=(listfb.size()-1);i>=0;i--){
				if(i==(listfb.size()-1)){
					String str= getnextapprovaler(listfb.get(listfb.size()-1),fbiaw3,context,iworkItem);
					if(StringUtil.isNotEmpty(str)){
						workflow.append("<tr><td class='workflow_left bgColorWhite'></td><td class='workflow_line bgColorWhite'>")
						.append("<div class='workflow_top_block'></div><div class='workflow_round_rad'></div></td>")
						.append("<td class='workflow_top_right bgColorWhite'>")
						.append(str).append("</td></tr>");
						if(i==0){
							workflow.append("<tr><td class='workflow_left bgColorGray'></td><td class='workflow_line bgColorGray lineColorGray'>")						
							.append("<div class='workflow_bottom_block'></div><div class='workflow_round_black'></div></td>")
							.append("<td class='workflow_right bgColorGray' style='border:0px;'>");
						}else{
							workflow.append("<tr><td class='workflow_left bgColorGray'></td><td class='workflow_line bgColorGray'>")
							.append("<div class='workflow_round_black'></div></td><td class='workflow_right bgColorGray'>");
						}
					}else{
						if(i==0){
							workflow.append("<tr><td class='workflow_left bgColorGray'></td><td class='workflow_line bgColorGray lineColorGray'>")						
							.append("<div class='workflow_top_block' style='background-color:rgb(240,240,240);'></div><div class='workflow_bottom_block'></div><div class='workflow_round_black'></div></td>")
							.append("<td class='workflow_right bgColorGray' style='border:0px;'>");
						}else{
							workflow.append("<tr><td class='workflow_left bgColorGray'></td><td class='workflow_line bgColorGray'>")
							.append("<div class='workflow_top_block' style='background-color:rgb(240,240,240);'></div><div class='workflow_round_black'></div></td><td class='workflow_right bgColorGray'>");
						}
					}
				}else if(i==0){
					workflow.append("<tr><td class='workflow_left bgColorGray'></td><td class='workflow_line bgColorGray lineColorGray'>")						
					.append("<div class='workflow_bottom_block'></div><div class='workflow_round_black'></div></td>")
					.append("<td class='workflow_right bgColorGray' style='border:0px;'>");
				}else{
					workflow.append("<tr><td class='workflow_left bgColorGray'></td><td class='workflow_line bgColorGray'>")
					.append("<div class='workflow_round_black'></div></td><td class='workflow_right bgColorGray'>");
				}
				workflow.append(createWorkflowContext(listfb.get(i),context,dateformat)).append("</td></tr>");
			}
		}
		workflow.append("</table>");
		return workflow.toString();
	}
	
	
	
	private String getnextapprovaler(FBusinessInstanceAndWorkItem fbiaw,FBusinessInstanceAndWorkItem fbiaw3,Context context,IWorkItem iworkItem){
		List<BaseWorkItem> lbwi=WorkflowRunUtil.getNextWorkItems(context,fbiaw.getWorkItemID());
		StringBuffer sbf=new StringBuffer();
		try{
			if(lbwi==null||lbwi.size()==0){
				if(fbiaw.getActionID()==99){
				}else{
					sbf.append("流程结束");
				}
			}else{
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				StringBuffer c=new StringBuffer();
				Map<String ,String> userids=new HashMap<String ,String>();
				if(fbiaw3!=null){
					String xml=fbiaw3.getAllsuggest();
					try {
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
							String value=sug.getAttribute("value");
							long approveDate=Long.parseLong(sug.getAttribute("approveDate"));
							int ActionID=Integer.parseInt(sug.getAttribute("ActionID"));
							StaffEntity staff=getStaff(userid,context);
							userids.put(userid, userid);
							if(ActionID==3){
								c.append("<strong>").append(staff.getName())
								.append("</strong>&nbsp;&nbsp;【<strong>")
								.append(ActionUtil.getActionTitle(context, ActionID))
								.append("</strong>】了申请<br>")
								.append("联系电话：<a href='tel:").append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("'>")
								.append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("</a><br>")
								.append("审批意见：").append(value).append("<br>")
								.append(dateformat.format(new Date(approveDate))).append("<br>");
							}else {
								c.append("<strong>").append(staff.getName())
								.append("</strong>&nbsp;&nbsp;已【<strong>")
								.append(ActionUtil.getActionTitle(context, ActionID))
								.append("</strong>】该单据<br>")
								.append("联系电话：<a href='tel:").append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("'>")
								.append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("</a><br>")
								.append("审批意见：").append(value).append("<br>")
								.append(dateformat.format(new Date(approveDate))).append("<br>");
							}
						}
						Node node=WorkflowRunUtil.loadWorkItem(context,fbiaw3.getWorkItemID().toString()).getActiveNode();
						c.append("环节名称：").append(node.getTitle());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				String ss="";
				for(BaseWorkItem b : lbwi){
					for(ParticipantObject po: b.getParticipant()){
						if(!userids.containsKey(po.getUserguid())){
							StaffEntity	staff=getStaff(po.getUserguid(),context);
							sbf.append("等待&nbsp;<strong>").append(staff.getName()).append("</strong>&nbsp;")
								.append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?("<a href='tel:"+staff.getTel()+"'>"+staff.getTel()+"</a>"):"&nbsp;")
								.append("&nbsp;审批...<br>");
						}
					}
					if(b.getActiveNode().getMultiSubscriptAction()>=1){
						ss="&nbsp;&nbsp;【<strong>会签</strong>】";
					}
				}
				sbf.append(c).append(ss);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return sbf.toString();
	}
	
	
	private String createWorkflowContext(FBusinessInstanceAndWorkItem fbiaw,Context context,SimpleDateFormat dateformat){
		StringBuffer c=new StringBuffer();
		String xml=fbiaw.getAllsuggest();
		try {
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
				String value=sug.getAttribute("value");
				long approveDate=Long.parseLong(sug.getAttribute("approveDate"));
				int ActionID=Integer.parseInt(sug.getAttribute("ActionID"));
				//String isAuto=sug.getAttribute("isAuto");//是否自动
				StaffEntity staff=getStaff(userid,context);
				if(ActionID==3){
					c.append("<strong>").append(staff.getName())
					.append("</strong>&nbsp;&nbsp;【<strong>")
					.append(ActionUtil.getActionTitle(context, ActionID))
					.append("</strong>】了申请<br>")
					.append("联系电话：<a href='tel:").append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("'>")
					.append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("</a><br>")
					.append("审批意见：").append(value).append("<br>")
					.append(dateformat.format(new Date(approveDate))).append("<br>");
				}else {
					c.append("<strong>").append(staff.getName())
					.append("</strong>&nbsp;&nbsp;已【<strong>")
					.append(ActionUtil.getActionTitle(context, ActionID))
					.append("</strong>】该单据<br>")
					.append("联系电话：<a href='tel:").append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("'>")
					.append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("</a><br>")
					.append("审批意见：").append(value).append("<br>")
					.append(dateformat.format(new Date(approveDate))).append("<br>");
				}
			}
			Node node=WorkflowRunUtil.loadWorkItem(context,fbiaw.getWorkItemID().toString()).getActiveNode();
			c.append("环节名称：").append(node.getTitle());
			if(node.getMultiSubscriptAction()>=1){
				c.append("&nbsp;&nbsp;【<strong>会签</strong>】");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.toString();
	}

	
	private String createWorkflowContext2(FBusinessInstanceAndWorkItem fbiaw,Context context,SimpleDateFormat dateformat){
		StringBuffer c=new StringBuffer();
		StaffEntity staff=null;
		if(StringUtil.isNotEmpty(fbiaw.getApprovalUserID().toString())){
			staff=getStaff(fbiaw.getApprovalUserID().toString(),context);
		}
		
		if(fbiaw.getActionID()==3){
			c.append("<strong>").append(staff.getName())
			.append("</strong>&nbsp;&nbsp;【<strong>")
			.append(ActionUtil.getActionTitle(context, fbiaw.getActionID()))
			.append("</strong>】了申请<br>")
			.append("联系电话：<a href='tel:").append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("'>")
			.append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("</a><br>")
			.append(fbiaw.getApproveUnitTitle()).append("<br>")
			.append(dateformat.format(new Date(fbiaw.getApproveDate())));
		}else {
			c.append("<strong>").append(staff.getName())
			.append("</strong>&nbsp;&nbsp;已【<strong>")
			.append(ActionUtil.getActionTitle(context, fbiaw.getActionID()))
			.append("</strong>】该单据<br>")
			.append("联系电话：<a href='tel:").append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("'>")
			.append((staff!=null&&StringUtil.isNotEmpty(staff.getTel()))?staff.getTel():"").append("</a><br>")
			.append("审批意见：").append(fbiaw.getSuggest()).append("<br>")
			.append("环节名称：")
			.append(WorkflowRunUtil.loadWorkItem(context,fbiaw.getWorkItemID().toString()).getActiveNode().getTitle()).append("<br>")
			.append(dateformat.format(new Date(fbiaw.getApproveDate())));
		}
		return c.toString();
	}
	
	
	
	private Map<String ,StaffEntity> staffs=new HashMap<String,StaffEntity>();
	public StaffEntity getStaff(String guid,Context context){
		if(staffs.containsKey(guid)){
			return staffs.get(guid);
		}
		StaffEntity staff=new StaffEntity();
		String staffSql = " define query stafftelQuery() begin \n select s.recid,s.stdname,s.tel,s.email  from md_staff as s where s.linkuser=guid'"+guid+"' \n end ";
		RecordSet staffSet = context.openQuery((QueryStatementDeclare) context.parseStatement(staffSql));
		while (staffSet.next()) {
			staff.setRecid(staffSet.getFields().get(0).getString());
			staff.setName(staffSet.getFields().get(1).getString());
			staff.setTel(staffSet.getFields().get(2).getString());
			staff.setEmail(staffSet.getFields().get(3).getString());
		}
		   
		User user=context.find(User.class,GUID.valueOf(guid));
		if(user!=null){
			staff.setLinkuser(guid);
			staffs.put(guid, staff);
			staff.setNamecode(user.getName());
		}
		return staff;
	}
	
	
}
