package com.chinidea.vibratorc.app.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by johnny on 2016/12/02.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    private Vibrator mVibrator;

    @Override
    public void onReceive(Context context, Intent intent) {

        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(am.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE) {
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences("patterns", 0);
        String value = prefs.getString("patterns", "");
        long[] patterns = getPatterns(value);

        mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        mVibrator.vibrate(patterns, 0);

        MFPhoneStateListener myPhoneStateListener = new MFPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private long[] getPatterns(String value) {
        String[] temp = value.split("#");
        long[] patterns = new long[temp.length];
        for (int i = 0; i < temp.length; i++) {
            patterns[i] = Long.valueOf(temp[i]);
        }
        return patterns;
    }

    private class MFPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    mVibrator.cancel();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    mVibrator.cancel();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
                default:
                    break;
            }
        }
    }
}
