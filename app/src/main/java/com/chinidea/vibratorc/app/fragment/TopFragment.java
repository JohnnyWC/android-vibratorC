package com.chinidea.vibratorc.app.fragment;

import android.app.Service;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chinidea.vibratorc.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Unbinder;

/**
 * Created by johnny on 2016/12/2.
 */

public class TopFragment extends Fragment {

    private final static String TAG = "TopFragment";

    private final long VIBRATION_INTERVAL = 0;

    @BindView(R.id.btn_play)
    protected Button play;
    @BindView(R.id.btn_assign)
    protected Button assign;
    @BindView(R.id.btn_record)
    protected Button record;

    private Unbinder unbinder;
    private View rootView;

    private List<Long> patterns = new ArrayList<Long>();
    private boolean isRecording = false;
    private boolean isRecordingPatterns = false;
    private Vibrator vibrator;


    public TopFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_top, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        initUI();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void initUI() {
        if(!(patterns.size() > 0)) {
            play.setEnabled(false);
            assign.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_play)
    protected void playRecordedPattern() {
        long[] values = new long[patterns.size()];
        for (int i = 0; i < patterns.size(); i++) {
            values[i] = patterns.get(i);
        }
        //for (int j = 0; j < values.length; j++) {
        //    vibrator.vibrate(values[j]);
        //}
        vibrator.vibrate(values, -1);
    }

    @OnClick(R.id.btn_record)
    protected void recordPattern() {
        if (isRecording) {
            isRecording = false;
            play.setEnabled(true);
            assign.setEnabled(true);
            record.setText(R.string.btn_record);
        } else {
            isRecording = true;
            patterns.clear();
            patterns.add(VIBRATION_INTERVAL);
            play.setEnabled(false);
            assign.setEnabled(false);
            record.setText(R.string.btn_recording);
        }
    }


    @OnTouch(R.id.pattern_zone)
    protected boolean onTouch(View view, MotionEvent event) {
        if (!isRecording) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isRecordingPatterns = true;
            new AsyncRecording().execute(new Object());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isRecordingPatterns = false;
            new AsyncInterval().execute(new Object());
        }
        return true;
    }

    @OnClick(R.id.btn_assign)
    protected void assignPattern() {
        SharedPreferences prefs = getContext().getSharedPreferences("patterns", 0);
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < patterns.size(); i++) {
            value.append(patterns.get(i));
            value.append("#");
        }
        Log.d(TAG, "pattern = " + value.toString());
        prefs.edit().putString("patterns", value.toString()).commit();

        patterns.clear();
        patterns.add(VIBRATION_INTERVAL);
        Snackbar.make(rootView, getString(R.string.pattern_assigned), Snackbar.LENGTH_SHORT).show();
    }

    private class AsyncInterval extends AsyncTask<Object, Integer, Long> {

        @Override
        protected Long doInBackground(Object... params) {
            long startTime = System.currentTimeMillis();
            while (!isRecordingPatterns) {

            }
            long endTime = System.currentTimeMillis();
            if (patterns.size() > 1) {
                patterns.add(endTime - startTime);
            }
            return new Long(endTime - startTime);
        }

        @Override
        protected void onPostExecute(Long time) {

            super.onPostExecute(time);
        }
    }

    private class AsyncRecording extends AsyncTask<Object, Integer, Long> {

        @Override
        protected Long doInBackground(Object... params) {
            long startTime = System.currentTimeMillis();
            vibrator.vibrate(10000000);
            while (isRecordingPatterns) {
            }
            vibrator.cancel();
            long endTime = System.currentTimeMillis();

            return new Long(endTime - startTime);
        }

        @Override
        protected void onPostExecute(Long time) {
//            patterns.add(VIBRATION_INTERVAL);
            patterns.add(time);
            super.onPostExecute(time);

        }
    }
}
