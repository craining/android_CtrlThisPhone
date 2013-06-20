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
	 * 用于周期更新桌面的Widget （因为从1.5之后的版本好像android:updatePeriodMillis就失效了，
	 * 不能实现更新，因此开辟线程进行更新）
	 */
	Handler widgetUpdateThread = new Handler();
	/* 速度控制参数(单位豪秒) */
	private int delay = 1000;
	private Context use_Context;
	private AppWidgetManager use_AppWidgetManager;
	public static int[] use_appWidgetIds;
	private final static File widgetExist = new File(UsefullVerbs.SAVE_FIRSTPATH + "existWidget/"); 
	
	// private boolean widgetDel = false;

	// 周期更新时调用
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

	// 当桌面部件删除时调用
	public void onDeleted(Context context, int[] appWidgetIds) {
		// 删除appWidget
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			ShowWidget.deleteTitlePref(context, appWidgetIds[i]);
		}
	}

	// 当AppWidgetProvider提供的第一个部件被创建时调用
	public void onEnabled(Context context) {
		 //写入文件，标记存在Widget
		Log.e("WIDGET", "++++++++++++++++++++++++++++++++++++++++============> Oncreated");
		widgetExist.mkdir();
		widgetUpdateThread.postDelayed(mTasks, delay);//开启更新Widget线程
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("com.craining.book.CtrlThisPhone", ".MyBroadcastReceiver"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
		
	}

	// 当AppWidgetProvider提供的最后一个部件被删除时调用
	public void onDisabled(Context context) {
		 //写入文件，标记Widget不存在了
		Log.e("WIDGET", "++++++++++++++++++++++++++++++++++++++++============> OnDelAll");
		widgetExist.delete();
		widgetUpdateThread.removeCallbacks(mTasks);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName("com.craining.book.CtrlThisPhone", ".MyBroadcastReceiver"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		
	}

	// 更新Widget
	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String titlePrefix) {

		Log.e("WIDGET", "++++++++++++++++++++++++++++++++++++++++============> UPDATE");
		// 构建RemoteViews对象来对桌面部件进行更新
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider);
		// 更新文本内容，指定布局的组件
		views.setTextViewText(R.id.appwidget_text, titlePrefix);

		linkButtons(context, views, true);// 添加Widget中的按钮点击事件

		// 将RemoteViews的更新传入AppWidget进行更新
		appWidgetManager.updateAppWidget(appWidgetId, views);

	}

	private static void linkButtons(Context context, RemoteViews views, boolean playerActive) {

//		views.setViewVisibility(R.id.img_changestate, View.GONE);
		
		// 此为点击按钮的事件响应
		Intent intent1 = new Intent(context, ChangeStateReceive.class);
		PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, 0);
		
		Intent intent2 = new Intent(context, SendSecretMsg.class);
		PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
		
		views.setOnClickPendingIntent(R.id.img_changestate, pendingIntent2);//改变状态
		views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent1);
		views.setOnClickPendingIntent(R.id.layout_widget, pendingIntent2);//发送加密信息
		
		/*
		 * 可以添加上一曲、播放与暂停、下一曲的按钮，对下面的代码进行修改
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
	 * 控制速度
	 * */
	public Runnable mTasks = new Runnable() {
		public void run() {
			
			Log.e("STARTED_WIDGET_UPDATE","++++++++++++++++START+++++++============> ");
			
			if( widgetExist.exists() ) {
				//如果存在widget时则更新

			} 

		}
	};


}
