package com.jiuqi.mt2.dna.mobile.wxapproval.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.util.WorkflowRunUtil;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.da.DBCommand;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.da.RecordSetField;
import com.jiuqi.dna.core.da.RecordSetFieldContainer;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.BillCommon;
import com.jiuqi.mt2.dna.mobile.wxapproval.util.FileParamUtil;
import com.jiuqi.mt2.spi.bill.metadata.MobileBillDefine;
import com.jiuqi.mt2.spi.bill.model.BillData;

public class WXFileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	FTPClient ftpClient = null;
	InputStream is = null;
	OutputStream out = null;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doRedirect(request, response);
	}

	private void doRedirect(HttpServletRequest request,
			HttpServletResponse response) {
		String fileName = null;
		ContextSPI context = AppUtil.getDefaultApp().getSystemSession()
				.newContext(false);
		// 接受参数
		String workItemId = request.getParameter("workItemId");
		String recid1 = request.getParameter("recid");
		IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context, workItemId);
		MobileBillDefine billDefine = BillCommon.getMobileBillDefineInfo(
				context, iworkItem);
		BillData billData = BillCommon.getBillData(context, iworkItem,
				billDefine);
		GUID billID = GUID.tryValueOf(billData.getMasterData().getId());
		/*
		 * 根据 recid1（request获取的参数）是否为空判断附件 1、recid1 为空为 资质文档或合同文档的下载
		 * 2、recid1不为空为单据上带附件控件的下载
		 */
		if (recid1 == null) {
			String filename = request.getParameter("fileName");
			Map<String, List<String>> enclosureMap = new HashMap<String, List<String>>();
			// 获取附件 返回map集合
			getEnclosureList1(context, billID, enclosureMap);
			if (enclosureMap.size() != 0) {
				for (String enclosureName : enclosureMap.keySet()) {
					List<String> list = enclosureMap.get(enclosureName);
					// file:文件路径+文件名
					String file = list.get(0);
					// 资质名称
					String zsmc = list.get(1);
					// 合同名称
					String htmc = list.get(2);
					// 合同编号
					String htcode = list.get(3);
					// 合同部门编号
					String htbmcode = list.get(4);
					String url = "10.2.12.176";
					String port = "21";
					String user = "eip";
					String pwd = "nex5Tarchive";

					// 资质名称不为空 表示是资质文档的下载
					if (zsmc != null) {
						fileName = file.substring(file.lastIndexOf("/") + 1);
						String filepath = file.substring(0,
								file.lastIndexOf("/") + 1);
						String recid = "";
						if (filename.equals(enclosureName)) {
							downloadFromFTP(request, response, fileName, url,
									Integer.parseInt(port), user, pwd,
									filepath, recid);

						}
					}
					/**
					 * htmc不为空 表示合同文档的下载 合同文档的下载分为两种情况 ：
					 * 1、普通合同文档下载路径为合同文档/合同编号.pdf 2、合同部门为数字传播、亿起联、瑞意恒动的下载路径为
					 * 子公司合同文档/recid合同编号.pdf(recid:文件所在表G1038_ENCLOSURE中的recid)
					 */
					else if (htmc != null) {

						if (htbmcode.startsWith("106")
								|| htbmcode.startsWith("117")
								|| htbmcode.startsWith("112")) {
							Map<String, String> enclosureMap2 = new HashMap<String, String>();
							getEnclosureList2(context, htmc, enclosureMap2);

							if (enclosureMap2.size() != 0) {
								for (String enclosure : enclosureMap2.keySet()) {
									String recid = enclosureMap2.get(enclosure);

									fileName = htcode + ".pdf";
									String filepath = "子公司合同文档/";
									if (filename.equals(enclosureName)) {
										downloadFromFTP(request, response,
												fileName, url,
												Integer.parseInt(port), user,
												pwd, filepath, recid);
									}
								}
							}
						} else {
							fileName = htcode + ".pdf";
							String filepath = "合同文档/";
							String recid = "";
							if (filename.equals(enclosureName)) {
								downloadFromFTP(request, response, fileName,
										url, Integer.parseInt(port), user, pwd,
										filepath, recid);
							}
						}
					}
				}
			}
		} else {
			Map<String, List<String>> enclosureMap = new HashMap<String, List<String>>();
			// 获取附件 返回map集合
			getEnclosureList(context, billID, enclosureMap);
			if (enclosureMap.size() != 0) {
				for (String enclosureName : enclosureMap.keySet()) {
					fileName = enclosureName;
					List<String> list = enclosureMap.get(enclosureName);
					String recid = list.get(0);
					String type = list.get(1);// 判断文件是在FTP ‘3’还是数据库‘0’

					if (type.equals("3")) {
						Map<String, String> map = FileParamUtil.getUrl(
								workItemId, fileName);
						String url = map.get("url");
						String port = map.get("port");
						String user = map.get("user");
						String pwd = map.get("pwd");
						String filepath = map.get("filepath");
						if (recid.equals(recid1)) {
							downloadFromFTP(request, response, fileName, url,
									Integer.parseInt(port), user, pwd,
									filepath, recid);
						}
					} else if (type.equals("0")) {
						if (recid.equals(recid1)) {
							downloadFromDataBase(request, response, recid,
									fileName);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * 从数据库下载附件
	 * 
	 * */
	private void downloadFromDataBase(HttpServletRequest request,
			HttpServletResponse response, String recid, String fileName) {
		ContextSPI context = AppUtil.getDefaultApp().getSystemSession()
				.newContext(false);
		// 拼接dnasql语句
		StringBuffer sql = new StringBuffer();
		sql.append("define query G1038_ENCLOSURE(@recid guid) ");
		sql.append("begin ");
		sql.append("select a.enclosuredata from G1038_ENCLOSURE as a where 1=1 ");
		sql.append(" and a.recid  = @recid");
		sql.append(" end");
		// 创建数据库访问对象访问数据库
		DBCommand dbCommand = context.prepareStatement(sql.toString());
		// 设置参数值
		dbCommand.setArgumentValues(GUID.valueOf(recid));
		// 查询数据库，返回RecordSet结果集
		RecordSet ors = dbCommand.executeQuery();
		while (ors.next()) {
			// 设置response参数
			response.setContentType("application/octet-stream");
			try {
				response.setHeader(
						"Content-Disposition",
						"attachment; filename="
								+ new String(fileName.getBytes(), "ISO8859-1"));
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("expires", "0");

				OutputStream output = response.getOutputStream();
				InputStream input;

				@SuppressWarnings("unchecked")
				RecordSetFieldContainer<RecordSetField> file = (RecordSetFieldContainer<RecordSetField>) ors
						.getFields();

				byte[] filecontent1 = file.get(0).getBytes();

				Blob b = new SerialBlob(filecontent1);
				input = b.getBinaryStream();
				byte[] buffer = new byte[1024];
				int i = 0;
				while ((i = input.read(buffer)) != -1) {
					output.write(buffer, 0, i);
				}
				output.close();
				input.close();

			} catch (Exception e1) {

				e1.printStackTrace();
			}

		}
		// 释放数据库连接
		dbCommand.unuse();
	}

	/**
	 * 从ftp下载附件
	 * 
	 * */
	@SuppressWarnings("deprecation")
	private void downloadFromFTP(HttpServletRequest request,
			HttpServletResponse response, String fileName, String url,
			int port, String user, String pwd, String filepath, String recid) {

		ftpClient = new FTPClient();
		String systemKey = "WINDOWS";
		String serverLanguageCode = "zh";
		FTPClientConfig conf = new FTPClientConfig(systemKey);
		conf.setServerLanguageCode(serverLanguageCode);
		conf.setDefaultDateFormatStr("yyyy-MM-dd");

		try {
			ftpClient.configure(conf);
			ftpClient.setConnectTimeout(2000);
			ftpClient.connect(url, port);
			if (!ftpClient.login(user, pwd)) {
				ftpClient.logout();
				ftpClient = null;
			} else {
				ftpClient.setFileType(2);
				ftpClient.setControlEncoding("GBK");
				ftpClient.enterLocalPassiveMode();
				ftpClient.setBufferSize(2048);
				int replyCode = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(replyCode)) {
					ftpClient = null;
				} else {
					System.out.println("login success !!!");
				}
			}
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ new String(fileName.getBytes(), "ISO8859-1"));
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("expires", "0");
			// response.setHeader("refresh", "0.1");// 刷新当前网页，不跳转
			String path = "./../" + filepath + recid.toString() + fileName;
			is = ftpClient.retrieveFileStream(new String(path.getBytes("GBK"),
					"iso-8859-1"));
			if (is != null) {
				out = response.getOutputStream();
				byte[] temp = null;
				int c = 0;
				temp = new byte[1024];
				while ((c = is.read(temp)) != -1) {
					out.write(temp, 0, c);
				}
				is.close();
				out.close();
			}
		} catch (Exception e) {

			e.printStackTrace();

		}

		finally {

			try {
				if (is != null) {
					is.close();
				}
				if (out != null) {
					out.close();
				}
				closeServer();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	public boolean closeServer() {
		try {
			if (is != null) {
				is.close();
			}
			if (out != null) {
				out.close();
			}
			if (ftpClient != null) {
				ftpClient.logout();
				ftpClient.disconnect();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 通过billID（recid）查询附件 （包括FTP和数据库上的附件），返回一个map
	 * 
	 * */
	private void getEnclosureList(Context context, GUID billID,
			Map<String, List<String>> enclosureMap) {
		StringBuffer getEnclosureSql = new StringBuffer();
		getEnclosureSql.append("define query getEnclosures(@billID guid) \n");
		getEnclosureSql.append("begin \n");
		getEnclosureSql.append("  select t.ENCLOSURENAME as ename, \n");
		getEnclosureSql
				.append(" t.RECID as recid ,t.enclosuretype as type  from G1038_ENCLOSURE as t \n");
		getEnclosureSql.append("  where 1 = 1 \n");
		getEnclosureSql.append("  and t.BILLID = @billID \n");
		getEnclosureSql.append("  and t.ISBILLSAVE = 1 \n");
		getEnclosureSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(getEnclosureSql);
		dbCommand.setArgumentValues(billID);
		RecordSet recordSet = dbCommand.executeQuery();
		while (recordSet.next()) {
			List<String> list = new ArrayList<>();
			list.add(recordSet.getFields().get(1).getGUID().toString());
			list.add(String.valueOf(recordSet.getFields().get(2).getInt()));
			enclosureMap.put(recordSet.getFields().get(0).getString(), list);
		}
		dbCommand.unuse();
	}

	/**
	 * 通过billID（recid）查询资质文档或合同文档
	 * 
	 * */

	private void getEnclosureList1(Context context, GUID billID,
			Map<String, List<String>> enclosureMap) {
		StringBuffer getEnclosureSql = new StringBuffer();
		getEnclosureSql.append("define query getEnclosures(@billID guid) \n");
		getEnclosureSql.append("begin \n");
		getEnclosureSql
				.append("  select case when t.zsmc is null then b.stdname  when t.htmc is null   then a.stdname end  as ename,t.savepath  as recid,   t.zsmc  as  zsmc , t.htmc as  htmc,case when t.htmc  is not null   then b.stdcode  else null end as  htcode ,  \n");
		getEnclosureSql
				.append(" case  when  t.htmc  is not  null  then  bm.stdcode    else  null  end as  htbm         from HT_JYDYSQD_ITEM as t  left join ZX_GSZZRYDJB as a  on a.recid =t.zsmc left join MD_CONTRACT as b on b.recid =t.HTMC  left join MD_DEPARTMENT as bm on bm.recid =b.DEPARTMENT \n");
		getEnclosureSql.append("            where 1 = 1 \n");
		getEnclosureSql.append("    and t.mrecid  = @billID \n");
		getEnclosureSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(getEnclosureSql);
		dbCommand.setArgumentValues(billID);
		RecordSet recordSet = dbCommand.executeQuery();
		while (recordSet.next()) {
			List<String> list = new ArrayList<>();
			list.add(recordSet.getFields().get(1).getString());
			list.add(recordSet.getFields().get(2).getString());
			list.add(recordSet.getFields().get(3).getString());
			list.add(recordSet.getFields().get(4).getString());
			list.add(recordSet.getFields().get(5).getString());
			enclosureMap.put(recordSet.getFields().get(0).getString(), list);
		}
		dbCommand.unuse();
	}

	private void getEnclosureList2(Context context, String htmc,
			Map<String, String> enclosureMap) {
		StringBuffer getEnclosureSql = new StringBuffer();
		getEnclosureSql.append("define query getEnclosures(@htmc string) \n");
		getEnclosureSql.append("begin \n");
		getEnclosureSql.append("  select t.ENCLOSURENAME as ename, \n");
		getEnclosureSql
				.append(" t.RECID as recid ,t.enclosuretype as type  from G1038_ENCLOSURE as t \n");
		getEnclosureSql.append("  where 1 = 1 \n");
		getEnclosureSql
				.append("    and t.BILLID =(select p.recid   from zgshtjc as p where p.HTMC =@htmc)     \n");
		getEnclosureSql.append("    and t.ISBILLSAVE = 1 \n");
		getEnclosureSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(getEnclosureSql);
		dbCommand.setArgumentValues(htmc);
		RecordSet recordSet = dbCommand.executeQuery();
		while (recordSet.next()) {

			enclosureMap.put(recordSet.getFields().get(0).getString(),
					recordSet.getFields().get(1).getGUID().toString());
		}
		dbCommand.unuse();
	}

}
