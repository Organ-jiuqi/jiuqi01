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
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXApprovalPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXApprovedPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXErrorPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.task.ObtainApprovalBillTask;
import com.jiuqi.mt2.dna.mobile.wxapproval.task.ObtainApprovalBillTask.Method;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * 展示审批单据信息
 * 
 * @author liuzihao
 */
public class ObtainApprovalBill extends DNAHttpServlet {
	private static final long serialVersionUID = -7875561297253335380L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		ContextSPI context = null;
		try {
			String key = req.getParameter("key");
			if (key == null || key.length() < 32) {
				SendMsgToWX.sendHint(resp,
						HintMessage.toError("提交信息有误,请联系管理员!"));
				return;
			}
			WXPlaintextScramble scramble = new WXPlaintextScramble(key);
			String code = scramble.getCode();
			String workItemId = scramble.getWorkitemid();
			context = AppUtil.getDefaultApp().getSystemSession()
					.newContext(false);
			if (StringUtil.isEmpty(code) && StringUtil.isEmpty(workItemId)) {
				SendMsgToWX.sendHtml(context, resp, new WXErrorPage("错误提示",
						"展示审批单据缺少请求信息,请联系管理员", "").getPage());
				return;
			}

			if (!StringUtil.isEmpty(code) || !StringUtil.isEmpty(workItemId)) {
				AsyncInfo e = new AsyncInfo();
				e.setSessionMode(SessionMode.INDIVIDUAL_ANONYMOUS);
				ObtainApprovalBillTask task = new ObtainApprovalBillTask();
				task.setCode(code);
				task.setWorkItemId(workItemId);
				task.setKey(key);
				AsyncTask<ObtainApprovalBillTask, Method> asynTask = context
						.asyncHandle(task, Method.ObtainApprovalBill, e);
				context.waitFor(asynTask, new AsyncHandle[0]);
				WXErrorPage errorPage = task.getErrorPage();
				WXApprovedPage approvaledPage = task.getApprovaledPage();
				WXApprovalPage approvalPage = task.getApprpvalPage();
				if (errorPage != null) {
					SendMsgToWX.sendHtml(context, resp, errorPage.getPage());
					return;
				} else {
					if (approvaledPage != null) {
						SendMsgToWX.sendHtml(context, resp,
								approvaledPage.getPage());
					} else if (approvalPage != null) {
						SendMsgToWX.sendHtml(context, resp,
								approvalPage.getPage());
						return;
					}
					return;
				}
			}
			SendMsgToWX.sendHtml(context, resp, (new WXErrorPage("错误提示",
					"展示审批单据缺少请求信息,请联系管理员", "")).getPage());
		} catch (Exception arg16) {
			context.exception(arg16);
			return;
		} finally {
			context.dispose();
		}
	}
}