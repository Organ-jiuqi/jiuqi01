package com.jiuqi.mt2.dna.mobile.wxapproval.servlet;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.da.DBCommand;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.da.RecordSetField;
import com.jiuqi.dna.core.da.RecordSetFieldContainer;
import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.Application;
import com.jiuqi.dna.core.spi.application.ContextSPI;
import com.jiuqi.dna.core.spi.application.Session;
import com.jiuqi.util.json.JSONArray;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetBaseDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRedirect(request, response);
	}

	private void doRedirect(HttpServletRequest req, HttpServletResponse resp) {
		List lx = new ArrayList();
		ContextSPI context = AppUtil.getDefaultApp().getSystemSession().newContext(false);
		try {
			req.setCharacterEncoding("utf-8");
			resp.setCharacterEncoding("utf-8");
			String keyword = req.getParameter("keyword");
			String keyword1 = req.getParameter("keyword1");
			String tablename = req.getParameter("tablename");
			if (keyword.contains("-"))
				keyword = keyword.substring(0, keyword.indexOf("-"));

			if (tablename.trim().equals("MD_STAFF"))
				getBaseData(context, tablename, keyword, lx);
			else if (tablename.trim().equals("MD_GANGWNLSZJB"))
				getBaseData1(context, tablename, keyword, keyword1, lx);
			else {
				getBaseData2(context, tablename, keyword, lx);
			}

			JSONArray json = new JSONArray();
			if (lx.size() > 0)
				for (Iterator localIterator = lx.iterator(); localIterator.hasNext();) {
					String pLog = (String) localIterator.next();
					json.put(pLog);
				}

			resp.getWriter().write(json.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getBaseData(Context context, String tablename, String param, List<String> paramlist) {
		StringBuffer getEnclosureSql = new StringBuffer();
		if ((param.trim().equals("")) || (param == null))
			getEnclosureSql.append("define query getEnclosures() \n");
		else
			getEnclosureSql.append("define query getEnclosures(@stdname string) \n");

		getEnclosureSql.append("begin \n");
		getEnclosureSql.append("  select t.stdname as name, \n");
		getEnclosureSql.append(" t.stdcode as code,b.stdname as bm  from   \n");
		getEnclosureSql.append(tablename);
		getEnclosureSql.append(" as t \n");
		getEnclosureSql.append("  left join md_ryzt as  a  on  t.ryzt =a.recid    \n");
		getEnclosureSql.append("  left join md_department  as  b  on  t.DEPARTMENTID =b.recid    \n");
		getEnclosureSql.append("where 1 = 1  and t.STARTFLAG=1  \n");
		if (!(param.trim().equals("")))
			getEnclosureSql.append("  and    t.stdname  like @stdname  \n");

		getEnclosureSql.append("     and a.stdcode like  '01%'  \n");
		getEnclosureSql.append(" order by   t.stdname      \n");
		getEnclosureSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(getEnclosureSql);
		if (!(param.trim().equals("")))
			dbCommand.setArgumentValues(new Object[] { "%" + param + "%" });

		RecordSet recordSet = dbCommand.executeQuery();
		while (recordSet.next())
			paramlist.add(((RecordSetField) recordSet.getFields().get(0)).getString() + "-"
					+ ((RecordSetField) recordSet.getFields().get(2)).getString() + "-"
					+ ((RecordSetField) recordSet.getFields().get(1)).getString());

		dbCommand.unuse();
	}

	private void getBaseData1(Context context, String tablename, String param, String param1, List<String> paramlist) {
		StringBuffer getEnclosureSql = new StringBuffer();
		String param2 = null;

		if (param1.trim().equals("ZDXDJ"))
			param2 = "00811";

		if (param1.trim().equals("GTNLDJ"))
			param2 = "00306";

		if (param1.trim().equals("JJWTDJ"))
			param2 = "00304";

		if (param1.trim().equals("ZRXDJ"))
			param2 = "00701";

		if (param1.trim().equals("GWSRDJ"))
			param2 = "00809";

		if (param1.trim().equals("JHZXDJ"))
			param2 = "00405";

		if (param1.trim().equals("FWDJ"))
			param2 = "00806";

		if (param1.trim().equals("HZDJ"))
			param2 = "00808";

		if ((param.trim().equals("")) || (param == null))
			getEnclosureSql.append("define query getEnclosures(@dingy string) \n");
		else
			getEnclosureSql.append("define query getEnclosures(@stdname string,@dingy string) \n");

		getEnclosureSql.append("begin \n");
		getEnclosureSql.append("  select t.stdname as name, \n");
		getEnclosureSql.append(" t.stdcode as code,t.dingy as dingy  from   \n");
		getEnclosureSql.append(tablename);
		getEnclosureSql.append(" as t \n");

		getEnclosureSql.append(" left join  MD_GANGWNLSZ  as b   on t.GANGWNLSZ =b.recid      \n");

		getEnclosureSql.append("where 1 = 1  and t.STARTFLAG=1   \n");
		if (!(param.trim().equals("")))
			getEnclosureSql.append("   and   t.stdname  like @stdname  \n");

		getEnclosureSql.append("   and   b.stdcode   = @dingy  \n");
		getEnclosureSql.append(" order by   t.stdcode      \n");
		getEnclosureSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(getEnclosureSql);
		if (!(param.trim().equals("")))
			dbCommand.setArgumentValues(new Object[] { "%" + param + "%", param2 });
		else
			dbCommand.setArgumentValues(new Object[] { param2 });

		RecordSet recordSet = dbCommand.executeQuery();
		while (recordSet.next())
			paramlist.add(((RecordSetField) recordSet.getFields().get(0)).getString() + "-"
					+ ((RecordSetField) recordSet.getFields().get(1)).getString() + "-"
					+ ((RecordSetField) recordSet.getFields().get(2)).getString());

		dbCommand.unuse();
	}

	private void getBaseData2(Context context, String tablename, String param, List<String> paramlist) {
		StringBuffer getEnclosureSql = new StringBuffer();
		if ((param.trim().equals("")) || (param == null))
			getEnclosureSql.append("define query getEnclosures() \n");
		else
			getEnclosureSql.append("define query getEnclosures(@stdname string) \n");

		getEnclosureSql.append("begin \n");
		getEnclosureSql.append("  select t.stdname as name, \n");
		getEnclosureSql.append(" t.stdcode as code  from   \n");
		getEnclosureSql.append(tablename);
		getEnclosureSql.append(" as t \n");
		getEnclosureSql.append("where 1 = 1   and t.STARTFLAG=1  \n");

		if (tablename.trim().equals("MD_COSTOBJECT")) {
			getEnclosureSql
					.append(" and (t.JBXX_XIANGMZT is not null or t.XS_XMZT is not null)    and t.JBXX_XIANGMZT not in('2CC4894940000001F78ABC40C06668E6','318E5724200000017EC574A6C8D90202')             \n");
		}

		if (!(param.trim().equals(""))) {
			getEnclosureSql.append("   and   t.stdname  like @stdname  \n");
		}

		getEnclosureSql.append(" order by   t.stdname      \n");
		getEnclosureSql.append("end \n");
		DBCommand dbCommand = context.prepareStatement(getEnclosureSql);
		if (!(param.trim().equals("")))
			dbCommand.setArgumentValues(new Object[] { "%" + param + "%" });

		RecordSet recordSet = dbCommand.executeQuery();
		while (recordSet.next())
			paramlist.add(((RecordSetField) recordSet.getFields().get(0)).getString() + "-"
					+ ((RecordSetField) recordSet.getFields().get(1)).getString());

		dbCommand.unuse();
	}
}