package com.jiuqi.mt2.dna.mobile.wxapproval.component;

import java.util.ArrayList;
import java.util.List;


/**
 * 微信页面
 * @author liuzihao
 */
public class WXHtml {
	private StringBuffer html=new StringBuffer();
	
	private WXHead head;
	private WXBody body;
	private List<String> javascript;
	
	public WXHtml (){
		this.head=new WXHead();
		this.body=new WXBody();
		this.javascript=new ArrayList<String>();
	}
	
	
	public void addHeadPorperty(String property){
		this.head.addProperty(property);
	}
	public void addMeta(String meta){
		this.head.addMeta(meta);
	}
	public void addLink(String link){
		this.head.addLink(link);
	}
	public void addTitle(String title){
		this.head.addTitle(title);
	}
	public void addCSS(String css){
		this.head.addCSS(css);
	}
	public void addHeadContext(String context){
		this.head.addContext(context);
	}
	
	
	
	public void addBodyPorperty(String property){
		this.body.addProperty(property);
	}
	public void addBodyContext(String context){
		this.body.addContext(context);
	}
	
	public void addJavaScript(String wxjavaScript){
		this.javascript.add(wxjavaScript);
	}
	
	
	
	
	
	
	
	public StringBuffer getHtml(){
		this.html.append("<!DOCTYPE html>").append("\n");
		this.html.append("<html>").append("\n");
		
		this.html.append(head.getHead());
		this.html.append(body.getBody());
		
		if(javascript!=null&&javascript.size()>0){
			this.html.append("<!--  JavaScript部分  -->\n");
			this.html.append("\t<script language='javascript' type='text/javascript'>\n");
			for(String js : javascript){
				this.html.append("\t\t").append(js).append("\n");
			}
			this.html.append("\t</script>\n");
		}
		
		this.html.append("</html>");
		return html;
	}
	

}
