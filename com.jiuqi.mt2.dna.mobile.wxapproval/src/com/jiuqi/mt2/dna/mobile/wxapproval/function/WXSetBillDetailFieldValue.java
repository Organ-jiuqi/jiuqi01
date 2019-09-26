package com.jiuqi.mt2.dna.mobile.wxapproval.function;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jiuqi.dna.bap.bill.common.model.BillCentre;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.bill.intf.facade.model.FBillDefine;
import com.jiuqi.dna.bap.model.common.define.base.BusinessObject;
import com.jiuqi.dna.bap.model.common.define.intf.IField;
import com.jiuqi.dna.bap.model.common.define.intf.ITable;
import com.jiuqi.dna.bap.model.common.expression.ModelDataContext;
import com.jiuqi.dna.bap.model.common.expression.ModelDataNode;
import com.jiuqi.dna.bap.model.common.type.FieldType;
import com.jiuqi.dna.bap.model.common.type.ModelState;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.expression.DataType;
import com.jiuqi.expression.ExpressionException;
import com.jiuqi.expression.base.DataContext;
import com.jiuqi.expression.data.AbstractData;
import com.jiuqi.expression.functions.Function;
import com.jiuqi.expression.nodes.BracketNode;
import com.jiuqi.expression.nodes.CommentNode;
import com.jiuqi.expression.nodes.Expression;
import com.jiuqi.expression.nodes.FunctionNode;
import com.jiuqi.expression.nodes.IfThenElseNode;
import com.jiuqi.expression.nodes.Node;
import com.jiuqi.expression.nodes.NodeList;
import com.jiuqi.expression.nodes.OperatorNode;
import com.jiuqi.expression.token.Token;
import com.jiuqi.vacomm.model.common.billconst.BillFunctionConst;

/**
 * @author liuzihao
 * @deprecated
 */
public class WXSetBillDetailFieldValue extends Function{
	public WXSetBillDetailFieldValue() {
		super("WXSetBillDetailFieldValue", "��������ģ���ӱ��ֶ�ֵ", BillFunctionConst.FunctionGroup.ModelFunction.getTitle());
		appendParameter("billDefine", "Ҫ�����ĵ��ݵĵ��ݶ���", DataType.String);
		appendParameter("recid", "Ҫ�����ĵ��ݵ�����recid", DataType.String);
		appendParameter("fieldName", "�ֶ���", DataType.Void);
		appendParameter("value", "����ֵ", DataType.Void);
		appendParameter("filterCondition", "��������", DataType.String);
		StringBuffer description = new StringBuffer();
		description.append("����ʾ����WXSetBillDetailFieldValue(\"BA192AEBB2CCDB32740B95688D713AD8\",\"15492AEBB2CCDB32740B95688D713AD8\",");
		description.append("Table[Field],Table[Field1]*Table[Field2]/Table[Field3],\"field1=value1;field2=value2...\")��\n");
		description.append("�������壺��������ģ���ӱ��ֶ�ֵ��������ݲ���һָ���ĵ��ݶ���û���ӱ��򵯳���ʾ��Ҫ�����ĵ���û���ӱ����ݡ�");
		description.append("������ݲ���һָ���ĵ��ݶ�����ӱ�û�в�����ָ���ı����򵯳���ʾ��Ҫ�����ĵ����в���������������������");
		description.append("���������ָ�������������ָ��������ͬ�򵯳���ʾ����ʽ������ֵ�Ĳ����б�����Ҫ������ģ���ӱ�����һ�¡�\n");
		description.append("����˵����\n");
		description.append("����һ��Ҫ�����ĵ��ݵĵ��ݶ��壻\n");
		description.append("��������Ҫ�����ĵ��ݵ�����recid��\n");
		description.append("���������ֶ�����\n");
		description.append("�����ģ�����ֵ��\n");
		description.append("�����壺����������\n");
		description.append("����ֵ˵��������boolean���͡�\n");
		this.setDescription(description.toString());
	}

	@Override
	public int judgeResultType(NodeList parameters) {
		return DataType.Bool;
	}

