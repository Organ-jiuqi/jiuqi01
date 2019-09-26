package com.jiuqi.mt2.dna.mobile.wxapproval.page;

import java.util.List;

import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.bap.workflowmanager.execute.common.util.WorkflowRunUtil;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.consts.ApprovalState;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.facade.FRecord;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.todo.facade.FApprovalDefine;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.ITodoItemCommon;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.MT2Common;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXPlaintextScramble;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXLink;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXMeta;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXTable;
import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;
import com.jiuqi.mt2.dna.mobile.wxapproval.service.WXBusinessProcessManager;
import com.jiuqi.mt2.spi.todo.model.ITodoCategory;
import com.jiuqi.mt2.spi.todo.model.impl.TodoItem;

/**
 * 单据列表分组
 * 
 * @author liuzihao
 */
public class WXPendingBillsPage {
	private WXPage page = new WXPage();
	private String title;
	private Context context;
	private String code;
	private List<ApprovalState> las;

	/**
	 * @param context
	 * @param code
	 * @param title
	 *            网站标题
	 */
	public WXPendingBillsPage(Context context, List<ApprovalState> las,
			String code, String title) {
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
		StringBuffer js = new StringBuffer("var billids = new Array();")
				.append("function load(){choiceState('")
				.append(getStateid(las.get(0)))
				.append("');var widths=document.body.clientWidth;$('.bill_select_div').css('left',widths-45+'px');$('.bill_approval_div').css('left',widths-73+'px');$('#bill_footer').hide();calculateWindowWidth();}")
				.append("function calculateWindowWidth(){var width=document.body.offsetWidth;$('.billtable_td_left').css({'width' : width*0.3+'px'});$('.billtable_td_right').css({'width':width*0.7-20+'px'});}")
				.append("function choiceState(s){$('#state_a,#state_b').hide();$('#state_'+s).show();}")
				.append("function selectTheBill(id){ if(forArray(id)){$('#'+id).css('background-position','0px 0px');}else{$('#'+id).css('background-position','0px 35px');billids.push(id);} if(billids.length>0){$('#bill_footer').show();}else{$('#bill_footer').hide();}}")
				.append("function forArray(id){ for(var i=0;i<billids.length;i++){if(billids[i]===id){billids.splice(i,1);return true;}}return false;}")
				.append("function wxalert(){alert('批量审批功能暂未开放！');}");

		this.page.addJavaScript(js.toString());
		// css
		StringBuffer css = new StringBuffer("")
				.append(".ui-icon-send-msg:after {background:url('/xspi/mt2/img/list_arrow_r_n@2x.png');background-size: 100% 100%;}")
				.append("#state_a,#state_b{margin-top:10px;}")
				.append(".ui-content{padding:0px;}")
				.append("html .ui-body-f, html .ui-page-theme-f .ui-body-inherit, html .ui-bar-f .ui-body-inherit, html .ui-body-f .ui-body-inherit, html body .ui-group-theme-f .ui-body-inherit, html .ui-panel-page-container-f{background-color:rgb(240,240,240);border:0px;font-size:14px;}")
				.append(".ui-body-inherit,.ui-collapsible-content{padding:1px;}")
				.append(".content{magrin:0;padding:0px;}")
				.append(".tablediv{margin-top:10px;background-color:rgb(245,245,245);border-radius:0px;width:100%;}")
				.append(".table_a_label{margin:10px 0px;padding:0px;boder-radius:0px;color:rgb(0,0,0);text-decoration:none;}")

				.append(".billtable_td_left{line-height:16px;padding:8px 0px;text-align:right;vertical-align:text-top;word-break:break-all;word-wrap:break-word;font-weight:normal;}")
				.append(".billtable_td_colon{vertical-align:top;width:12px;padding-top:6px;text-align:left;color:rgb(0,0,0);font-weight:normal;}")
				.append(".billtable_td_right{line-height:16px;padding:8px 0px;text-align:left;vertical-align:text-top;word-break:break-all;word-wrap:break-word;font-weight:normal;}")
				.append(".billtable_center{line-height:16px;padding:8px 0px;width:100%;text-align:center;word-break:break-all;word-wrap:break-word;font-weight:normal;}")

				.append(".billtable_div{position:relative;border-top:1px solid rgb(230,230,230);border-bottom:1px solid rgb(230,230,230);margin:0px 0px 10px 0px;background:rgb(255,255,255);min-height:100px;}")
				.append(".bill_select_div{position:absolute;width:35px;height:35px;background:url('/xspi/mt2/img/select_tick.png');background-position:0px 0px;border:0px;top:8px;}");
		this.page.addCSS(css.toString());

		this.page.addBodyPorperty(" onload='load()' ");

		// 首页
		StringBuffer home_page = new StringBuffer()
				.append("<div data-role='page' data-theme='f' id='home_page' data-title='")
				.append(this.title)
				.append("'>")
				.append("<div data-role='header' data-theme='f' data-position='fixed' data-tap-toggle='false'> ")
				.append("<div data-role='navbar'>").append(getNavByState(las))
				.append("</div></div>");
		home_page.append("<div data-role=\"content\" id=\"content\">")
				.append(getBillListGroup()).append("</div>");
		home_page
				.append("<div data-role='footer' id='bill_footer' data-position='fixed' data-theme='w' data-tap-toggle='false' style='background-color:rgb(255,255,255);border:0px;border-top:1px solid rgb(230,230,230);'>")
				.append("<div data-role='navbar' data-theme='a'><ul>")
				.append("<li></li>")
				.append("<li><a data-theme='f' onclick='wxalert();'>同意</a></li>")
				.append("<li></li>")
				.append("<li><a data-theme='f' onclick='wxalert();'>驳回</a></li>")
				.append("<li></li>").append("</ul></div></div>");
		home_page.append("</div>");
		this.page.addBodyContext(home_page.toString());

		String thispage = this.page.getPage();

		return thispage;
	}

