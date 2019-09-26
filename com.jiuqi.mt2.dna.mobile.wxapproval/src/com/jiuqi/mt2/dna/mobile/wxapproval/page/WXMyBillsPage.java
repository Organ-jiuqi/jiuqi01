package com.jiuqi.mt2.dna.mobile.wxapproval.page;

import java.util.List;

import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.WorkflowRunUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.consts.ApplyState;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.consts.ApprovalState;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FApplyRecord;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.impl.RecordNavigatorImpl;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.todo.facade.FApprovalDefine;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.BillCommon;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.ITodoItemCommon;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.MT2Common;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXPlaintextScramble;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXLink;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXMeta;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXTable;
import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;
import com.jiuqi.mt2.dna.mobile.wxapproval.service.WXBusinessProcessManager;
import com.jiuqi.mt2.spi.ICallMonitor;
import com.jiuqi.mt2.spi.todo.model.ITodoCategory;
import com.jiuqi.mt2.spi.todo.model.impl.TodoItem;

/**
 * 单据列表分组
 * 
 * @author liuzihao
 */
public class WXMyBillsPage {
	private WXPage page;
	private String title;
	private Context context;
	private String code;
	private List<ApplyState> las;

	public WXMyBillsPage(Context context, List<ApplyState> las, String code,
			String title) {
		this.page = new WXPage();
		this.title = title;
		this.context = context;
		this.code = code;
		this.las = las;
	}

