package com.jiuqi.mt2.dna.mobile.wxapproval.component;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面的Body
 * @author liuzihao
 */
public class WXBody {
	private StringBuffer body;
	
	private List<String> property=new ArrayList<String>();//属性
	private List<String> context=new ArrayList<String>();//内容
	
	public WXBody(){
		body=new StringBuffer("<body ");
	}
	
	public void addProperty(String str){
		property.add(str);
	}
	public void addContext(String str){
		context.add(str);
	}
	
	public StringBuffer getBody(){
		if(property!=null&&property.size()>0){
			for(String p : property){
				body.append(p).append(" ");
			}
		}
		body.append(">").append("\n");
		
		if(context!=null&&context.size()>0){
			for(String c : context){
				body.append(c).append("\n");
			}
		}
		
		body.append("</body>").append("\n");
		return body;
	}
}
