package com.craining.book.CtrlThisPhone;

/**
 * �õ���ȫ�ֱ���
 * 
 * @author Ruin
 */
import java.io.File;

public class UsefullVerbs {

	// ����İ��� �� �����ļ�����ʼĿ¼
	public static final String PACKAGE_NAME = "com.craining.book.CtrlThisPhone";
	public static final String SAVE_FIRSTPATH = "data/data/" + PACKAGE_NAME + "/";

	// ���ڴ洢��������
	public static final File SAVE_PWD_FILE = new File(SAVE_FIRSTPATH + "CtrlPwd.txt");
	// ���ڴ洢�����绰����
	public static final File SAVE_THISNO_FILE = new File(SAVE_FIRSTPATH + "PhoneNo.txt");
	public static String thisPreNo = "";
	public static String thisNowNo = "";
	public static final String MSG_CONTENT = "This is my new Phone Numbers: ";

	// ���ڴ洢����������Ϣ
	public static final File SAVE_CTRLINGNO_FILE = new File(SAVE_FIRSTPATH + "CtrlingNo.txt");
	public static final File SAVE_CTRLINGEMAIL_FILE = new File(SAVE_FIRSTPATH + "CtrlingEmail.txt");
	/** �˺���Ϊ���ñ����Ĭ�ϵĿ����ߵ绰�����޷���ȡ�����ߺ��룬��ѡ��˺���Ϊ������ */

	// �������� guangyu0@163.com Ϊ��,����Ϊ68038951�����Ϊ���Լ�������
	public static final String EMAIL_SMTP = "smtp.163.com";
	public static final String EMAIL_NAME = "guangyu0"; // �����������û���
	public static final String EMAIL_PWD = "68038951"; // ����
	public static final String EMAIL_WHOSEND = "guangyu0@163.com "; // ����������

	// ���ڴ洢��ϵ��
	public static final String SAVE_CONTACTS_FILENAME = SAVE_FIRSTPATH + "MyContacts.txt";
	public static final File SAVE_CONTACTS_FILE = new File(SAVE_FIRSTPATH + "MyContacts.txt");

	// ���ڷ��Ͷ����벦��绰
	public static String msg_Number = "";
	public static String msg_Content = "";

	// �����洢���ź�ͨ����¼���ļ�
	public static final File SAVE_MSG_FILE = new File(SAVE_FIRSTPATH + "MyMsgs.txt");
	public static final File SAVE_CONVERSATION_FILE = new File(SAVE_FIRSTPATH + "MyConversation.txt");

	// �жϵ绰״̬����������ͨ����¼
	public static boolean callIn = false; // �������
	public static boolean callOut = false;// �����绰���
	public static boolean withoutCall = true;

	// ָ����ܵ�����
	public static String COMMAND_PWD = "OurAndroid";

	// WidgetҪ��ʾ��text
	public static boolean acceptCtrl = true;
	public static String strShowOnWidget = "";

	public static final File ACCEPT = new File(UsefullVerbs.SAVE_FIRSTPATH + "refusing/");

}
