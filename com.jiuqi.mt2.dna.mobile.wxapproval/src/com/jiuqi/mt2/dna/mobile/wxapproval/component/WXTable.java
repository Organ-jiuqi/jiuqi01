package com.jiuqi.mt2.dna.mobile.wxapproval.component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jiuqi.dna.core.Context;
import com.jiuqi.mt2.dna.mobile.wxapproval.common.ApprovalPropertieInfo;
import com.jiuqi.mt2.dna.mobile.wxapproval.util.WXFieldUtil;
import com.jiuqi.mt2.spi.bill.metadata.MobileBillDefine;
import com.jiuqi.mt2.spi.bill.metadata.PageDefine;
import com.jiuqi.mt2.spi.bill.model.BillData;
import com.jiuqi.mt2.spi.bill.model.DetailData;
import com.jiuqi.mt2.spi.common2.metadata.MBaseData;
import com.jiuqi.mt2.spi.common2.table.IMFieldsCollection;
import com.jiuqi.mt2.spi.common2.table.IMShowTemplate;
import com.jiuqi.mt2.spi.common2.table.IMTable;
import com.jiuqi.mt2.spi.common2.table.IMTableCell;
import com.jiuqi.xlib.utils.StringUtil;

/**
 * 单据内容
 * 
 * @author liuzihao
 */
public class WXTable {
	private BillData billData;
	private MobileBillDefine billDefine;
	private Context context;
	List<String> list = null;
	private ApprovalPropertieInfo api = null;

	public WXTable() {
	}

	public WXTable(BillData billData, MobileBillDefine billDefine) {
		this.billData = billData;
		this.billDefine = billDefine;
	}

	public WXTable(BillData billData, MobileBillDefine billDefine,
			Context context1) {
		this.billData = billData;
		this.billDefine = billDefine;
		this.context = context1;

	}

	public String getHtml() {
		StringBuffer table = new StringBuffer("");
		table.append(createSupTable());
		table.append(createSubTable());
		return table.toString();
	}

	public String getHtml(ApprovalPropertieInfo ap) {
		StringBuffer table = new StringBuffer("");
		api = ap;
		String[] edit = api.getEditField();
		list = Arrays.asList(edit);
		table.append(createSupTable());
		table.append(createSubTable());
		return table.toString();
	}

	public String getApprovedHtml() {
		StringBuffer table = new StringBuffer("<div class='tablediv'>");

		table.append(createSupTable());
		table.append(createSubTable());
		table.append("</div>");
		return table.toString();
	}

	// 创建主表
	private String createSupTable() {
		StringBuffer suptable = new StringBuffer("");
		suptable.append("<div class='tablediv' id='")
				.append(billDefine.getMasterPage().getName()).append("'>");
		if (billDefine.getMasterPage().getmInputLayout() == null) {
			suptable.append(createTable(null, null, null));
		} else {
			suptable.append(createTable(billDefine.getMasterPage()
					.getmInputLayout().getTable(), billData.getMasterData(),
					billDefine.getMasterPage().getName()));
		}
		suptable.append("</div>");
		return suptable.toString();
	}

	// 创建子表
	private String createSubTable() {
		StringBuffer subtable = new StringBuffer();
		if (billData.getDetailDatas() != null
				&& billData.getDetailDatas().size() > 0) {
			for (int a = 0; a < billData.getDetailDatas().size(); a++) {
				DetailData detailData = billData.getDetailDatas().get(a);
				if (detailData.getRowDataes() == null
						|| detailData.getRowDataes().getSize() == 0)
					continue;
				PageDefine pageDefine = null;
				if (billDefine.getDetailPages() != null) {
					for (PageDefine define : billDefine.getDetailPages()) {
						if (!define.isHidden()) {
							if (define.getReferenceTable().getName()
									.equals(detailData.getTableName())) {
								pageDefine = define;
							}
						}
					}
				}
				if (pageDefine != null) {
					subtable.append(
							"<div data-role='collapsible' data-collapsed='false' class='tablediv' id='")
							.append(pageDefine.getName()).append("'>")
							.append("<h4>")
							.append(pageDefine.getReferenceTable().getTitle())
							.append("</h4>");
					for (int b = 0; b < detailData.getRowDataes().getSize(); b++) {
						String subtabel = createTable(pageDefine
								.getmInputLayout().getTable(), detailData
								.getRowDataes().getFieldCollectionAtIndex(b),
								pageDefine.getName());
						subtable.append(subtabel);
					}
					subtable.append("</div>");
				}
			}
		}
		return subtable.toString();
	}

