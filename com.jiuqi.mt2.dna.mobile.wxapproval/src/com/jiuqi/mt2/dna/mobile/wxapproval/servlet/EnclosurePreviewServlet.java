package com.jiuqi.mt2.dna.mobile.wxapproval.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.dna.core.spi.application.Session;
import com.jiuqi.dna.ui.wt.graphics.utils.ImageRegistry;
 
import com.jiuqi.vacomm.utils.document.DocumentConvertFactory;
import com.jiuqi.vacomm.utils.document.IllegalDocumentTypeException;
import com.jiuqi.xlib.utils.StringUtil;
import com.jiuqi.xlib.utils.text.Base64;


public class EnclosurePreviewServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRedirect(request, response);
	}
	
	private void doRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取context
	 	Session session = com.jiuqi.dna.core.spi.application.AppUtil.getDefaultApp().getSystemSession();
		Context context = session.newContext(false);
		response.setHeader("pragma", "no-cache");
		response.setHeader("cache-control", "no-no-cache");
		response.setHeader("expires", "0");
		String[] queryStrArr = request.getQueryString().split("&");// 直接getParamter()可能会因为+和%等字符被字符集重新解码后变为空格，因此直接获取原始的完整参数
		String name = "";
		String value = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		for (String param : queryStrArr) {
			name = param.substring(0, param.indexOf("="));
			value = param.substring(param.indexOf("=") + 1);
			paramMap.put(name, value);
		}
		String fileID = paramMap.get("fileID");
		String accessId;
		try {
			accessId = new String(Base64.base64ToByteArray(fileID));
		} catch (Exception e1) {
			OutputStream out = null;
			try {
				out = response.getOutputStream();
				writeErrorPage(out);
			} 
			finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return;
		}
		String fileName = null;
		String widthStr = 400 + "";
		String heightStr = "";
		if (!StringUtil.isEmpty(accessId) && accessId.indexOf('$') > 0) {
			String[] ss = accessId.split("\\$");
			accessId = ss[0];
			fileName = ss[1];// 文件类型
			if(ss.length > 2){
				widthStr = ss[2];
			}
			if(ss.length > 3){
				heightStr = ss[3];
			}
		}
		int index = 0;
		if((index = widthStr.indexOf(".")) > 0){
			widthStr = widthStr.substring(0, index);
		}
		if((index = heightStr.indexOf(".")) > 0){
			heightStr = heightStr.substring(0, index);
		}
		String type = fileName.substring(fileName.lastIndexOf(".") + 1);
		InputStream input = null;
		byte[] data = null;
		if(ImageRegistry.getBySimpleId(accessId) == null){
			data = ImageRegistry.get(accessId).getImageData().getBytes();
		}else{
			data = ImageRegistry.getBySimpleId(accessId).getImageData().getBytes();
		}
		input = new ByteArrayInputStream(data);
		OutputStream out = null;
		try {
			out = response.getOutputStream();
			response.setContentType("text/html;charset=gbk");
			String queryString = request.getQueryString();
			String url = request.getContextPath() + request.getServletPath() + "?fileID=" + fileID;
			if(type.equals("pdf") && !queryString.contains("&width=" + widthStr)){
				queryString = String.format("%1$s&width=" + widthStr, queryString);
			}
			if((type.equals("xls") || type.equals("xlsx")) && !queryString.contains("&height=" + widthStr)){
				queryString = String.format("%1$s&height=" + widthStr, queryString);
			}
			if (heightStr.length() > 0) {
				queryString = queryString + "&height=" + heightStr;
			}
			if (type.equals("txt")) {
				String txt = new String(data);
				writeTxtPage(out, txt);
			}else {
				DocumentConvertFactory.create(fileName).convert(input, out, url, queryString);
			}
		} catch (IllegalDocumentTypeException e) {
			writeErrorPage(out);
		} finally {
			if (input != null) {
				input.close();
			}
			if (out != null) {
				out.close();
			}
			if (context != null) {
				((ContextSPI) context).dispose();
			}
		}
	}

	private void writeErrorPage(OutputStream out) throws IOException {
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head>");
		html.append("<META http-equiv='Content-Type' content='text/html; charset=GB2312'>");
		html.append("</head>");
		html.append("<body>");
		html.append("未知的文件类型，无法预览。");
		html.append("</body>");
		html.append("</html>");
		out.write((html.toString()).getBytes());
		out.flush();
	}
	
	private void writeTxtPage(OutputStream out, String txt) throws IOException {
		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head>");
		html.append("<META http-equiv='Content-Type' content='text/html; charset=GB2312'>");
		html.append("</head>");
		html.append("<body>");
		for (String t : txt.split("\\r\\n")) {
			html.append(t + "<br/>");
		}
		html.append("</body>");
		html.append("</html>");
		out.write((html.toString()).getBytes());
		out.flush();
	}
}