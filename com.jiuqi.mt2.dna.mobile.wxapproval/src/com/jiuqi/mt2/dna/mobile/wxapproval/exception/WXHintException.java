package com.jiuqi.mt2.dna.mobile.wxapproval.exception;

/**
 * һЩλ����Ҫ�жϣ�ͬʱ��ҳ����Ӧ���
 * @author liuzihao
 */
public class WXHintException extends Exception{
	private static final long serialVersionUID = -7157889312748200657L;
	private String context;
	
	public WXHintException(String message){
		this.context=message;
	}
	public WXHintException(String message,StackTraceElement[] s){
		this.context=message;
		super.setStackTrace(s);
	}
	
	public String getContext(){
		return this.context;
	}

}