	// 待审批
	public String createTable(IMTable table, IMFieldsCollection collection,
			String tablename) {
		StringBuffer buffer = new StringBuffer();
		int row = 0;
		int column = 0;
		if (table != null) {
			row = table.getRowCount();
			column = table.getColCount();
		}
		if (row <= 0 || column <= 0) {
			buffer.append("<table> </table>");
			return buffer.toString();
		}
		buffer.append("<table width='100%' border='0' cellspacing='0' >");
		int flag = 0;
		// 行
		for (int i = 0; i < row; i++) {
			// 列
			int colspan = 1;
			String td1_id = "";
			String td1_text = "";
			String td1_type = "";
			String td2_id = "";
			String td2_text = "";
			String td2_type = "";

			double fontsize = 1;
			for (int j = 0; j < column; j++) {
				if (j > 1) {
					continue;
				}
				String id = "";
				IMTableCell cell = table.getCell(i, j);
				String text = cell.getTitle();
				String textType = "";
				if (StringUtil.isNotEmpty(tablename)
						&& StringUtil.isNotEmpty(text)
						&& text.indexOf("{#") > -1 && text.indexOf("}") > -1
						&& text.indexOf("{#") < text.indexOf("}")) {
					id = text.substring(text.indexOf("{#") + 2,
							text.indexOf("}"));
				}
				if (collection != null && text != null) {
					Pattern p = Pattern.compile("\\{#([^\\}]+)\\}");
					Matcher m = p.matcher(text);
					while (m.find()) {
						String value = WXFieldUtil.getFieldValueByName(
								m.group(1), collection);
						String valueType = WXFieldUtil.getFieldValueType(
								m.group(1), collection);
						text = (StringUtil.isNotEmpty(value) ? text.replace(
								m.group(), value) : "");
						textType = (StringUtil.isNotEmpty(valueType) ? valueType
								: "");
					}
				}

				fontsize = 1 + cell.getStyle().getFontSize() * 0.1;

				if (StringUtil.isEmpty(text)) {
					text = "";
				}
				if (j == 0) {
					if (cell.getRowSpan() > 1) {
						colspan = 2;
					}
					if (StringUtil.isNotEmpty(id)) {
						td1_id = id;
					}
					if (StringUtil.isNotEmpty(textType)) {
						td1_type = textType;
					}
					if (StringUtil.isNotEmpty(text)) {
						td1_text = text;
					}
				} else if (j == 1) {
					if (colspan == 2) {
						continue;
					} else {
						if (StringUtil.isNotEmpty(id)) {
							td2_id = id;
							td1_id = id + "_TITLE";
						}
						if (StringUtil.isNotEmpty(textType)) {
							td2_type = textType;
						}
						if (StringUtil.isNotEmpty(text)) {
							td2_text = text;
						}
					}
				}
			}
			flag++;
			if (colspan == 2) {
				buffer.append("<tr><td class='")
						.append("billtable_center' ")
						.append("colspan='4'  style='padding:5px 5px;line-height:20px;color:#000000")
						.append(";")
						.append("text-align:left")
						.append(";font-size:")
						.append(fontsize * 16.0D)
						.append("px;'>")
						.append((StringUtil.isNotEmpty(td1_text)) ? td1_text
								+ "&nbsp;&nbsp;" : "").append("</td></tr>");
			} else if (colspan == 1) {
				MBaseData baseData;
				String baseTableName;
				buffer.append(
						"<tr><td style='padding:5px 5px;  width:  25%;'><span ")
						.append("class='")
						.append(td2_id + "___" + flag)
						.append("' ")
						.append("style='font-weight:bold;display:inline-block;color:")
						.append("#000000")
						.append("; ")
						.append("text-align:left")
						.append(";font-size:")
						.append(fontsize * 14.0D)
						.append("px;'>")
						.append(td1_text)
						.append((StringUtil.isNotEmpty(td1_text)) ? "：&nbsp;&nbsp;"
								: "").append("</span></td>");

				if (td2_text.isEmpty()) {
					if (list == null || list.size() == 0) {
						buffer.append(
								"<td style='padding:5px 5px; width: 75%;'><span class='")
								.append(td2_id).append("' ")
								.append("titleid='").append(flag).append("' ")
								.append("RECID='").append(collection.getId())
								.append("' ").append("BData='")
								.append(td2_text).append("' ")
								.append("contenteditable='false' TextType='")
								.append(td2_type).append("' ")
								.append("style='color:#000000").append("; ")
								.append(";font-size:").append(fontsize * 14.0D)
								.append("px;'>").append(td2_text)
								.append("</span></td></tr>");
					} else {
						if (td2_type.equals("single")) {
							baseData = (MBaseData) collection.getField(td2_id)
									.getObjectValue();
							baseTableName = baseData.getTableName();
							buffer.append(
									"<td style='padding:5px 5px; width: 75%;'><span      class='")
									.append(td2_id)
									.append("'  ")
									.append(" id='")
									.append(baseTableName + flag)
									.append("'  ")
									.append("titleid='")
									.append(flag)
									.append("' ")
									.append("RECID='")
									.append(collection.getId())
									.append("' ")
									.append("BData='")
									.append(baseTableName)
									.append("' ")
									.append("contenteditable='false'   TextType='")
									.append(td2_type)
									.append("' ")
									.append("style='border-bottom: 1px solid #ff0000;display: inline-block;  min-width: 100%; color:")
									.append("#000000")
									.append(";font-size:")
									.append(fontsize * 14.0D)
									.append("px;'   onclick='showdiv(this)'></span>")
									.append("  </td></tr>");
						} else if (td2_type.equals("multi")) {
							baseData = (MBaseData) collection.getField(td2_id)
									.getObjectValue();
							baseTableName = baseData.getTableName();

							buffer.append(
									"<td style='padding:5px 5px; width: 75%;'><span      class='")
									.append(td2_id)
									.append("'  ")
									.append(" id='")
									.append(baseTableName + flag)
									.append("'  ")
									.append("titleid='")
									.append(flag)
									.append("' ")
									.append("RECID='")
									.append(collection.getId())
									.append("' ")
									.append("BData='")
									.append(baseTableName)
									.append("' ")
									.append("contenteditable='false'   TextType='")
									.append(td2_type)
									.append("' ")
									.append("style='border-bottom: 1px solid #ff0000;display: inline-block;  min-width: 100%; color:")
									.append("#000000")
									.append(";font-size:")
									.append(fontsize * 14.0D)
									.append("px;'  onclick='showdiv2(this)'></span>")
									.append("  </td></tr>");
						} else if (td2_type.equals("date")) {
							buffer.append(
									"<td style='padding:5px 5px; width: 75%;'><input   class='")
									.append(td2_id)
									.append("' id='USER_AGE'")
									.append("titleid='")
									.append(flag)
									.append("'  ")
									.append("RECID='")
									.append(collection.getId())
									.append("' ")
									.append("BData='")
									.append(td2_text)
									.append("' ")
									.append("contenteditable='false'   TextType='")
									.append(td2_type)
									.append("' ")
									.append("style='border:none;border-bottom: thin solid  ;    min-width: 100%; color:#000000")
									.append(";font-size:")
									.append(fontsize * 14.0D)
									.append("px;'  > ").append("  </td></tr>");
						} else {
							buffer.append(
									"<td  style='padding:5px 5px; width: 75%;'><span class='")
									.append(td2_id)
									.append("' ")
									.append("titleid='")
									.append(flag)
									.append("' ")
									.append("RECID='")
									.append(collection.getId())
									.append("' ")
									.append("BData='")
									.append(td2_text)
									.append("' ")
									.append("contenteditable='false' TextType='")
									.append(td2_type)
									.append("' ")
									.append("style='border-bottom: 1px solid #ff0000;display: inline-block; min-width: 100%; color:")
									.append("#000000").append("; ")
									.append(";font-size:")
									.append(fontsize * 14.0D).append("px;'>")
									.append("</span></td></tr>");
						}
					}
				} else {
					if (list == null || list.size() == 0) {
						buffer.append(
								"<td style='padding:5px 5px;  width: 75%;'><span class='")
								.append(td2_id).append("' ")
								.append("titleid='").append(flag).append("' ")
								.append("RECID='").append(collection.getId())
								.append("' ").append("BData='")
								.append(td2_text).append("' ")
								.append("contenteditable='false' TextType='")
								.append(td2_type).append("' ")
								.append("style='color:").append("#000000")
								.append("; ").append(";font-size:")
								.append(fontsize * 14.0D).append("px;'>")
								.append(td2_text).append("</span></td></tr>");
					} else {
						if (td2_type.equals("single")) {
							baseData = (MBaseData) collection.getField(td2_id)
									.getObjectValue();
							baseTableName = baseData.getTableName();
							buffer.append(
									"<td style='padding:5px 5px; width: 75%;'><span      class='")
									.append(td2_id)
									.append("'  ")
									.append(" id='")
									.append(baseTableName + flag)
									.append("'  ")
									.append("titleid='")
									.append(flag)
									.append("' ")
									.append("RECID='")
									.append(collection.getId())
									.append("'  ")
									.append("BData='")
									.append(baseTableName)
									.append("' ")
									.append("contenteditable='false'   TextType='")
									.append(td2_type)
									.append("' ")
									.append("style='border-bottom: 1px solid #ff0000;display: inline-block;  min-width: 100%; color:")
									.append("#000000")
									.append(";font-size:")
									.append(fontsize * 14.0D)
									.append("px;'   onclick='showdiv(this)'> "
											+ td2_text + "</span>");
						} else if (td2_type.equals("boolean")) {
							buffer.append(
									"<td style='padding:5px 5px; width: 75%;'><span class='")
									.append(td2_id)
									.append("'   ")
									.append("id='")
									.append(td2_id)
									.append("' ")
									.append("titleid='")
									.append(flag)
									.append("' ")
									.append("RECID='")
									.append(collection.getId())
									.append("'  ")
									.append("BData='")
									.append(td2_text)
									.append("'  ")
									.append("contenteditable='false' TextType='")
									.append(td2_type)
									.append("' ")
									.append("style='border-bottom: 1px solid #ff0000;display: inline-block;  min-width: 100%; color:")
									.append("#000000")
									.append("; ")
									.append(";font-size:")
									.append(fontsize * 14.0D)
									.append("px;'    onclick='showdiv1(this)'>")
									.append(td2_text)
									.append("   </span></td></tr>");
						}

						else if (td2_type.equals("date")) {
							buffer.append(
									"<td style='padding:5px 5px; width: 75%;'><input   class='")
									.append(td2_id)
									.append("'")
									.append("value='")
									.append(td2_text)
									.append("' id='USER_AGE'")
									.append("titleid='")
									.append(flag)
									.append("'  ")
									.append("RECID='")
									.append(collection.getId())
									.append("' ")
									.append("BData='")
									.append(td2_text)
									.append("' ")
									.append("contenteditable='false'   TextType='")
									.append(td2_type)
									.append("' ")
									.append("style='border: 0;border-bottom:  solid green;  min-width: 100%; color:")
									.append("#000000").append(";font-size:")
									.append(fontsize * 14.0D)
									.append("px;'  > ").append("  </td></tr>");
						}

						else if (td2_type.equals("multi")) {
							baseData = (MBaseData) collection.getField(td2_id)
									.getObjectValue();
							baseTableName = baseData.getTableName();
							buffer.append(
									"<td style='padding:5px 5px; width: 75%;'><span      class='")
									.append(td2_id)
									.append("'  ")
									.append(" id='")
									.append(baseTableName + flag)
									.append("'  ")
									.append("titleid='")
									.append(flag)
									.append("' ")
									.append("RECID='")
									.append(collection.getId())
									.append("' ")
									.append("BData='")
									.append(baseTableName)
									.append("' ")
									.append("contenteditable='false'   TextType='")
									.append(td2_type)
									.append("' ")
									.append("style='border-bottom: 1px solid #ff0000;display: inline-block;  min-width: 100%; color:")
									.append("#000000")
									.append(";font-size:")
									.append(fontsize * 14.0D)
									.append("px;'  onclick='showdiv2(this)'>"
											+ td2_text + "</span>")
									.append("  </td></tr>");
						} else {
							buffer.append(
									"<td style='padding:5px 5px; width: 75%;'><span class='")
									.append(td2_id)
									.append("' ")
									.append("titleid='")
									.append(flag)
									.append("' ")
									.append("RECID='")
									.append(collection.getId())
									.append("' ")
									.append("BData='")
									.append(td2_text)
									.append("' ")
									.append("contenteditable='false' TextType='")
									.append(td2_type)
									.append("' ")
									.append("style='border-bottom: 1px solid #ff0000;display: inline-block;  min-width: 100%; color:")
									.append("#000000").append("; ")
									.append(";font-size:")
									.append(fontsize * 14.0D).append("px;'>")
									.append(td2_text)
									.append("</span></td></tr>");

						}
					}
				}
			}
		}
		buffer.append("</table>");
		buffer.append("<div  id ='test' ></div>")
				.append("<div id='dv'> ")
				.append("<div style='width:100%; height:5%;'>")
				.append("<button class='button' id='button' style='background-color:white;border:0;cursor:pointer;float:right' onclick='closediv(this)'>╳</button></div>")
				.append("<div style='width:94%;  height:13%;margin-top:10px;margin-left:10px;'>")
				.append("<input type='text' class='input' id ='input'  value='' style='-webkit-appearance:none;float:left; overflow:auto;width:66%;box-sizing: border-box; height:60%; border:1px solid #bfd1eb;'>")
				.append("<input type='button' value='清除' class='input' style='-webkit-appearance:none; padding:0;  text-align:center; width:15%;  margin-left:5px;   height:60%; float:right; border:1px solid #bfd1eb ;' onclick='clearinput(this)'>")
				.append("<input type='button' value='查询' class='button' style='-webkit-appearance:none; padding:0;  text-align:center;width:15%; margin-left:6px;   height:60%; float:right; border:1px solid #bfd1eb ;' onclick='getMoreContents()'> ")
				.append("</div><div style='width:94%; text-align:center;height:75%; margin-left:10px; overflow:auto; border:1px solid #bfd1eb; '>")
				.append("<table id='content' style='width:95%;margin-left:10px; margin-right:10px;' border='0'  cellspacing='0' cellpadding='0'> </table> </div> </div>");

		buffer.append("<div id='dv2'> ")
				.append("<div style='width:100%; height:5%;'>")
				.append("<button class='button' id='button' style='background-color:white;border:0;cursor:pointer;float:right' onclick='closediv(this)'>╳</button></div>")
				.append("<div style='width:98%;  height:13%;margin-top:10px;margin-left:7px;'>")
				.append("<input type='text' class='input' id ='input2'   value='' style='-webkit-appearance:none;float:left; overflow:auto;width:56%;box-sizing: border-box; height:60%; border:1px solid #bfd1eb;'>")
				.append("<input type='button' value='查询' class='button'style='-webkit-appearance:none;  padding:0;  text-align:center;width:13%;margin-left:4px;  margin-top: 0px; height:60%; float:left; border:1px solid #bfd1eb ;' onclick='getMoreContents()'> ")
				.append("<input type='button' value='清除' class='button' style='-webkit-appearance:none;width:13%;  padding:0;  text-align:center; margin-left:4px; margin-top: 0px; height:60%; float:left; border:1px solid #bfd1eb ;' onclick='clearinput(this)'>")
				.append("<input type='button' value='确认' class='button' style='-webkit-appearance:none;width:13%;  padding:0;  text-align:center; margin-left:4px; margin-top: 0px; height:60%; float:left; border:1px solid #bfd1eb ;' onclick='confirm(this)'>")
				.append("</div><div style='width:96%; text-align:center;height:75%; margin-left:7px; overflow:auto; border:1px solid #bfd1eb; '>")
				.append("<table id='content1' style='width:95%;margin-left:10px; margin-right:10px;' border='0'  cellspacing='0' cellpadding='0'> </table> </div> </div>");

		buffer.append("<div id='dv1'> <div style='width:100%; height:5%;'> ")
				.append("<button class='button' id='button' style='background-color:white;border:0;cursor:pointer;float:right'  onclick='closediv(this)'>╳</button></div>  ")
				.append("<div style='width:94%;  height:13%;margin-top:10px;margin-left:10px;'><input type='text' class='input' id='input1' value=''")
				.append(" style='-webkit-appearance:none;float:left; overflow:auto;width:77%;box-sizing: border-box; height:60%; border:1px solid #bfd1eb;'>")
				.append("<input type='button' value='查询' class='button' style='-webkit-appearance:none; padding:0;  text-align:center; width:20%;margin-left:6px;   height:60%; float:left; border:1px solid #bfd1eb ;'>  ")
				.append("</div>")
				.append(" <div style='width:94%; height:75%; margin-left:10px; text-align:center; overflow:auto; border:1px solid #bfd1eb; '>")
				.append("<table  id='content' style='width:95%;margin-left:10px;margin-right:10px;' border='0' cellspacing='0' cellpadding='0'>")
				.append("<tr class='change' style=' text-align:left; '   onclick='setContent1(this)'> <td>是</td></tr><tr class='change'   style=' text-align:left; '  onclick='setContent1(this)'> <td>否</td></tr></table> </div> </div> ");
		return buffer.toString();
	}

