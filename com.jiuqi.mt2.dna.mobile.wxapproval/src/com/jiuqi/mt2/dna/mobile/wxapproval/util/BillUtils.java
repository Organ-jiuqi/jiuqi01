package com.jiuqi.mt2.dna.mobile.wxapproval.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.jiuqi.dna.bap.basedata.common.util.BaseDataCenter;
import com.jiuqi.dna.bap.basedata.intf.facade.FBaseDataObject;
import com.jiuqi.dna.bap.bill.common.model.BillModel;
import com.jiuqi.dna.bap.model.common.define.base.BusinessObject;
import com.jiuqi.dna.bap.model.common.define.intf.IField;
import com.jiuqi.dna.bap.model.common.runtime.base.ModelData;
import com.jiuqi.dna.bap.model.common.type.FieldType;
import com.jiuqi.dna.bap.model.common.util.BDMultiSelectUtil;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.mt2.dna.service.bill.BillTemplateProviderAdptor;
import com.jiuqi.mt2.spi.bill.model.BillData;
import com.jiuqi.mt2.spi.bill.model.Constant;
import com.jiuqi.mt2.spi.bill.model.DetailData;
import com.jiuqi.mt2.spi.bill.model.MBillInputField;
import com.jiuqi.mt2.spi.bill.template.BillTemplate;
import com.jiuqi.mt2.spi.bill.template.MobileTable;
import com.jiuqi.mt2.spi.common2.impl.MInputDataField;
import com.jiuqi.mt2.spi.common2.metadata.MBaseData;
import com.jiuqi.mt2.spi.common2.table.IMField;
import com.jiuqi.mt2.spi.common2.table.IMField.MDataType;
import com.jiuqi.mt2.spi.common2.table.IMFieldsCollection;
import com.jiuqi.mt2.spi.common2.table.impl.MField;
import com.jiuqi.mt2.spi.common2.table.impl.MFieldsCollection;
import com.jiuqi.mt2.spi.common2.table.impl.MFieldsCollectionList;
import com.jiuqi.mt2.spi.log.MobileLog;
import com.jiuqi.xlib.json.JSONObject;
import com.jiuqi.xlib.utils.StringUtil;

public class BillUtils {

	/**
	 * 从BAP单据数据模型中同步数据到移动单据数据
	 * 
	 * @param billData
	 * @param mBillDefine
	 * @param modelData
	 */
	public static void loadBillData(Context context, BillData billData,
			BillModel billModel) {

		ModelData modelData = billModel.getModelData();
		BillTemplate billTemplate = BillTemplateProviderAdptor
				.convert2BillTemplate(context, billModel.getDefine(), null);
		// 首先给子表新增一默认值行
		for (MobileTable detailPage : billTemplate.getDetailTables()) {
			String detailTabName = detailPage.getName();
			billModel.insertRow(detailTabName, 1);
		}
		// 设置主表数据
		MFieldsCollection masterData = billData.getMasterData();
		// 构造主表行
		// masterData.setRowId(modelData.getMaster().getRECID().toString());
		// 主表增加行标记
		addID(masterData, modelData.getMaster().getRECID());
		
		initFieldValue(context, masterData, modelData.getMaster(), billTemplate
				.getMasterTable().getFields(),billData.getEditableFieldNames());
		List<DetailData> detailDatas = new ArrayList<DetailData>();
		// 设置子表数据
		for (MobileTable detailPage : billTemplate.getDetailTables()) {

			String detailTabName = detailPage.getName();
			// 子表数据
			List<BusinessObject> detailDataes = getDetailDataes(detailTabName,
					modelData);
			// 子表字段
			IMFieldsCollection mobileFields = detailPage.getFields();
			// 构造子表
			DetailData detailData = new DetailData(detailTabName);
			MFieldsCollectionList detailDataFields = detailData.getRowDataes();
			for (int i = 0, s = detailDataes.size(); i < s; i++) {
				BusinessObject bo = detailDataes.get(i);
				// 是否到了最后一行（默认值行）
				if (i == s - 1) {
					MFieldsCollection newRowFieldsCollection = new MFieldsCollection() {

						public void deserialize(JSONObject json) {
							deserialize(json, MBillInputField.class);
						}
					};
					addID(newRowFieldsCollection, GUID.randomID());
					initFieldValue(context, newRowFieldsCollection, bo,
							mobileFields,detailData.getEditableFieldNames());
					detailData.setDefaultValue(newRowFieldsCollection);
					break;
				}
				MFieldsCollection newRowFieldsCollection = new MFieldsCollection() {

					public void deserialize(JSONObject json) {
						deserialize(json, MBillInputField.class);
					}
				};
				addID(newRowFieldsCollection, bo.getRECID().toString());
				initFieldValue(context, newRowFieldsCollection, bo,
						mobileFields,detailData.getEditableFieldNames());
				detailDataFields.addFieldCollection(newRowFieldsCollection);
			}// 子表行
			detailDatas.add(detailData);
		}
		billData.setDetailDatas(detailDatas);
	}

