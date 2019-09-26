package com.jiuqi.mt2.dna.mobile.wxapproval.page;

import java.text.ParseException;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import com.jiuqi.dna.core.Context;

import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.ApprovalPropertieInfo;

import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXLink;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXMeta;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXTable;
import com.jiuqi.mt2.dna.mobile.wxapproval.constants.Constants;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.HintMessage;
import com.jiuqi.mt2.spi.bill.metadata.MobileBillDefine;

public class WXApprovalPage {
	private WXPage page;
	private ApprovalPropertieInfo apab = null;
	private List<WXTable> table;
	private String workflowhistory;
	private MobileBillDefine billDefine;
	private String wxbillTitle;
	private String key;
	private Context context;
	private IWorkItem iworkItem;
	private String flag;
	private Map<String, String> enclosureMap;
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setEnclosureMap(Map<String, String> enclosureMap) {
		this.enclosureMap = enclosureMap;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setIworkItem(IWorkItem iworkItem) {
		this.iworkItem = iworkItem;
	}

	public WXApprovalPage() {
		this.page = new WXPage();
		this.table = new ArrayList<WXTable>();
	}

	public void addTable(WXTable table) {
		this.table.add(table);
	}

	public void setApab(ApprovalPropertieInfo apab) {
		this.apab = apab;
	}

	public void setTable(List<WXTable> table) {
		this.table = table;
	}

	public void setKey(String key) {
		this.key = key;
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

	public String getPage() throws ParseException {
		this.page.addMeta(WXMeta.Coded_UTF_8);
		this.page.addLink(WXLink.createLinkJavaScript("/xspi/mt2/js/init.js"));
		this.page.addLink(WXLink
				.createLinkCSS("/xspi/mt2/css/jquery.mobile.flatui.css"));
		this.page.addLink(WXLink
				.createLinkCSS("/xspi/mt2/css/mobiscroll_date.css"));
		this.page.addLink(WXLink.createLinkCSS("/xspi/mt2/css/mobiscroll.css"));
		this.page.addLink(WXLink
				.createLinkJavaScript("/xspi/mt2/js/jquery.min.js"));
		this.page.addLink(WXLink
				.createLinkJavaScript("/xspi/mt2/js/jquery-1.10.2.min.js"));
		this.page
				.addLink(WXLink
						.createLinkJavaScript("/xspi/mt2/js/jquery.mobile-1.4.5.min.js"));
		this.page
				.addLink(WXLink
						.createLinkJavaScript("http://res.wx.qq.com/open/js/jweixin-1.0.0.js"));
		this.page.addLink(WXLink
				.createLinkJavaScript1("/xspi/mt2/js/mobiscroll_date.js"));
		this.page.addLink(WXLink
				.createLinkJavaScript("/xspi/mt2/js/mobiscroll.js"));
		this.page.addTitle(this.wxbillTitle);

		StringBuffer js = new StringBuffer(
				"function load(){       editField(); clearexception();calculateWindowWidth();}")
				.append("function calculateWindowWidth(){var width=document.body.offsetWidth;$('.billtable_td_left').css({'width' : width*0.3+'px'});$('.billtable_td_right').css({'width':width*0.7-20+'px'});}")
				.append("function changeaction(action){clearexception(); var flag=submitverify(action);if(flag=='true'){$('#action').val(action);var formData=$('#myform').serialize();$.ajax({type:'POST',url:'")
				.append(Constants.UrlWXApproval)
				.append("?key=")
				.append(this.key)
				.append("',cache:false, timeout:0,data:formData,dataType:'json',success:onSuccess,error:onError});$.mobile.loading('show',{text:'审批中...',textVisible:true,theme:'b',textonly:false,html:''});}}")
				.append("function onSuccess(data,status){$('#hint_yes').hide().unbind();$('#hint_no').hide().unbind();$.mobile.loading('hide');var jo=eval(data);var htype=jo.Hint_Typt;var htitle=jo.Hint_Title;var hcontext=jo.Hint_Context;var hexception=jo.Hint_Exception;var hurl=jo.Hint_Url;   if(htype=='")
				.append(HintMessage.HintType_Success)
				.append("'){if(htitle=='' || htitle==null){$('#hint_title').html('审批成功');}else{$('#hint_title').html(htitle);}if(hcontext=='' || hcontext==null){$('#hint_context').html('单据审批成功!');}else{$('#hint_context').html(hcontext);}$('#hint_yes').show().bind('click',function(){window.location.href='https://www.jiuqi.com.cn/xspi/mt2/openwxpendingbills?key="+this.key+"'; });$('#hint_page_back').hide();}else{")
				.append("if(htitle=='' || htitle==null){$('#hint_title').html('提示信息');}else{$('#hint_title').html(htitle);}$('#hint_context').html(hcontext);")
				.append("if(hexception!=null && hexception!=''){$('#open_msg').show();$('#close_msg').hide();$('#hint_msg').html(hexception);}")
				.append("if(htype=='")
				.append(HintMessage.HintType_Error)
				.append("' || htype=='")
				.append(HintMessage.HintType_AlertStop)
				.append("'){$('#hint_yes').show().bind('click',function(){$.mobile.changePage('#home_page','flip');});}else if(htype=='")
				.append(HintMessage.HintType_AlertRun)
				.append("'){$('#hint_yes').show().bind('click',function(){trailingAction(hurl,'")
				.append(HintMessage.UserSelectYes)
				.append("');});}else if(htype=='")
				.append(HintMessage.HintType_ConfirmNone)
				.append("'){$('#hint_yes').show().bind('click',function(){trailingAction(hurl,'")
				.append(HintMessage.UserSelectYes)
				.append("');});$('#hint_no').show().bind('click',function(){$.mobile.changePage('#home_page','flip');});}else if(htype=='")
				.append(HintMessage.HintType_ConfirmAction)
				.append("' || htype=='")
				.append(HintMessage.HintType_ConfirmFormula)
				.append("'){$('#hint_yes').show().bind('click',function(){trailingAction(hurl,'")
				.append(HintMessage.UserSelectYes)
				.append("');});$('#hint_no').show().bind('click',function(){trailingAction(hurl,'")
				.append(HintMessage.UserSelectNo)
				.append("');});}else{$('#hint_context').html('返回结果无法解析<br/>请联系管理员!'); $('#hint_yes').show().bind('click',function(){closepage();});}$('#hint_page_back').show();}$.mobile.changePage('#hint_page','flip');}")
				.append("function onError(data,status){      alert( status );      $.mobile.loading('hide');$('#hint_yes').hide().unbind();$('#hint_no').hide().unbind();$('#hint_title').html('审批失败');$('#hint_context').html('返回结果有误,联系管理员!<br/>详细信息：ajax返回状态为error;或无返回值;或等待超时');\t$('#hint_yes').show().bind('click',function(){$.mobile.changePage('#home_page','flip');});$.mobile.changePage('#hint_page','flip');}")
				.append("function trailingAction(url,action){$.ajax({type:'POST',url:url+action,cache:false,data:'',dataType:'json',success:onSuccess,error:onError});$.mobile.loading('show',{text:'审批中...',textVisible:true,theme:'b',textonly:false,html:''});}")
				.append("function closepage(){WeixinJSBridge.call('closeWindow');}")
				.append("function submitverify(select){var c=$('#comment').val();if(c==''){var fa=$('#flagAgree').val();var fr=$('#flagReject').val();if((select=='")
				.append(Constants.UserDefineAccept)
				.append("'|| select=='")
				.append(Constants.ACCEPT)
				.append("') && fa=='false'){commentHint('选择同意时<br/>');return 'false';}else if((select=='")
				.append(Constants.UserDefineReject)
				.append("' || select=='")
				.append(Constants.REJECT)
				.append("') && fr=='false'){commentHint('选择驳回时<br/>');return 'false';}}return 'true';}")
				.append(" function back(){ window.location.href='https://www.jiuqi.com.cn/xspi/mt2/openwxpendingbills?key=")
				.append(this.key)
				.append("' ;}")
				// 2018-09-13 begin
				.append("    $('#USER_AGE').attr('data-role', 'none');     $('.input').attr('data-role', 'none');$('.button').attr('data-role', 'none');")
				.append("var type; var flag; var parm; var zj; var table; var xmlHttp;")
				.append("function showdiv(s) {$('#test').css('display', 'block'); $('#dv').css('display', 'block');")
				.append("   table = s.getAttribute('BData'); flag = s.getAttribute('titleid'); parm = s.getAttribute('class');")
				.append("   type = s.getAttribute('TextType'); document.getElementById('input').value = ''; $('#input').focus();")
				.append("   if (document.getElementById(table + flag).innerHTML.length == 0) {getMoreContents();} ")
				.append("   else { var tr = document.createElement('tr'); tr.setAttribute('style', 'text-align:left'); ")
				.append("   tr.setAttribute('class', 'change');var td = document.createElement('td'); td.setAttribute('borde', '0');")
				.append("   td.setAttribute('gbcolor', '#FFFAFA'); var text = document.createTextNode(document.getElementById(table + flag).innerHTML);")
				.append("   td.appendChild(text); tr.appendChild(td); document.getElementById('content').appendChild(tr);")
				.append("   td.onmousedown = function() {document.getElementById(table + flag).innerHTML = this.innerHTML; ")
				.append("   $('#test').css('display', 'none'); $('#dv').css('display', 'none');document.getElementById('input').value = ''; clearContent();};}}")
				.append("function showdiv1(p) { $('#test').css('display', 'block'); $('#dv1').css('display', 'block'); parm = p.getAttribute('class'); type = p.getAttribute('TextType'); $('#input1').focus();}")
				.append("function showdiv2(q) { $('#test').css('display', 'block'); $('#dv2').css('display', 'block');$('#input2').focus();")
				.append("   table = q.getAttribute('BData'); flag = q.getAttribute('titleid'); parm = q.getAttribute('class'); type = q.getAttribute('TextType');")
				.append("   document.getElementById('input2').value = '';  if (document.getElementById(table + flag).innerHTML.length == 0) {getMoreContents();} ")
				.append("   else {  var c=document.getElementById(table + flag).innerHTML; setContent21(c);} }")
				.append("function getMoreContents() {  if (type == 'multi') { var content = $('#input2').val(); } else if (type == 'single') { var content = $('#input').val(); }")
				.append("   xmlHttp = createXMLHttp();var url ='https://www.jiuqi.com.cn/xspi/mt2/selectbasedata?keyword=' + content + '&tablename=' + table + '&keyword1=' + parm;      xmlHttp.open('GET', url, true); xmlHttp.onreadystatechange = callback; xmlHttp.send(null);}")
				.append("function callback() {    if (xmlHttp.readyState == 4) {  if (xmlHttp.status == 200) {  var result = xmlHttp.responseText; var json = eval('(' + result + ')');  ")
				.append("   if (type == 'multi') {setContent2(json);} else if (type == 'single') {setContent(json);}}}}")
				.append("function setContent(contents) { clearContent();  var size = contents.length; for (var i = 0; i < size; i++) {")
				.append("   var nextNode = contents[i];var tr = document.createElement('tr');tr.setAttribute('style', 'text-align:left');tr.setAttribute('class', 'change');")
				.append("   var td = document.createElement('td'); td.setAttribute('borde', '0'); td.setAttribute('gbcolor', '#FFFAFA');")
				.append("   td.onmousedown = function() { document.getElementById(table + flag).innerHTML = this.innerHTML;")
				.append("   $('#test').css('display', 'none'); $('#dv').css('display', 'none');clearContent();};var text = document.createTextNode(nextNode);")
				.append("   td.appendChild(text); tr.appendChild(td); document.getElementById('content').appendChild(tr);}}")
				.append("function setContent1(n) {document.getElementById(parm).innerHTML = $(n).text();$('#test').css('display', 'none');$('#dv1').css('display', 'none');}")
				.append("function setContent2(contents) { clearContent();     var size = contents.length; for (var i = 0; i < size; i++) {")
				.append("   var nextNode = contents[i]; var tr = document.createElement('tr'); tr.setAttribute('style', 'text-align:left');tr.setAttribute('class', 'change');")
				.append("   var td = document.createElement('td'); td.setAttribute('borde', '0'); td.setAttribute('gbcolor', '#FFFAFA'); var input = document.createElement('input');")
				.append("   input.setAttribute('type', 'checkbox');input.setAttribute('name', 'box');input.setAttribute('id', 'box' + i);input.setAttribute('onclick', 'check(this)');")
				.append("   var text1 = JSON.stringify(nextNode); input.setAttribute('value', text1); var text = document.createTextNode(nextNode);")
				.append("   td.appendChild(input); td.appendChild(text); tr.appendChild(td); document.getElementById('content1').appendChild(tr); } }")
				.append("function setContent21(contents) { clearContent();  var content= contents.split(',') ;  var size = content.length; for (var i = 0; i < size; i++) {")
				.append("   var text1= content[i]; var tr = document.createElement('tr'); tr.setAttribute('style', 'text-align:left');tr.setAttribute('class', 'change');")
				.append("   var td = document.createElement('td'); td.setAttribute('borde', '0'); td.setAttribute('gbcolor', '#FFFAFA'); var input = document.createElement('input');")
				.append("   input.setAttribute('type', 'checkbox');input.setAttribute('name', 'box');input.setAttribute('id', 'box' + i);input.setAttribute('onclick', 'check(this)');")
				.append("   input.setAttribute('value', text1); input.setAttribute('checked', 'checked'); var text = document.createTextNode(text1);")
				.append("   td.appendChild(input); td.appendChild(text); tr.appendChild(td); document.getElementById('content1').appendChild(tr); } }")
				.append("function clearContent() {if (type == 'multi') {var contentTableBody = document.getElementById('content1');")
				.append("   } else if (type == 'single') { var contentTableBody = document.getElementById('content'); }  var size = contentTableBody.childNodes.length;")
				.append("   for (var i = size - 1; i >= 0; i--) { contentTableBody.removeChild(contentTableBody.childNodes[i]);} }")
				.append("function closediv(e) { if (type == 'multi') { $('#test').css('display', 'none');      $('#dv2').css('display', 'none'); clearContent();")
				.append("    } if (type == 'single') { $('#test').css('display', 'none'); $('#dv').css('display', 'none');   clearContent(); }")
				.append("    if (type == 'boolean') { $('#test').css('display', 'none');      $('#dv1').css('display', 'none'); } }")
				.append("function clearinput(f) { if (type == 'multi') { document.getElementById(table + flag).innerHTML = '' ;$('input[name=box]').removeAttr('checked');     clearContent();")
				.append("     } if (type == 'single') { document.getElementById(table + flag).innerHTML = '' ; clearContent(); } }")
				.append("function createXMLHttp() { var xmlHttp; if (window.XMLHttpRequest) { xmlHttp = new XMLHttpRequest(); }")
				.append("    if (window.ActiveXObject) { xmlHttp = new ActiveXObject('Microsoft.XMLHTTP'); if (!xmlHttp) { \txmlHttp = new ActiveXObject('Msxml2.XMLHTTP'); } } return xmlHttp; }")
				.append("function check(e) { document.getElementById(e.id).setAttribute('checked', 'checked');} ")
				.append("function confirm() { var obj = document.getElementsByName('box'); var s = '';")
				.append("    for (var i = 0; i < obj.length; i++) { if (obj[i].checked) s += obj[i].value.replace('\"', '').replace('\"', '') + ',';}")
				.append("    document.getElementById(table + flag).innerHTML = s.substring(0, s.lastIndexOf(','));document.getElementById('input2').value = ''; $('#test').css('display', 'none');  $('#dv2').css('display', 'none');\t}")
				.append("$(function () { var currYear = (new Date()).getFullYear();  var opt={}; opt.date = {preset : 'date'}; opt.datetime = {preset : 'datetime'};")
				.append("    opt.time = {preset : 'time'}; opt.default = { theme: 'android-ics light',  display: 'modal',   mode: 'scroller',  dateFormat: 'yyyy-mm-dd',")
				.append("    lang: 'zh', showNow: true, nowText: '今天', startYear: currYear - 50, endYear: currYear + 100  };$('#USER_AGE').mobiscroll($.extend(opt['date'], opt['default']));});")
				// 2018-09-13 end
				.append("function commentHint(str){$('#hint_page_back').show();$('#hint_yes').hide().unbind();$('#hint_no').hide().unbind();$.mobile.loading('hide');$('#hint_title').html('错误提示');$('#hint_context').html(str+'审批意见为必填项,不能为空!');$('#hint_yes').show().bind('click', function() {$.mobile.changePage('#dialog_page','flip');});$.mobile.changePage('#hint_page','flip');}")
				.append("function openorclose(id){$('#open_msg').hide();$('#close_msg').hide();$('#hite_msg').hide();if(id==1){$('#close_msg').show();$('#hite_msg').show();}else if(id==2){$('#open_msg').show();}}")
				.append("function  dialog(){alert('下载提示：1、审批通过后才能下载 ；2、只有申请人和创建人才能下载；3、在阅览开始时间到阅览结束时间内才能下载。'); return false;}")
				.append("function clearexception(){hexception='';$('#open_msg').hide();$('#close_msg').hide();$('#hint_msg').html(hexception);}");

		js.append(editAndSave());
		this.page.addJavaScript(js.toString());
		StringBuffer css = new StringBuffer()
				.append(".ui-content{padding:0px;}")
				.append("textarea.ui-input-text{min-height:100px;}")
				.append(".ui-body-inherit,.ui-collapsible-content{padding:2px 0px 10px 0px;}")
				.append("html .ui-body-f, html .ui-page-theme-f .ui-body-inherit, html .ui-bar-f .ui-body-inherit, html .ui-body-f .ui-body-inherit, html body .ui-group-theme-f .ui-body-inherit, html .ui-panel-page-container-f{border:0px;border-bottom:1px solid rgb(230,230,230);font-size:14px;}")
				.append("span{font-family:aria;font-weight:300;}")
				.append("#home_page,#dialog_page,#hint_page{background-color:rgb(230,230,230);}")
				.append("#dialog_content{margin-top:15px;}")
				.append("#hint_content{background-color:rgb(255,255,255);border:2px solid rgb(165,220,255);border-radius:5px;margin:10px;text-align:center;}")
				.append(".content{background-color:rgb(245,245,245);magrin:0;padding:0px;}")
				.append(".wf_tab{word-break: break-all;word-wrap:break-word;margin:3px 0px;}")
				.append(".workflow_a:active{color:rgb(215,64,178);}")
				.append(".workflow_top_right{padding:5px 10px 5px 0px;border-bottom:1px solid rgb(235,235,235);border-right:2px solid rgb(215,215,215);line-height:20px;}")
				.append(".workflow_line{position:relative;width:14px;border-left:1px solid rgb(160,160,160);vertical-align:top;}")
				.append(".lineColorGray{border-left:1px solid rgb(245,245,245);}")
				.append(".workflow_left{border-left:2px solid rgb(215,215,215);width:14px;}")
				.append(".workflow_right{padding:5px 10px 5px 0px;border-top:1px solid rgb(255,255,255);border-bottom:1px solid rgb(235,235,235);border-right:2px solid rgb(215,215,215);line-height:20px;}")
				.append(".bgColorWhite{background-color:rgb(255,255,255);}")
				.append(".bgColorGray{background-color:rgb(245,245,245);}")
				.append(".workflow_round_rad{top:10px;left:-6px;position:absolute;width:9px;height:9px;border-radius:10px;border:1px solid rgb(255,70,80);background-color:rgb(255,10,20);}")
				.append(".workflow_round_black{top:10px;left:-6px;position:absolute;width:9px;height:9px;border-radius:10px;border:1px solid rgb(100,100,100);background-color:rgb(80,80,80);}")
				.append(".workflow_top_block{width:10px;height:10px;left:-6px;position:absolute;background:rgb(255,255,255);}")
				.append(".workflow_bottom_block{width:1px;height:10px;left:-1px;position:absolute;background:rgb(160,160,160);}")
				.append(".tablediv{margin-top:5px;background-color:rgb(255,255,255);border-radius:0px;}")
				// 2018-09-13
				.append(" tr.change:hover   { background-color:gray }")
				.append("#dv{ width: 95%;  height:400px; display: none; border: 1px solid #bfd1eb; background:white; position:absolute;left:10px;top:100px;     z-index:10000;}")
				.append("#dv1 { width: 95%;  height:400px; display: none; border: 1px solid #bfd1eb; background:white; position:fixed;  left:10px; top:100px;   z-index:10000;}")
				.append("#dv2 { width: 95%;  height:400px; display: none; border: 1px solid #bfd1eb; background:white; position:fixed;  left:10px; top:100px;    ;z-index:10000;}")
				.append("#test{width: 100%; height:100%;  opacity:0.3;  display: none; border: 1px solid #bfd1eb; background:black;  position:fixed;  left: 0;  right: 0; top: 0%; bottom: 0;   z-index:10000;          }")

				// 2018-09-13
				.append(".billtable_td_left{text-align:right;vertical-align:text-top;word-break:break-all;word-wrap:break-word;line-height:16px;padding-top:5px;font-size:14px;}")
				.append(".billtable_td_right{text-align:left;vertical-align:text-top;word-break:break-all;word-wrap:break-word;line-height:16px;padding-top:5px;font-size:14px;}")
				.append(".billtable_center{width:100%;text-align:center;word-break:break-all;word-wrap:break-word;line-height:16px;padding-top:5px;font-size:14px;}");
		this.page.addCSS(css.toString());
		this.page.addBodyPorperty("onload='load()'");
		// 首页
		StringBuffer homepage = new StringBuffer(
				"<div  data-role='page' data-theme='f' id='home_page'>");
		// 头部DIV

		homepage.append(
				"<div  data-role='header' data-theme='f' data-position='fixed' data-tap-toggle='false'>")
				.append("<a   onclick='back();'  class='ui-btn-left' data-theme='f' data-iconpos='left' data-icon='arrow-l' data-iconpos='left'>返回</a>")
				.append("<h1>")
				.append(this.wxbillTitle)
				.append("&nbsp;单据详情</h1>")
				.append("<a onclick='saveField();' class='ui-btn-right' data-theme='f' data-iconpos='right' data-icon='arrow-r' data-iconpos='right'>审批</a>")
				.append("</div>");
		// 内容DIV
		homepage.append("<div data-role='content'   id='home_content' class='content'>");
		for (WXTable t : table) {
			if (t.getHtml(apab) == null)
				continue;
			homepage.append(t.getHtml(apab));
		}
		// --------------------------------------------展示附件-----------------------------------------------
		if (flag == null) {
			if (enclosureMap != null && enclosureMap.size() != 0) {
				homepage.append(
						"<div data-role='collapsible' data-collapsed='false' data-theme='b' class='tablediv'>")
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
							"<div data-role='collapsible' data-collapsed='false' data-theme='b' class='tablediv'>")
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
							"<div data-role='collapsible' data-collapsed='false' data-theme='b' class='tablediv'>")
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
		homepage.append(
				"<div data-role='collapsible' data-collapsed='false' data-theme='b' class='tablediv'>")
				.append("<h4>审批流程历史</h4>").append(workflowhistory)
				.append("</div>");

		homepage.append("</div>");
		// 首页结束
		homepage.append("</div>");

		this.page.addBodyContext(homepage.toString());

		// 审批意见页面
		StringBuffer dialogpage = new StringBuffer()
				.append("<div data-role='page' data-theme='f' id='dialog_page'>")
				.append("<div data-role='header' data-theme='f' data-position='fixed'>")
				.append("<a href='javascript:history.go(-1);' data-role='button' data-theme='f' data-position-to='window' data-transition='slide' data-direction='reverse' data-icon='arrow-l'>返回</a>")
				.append("<h1>")
				.append(this.wxbillTitle)
				.append("&nbsp;审批意见</h1>")
				.append("</div>")
				.append("<div data-role='content' id='dialog_content' class='content'><form id='myform'><div style='text-align:left;'>")
				.append("<lable for='comment' style='font-size:16px;line-height:20px;'>请填写审批意见:</label>");
		if (apab.isFlagSuggestionAgree()) {
			dialogpage
					.append("<lable style='line-height:20px;color:rgb(154,54,206);font-size:14px;margin-left:20px;'>同意：选填</lable>");
			dialogpage
					.append("<input type='hidden' id='flagAgree' value='true'>");
		} else {
			dialogpage
					.append("<lable style='line-height:20px;color:rgb(154,54,206);font-size:14px;margin-left:20px;'>同意：必填</lable>");
			dialogpage
					.append("<input type='hidden' id='flagAgree' value='false'>");
		}
		if (apab.isFlagSuggestionReject()) {
			dialogpage
					.append("<lable style='line-height:20px;color:rgb(154,54,206);font-size:14px;margin-left:20px;'>驳回：选填</lable>");
			dialogpage
					.append("<input type='hidden' id='flagReject' value='true'>");
		} else {
			dialogpage
					.append("<lable style='line-height:20px;color:rgb(154,54,206);font-size:14px;margin-left:20px;'>驳回：必填</lable>");
			dialogpage
					.append("<input type='hidden' id='flagReject' value='false'>");
		}
		dialogpage
				.append("<textarea name='comment'  id='comment' class='ui-input-text' value='' ></textarea><input type='hidden' id='action' name='action'>")
				.append("<input type='hidden' id='datas' name='datas' value=''></div><div style='text-algin:center;'>");

		if (apab.getButtonNumber() > 0) {
			if (apab.getUserDefineAccept() != null) {
				dialogpage
						.append("<input type='button' id='button_ok' name='ok' data-theme='b' value='同意' onclick=\"changeaction('")
						.append(Constants.UserDefineAccept).append("')\">");
			} else if (apab.getAccept() != null) {
				dialogpage
						.append("<input type='button' id='button_ok' name='ok' data-theme='b' value='同意' onclick=\"changeaction('")
						.append(Constants.ACCEPT).append("')\">");
			}
			if (apab.getUserDefineReject() != null) {
				dialogpage
						.append("<input type='button' id='button_cancle' name='cancle' data-theme='d' value='驳回' onclick=\"changeaction('")
						.append(Constants.UserDefineReject).append("')\">");
			} else if (apab.getReject() != null) {
				dialogpage
						.append("<input type='button' id='button_cancle' name='cancle' data-theme='d' value='驳回' onclick=\"changeaction('")
						.append(Constants.REJECT).append("')\">");
			}
		} else {
			dialogpage
					.append("<div style='background-color：rgb(255,255,255);border:1px solid rgb(0,0,0);text-align:center;'>该节点没有配置审批按钮<br/>如需审批请联系管理人员</div>");
		}
		dialogpage.append("</div></form></div></div>");

		this.page.addBodyContext(dialogpage.toString());

		// 提示信息
		StringBuffer hintpage = new StringBuffer()
				.append("<div data-role='page' data-theme='f' id='hint_page'>")
				.append("<div data-role='header' data-theme='f' data-position='fixed'>")
				.append("<a id='hint_page_back' href='#home_page' data-role='button' data-theme='f' data-position-to='window' data-transition='fade' data-icon='arrow-l'>返回</a>")
				.append("<h1>")
				.append(this.wxbillTitle)
				.append("&nbsp;<lable id='hint_title'>提示信息</lable></h1></div>")
				.append("<div data-role='content' id='hint_content' class='content'>")
				.append("<div id='hint_context' style='margin:20px;word-break:break-all;word-wrap:break-word;'></div>")
				.append("<div onclick='openorclose(1);' id='open_msg' style='margin:12px;text-align:left;cursor:pointer;color:blue;line-height: 20px;'>展开错误信息&gt;&gt;</div>")
				.append("<div onclick='openorclose(2);' id='close_msg' style='margin:12px;text-align:right;cursor:pointer;display:none;color:blue;line-height:20px;'>&lt;&lt;关闭错误信息</div><div id='hite_msg' style='margin:10px;text-align:center;display:none;'>")
				.append("<textarea id='hint_msg' style='background:none;font-size:8px;' rows='6'>")
				.append("</textarea></div>")
				.append("<div style='text-aline:center;'>")
				.append("<a id='hint_yes' data-role='button' data-theme='b' data-inline='true' style='display:none;'>确认 </a>")
				.append("<a id='hint_no' data-role='button' data-theme='d' data-inline='true' style='display:none;'>取消</a></div></div></div>");

		this.page.addBodyContext(hintpage.toString());

		String str = this.page.getPage();
		 
		return str;
	}

	public String editAndSave() {
		StringBuffer js = new StringBuffer();
		js.append("var feildid =new Array();");
		String[] field = apab.getEditField();
		for (String f : field) {
			js.append("feildid.push('").append(f).append("');");
		}

		js.append(
				"function editField(){for(var i=0;i<feildid.length;i++){var fname=feildid[i].replace('.',' .');")
				.append("var ftype=$('#'+fname).attr('textType');if(ftype=='string'||ftype=='int'||ftype=='numric'){")
				.append("$('#'+fname).css({'color':'blue','border-bottom':'1px solid blue'}).attr('contenteditable','true');")
				.append("if(ftype=='date'){$('#'+fname).attr('onfocus','removeFInt(this)').attr('onblur','editData(this)').attr('onblur','removeFInt(this)');")
				.append("}else if(ftype=='int'){$('#'+fname).attr('onfocus','removeFInt(this)').attr('onkeydown','removeFInt(this)').attr('onblur','removeFInt(this)');")
				.append("}else if(ftype=='numric'){$('#'+fname).attr('onfocus','removeFNum(this)').attr('onkeydown','removeFNum(this)').attr('onblur','removeFNum(this)');")
				.append("}}else{  $('#' + fname).css({'color' : 'blue','border-bottom' : '1px solid blue'}).attr('contenteditable', 'true');            }}}");
		js.append(
				"function editData(td){td.innerHTML=td.innerHTML.replace(/[^0-9]/g,'');if(td.innerHTML!=null&&td.innerHTML.length==8){")
				.append("if(td.innerHTML.substr(0,1)==0){alert('请输入有效年份!');return false;}else if(td.innerHTML.substr(4,2)>12||td.innerHTML.substr(4,2)==0){")
				.append("alert('请输入有效月份!');return false;} if(td.innerHTML.substr(6,2)>31||td.innerHTML.substr(6,2)==0){")
				.append("alert('请输入有效日期!');return false;}else{td.innerHTML=(td.innerHTML.substr(0,4)+'-'+td.innerHTML.substr(4,2)+'-'+td.innerHTML.substr(6,2));")
				.append("return true;}}else{alert('日期应为8位有效数字');return false;}}");
		js.append("function removeFInt(td){td.innerHTML=td.innerHTML.replace(/[^0-9]/g,'');}");
		js.append("function removeFNum(td){td.innerHTML=td.innerHTML.replace(/[^\\d+\\.]/g,'');}");

		js.append(
				"function saveField(){ var datas='';var judgeDate=/^(\\d{4})([-])(\\d{2})([-])(\\d{2})$/;var judgeInt=/^([-]?)([0-9]+)$/;var judgeDouble=/^([-]?)([0-9]+)([.]?)([0-9]*)$/;")
				.append("for(var i=0;i<feildid.length;i++){     var fild=$('#'+feildid[i].replace('.',' .'));   for(var j=0;j<fild.length;j++){")
				.append(" var type=fild.eq(j).attr('TextType');   if(type=='date'){ var ldata=fild.eq(j).val();}else{ var ldata=fild.eq(j).text();}               var recid=fild.eq(j).attr('RECID');")
				.append("var bdata=fild.eq(j).attr('BData');if(ldata!=''&&type=='date'&&!judgeDate.test(ldata)){")
				.append("alert(ldata+' 格式有误!日期请使用yyyy-MM-dd格式');\treturn;")
				.append("}else if(ldata!=''&&type =='int'&&!judgeInt.test(ldata.replace(',',''))){")
				.append("ldata=ldata.replace(new RegExp(',' , \"g\"),'');alert(ldata+' 格式有误!请输入数字');return;")
				.append("}else if(ldata!=''&&type=='numric'&&!judgeDouble.test(ldata.replace(new RegExp(',' , \"g\"),''))){")
				.append("alert(ldata+' 格式有误!请输入数字');return;}")
				.append("if(ldata!=null&&ldata!=undefined&&ldata!=''&&ldata!=bdata ){")
				.append("if (type == 'single' || type == 'multi' ) {    datas += feildid[i] + ';.,;' + recid + ';,.;' + ldata + ';..;' + type + ';,;;' + bdata+';,,;' ; } else { datas += feildid[i] + ';.,;' + recid + ';,.;' + ldata + ';..;' + type + ';,,;';} }}}")
				.append("$('#datas').val(datas);  $.mobile.changePage('#dialog_page','slide');  }");

		return js.toString();
	}

}