	public String getHtml() {
		this.page.addMeta(WXMeta.Coded_UTF_8);
		this.page.addLink(WXLink.createLinkJavaScript("/xspi/mt2/js/init.js"));
		this.page.addLink(WXLink
				.createLinkCSS("/xspi/mt2/css/jquery.mobile.flatui.css"));
		this.page.addLink(WXLink
				.createLinkJavaScript("/xspi/mt2/js/jquery-1.10.2.min.js"));
		this.page
				.addLink(WXLink
						.createLinkJavaScript("/xspi/mt2/js/jquery.mobile-1.4.5.min.js"));
		this.page
				.addLink(WXLink
						.createLinkJavaScript("http://res.wx.qq.com/open/js/jweixin-1.0.0.js"));
		// js
		StringBuffer js = new StringBuffer();
		js.append("var billids = new Array();");
		js.append("function load(){");
		js.append("    choiceState('").append(getStateid(las.get(0)))
				.append("');");
		js.append("    var widths=document.body.clientWidth;");
		js.append("    $('.bill_select_div').css('left',widths-45+'px');");
		js.append("    $('.bill_approval_div').css('left',widths-73+'px');");
		js.append("    $('#bill_footer').hide();");
		js.append("    calculateWindowWidth();");
		js.append("}");
		js.append("function calculateWindowWidth(){");
		js.append("    var width=document.body.offsetWidth;");
		js.append("    $('.billtable_td_left').css({'width':width*0.3+'px'});");
		js.append("    $('.billtable_td_right').css({'width':width*0.7-20+'px'});");
		js.append("}");
		js.append("function choiceState(s){");
		js.append("    $('#state_a,#state_b,#state_c').hide();");
		js.append("    $('#state_'+s).show();");
		js.append("}");
		js.append("function selectTheBill(id){");
		js.append("    if(forArray(id)){");
		js.append("        $('#'+id).css('background-position','0px 0px');");
		js.append("    }else{");
		js.append("        $('#'+id).css('background-position','0px 35px');");
		js.append("        billids.push(id);");
		js.append("    } ");
		js.append("    if(billids.length>0){");
		js.append("        $('#bill_footer').show();");
		js.append("    }else{");
		js.append("        $('#bill_footer').hide();");
		js.append("    }");
		js.append("}");
		js.append("function forArray(id){ ");
		js.append("    for(var i=0;i<billids.length;i++){");
		js.append("        if(billids[i]===id){");
		js.append("            billids.splice(i,1);");
		js.append("            return true;");
		js.append("        }");
		js.append("    }return false;");
		js.append("}");
		js.append("function wxalert(){");
		js.append("    alert('批量提交功能暂未开放！');");
		js.append("}");
		;
		this.page.addJavaScript(js.toString());
		// css
		StringBuffer css = new StringBuffer("");
		css.append(".ui-icon-send-msg:after {");
		css.append("    background:url('/xspi/mt2/img/list_arrow_r_n@2x.png');");
		css.append("    background-size: 100% 100%;");
		css.append("}");
		css.append("#state_a,#state_b,#state_c{margin-top:10px;}");
		css.append(".ui-content{padding:0px;}");
		css.append("html .ui-body-a, html .ui-page-theme-a .ui-body-inherit, html .ui-bar-a .ui-body-inherit, html .ui-body-a .ui-body-inherit, html body .ui-group-theme-a .ui-body-inherit, html .ui-panel-page-container-f{background-color:rgb(240,240,240);border:0px;font-size:14px;}");
		css.append(".ui-body-inherit,.ui-collapsible-content{padding:1px;}");
		css.append(".content{magrin:0;padding:0px;}");
		css.append(".tablediv{background-color:rgb(245,245,245);border-radius:0px;width:100%;}");
		css.append(".table_a_label{margin:10px 0px;padding:0px;boder-radius:0px;color:rgb(0,0,0);text-decoration:none;}");
		css.append(".billtable_td_left{");
		css.append("    line-height:16px;");
		css.append("    padding:8px 0px;");
		css.append("    text-align:right;");
		css.append("    vertical-align:text-top;");
		css.append("    word-break:break-all;");
		css.append("    word-wrap:break-word;");
		css.append("    font-weight:normal;");
		css.append("}");
		css.append(".billtable_td_colon{");
		css.append("    vertical-align:top;");
		css.append("    width:12px;");
		css.append("    padding-top:6px;");
		css.append("    text-align:left;");
		css.append("    font-weight:normal;");
		css.append("    color:rgb(0,0,0);");
		css.append("}");
		css.append(".billtable_td_right{");
		css.append("    line-height:16px;");
		css.append("    padding:8px 0px;");
		css.append("    text-align:left;");
		css.append("    vertical-align:text-top;");
		css.append("    word-break:break-all;");
		css.append("    word-wrap:break-word;");
		css.append("    font-weight:normal;");
		css.append("}");
		css.append(".billtable_center{");
		css.append("    line-height:16px;");
		css.append("    padding:8px 0px;");
		css.append("    width:100%;");
		css.append("    text-align:center;");
		css.append("    word-break:break-all;");
		css.append("    word-wrap:break-word;");
		css.append("    font-weight:normal;");
		css.append("}");
		css.append(".billtable_div{");
		css.append("    position:relative;");
		css.append("    border-top:1px solid rgb(230,230,230);");
		css.append("    border-bottom:1px solid rgb(230,230,230);");
		css.append("    margin:0px 0px 10px 0px;");
		css.append("    background:rgb(255,255,255);");
		css.append("    min-height:100px;");
		css.append("}");
		this.page.addCSS(css.toString());
		this.page.addBodyPorperty(" onload='load()' ");
		// 首页
		StringBuffer home_page = new StringBuffer();
		home_page
				.append("<div data-role='page' data-theme='a' id='home_page' data-title='")
				.append(this.title).append("'>");
		home_page
				.append("    <div data-role='header' data-theme='a' data-position='fixed' data-tap-toggle='false'>");
		home_page.append("        <div data-role='navbar'>")
				.append(getNavByState(las)).append("</div>");
		home_page.append("    </div>");
		home_page.append("    <div data-role='content' id='content'>")
				.append(getBillListGroup()).append("</div>");
		// home_page.append("<div data-role='footer' id='bill_footer' data-position='fixed' data-tap-toggle='false' data-theme='w' style='background-color:rgb(255,255,255);border:0px;border-top:1px solid rgb(230,230,230);'>")
		// .append("<div data-role='navbar' data-theme='a'><ul>")
		// .append("<li></li>")
		// .append("<li></li>")
		// .append("<li><a data-theme='b' onclick='wxalert();'>提交</a></li>")
		// .append("<li></li>")
		// .append("<li></li>")
		// .append("</ul></div></div>");
		home_page.append("</div>");
		this.page.addBodyContext(home_page.toString());
		String thispage = this.page.getPage();
		System.out.println("------------------我提交的订单页面----------------------");
		System.out.println();
		System.out.println();
		System.out.println(thispage);
		System.out.println();
		System.out.println();
		System.out.println("------------------我提交的订单页面----------------------");

		return thispage;
	}

