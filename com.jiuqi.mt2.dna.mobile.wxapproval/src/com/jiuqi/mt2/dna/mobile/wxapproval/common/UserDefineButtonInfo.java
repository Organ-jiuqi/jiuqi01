package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import javax.servlet.http.HttpServletResponse;

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
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.HintMessage;
import com.jiuqi.mt2.dna.mobile.wxapproval.mfo.SendMsgToWX;
import com.jiuqi.text.StringHelper;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * Button按钮中属性Param的解析
 * @author liuzihao
 */
public class UserDefineButtonInfo {
	private boolean state=false;//是否有意义 true有意义 false无效
	private boolean showConfirm=true;//是否显示确认提示  true显示 false不提示
	private String showInfo="";//提示内容
	private String[] formuals;//执行函数
	private boolean saveAfterExecute;//执行后保存数据
	
	
	
	public UserDefineButtonInfo(BusinessModel model,String param, HttpServletResponse resp){
		if(!checkRecerver(model)){
			SendMsgToWX.sendHint(resp, HintMessage.toError("数据已发生变化，操作失败，请刷新后重试！"));
			return;
		}
		if(StringUtil.isNotEmpty(param)){
			this.state=true;
			ActionParameter actionArg = new ActionParameter(new ActionParameter1(param).getOldParam());
			this.showConfirm = StringHelper.isBoolean(actionArg.get(ActionConstant.ShowConfirm));
			this.showInfo = actionArg.get(ActionConstant.ShowInfo);
			this.formuals = (actionArg.get(ActionConstant.Formual)==null)?(new String[0]):(actionArg.get(ActionConstant.Formual).split(","));
			this.saveAfterExecute = StringHelper.isBoolean(actionArg.get(ActionConstant.SaveAfterExecute));
		}else{
			this.state=false;
			this.saveAfterExecute = false;
		}
	}
	
	/**
	 * 执行公式
	 * @param model void
	 */
	public void executeFormula(BusinessModel model, String[] formulaNames,boolean saveAfterexecute){
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
				if (!(modelState.equals(ModelState.NEW) || modelState.equals(ModelState.EDIT))) {
					model.edit();
				}
				model.save();
			}
		} finally {
			model.endUpdate();
		}
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

	public boolean isState() {
		return state;
	}

	public boolean isConfirm() {
		return showConfirm;
	}

	public String getShowInfo() {
		return showInfo;
	}

	public String[] getFormuals() {
		return formuals;
	}

	public boolean isSaveAfterExecute() {
		return saveAfterExecute;
	}
	
}
