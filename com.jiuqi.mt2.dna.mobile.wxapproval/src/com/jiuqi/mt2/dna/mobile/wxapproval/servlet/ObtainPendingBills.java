package com.jiuqi.mt2.dna.mobile.wxapproval.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.bap.workflowmanager.execute.intf.consts.ApprovalState;
import com.jiuqi.dna.core.http.DNAHttpServlet;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.mt2.dna.mobile.qiyehao.util.QiYEHAOUtil;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXPlaintextScramble;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.SendMsgToWX;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXPendingBillsPage;

/**
 * 获取单据组的列表
 * 
 * @author liuzihao
 */
public class ObtainPendingBills extends DNAHttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String userId;
		String key = req.getParameter("key");
		String code = req.getParameter("code");
		if (key != null) {
			WXPlaintextScramble a = new WXPlaintextScramble(key);
			userId = a.getCode();
		} else {
			userId = QiYEHAOUtil.getUserID(code);
		}
		List<ApprovalState> las = new ArrayList<ApprovalState>();
		las.add(ApprovalState.WAIT);
		las.add(ApprovalState.WAITHIGHER);
		ContextSPI context = AppUtil.getDefaultApp().getSystemSession()
				.newContext(false);
		if (userId == null || userId.equals("")) {
			return;
		}
		WXPendingBillsPage page = new WXPendingBillsPage(context, las, userId,
				"未完成单据");
		SendMsgToWX.sendHtml(context, resp, page.getHtml());
	}
}
