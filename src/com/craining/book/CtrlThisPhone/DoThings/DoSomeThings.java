package com.craining.book.CtrlThisPhone.DoThings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.craining.book.CtrlThisPhone.UsefullVerbs;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.FloatMath;
import android.util.Log;
import android.widget.Toast;

/**
 * �������������� 1����ñ������� 2�����Ͷ��� 3���绰��ͬ����״̬���еĲ��� 4�����GPS 5��java IO 6�����ϵͳ��ǰʱ��
 * 
 * @author Ruin
 * 
 */

public class DoSomeThings {

	private static final String TAG = "DoSomeThings";
	private static final String TEXT_ENCODING = "utf-8";

	/**
	 * ��ñ�������<br>
	 * �÷�: <br>
	 * getMyPhoneNumber( this );
	 * 
	 * @return ��������
	 */
	public String getMyPhoneNumber(Context context) {

		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return mTelephonyMgr.getLine1Number();
	}

	/**
	 * ��̨���Ͷ���<br>
	 * �÷�: <br>
	 * sendMsg( this );
	 * 
	 * @param mTelephonyMgr
	 * @param toWho
	 * @param msgText
	 */
	public void sendMsg(Context context, String toWho, String msgText) {

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(toWho, getMyPhoneNumber(context), msgText, null, null);
	}
	/**
	 * �����绰<br>
	 * �÷�: <br>
	 * private DoSomeThings doThings = new DoSomeThings();<br>
	 * startActivity( doThings.callSomeBody("10086") );<br>
	 * 
	 * @param callWho
	 */
	public Intent callSomeBody(String callWho) {
		Uri uri = Uri.parse("tel:" + callWho);
		Intent call = new Intent(Intent.ACTION_CALL, uri);
		// Intent call = new Intent(Intent.ACTION_DIAL, uri); //ֻ�ǲ���, ������

		return call;
	}

	/**
	 * �����绰<br>
	 * �÷�: <br>
	 * listenCallIn( this );<br>
	 * 
	 * @param mTelephonyMgr
	 * @return
	 */
	public void listenCallIn(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyMgr.listen(new ReceiveCallIn(), PhoneStateListener.LISTEN_CALL_STATE);
		mTelephonyMgr.getCallState();
	}

	/**
	 * �绰����״̬(û��ͨ��)
	 */
	public void onPhoneIdle() {

		Log.i(TAG, "++++++++++++++++++++++++++++++++++++++++============> Free");

		if ( UsefullVerbs.callIn ) {
			writeFile("   ״̬�����˾ܽ�\r\n", UsefullVerbs.SAVE_CONVERSATION_FILE, true);
			UsefullVerbs.callIn = false;
		} else if ( !UsefullVerbs.withoutCall ) {
			writeFile("   ͨ������ʱ�䣺" + returnDateOrTime((int) 1) + " \r\n", UsefullVerbs.SAVE_CONVERSATION_FILE, true);
			UsefullVerbs.callIn = false;

		}
		UsefullVerbs.withoutCall = true;

	}

	/**
	 * ժ��״̬(�����绰)
	 */
	public void onPhoneOffhook() {

		Log.i(TAG, "++++++++++++++++++++++++++++++++++++++++============> Hold");

		if ( UsefullVerbs.callIn ) {
			writeFile("   ״̬���Ѿ�����", UsefullVerbs.SAVE_CONVERSATION_FILE, true);
			UsefullVerbs.callIn = false;
			UsefullVerbs.withoutCall = false;
		}

	}

	/**
	 * ����״̬(����)
	 * 
	 * @param incomNum
	 *            (�����ĵ绰����)
	 */
	public void onPhoneRinging(String incomNum) {// incomMun: ������ֻ���

		Log.i(TAG, "++++++++++++++++++++++++++++++++++++++++============> Ring ++ " + incomNum);

		UsefullVerbs.callIn = true;
		String toSaveMsg = "���ڣ�" + returnDateOrTime((int) 0) + "ʱ�䣺" + returnDateOrTime((int) 1) + "    ����绰��" + incomNum;
		writeFile(toSaveMsg, UsefullVerbs.SAVE_CONVERSATION_FILE, true);

	}

