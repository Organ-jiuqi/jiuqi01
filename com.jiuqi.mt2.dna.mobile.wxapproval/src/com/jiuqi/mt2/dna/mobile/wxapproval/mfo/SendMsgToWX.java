package com.jiuqi.mt2.dna.mobile.wxapproval.mfo;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXErrorPage;
import com.jiuqi.mt2.spi.ICallMonitor;
import com.jiuqi.mt2.spi.IProcessContext;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * ����ҳ���������ҳ�淢����Ϣ
 * 
 * @author liuzihao
 */
public class SendMsgToWX {
	/**
	 * ��ƴ�Ӻõ�ҳ�淢�͸�΢�Ŷ�
	 * @param callMonitor ICallMonitor
	 * @param response  HttpServletResponse
	 * @param html  String ҳ��
	 */
	public static void sendHtml(Context context, HttpServletResponse response, String html) {
		try {
			if (context != null && context instanceof ContextSPI) {
				((ContextSPI) context).dispose();
			}
			OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "utf-8");
			response.setContentType("text/html");
			out.write(html);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���ͷ��ؽ��
	 * @param response HttpServletResponse
	 * @param result String
	 */
	public static void sendHint(Context context, HttpServletResponse response, String result) {
		try {
			if (context != null && context instanceof ContextSPI) {
				((ContextSPI) context).dispose();
			}
			OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "utf-8");
			response.setContentType("application/json");
			out.write(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��ƴ�Ӻõ�ҳ�淢�͸�΢�Ŷ�
	 * @param callMonitor ICallMonitor
	 * @param response  HttpServletResponse
	 * @param html  String ҳ��
	 */
	public static void sendHtml( HttpServletResponse response, String html) {
		try {
			OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "utf-8");
			response.setContentType("text/html");
			out.write(html);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���ͷ��ؽ��
	 * @param response HttpServletResponse
	 * @param result String
	 */
	public static void sendHint(HttpServletResponse response, String result) {
		try {
			OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "utf-8");
			response.setContentType("application/json");
			out.write(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ͼƬ��ҳ��
	 */
	public static void sendImage(HttpServletResponse resp, byte[] result) {
		try {
			OutputStream toClient = new BufferedOutputStream(resp.getOutputStream());
			resp.setContentType("image/png");
			toClient.write(result);
			toClient.flush();
			toClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����쳣��Ϣ
	 */
	public static void sendException(Exception e,HttpServletResponse resp){
		if(StringUtil.isNotEmpty(e.getMessage())){
			System.out.println(e.getMessage());
		}
		e.printStackTrace();
		StringBuffer error=new StringBuffer();
		for(StackTraceElement einfo: e.getStackTrace()){
			error.append(einfo).append("<br>");
		}
		SendMsgToWX.sendHint(resp, HintMessage.toError(StringUtil.isNotEmpty(e.getMessage())?e.getMessage():"�����쳣",error.toString()));
	}
	
	/**
	 * �����쳣��Ϣ����ҳ��
	 */
	public static void sendExceptionToPage(Exception e,HttpServletResponse resp){
		if(StringUtil.isNotEmpty(e.getMessage())){
			System.out.println(e.getMessage());
		}
		e.printStackTrace();
		StringBuffer error=new StringBuffer();
		for(StackTraceElement einfo: e.getStackTrace()){
			error.append(einfo).append("<br>");
		}
		SendMsgToWX.sendHtml(resp, new WXErrorPage("�����쳣��",StringUtil.isNotEmpty(e.getMessage())?e.getMessage():"�����쳣������ϵ����Ա!",error.toString()).getPage());
	}
}