	/**
	 * 初始化一行数据（从BAP单据到移动单据）
	 * 
	 * @param context
	 * @param rd
	 * @param bo
	 * @param mobileFields
	 */
	private static void initFieldValue(Context context, IMFieldsCollection rd,
			BusinessObject bo, IMFieldsCollection mobileFields,List<String> editableFieldNames) {

		Set<String> keyset = mobileFields.getKeySet();
		for (String fieldKey : keyset) {
			if ("RECID".equalsIgnoreCase(fieldKey))
				continue;
			IMField mfield = mobileFields.getField(fieldKey);
			try {
				if (StringUtil.isEmpty(mfield.getFieldName())) {
					Object value = bo.getFieldValue(fieldKey);
					FieldType type = bo.getFieldType(fieldKey);
					rd.addField(fieldKey,
							getNewField(context, mfield, type, value, fieldKey));
				} else {
					Object value = bo.getFieldValue(mfield.getFieldName());
					FieldType type = bo.getFieldType(mfield.getFieldName());
					rd.addField(mfield.getFieldName(),
							getNewField(context, mfield, type, value, fieldKey));
				}
				if(mfield instanceof MInputDataField&&!((MInputDataField) mfield).getFieldConstraint().isReadOnly()){
					editableFieldNames.add(fieldKey);
				}
			} catch (Exception e) {
				MobileLog.logError(e);
			}

		}
	}

	private static List<BusinessObject> getDetailDataes(String detailTabName,
			ModelData modelData) {
		for (List<BusinessObject> detailDatas : modelData.getDetailsList()) {
			if (detailDatas.size() < 1) {
				continue;
			}
			String tableName = detailDatas.get(0).getTable().getName();
			if (detailTabName.equals(tableName)) {
				return detailDatas;
			}

		}// 子表
		return new ArrayList<BusinessObject>();
	}

	public static IMField getNewField(Context context, IMField defaultFiled,
			FieldType type, Object value, String fieldName) {
		if (value == null) {
			return defaultFiled;
		}
		MInputDataField newField = new MInputDataField();

		newField.setFieldName(fieldName);
		newField.setTitle(defaultFiled.getFieldTitle());
		newField.setValueType(defaultFiled.getValueType());
		// // 处理特殊数据类型（GUID和关联基础数据）
		// if (value == null) {
		// return value;
		// }
		// if (type != FieldType.GUID && type != FieldType.VARBINARY) {
		// return value;
		// }
		switch (type) {
		case STRING:
			newField.setStringValue(value.toString());
			break;
		case GUID:
			newField.setStringValue(((GUID) value).toString());
			newField.setObjectValue(getMobileBaseData(context, defaultFiled,
					(GUID) value));
			newField.setValueType(defaultFiled.getValueType());
			break;
		case VARBINARY:
			newField.setObjectValue(getMobileBaseData(context, defaultFiled,
					GUID.emptyID));
			GUID[] values = BDMultiSelectUtil.bytesTOGuids((byte[]) value);
			getMobileBaseData(context, defaultFiled, values);
			newField.setObjectValue(getMobileBaseData(context, defaultFiled,
					values));
			break;
		case BOOLEAN:
			newField.setBooleanValue((Boolean) value);
			break;
		case DATE:
			newField.setLongValue(((Date) value).getTime());
			newField.setValueType(MDataType.DATE);
			break;
		case LONG:
			newField.setLongValue((Long) value);
			break;
		case INT:
			newField.setIntValue((Integer) value);
			break;
		case NUMERIC:
			newField.setDoubleValue((Double) value);
			break;
		case TEXT:
			newField.setStringValue((value.toString()));
			break;
		case BYTES:
			newField.setStringValue((value.toString()));
			break;
		}
		if (defaultFiled instanceof MInputDataField) {
			newField.getFieldConstraint().deserialize(
					((MInputDataField) defaultFiled).getFieldConstraint()
							.serialize());
		}
		return newField;
	}

