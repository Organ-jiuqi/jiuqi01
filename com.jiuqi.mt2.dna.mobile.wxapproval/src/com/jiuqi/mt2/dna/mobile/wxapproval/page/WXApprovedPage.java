package com.jiuqi.mt2.dna.mobile.wxapproval.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXLink;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXMeta;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXTable;
import com.jiuqi.mt2.spi.bill.metadata.MobileBillDefine;

public class WXApprovedPage {
	private WXPage page;
	private List<WXTable> table;
	private String workflowhistory;
	private MobileBillDefine billDefine;
	private String wxbillTitle;
	private Map<String, String> enclosureMap;
	private String key;

	public void setKey(String key) {
		this.key = key;
	}

	private String flag;

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setEnclosureMap(Map<String, String> enclosureMap) {
		this.enclosureMap = enclosureMap;
	}

	public WXApprovedPage() {
		this.page = new WXPage();
		this.table = new ArrayList<WXTable>();
	}

	public void addTable(WXTable table) {
		this.table.add(table);
	}

	public void setTable(List<WXTable> table) {
		this.table = table;
	}

	public void setWorkflowhistory(String workflowhistory) {
		this.workflowhistory = workflowhistory;
	}

	public void setBillDefine(MobileBillDefine billDefine) {
		this.billDefine = billDefine;
	}

	public void setWxbillTitle(String wxbillTitle) {
		this.wxbillTitle = wxbillTitle;
	}

