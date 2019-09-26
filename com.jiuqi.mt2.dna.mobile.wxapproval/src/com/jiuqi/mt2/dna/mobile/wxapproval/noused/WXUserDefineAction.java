package com.jiuqi.mt2.dna.mobile.wxapproval.noused;

import com.jiuqi.dna.bap.common.constants.MsgDialogConst;
import com.jiuqi.dna.bap.common.util.ActionParameter;
import com.jiuqi.dna.bap.common.util.ActionParameter1;
import com.jiuqi.dna.bap.model.common.action.ActionConstant;
import com.jiuqi.dna.bap.model.common.define.base.BusinessObject;
import com.jiuqi.dna.bap.model.common.define.intf.IFormula;
import com.jiuqi.dna.bap.model.common.runtime.base.BusinessModel;
import com.jiuqi.dna.bap.model.common.type.ModelState;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.def.query.QuTableRefDeclare;
import com.jiuqi.dna.core.def.query.QueryStatementDeclare;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.text.StringHelper;

public class WXUserDefineAction {
	private ActionParameter actionArg = null;
	private boolean showConfirm = true;//�Ƿ���ʾȷ����ʾ
	private String alertMsg = "";//��ʾ��Ϣ
	private String[] tmpFormula = null;//��ʽ
	private boolean saveAfterexecute=false;//�����ִ��
	public boolean execute(BusinessModel model, String param){
		
		if(!checkRecerver(model)){
			model.messageDialog.alert(MsgDialogConst.MSG_TITLE_HINT(),"�����ѷ����仯������ʧ�ܣ���ˢ�º����ԣ�"); 
			return false;
		}
		
		if(param != null){
			//����һ�²�������
			ActionParameter1 param1 = new ActionParameter1(param);
			actionArg = new ActionParameter(param1.getOldParam());
			showConfirm = StringHelper.isBoolean(actionArg.get(ActionConstant.ShowConfirm));
			alertMsg = actionArg.get(ActionConstant.ShowInfo);
			String tmpFormulaStr = actionArg.get(ActionConstant.Formual);
			tmpFormula = tmpFormulaStr==null?new String[0]:actionArg.get(ActionConstant.Formual).split(",");
			saveAfterexecute = StringHelper.isBoolean(actionArg.get(ActionConstant.SaveAfterExecute));
		}else{
			saveAfterexecute = false;
		}
		//����ʾȷ����ʾ ֱ��ִ�й�ʽ ִ����ɺ󷵻�
		if(!showConfirm){
			executeFormula(model, tmpFormula,saveAfterexecute);
			return true;
		}
		//ִ�й�ʽȷ��
		Context context = model.getContext();
		//����confirm��ʾ
		return false;
    }
	
	/**
	 * ���汾���Ƿ������ݿ�һ��
	 * 
	 * @return boolean ���һ���򷵻�true
	 */
	private boolean checkRecerver(BusinessModel model) {
		if(model.getModelState()==ModelState.NEW){
			return true;
		}
		Context context = model.getContext();
		TableDefine masterDefine = model.getDefine().getMasterTable()
				.getTable();
		QueryStatementDeclare query = context.newQueryStatement();
		QuTableRefDeclare ref = query.newReference(masterDefine);
		query.newColumn(masterDefine.f_RECID());
		BusinessObject m_bo = model.getModelData().getMaster();
		query.setCondition(ref.expOf(masterDefine.f_RECID()).xEq(
				m_bo.getRECID()).and(
				ref.expOf(masterDefine.f_RECVER()).xEq(m_bo.getRECVER())));
		RecordSet rs = context.openQuery(query);
		return !rs.isEmpty();
	}
	
	
	/**
	 * ִ�й�ʽ
	 * 
	 * @param model void
	 */
	private void executeFormula(BusinessModel model, String[] formulaNames,boolean saveAfterexecute){
		model.beginUpdate();
		try {
			if (formulaNames == null || formulaNames.length < 1)
				return;
			for (String formulaName : formulaNames) {
				for (IFormula formula : model.getDefine().getFormulas()) {
					if (formulaName != null
							&& formulaName.equals(formula.getName())) {
						if (model.execute(formula))
							break;
						else
							return;
					}
				}
			}
			ModelState modelState = model.getModelState();
			if (saveAfterexecute) {
				if (!(modelState.equals(ModelState.NEW) || modelState
						.equals(ModelState.EDIT))) {
					model.edit();
				}
				model.save();
			}
		} finally {
			model.endUpdate();
		}
	}

}