	public static IMField getNewField2(Context context, IMField defaultFiled,
			FieldType type, Object value, String fieldName) {
		if (value == null) {
			return defaultFiled;
		}
		MField newField = new MInputDataField();

		newField.setFieldName(fieldName);
		newField.setTitle(defaultFiled.getFieldTitle());
		newField.setValueType(defaultFiled.getValueType());
		// // 处理特殊数据类型（GUID和关联基础数据）
		// if (value == null) {
		// return value;
		// }
		// if (type != FieldType.GUID && type != FieldType.VARBINARY) {
		// return value;
		// }
		switch (type) {
		case STRING:
			newField.setStringValue(value.toString());
			break;
		case GUID:
			newField.setStringValue(((GUID) value).toString());
			newField.setObjectValue(getMobileBaseData(context, defaultFiled,
					(GUID) value));
			newField.setValueType(defaultFiled.getValueType());
			break;
		case VARBINARY:
			newField.setObjectValue(getMobileBaseData(context, defaultFiled,
					GUID.emptyID));
			GUID[] values = BDMultiSelectUtil.bytesTOGuids((byte[]) value);
			getMobileBaseData(context, defaultFiled, values);
			break;
		case BOOLEAN:
			newField.setBooleanValue((Boolean) value);
			break;
		case DATE:
			newField.setLongValue(((Date) value).getTime());
			newField.setValueType(MDataType.DATE);
			break;
		case LONG:
			newField.setLongValue((Long) value);
			break;
		case INT:
			newField.setIntValue((Integer) value);
			break;
		case NUMERIC:
			newField.setDoubleValue((Double) value);
			break;
		case TEXT:
			newField.setStringValue((value.toString()));
			break;
		case BYTES:
			newField.setStringValue((value.toString()));
			break;
		}
		return newField;
	}

	public static void addID(IMFieldsCollection fields, String id) {
		MInputDataField field = new MInputDataField();
		field.setFieldName(Constant.FIELDNAME_RECID);
		field.setTitle(Constant.FIELDTITLE_ID);
		field.setStringValue(id);
		field.setValueType(MDataType.STRING);
		fields.addField(Constant.FIELDNAME_RECID, field);
		if (fields instanceof MFieldsCollection) {
			((MFieldsCollection) fields).setId(id);
		}
	}

	public static void addID(IMFieldsCollection fields, GUID id) {
		addID(fields, id.toString());

	}

	private final static MBaseData getMobileBaseData(Context context,
			IMField mField, GUID value) {
		MBaseData baseData = (MBaseData) mField.getObjectValue();
		if (baseData == null) {
			baseData = new MBaseData();
			baseData.setValue(new String[] { value.toString() });
			baseData.setTitle(new String[] { value.toString() });
			return baseData;
		}
		if (value == null || StringUtil.isEmpty(baseData.getTableName())) {
			baseData = new MBaseData();
			baseData.setValue(new String[] { value.toString() });
			baseData.setTitle(new String[] { value.toString() });
			return baseData;
		}
		FBaseDataObject fdb = BaseDataCenter.findObject(context,
				baseData.getTableName(), value);
		if (fdb != null) {
			return new MBaseData(fdb.getRECID().toString(), fdb.getStdName(),
					baseData.getTableName());
		} else {
			return new MBaseData(value.toString(), "", "");
		}
	}

	private final static MBaseData getMobileBaseData(Context context,
			IMField mField, GUID[] value) {
		MBaseData baseData = (MBaseData) mField.getObjectValue();
		String[] values = new String[value.length];
		String[] titles = new String[value.length];
		for (int i = 0; i < value.length; i++) {
			FBaseDataObject fdb = BaseDataCenter.findObject(context,
					baseData.getTableName(), value[i]);
			if (fdb != null) {
				values[i] = fdb.getRECID().toString();
				titles[i] = fdb.getStdName().toString();
			}
		}
		return new MBaseData(values, titles, baseData.getTableName());
	}

