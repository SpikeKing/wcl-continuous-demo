package me.chunyu.spike.wcl_continuous_demo;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 消息服务
 * <p>
 * Created by wangchenlong on 16/2/10.
 */
public class CustomService extends IntentService {

    public static final String KEY_EXTRA_BUSY = "busy";
    public static final String KEY_EXTRA_PROGRESS = "progress";

    private LocalBroadcastManager mLbm;

    public CustomService() {
        super(CustomService.class.getSimpleName());
    }

    @Override protected void onHandleIntent(Intent intent) {
        mLbm = LocalBroadcastManager.getInstance(getApplicationContext());

        Intent broadcastIntent = new Intent(MainActivity.UPDATE_PROGRESS_FILTER);
        broadcastIntent.putExtra(KEY_EXTRA_BUSY, true);
        mLbm.sendBroadcast(broadcastIntent);

        for (int i = 1; i < MainActivity.MAX_PROGRESS + 1; ++i) {
            broadcastIntent = new Intent(MainActivity.UPDATE_PROGRESS_FILTER);
            broadcastIntent.putExtra(KEY_EXTRA_PROGRESS, i);
            mLbm.sendBroadcast(broadcastIntent);
            SystemClock.sleep(MainActivity.EMIT_DELAY_MS);
        }

        broadcastIntent = new Intent(MainActivity.UPDATE_PROGRESS_FILTER);
        broadcastIntent.putExtra(KEY_EXTRA_BUSY, false);
        broadcastIntent.putExtra(KEY_EXTRA_PROGRESS, 0);
        mLbm.sendBroadcast(broadcastIntent);
    }
}
