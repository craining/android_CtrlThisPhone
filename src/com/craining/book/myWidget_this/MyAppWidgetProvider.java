package com.craining.book.myWidget_this;

import java.io.File;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.craining.book.CtrlThisPhone.R;
import com.craining.book.CtrlThisPhone.UsefullVerbs;
import com.craining.book.CtrlThisPhone.DoThings.SendSecretMsg;

public class MyAppWidgetProvider extends AppWidgetProvider {

	/*
	 * �������ڸ��������Widget ����Ϊ��1.5֮��İ汾����android:updatePeriodMillis��ʧЧ�ˣ�
	 * ����ʵ�ָ��£���˿����߳̽��и��£�
	 */
	Handler widgetUpdateThread = new Handler();
	/* �ٶȿ��Ʋ���(��λ����) */
	private int delay = 1000;
	private Context use_Context;
	private AppWidgetManager use_AppWidgetManager;
	public static int[] use_appWidgetIds;
	private final static File widgetExist = new File(UsefullVerbs.SAVE_FIRSTPATH + "existWidget/"); 
	
	// private boolean widgetDel = false;

	// ���ڸ���ʱ����
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		use_Context = context;
		use_AppWidgetManager = appWidgetManager;
		use_appWidgetIds = appWidgetIds;
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			String titlePrefix = ShowWidget.loadTitlePref(context, appWidgetId);
			updateAppWidget(context, appWidgetManager, appWidgetId, titlePrefix);
		}
	}

	// �����沿��ɾ��ʱ����
	public void onDeleted(Context context, int[] appWidgetIds) {
		// ɾ��appWidget
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			ShowWidget.deleteTitlePref(context, appWidgetIds[i]);
		}
	}

	// ��AppWidgetProvider�ṩ�ĵ�һ������������ʱ����
	public void onEnabled(Context context) {
		 //д���ļ�����Ǵ���Widget
		Log.e("WIDGET", "++++++++++++++++++++++++++++++++++++++++============> Oncreated");
		widgetExist.mkdir();
		widgetUpdateThread.postDelayed(mTasks, delay);//��������Widget�߳�
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("com.craining.book.CtrlThisPhone", ".MyBroadcastReceiver"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
		
	}

	// ��AppWidgetProvider�ṩ�����һ��������ɾ��ʱ����
	public void onDisabled(Context context) {
		 //д���ļ������Widget��������
		Log.e("WIDGET", "++++++++++++++++++++++++++++++++++++++++============> OnDelAll");
		widgetExist.delete();
		widgetUpdateThread.removeCallbacks(mTasks);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("com.craining.book.CtrlThisPhone", ".MyBroadcastReceiver"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		
	}

	// ����Widget
	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String titlePrefix) {

		Log.e("WIDGET", "++++++++++++++++++++++++++++++++++++++++============> UPDATE");
		// ����RemoteViews�����������沿�����и���
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider);
		// �����ı����ݣ�ָ�����ֵ����
		views.setTextViewText(R.id.appwidget_text, titlePrefix);

		linkButtons(context, views, true);// ���Widget�еİ�ť����¼�

		// ��RemoteViews�ĸ��´���AppWidget���и���
		appWidgetManager.updateAppWidget(appWidgetId, views);

	}

	private static void linkButtons(Context context, RemoteViews views, boolean playerActive) {

//		views.setViewVisibility(R.id.img_changestate, View.GONE);
		
		// ��Ϊ�����ť���¼���Ӧ
		Intent intent1 = new Intent(context, ChangeStateReceive.class);
		PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, 0);
		
		Intent intent2 = new Intent(context, SendSecretMsg.class);
		PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
		
		views.setOnClickPendingIntent(R.id.img_changestate, pendingIntent2);//�ı�״̬
		views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent1);
		views.setOnClickPendingIntent(R.id.layout_widget, pendingIntent2);//���ͼ�����Ϣ
		
		/*
		 * ���������һ������������ͣ����һ���İ�ť��������Ĵ�������޸�
		 * 
		 * final ComponentName serviceName = new ComponentName(context,
		 * MusicPlayer.class); intent = new
		 * Intent(MediaPlaybackService.TOGGLEPAUSE_ACTION);
		 * intent.setComponent(serviceName); pendingIntent =
		 * PendingIntent.getService(context, 0 , intent, 0 );
		 * views.setOnClickPendingIntent(R.id.control_play, pendingIntent);
		 * intent = new Intent(MediaPlaybackService.NEXT_ACTION);
		 * intent.setComponent(serviceName); pendingIntent =
		 * PendingIntent.getService(context, 0 , intent, 0 /);
		 * views.setOnClickPendingIntent(R.id.control_next, pendingIntent);
		 */
	}
	/**
	 * �����ٶ�
	 * */
	public Runnable mTasks = new Runnable() {
		public void run() {
			
			Log.e("STARTED_WIDGET_UPDATE","++++++++++++++++START+++++++============> ");
			
			if( widgetExist.exists() ) {
				//�������widgetʱ�����

			} 

		}
	};


}
