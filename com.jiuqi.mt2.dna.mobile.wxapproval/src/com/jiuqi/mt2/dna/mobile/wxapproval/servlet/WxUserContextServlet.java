package com.jiuqi.mt2.dna.mobile.wxapproval.servlet;

import com.jiuqi.dna.core.http.DNAHttpServlet;
import com.jiuqi.dna.core.invoke.AsyncTask;
import com.jiuqi.dna.core.service.AsyncInfo;
import com.jiuqi.dna.core.service.AsyncInfo.SessionMode;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.mt2.dna.mobile.qiyehao.task.WxUserContextTask;
import com.jiuqi.mt2.dna.mobile.qiyehao.task.WxUserContextTask.Method;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.HintMessage;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.SendMsgToWX;
import com.jiuqi.mt2.dna.mobile.wxapproval.util.WxContextUtil;
import com.jiuqi.xlib.utils.StringUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WxUserContextServlet extends DNAHttpServlet {
	private static final long serialVersionUID = 6954550452912619523L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		ContextSPI context = AppUtil.getDefaultApp().getSystemSession()
				.newContext(false);
		String code = req.getParameter("code");
		if (StringUtil.isEmpty(code)) {
			SendMsgToWX.sendHint(resp, HintMessage.toError("获取审批员工Code出错!"));
		} else {
			try {
				AsyncInfo e = new AsyncInfo();
				e.setSessionMode(SessionMode.INDIVIDUAL_ANONYMOUS);
				WxUserContextTask task = new WxUserContextTask();
				task.setCode(code);
				AsyncTask asynTask = context.asyncHandle(task, Method.Add, e);

				while (asynTask.getProgress() != 1.0F) {
					;
				}

				WxContextUtil.setWxUserContext(task.getCode(),
						task.getUserContext());
			} catch (Exception arg10) {
				context.exception(arg10);
			} finally {
				context.resolveTrans();
				context.dispose();
			}

		}
	}
}