package com.biophysics.radioplayer;


import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class OutgoingCallReceiver extends BroadcastReceiver implements PlayerCallback {
	



	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		
		if(null == bundle)
			return;
		
//		String phonenumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

//		Log.i("OutgoingCallReceiver",phonenumber);
//		Log.i("OutgoingCallReceiver",bundle.toString());

		
//		String info = "--8***----NEW Detect Calls sample application\nOutgoing number: " + phonenumber;
		
		System.exit(0);
		
//		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public void playerStarted() {
		// TODO Auto-generated method stub
		
	}

	public void playerPCMFeedBuffer(boolean isPlaying, int audioBufferSizeMs,
			int audioBufferCapacityMs) {
		// TODO Auto-generated method stub
		
	}

	public void playerStopped(int perf) {
		// TODO Auto-generated method stub
		
	}

	public void playerException(Throwable t) {
		// TODO Auto-generated method stub
		
	}
	

}
