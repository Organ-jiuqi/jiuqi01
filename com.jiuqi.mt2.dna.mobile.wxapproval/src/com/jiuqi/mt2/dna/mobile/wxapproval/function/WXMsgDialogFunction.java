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
		super("WXMsgDialog", "显示提示消息窗口", BillFunctionConst.FunctionGroup.CommonFunction.getTitle());
		appendParameter("title", "标题", DataType.String);
		appendParameter("message", "消息", DataType.String);
		StringBuffer description = new StringBuffer();
		description.append("函数示例：WXMsgDialog(\"提示信息\",\"数据类型不匹配。\")。\n");
		description.append("函数意义：在执行函数时弹出提示信息框，消息内容为“数据类型不匹配。\n");
		description.append("参数说明：\n");
		description.append("参数一：标题；\n");
		description.append("参数二：消息。\n");
		description.append("返回值说明：返回Boolean类型。\n");
		setDescription(description.toString());
	}

	@Override
	public AbstractData callFunction(DataContext context, NodeList parameters)
			throws ExpressionException {
		ModelDataContext modelContext = (ModelDataContext)context;
		BusinessModel model = modelContext.model;
		String title = parameters.get(0).computeResult(context).getAsString();
		String message = parameters.get(1).computeResult(context).getAsString();
		
		//将信息传递出来
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
