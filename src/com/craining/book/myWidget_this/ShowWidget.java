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
		// �˴��������һ���ȴ��Ի��򡢡�������ʾʲôҲ�ɡ���

		// playingMusicName = getPlayingName();// ������ڲ��ŵ���������

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if ( extras != null ) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if ( mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
			DoSomeThings.DisplayToast(ShowWidget.this, "���Widgetʧ�ܣ�");
			finish();
		} else if (  toShowWidget() ) {
			// ��ʾWidget�ɹ���
			DoSomeThings.DisplayToast(ShowWidget.this, "���Widget�ɹ���");
			finish();
		} 

	}

	// // ���Widget��ʾ����
	static String loadTitlePref(Context context, int appWidgetId) {
		
		if( UsefullVerbs.ACCEPT.exists() ) {
			UsefullVerbs.strShowOnWidget = "����Э��";
			UsefullVerbs.acceptCtrl = true;
		} else {
			UsefullVerbs.strShowOnWidget = "�ܾ�Э��";
			UsefullVerbs.acceptCtrl = false;
		}
		
		return UsefullVerbs.strShowOnWidget;
	}

	static void deleteTitlePref(Context context, int appWidgetId) {
		// ɾ�����沿��ʱ����

	}

	static void loadAllTitlePrefs(Context context, ArrayList<Integer> appWidgetIds, ArrayList<String> texts) {
		// ÿ�����沿��Ҫ��ʾ������

	}

	private boolean toShowWidget() {
		final Context context = ShowWidget.this;

		// ȡ��AppWidgetManagerʵ��
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		// ����AppWidget
		MyAppWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId, UsefullVerbs.strShowOnWidget);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);

		return true;
	}

}
