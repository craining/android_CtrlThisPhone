package com.craining.book.myWidget_this;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.craining.book.CtrlThisPhone.UsefullVerbs;

public class ChangeStateReceive extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		// 通过BroadcastReceiver来更新AppWidget
		Log.e("", "ChangeStateReceive Doing");
		if ( UsefullVerbs.acceptCtrl ) {
			UsefullVerbs.acceptCtrl = false;
			UsefullVerbs.strShowOnWidget = "拒绝协助";
			if( UsefullVerbs.ACCEPT.exists() ) {
				UsefullVerbs.ACCEPT.delete();
			}
		} else {
			UsefullVerbs.acceptCtrl = true;
			UsefullVerbs.strShowOnWidget = "接受协助";
			UsefullVerbs.ACCEPT.mkdir();
		}

		AppWidgetManager gm = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = MyAppWidgetProvider.use_appWidgetIds;
		
		// 更新所有AppWidget
		final int N = appWidgetIds.length;
		for (int i = 0;i < N;i++) {
			MyAppWidgetProvider.updateAppWidget(context, gm, appWidgetIds[i], UsefullVerbs.strShowOnWidget);
		}
	}

}
