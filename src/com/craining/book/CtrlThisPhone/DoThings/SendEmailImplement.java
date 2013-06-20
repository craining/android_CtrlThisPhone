package com.craining.book.CtrlThisPhone.DoThings;

/**
 * 发送邮件
 * 
 * @author Ruin
 */

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendEmailImplement {

	String to = "";// 收件人
	String from = "";// 发件人
	String host = "";// smtp主机
	String username = "";
	String password = "";
	String filename = "";// 附件文件名
	String subject = "";// 邮件主题
	String content = "";// 邮件正文
	static Vector<String> file = new Vector<String>();// 附件文件集合

	public SendEmailImplement() {
	}

	public SendEmailImplement(String to, String from, String smtpServer,
			String username, String password, String subject, String content) {
		this.to = to;
		this.from = from;
		this.host = smtpServer;
		this.username = username;
		this.password = password;
		this.subject = subject;
		this.content = content;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPassWord(String pwd) {
		this.password = pwd;
	}

	public void setUserName(String usn) {
		this.username = usn;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean sendMail() {

		Properties props = System.getProperties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		props.put("mail.transport.protocol", "smtp");
		Session session = Session.getInstance(props, null);
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = { new InternetAddress(to) };
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(subject);

			Multipart mp = new MimeMultipart();

			MimeBodyPart mbpContent = new MimeBodyPart();
			mbpContent.setText(content);
			mp.addBodyPart(mbpContent);

			Enumeration<String> efile = file.elements();
			while (efile.hasMoreElements()) {

				MimeBodyPart mbpFile = new MimeBodyPart();
				filename = efile.nextElement().toString();
				FileDataSource fds = new FileDataSource(filename);
				mbpFile.setDataHandler(new DataHandler(fds));
				mbpFile.setFileName(fds.getName());
				mp.addBodyPart(mbpFile);

			}

			file.removeAllElements();
			msg.setContent(mp);
			msg.setSentDate(new Date());

			Transport transport = session.getTransport();
			transport.connect(host, username, password);
			transport.sendMessage(msg,
					new Address[] { new InternetAddress(to) });
			transport.close();

		} catch (MessagingException mex) {
			mex.printStackTrace();
			Exception ex = null;
			if ((ex = mex.getNextException()) != null) {
				ex.printStackTrace();
			}
			return false;
		}
		return true;
	}

//	public int login() throws IOException, MessagingException {
//
//
//		Properties props = System.getProperties();
//		props.put("mail.smtp.host", host);
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.transport.protocol", "smtp");
//		Session session = Session.getInstance(props);
//		// session.setDebug(false);
//		Transport trans = session.getTransport();
//		try {
//			trans.connect(host, username, password);
//			trans.close();
//
//		} catch (AuthenticationFailedException e) {
//			e.printStackTrace();
//
//			return 3;
//		} catch (MessagingException e) {
//			return 3;
//		}
//
//		return 0;
//	}

	public boolean sendMyEmail(String host, String name, String pwd,
			String tosomebody, String whosend, String subject, String content,
			Vector<String> Attachfile) {

//		try {
//			login();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		SendEmailImplement sendmail = new SendEmailImplement();
		sendmail.setHost(host);// smtp.mail.yahoo.com.cn
		sendmail.setUserName(name);// 您的邮箱用户名
		sendmail.setPassWord(pwd);// 您的邮箱密码
		sendmail.setTo(tosomebody);// 接收者
		sendmail.setFrom(whosend);// 发送者
		sendmail.setSubject(subject);// 邮件主题
		sendmail.setContent(content);// 邮件内容
		file = Attachfile;
		if (sendmail.sendMail()) {
			return true;
		}

		return false;
	}

}