	public String getBillListGroup() {
		StringBuffer grouplist = new StringBuffer();
		ITodoItemCommon wxals = new ITodoItemCommon();
		RecordNavigatorImpl navigator = new RecordNavigatorImpl();
		FUser fuser = context.find(FUser.class, code.toUpperCase());
		// 状态分组
		for (ApplyState as : las) {
			List<FApprovalDefine> list = context.getList(FApprovalDefine.class);
			grouplist.append("<div id='state_").append(getStateid(as))
					.append("'>");
			int flag = 0;
			// 单据类型分组WORKFLOWSTATE
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					// List<FRecord>
					// lfr=WXBusinessProcessManager.getFRecordsByBilldefineidAndState(context,
					// list.get(i).getTaskDefineID(), fuser.getGuid(),
					// getApprovalState(as));

					List<FApplyRecord> lfr = WXBusinessProcessManager
							.getFApplyRecord(context, list.get(i)
									.getTaskDefineID(), fuser.getGuid(), as);
					if (lfr != null && lfr.size() > 0) {
						StringBuffer sb2 = new StringBuffer("");
						int sbn = 0;
						for (FApplyRecord f : lfr) {
							IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(
									context, f.getWorkItemID().toString());
							BillModel billModel = BillCommon.getBillModel(
									context, iworkItem);
							billModel.load(GUID.valueOf(iworkItem
									.getProcessInstance().getGUIDRef()));
							int stitc = billModel.getModelData().getMaster()
									.getValueAsInt("WORKFLOWSTATE");
							if (as == ApplyState.BACK && stitc != 5) {
								continue;
							}

							ITodoCategory entity = new MT2Common()
									.getTodoCategory(f.getWorkItemID()
											.toString(), this.context);
							if (entity == null) {
								continue;
							}
							sbn++;
							TodoItem item = wxals
									.createTodoItem(f, context, entity.getID(),
											iworkItem.getWorkCategory());
							WXPlaintextScramble wxps = new WXPlaintextScramble(
									code, item.getId());
							String url = Constants.UrlApprovedBill + "?key="
									+ wxps.getResult();

							sb2.append("<div class='billtable_div'>");
							if (as == ApplyState.BACK) {
								sb2.append("<div class='bill_select_div' id='")
										.append(wxps.getResult())
										.append("' onclick=\"selectTheBill('")
										.append(wxps.getResult())
										.append("');\">").append("</div>");
							}
							sb2.append(new WXTable().createTabled(
									entity.getListTemplate(), item, url));
							sb2.append("</div>");
						}
						if (sbn > 0) {
							grouplist
									.append("<div data-role='collapsible' data-collapsed='true' data-theme='a' class='tablediv'>")
									.append("<h4>")
									.append(list.get(i).getTitle())
									.append("<span class='ui-li-count'>")
									.append(sbn).append("&nbsp;条</span>")
									.append("</h4>");
							flag++;
							grouplist.append(sb2);
							grouplist.append("</div>");
						}
					}
				}
			}
			if (flag == 0) {
				grouplist
						.append("<div style='margin:50px 10px;width:100%;color:red;font-size:20px;text-align:center;'>")
						.append("暂无&nbsp;").append(getStateTitle(as))
						.append("&nbsp;事项</div>");
			}
			grouplist.append("</div>");
		}
		return grouplist.toString();
	}

	// 获取状态id
	private String getStateid(ApplyState as) {
		String flag = "a";
		if (as == ApplyState.APPROVAL) {
			flag = "a";
		} else if (as == ApplyState.BACK) {
			flag = "b";
		} else if (as == ApplyState.PASS) {
			flag = "c";
		}
		return flag;
	}

	// 获取状态id
	private String getStateTitle(ApplyState as) {
		String flag = "";
		if (as == ApplyState.APPROVAL) {
			flag = "流程中";
		} else if (as == ApplyState.BACK) {
			flag = "已驳回";
		} else if (as == ApplyState.PASS) {
			flag = "已完成";
		}
		return flag;
	}

	private static ApprovalState getApprovalState(ApplyState as) {
		ApprovalState falg = null;
		if (as == ApplyState.APPROVAL) {
			falg = ApprovalState.WAIT;
		} else if (as == ApplyState.BACK) {
			falg = ApprovalState.BACK;
		} else if (as == ApplyState.PASS) {
			falg = ApprovalState.PASS;
		} else if (as == ApplyState.ALL) {
			falg = ApprovalState.ALL;
		}
		return falg;
	}

	private String getNavByState(List<ApplyState> state) {
		StringBuffer state_nav = new StringBuffer("<ul>");
		for (int i = 0; i < state.size(); i++) {
			if (state.get(i) == ApplyState.APPROVAL) {
				state_nav.append("<li><a class='");
				if (i == 0) {
					state_nav.append("ui-btn-active ui-state-persist");
				}
				state_nav
						.append("' onclick=\"choiceState('a');\">流程中</a></li>");
			} else if (state.get(i) == ApplyState.BACK) {
				state_nav.append("<li><a class='");
				if (i == 0) {
					state_nav.append("ui-btn-active ui-state-persist");
				}
				state_nav
						.append("' onclick=\"choiceState('b');\">已驳回</a></li>");
			} else if (state.get(i) == ApplyState.PASS) {
				state_nav.append("<li><a class='");
				if (i == 0) {
					state_nav.append("ui-btn-active ui-state-persist");
				}
				state_nav
						.append("' onclick=\"choiceState('c');\">已完成</a></li>");
			}
		}
		state_nav.append("</ul>");
		return state_nav.toString();
	}
}
