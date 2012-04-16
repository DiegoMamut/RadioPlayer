package com.biophysics.radioplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.biophysics.radioplayer.R;

public class IncomingCallReceiver extends BroadcastReceiver {
	

	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		
		if(null == bundle)
			return;
		
		String state = bundle.getString(TelephonyManager.EXTRA_STATE);
				
		if(state.equalsIgnoreCase(TelephonyManager.ACTION_PHONE_STATE_CHANGED))

		{
			System.exit(0);
		}
	}

}
