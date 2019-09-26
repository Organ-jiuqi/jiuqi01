package com.jiuqi.mt2.dna.mobile.wxapproval.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.core.http.DNAHttpServlet;
import com.jiuqi.dna.core.invoke.AsyncHandle;
import com.jiuqi.dna.core.invoke.AsyncTask;
import com.jiuqi.dna.core.service.AsyncInfo;
import com.jiuqi.dna.core.service.AsyncInfo.SessionMode;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXPlaintextScramble;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.HintMessage;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.SendMsgToWX;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXApprovedPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXErrorPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.task.ObtainApprovalBillTask;
import com.jiuqi.mt2.dna.mobile.wxapproval.task.ObtainApprovalBillTask.Method;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * �����������ĵ���
 * 
 * @author liuzihao
 */
public class ObtainApprovedBill extends DNAHttpServlet {

	private static final long serialVersionUID = -7875561297253335380L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		ContextSPI context = null;
		try {
			context = AppUtil.getDefaultApp().getSystemSession()
					.newContext(false);
			String key = req.getParameter("key");
			if (key == null || key.length() < 32) {
				SendMsgToWX.sendHint(resp,
						HintMessage.toError("�ύ��Ϣ����,����ϵ����Ա!"));
				return;
			}
			WXPlaintextScramble scramble = new WXPlaintextScramble(key);
			String code = scramble.getCode();
			String workItemId = scramble.getWorkitemid();
			if (StringUtil.isEmpty(code) && StringUtil.isEmpty(workItemId)) {
				SendMsgToWX.sendHtml(context, resp, new WXErrorPage("������ʾ",
						"չʾ��������ȱ��������Ϣ,����ϵ����Ա", "").getPage());
				return;
			}
			AsyncInfo e = new AsyncInfo();
			e.setSessionMode(SessionMode.INDIVIDUAL_ANONYMOUS);
			ObtainApprovalBillTask task = new ObtainApprovalBillTask();
			task.setCode(code);
			task.setKey(key);
			task.setWorkItemId(workItemId);
			AsyncTask<ObtainApprovalBillTask, Method> asynTask = context
					.asyncHandle(task, Method.ObtainApprovaledBill, e);
			context.waitFor(asynTask, new AsyncHandle[0]);
			WXErrorPage errorPage = task.getErrorPage();
			WXApprovedPage approvaledPage = task.getApprovaledPage();
			if (errorPage != null) {
				SendMsgToWX.sendHtml(context, resp, errorPage.getPage());
			} else if (approvaledPage != null) {
				SendMsgToWX.sendHtml(context, resp, approvaledPage.getPage());
				return;
			}
			return;
		} catch (Exception e) {
			SendMsgToWX.sendExceptionToPage(e, resp);
		}
	}
}
