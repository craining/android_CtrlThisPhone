package com.craining.book.CtrlThisPhone;

/**
 * 用到的全局变量
 * 
 * @author Ruin
 */
import java.io.File;

public class UsefullVerbs {

	// 程序的包名 和 保存文件的起始目录
	public static final String PACKAGE_NAME = "com.craining.book.CtrlThisPhone";
	public static final String SAVE_FIRSTPATH = "data/data/" + PACKAGE_NAME + "/";

	// 用于存储控制密码
	public static final File SAVE_PWD_FILE = new File(SAVE_FIRSTPATH + "CtrlPwd.txt");
	// 用于存储本机电话号码
	public static final File SAVE_THISNO_FILE = new File(SAVE_FIRSTPATH + "PhoneNo.txt");
	public static String thisPreNo = "";
	public static String thisNowNo = "";
	public static final String MSG_CONTENT = "This is my new Phone Numbers: ";

	// 用于存储被控制者信息
	public static final File SAVE_CTRLINGNO_FILE = new File(SAVE_FIRSTPATH + "CtrlingNo.txt");
	public static final File SAVE_CTRLINGEMAIL_FILE = new File(SAVE_FIRSTPATH + "CtrlingEmail.txt");
	/** 此号码为永久保存的默认的控制者电话，若无法获取控制者号码，则选择此号码为控制者 */

	// 发件人以 guangyu0@163.com 为例,密码为68038951，请改为你自己的邮箱
	public static final String EMAIL_SMTP = "smtp.163.com";
	public static final String EMAIL_NAME = "guangyu0"; // 发件人邮箱用户名
	public static final String EMAIL_PWD = "68038951"; // 密码
	public static final String EMAIL_WHOSEND = "guangyu0@163.com "; // 发件人邮箱

	// 用于存储联系人
	public static final String SAVE_CONTACTS_FILENAME = SAVE_FIRSTPATH + "MyContacts.txt";
	public static final File SAVE_CONTACTS_FILE = new File(SAVE_FIRSTPATH + "MyContacts.txt");

	// 用于发送短信与拨打电话
	public static String msg_Number = "";
	public static String msg_Content = "";

	// 用来存储短信和通话记录的文件
	public static final File SAVE_MSG_FILE = new File(SAVE_FIRSTPATH + "MyMsgs.txt");
	public static final File SAVE_CONVERSATION_FILE = new File(SAVE_FIRSTPATH + "MyConversation.txt");

	// 判断电话状态，用来保存通话记录
	public static boolean callIn = false; // 来电与否
	public static boolean callOut = false;// 拨出电话与否
	public static boolean withoutCall = true;

	// 指令加密的密码
	public static String COMMAND_PWD = "OurAndroid";

	// Widget要显示的text
	public static boolean acceptCtrl = true;
	public static String strShowOnWidget = "";

	public static final File ACCEPT = new File(UsefullVerbs.SAVE_FIRSTPATH + "refusing/");

}
