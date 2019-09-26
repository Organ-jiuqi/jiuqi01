package com.jiuqi.mt2.dna.mobile.wxapproval.common;

import java.text.SimpleDateFormat;
import java.util.List;

import com.jiuqi.dna.bap.authority.intf.facade.FUser;
import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.workflow.engine.object.ParticipantObject;
import com.jiuqi.dna.workflow.intf.facade.IWorkItem;
import com.jiuqi.xlib.utils.StringUtil;

public class WXApprovalCommon {
	
	// 工作流状态已经结束 返回界面提示信息
	public static String stateComplete(Context context,IWorkItem workItem ){
		List<ParticipantObject> ParticipantObjectS = workItem.getParticipants();
		String userName = "";
		for (ParticipantObject Participant : ParticipantObjectS) {
			if (Participant.getAction() != 0) {
				String userGuid = Participant.getUserguid();
				if (!StringUtil.isEmpty(userGuid)) {
					FUser user = context.find(FUser.class,GUID.valueOf(userGuid));
					userName = user.getTitle();
				}
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = format.format(workItem.getFinishTime().getTime());
		String action = "审批!";
		if (workItem.getActionId() == 1) {
			action = "同意!";
		} else if (workItem.getActionId() == 99) {
			action = "驳回!";
		}
		return currentTime+"<br/>该工作项已被"+userName+action;	
	}
}
