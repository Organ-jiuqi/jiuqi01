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
 * Button��ť������Param�Ľ���
 * @author liuzihao
 */
public class UserDefineButtonInfo {
	private boolean state=false;//�Ƿ������� true������ false��Ч
	private boolean showConfirm=true;//�Ƿ���ʾȷ����ʾ  true��ʾ false����ʾ
	private String showInfo="";//��ʾ����
	private String[] formuals;//ִ�к���
	private boolean saveAfterExecute;//ִ�к󱣴�����
	
	
	
	public UserDefineButtonInfo(BusinessModel model,String param, HttpServletResponse resp){
		if(!checkRecerver(model)){
			SendMsgToWX.sendHint(resp, HintMessage.toError("�����ѷ����仯������ʧ�ܣ���ˢ�º����ԣ�"));
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
	 * ִ�й�ʽ
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
