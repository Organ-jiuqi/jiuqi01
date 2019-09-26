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
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.DialogInfo;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.FunctionMssageDialog;

public class WXDoAction extends Function{
	public WXDoAction() {
		super("WXDoAction", "执行指定动作", BillFunctionConst.FunctionGroup.CommonFunction.getTitle());
		this.appendParameter("ActionName", "公式标示", DataType.String);
		this.appendParameter("param", "公式参数(非必填)", DataType.String,true, false, null);
	    this.setDescription("函数示例：\n" +
				"WXDoAction(\"ActionName\",\"param\")\n"
				+ "函数说明：\n" +
				"执行动作标识为“ActionName”的动作按钮\n" +
				"参数说明：\n" +
				"参数一：动作标识,String类型\n"+
				"参数二：动作参数,String类型(非必填)\n"+
				"返回值说明：Int类型");
   }

	/**
	 * 返回值类型
	 * @param parameters 参数
	 */
	@Override
	public int judgeResultType(NodeList parameters) {
		return DataType.Int;
	}
	
	@Override
	public AbstractData callFunction(DataContext context, NodeList parameters)
			throws ExpressionException 
    {
		ModelDataContext modelContext = (ModelDataContext)context;
		BusinessModel model = modelContext.model;
		String ActionName = parameters.get(0).computeResult(context).getAsString();
		String param = "";
		if(parameters.size()>1){
			param = parameters.get(1).computeResult(context).getAsString();
		}
		if(FunctionMssageDialog.addDoAction("WXDoAction", ActionName, DialogInfo.DialogDoAction, model)){
			return AbstractData.valueOf(0);
		}
		model.executeAction(ActionName, param);
		return AbstractData.valueOf(0);
	}
}
