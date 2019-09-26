package com.jiuqi.mt2.dna.mobile.wxapproval.noused;

import com.jiuqi.dna.bap.bill.common.model.BillCentre;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.bill.intf.facade.model.FBillDefine;
import com.jiuqi.dna.bap.bill.intf.model.BillConst;
import com.jiuqi.dna.bap.log.intf.task.AddLogInfoTask;
import com.jiuqi.dna.bap.log.intf.task.AddLogInfoTask.Constant;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.mt2.dna.service.bill.process.GetBillDefine;
import com.jiuqi.mt2.spi.ICallMonitor;
import com.jiuqi.mt2.spi.bill.exception.HintException;
import com.jiuqi.mt2.spi.bill.metadata.MobileBillDefine;
import com.jiuqi.mt2.spi.log.MobileLog;

public class TodoWXCommit {

	public boolean commitBill(String billId, String dataId, ICallMonitor monitor) {
		Context context = monitor.getAdaptor(Context.class);
		// ����billId�����ƶ����ݶ���
		MobileBillDefine mBillDefine = GetBillDefine.getInstance().getBillDefine(billId, monitor);
		FBillDefine billDefine = context.find(FBillDefine.class, GUID.valueOf(mBillDefine.getTempId()));
		BillModel billModel = BillCentre.createBillModel(context, billDefine);
		billModel.load(GUID.valueOf(dataId));
		try {
			boolean result = billModel.commitToWorkflow();
			if (billModel.isNeedSaveLog() && result) {
				// ϵͳ��־
				AddLogInfoTask sysTask = new AddLogInfoTask(Constant.INFOMATION, billModel.getDefine().getBillInfo().getTitle(), "�ύ�ɹ���", getSysLogBillName(billModel));
				billModel.getContext().asyncHandle(sysTask);
			}
			return result;
		} catch (Exception e) {
			MobileLog.logError(e);
			throw new HintException(e.getMessage());
		}
	}

	protected String getSysLogBillName(BillModel billModel) {
		if (billModel.getModelData().getMaster().getTable().find(BillConst.f_billCode) == null) {
			return "(����ID��" + billModel.getModelData().getMaster().getRECID() + ")"; //$NON-NLS-1$
		}
		return "(���ݱ�ţ�" //$NON-NLS-1$
				+ billModel.getModelData().getMaster().getValueAsString(BillConst.f_billCode) + ")";
	}
}
