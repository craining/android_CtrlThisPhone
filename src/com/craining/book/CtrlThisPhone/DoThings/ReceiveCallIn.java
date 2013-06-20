package com.craining.book.CtrlThisPhone.DoThings;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * 监听电话的呼入状态
 * 
 * @author Ruin
 * 
 */
public class ReceiveCallIn extends PhoneStateListener {

	private DoSomeThings doThings = new DoSomeThings();

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE: { // 空闲(没有通话)

			doThings.onPhoneIdle();

			break;
		}

		case TelephonyManager.CALL_STATE_RINGING: { // 振铃(来电)

			doThings.onPhoneRinging(incomingNumber);

			break;
		}

		case TelephonyManager.CALL_STATE_OFFHOOK: { // 摘机(接听)

			doThings.onPhoneOffhook();

			break;
		}

		default:
			break;
		}
	}

}
