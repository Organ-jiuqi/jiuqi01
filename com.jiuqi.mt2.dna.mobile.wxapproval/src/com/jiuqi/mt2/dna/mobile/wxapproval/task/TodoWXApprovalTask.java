package com.jiuqi.mt2.dna.mobile.wxapproval.task;

import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.WXPlaintextScramble;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXApprovalPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXApprovedPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXErrorPage;
import javax.servlet.http.HttpServletResponse;

public class TodoWXApprovalTask extends Task<TodoWXApprovalTask.Method>{
	private String code;
	private String workItemId;
	private String key;
	private String actionName;
	private String comment;
	private String data;
	private String hintId;
	private String userSelect;
	private String errorInfo;
	private WXErrorPage errorPage;
	private WXApprovalPage apprpvalPage;
	private WXApprovedPage approvaledPage;
	private HttpServletResponse resp;
	private WXPlaintextScramble scramble;

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getWorkItemId() {
		return this.workItemId;
	}

	public void setWorkItemId(String workItemId) {
		this.workItemId = workItemId;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getActionName() {
		return this.actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public WXErrorPage getErrorPage() {
		return this.errorPage;
	}

	public void setErrorPage(WXErrorPage errorPage) {
		this.errorPage = errorPage;
	}

	public WXApprovalPage getApprpvalPage() {
		return this.apprpvalPage;
	}

	public void setApprpvalPage(WXApprovalPage apprpvalPage) {
		this.apprpvalPage = apprpvalPage;
	}

	public WXApprovedPage getApprovaledPage() {
		return this.approvaledPage;
	}

	public void setApprovaledPage(WXApprovedPage approvaledPage) {
		this.approvaledPage = approvaledPage;
	}

	public String getErrorInfo() {
		return this.errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public HttpServletResponse getResp() {
		return this.resp;
	}

	public void setResp(HttpServletResponse resp) {
		this.resp = resp;
	}

	public WXPlaintextScramble getScramble() {
		return this.scramble;
	}

	public void setScramble(WXPlaintextScramble scramble) {
		this.scramble = scramble;
	}

	public String getHintId() {
		return this.hintId;
	}

	public void setHintId(String hintId) {
		this.hintId = hintId;
	}

	public String getUserSelect() {
		return this.userSelect;
	}

	public void setUserSelect(String userSelect) {
		this.userSelect = userSelect;
	}
	public static enum Method {
		DoWXApproval, DoWXApprovalHint;
	}
}