package com.jiuqi.mt2.dna.mobile.wxapproval.util;

import java.util.HashMap;
import java.util.Map;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.workflowmanager.execute.intf.util.WorkflowRunUtil;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.BillCommon;

public class FileParamUtil {

	public static Map<String, String> getUrl(String workItemId, String fileName) {

		ContextSPI context = AppUtil.getDefaultApp().getSystemSession()
				.newContext(false);

		IWorkItem iworkItem = WorkflowRunUtil.loadWorkItem(context, workItemId);
		BillModel billModel = BillCommon.getBillModel(context, iworkItem);

		String xml = billModel.getDefine().getEditUI2().toString();
		String urlValue = xml.substring(xml.indexOf("url"),
				xml.indexOf("port") - 10);
		String url = urlValue.substring(urlValue.indexOf("value=\"") + 7,
				urlValue.lastIndexOf("\""));
		String portValue = xml.substring(xml.indexOf("port"),
				xml.indexOf("filepath") - 10);
		int port = Integer
				.parseInt(portValue.substring(
						portValue.indexOf("value=\"") + 7,
						portValue.lastIndexOf("\"")));
		String filepathValue = xml.substring(xml.indexOf("filepath"),
				xml.indexOf("user") - 10);
		String filepath = "";
		if (filepathValue.contains("value")) {
			filepath = filepathValue.substring(
					filepathValue.indexOf("value=\"") + 7,
					filepathValue.lastIndexOf("\""));
		}
		String userValue = xml.substring(xml.indexOf("user"),
				xml.indexOf("pwd") - 10);
		String user = userValue.substring(userValue.indexOf("value=\"") + 7,
				userValue.lastIndexOf("\""));
		String pwdValue = xml.substring(xml.indexOf("pwd"),
				xml.indexOf("selectF") - 10);
		String pwd = pwdValue.substring(pwdValue.indexOf("value=\"") + 7,
				pwdValue.lastIndexOf("\""));

		Map<String, String> map = new HashMap<String, String>();
		map.put("url", url);
		map.put("port", String.valueOf(port));
		map.put("user", user);
		map.put("pwd", pwd);
		map.put("filepath", filepath);
		return map;

	}
}
