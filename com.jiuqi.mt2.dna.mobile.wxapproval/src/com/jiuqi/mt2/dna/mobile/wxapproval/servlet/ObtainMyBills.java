package com.jiuqi.mt2.dna.mobile.wxapproval.servlet;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.bap.workflowmanager.execute.intf.consts.ApplyState;
import com.jiuqi.dna.core.http.DNAHttpServlet;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.mt2.dna.mobile.qiyehao.util.QiYEHAOUtil;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.SendMsgToWX;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXMyBillsPage;
/**
 * 获取我提交的单据列表
 * @author liuzihao
 */
public class ObtainMyBills extends DNAHttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		doGet(req, resp);
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp){
		String code = req.getParameter("code");
		String userId = QiYEHAOUtil.getUserID(code);
		ArrayList<ApplyState> las = new ArrayList<ApplyState>();
		las.add(ApplyState.APPROVAL);
		las.add(ApplyState.BACK);
		ContextSPI context = AppUtil.getDefaultApp().getSystemSession()
				.newContext(false);
		WXMyBillsPage page = new WXMyBillsPage(context, las, userId, "我提交的单据");
		SendMsgToWX.sendHtml(context, resp, page.getHtml());
	}
}