	// 已完成
	public String createTabled(IMShowTemplate showTemplate,
			IMFieldsCollection collection, String link) {
		StringBuffer buffer = new StringBuffer();
		if (showTemplate == null) {
			buffer.append("<table>  </table>");
			return buffer.toString();
		}
		IMTable table = showTemplate.getTable();
		int row = 0;
		int column = 0;
		if (table != null) {
			row = table.getRowCount();
			column = table.getColCount();
		}
		if (row <= 0 || column <= 0) {
			buffer.append("<table></table>");
			return buffer.toString();
		}
		if (link != null) {
			buffer.append("<a data-ajax='false' class='table_a_label' href='")
					.append(link).append("'>");
		}
		buffer.append("<table width='100%' border='0' cellspacing='0'>");
		// 行
		for (int i = 0; i < 6; i++) {
			if (row < 6 && i >= row) {
				buffer.append("<tr><td colspan='4' style='height:22px;'></td></tr>");
				continue;
			}
			// 列
			int colspan = 1;
			String td1_id = "";
			String td1_text = "";
			String td1_type = "";
			String td2_id = "";
			String td2_text = "";
			String td2_type = "";
			for (int j = 0; j < column; j++) {
				if (j > 1) {
					continue;
				}
				IMTableCell cell = table.getCell(i, j);
				String text = cell.getTitle();
				String textType = "";
				if (collection != null && text != null) {
					Pattern p = Pattern.compile("\\{#([^\\}]+)\\}");
					Matcher m = p.matcher(text);
					while (m.find()) {
						String value = WXFieldUtil.getFieldValueByName(
								m.group(1), collection);
						String valueType = WXFieldUtil.getFieldValueType(
								m.group(1), collection);
						text = (StringUtil.isNotEmpty(value) ? text.replace(
								m.group(), value) : "");
						textType = (StringUtil.isNotEmpty(valueType) ? valueType
								: "");
					}
				}
				if (StringUtil.isEmpty(text)) {
					text = "";
				}
				if (j == 0) {
					if (cell.getRowSpan() > 1) {
						colspan = 2;
					}
					if (StringUtil.isNotEmpty(textType)) {
						td1_type = textType;
					}
					if (StringUtil.isNotEmpty(text)) {
						td1_text = text;
					}
				} else if (j == 1) {
					if (colspan == 2) {
						continue;
					} else {
						if (StringUtil.isNotEmpty(textType)) {
							td2_type = textType;
						}
						if (StringUtil.isNotEmpty(text)) {
							td2_text = text;
						}
					}
				}
			}
			if (colspan == 2) {
				buffer.append("<tr><td class='billtable_center' colspan='4' ")
						.append("style='color:#000000")
						.append(";text-align:left")
						.append(";font-size:14px;'>")
						.append(StringUtil.isNotEmpty(td1_text) ? (td1_text + "&nbsp;&nbsp;")
								: "").append("</td></tr>");
				;
			} else if (colspan == 1) {
				buffer.append(
						"<tr><td style='padding:5px 5px;line-height:20px;'><span class='billtable_td_left ")
						.append(td1_id)
						.append("' ")
						.append("TextType='")
						.append(td2_type)
						.append("' ")
						.append("RECID='")
						.append(collection.getId())
						.append("' ")
						.append("BData='")
						.append(td1_text)
						.append("' ")
						.append("style='color:#000000")
						.append("; ")
						.append("text-align:left")
						.append(";font-size:14px;'>")
						.append(td1_text)
						.append("</span>")
						.append("<span class='billtable_td_colon'>")
						.append(StringUtil.isNotEmpty(td1_text) ? ":&nbsp;&nbsp;"
								: "")
						.append("</span>")
						.append("<span class='billtable_td_right ")
						.append(td2_id)
						.append("' ")
						.append("RECID='")
						.append(collection.getId())
						.append("' ")
						.append("BData='")
						.append(td2_text)
						.append("' ")
						.append("contenteditable='false' TextType='")
						.append(td2_type)
						.append("' ")
						.append("style='color:#000000")
						.append("; ")
						.append("text-align:left")
						.append(";font-size:14px;'>")
						.append(td2_text)
						.append("</span><span style='width:4px;'></span></td></tr>");
			}
		}
		buffer.append("</table>");
		if (link != null) {
			buffer.append("</a>");
		}
		return buffer.toString();
	}

