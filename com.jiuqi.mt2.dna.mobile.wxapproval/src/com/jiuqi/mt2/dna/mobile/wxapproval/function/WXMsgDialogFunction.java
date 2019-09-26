package com.jiuqi.mt2.dna.mobile.wxapproval.function;

import com.jiuqi.dna.bap.model.common.expression.ModelDataContext;
import com.jiuqi.dna.bap.model.common.runtime.base.BusinessModel;
import com.jiuqi.expression.DataType;
import com.jiuqi.expression.ExpressionException;
import com.jiuqi.expression.base.DataContext;
import com.jiuqi.expression.data.AbstractData;
import com.jiuqi.expression.functions.Function;
import com.jiuqi.expression.nodes.NodeList;
import com.jiuqi.vacomm.model.common.billconst.BillFunctionConst;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.FunctionMssageDialog;

/**
 * @author liuzihao
 * @deprecated
 */
public class WXMsgDialogFunction extends Function {
	public WXMsgDialogFunction() {
		super("WXMsgDialog", "��ʾ��ʾ��Ϣ����", BillFunctionConst.FunctionGroup.CommonFunction.getTitle());
		appendParameter("title", "����", DataType.String);
		appendParameter("message", "��Ϣ", DataType.String);
		StringBuffer description = new StringBuffer();
		description.append("����ʾ����WXMsgDialog(\"��ʾ��Ϣ\",\"�������Ͳ�ƥ�䡣\")��\n");
		description.append("�������壺��ִ�к���ʱ������ʾ��Ϣ����Ϣ����Ϊ���������Ͳ�ƥ�䡣\n");
		description.append("����˵����\n");
		description.append("����һ�����⣻\n");
		description.append("����������Ϣ��\n");
		description.append("����ֵ˵��������Boolean���͡�\n");
		setDescription(description.toString());
	}

	@Override
	public AbstractData callFunction(DataContext context, NodeList parameters)
			throws ExpressionException {
		ModelDataContext modelContext = (ModelDataContext)context;
		BusinessModel model = modelContext.model;
		String title = parameters.get(0).computeResult(context).getAsString();
		String message = parameters.get(1).computeResult(context).getAsString();
		
		//����Ϣ���ݳ���
		if(FunctionMssageDialog.addAlert(title,message,"WXMsgDialog",model)){
			return AbstractData.valueOf(true);
		}
		
		if(title == "")
			model.messageDialog.alert(message);
		if(message == "")
			return AbstractData.valueOf(false);
		model.messageDialog.alert(title, message);
		return AbstractData.valueOf(true);
	}

	@Override
	public int judgeResultType(NodeList parameters) {
		return DataType.Bool;
	}

}