	/**
	 * ���GPSλ��<br>
	 * �÷�: <br>
	 * private DoSomeThings doThings = new DoSomeThings();<br>
	 * LocationManager myLoacation = (LocationManager)
	 * getSystemService(Context.LOCATION_SERVICE);<br>
	 * double[] nowPosition = doThings.getGPS( myLoacation );<br>
	 * 
	 * @return ��γ��
	 */
	public double[] getGPS(LocationManager lm) {

		List<String> providers = lm.getProviders(true);

		/*
		 * Loop over the array backwards, and if you get an accurate location,
		 * then break out the loop
		 */
		Location l = null;

		for (int i = providers.size() - 1; i >= 0; i--) {
			l = lm.getLastKnownLocation(providers.get(i));
			if ( l != null )
				break;
		}

		double[] gps = new double[2];
		if ( l != null ) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		Log.e("GPS00000000", "GPS Text : " + gps[0] + ":" + gps[1]);
		return gps;
	}

	/**
	 * ������������֮��ľ���
	 * 
	 * @param lat_a
	 * @param lng_a
	 * @param lat_b
	 * @param lng_b
	 * @return
	 */
	public double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
		float pk = (float) (180 / 3.14169);

		float a1 = lat_a / pk;
		float a2 = lng_a / pk;
		float b1 = lat_b / pk;
		float b2 = lng_b / pk;

		float t1 = FloatMath.cos(a1) * FloatMath.cos(a2) * FloatMath.cos(b1) * FloatMath.cos(b2);
		float t2 = FloatMath.cos(a1) * FloatMath.sin(a2) * FloatMath.cos(b1) * FloatMath.sin(b2);
		float t3 = FloatMath.sin(a1) * FloatMath.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);

		return 6366000 * tt;
	}

	public boolean writeFile(String str, File file, boolean add) {
		FileOutputStream out;
		try {
			if ( !file.exists() ) {
				// �����ļ�
				file.createNewFile();
			}

			// ���ļ�file��OutputStream
			out = new FileOutputStream(file, add);// ׷�ӵ���ʽд��
			String infoToWrite = str;
			// ���ַ���ת����byte����д���ļ�
			out.write(infoToWrite.getBytes());
			// �ر��ļ�file��OutputStream
			out.close();

		} catch (IOException e) {
			return false;
		}

		return true;
	}

	public String getinfo(File file) {
		String str = "";
		FileInputStream in;
		try {
			// ���ļ�file��InputStream
			in = new FileInputStream(file);
			// ���ļ�����ȫ�����뵽byte����
			int length = (int) file.length();
			byte[] temp = new byte[length];
			in.read(temp, 0, length);
			// ��byte������UTF-8���벢����display�ַ�����
			str = EncodingUtils.getString(temp, TEXT_ENCODING);
			// �ر��ļ�file��InputStream

			in.close();
		} catch (IOException e) {
		}

		return str;
	}

	/**
	 * ������ڻ�ʱ���ַ���
	 * 
	 * @param dateOrTime
	 * @return
	 */
	public String returnDateOrTime(int dateOrTime) {
		if ( dateOrTime == 0 ) { // ��������
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return sDateFormat.format(new java.util.Date());
		} else {// ����ʱ��
			Calendar ca = Calendar.getInstance();
			int minute = ca.get(Calendar.MINUTE);
			int hour = ca.get(Calendar.HOUR_OF_DAY);
			int second = ca.get(Calendar.SECOND);

			return getformatString(hour) + ":" + getformatString(minute) + ":" + getformatString(second);
		}
	}

	/**
	 * ��ʱ��תΪ�ض��ĸ�ʽ ��: 1 תΪ 01
	 * 
	 * @param mmm
	 * @return
	 */
	public static String getformatString(int mmm) {
		if ( mmm < 10 ) {
			if ( mmm == 0 ) {
				return "00";
			} else {
				return "0" + mmm;
			}
		} else {
			return "" + mmm;
		}
	}

	public static void DisplayToast(Context context, String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

}