	// 待审批
	public String createTable2(IMTable table, IMFieldsCollection collection,
			String tablename) {
		StringBuffer buffer = new StringBuffer();
		int row = 0;
		int column = 0;
		if (table != null) {
			row = table.getRowCount();
			column = table.getColCount();
		}
		if (row <= 0 || column <= 0) {
			buffer.append("<table></table>");
			return buffer.toString();
		}

		buffer.append("<table width='100%' border='0' cellspacing='0' >");
		// 行
		for (int i = 0; i < row; i++) {
			// 列
			int colspan = 1;
			String td1_id = "";
			String td1_text = "";
			String td1_type = "";
			String td2_id = "";
			String td2_text = "";
			String td2_type = "";
			String al = "";
			String cr = "";
			double fontsize = 1;
			for (int j = 0; j < column; j++) {
				if (j > 1) {
					continue;
				}
				String id = "";
				IMTableCell cell = table.getCell(i, j);
				String text = cell.getTitle();
				String textType = "";
				if (StringUtil.isNotEmpty(tablename)
						&& StringUtil.isNotEmpty(text)
						&& text.indexOf("{#") > -1 && text.indexOf("}") > -1
						&& text.indexOf("{#") < text.indexOf("}")) {
					id = text.substring(text.indexOf("{#") + 2,
							text.indexOf("}"));
				}
				if (collection != null && text != null) {
					Pattern p = Pattern.compile("\\{#([^\\}]+)\\}");
					Matcher m = p.matcher(text);
					while (m.find()) {
						String value = WXFieldUtil.getFieldValueByName(
								m.group(1), collection);
						String valueType = WXFieldUtil.getFieldValueType(
								m.group(1), collection);
						text = (StringUtil.isNotEmpty(value) ? text.replace(
								m.group(), value) : "");
						textType = (StringUtil.isNotEmpty(valueType) ? valueType
								: "");
					}
				}
				int align = cell.getStyle().getTextAlignment();
				int color = cell.getStyle().getTextColor();
				fontsize = 1 + cell.getStyle().getFontSize() * 0.1;

				switch (align) {
				case 1 << 14: {
					al = "left";
					break;
				}
				case 1 << 24: {
					al = "center";
					break;
				}
				case 1 << 17: {
					al = "right";
					break;
				}
				}
				switch (color) {
				case 0x000000: {
					cr = "#000000";
					break;
				}
				case 0x265717: {
					cr = "#006600";
					break;
				}
				case 0xe47f1c: {
					cr = "#FF9900";
					break;
				}
				case 0x00ff00: {
					cr = "#66FF66";
					break;
				}
				case 0x0000ff: {
					cr = "#0000FF";
					break;
				}
				case 0xffffff: {
					cr = "#FFFFFF";
					break;
				}
				case 0x81b709: {
					cr = "#99CC00";
					break;
				}
				case 0x595959: {
					cr = "#707070";
					break;
				}
				case 0x979797: {
					cr = "#C0C0C0";
					break;
				}
				}
				if (StringUtil.isEmpty(text)) {
					text = "";
				}
				if (j == 0) {
					if (cell.getRowSpan() > 1) {
						colspan = 2;
					}
					if (StringUtil.isNotEmpty(id)) {
						td1_id = id;
					}
					if (StringUtil.isNotEmpty(textType)) {
						td1_type = textType;
					}
					if (StringUtil.isNotEmpty(text)) {
						td1_text = text;
					}
				} else if (j == 1) {
					if (colspan == 2) {
						continue;
					} else {
						if (StringUtil.isNotEmpty(id)) {
							td2_id = id;
							td1_id = id + "_TITLE";
						}
						if (StringUtil.isNotEmpty(textType)) {
							td2_type = textType;
						}
						if (StringUtil.isNotEmpty(text)) {
							td2_text = text;
						}
					}
				}
			}
			if (colspan == 2) {
				buffer.append("<tr><td class='")
						.append("billtable_center ")
						.append(td1_id)
						.append("' ")
						.append("RECID='")
						.append(collection.getId())
						.append("' ")
						.append("colspan='4' style='color:")
						.append(cr)
						.append(";")
						.append("text-align:")
						.append(al)
						.append(";font-size:")
						.append(fontsize * 14)
						.append("px;'>")
						.append(StringUtil.isNotEmpty(td1_text) ? (td1_text + "&nbsp;&nbsp;")
								: "").append("</td></tr>");
				;
			} else if (colspan == 1) {
				buffer.append("<tr><td class='billtable_td_left ")
						.append(td1_id).append("' ").append("TextType='")
						.append(td2_type).append("' ").append("RECID='")
						.append(collection.getId()).append("' ")
						.append("BData='").append(td1_text).append("' ")
						.append("style='color:").append(cr).append("; ")
						.append("text-align:").append(al).append(";font-size:")
						.append(fontsize * 14).append("px;'>").append(td1_text)
						.append("</td>")
						.append("<td class='billtable_td_colon'>")
						.append(StringUtil.isNotEmpty(td1_text) ? ":" : "")
						.append("</td>")
						.append("<td class='billtable_td_right ")
						.append(td2_id).append("' ").append("RECID='")
						.append(collection.getId()).append("' ")
						.append("BData='").append(td2_text).append("' ")
						.append("contenteditable='false' TextType='")
						.append(td2_type).append("' ").append("style='color:")
						.append(cr).append("; ").append("text-align:")
						.append(al).append(";font-size:").append(fontsize * 14)
						.append("px;'>").append(td2_text)
						.append("</td><td style='width:4px;'></td></tr>");
			}
		}
		buffer.append("</table>");
		return buffer.toString();
	}

