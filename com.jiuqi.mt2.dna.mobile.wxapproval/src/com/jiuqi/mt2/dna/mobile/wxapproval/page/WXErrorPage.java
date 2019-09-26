package com.jiuqi.mt2.dna.mobile.wxapproval.page;

import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXLink;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXMeta;
import com.jiuqi.mt2.dna.mobile.wxapproval.component.WXPage;
import com.jiuqi.xlib.utils.StringUtil;
/**
 * 错误提示页面
 * @author liuzihao
 */
public class WXErrorPage {
	private WXPage page;
	private String title;
	private String context;
	private String exception;
	
	public WXErrorPage(String title, String context,String exception) {
		this.page = new WXPage();
		this.title = title;
		this.context = context;
		this.exception = exception;
	}
	public void setPage(WXPage page) {
		this.page = page;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public void setException(String exception) {
		this.exception = exception;
	}

	public String getPage(){
		this.page.addMeta(WXMeta.Coded_UTF_8);
		this.page.addLink(WXLink.createLinkJavaScript("/xspi/mt2/js/init.js"));
		this.page.addLink(WXLink.createLinkCSS("/xspi/mt2/css/jquery.mobile.flatui.css"));
		this.page.addLink(WXLink.createLinkJavaScript("/xspi/mt2/js/jquery-1.10.2.min.js"));
		this.page.addLink(WXLink.createLinkJavaScript("/xspi/mt2/js/jquery.mobile-1.4.5.min.js"));
		this.page.addLink(WXLink.createLinkJavaScript("http://res.wx.qq.com/open/js/jweixin-1.0.0.js"));
		//CSS
		StringBuffer css=new StringBuffer();
		css.append("textarea.ui-input-text{min-height:100px;}");
		this.page.addCSS(css.toString());
		
		//JS
		StringBuffer js=new StringBuffer();
		js.append("function closepage(){");
		js.append("    wx.closeWindow();");
		js.append("    window.opener=null;");
		js.append("    window.open('','_self');");
		js.append("    window.close();");
		js.append("}");
		js.append("function openorclose(id){");
		js.append("    $('#open_msg').hide();");
		js.append("    $('#close_msg').hide();");
		js.append("    $('#hite_msg').hide();");
		js.append("    if(id==1){");
		js.append("        $('#close_msg').show();");
		js.append("        $('#hite_msg').show();");
		js.append("    }else if(id==2){");
		js.append("        $('#open_msg').show();");
		js.append("    }");
		js.append("}");
		this.page.addJavaScript(js.toString());
		
		//错误信息展示
		StringBuffer errorpage=new StringBuffer();
		errorpage.append("<div data-role='page' data-theme='h' id='error_page'  data-title='").append(this.title).append("'>");
		errorpage.append("    <div data-role='header' data-theme='f'>");
		errorpage.append("        <a id='hint_page_close' onclick='closepage();' data-role='button' data-theme='f' ");
		errorpage.append("            data-icon='delete'>关闭</a>");
		errorpage.append("        <h1><lable id='hint_title'>").append(this.title).append("</lable></h1>");
		errorpage.append("    </div>");
		errorpage.append("    <div data-role='content' id='home_content' style='margin: 20px;text-align: center;");
		errorpage.append("        border: 10px solid rgb(246,246,246);background-color:rgb(222,222,222);");
		errorpage.append("        border-radius:10px;padding:10px;'>");
		errorpage.append("        <div id='error_msg' style='margin: 10px;color: red;'>").append(this.context).append("</div>");
		if(StringUtil.isNotEmpty(exception)){
			errorpage.append("    <div onclick='openorclose(1);' id='open_msg' style='margin:12px;");
		    errorpage.append("        text-align:left;cursor:pointer;color:blue;line-height: 20px;'>展开错误信息&gt;&gt;</div>");
			errorpage.append("    <div onclick='openorclose(2);' id='close_msg' style='margin:12px;text-align:right;");
			errorpage.append("        cursor:pointer;display:none;color:blue;line-height:20px;'>&lt;&lt;关闭错误信息</div>");
			errorpage.append("    <div id='hite_msg' style='margin:10px;text-align:center;display:none;'>");
			errorpage.append("        <textarea id='hint_msg' style='background:none;font-size:8px;' rows='6'>");
			errorpage.append(             this.exception).append("</textarea>");
			errorpage.append("    </div>");
		}
		errorpage.append("        <div style='text-align:center;'>");
		errorpage.append("            <a data-role='button' data-theme='b' data-inline='true' onclick='closepage();'>确认</a>");
		errorpage.append("        </div>");
		errorpage.append("    </div>");
		errorpage.append("</div>");
		this.page.addBodyContext(errorpage.toString());
		
		String str=this.page.getPage();
		 
		
		return str;
	}
}
