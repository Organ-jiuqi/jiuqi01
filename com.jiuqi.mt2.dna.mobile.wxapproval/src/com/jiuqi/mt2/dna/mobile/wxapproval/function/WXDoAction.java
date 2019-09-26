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
		super("WXDoAction", "ִ��ָ������", BillFunctionConst.FunctionGroup.CommonFunction.getTitle());
		this.appendParameter("ActionName", "��ʽ��ʾ", DataType.String);
		this.appendParameter("param", "��ʽ����(�Ǳ���)", DataType.String,true, false, null);
	    this.setDescription("����ʾ����\n" +
				"WXDoAction(\"ActionName\",\"param\")\n"
				+ "����˵����\n" +
				"ִ�ж�����ʶΪ��ActionName���Ķ�����ť\n" +
				"����˵����\n" +
				"����һ��������ʶ,String����\n"+
				"����������������,String����(�Ǳ���)\n"+
				"����ֵ˵����Int����");
   }

	/**
	 * ����ֵ����
	 * @param parameters ����
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
