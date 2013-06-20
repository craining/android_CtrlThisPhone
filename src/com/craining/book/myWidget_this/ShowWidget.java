package com.craining.book.myWidget_this;

import java.util.ArrayList;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.craining.book.CtrlThisPhone.UsefullVerbs;
import com.craining.book.CtrlThisPhone.DoThings.DoSomeThings;

public class ShowWidget extends Activity {

	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	public ShowWidget() {
		super();
	}

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setResult(RESULT_CANCELED);
		// 此处可以添加一个等待对话框、、、不显示什么也可、、

		// playingMusicName = getPlayingName();// 获得正在播放的音乐名称

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if ( extras != null ) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if ( mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
			DoSomeThings.DisplayToast(ShowWidget.this, "添加Widget失败！");
			finish();
		} else if (  toShowWidget() ) {
			// 显示Widget成功！
			DoSomeThings.DisplayToast(ShowWidget.this, "添加Widget成功！");
			finish();
		} 

	}

	// // 获得Widget显示内容
	static String loadTitlePref(Context context, int appWidgetId) {
		
		if( UsefullVerbs.ACCEPT.exists() ) {
			UsefullVerbs.strShowOnWidget = "接受协助";
			UsefullVerbs.acceptCtrl = true;
		} else {
			UsefullVerbs.strShowOnWidget = "拒绝协助";
			UsefullVerbs.acceptCtrl = false;
		}
		
		return UsefullVerbs.strShowOnWidget;
	}

	static void deleteTitlePref(Context context, int appWidgetId) {
		// 删除桌面部件时调用

	}

	static void loadAllTitlePrefs(Context context, ArrayList<Integer> appWidgetIds, ArrayList<String> texts) {
		// 每个桌面部件要显示的内容

	}

	private boolean toShowWidget() {
		final Context context = ShowWidget.this;

		// 取得AppWidgetManager实例
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		// 更新AppWidget
		MyAppWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId, UsefullVerbs.strShowOnWidget);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);

		return true;
	}

}