	// 已完成
	public String createTabled2(IMShowTemplate showTemplate,
			IMFieldsCollection collection, String link) {
		StringBuffer buffer = new StringBuffer();
		if (showTemplate == null) {
			buffer.append("<table></table>");
			return buffer.toString();
		}
		IMTable table = showTemplate.getTable();
		int row = 0;
		int column = 0;
		if (table != null) {
			row = table.getRowCount();
			column = table.getColCount();
		}
		if (row <= 0 || column <= 0) {
			buffer.append("<table></table>");
			return buffer.toString();
		}
		if (link != null) {
			buffer.append("<a data-ajax='false' class='table_a_label' href='")
					.append(link).append("'>");
		}

		buffer.append("<table width='100%' border='0' cellspacing='0'>");
		// 行
		for (int i = 0; i < 6; i++) {
			if (row < 6 && i >= row) {
				buffer.append("<tr><td colspan='4' style='height:22px;'></td></tr>");
				continue;
			}
			// 列
			int colspan = 1;
			String td1_id = "";
			String td1_text = "";
			String td1_type = "";
			String td2_id = "";
			String td2_text = "";
			String td2_type = "";
			String al = "";
			String cr = "";
			double fontsize = 1;
			for (int j = 0; j < column; j++) {
				if (j > 1) {
					continue;
				}
				IMTableCell cell = table.getCell(i, j);
				String text = cell.getTitle();
				String textType = "";
				if (collection != null && text != null) {
					Pattern p = Pattern.compile("\\{#([^\\}]+)\\}");
					Matcher m = p.matcher(text);
					while (m.find()) {
						String value = WXFieldUtil.getFieldValueByName(
								m.group(1), collection);
						String valueType = WXFieldUtil.getFieldValueType(
								m.group(1), collection);
						text = (StringUtil.isNotEmpty(value) ? text.replace(
								m.group(), value) : "");
						textType = (StringUtil.isNotEmpty(valueType) ? valueType
								: "");
					}
				}
				int align = cell.getStyle().getTextAlignment();
				int color = cell.getStyle().getTextColor();
				fontsize = 1 + cell.getStyle().getFontSize() * 0.1;

				switch (align) {
				case 1 << 14: {
					al = "left";
					break;
				}
				case 1 << 24: {
					al = "center";
					break;
				}
				case 1 << 17: {
					al = "right";
					break;
				}
				}
				switch (color) {
				case 0x000000: {
					cr = "#000000";
					break;
				}
				case 0x265717: {
					cr = "#006600";
					break;
				}
				case 0xe47f1c: {
					cr = "#FF9900";
					break;
				}
				case 0x00ff00: {
					cr = "#66FF66";
					break;
				}
				case 0x0000ff: {
					cr = "#0000FF";
					break;
				}
				case 0xffffff: {
					cr = "#FFFFFF";
					break;
				}
				case 0x81b709: {
					cr = "#99CC00";
					break;
				}
				case 0x595959: {
					cr = "#707070";
					break;
				}
				case 0x979797: {
					cr = "#C0C0C0";
					break;
				}
				}

				if (StringUtil.isEmpty(text)) {
					text = "";
				}
				if (j == 0) {
					if (cell.getRowSpan() > 1) {
						colspan = 2;
					}
					if (StringUtil.isNotEmpty(textType)) {
						td1_type = textType;
					}
					if (StringUtil.isNotEmpty(text)) {
						td1_text = text;
					}
				} else if (j == 1) {
					if (colspan == 2) {
						continue;
					} else {
						if (StringUtil.isNotEmpty(textType)) {
							td2_type = textType;
						}
						if (StringUtil.isNotEmpty(text)) {
							td2_text = text;
						}
					}
				}
			}
			if (colspan == 2) {
				buffer.append("<tr><td class='billtable_center' colspan='4' ")
						.append("style='color:")
						.append(cr)
						.append(";text-align:")
						.append(al)
						.append(";font-size:")
						.append(fontsize * 14)
						.append("px;'>")
						.append(StringUtil.isNotEmpty(td1_text) ? (td1_text + "&nbsp;&nbsp;")
								: "").append("</td></tr>");
				;
			} else if (colspan == 1) {
				buffer.append("<tr><td class='billtable_td_left' ")
						.append("textType='").append(td2_type).append("' ")
						.append("id='").append(td1_id).append("' ")
						.append("id='").append(td2_id).append("' ")
						.append("style='color:").append(cr)
						.append(";text-align:").append(al)
						.append(";font-size:").append(fontsize * 14)
						.append("px;'>").append(td1_text).append("</td>")
						.append("<td class='billtable_td_colon'>")
						.append(StringUtil.isNotEmpty(td1_text) ? ":" : "")
						.append("</td>")
						.append("<td class='billtable_td_right' ")
						.append("contenteditable='false' ")
						.append("textType='").append(td2_type).append("' ")
						.append("id='").append(td2_id).append("' ")
						.append("style='color:").append(cr)
						.append(";text-align:").append(al)
						.append(";font-size:").append(fontsize * 14)
						.append("px;'>").append(td2_text)
						.append("</td><td style='width:4px;'></tr>");
			}
		}
		buffer.append("</table>");
		if (link != null) {
			buffer.append("</a>");
		}
		return buffer.toString();
	}

