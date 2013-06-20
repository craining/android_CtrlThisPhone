package com.craining.book.CtrlThisPhone.DoThings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.craining.book.CtrlThisPhone.R;

public class ReadMsg extends Activity {
	private TextView text_cyphercontent = null;
	private EditText edittext_msgpwd = null;
	private Button btn_encodeandredo = null;
	private Button btn_giveup = null;

	private String cyphercontent = "";
	private String msgpwd = "";
	private String fromwho = "";
	private boolean decode = true;
	private boolean decodesuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_receivemsg);
		setTitle("读取加密短信：");

		Bundle bundle = getIntent().getExtras();
		cyphercontent = bundle.getString("MSGCONTENT");
		msgpwd = bundle.getString("MSGPWD");
		fromwho = bundle.getString("FROM");

		text_cyphercontent = (TextView) findViewById(R.id.text_showcyphercontent);
		edittext_msgpwd = (EditText) findViewById(R.id.edittext_msgpwd);
		btn_encodeandredo = (Button) findViewById(R.id.button_decodeandredo);
		btn_giveup = (Button) this.findViewById(R.id.button_giveupdecodemsg);
		text_cyphercontent.setText(cyphercontent);

		btn_encodeandredo.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if ( decodesuccess ) {
					//回复
					SendSecretMsg.fromRelay = true;
					SendSecretMsg.relyToWho = fromwho;
					Intent i = new Intent();
					i.setClass(ReadMsg.this, SendSecretMsg.class);
					ReadMsg.this.finish();
					startActivity( i );
				} else {
					// 解密
					String get_ctrlpwd = edittext_msgpwd.getText().toString();
					if ( TextUtils.isEmpty(get_ctrlpwd) ) {
						get_ctrlpwd = "";
					}
					if ( msgpwd.equals(get_ctrlpwd) ) {
						String clearcontent = "";
						try {
							edittext_msgpwd.setVisibility(View.GONE);
							clearcontent = SimpleCrypto.decrypt(get_ctrlpwd, cyphercontent);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							edittext_msgpwd.setText("");
							DoSomeThings.DisplayToast(ReadMsg.this, "密码正确！但解密失败，请重试。");
							e.printStackTrace();
						} finally {
							DoSomeThings.DisplayToast(ReadMsg.this, "解密成功！");
							decodesuccess = true;
							btn_encodeandredo.setText("回复");
							text_cyphercontent.setText(clearcontent);
							btn_giveup.setText("退出");
						}

					} else {
						edittext_msgpwd.setText("");
						DoSomeThings.DisplayToast(ReadMsg.this, "密码错误！请重试...");
					}
				}
			}
		});

		btn_giveup.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if ( !decodesuccess ) {
					showDelDlg(ReadMsg.this);
				} else {
					ReadMsg.this.finish();
				}
			}
		});

	}

	private void showDelDlg(Context context) {
		AlertDialog.Builder testDialog = new AlertDialog.Builder(context);
		testDialog.setTitle("警告：");
		testDialog.setMessage("\n\t\t您尚未解密成功，确定要舍弃吗？");
		testDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				ReadMsg.this.finish();
			}
		});
		testDialog.setNegativeButton(getString(R.string.ctrl_cancle), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		})

		.create();
		testDialog.show();
	}

}
