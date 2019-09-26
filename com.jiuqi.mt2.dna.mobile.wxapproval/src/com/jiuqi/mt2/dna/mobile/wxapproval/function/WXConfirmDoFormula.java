package com.jiuqi.mt2.dna.mobile.wxapproval.function;

import com.jiuqi.dna.bap.model.common.expression.ModelDataContext;
import com.jiuqi.dna.bap.model.common.runtime.base.BusinessModel;
import com.jiuqi.dna.ui.wt.events.SelectionEvent;
import com.jiuqi.dna.ui.wt.events.SelectionListener;
import com.jiuqi.dna.ui.wt.widgets.MessageDialog;
import com.jiuqi.expression.DataType;
import com.jiuqi.expression.ExpressionException;
import com.jiuqi.expression.base.DataContext;
import com.jiuqi.expression.data.AbstractData;
import com.jiuqi.expression.functions.Function;
import com.jiuqi.expression.nodes.NodeList;
import com.jiuqi.vacomm.model.common.billconst.BillFunctionConst;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.DialogInfo;
import com.jiuqi.mt2.dna.mobile.wxapproval.hint.FunctionMssageDialog;

public class WXConfirmDoFormula extends Function {

	public WXConfirmDoFormula() {
		super("WXConfirmDoFormula", "确认执行公式", BillFunctionConst.FunctionGroup.CommonFunction.getTitle());
		this.appendParameter("WXConfirmTitle", "提示标题", DataType.String);
		this.appendParameter("Msg", "提示信息", DataType.String);
		this.appendParameter("FormulaYes", "选择是的时候执行的公式名称", DataType.String);
		this.appendParameter("FormulaNo", "选择否的时候执行的公式名称", DataType.String);
		this.setDescription("函数示例：\n" +
				"WXConfirmDoFormula(\"WXConfirmTitle\",\"Msg\", \"FormulaYes\", \"FormulaNo\")\n"
				+ "函数说明：\n" +
				"同意提示信息时，执行标识为‘FormulaYes’的公式，不同意提示信息时，执行标识为‘FormulaNo’的公式\n" +
				"参数说明：\n" +
				"参数一：提示标题\n" +
				"参数二：提示信息\n" +
				"参数三：选择是的时候执行的按钮标识\n" +
				"参数四：选择否的时候执行的按钮标识\n" +
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
		ModelDataContext modelContext = (ModelDataContext) context;
		final BusinessModel model = modelContext.model;
		String ConfirmTitle = parameters.get(0).computeResult(context).getAsString();
		String Msg = parameters.get(1).computeResult(context).getAsString();
		final String FormulaYes = parameters.get(2).computeResult(context).getAsString();
		final String FormulaNo = parameters.get(3).computeResult(context).getAsString();
		
		//将信息传递出来
		if(FunctionMssageDialog.addConfirm(ConfirmTitle,Msg,"WXConfirmDoFormula",FormulaYes,FormulaNo,model,DialogInfo.DialogConfirmFormula)){
			return AbstractData.valueOf(0);
		}
		final MessageDialog dialog = MessageDialog.confirm(ConfirmTitle, Msg);
		dialog.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				if (dialog.getReturnCode() == 256) {
					model.executeFormula(FormulaYes);
				} else if (dialog.getReturnCode() == 512) {
					model.executeFormula(FormulaNo);
				}
			}
		});
		return AbstractData.valueOf(0);
	}
}