	// 待审批
	public String createTable3(IMTable table, IMFieldsCollection collection,
			String tablename) {
		StringBuffer buffer = new StringBuffer();
		int row = 0;
		int column = 0;
		if (table != null) {
			row = table.getRowCount();
			column = table.getColCount();
		}
		if (row <= 0 || column <= 0) {
			buffer.append("<table></table>");
			return buffer.toString();
		}

		buffer.append("<table width='100%' border='0' cellspacing='0' >");
		// 行
		for (int i = 0; i < row; i++) {
			// 列
			int colspan = 1;
			String td1_id = "";
			String td1_text = "";
			String td1_type = "";
			String td2_id = "";
			String td2_text = "";
			String td2_type = "";
			String al = "";
			String cr = "";
			double fontsize = 1;
			for (int j = 0; j < column; j++) {
				if (j > 1) {
					continue;
				}
				String id = "";
				IMTableCell cell = table.getCell(i, j);
				String text = cell.getTitle();
				String textType = "";
				if (StringUtil.isNotEmpty(tablename)
						&& StringUtil.isNotEmpty(text)
						&& text.indexOf("{#") > -1 && text.indexOf("}") > -1
						&& text.indexOf("{#") < text.indexOf("}")) {
					id = text.substring(text.indexOf("{#") + 2,
							text.indexOf("}"));
				}
				if (collection != null && text != null) {
					Pattern p = Pattern.compile("\\{#([^\\}]+)\\}");
					Matcher m = p.matcher(text);
					while (m.find()) {
						String value = WXFieldUtil.getFieldValueByName(
								m.group(1), collection);
						String valueType = WXFieldUtil.getFieldValueType(
								m.group(1), collection);
						text = (StringUtil.isNotEmpty(value) ? text.replace(
								m.group(), value) : "");
						textType = (StringUtil.isNotEmpty(valueType) ? valueType
								: "");
					}
				}
				int align = cell.getStyle().getTextAlignment();
				int color = cell.getStyle().getTextColor();
				fontsize = 1 + cell.getStyle().getFontSize() * 0.1;

				switch (align) {
				case 1 << 14: {
					al = "left";
					break;
				}
				case 1 << 24: {
					al = "center";
					break;
				}
				case 1 << 17: {
					al = "right";
					break;
				}
				}
				switch (color) {
				case 0x000000: {
					cr = "#000000";
					break;
				}
				case 0x265717: {
					cr = "#006600";
					break;
				}
				case 0xe47f1c: {
					cr = "#FF9900";
					break;
				}
				case 0x00ff00: {
					cr = "#66FF66";
					break;
				}
				case 0x0000ff: {
					cr = "#0000FF";
					break;
				}
				case 0xffffff: {
					cr = "#FFFFFF";
					break;
				}
				case 0x81b709: {
					cr = "#99CC00";
					break;
				}
				case 0x595959: {
					cr = "#707070";
					break;
				}
				case 0x979797: {
					cr = "#C0C0C0";
					break;
				}
				}
				if (StringUtil.isEmpty(text)) {
					text = "";
				}
				if (j == 0) {
					if (cell.getRowSpan() > 1) {
						colspan = 2;
					}
					if (StringUtil.isNotEmpty(id)) {
						td1_id = id;
					}
					if (StringUtil.isNotEmpty(textType)) {
						td1_type = textType;
					}
					if (StringUtil.isNotEmpty(text)) {
						td1_text = text;
					}
				} else if (j == 1) {
					if (colspan == 2) {
						continue;
					} else {
						if (StringUtil.isNotEmpty(id)) {
							td2_id = id;
							td1_id = id + "_TITLE";
						}
						if (StringUtil.isNotEmpty(textType)) {
							td2_type = textType;
						}
						if (StringUtil.isNotEmpty(text)) {
							td2_text = text;
						}
					}
				}
			}
			if (colspan == 2) {
				buffer.append("<tr><td class='")
						.append("billtable_center ")
						.append(td1_id)
						.append("' ")
						.append("RECID='")
						.append(collection.getId())
						.append("' ")
						.append("colspan='4' style='color:")
						.append(cr)
						.append(";")
						.append("text-align:")
						.append(al)
						.append(";font-size:")
						.append(fontsize * 14)
						.append("px;'>")
						.append(StringUtil.isNotEmpty(td1_text) ? (td1_text + "&nbsp;&nbsp;")
								: "").append("</td></tr>");
				;
			} else if (colspan == 1) {
				buffer.append(
						"<tr><td style='padding:5px 5px;line-height:20px;'><span class='billtable_td_left ")
						.append(td1_id)
						.append("' ")
						.append("TextType='")
						.append(td2_type)
						.append("' ")
						.append("RECID='")
						.append(collection.getId())
						.append("' ")
						.append("BData='")
						.append(td1_text)
						.append("' ")
						.append("style='color:")
						.append(cr)
						.append("; ")
						.append("text-align:")
						.append(al)
						.append(";font-size:")
						.append(fontsize * 14)
						.append("px;'>:&nbsp;&nbsp;")
						.append(td1_text)
						.append("< :&nbsp;&nbsp;  /span>")
						.append("<span class='billtable_td_colon'>")
						.append(StringUtil.isNotEmpty(td1_text) ? ":&nbsp;&nbsp;"
								: "")
						.append("</span>")
						.append("<span class='billtable_td_right ")
						.append(td2_id)
						.append("' ")
						.append("RECID='")
						.append(collection.getId())
						.append("' ")
						.append("BData='")
						.append(td2_text)
						.append("' ")
						.append("contenteditable='false' TextType='")
						.append(td2_type)
						.append("' ")
						.append("style='color:")
						.append(cr)
						.append("; ")
						.append("text-align:")
						.append(al)
						.append(";font-size:")
						.append(fontsize * 14)
						.append("px;'>")
						.append(td2_text)
						.append("</span><span style='width:4px;'></span></td></tr>");
			}
		}
		buffer.append("</table>");
		return buffer.toString();
	}

