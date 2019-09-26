package com.jiuqi.mt2.dna.mobile.wxapproval.function;

import java.util.List;

import com.jiuqi.dna.bap.basedata.common.util.BaseDataCenter;
import com.jiuqi.dna.bap.basedata.intf.facade.BaseDataObject;
import com.jiuqi.dna.bap.masterdata.define.intf.facade.MasterDataDefine;
import com.jiuqi.dna.bap.model.common.define.base.BusinessObject;
import com.jiuqi.dna.bap.model.common.define.intf.IField;
import com.jiuqi.dna.bap.model.common.define.intf.ITable;
import com.jiuqi.dna.bap.model.common.expression.ModelDataContext;
import com.jiuqi.dna.bap.model.common.expression.ModelDataNode;
import com.jiuqi.dna.bap.model.common.runtime.base.BusinessModel;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.da.DBCommand;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.ui.wt.InfomationException;
import com.jiuqi.expression.DataType;
import com.jiuqi.expression.ExpressionException;
import com.jiuqi.expression.base.DataContext;
import com.jiuqi.expression.data.AbstractData;
import com.jiuqi.expression.functions.Function;
import com.jiuqi.expression.nodes.NodeList;
import com.jiuqi.vacomm.model.common.billconst.BillFunctionConst;

public class WXSetOtherTableFieldValue extends Function {

	public WXSetOtherTableFieldValue() {
		super("WXSetOtherTableFieldValue", "把当前单据的值回写到目标表的目标字段", BillFunctionConst.FunctionGroup.CommonFunction.getTitle(), "", "");
	    appendParameter("aimtable[aimfield]", "回写表名[回写字段名]", DataType.Void);
	    appendParameter("value", "写入参数值", DataType.Void);
	    appendParameter("sourcetable[connectid;sourceField]", "数据来源表[关联字段]", DataType.String);
	    appendParameter("fieldtype", "写入参数类型", DataType.String);
	    appendParameter("\"table1[infield1;outfield1],table2[infield2;outfield2],···\"", "中间关联的表及字段，注意顺序", DataType.String);
//	    StringBuffer description = new StringBuffer();
//	    description.append("函数示例：WXSetOtherTableFieldValue(MD_COSTOBJECT[XSFZR],");
//	    description.append(" \"XSLZRYJJXM[XMLX;LZJJR]\", \"guid\",");
//	    description.append(" \"MD_COSTOBJECT[JBXX_XIAOSLXBH;RECID]\" ) \n");
//	    description.append("函数意义：把当前单据的值回写到目标表的目标字段。\n");
//	    description.append("参数说明：\n");
//	    description.append("参数一：回写表名[回写字段名]，例：回写到MD_COSTOBJECT表的XSFZR字段 \n");
//	    description.append("参数二：数据来源表[关联字段，数据来源字段]，例：把XSLZRYJJXM表的LZJJR回写过去，关联字段为XMLX\n");
//	    description.append("参数三：回写的值的类型，仅支持boolean、guid、string、int、date、double，注意小写 \n");
//	    description.append("参数四：中间关联的表及字段，in为输入字段，out为输出字段，例：XSLZRYJJXM表的XMLX字段关联MD_COSTOBJECT表的JBXX_XIAOSLXBH，");
//	    description.append("得到MD_COSTOBJECT表的RECID字段输出作为下次关联的输入字段");
//	    description.append("这张表的RECID关联下一张表的关联字段，如果没有则对应到要回写到的表MD_COSTOBJECT \n");
//	    description.append("参数四也可这样写，如WXSetOtherTableFieldValue(MD_CONTRACT[GENZR],\"XSRYLZJJHT[XSHT;LZJJR]\",\"guid\") ");
//	    description.append("关联字段直接为要回写表的recid \n");
//	    description.append("举例：多次关联 WXSetOtherTableFieldValue(XS_SQZCSDQ[XS_XSRY],\"XSRYLZJJHT[XSHT;LZJJR]\",\"guid\",");
//	    description.append("\"MD_CONTRACT[RECID;XIAOSLX],XS_SQZCSDQ[XS_XMMC;RECID]\") \n");
//	    description.append("返回值说明：返回boolean类型。\n");
//	    setDescription(description.toString());
	}

