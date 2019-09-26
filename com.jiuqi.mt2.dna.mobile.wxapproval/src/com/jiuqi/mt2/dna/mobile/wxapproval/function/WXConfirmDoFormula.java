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
		super("WXConfirmDoFormula", "ȷ��ִ�й�ʽ", BillFunctionConst.FunctionGroup.CommonFunction.getTitle());
		this.appendParameter("WXConfirmTitle", "��ʾ����", DataType.String);
		this.appendParameter("Msg", "��ʾ��Ϣ", DataType.String);
		this.appendParameter("FormulaYes", "ѡ���ǵ�ʱ��ִ�еĹ�ʽ����", DataType.String);
		this.appendParameter("FormulaNo", "ѡ����ʱ��ִ�еĹ�ʽ����", DataType.String);
		this.setDescription("����ʾ����\n" +
				"WXConfirmDoFormula(\"WXConfirmTitle\",\"Msg\", \"FormulaYes\", \"FormulaNo\")\n"
				+ "����˵����\n" +
				"ͬ����ʾ��Ϣʱ��ִ�б�ʶΪ��FormulaYes���Ĺ�ʽ����ͬ����ʾ��Ϣʱ��ִ�б�ʶΪ��FormulaNo���Ĺ�ʽ\n" +
				"����˵����\n" +
				"����һ����ʾ����\n" +
				"����������ʾ��Ϣ\n" +
				"��������ѡ���ǵ�ʱ��ִ�еİ�ť��ʶ\n" +
				"�����ģ�ѡ����ʱ��ִ�еİ�ť��ʶ\n" +
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
		ModelDataContext modelContext = (ModelDataContext) context;
		final BusinessModel model = modelContext.model;
		String ConfirmTitle = parameters.get(0).computeResult(context).getAsString();
		String Msg = parameters.get(1).computeResult(context).getAsString();
		final String FormulaYes = parameters.get(2).computeResult(context).getAsString();
		final String FormulaNo = parameters.get(3).computeResult(context).getAsString();
		
		//����Ϣ���ݳ���
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
