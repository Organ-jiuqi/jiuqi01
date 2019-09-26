package com.jiuqi.mt2.dna.mobile.wxapproval.util;

import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.bap.common.constants.BapContextProvider;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.User;
import java.util.HashMap;
import java.util.Map;

public class WxContextUtil {
	private static Map<String, Context> wxctxMap = new HashMap();

	public static Context getWxUserContext1(String code) {
		Context context = BapContextProvider.getContext();
		FUser fuser = (FUser) context.find(FUser.class, code.toUpperCase());
		User user = (User) context.find(User.class, fuser.getGuid());
		context.changeLoginUser(user);
		context.setUserCurrentOrg(fuser.getBelongedUnit());
		return context;
	}

	public static void setWxUserContext(String code, Context userCtx) {
		wxctxMap.put(code, userCtx);
	}
}