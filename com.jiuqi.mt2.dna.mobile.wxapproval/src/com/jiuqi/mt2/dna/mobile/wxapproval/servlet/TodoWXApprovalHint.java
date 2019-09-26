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
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXErrorPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.task.TodoWXApprovalTask;
import com.jiuqi.mt2.dna.mobile.wxapproval.task.TodoWXApprovalTask.Method;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * ִ�а�ť�� �����˶���ȷ�Ͽ�ĺ�������
 * 
 * @author liuzihao
 */
@SuppressWarnings("restriction")
public class TodoWXApprovalHint extends DNAHttpServlet {
	private static final long serialVersionUID = -4296481350165323821L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp){
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		String key = req.getParameter("key");// key����������
		if(key==null || key.length()<32){
			SendMsgToWX.sendHint(resp, HintMessage.toError("�ύ��Ϣ����,����ϵ����Ա!"));
			return;
		}
		WXPlaintextScramble scramble = new WXPlaintextScramble(key);
		String code = scramble.getCode();//�û���
		String workItemId = scramble.getWorkitemid();//�ڵ�ID
		String userSelect = req.getParameter("userSelect");// �û���ѡ��
		String hintId = req.getParameter("hintId");// ��ʾID
		
		if (StringUtil.isEmpty(code) || StringUtil.isEmpty(workItemId) || StringUtil.isEmpty(userSelect) || StringUtil.isEmpty(hintId) ) {
			SendMsgToWX.sendHint(resp, HintMessage.toError("�ύ��Ϣ����,����ϵ����Ա!"));
			return;
		}
		
		ContextSPI context = null;

		try {
			context = AppUtil.getDefaultApp().getSystemSession().newContext(false);
			AsyncInfo e = new AsyncInfo();
			e.setSessionMode(SessionMode.INDIVIDUAL_ANONYMOUS);
			TodoWXApprovalTask task = new TodoWXApprovalTask();
			task.setCode(code);
			task.setWorkItemId(workItemId);
			task.setKey(key);
			task.setResp(resp);
			task.setHintId(hintId);
			task.setUserSelect(userSelect);
			task.setScramble(scramble);
			AsyncTask<TodoWXApprovalTask, Method> asynTask = context.asyncHandle(task,
					Method.DoWXApprovalHint, e);
			context.waitFor(asynTask, new AsyncHandle[0]);
			String errorInfo = task.getErrorInfo();
			WXErrorPage errorPage = task.getErrorPage();
			if (!StringUtil.isEmpty(errorInfo)) {
				SendMsgToWX.sendHint(resp, errorInfo);
			} else if (errorPage != null) {
				SendMsgToWX
						.sendHtml(context, resp, errorPage.getPage());
			}
		} catch (Exception arg17) {
			context.exception(arg17);
		} finally {
			context.dispose();
		}
	}
	
}
