package com.craining.book.CtrlThisPhone;

/**
 * �����������񣬺�̨���б�Ҫ����
 * 
 * @author Ruin
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MainReceiver extends BroadcastReceiver {
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	private Context context_this;
	Intent i_this;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			Intent i = new Intent(context, BackService.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService( i );
			context_this = context;
			i_this = i;
			//�����̵߳ȴ�
			Thread background = new Thread(new Runnable() { 
				public void run() { 	                
					try {	            		
						Thread.sleep(5000);	          
						context_this.startService( i_this );
					} catch (InterruptedException e) {
						e.printStackTrace();
					}  	            	         	
				} 
			});
			background.start();
		}
	}
}
