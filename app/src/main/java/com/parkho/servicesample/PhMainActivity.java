package com.parkho.servicesample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.parkho.servicesample.PhService.PhBinder;

import java.util.concurrent.TimeUnit;

public class PhMainActivity extends AppCompatActivity
{
    private PhService mService;

    private Handler mHandler;

    private TextView mTvTime;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName a_name, IBinder a_service) {
            PhBinder binder = (PhBinder) a_service;
            mService = binder.getService();

            mHandler.post(mClockTick);
        }

        public void onServiceDisconnected(ComponentName a_name) {
            onStopService();
        }
    };

    // 1초에 한 번씩 time 확인
    private Runnable mClockTick = new Runnable() {
        @Override
        public void run() {
            String strTime = convertTimeFormat(mService.getTime());
            mTvTime.setText(strTime);

            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        // 서비스 시작
        Button btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new OnClickListener() {
            public void onClick(View a_view) {
                onStartService();
            }
        });

        // 서비스 종료
        Button btnStop = findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new OnClickListener() {
            public void onClick(View a_view) {
                onStopService();
            }
        });

        mTvTime = findViewById(R.id.tv_time);
    }

    /**
     * 서비스 시작
     */
    private void onStartService() {
        Intent intent = new Intent(getApplicationContext(), PhService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 서비스 종료
     */
    private void onStopService() {
        if (mService != null) {
            unbindService(mServiceConnection);
            mService = null;
        }

        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Time format
     */
    private String convertTimeFormat(final long a_time) {
        final long hr = TimeUnit.MILLISECONDS.toHours(a_time);
        final long min = TimeUnit.MILLISECONDS.toMinutes(a_time - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(a_time - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final String strSec = String.format("%02d:%02d:%02d", hr, min, sec);
        return strSec;
    }
}