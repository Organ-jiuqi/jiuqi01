package com.jiuqi.mt2.dna.mobile.wxapproval.component;

import java.util.ArrayList;
import java.util.List;

/**
 * ҳ�� head
 * @author liuzihao
 */
public class WXHead {
	private StringBuffer head=new StringBuffer();
	
	private List<String> property=new ArrayList<String>();//����
	private List<String> meta =new ArrayList<String>();//Mate��ǩ
	private List<String> link =new ArrayList<String>();//�ⲿ����
	private String title=new String();//ҳ�����
	private List<String> css=new ArrayList<String>();//CSS��ʽ
	private List<String> context=new ArrayList<String>();//��������
	
	public WXHead(){
		this.meta.add(WXMeta.DefineViewport);
	}
	
	public void addProperty(String str){
		this.property.add(str);
	}
	public void addMeta(String meta){
		this.meta.add(meta);
	}
	public void addLink(String str){
		this.link.add(str);
	}
	public void addTitle(String title){
		this.title=title;
	}
	public void addCSS(String css){
		this.css.add(css);
	}
	public void addContext(String str){
		this.context.add(str);
	}
	
	public StringBuffer getHead(){
		this.head.append("<head ");
		if(property!=null&&property.size()>0){
			for(String p : property){
				this.head.append(p).append(" ");
			}
		}
		this.head.append(">\n");
		
		if(meta!=null&&meta.size()>0){
			for(String m : meta){
				this.head.append("\t").append(m).append("\n");
			}
		}
		
		if(link!=null&&link.size()>0){
			this.head.append("<!--  �ⲿ����JS��CSS  -->\n");
			for(String l : link){
				this.head.append("\t").append(l).append("\n");
			}
		}
		
		if(title!=null){
			this.head.append("\t<title>").append(title).append("</title>\n");
		}
		
		if(css!=null&&css.size()>0){
			this.head.append("<!--  CSS��ʽ  -->\n");
			this.head.append("\t<style type='text/css'>\n");
			for(String c : css){
				this.head.append("\t\t").append(c).append("\n");
			}
			this.head.append("\t</style>\n");
		}
		
		if(context!=null&&context.size()>0){
			for(String c : context){
				this.head.append("\t").append(c).append("\n");
			}
		}
		
		this.head.append("</head>\n");
		return this.head;
	}
	
	
}
