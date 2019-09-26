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
	private boolean showConfirm = true;//是否显示确认提示
	private String alertMsg = "";//提示信息
	private String[] tmpFormula = null;//公式
	private boolean saveAfterexecute=false;//保存后执行
	public boolean execute(BusinessModel model, String param){
		
		if(!checkRecerver(model)){
			model.messageDialog.alert(MsgDialogConst.MSG_TITLE_HINT(),"数据已发生变化，操作失败，请刷新后重试！"); 
			return false;
		}
		
		if(param != null){
			//做了一下参数兼容
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
		//不显示确认提示 直接执行公式 执行完成后返回
		if(!showConfirm){
			executeFormula(model, tmpFormula,saveAfterexecute);
			return true;
		}
		//执行公式确认
		Context context = model.getContext();
		//弹出confirm提示
		return false;
    }
	
	/**
	 * 检查版本号是否与数据库一致
	 * 
	 * @return boolean 如果一致则返回true
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
	 * 执行公式
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
