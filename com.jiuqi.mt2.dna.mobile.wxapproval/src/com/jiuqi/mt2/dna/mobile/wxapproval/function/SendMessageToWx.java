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
		super("SendMessageToWx", "向微信发送消息", "EIP函数");
		appendParameter("SENDPERSON", "接收人1", DataType.String);
		appendParameter("SENDMOREPERSON", "接收人2", DataType.Void);
		appendParameter("SENDROLEPERSON", "接收人3", DataType.String);
		appendParameter("SENDMESSAGE", "通知信息", DataType.String);
		setDescription("函数示例：\n" + "SendMessageToWx(WXTEST[PERSON],\"你好\")\n"
				+ "参数一：SENDPERSON接收人，必需，字符型(单职员RECID)。\n"
				+ "参数二：SENDMOREPERSON ，必需，字节型(多个职员（字节型）)。\n"
				+ "参数三：SENDROLEPERSON ，必需，字符型(权限组标识，多个权限组用逗号分隔)。\n"
				+ "参数四：SENDMESSAGE通知信息，必需，字符型。\n"
				+ "公式位置： com.jiuqi.mt2.dna.mobile.wxapproval.function");
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
		// 单接收人
		if (!StringUtil.isEmpty(sendPesrson)) {
			recvStaffs.add(GUID.valueOf(sendPesrson));
		}
		// 多位接收人
		if (sendMorePesrson instanceof GUID[]) {
			GUID[] recv_bytes = (GUID[]) sendMorePesrson;
			for (GUID recv : recv_bytes) {
				recvStaffs.add(recv);
			}
		}

		// 组接收人

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
			MessageDialog.alert("提示信息", "人员设置有误，请检查消息接收人员！");
			return AbstractData.valueOf(false);
		}

	}
}
