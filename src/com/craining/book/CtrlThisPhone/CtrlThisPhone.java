package com.craining.book.CtrlThisPhone;

/**
 * 主程序：重启服务后退出
 * 
 * @author Ruin
 */

import com.craining.book.CtrlThisPhone.DoThings.DoSomeThings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class CtrlThisPhone extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.main);
		checkFiles();
	}
	
	private void startService() {
		startService(new Intent(UsefullVerbs.PACKAGE_NAME + ".BackService"));

		// 开启线程等待
		Thread background = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(5000);
					stopService(new Intent(UsefullVerbs.PACKAGE_NAME + ".BackService"));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		background.start();

		CtrlThisPhone.this.finish();
	}

	private void checkFiles() {
		if ( !UsefullVerbs.SAVE_CTRLINGNO_FILE.exists() || !UsefullVerbs.SAVE_CTRLINGEMAIL_FILE.exists() ) {
			showSaveFilesDlg(CtrlThisPhone.this);
		} else {
			startService();
		}
	}

	public void showSaveFilesDlg(Context context) {
		LayoutInflater factory = LayoutInflater.from(context);
		// 得到自定义对话框
		final View testDialogView = factory.inflate(R.layout.dlg_savefiles, null);
		final EditText edittext_number = (EditText) testDialogView.findViewById(R.id.edittext_number);
		final EditText edittext_mailadd = (EditText) testDialogView.findViewById(R.id.edittext_emailadd);
		final EditText edittext_ctrlpwd = (EditText) testDialogView.findViewById(R.id.edittext_ctrlpwd);
		String title = new String("初始操作：");

		// 创建对话框
		AlertDialog.Builder testDialog = new AlertDialog.Builder(context);
		testDialog.setTitle(title);
		testDialog.setView(testDialogView);
		testDialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String get_emailadd = edittext_mailadd.getText().toString();
				String get_addnum = edittext_number.getText().toString();
				String get_ctrlpwd = edittext_ctrlpwd.getText().toString();
				if ( TextUtils.isEmpty(get_emailadd) || TextUtils.isEmpty(get_addnum) || TextUtils.isEmpty(get_ctrlpwd) ) {
					DoSomeThings.DisplayToast(CtrlThisPhone.this, "各项均不能为空！");
					showSaveFilesDlg(CtrlThisPhone.this);
				} else {
					toSaveCtrlingEmail(get_emailadd);
					toSaveCtrlingNo(get_addnum);
					toSavePwd(get_ctrlpwd);
					startService();
				}
			}

		});

		testDialog.setNegativeButton(getString(R.string.ctrl_cancle), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				DoSomeThings.DisplayToast(CtrlThisPhone.this, "请务必进行设置！");
				showSaveFilesDlg(CtrlThisPhone.this);
			}
		})

		.create();
		testDialog.show();
	}


	private void toSaveCtrlingNo(String num) {
		DoSomeThings doThings = new DoSomeThings();
		doThings.writeFile(num, UsefullVerbs.SAVE_CTRLINGNO_FILE, false);
		Log.e("", "saved First Num :  " + num);
	}

	private void toSaveCtrlingEmail(String emailadd) {
		DoSomeThings doThings = new DoSomeThings();
		doThings.writeFile(emailadd, UsefullVerbs.SAVE_CTRLINGEMAIL_FILE, false);
		Log.e("", "saved First Email :  " + emailadd);
	}
	
	private void toSavePwd(String pwd) {
		DoSomeThings doThings = new DoSomeThings();
		doThings.writeFile(pwd, UsefullVerbs.SAVE_PWD_FILE, false);
		Log.e("", "saved PWD :  " + pwd);
	}
}