	public String getBillListGroup() {
		StringBuffer grouplist = new StringBuffer();
		ITodoItemCommon wxals = new ITodoItemCommon();
		FUser fuser2 = context.find(FUser.class, code.toUpperCase());

		// 状态分组
		for (ApprovalState as : las) {
			List<FApprovalDefine> list = context.getList(FApprovalDefine.class);
			grouplist.append("<div id='state_").append(getStateid(as))
					.append("'>");
			int flag = 0;

			// 单据类型分组
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {

					List<FRecord> lfr = WXBusinessProcessManager
							.getFRecordsByBilldefineidAndState(context, list
									.get(i).getTaskDefineID(),
									fuser2.getGuid(), as);

					if (lfr != null && lfr.size() > 0) {
						grouplist
								.append("<div data-role='collapsible' data-collapsed='true' data-theme='f' class='tablediv'>")
								.append("<h4>").append(list.get(i).getTitle())
								.append("<span class='ui-li-count'>")
								.append(lfr.size()).append("&nbsp;条</span>")
								.append("</h4>");
						flag++;
						for (FRecord f : lfr) {
							IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(
									context, f.getWorkItemID().toString());
							ITodoCategory entity = new MT2Common()
									.getTodoCategory(f.getWorkItemID()
											.toString(), this.context);
							if (entity == null) {
								continue;
							}
							TodoItem item = wxals
									.createTodoItem(f, context, entity.getID(),
											iworkItem.getWorkCategory());
							WXPlaintextScramble wxps = new WXPlaintextScramble(
									code, item.getId());
							String url = null;
							if (as == ApprovalState.WAIT) {
								url = Constants.UrlApprovalBill + "?key="
										+ wxps.getResult();
							} else {
								url = Constants.UrlApprovedBill + "?key="
										+ wxps.getResult();
							}

							grouplist.append("<div class='billtable_div'>");
							if (as == ApprovalState.WAIT) {
								// 选择框
								grouplist
										.append("<div class='bill_select_div' id='")
										.append(wxps.getResult())
										.append("' onclick=\"selectTheBill('")
										.append(wxps.getResult())
										.append("');\">").append("</div>");
							}
							grouplist.append(new WXTable().createTabled(
									entity.getListTemplate(), item, url));
							grouplist.append("</div>");
						}
						grouplist.append("</div>");
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
	private String getStateid(ApprovalState as) {
		String flag = "";
		if (as == ApprovalState.WAIT) {
			flag = "a";
		} else if (as == ApprovalState.WAITHIGHER) {
			flag = "b";
		}
		// else if(as==ApprovalState.PASS){
		// flag="c";
		// }else if(as==ApprovalState.BACK){
		// flag="d";
		// }
		return flag;
	}

	// 获取状态id
	private String getStateTitle(ApprovalState as) {
		String flag = "";
		if (as == ApprovalState.WAIT) {
			flag = "待审批";
		} else if (as == ApprovalState.WAITHIGHER) {
			flag = "等待下一步";
		}
		// else if(as==ApprovalState.PASS){
		// flag="已完成";
		// }else if(as==ApprovalState.BACK){
		// flag="已驳回";
		// }
		return flag;
	}

	private String getNavByState(List<ApprovalState> state) {
		StringBuffer state_nav = new StringBuffer("<ul>");
		for (int i = 0; i < state.size(); i++) {
			if (state.get(i) == ApprovalState.WAIT) {
				state_nav.append("<li><a class='");
				if (i == 0) {
					state_nav.append("ui-btn-active ui-state-persist");
				}
				state_nav
						.append("' onclick=\"choiceState('a');\" >待审批</a></li>");
			} else if (state.get(i) == ApprovalState.WAITHIGHER) {
				state_nav.append("<li><a class='");
				if (i == 0) {
					state_nav.append("ui-btn-active ui-state-persist");
				}
				state_nav
						.append("' onclick=\"choiceState('b');\">等待下一步</a></li>");
			}
			// else if(state.get(i)==ApprovalState.PASS){
			// state_nav.append("<li><a class='");
			// if(i==0){
			// state_nav.append("ui-btn-active ui-state-persist");
			// }
			// state_nav.append("' onclick=\"choiceState('c');\">已完成</a></li>");
			// }else if(state.get(i)==ApprovalState.BACK){
			// state_nav.append("<li><a class='");
			// if(i==0){
			// state_nav.append("ui-btn-active ui-state-persist");
			// }
			// state_nav.append("' onclick=\"choiceState('d');\">已驳回</a></li>");
			// }
		}
		state_nav.append("</ul>");
		return state_nav.toString();
	}
}