	public static Object getValue(IField field, BillData data, IMField fData) {
		// String match = data.getTableName()+"."+fData.getFieldName();
		// if (this._map.get(match) != null) {
		// return null;
		// }
		Object resultValue = null;
		if (field != null) {
			switch (field.getType()) {
			case VARBINARY:
				switch (fData.getValueType()) {
				case BASEDATA_MULTI:
					Object value = fData.getObjectValue();
					if (value != null && value instanceof MBaseData
							&& ((MBaseData) value).getValue() != null
							&& ((MBaseData) value).getValue().length != 0) {
						value = getBaseDataBytes(((MBaseData) value).getValue());
					} else {
						if (StringUtil.isNotEmpty(fData.getStringValue())) {
							value = GUID.valueOf(fData.getStringValue());
						} else {
							value = null;
						}
					}
					resultValue = value;
					break;
				case BASEDATA_SINGLE:
					Object value2 = fData.getObjectValue();
					if (value2 != null && value2 instanceof MBaseData
							&& ((MBaseData) value2).getValue() != null
							&& ((MBaseData) value2).getValue().length != 0) {
						value2 = GUID
								.valueOf(((MBaseData) value2).getValue()[0]);
					} else {
						if (StringUtil.isNotEmpty(fData.getStringValue())) {
							value = GUID.valueOf(fData.getStringValue());
						} else {
							value = null;
						}
					}
					resultValue = value2;
					break;
				case STRING:
					if (StringUtil.isNotEmpty(fData.getStringValue())) {
						resultValue = GUID.valueOf(fData.getStringValue());
					} else {
						resultValue = null;
					}
				default:
					break;
				}
				break;
			case GUID:
				switch (fData.getValueType()) {
				case BASEDATA_MULTI:
					Object value = fData.getObjectValue();
					if (value != null && value instanceof MBaseData
							&& ((MBaseData) value).getValue() != null
							&& ((MBaseData) value).getValue().length != 0) {
						value = getBaseDataBytes(((MBaseData) value).getValue());
					} else {
						if (StringUtil.isNotEmpty(fData.getStringValue())) {
							value = GUID.valueOf(fData.getStringValue());
						} else {
							value = null;
						}
					}
					resultValue = value;
					break;
				case BASEDATA_SINGLE:
					Object value2 = fData.getObjectValue();
					if (value2 != null && value2 instanceof MBaseData
							&& ((MBaseData) value2).getValue() != null
							&& ((MBaseData) value2).getValue().length != 0) {
						value2 = GUID
								.valueOf(((MBaseData) value2).getValue()[0]);
					} else {
						if (StringUtil.isNotEmpty(fData.getStringValue())) {
							value2 = GUID.valueOf(fData.getStringValue());
						} else {
							value2 = null;
						}
					}
					resultValue = value2;
					break;
				case STRING:
					if (StringUtil.isNotEmpty(fData.getStringValue())) {
						resultValue = GUID.valueOf(fData.getStringValue());
					} else {
						resultValue = null;
					}

				default:
					break;
				}
				break;
			case BOOLEAN:
				resultValue = fData.getBooleanValue();
				break;
			case DATE:
				resultValue = new Date(fData.getLongValue());
				break;
			case INT:
				resultValue = fData.getLongValue();
				break;
			case NUMERIC:
				resultValue = fData.getDoubleValue();
				break;
			case STRING:
				resultValue = fData.getStringValue();
				break;
			case BYTES:
				resultValue = fData.getStringValue();
				break;
			case LONG:
				resultValue = fData.getLongValue();
				break;
			case TEXT:
				resultValue = fData.getStringValue();
				break;
			default:
				break;
			}
			return resultValue;
		}
		switch (fData.getValueType()) {
		case BASEDATA_MULTI:
			Object value = fData.getObjectValue();
			if (value != null && value instanceof MBaseData
					&& ((MBaseData) value).getValue() != null
					&& ((MBaseData) value).getValue().length != 0) {
				value = getBaseDataBytes(((MBaseData) value).getValue());
			} else {
				value = null;
			}
			resultValue = value;
			break;
		case BASEDATA_SINGLE:
			Object value2 = fData.getObjectValue();
			if (value2 != null && value2 instanceof MBaseData
					&& ((MBaseData) value2).getValue() != null
					&& ((MBaseData) value2).getValue().length != 0) {
				value2 = GUID.valueOf(((MBaseData) value2).getValue()[0]);
			} else {
				value2 = null;
			}
			resultValue = value2;
			break;
		case BOOLEAN:
			resultValue = fData.getBooleanValue();
			break;
		case DATE:
			resultValue = new Date(fData.getLongValue());
			break;
		case DATETIME:
			resultValue = new Date(fData.getLongValue());
			break;
		case INT:
			resultValue = fData.getLongValue();
			break;
		case NUMRIC:
			resultValue = fData.getDoubleValue();
			break;
		case STRING:
			resultValue = fData.getStringValue();
			break;
		case UNDEFINE:
			resultValue = fData.getStringValue();
			break;

		default:
			break;
		}
		return resultValue;
	}

	public static final byte[] getBaseDataBytes(String[] value) {
		GUID[] guidArr = new GUID[value.length];
		int i = 0;
		for (String mbd : value) {
			guidArr[i] = GUID.valueOf(mbd);
			i++;
		}
		return BDMultiSelectUtil.guidsTOBytes(guidArr);
	}
}
