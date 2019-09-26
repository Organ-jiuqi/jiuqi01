package com.jiuqi.mt2.dna.mobile.wxapproval.function;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jiuqi.dna.bap.authority.common.query.UserAndRoleQuery;
import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.bap.authority.intf.impl.FRole;
import com.jiuqi.dna.bap.basedata.common.filter.BaseDataObjectFieldValueFilter;
import com.jiuqi.dna.bap.basedata.common.util.BaseDataCenter;
import com.jiuqi.dna.bap.basedata.intf.facade.FBaseDataObject;
import com.jiuqi.dna.bap.common.constants.BapContextProvider;
import com.jiuqi.dna.bap.designmgr2.common.util.StringUtil;
import com.jiuqi.dna.bap.model.common.expression.ModelDataContext;
import com.jiuqi.dna.bap.model.common.runtime.base.BusinessModel;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.ui.wt.widgets.MessageDialog;
import com.jiuqi.expression.DataType;
import com.jiuqi.expression.ExpressionException;
import com.jiuqi.expression.base.DataContext;
import com.jiuqi.expression.data.AbstractData;
import com.jiuqi.expression.functions.Function;
import com.jiuqi.expression.nodes.NodeList;
import com.jiuqi.mt2.dna.mobile.qiyehao.util.QiYEHAOUtil1;

public class SendMessageToWx extends Function {
	@SuppressWarnings("deprecation")
	public SendMessageToWx() {
		super("SendMessageToWx", "��΢�ŷ�����Ϣ", "EIP����");
		appendParameter("SENDPERSON", "������1", DataType.String);
		appendParameter("SENDMOREPERSON", "������2", DataType.Void);
		appendParameter("SENDROLEPERSON", "������3", DataType.String);
		appendParameter("SENDMESSAGE", "֪ͨ��Ϣ", DataType.String);
		setDescription("����ʾ����\n" + "SendMessageToWx(WXTEST[PERSON],\"���\")\n"
				+ "����һ��SENDPERSON�����ˣ����裬�ַ���(��ְԱRECID)��\n"
				+ "��������SENDMOREPERSON �����裬�ֽ���(���ְԱ���ֽ��ͣ�)��\n"
				+ "��������SENDROLEPERSON �����裬�ַ���(Ȩ�����ʶ�����Ȩ�����ö��ŷָ�)��\n"
				+ "�����ģ�SENDMESSAGE֪ͨ��Ϣ�����裬�ַ��͡�\n"
				+ "��ʽλ�ã� com.jiuqi.mt2.dna.mobile.wxapproval.function");
	}

	@Override
	public int judgeResultType(NodeList parameters) {
		return DataType.Bool;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public AbstractData callFunction(DataContext context, NodeList parameters)
			throws ExpressionException {
		ModelDataContext modelContext = (ModelDataContext) context;
		BusinessModel model = modelContext.model;
		Context context1 = BapContextProvider.getContext();
		String sendPesrson = parameters.get(0).computeResult(context)
				.getAsString();
		Object sendMorePesrson = parameters.get(1).computeResult(context)
				.getAsObject();
		String sendRolePesrson = parameters.get(2).computeResult(context)
				.getAsString();
		String sendMessage = parameters.get(3).computeResult(context)
				.getAsString();

		Set<GUID> recvStaffs = new HashSet<GUID>();
		// ��������
		if (!StringUtil.isEmpty(sendPesrson)) {
			recvStaffs.add(GUID.valueOf(sendPesrson));
		}
		// ��λ������
		if (sendMorePesrson instanceof GUID[]) {
			GUID[] recv_bytes = (GUID[]) sendMorePesrson;
			for (GUID recv : recv_bytes) {
				recvStaffs.add(recv);
			}
		}

		// �������

		if (!StringUtil.isEmpty(sendRolePesrson)) {
			String rolegroups = (String) sendRolePesrson;
			String[] groups = rolegroups.split(",");

			for (String group : groups) {
				Object obj = model.getContext().find(FRole.class, group);
				if (null == obj || !(obj instanceof FRole)) {
					return AbstractData.valueOf(false);
				}
				FRole role = (FRole) obj;
				UserAndRoleQuery query = new UserAndRoleQuery(
						model.getContext());
				List<FUser> users = query.getFUsersByRole(role.getIdentifier());
				for (FUser user : users) {
					List<FBaseDataObject> list = BaseDataCenter.getObjectList(
							model.getContext(),
							"MD_STAFF",
							new BaseDataObjectFieldValueFilter("LINKUSER", user
									.getGuid()));
					if (null != list && list.size() > 0) {
						recvStaffs.add(list.get(0).getRECID());
					}
				}
			}
		}

		if (recvStaffs.size() > 0 && !sendMessage.equals("")) {
			for (GUID staff : recvStaffs) {
				String yuzh = (String) BaseDataCenter.findObject(context1,
						"MD_STAFF", staff).getFieldValue("YUZH");
				QiYEHAOUtil1.sendMessage(yuzh, sendMessage);
			}
			return AbstractData.valueOf(true);
		} else {
			MessageDialog.alert("��ʾ��Ϣ", "��Ա��������������Ϣ������Ա��");
			return AbstractData.valueOf(false);
		}

	}
}
