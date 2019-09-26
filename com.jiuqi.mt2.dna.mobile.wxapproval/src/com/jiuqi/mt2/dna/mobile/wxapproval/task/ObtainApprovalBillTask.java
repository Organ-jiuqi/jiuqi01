package com.jiuqi.mt2.dna.mobile.wxapproval.task;

import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXApprovalPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXApprovedPage;
import com.jiuqi.mt2.dna.mobile.wxapproval.page.WXErrorPage;

public class ObtainApprovalBillTask extends Task<ObtainApprovalBillTask.Method>{
	private String code;
	private String workItemId;
	private String key;
	private WXErrorPage errorPage;
	private WXApprovalPage apprpvalPage;
	private WXApprovedPage approvaledPage;

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

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	public static enum Method {
		ObtainApprovalBill, ObtainApprovaledBill;
	}
}