	public String getPage() {
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
		// CSS
		StringBuffer css = new StringBuffer();
		css.append(".ui-content{padding:0px;}");
		css.append("textarea.ui-input-text{min-height:100px;}");
		css.append(".ui-body-inherit,.ui-collapsible-content{padding:1px;}");
		css.append("html .ui-body-a, html .ui-page-theme-a .ui-body-inherit,");
		css.append("html .ui-bar-a .ui-body-inherit,");
		css.append("html .ui-body-a .ui-body-inherit,");
		css.append("html body .ui-group-theme-a .ui-body-inherit,");
		css.append("html .ui-panel-page-container-a{");
		css.append("    border:0px;");
		css.append("    border-bottom:1px solid rgb(230,230,230);");
		css.append("    font-size:14px;");
		css.append("}");
		css.append("span{");
		css.append("    font-family:aria;");
		css.append("    font-weight:300;");
		css.append("}");
		css.append(".content{background-color:rgb(245,245,245);}");
		css.append(".wf_tab{");
		css.append("    word-break: break-all;");
		css.append("    word-wrap:break-word;");
		css.append("    margin:3px 0px;");
		css.append("}");
		css.append(".workflow_a:active{");
		css.append("    color:rgb(215,64,178);");
		css.append("}");
		css.append(".workflow_top_right{");
		css.append("    padding:5px 10px 5px 0px;");
		css.append("    border-bottom:1px solid rgb(235,235,235);");
		css.append("    border-right:2px solid rgb(215,215,215);");
		css.append("    line-height:20px;");
		css.append("}");
		css.append(".workflow_line{");
		css.append("    position:relative;");
		css.append("    width:15px;");
		css.append("    border-left:1px solid rgb(160,160,160);");
		css.append("    vertical-align:top;");
		css.append("}");
		css.append(".lineColorGray{");
		css.append("    border-left:1px solid rgb(245,245,245);");
		css.append("}");
		css.append(".workflow_left{");
		css.append("    border-left:2px solid rgb(215,215,215);");
		css.append("    width:40px;");
		css.append("}");
		css.append(".workflow_right{");
		css.append("    padding:5px 10px 5px 0px;");
		css.append("    border-top:1px solid rgb(255,255,255);");
		css.append("    border-bottom:1px solid rgb(235,235,235);");
		css.append("    border-right:2px solid rgb(215,215,215);");
		css.append("    line-height:20px;");
		css.append("}");
		css.append(".bgColorWhite{");
		css.append("    background-color:rgb(255,255,255);");
		css.append("}");
		css.append(".bgColorGray{");
		css.append("    background-color:rgb(245,245,245);");
		css.append("}");
		css.append(".workflow_round_rad{");
		css.append("    top:10px;");
		css.append("    left:-6px;");
		css.append("    position:absolute;");
		css.append("    width:9px;");
		css.append("    height:9px;");
		css.append("    border-radius:10px;");
		css.append("    border:1px solid rgb(255,70,80);");
		css.append("    background-color:rgb(255,10,20);");
		css.append("}");
		css.append(".workflow_round_black{");
		css.append("    top:10px;");
		css.append("    left:-6px;");
		css.append("    position:absolute;");
		css.append("    width:9px;");
		css.append("    height:9px;");
		css.append("    border-radius:10px;");
		css.append("    border:1px solid rgb(100,100,100);");
		css.append("    background-color:rgb(80,80,80);");
		css.append("}");
		css.append(".workflow_top_block{");
		css.append("    left:-6px;");
		css.append("    position:absolute;");
		css.append("    height:10px;");
		css.append("    width:10px;");
		css.append("    background:rgb(255,255,255);");
		css.append("}");
		css.append(".workflow_bottom_block{");
		css.append("    left:-1px;");
		css.append("    position:absolute;");
		css.append("    height:10px;");
		css.append("    width:1px;");
		css.append("    background:rgb(160,160,160);");
		css.append("}");
		css.append(".tablediv{");
		css.append("    margin-top:10px;");
		css.append("    background-color:rgb(255,255,255);");
		css.append("    border-radius:0px;");
		css.append("}");
		css.append(".billtable_td_left{");
		css.append("    line-height:16px;");
		css.append("    padding:8px 0px;");
		css.append("    text-align:right;");
		css.append("    vertical-align:text-top;");
		css.append("    word-break:break-all;");
		css.append("    word-wrap:break-word;");
		css.append("}");
		css.append(".billtable_td_colon{");
		css.append("    vertical-align:top;");
		css.append("    width:12px;");
		css.append("    padding-top:6px;");
		css.append("    text-align:left;");
		css.append("}");
		css.append(".billtable_td_right{");
		css.append("    line-height:16px;");
		css.append("    padding:8px 0px;");
		css.append("    text-align:left;");
		css.append("    vertical-align:text-top;");
		css.append("    word-break:break-all;");
		css.append("    word-wrap:break-word;");
		css.append("}");
		css.append(".billtable_center{");
		css.append("    line-height:16px;");
		css.append("    padding:8px 0px;");
		css.append("    width:100%;");
		css.append("    word-break:break-all;");
		css.append("    word-wrap:break-word;");
		css.append("}");

		css.append("    #dv1 { width: 95%;  height:400px; display: none; border: 1px solid #bfd1eb; background:white; position:fixed;   top :100%;  left:50%;transform:translate(-50%,-120%);z-index:10000;}");
		css.append("    #dv2 { width: 95%;  height:400px; display: none; border: 1px solid #bfd1eb; background:white; position:fixed;     left:50%;transform:translate(-50%,-50%);z-index:10000;}");

		css.append("    #dv { width: 95%;  height:400px; display: none; border: 1px solid #bfd1eb; background:white; position:fixed;  left:50%;transform:translate(-50%,-50%);z-index:10000;}");
		css.append(" #test{width: 100%; height:100%;  opacity:0.3;  display: none; border: 1px solid #bfd1eb; background:black;  position:fixed;  left: 0;  right: 0; top: 0%; bottom: 0;   z-index:10000;          }");
		this.page.addCSS(css.toString());

		// JS
		StringBuffer js = new StringBuffer();
		js.append("function load(){");
		js.append("    calculateWindowWidth();");
		js.append("}");
		js.append("function calculateWindowWidth(){");
		js.append("    var width=document.body.offsetWidth;");
		js.append("    $('.billtable_td_left').css({'width' : width*0.3+'px'});");
		js.append("    $('.billtable_td_right').css({'width':width*0.7-20+'px'});");
		js.append("}");
		js.append("function closepage(){");
		js.append("    wx.closeWindow();");
		js.append("    window.opener=null;");
		js.append("    window.open('','_self');");
		js.append("    window.close();");
		js.append("}");
		js.append("function dialog(){alert('下载提示：1、审批通过后才能下载 ；2、只有申请人和创建人才能下载；3、在阅览开始时间到阅览结束时间内才能下载。'); return false;}");
		js.append(
				" function back(){ window.location.href='https://www.jiuqi.com.cn/xspi/mt2/openwxpendingbills?key=")
				.append(this.key).append("' ;}");
		this.page.addJavaScript(js.toString());
		this.page.addBodyPorperty(" onload='load()' ");
		// 首页
		StringBuffer homepage = new StringBuffer();
		homepage.append("<div data-role='page' data-theme='a' id='home_page' data-title='单据详情'>");
		homepage.append("    <div data-role='header' data-theme='a' data-position='fixed' data-tap-toggle='false' >");
		homepage.append("        <a onclick='back();' class='ui-btn-left' data-theme='a' ");
		homepage.append("            data-iconpos='left' data-icon='arrow-l' data-iconpos='left'>返回</a>");
		homepage.append("        <h1>").append(wxbillTitle).append("</h1>");
		homepage.append("    </div>");
		homepage.append("    <div data-role='content' id='home_content' class='content'>");
		for (WXTable t : table) {
			if (t.getHtml() == null)
				continue;
			homepage.append(t.getApprovedHtml());
		}

		// --------------- -----展示附件--------------------

		if (flag == null) {

			if (enclosureMap != null && enclosureMap.size() != 0) {
				homepage.append(
						"<div data-role='collapsible' data-collapsed='false'   class='tablediv'>")
						.append("<h4>附件</h4>");
				for (String enclosureName : enclosureMap.keySet()) {
					homepage.append(
							"<a   target=\"_blank\" href=\""
									+ enclosureMap.get(enclosureName) + "\""
									+ ">").append(enclosureName)
							.append("</a> ");
					homepage.append("<br/>");
				}
				homepage.append("</div>");
			}

		} else {
			if (flag.equals("true")) {

				if (enclosureMap != null && enclosureMap.size() != 0) {
					homepage.append(
							"<div data-role='collapsible' data-collapsed='false'   class='tablediv'>")
							.append("<h4>附件</h4>");
					for (String enclosureName : enclosureMap.keySet()) {
						homepage.append(
								"<a   target=\"_blank\" href=\""
										+ enclosureMap.get(enclosureName)
										+ "\"" + ">").append(enclosureName)
								.append("</a> ");
						homepage.append("<br/>");
					}
					homepage.append("</div>");
				}

			} else {

				if (enclosureMap != null && enclosureMap.size() != 0) {
					homepage.append(
							"<div data-role='collapsible' data-collapsed='false'   class='tablediv'>")
							.append("<h4>附件</h4>");
					for (String enclosureName : enclosureMap.keySet()) {
						homepage.append(
								"<a   target=\"_blank\" href=\"#\""
										+ " onclick=\"dialog()\"" + " >")
								.append(enclosureName).append("</a> ");
						homepage.append("<br/>");
					}
					homepage.append("</div>");
				}

			}

		}

		// -----------------------------------------工作流程--------------------------------

		homepage.append("        <div data-role='collapsible' data-collapsed='false' class='tablediv'>");
		homepage.append("            <h4>审批轨迹</h4>").append(workflowhistory);
		homepage.append("        </div>");

		homepage.append("    </div>");
		homepage.append("</div>");
		this.page.addBodyContext(homepage.toString());

		String str = this.page.getPage();

		return str;
	}
}