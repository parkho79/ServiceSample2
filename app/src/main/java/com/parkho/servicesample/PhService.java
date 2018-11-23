package com.parkho.servicesample;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

public class PhService extends Service
{
    private IBinder mBinder;

    private MediaPlayer mMediaPlayer;

    private long mStartTime;

    class PhBinder extends Binder {
        PhService getService() {
            return PhService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new PhBinder();

        mMediaPlayer = MediaPlayer.create(this, R.raw.sample);
        mMediaPlayer.setLooping(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mMediaPlayer.stop();
        mStartTime = 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mStartTime = SystemClock.elapsedRealtime();
        mMediaPlayer.start();

        return mBinder;
    }

    public long getTime() {
        return SystemClock.elapsedRealtime() - mStartTime;
    }
}