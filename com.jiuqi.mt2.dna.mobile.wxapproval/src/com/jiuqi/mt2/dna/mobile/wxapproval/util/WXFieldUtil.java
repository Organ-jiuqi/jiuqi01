package com.jiuqi.mt2.dna.mobile.wxapproval.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jiuqi.mt2.spi.common2.impl.MInputDataField;
import com.jiuqi.mt2.spi.common2.metadata.MBaseData;
import com.jiuqi.mt2.spi.common2.table.IMField;
import com.jiuqi.mt2.spi.common2.table.IMFieldsCollection;
import com.jiuqi.xlib.utils.StringUtil;

public class WXFieldUtil {
	public static final SimpleDateFormat DDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DDF_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	/**
	 * 根据字段类型返回值，用来做界面显示
	 * @param fieldData
	 * @param constraints
	 * @return String
	 */
	public static final String getFieldValueByName(String fieldName, IMFieldsCollection fields, IMFieldsCollection constraints) {
		if (StringUtil.isEmpty(fieldName)) {
			return null;
		}
		IMField fieldData = fields.getField(fieldName);
		IMField constraint = null;
		if (constraints != null) {
			constraint = constraints.getField(fieldName);
		}
		if (constraint != null && constraint instanceof MInputDataField) {
			return getFieldValue(fieldData, (MInputDataField) constraint);
		} else {
			return getFieldValue(fieldData, null);
		}
	}

	/**
	 * 根据字段类型返回值，用来做界面显示
	 * 
	 * @param fieldData
	 * @return String
	 */
	public static final String getFieldValueByName(String fieldName, IMFieldsCollection fields) {
		return getFieldValueByName(fieldName, fields, null);
	}

	/**
	 * @param fieldData
	 * @param constraint
	 * @return
	 */
	public static String getFieldValue(IMField fieldData, MInputDataField constraint) {
		if (null == fieldData || null == fieldData.getValueType())
			return null;
		com.jiuqi.mt2.spi.common2.table.IMField.MDataType type = fieldData.getValueType();
		String valueStr = "";
		switch (type) {
		case INT:
			Integer temp = fieldData.getIntValue();
			valueStr = temp.toString();
			break;
		case NUMRIC:
			if (fieldData.getDoubleValue() == 0) {
				valueStr = "0";
			} else {
				if (constraint != null && constraint.getFieldConstraint() != null) {
					BigDecimal bd = new BigDecimal(fieldData.getDoubleValue());
					bd = bd.setScale(constraint.getFieldConstraint().getDecimal(), BigDecimal.ROUND_HALF_UP);
					StringBuffer sb = new StringBuffer("###,###");
					if (constraint.getFieldConstraint().getDecimal() > 0) {
						sb.append(".");
						for (int i = 0; i < constraint.getFieldConstraint().getDecimal(); i++) {
							sb.append("0");
						}
					}
					DecimalFormat nf = new DecimalFormat(sb.toString());
					return nf.format(bd);
				}
				BigDecimal bd = new BigDecimal(fieldData.getDoubleValue());
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				DecimalFormat nf = new DecimalFormat("###,###.##");
				return nf.format(bd);
			}
			break;
		case DATE:
			if (fieldData.getLongValue() == 0) {
				valueStr = "";
			} else {
				valueStr = DDF_DATE.format(new Date(fieldData.getLongValue()));
			}
			break;
		case STRING:
			valueStr = fieldData.getStringValue();
			break;
		case BOOLEAN:
			valueStr = fieldData.getBooleanValue() ? "是" : "否";
			break;
		case DATETIME:
			if (fieldData.getLongValue() == 0) {
				valueStr = "";
			} else {
				valueStr = DDF_DATE_TIME.format(new Date(fieldData.getLongValue()));
			}
			break;
		case BASEDATA_SINGLE:
		case BASEDATA_MULTI:
			if (fieldData.getObjectValue() == null) {
				return "";
			}
			MBaseData dataInBase = (MBaseData) fieldData.getObjectValue();
			StringBuffer valueBuilder = new StringBuffer();
			if(dataInBase.getTitle()!=null){
				for (int i = 0; i < dataInBase.getTitle().length; i++) {
					if (i == 0) {
						valueBuilder.append(dataInBase.getTitle()[i]);
					} else {
						valueBuilder.append("," + dataInBase.getTitle()[i]);
					}
				}
				valueStr = valueBuilder.toString();
				break;
			}else{
				
				
				
				
				valueStr="";
				break;
			}
			
		case UNDEFINE:
			break;
		default:
			break;
		}
		return valueStr;
	}
	
	public static String Field_Type_Int="int";
	public static String Field_Type_String="string";
	public static String Field_Type_Numric="numric";
	public static String Field_Type_Date="date";
	public static String Field_Type_Boolean="boolean";
	public static String Field_Type_DateTime="datetime";
	public static String Field_Type_Single="single";
	public static String Field_Type_Multi="multi";
	public static String Field_Type_Undefine="undefine";
	
	/**
	 * 获取单据字段类型
	 * @param fieldData
	 * @param constraint
	 * @return
	 */
	public static String getFieldValueType(String fieldName, IMFieldsCollection fields) {
		if (StringUtil.isEmpty(fieldName)) {
			return null;
		}
		IMField fieldData = fields.getField(fieldName);
	      	 
		if (null == fieldData || null == fieldData.getValueType())return null;
		com.jiuqi.mt2.spi.common2.table.IMField.MDataType type = fieldData.getValueType();
		String valueType = "";
		switch (type) {
		case INT:
			valueType=Field_Type_Int;
			break;
		case NUMRIC:
			valueType=Field_Type_Numric;
			break;
		case DATE:
			valueType=Field_Type_Date;
			break;
		case STRING:
			valueType = Field_Type_String;
			break;
		case BOOLEAN:
			valueType =Field_Type_Boolean;
			break;
		case DATETIME:
			valueType=Field_Type_DateTime;
			break;
		case BASEDATA_SINGLE:
			valueType=Field_Type_Single;			 
			break;
		case BASEDATA_MULTI:
			valueType=Field_Type_Multi;
			break;
		case UNDEFINE:
			valueType=Field_Type_Undefine;
			break;
		default:
			break;
		}
		return valueType;
	}
	
}
