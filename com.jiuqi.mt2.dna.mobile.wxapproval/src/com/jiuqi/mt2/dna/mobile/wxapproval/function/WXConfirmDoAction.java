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

public class WXConfirmDoAction extends Function{

	public WXConfirmDoAction() {
		super("WXConfirmDoAction", "ȷ��ִ�ж���", BillFunctionConst.FunctionGroup.CommonFunction.getTitle());
		this.appendParameter("ConfirmTitle", "��ʾ����", DataType.String);
		this.appendParameter("Msg", "��ʾ��Ϣ", DataType.String);
		this.appendParameter("ActionYes", "ѡ���ǵ�ʱ��ִ�еİ�ť����", DataType.String);
		this.appendParameter("ActionNo", "ѡ����ʱ��ִ�еİ�ť����", DataType.String);
		this.setDescription("����ʾ����\n" +
				"WXConfirmDoAction(\"WXConfirmTitle\",\"Msg\", \"ActionYes\", \"ActionNo\")\n"
				+ "����˵����\n" +
				"ͬ����ʾ��Ϣʱ��ִ�а�ť��ʶΪ��ActionYes���İ�ť��������ͬ����ʾ��Ϣʱ��ִ�а�ť��ʶΪ��ActionNo���İ�ť����,ִ�еĶ�������Ĭ��Ϊ�ա�\n" +
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
		final String ActionYes = parameters.get(2).computeResult(context).getAsString();
		final String ActionNo = parameters.get(3).computeResult(context).getAsString();
		
		//����Ϣ���ݳ���
		boolean flog=FunctionMssageDialog.addConfirm(ConfirmTitle,Msg,"WXConfirmDoAction",ActionYes,ActionNo,model,DialogInfo.DialogConfirmAction);
		if(flog){
			return AbstractData.valueOf(0);
		}
		final MessageDialog dialog = MessageDialog.confirm(ConfirmTitle, Msg);
		dialog.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				if (dialog.getReturnCode() == 256) {
					model.executeAction(ActionYes, "");
				} else if (dialog.getReturnCode() == 512) {
					model.executeAction(ActionNo, "");
				}
			}
		});
		return AbstractData.valueOf(0);
	}

}
