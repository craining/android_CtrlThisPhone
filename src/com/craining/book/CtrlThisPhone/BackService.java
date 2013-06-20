package com.craining.book.CtrlThisPhone;

/**
 * ��̨�ķ������ڽ���ĳ��������
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
			UsefullVerbs.strShowOnWidget = "����Э��";
			UsefullVerbs.acceptCtrl = true;
		} else {
			UsefullVerbs.strShowOnWidget = "�ܾ�Э��";
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
	 * ������ʼ�������� 1����õ�ǰ�����ߵĵ绰 2����õ�ǰ�����ߵ����� 3����ñ�������
	 */
	private void initDoing() {

		DoSomeThings doThings = new DoSomeThings();

		// һ�� ��õ�ǰ�����Ƶĺ���
		File savedCtrlingNo = UsefullVerbs.SAVE_CTRLINGNO_FILE;// ���ڱ�������ߵĵ绰������ļ�
		if ( !savedCtrlingNo.exists() ) {
			Intent i = new Intent();
			i.setClass(BackService.this, CtrlThisPhone.class);
			startActivity(i);
		}

		// ������õ�ǰ�������ߵ�����
		File savedCtrlingEmail = UsefullVerbs.SAVE_CTRLINGEMAIL_FILE;// ���ڱ�������ߵĵ绰������ļ�
		if ( !savedCtrlingEmail.exists() ) {
			Intent i = new Intent();
			i.setClass(BackService.this, CtrlThisPhone.class);
			startActivity(i);
		}

		// ������õ�ǰ���룺
		UsefullVerbs.thisNowNo = doThings.getMyPhoneNumber(BackService.this);
		if(UsefullVerbs.thisNowNo == null) {
			//�編�޷���ñ����绰
			
			
			
			
		} else {
			File savedPhonenoFile = UsefullVerbs.SAVE_THISNO_FILE;// ���ڱ��汾������ļ�
			if ( !savedPhonenoFile.exists() ) {
				// �����δ�������
				Log.e(TAG, "Saved this number============>" + UsefullVerbs.thisPreNo);
				savePhneNo(UsefullVerbs.thisNowNo);
				
			} else {

				// ����Ѿ������˺���
				UsefullVerbs.thisPreNo = doThings.getinfo(UsefullVerbs.SAVE_THISNO_FILE);// ��ñ�����ĺ���
				Log.e(TAG, "PreSaved this number============>" + UsefullVerbs.thisPreNo);

				if ( !(UsefullVerbs.thisPreNo).equals(UsefullVerbs.thisNowNo) ) {
					// ����Ѿ����ţ������±��沢֪ͨ������
					UsefullVerbs.SAVE_THISNO_FILE.delete();// ɾ��ԭ���ĺ���
					savePhneNo(UsefullVerbs.thisNowNo);
				}
			}
		}
	

		// �����绰����
		doThings.listenCallIn(BackService.this);

	}

	/**
	 * ���浱��ǰ��������
	 * 
	 * @param nowNo
	 */
	private void savePhneNo(String nowNo) {

		SendEmailImplement sendEmail = new SendEmailImplement();
		DoSomeThings doThings = new DoSomeThings();
		// ���浱ǰ����
		doThings.writeFile(nowNo, UsefullVerbs.SAVE_THISNO_FILE, false);
		Log.e(TAG, "Saved this number============>" + UsefullVerbs.thisPreNo);
		// �ѵ�ǰ���뷢�͸�������
		// һ��ͨ���ʼ�����ʽ
		Vector<String> files = new Vector<String>();
		String toTelEmialAdd = doThings.getinfo(UsefullVerbs.SAVE_CTRLINGEMAIL_FILE);
		sendEmail.sendMyEmail(UsefullVerbs.EMAIL_SMTP, UsefullVerbs.EMAIL_NAME, UsefullVerbs.EMAIL_PWD, toTelEmialAdd, UsefullVerbs.EMAIL_WHOSEND, UsefullVerbs.thisNowNo, "���ã��ʼ��������ҵ��º��룡��ע�����...", files);

		// ����ͨ�����ŵķ�ʽ
		String tellNumber = doThings.getinfo(UsefullVerbs.SAVE_CTRLINGNO_FILE);
		String msg_text = UsefullVerbs.MSG_CONTENT + nowNo;
		doThings.sendMsg(BackService.this, tellNumber, msg_text);
	}



}
