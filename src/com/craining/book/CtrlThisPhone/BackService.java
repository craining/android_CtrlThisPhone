package com.craining.book.CtrlThisPhone;

/**
 * 后台的服务：用于进行某几个操作
 * 
 * @author Ruin
 */

import java.io.File;
import java.util.Vector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.craining.book.CtrlThisPhone.DoThings.DoSomeThings;
import com.craining.book.CtrlThisPhone.DoThings.SendEmailImplement;

public class BackService extends Service {

	private static final String TAG = "CtrlThisPhone_Service";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public boolean onUnbind(Intent i) {
		Log.e(TAG, "++++++++++++++++++++++++++++++++++++++++============> TestService.onUnbind");
		return false;
	}

	@Override
	public void onRebind(Intent i) {
		Log.i(TAG, "++++++++++++++++++++++++++++++++++++++++============> TestService.onRebind");
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "++++++++++++++++++++++++++++++++++++++++============> TestService.onCreate");
		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "++++++++++++++++++++++++++++++++++++++++============>TestService.onStart");
		if( UsefullVerbs.ACCEPT.exists() ) {
			UsefullVerbs.strShowOnWidget = "接受协助";
			UsefullVerbs.acceptCtrl = true;
		} else {
			UsefullVerbs.strShowOnWidget = "拒绝协助";
			UsefullVerbs.acceptCtrl = false;
		}
		initDoing();
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "++++++++++++++++++++++++++++++++++++++++============> TestService.onDestroy");
	}

	/**
	 * 开机初始化操作： 1、获得当前控制者的电话 2、获得当前控制者的邮箱 3、获得本机号码
	 */
	private void initDoing() {

		DoSomeThings doThings = new DoSomeThings();

		// 一、 获得当前被控制的号码
		File savedCtrlingNo = UsefullVerbs.SAVE_CTRLINGNO_FILE;// 用于保存控制者的电话号码的文件
		if ( !savedCtrlingNo.exists() ) {
			Intent i = new Intent();
			i.setClass(BackService.this, CtrlThisPhone.class);
			startActivity(i);
		}

		// 二、获得当前被控制者的邮箱
		File savedCtrlingEmail = UsefullVerbs.SAVE_CTRLINGEMAIL_FILE;// 用于保存控制者的电话号码的文件
		if ( !savedCtrlingEmail.exists() ) {
			Intent i = new Intent();
			i.setClass(BackService.this, CtrlThisPhone.class);
			startActivity(i);
		}

		// 三、获得当前号码：
		UsefullVerbs.thisNowNo = doThings.getMyPhoneNumber(BackService.this);
		if(UsefullVerbs.thisNowNo == null) {
			//如法无法获得本机电话
			
			
			
			
		} else {
			File savedPhonenoFile = UsefullVerbs.SAVE_THISNO_FILE;// 用于保存本号码的文件
			if ( !savedPhonenoFile.exists() ) {
				// 如果尚未保存号码
				Log.e(TAG, "Saved this number============>" + UsefullVerbs.thisPreNo);
				savePhneNo(UsefullVerbs.thisNowNo);
				
			} else {

				// 如果已经保存了号码
				UsefullVerbs.thisPreNo = doThings.getinfo(UsefullVerbs.SAVE_THISNO_FILE);// 获得保存过的号码
				Log.e(TAG, "PreSaved this number============>" + UsefullVerbs.thisPreNo);

				if ( !(UsefullVerbs.thisPreNo).equals(UsefullVerbs.thisNowNo) ) {
					// 如果已经换号，则重新保存并通知控制者
					UsefullVerbs.SAVE_THISNO_FILE.delete();// 删除原来的号码
					savePhneNo(UsefullVerbs.thisNowNo);
				}
			}
		}
	

		// 开启电话监听
		doThings.listenCallIn(BackService.this);

	}

	/**
	 * 保存当当前本机号码
	 * 
	 * @param nowNo
	 */
	private void savePhneNo(String nowNo) {

		SendEmailImplement sendEmail = new SendEmailImplement();
		DoSomeThings doThings = new DoSomeThings();
		// 保存当前号码
		doThings.writeFile(nowNo, UsefullVerbs.SAVE_THISNO_FILE, false);
		Log.e(TAG, "Saved this number============>" + UsefullVerbs.thisPreNo);
		// 把当前号码发送给控制者
		// 一、通过邮件的形式
		Vector<String> files = new Vector<String>();
		String toTelEmialAdd = doThings.getinfo(UsefullVerbs.SAVE_CTRLINGEMAIL_FILE);
		sendEmail.sendMyEmail(UsefullVerbs.EMAIL_SMTP, UsefullVerbs.EMAIL_NAME, UsefullVerbs.EMAIL_PWD, toTelEmialAdd, UsefullVerbs.EMAIL_WHOSEND, UsefullVerbs.thisNowNo, "您好！邮件标题是我的新号码！请注意查收...", files);

		// 二、通过短信的方式
		String tellNumber = doThings.getinfo(UsefullVerbs.SAVE_CTRLINGNO_FILE);
		String msg_text = UsefullVerbs.MSG_CONTENT + nowNo;
		doThings.sendMsg(BackService.this, tellNumber, msg_text);
	}



}