	// 已完成
	public String createTabled3(IMShowTemplate showTemplate,
			IMFieldsCollection collection, String link) {
		StringBuffer buffer = new StringBuffer();
		if (showTemplate == null) {
			buffer.append("<table></table>");
			return buffer.toString();
		}
		IMTable table = showTemplate.getTable();
		int row = 0;
		int column = 0;
		if (table != null) {
			row = table.getRowCount();
			column = table.getColCount();
		}
		if (row <= 0 || column <= 0) {
			buffer.append("<table></table>");
			return buffer.toString();
		}
		if (link != null) {
			buffer.append("<a data-ajax='false' class='table_a_label' href='")
					.append(link).append("'>");
		}

		buffer.append("<table width='100%' border='0' cellspacing='0'>");
		// 行
		for (int i = 0; i < 6; i++) {
			if (row < 6 && i >= row) {
				buffer.append("<tr><td colspan='4' style='height:22px;'></td></tr>");
				continue;
			}
			// 列
			int colspan = 1;
			String td1_id = "";
			String td1_text = "";
			String td1_type = "";
			String td2_id = "";
			String td2_text = "";
			String td2_type = "";
			String al = "";
			String cr = "";
			double fontsize = 1;
			for (int j = 0; j < column; j++) {
				if (j > 1) {
					continue;
				}
				IMTableCell cell = table.getCell(i, j);
				String text = cell.getTitle();
				String textType = "";
				if (collection != null && text != null) {
					Pattern p = Pattern.compile("\\{#([^\\}]+)\\}");
					Matcher m = p.matcher(text);
					while (m.find()) {
						String value = WXFieldUtil.getFieldValueByName(
								m.group(1), collection);
						String valueType = WXFieldUtil.getFieldValueType(
								m.group(1), collection);
						text = (StringUtil.isNotEmpty(value) ? text.replace(
								m.group(), value) : "");
						textType = (StringUtil.isNotEmpty(valueType) ? valueType
								: "");
					}
				}
				int align = cell.getStyle().getTextAlignment();
				int color = cell.getStyle().getTextColor();
				fontsize = 1 + cell.getStyle().getFontSize() * 0.1;

				switch (align) {
				case 1 << 14: {
					al = "left";
					break;
				}
				case 1 << 24: {
					al = "center";
					break;
				}
				case 1 << 17: {
					al = "right";
					break;
				}
				}
				switch (color) {
				case 0x000000: {
					cr = "#000000";
					break;
				}
				case 0x265717: {
					cr = "#006600";
					break;
				}
				case 0xe47f1c: {
					cr = "#FF9900";
					break;
				}
				case 0x00ff00: {
					cr = "#66FF66";
					break;
				}
				case 0x0000ff: {
					cr = "#0000FF";
					break;
				}
				case 0xffffff: {
					cr = "#FFFFFF";
					break;
				}
				case 0x81b709: {
					cr = "#99CC00";
					break;
				}
				case 0x595959: {
					cr = "#707070";
					break;
				}
				case 0x979797: {
					cr = "#C0C0C0";
					break;
				}
				}

				if (StringUtil.isEmpty(text)) {
					text = "";
				}
				if (j == 0) {
					if (cell.getRowSpan() > 1) {
						colspan = 2;
					}
					if (StringUtil.isNotEmpty(textType)) {
						td1_type = textType;
					}
					if (StringUtil.isNotEmpty(text)) {
						td1_text = text;
					}
				} else if (j == 1) {
					if (colspan == 2) {
						continue;
					} else {
						if (StringUtil.isNotEmpty(textType)) {
							td2_type = textType;
						}
						if (StringUtil.isNotEmpty(text)) {
							td2_text = text;
						}
					}
				}
			}
			if (colspan == 2) {
				buffer.append("<tr><td class='billtable_center' colspan='4' ")
						.append("style='color:")
						.append(cr)
						.append(";text-align:")
						.append(al)
						.append(";font-size:")
						.append(fontsize * 14)
						.append("px;'>")
						.append(StringUtil.isNotEmpty(td1_text) ? (td1_text + "&nbsp;&nbsp;")
								: "").append("</td></tr>");
				;
			} else if (colspan == 1) {
				buffer.append(
						"<tr><td style='padding:5px 5px;line-height:20px;'><span class='billtable_td_left ")
						.append(td1_id)
						.append("' ")
						.append("TextType='")
						.append(td2_type)
						.append("' ")
						.append("RECID='")
						.append(collection.getId())
						.append("' ")
						.append("BData='")
						.append(td1_text)
						.append("' ")
						.append("style='color:")
						.append(cr)
						.append("; ")
						.append("text-align:")
						.append(al)
						.append(";font-size:")
						.append(fontsize * 14)
						.append("px;'>")
						.append(td1_text)
						.append("</span>")
						.append("<span class='billtable_td_colon'>")
						.append(StringUtil.isNotEmpty(td1_text) ? ":&nbsp;&nbsp;"
								: "")
						.append("</span>")
						.append("<span class='billtable_td_right ")
						.append(td2_id)
						.append("' ")
						.append("RECID='")
						.append(collection.getId())
						.append("' ")
						.append("BData='")
						.append(td2_text)
						.append("' ")
						.append("contenteditable='false' TextType='")
						.append(td2_type)
						.append("' ")
						.append("style='color:")
						.append(cr)
						.append("; ")
						.append("text-align:")
						.append(al)
						.append(";font-size:")
						.append(fontsize * 14)
						.append("px;'>")
						.append(td2_text)
						.append("</span><span style='width:4px;'></span></td></tr>");
			}
		}
		buffer.append("</table>");
		if (link != null) {
			buffer.append("</a>");
		}
		return buffer.toString();
	}

}