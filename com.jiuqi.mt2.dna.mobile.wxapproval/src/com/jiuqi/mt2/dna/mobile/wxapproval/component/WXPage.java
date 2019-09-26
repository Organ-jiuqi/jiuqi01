package com.jiuqi.mt2.dna.mobile.wxapproval.component;

/**
 * ΢��ҳ����װ���������齨ֱ��������þ���
 * @author liuzihao
 */
public class WXPage {
	private WXHtml html;
	
	public WXPage() {
		this.html=new WXHtml();
	}
	
	public void addHeadPorperty(String property){
		this.html.addHeadPorperty(property);
	}
	public void addMeta(String meta){
		this.html.addMeta(meta);
	}
	public void addLink(String link){
		this.html.addLink(link);
	}
	public void addTitle(String title){
		this.html.addTitle(title);
	}
	public void addCSS(String css){
		this.html.addCSS(css);
	}
	public void addHeadContext(String context){
		this.html.addHeadContext(context);
	}
	
	public void addBodyPorperty(String property){
		this.html.addBodyPorperty(property);
	}
	public void addBodyContext(String context){
		this.html.addBodyContext(context);
	}
	
	public void addJavaScript(String javaSciprt){
		this.html.addJavaScript(javaSciprt);
	}
	
	//����ҳ��
	public String getPage(){
		return this.html.getHtml().toString();
	}
}
