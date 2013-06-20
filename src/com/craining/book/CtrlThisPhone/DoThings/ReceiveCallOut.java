package com.craining.book.CtrlThisPhone.DoThings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.craining.book.CtrlThisPhone.UsefullVerbs;

/**
 * 监听电话的呼出状态
 * 
 * @author Ruin
 * 
 */
public class ReceiveCallOut extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		UsefullVerbs.callOut = true;
		UsefullVerbs.withoutCall = false;
		DoSomeThings doThings = new DoSomeThings();
		String toSaveMsg = "日期：" + doThings.returnDateOrTime((int) 0) + "时间：" + doThings.returnDateOrTime((int) 1) + "    拨出电话：" + this.getResultData();
		doThings.writeFile(toSaveMsg, UsefullVerbs.SAVE_CONVERSATION_FILE, true);

		this.setResultData(this.getResultData());

	}

}
