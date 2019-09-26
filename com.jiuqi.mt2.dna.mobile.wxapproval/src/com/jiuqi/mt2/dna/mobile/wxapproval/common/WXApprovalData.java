package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.core.Context;
import com.jiuqi.mt2.dna.mobile.wxapproval.entity.StaffEntity;
import com.jiuqi.mt2.spi.ICallMonitor;
import com.jiuqi.mt2.spi.bill.model.BillData;

/**
 * ����������Ϣ �����û�code workitemid
 * @author liuzihao
 */
public class WXApprovalData {
	private String key;//ʶ����
	private String userName;//�û���
	
	private StaffEntity staff;//ְԱ��Ϣ
	
	private String workitemid;//������ID
	
	private String action;//ͬ��򲵻�
	private BillModel billModel;//����ģ��
	private String data;//�����仯������
	
	private int executeApproval=0;//ִ�������Ĵ���
	private int executeSave=0;//ִ�б���Ĵ���
	
	private long createtime=0;
	
	private HttpServletResponse resp;
	
	private String wxbillTitle;
	private UserDefineButtonInfo udfbi;
	
	private boolean save_check=false;//ִ�б����߼���false��ִ�У�trueִ��
	private Context context;
	private String billDataId="" ;
	
	public WXApprovalData(String action,BillModel billModel,String data,WXPlaintextScramble scramble,HttpServletResponse resp,Context context){
		this.data=data;
		this.action=action;
		this.billModel=billModel;
		this.key=scramble.getResult();
		this.userName=scramble.getCode();
		this.resp=resp;
		this.workitemid=scramble.getWorkitemid();
		this.createtime=System.currentTimeMillis();
		this.context = context;
		
		this.staff=BillCommon.getStaffEntityByCode(context, context.getLogin().getUser().getID());
	}
	
	public Context getContext(){
		return context;
	}
	
	public StaffEntity getStaff() {
		return staff;
	}

	public void setStaff(StaffEntity staff) {
		this.staff = staff;
	}

	public HttpServletResponse getResp() {
		return resp;
	}

	public void setResp(HttpServletResponse resp) {
		this.resp = resp;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getWorkitemid() {
		return workitemid;
	}
	public void setWorkitemid(String workitemid) {
		this.workitemid = workitemid;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BillModel getBillModel() {
		return billModel;
	}

	public void setBillModel(BillModel billModel) {
		this.billModel = billModel;
	}
	public int getExecuteApproval() {
		return executeApproval;
	}
	public void addExecuteApprovalTimes() {
		this.executeApproval++;
	}
	public int getExecuteSave() {
		return executeSave;
	}
	public void addExecuteSave() {
		this.executeSave++;
	}

	public long getCreatetime() {
		return createtime;
	}

	public String getWxbillTitle() {
		return wxbillTitle;
	}

	public void setWxbillTitle(String wxbillTitle) {
		this.wxbillTitle = wxbillTitle;
	}

	public UserDefineButtonInfo getUdfbi() {
		return udfbi;
	}

	public void setUdfbi(UserDefineButtonInfo udfbi) {
		this.udfbi = udfbi;
	}

	public boolean isSave_check() {
		return save_check;
	}

	public void setSave_check(boolean save_check) {
		this.save_check = save_check;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getBillDataId() {
		return billDataId;
	}

	public void setBillDataId(String billDataId) {
		this.billDataId = billDataId;
	}
	
}