	@Override
	public int judgeResultType(NodeList parameters) {
		if (parameters.size() == 5) {
			return DataType.Bool;
		}
		return DataType.Error;
	}

	@Override
	public AbstractData callFunction(DataContext context, NodeList parameters)
			throws ExpressionException {
		ModelDataContext modelContext = (ModelDataContext) context;
		BusinessModel model = modelContext.model;
		Context cxt = model.getContext();
		// 目标表和目标字段
		ModelDataNode node0 = (ModelDataNode) parameters.get(0);
		ITable aimTable = node0.getTable();
		IField aimField = node0.getField();
		// 目标表的主键列
		Object fieldValue = parameters.get(1).computeResult(context).getAsObject();
		String connect = parameters.get(2).computeResult(context).getAsString();
		String sourceTableName = connect.substring(0, connect.indexOf("["));
		TableDefine sourceTableDefine = cxt.find(TableDefine.class, sourceTableName);
		if (sourceTableDefine == null) {
			throw new InfomationException("未找到标识为" + sourceTableName
					+ "表，请检查单据公式SetOtherTableFieldValue的配置。");
		}
		String connectField;
		String sourceField = null;
		if(connect.contains(";")){
			connectField = connect.substring(connect.indexOf("[") + 1, connect.indexOf(";"));
			sourceField = connect.substring(connect.indexOf(";") + 1, connect.indexOf("]"));
		}else{
			connectField = connect.substring(connect.indexOf("[") + 1, connect.indexOf("]"));
		}
		TableFieldDefine connectFieldDefine = sourceTableDefine.getFields().get(connectField.trim());
		if (connectFieldDefine == null) {
			throw new InfomationException("表" + sourceTableName + "中未找到标识为"
					+ connectField + "的字段，请检查单据公式SetOtherTableFieldValue的配置。");
		}
		String fieldType = parameters.get(3).computeResult(context).getAsString();
		if (!fieldType.equals("guid") && !fieldType.equals("date")
				&& !fieldType.equals("int") && !fieldType.equals("double")
				&& !fieldType.equals("string") && !fieldType.equals("boolean")) {
			throw new InfomationException("回写的值的类型" + fieldType
					+ "未识别，请检查单据公式SetOtherTableFieldValue的配置。");
		}
		String valueToRecid = parameters.get(4).computeResult(context).getAsString();
		String billMasterTable = model.getModelData().getMaster().getTable()
				.getName();
		if (billMasterTable.equals(sourceTableName)) {
			GUID recid = (GUID) model.getModelData().getMaster().getFieldValue(connectField);
			recid = valueToRecid.trim().length() > 0 ? getLastRecid(cxt, recid, valueToRecid) : recid;
			if(connect.contains(";")){
				fieldValue = model.getModelData().getMaster().getFieldValue(sourceField);
			}
			if(recid != null){
				updateAimField(cxt, aimTable, aimField, recid, fieldValue,fieldType);
				//修改缓存中的数据
				updateBaseData(cxt, aimTable.getName(), recid, aimField.getName(), fieldValue);
			}
		} else {
			List<BusinessObject> detail = model.getModelData().getDetail(model.getDefine(), sourceTableName);
			if (detail == null) {
				throw new InfomationException("当前单据不包含" + sourceTableName
						+ "表，无法完成SetOtherTableFieldValue的执行，请检查公式配置。");
			}
			for (int i = 0; i < detail.size(); i++) {
				BusinessObject record = detail.get(i);
				GUID recid = record.getValueAsGUID(connectField);
				recid = valueToRecid.trim().length() > 0 ? getLastRecid(cxt, recid, valueToRecid) : recid;
				if(connect.contains(";")){
					fieldValue = record.getFieldValue(sourceField);
				}
				updateAimField(cxt, aimTable, aimField, recid, fieldValue, fieldType);
				//修改缓存中的数据
				updateBaseData(cxt, aimTable.getName(), recid, aimField.getName(), fieldValue);
			}
		}
		return AbstractData.valueOf(true);
	}

