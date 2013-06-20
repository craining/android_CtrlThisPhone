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
 * 包含几个操作： 1、获得本机号码 2、发送短信 3、电话不同接听状态进行的操作 4、获得GPS 5、java IO 6、获得系统当前时间
 * 
 * @author Ruin
 * 
 */

public class DoSomeThings {

	private static final String TAG = "DoSomeThings";
	private static final String TEXT_ENCODING = "utf-8";

	/**
	 * 获得本机号码<br>
	 * 用法: <br>
	 * getMyPhoneNumber( this );
	 * 
	 * @return 本机号码
	 */
	public String getMyPhoneNumber(Context context) {

		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return mTelephonyMgr.getLine1Number();
	}

	/**
	 * 后台发送短信<br>
	 * 用法: <br>
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
	 * 拨出电话<br>
	 * 用法: <br>
	 * private DoSomeThings doThings = new DoSomeThings();<br>
	 * startActivity( doThings.callSomeBody("10086") );<br>
	 * 
	 * @param callWho
	 */
	public Intent callSomeBody(String callWho) {
		Uri uri = Uri.parse("tel:" + callWho);
		Intent call = new Intent(Intent.ACTION_CALL, uri);
		// Intent call = new Intent(Intent.ACTION_DIAL, uri); //只是拨号, 不呼叫

		return call;
	}

	/**
	 * 监听电话<br>
	 * 用法: <br>
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
	 * 电话空闲状态(没有通话)
	 */
	public void onPhoneIdle() {

		Log.i(TAG, "++++++++++++++++++++++++++++++++++++++++============> Free");

		if ( UsefullVerbs.callIn ) {
			writeFile("   状态：主人拒接\r\n", UsefullVerbs.SAVE_CONVERSATION_FILE, true);
			UsefullVerbs.callIn = false;
		} else if ( !UsefullVerbs.withoutCall ) {
			writeFile("   通话结束时间：" + returnDateOrTime((int) 1) + " \r\n", UsefullVerbs.SAVE_CONVERSATION_FILE, true);
			UsefullVerbs.callIn = false;

		}
		UsefullVerbs.withoutCall = true;

	}

	/**
	 * 摘机状态(接听电话)
	 */
	public void onPhoneOffhook() {

		Log.i(TAG, "++++++++++++++++++++++++++++++++++++++++============> Hold");

		if ( UsefullVerbs.callIn ) {
			writeFile("   状态：已经接听", UsefullVerbs.SAVE_CONVERSATION_FILE, true);
			UsefullVerbs.callIn = false;
			UsefullVerbs.withoutCall = false;
		}

	}

	/**
	 * 振铃状态(来电)
	 * 
	 * @param incomNum
	 *            (打来的电话号码)
	 */
	public void onPhoneRinging(String incomNum) {// incomMun: 来电的手机号

		Log.i(TAG, "++++++++++++++++++++++++++++++++++++++++============> Ring ++ " + incomNum);

		UsefullVerbs.callIn = true;
		String toSaveMsg = "日期：" + returnDateOrTime((int) 0) + "时间：" + returnDateOrTime((int) 1) + "    打入电话：" + incomNum;
		writeFile(toSaveMsg, UsefullVerbs.SAVE_CONVERSATION_FILE, true);

	}

	/**
	 * 获得GPS位置<br>
	 * 用法: <br>
	 * private DoSomeThings doThings = new DoSomeThings();<br>
	 * LocationManager myLoacation = (LocationManager)
	 * getSystemService(Context.LOCATION_SERVICE);<br>
	 * double[] nowPosition = doThings.getGPS( myLoacation );<br>
	 * 
	 * @return 经纬度
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
	 * 返回两个坐标之间的距离
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
				// 创建文件
				file.createNewFile();
			}

			// 打开文件file的OutputStream
			out = new FileOutputStream(file, add);// 追加的形式写入
			String infoToWrite = str;
			// 将字符串转换成byte数组写入文件
			out.write(infoToWrite.getBytes());
			// 关闭文件file的OutputStream
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
			// 打开文件file的InputStream
			in = new FileInputStream(file);
			// 将文件内容全部读入到byte数组
			int length = (int) file.length();
			byte[] temp = new byte[length];
			in.read(temp, 0, length);
			// 将byte数组用UTF-8编码并存入display字符串中
			str = EncodingUtils.getString(temp, TEXT_ENCODING);
			// 关闭文件file的InputStream

			in.close();
		} catch (IOException e) {
		}

		return str;
	}

	/**
	 * 获得日期或时间字符串
	 * 
	 * @param dateOrTime
	 * @return
	 */
	public String returnDateOrTime(int dateOrTime) {
		if ( dateOrTime == 0 ) { // 返回日期
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return sDateFormat.format(new java.util.Date());
		} else {// 返回时刻
			Calendar ca = Calendar.getInstance();
			int minute = ca.get(Calendar.MINUTE);
			int hour = ca.get(Calendar.HOUR_OF_DAY);
			int second = ca.get(Calendar.SECOND);

			return getformatString(hour) + ":" + getformatString(minute) + ":" + getformatString(second);
		}
	}

	/**
	 * 将时间转为特定的格式 如: 1 转为 01
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
