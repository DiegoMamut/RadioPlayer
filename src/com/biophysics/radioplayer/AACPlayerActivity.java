/*
 ** AACPlayer - Freeware Advanced Audio (AAC) Player for Android
 ** Copyright (C) 2011 Spolecne s.r.o., http://www.spoledge.com
 **  
 ** This program is free software; you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation; either version 3 of the License, or
 ** (at your option) any later version.
 ** 
 ** This program is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 ** GNU General Public License for more details.
 ** 
 ** You should have received a copy of the GNU General Public License
 ** along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/
package com.biophysics.radioplayer;

//import java.net.URL;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.lang.String;
//import java.lang.StringBuilder;
import android.util.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;

//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
import android.widget.ImageButton; //import android.widget.EditText;
import android.widget.ProgressBar; //import android.widget.TextView;
import android.widget.TextView;


import com.biophysics.radioplayer.R;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

/**
 * This is the main activity.
 */
public class AACPlayerActivity extends Activity implements
		View.OnClickListener, PlayerCallback {

	private ImageButton mPlay1;
	private ImageButton mPlay1xtra;
	private ImageButton mPlay2;
	private ImageButton mPlay3;
	private ImageButton mPlay4;
	private ImageButton mPlay4xtra;
	private ImageButton mPlay5;
	private ImageButton mPlay5xtra;
	private ImageButton mPlayAsian;
	private ImageButton mStop;
	private ImageButton mPlayWs;

	TelephonyManager telephonyManager;
	PhoneStateListener listener;
	
    // Wifi lock that we hold when streaming files from the internet, in order to prevent the
    // device from shutting off the Wifi radio
    WifiLock mWifiLock;
    AudioManager mAudioManager;
    NotificationManager mNotificationManager;


	static final int txtBufAudio = 3500;
	static final int txtBufDecode = 700;
	private ProgressBar progress;
	private Handler uiHandler;

	/**
	 * Decoder features: FAAD | FFmpeg | OpenCORE
	 */

	private AACPlayer aacPlayer;

	// //////////////////////////////////////////////////////////////////////////
	// PlayerCallback
	// //////////////////////////////////////////////////////////////////////////

	private boolean playerStarted;

/*
	public void displayAlert(){
		new AlertDialog.Builder(this)
		.setMessage("There is no internet connection.")
		.setTitle("No network connection")
		.setNeutralButton( "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.d( "AlertDialog", "Neutral" );
			}
		})
		.show();
	}

*/
	public void displayAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("No network connection");
		builder.setMessage("Cannot play the radio because there is no internet connection.");
		builder.setPositiveButton("OK", null);
		AlertDialog dialog = builder.show();

		// Must call show() prior to fetching text view
		TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.LEFT);
		
	}
	
	public void playerStarted() {
		uiHandler.post(new Runnable() {
			public void run() {
				mStop.setEnabled(true);
				progress.setProgress(0);
				progress.setVisibility(View.VISIBLE);
				playerStarted = true;
			}
		});
	}

	public void playerPCMFeedBuffer(final boolean isPlaying,
			final int audioBufferSizeMs, final int audioBufferCapacityMs) {

		uiHandler.post(new Runnable() {
			public void run() {
				progress.setProgress(audioBufferSizeMs * progress.getMax()
						/ audioBufferCapacityMs);
				// if (isPlaying) txtPlayStatus.setText( R.string.text_playing
				// );
			}
		});
	}

	public void playerStopped(final int perf) {
		uiHandler.post(new Runnable() {
			public void run() {
				enableButtons();
				mStop.setEnabled(false);

				progress.setVisibility(View.INVISIBLE);

				playerStarted = false;
			}
		});
	}

	public void playerException(final Throwable t) {

		if (playerStarted)
			playerStopped(0);
	};

	// //////////////////////////////////////////////////////////////////////////
	// OnClickListener
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Called when a view has been clicked.
	 */
	public void onClick(View v) {
		try {
			
			switch (v.getId()) {

			case R.id.play1:
				stop();
				if (checkNetwork() == false) break;
				mPlay1.setEnabled(false);
				mPlay1.setAlpha(128);
				startOne(Decoder.DECODER_FFMPEG_WMA);
				// txtStatus.setText( R.string.text_using_FFmpeg );
				break;

			case R.id.play1xtra:
				stop();
				if (checkNetwork() == false) break;
				mPlay1xtra.setEnabled(false);
				mPlay1xtra.setAlpha(128);
				startOnextra(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play2:
				stop();
				if (checkNetwork() == false) break;
				mPlay2.setEnabled(false);
				mPlay2.setAlpha(128);
				startTwo(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play3:
				stop();
				if (checkNetwork() == false) break;
				mPlay3.setEnabled(false);
				mPlay3.setAlpha(128);
				startThree(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play4:
				stop();				
//				checkNetwork();
				if (checkNetwork() == false) break;
				mPlay4.setEnabled(false);
				mPlay4.setAlpha(128);
				startFour(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play5:
				stop();
				if (checkNetwork() == false) break;
				mPlay5.setEnabled(false);
				mPlay5.setAlpha(128);
				startFive(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play4xtra:
				stop();
				if (checkNetwork() == false) break;
				mPlay4xtra.setEnabled(false);
				mPlay4xtra.setAlpha(128);
				startFourxtra(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.play5xtra:
				stop();
				if (checkNetwork() == false) break;
				mPlay5xtra.setEnabled(false);
				mPlay5xtra.setAlpha(128);
				startFivextra(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.playAsian:
				stop();
				if (checkNetwork() == false) break;
				mPlayAsian.setEnabled(false);
				mPlayAsian.setAlpha(128);
				startAsian(Decoder.DECODER_FFMPEG_WMA);
				break;

			case R.id.playWs:
				stop();
				if (checkNetwork() == false) break;
				mPlayWs.setEnabled(false);
				mPlayWs.setAlpha(128);
				startWs(Decoder.DECODER_FFMPEG_WMA);
				
				break;

			case R.id.stop:
				stop();
				enableButtons();
				System.exit(0);

				// finish();
				break;
			}
		} catch (Exception e) {
			Log.e("AACPlayerActivity", "exc", e);
		}
	}


	// //////////////////////////////////////////////////////////////////////////
	// Protected
	// //////////////////////////////////////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mPlay1 = (ImageButton) findViewById(R.id.play1);
		mPlay1xtra = (ImageButton) findViewById(R.id.play1xtra);
		mPlay2 = (ImageButton) findViewById(R.id.play2);
		mPlay3 = (ImageButton) findViewById(R.id.play3);
		mPlay4 = (ImageButton) findViewById(R.id.play4);
		mPlay4xtra = (ImageButton) findViewById(R.id.play4xtra);
		mPlay5 = (ImageButton) findViewById(R.id.play5);
		mPlay5xtra = (ImageButton) findViewById(R.id.play5xtra);
		mPlayAsian = (ImageButton) findViewById(R.id.playAsian);
		mStop = (ImageButton) findViewById(R.id.stop);
		mPlayWs = (ImageButton) findViewById(R.id.playWs);

		progress = (ProgressBar) findViewById(R.id.view_main_progress);

		mPlay1.setOnClickListener(this);
		mPlay1xtra.setOnClickListener(this);
		mPlay2.setOnClickListener(this);
		mPlay3.setOnClickListener(this);
		mPlay4.setOnClickListener(this);
		mPlay4xtra.setOnClickListener(this);
		mPlay5.setOnClickListener(this);
		mPlay5xtra.setOnClickListener(this);
		mPlayAsian.setOnClickListener(this);
		mPlayWs.setOnClickListener(this);

		mStop.setOnClickListener(this);
		enableButtons();
		uiHandler = new Handler();
		

        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                        .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stop();
	}

	// //////////////////////////////////////////////////////////////////////////
	// Private
	// //////////////////////////////////////////////////////////////////////////

	private void startOne(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);
		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlOne());
	}

	private void startOnextra(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlOnextra());
	}

	private void startTwo(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlTwo());
	}

	private void startThree(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlThree());
	}

	private void startFour(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlFour());
	}

	private void startFourxtra(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlFourxtra());
	}

	private void startFive(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlFive());
	}

	private void startFivextra(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlFivextra());
	}

	private void startAsian(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlAsian());
	}

	private void startWs(int decoder) throws Exception {
		// stop();
		mStop.setEnabled(true);

		aacPlayer = new ArrayAACPlayer(ArrayDecoder.create(decoder), this,
				txtBufAudio, txtBufDecode);
		aacPlayer.playAsync(getUrlWs());

	}

	private void stop() {
		mStop.setAlpha(255);
		if (mWifiLock.isHeld()) mWifiLock.release();
		// if (aacFileChunkPlayer != null) { aacFileChunkPlayer.stop();
		// aacFileChunkPlayer = null; }
		if (aacPlayer != null) {
			aacPlayer.stop();
			aacPlayer = null;
		}
	}

	private String getUrlFour() throws Exception {
/*		URL url44 = new URL("http://www.bbc.co.uk/radio/listen/live/r4.asx");
		
		// URL from http://faq.external.bbc.co.uk/questions/radio/online_radiohowto/?src=interstitial
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder();
		// try {
		reader = new BufferedReader(new InputStreamReader(url44.openStream(),
				"UTF-8"));
		for (String line; (line = reader.readLine()) != null;) {
			builder.append(line.trim());
		}
		// } finally {
		// if (reader != null) try { reader.close(); } catch (Exception
		// logOrIgnore) {}
		// }

		// String Contents4 = (String) url4.getContent();

		String start = "<ref href=\"";
		String end = "=\" />";
		String part1 = builder.substring(builder.indexOf(start)
				+ start.length());
		String ret = part1.substring(0, part1.indexOf(end) - 2 );
		
*/		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio4/radio4_bb_live_int_eq1_sl0";
		
		return ret;
	}

	private String getUrlOne() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio1/radio1_bb_live_int_ep1_sl0";
		
		return ret;
	}

	private String getUrlOnextra() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/1xtra/1xtra_bb_live_int_eq1_sl0";
		return ret;
	}

	private String getUrlTwo() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio2/radio2_bb_live_int_ep1_sl0";
		return ret;
	}

	private String getUrlThree() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio3/radio3_bb_live_int_eq1_sl0";
		return ret;
	}

	private String getUrlFive() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio5/radio5_bb_live_int_ep1_sl0";
		return ret;
	}

	private String getUrlFivextra() throws Exception {
		//String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio5/5spxtra_bb_live_int_ep1_sl0";
		String ret = "mms://a1671.l2063252432.c20632.g.lm.akamaistream.net/D/1671/20632/v0001/reflector:52432" ;
		return ret;
	}

	private String getUrlFourxtra() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/radio4/radio4xtra_bb_live_int_ep1_sl0";
		return ret;
	}

	private String getUrlAsian() throws Exception {
		String ret = "mms://wmlive-nonacl.bbc.net.uk/wms/bbc_ami/asiannet/asiannet_bb_live_int_ep1_sl0";
		return ret;
	}

	private String getUrlWs() throws Exception {
//		URL url4 = new URL("https://media-translate.googlecode.com/svn/trunk/etc/playlists/radio-bbc-world.xspf");
// URL below obtained from http://www.bbc.co.uk/worldservice/institutional/2009/03/000000_mobile.shtml
/*		URL url4 = new URL("http://www.bbc.co.uk/worldservice/meta/tx/nb/live/eneuk.asx");
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder();
		// try {
		reader = new BufferedReader(new InputStreamReader(url4.openStream(),
				"UTF-8"));
		for (String line; (line = reader.readLine()) != null;) {
			builder.append(line.trim());
		}

		String start = "href=\"";
		String end = "\"";
		String part1 = builder.substring(builder.indexOf(start)
				+ start.length());
		String ret = part1.substring(0, part1.indexOf(end) );
*///		String ret = "mms://a243.l3944038972.c39440.g.lm.akamaistream.net/D/243/39440/v0001/reflector:38972";
//		String ret = "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_intl_lc_radio4_q";
//		rtmpdump -r rtmp://cp60697.live.edgefcs.net -a live -y Radio_4_Int@6448 -o o.flv -v
		String ret = "mms://a243.l3944038972.c39440.g.lm.akamaistream.net/D/243/39440/v0001/reflector:38972";
		return ret;
	}

	private void enableButtons() {
		// if ((dfeatures & Decoder.DECODER_FAAD2) != 0) btnFaad2.setEnabled(
		// true );
		// if ((dfeatures & Decoder.DECODER_FFMPEG) != 0) btnFFmpeg.setEnabled(
		// true );
		// if ((dfeatures & Decoder.DECODER_FFMPEG) != 0) mPlay4.setEnabled(
		// true );
		// if ((dfeatures & Decoder.DECODER_FFMPEG) != 0) mPlay1.setEnabled(
		// true );
		// if ((dfeatures & Decoder.DECODER_FFMPEG) != 0) mPlayAsian.setEnabled(
		// true );
		mStop.setAlpha(99);
		mStop.setEnabled(false);

		mPlay1.setEnabled(true);
		mPlay1xtra.setEnabled(true);
		mPlay2.setEnabled(true);
		mPlay3.setEnabled(true);
		mPlay4.setEnabled(true);
		mPlay4xtra.setEnabled(true);
		mPlay5.setEnabled(true);
		mPlay5xtra.setEnabled(true);
		mPlayWs.setEnabled(true);
		mPlayAsian.setEnabled(true);

		mPlay1.setAlpha(255);
		mPlay2.setAlpha(255);
		mPlay3.setAlpha(255);
		mPlay4.setAlpha(255);
		mPlay1xtra.setAlpha(255);
		mPlay4xtra.setAlpha(255);
		mPlay5.setAlpha(255);
		mPlay5xtra.setAlpha(255);
		mPlayWs.setAlpha(255);
		mPlayAsian.setAlpha(255);

	}

	private void disableButtons() {
		mPlay1.setEnabled(false);
		mPlay2.setEnabled(false);
		mPlay3.setEnabled(false);
		mPlay4.setEnabled(false);
		mPlay4xtra.setEnabled(false);
		mPlay5.setEnabled(false);
		mPlay5xtra.setEnabled(false);
		mPlayWs.setEnabled(false);
		mPlayAsian.setEnabled(false);
		mPlay1.setAlpha(255);
		mPlay2.setAlpha(255);
		mPlay3.setAlpha(255);
		mPlay4.setAlpha(255);
		mPlay4xtra.setAlpha(255);
		mPlay5.setAlpha(255);
		mPlay5xtra.setAlpha(255);
		mPlayWs.setAlpha(255);
		mPlayAsian.setAlpha(255);
	}

	
	private boolean checkNetwork() {
		
	    boolean HaveConnectedWifi = false;

		
		ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if ( conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED 
			    &&  conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
			/*		if ( conMgr.getNetworkInfo(0).isConnected() == true) {*/
			displayAlert();
			if (mWifiLock.isHeld()) mWifiLock.release();
            return false;
		} else {
			disableButtons();
			NetworkInfo[] netInfo = conMgr.getAllNetworkInfo();
					for (NetworkInfo ni : netInfo) {
							Log.d("HaveNetworkConnection()", ni.toString());
								if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
										HaveConnectedWifi = true;
										mWifiLock.acquire();
								}
//								if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
//										HaveConnectedMobile = true;
					}
			return true;
			}
	}

	/*
	private boolean checkNetwork2() throws Exception {
		URL url4 = new URL("http://www.bbc.co.uk");
		HttpURLConnection urlc = (HttpURLConnection) url4.openConnection();
		urlc.setRequestProperty("Connection", "close");
	    urlc.setConnectTimeout(1000 * 30); // mTimeout is in seconds
        urlc.connect();
        if (urlc.getResponseCode() == 200) {
        	return new Boolean(true);
        } else {
        	displayAlert();
        }
		return true;
	}
	*/
}