	private void updateBaseData(Context context, String tableName, GUID objectID, String aimField, Object fieldValue) {
		MasterDataDefine findMasterDataDefine = BaseDataCenter.findMasterDataDefine(context, tableName);
		if(findMasterDataDefine != null){
			BaseDataObject obj = (BaseDataObject) BaseDataCenter.findObjectbyObjectID(context, tableName, objectID);
			if(obj != null){
				obj.setFieldValue(aimField, fieldValue);
			}			
		}
	}

	private GUID getLastRecid(Context context, GUID recid, String valueToRecid) {
		String[] split = valueToRecid.split(",");
		int length = split.length - 1;
		if(split[length].trim().length() == 0){
			throw new InfomationException("SetOtherTableFieldValue公式参数四配置错误，请检查公式配置。");
		}
		StringBuffer getLastRecidSql = new StringBuffer();
		getLastRecidSql.append("define query getLastRecid() \n");
		getLastRecidSql.append("begin \n");
		//获得待会写表的标识
		String outRecid = split[length].substring(split[length].indexOf(";") + 1, split[length].indexOf("]"));
		getLastRecidSql.append("  select t" + length + "." + outRecid + " as lastrecid \n");
		getLastRecidSql.append("    from ");
		for (int i = 0; i < split.length; i++) {
			String tableName = split[i].substring(0, split[i].indexOf("["));
			TableDefine tableDefine = context.find(TableDefine.class, tableName);
			if (tableDefine == null) {
				throw new InfomationException("SetOtherTableFieldValue公式未找到参数四中" + tableName + "表，请检查该表是否存在。");
			}
			
			String inFieldName = split[i].substring(split[i].indexOf("[") + 1, split[i].indexOf(";"));
			TableFieldDefine inFieldDefine = tableDefine.getFields().get(inFieldName.trim());
			if (inFieldDefine == null) {
				throw new InfomationException("表" + tableName + "中未找到标识为"
						+ inFieldName + "的字段，请检查单据公式SetOtherTableFieldValue参数四的配置。");
			}
			
			String outFieldName = split[i].substring(split[i].indexOf(";") + 1, split[i].indexOf("]"));
			TableFieldDefine outFieldDefine = tableDefine.getFields().get(outFieldName.trim());
			if (outFieldDefine == null) {
				throw new InfomationException("表" + tableName + "中未找到标识为"
						+ outFieldName + "的字段，请检查单据公式SetOtherTableFieldValue参数四的配置。");
			}
			
			if(i == 0){
				getLastRecidSql.append(tableName).append(" as t" + i + " \n");
			}else{
				String connectField = split[i-1].substring(split[i-1].indexOf(";") + 1, split[i-1].indexOf("]"));
				getLastRecidSql.append(" join " + tableName).append(" as t" + i);
				getLastRecidSql.append("   on t" + i + "." + inFieldName).append(" = t" + (i - 1) + "."+ connectField +"\n" );
			}
		}
		String inFieldName = split[0].substring(split[0].indexOf("[") + 1, split[0].indexOf(";"));
		getLastRecidSql.append("  where t0." + inFieldName + " = guid'" + recid + "' \n");
		getLastRecidSql.append("end \n");
		DBCommand dbCommand = null;
		GUID guid = null;
		try {
			dbCommand = context.prepareStatement(getLastRecidSql);
			RecordSet rs = dbCommand.executeQuery();
			while (rs.next()) {
				guid = rs.getFields().get(0).getGUID();
			}
		}finally{
			dbCommand.unuse();
		}
		return guid;
	}

	private void updateAimField(Context context, ITable aimTable,
			IField aimField, GUID recid, Object fieldValue, String fieldType) {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("define update updateAimField(@fieldValue "
				+ fieldType + ", @recid guid) \n");
		updateSql.append("begin \n");
		updateSql.append("  update " + aimTable.getName() + " as t \n");
		updateSql.append("    set " + aimField.getName() + " = @fieldValue \n");
		updateSql.append("  where t.recid = @recid \n");
		updateSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(updateSql);
		dbCommand.setArgumentValues(fieldValue, recid);
		
		dbCommand.executeUpdate();
		dbCommand.unuse();
	}
}
