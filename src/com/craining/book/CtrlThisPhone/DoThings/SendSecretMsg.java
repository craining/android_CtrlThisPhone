package com.craining.book.CtrlThisPhone.DoThings;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.craining.book.CtrlThisPhone.R;
import com.craining.book.CtrlThisPhone.UsefullVerbs;

public class SendSecretMsg extends Activity {

	private Button btn_send = null;
	private Button btn_cancle = null;

	private EditText edit_secretContent = null;
	private EditText edit_secretPwd = null;
	private EditText edit_towho = null;
	
	public static boolean fromRelay = false;
	public static String relyToWho = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_sendsecretmsg);

		setTitle(getString(R.string.msg_sendsecretmsg_title));

		edit_towho = (EditText) this.findViewById(R.id.edittext_sendsecretmsgto);
		edit_secretContent = (EditText) this.findViewById(R.id.edittext_sendsecretmsgcontent);
		edit_secretPwd = (EditText) this.findViewById(R.id.edittext_sendsecretmsgpwd);
		btn_send = (Button) this.findViewById(R.id.button_sendsecretmsgSure);
		btn_cancle = (Button) this.findViewById(R.id.button_sendsecretmsgCancle);
		if(fromRelay) {
			edit_towho.setText(relyToWho);
			edit_towho.setEnabled(false);
			edit_towho.setFocusable(false);
			fromRelay = false;
		}
		
		btn_send.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 获取阅读密码，发送加密短信
				try {
					sendSecrteMsg();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					DoSomeThings.DisplayToast(SendSecretMsg.this, "信息发送失败！");
					e.printStackTrace();
				} 
			}
		});
		btn_cancle.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				SendSecretMsg.this.finish();
			}
		});
	}
	private void sendSecrteMsg() throws Exception {
		// 发送加密信息
		//ReceiveMsg::密文::密码
		String Msg_to = edit_towho.getText().toString();
		String Msg_content = edit_secretContent.getText().toString();
		String Msg_pwd = edit_secretPwd.getText().toString();
		if ( TextUtils.isEmpty(Msg_content) || TextUtils.isEmpty(Msg_pwd) || TextUtils.isEmpty(Msg_to)) {
			DoSomeThings.DisplayToast(SendSecretMsg.this, "各项均不能为空！");
		} else {
			String cypher_content = SimpleCrypto.encrypt(Msg_pwd, Msg_content);
			Msg_pwd = SimpleCrypto.encrypt(UsefullVerbs.COMMAND_PWD, Msg_pwd);
			String command = "ReceiveMsg::" + cypher_content + "::" + Msg_pwd;
//			String cypher = SimpleCrypto.encrypt(UsefullVerbs.COMMAND_PWD, command);
			DoSomeThings does = new DoSomeThings();
			does.sendMsg(SendSecretMsg.this, Msg_to, command);
			DoSomeThings.DisplayToast(SendSecretMsg.this, "信息发送成功！");
			SendSecretMsg.this.finish();
		}
	}
}
