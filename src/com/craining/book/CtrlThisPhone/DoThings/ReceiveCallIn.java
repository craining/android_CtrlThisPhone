package com.craining.book.CtrlThisPhone.DoThings;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * �����绰�ĺ���״̬
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
		case TelephonyManager.CALL_STATE_IDLE: { // ����(û��ͨ��)

			doThings.onPhoneIdle();

			break;
		}

		case TelephonyManager.CALL_STATE_RINGING: { // ����(����)

			doThings.onPhoneRinging(incomingNumber);

			break;
		}

		case TelephonyManager.CALL_STATE_OFFHOOK: { // ժ��(����)

			doThings.onPhoneOffhook();

			break;
		}

		default:
			break;
		}
	}

}