	@Override
	public AbstractData callFunction(DataContext context, NodeList parameters)
			throws ExpressionException {
		ModelDataContext modelContext = (ModelDataContext) context;
		BillModel tempModel = (BillModel) modelContext.model;
		GUID billDefine = GUID.valueOf(parameters.get(0).computeResult(context)
				.getAsString());
		FBillDefine define = BillCentre.findBillDefine(tempModel.getContext(),
				billDefine);
		BillModel model = BillCentre.createBillModel(tempModel
				.getContext(), define);
		GUID recid = GUID.valueOf(parameters.get(1).computeResult(context).getAsString());
		model.load(recid);
		if(model.getModelData().getDetailsList() == null){
			model.messageDialog.alert("Ҫ�����ĵ���û���ӱ�����");
			return null;
		}
		ModelDataNode modelNode = (ModelDataNode) parameters.get(2);
		ITable table = modelNode.getTable();
		IField field = modelNode.getField();
		int tableNo = 0;
		int recordNo = 0;
		for(int i = 0; i < model.getDefine().getDetailTables().size(); i ++){
			ITable deTable = model.getDefine().getDetailTables().get(i);
			if(table.getName().equals(deTable.getName())){
				tableNo = i;
				break;
			}
			if(i == model.getDefine().getDetailTables().size() - 1){
				model.messageDialog.alert("Ҫ�����ĵ����в�������"+table.getName()+"��");
				return null;
			}
		}
		Map<String, String> fieldValue = new HashMap<String, String>();
		parseConditions(fieldValue, parameters.get(4).computeResult(context).getAsString());
		BusinessObject tempBo;
		boolean existRecord;
		for(int i = 0; i < model.getModelData().getDetailsList().get(tableNo).size(); i ++){
			tempBo = model.getModelData().getDetailsList().get(tableNo).get(i);
			existRecord = true;
			for(Entry<String, String> entry : fieldValue.entrySet()){
				FieldType fieldType = tempBo.getFieldType(entry.getKey());
				Object object = tempBo.getFieldValue(entry.getKey());
				if(fieldType == FieldType.GUID){
					if(!String.valueOf(object).equals(entry.getValue())){
						existRecord = false;
						break;
					}
				}else if(fieldType == FieldType.NUMERIC){
					if(Convert.toDouble(object) != Double.valueOf(entry.getValue())){
						existRecord = false;
						break;
					}
				}else if(fieldType == FieldType.INT){
					if(Convert.toInt(object) != Integer.valueOf(entry.getValue())){
						existRecord = false;
						break;
					}
				}else if(fieldType == FieldType.BOOLEAN){
					if(Convert.toBoolean(object) != Boolean.valueOf(entry.getValue())){
						existRecord = false;
						break;
					}
				}else if(fieldType == FieldType.DATE){
					if(Convert.toDate(object) != Date.valueOf(entry.getValue()).getTime()){
						existRecord = false;
						break;
					}
				}else {
					if(!String.valueOf(object).equals(entry.getValue())){
						existRecord = false;
						break;
					}
				}
			}
			if(existRecord){
				recordNo = i;
				break;
			}
		}
		
		Node node = parameters.get(3); 
		BusinessObject bo = model.getModelData().getDetailsList().get(tableNo).get(recordNo);
		Node cloneNode = cloneNode(node, bo, table.getName());
		if(cloneNode == null){
			model.messageDialog.alert("��ʽ������ֵ�Ĳ����б�����Ҫ������ģ���ӱ�����һ��");
			return null;
		}
		model.setModelState(ModelState.EDIT);
		model.getModelData().getDetailsList().get(tableNo).get(recordNo).setFieldValue(field.getName(), cloneNode.computeResult(context).getAsObject());
		model.save();
		return null;
	}
	
	private Node cloneNode(Node node, BusinessObject bo, String table) {
		if (node.getChildCount() == 0) {
			if (node instanceof ModelDataNode) {
				ModelDataNode modelNode = (ModelDataNode)node;
				if(modelNode.getTable().getName().equalsIgnoreCase(table)){
					Object object = bo.getFieldValue(modelNode.getField().getName());
					FieldType type = bo.getFieldType(modelNode.getField().getName());
					return new ValueNode(node.getToken(), object, type);
				}else{
					return null;
				}
			} else {
				return node;
			}
		} else {
			Node[] children = new Node[node.getChildCount()];
			for (int i = 0; i < node.getChildCount(); i++) {
				children[i] = cloneNode(node.getChild(i), bo, table);
			}
			boolean childCloned = false;
			for (int i = 0; i < node.getChildCount(); i++) {
				if (children[i] != node.getChild(i)) {
					childCloned = true;
					break;
				}
			}
			if (childCloned) {
				if (node instanceof BracketNode) {
					if (children.length != 1) {
						throw new RuntimeException("BracketNode��¡����");
					}
					return new BracketNode(node.getToken(), children[0]);
				} else if(node instanceof CommentNode){
					return new CommentNode(node.getToken());
				} else if(node instanceof OperatorNode){
					if(children.length != 2){
						throw new RuntimeException("OperatorNode��¡����");
					}
					OperatorNode operNode = new OperatorNode(node.getToken(), ((OperatorNode) node).getOperator());
					operNode.setOperand(children[0], children[1]);
					return operNode;
				} else if(node instanceof Expression){
					if(children.length != 1){
						throw new RuntimeException("Expression��¡����");
					}
					return new Expression(children[0]);
				}else if(node instanceof IfThenElseNode){
					IfThenElseNode ifthenelseNode = (IfThenElseNode)node;
					if(children.length == 2){
						try {
							ifthenelseNode.setIfThenElse(children[0], children[1], null);
						} catch (ExpressionException e) {
							e.printStackTrace();
						}
					}else if(children.length == 3){
						try {
							ifthenelseNode.setIfThenElse(children[0], children[1], children[2]);
						} catch (ExpressionException e) {
							e.printStackTrace();
						}
					}else{
						throw new RuntimeException("Expression��¡����");
					}
					return ifthenelseNode;
				}else if(node instanceof FunctionNode){
					NodeList nodeList = new NodeList();
					for(int i = 0; i < node.getChildCount(); i ++ ){
						nodeList.append(node.getChild(i));
					}
					FunctionNode functionNode = (FunctionNode)node;
					functionNode.setParameters(nodeList);
					return functionNode;
				}
				else {
					throw new RuntimeException("�����͵�nodeδ����");
				}
			} else {
				return node;
			}
		}
	}
	
	private void parseConditions(Map<String, String> map, String conditions){
		String[] condition = conditions.split(";");
		for(int i = 0; i < condition.length; i ++){
			String[] fieldValue = condition[i].split("=");
			map.put(fieldValue[0], fieldValue[1]);
		}
	}
}

class ValueNode extends Node {

	private Object value;
	private FieldType type;
	
	public ValueNode(Token token, Object value, FieldType type) {
		super(token);
		this.value = value;
		this.type = type;
	}

	@Override
	public AbstractData computeResult(DataContext context)
			throws ExpressionException {
		return ModelDataNode.valueOf(value, type);
	}
}
