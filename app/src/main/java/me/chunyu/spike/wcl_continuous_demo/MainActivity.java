package me.chunyu.spike.wcl_continuous_demo;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.leakcanary.LeakCanary;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 使用各种异步线程处理屏幕旋转.
 * 主要屏幕旋转调用的主要生命周期: onCreate -> onRestoreInstanceState -> onResume
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG-WCL: " + MainActivity.class.getSimpleName();

    // Spinner的位置
    private static final int ASYNC_TASK = 0; // 异步任务
    private static final int INTENT_SERVICE = 1; // 消息服务

    public static final String UPDATE_PROGRESS_FILTER = "update_progress_filter";

    @Bind(R.id.main_s_modes) Spinner mSModesSpinner; // 切换模式
    @Bind(R.id.main_s_track_leaks) Switch mSTrackLeaks; // 检测内存
    @Bind(R.id.main_tv_progress_text) TextView mTvProgressText; // 处理文本
    @Bind(R.id.main_pb_progress_bar) ProgressBar mPbProgressBar; // 处理条
    @Bind(R.id.main_b_start_button) Button mBStartButton; // 开始按钮

    private static final String RETAINED_FRAGMENT = "retained_fragment"; // Fragment的标签
    public final static int MAX_PROGRESS = 10; // 最大点
    public final static int EMIT_DELAY_MS = 1000; // 每次间隔

    // 保留Fragment, 主要目的是为了旋转的时候, 保存异步线程.
    private RetainedFragment mRetainedFragment;

    private CustomAsyncTask mCustomAsyncTask; // 异步任务
    private int mMode = ASYNC_TASK; // 进度条的选择模式

    private LocalBroadcastManager mLbm; // 广播接收器, 配合服务使用.

    private BroadcastReceiver mUpdateProgressReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(CustomService.KEY_EXTRA_BUSY)) {
                setBusy(intent.getBooleanExtra(CustomService.KEY_EXTRA_BUSY, false));
            }
            if (intent.hasExtra(CustomService.KEY_EXTRA_PROGRESS)) {
                int progress = intent.getIntExtra(CustomService.KEY_EXTRA_PROGRESS, 0);
                mPbProgressBar.setProgress(progress);
                setProgressPercentText(progress);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPbProgressBar.setMax(MAX_PROGRESS); // 设置进度条最大值
        mSModesSpinner.setEnabled(mBStartButton.isEnabled()); // 设置是否可以允许
        mLbm = LocalBroadcastManager.getInstance(getApplicationContext());

        // 注册服务接收器
        mLbm.registerReceiver(mUpdateProgressReceiver, new IntentFilter(UPDATE_PROGRESS_FILTER));

        // 设置存储的Fragment
        FragmentManager fm = getFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag(RETAINED_FRAGMENT);

        if (mRetainedFragment == null) {
            mRetainedFragment = new RetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, RETAINED_FRAGMENT).commit();
        }

        // Button点击事件
        mBStartButton.setOnClickListener(this::startProgress);

        // Spinner选择事件, 延迟处理
        mSModesSpinner.post(() -> mSModesSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // 设置旋转模式
                        mMode = position;
                        mRetainedFragment.setMode(mMode);
                    }

                    @Override public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        ));
    }

    // 启动ProgressBar
    private void startProgress(View view) {
        mMode = mSModesSpinner.getSelectedItemPosition();
        mRetainedFragment.setMode(mMode);
        setBusy(true); // 设置繁忙
        switch (mMode) {
            case ASYNC_TASK:
                handleAsyncClick();
                break;
            case INTENT_SERVICE:
                handleIntentServiceClick();
                break;
            default:
                break;
        }
    }

    /**
     * 在onResume中设置setActivity, 因为会执行onRestoreInstanceState方法,
     * 会恢复旋转屏幕之前保存的数据, mPbProgressBar的值, 再设置初始值.
     * 如果移到onCreate时设置, 则会导致Progress值为0, 因为Activity并没有开始恢复数据.
     * 生命周期: onCreate -> onRestoreInstanceState -> onResume.
     */
    @Override protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume() Leak tracking enabled: " + mSTrackLeaks.isChecked());

        // 是否包含内存泄露
        if (mSTrackLeaks.isChecked()) {
            LeakCanary.install(getApplication());
        }

        mMode = mRetainedFragment.getMode();

        switch (mMode) {
            case ASYNC_TASK:
                mCustomAsyncTask = mRetainedFragment.getCustomAsyncTask();

                if (mCustomAsyncTask != null) {
                    if (!mCustomAsyncTask.isCompleted()) {
                        mCustomAsyncTask.setActivity(this);
                    } else {
                        mRetainedFragment.setCustomAsyncTask(null);
                    }
                }
                break;
            default:
                break;

        }

        setBusy(mRetainedFragment.isBusy());
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mLbm.unregisterReceiver(mUpdateProgressReceiver);
    }

    // 设置进度条显示
    public void setProgressText(String string) {
        mTvProgressText.setText(string);
    }

    // 设置进度条显示
    public void setProgressPercentText(int value) {
        mTvProgressText.setText(String.valueOf("进度" + value * 100 / MAX_PROGRESS + "%"));
    }

    // 设置进度条的值
    public void setProgressValue(int value) {
        mPbProgressBar.setProgress(value);
    }

    // 处理异步线程的点击
    private void handleAsyncClick() {
        // 获得异步线程
        mCustomAsyncTask = new CustomAsyncTask();
        mCustomAsyncTask.setActivity(this);

        // 存储异步线程
        FragmentManager fm = getFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag(RETAINED_FRAGMENT);
        mRetainedFragment.setCustomAsyncTask(mCustomAsyncTask);

        // 执行异步线程
        mCustomAsyncTask.execute();
    }

    private void handleIntentServiceClick() {
        mTvProgressText.setText("开始消息服务...");
        Intent intent = new Intent(this, CustomService.class);
        startService(intent);
    }

    // 设置进度条的状态
    public void setBusy(boolean busy) {
        Log.e(TAG, "progress: " + mPbProgressBar.getProgress());
        if (mPbProgressBar.getProgress() > 0 && mPbProgressBar.getProgress() != mPbProgressBar.getMax()) {
            setProgressPercentText(mPbProgressBar.getProgress());
        } else {
            Log.e(TAG, busy ? "繁忙" : "闲置");
            mTvProgressText.setText(busy ? "繁忙" : "闲置");
        }

        // 设置按钮显示
        mBStartButton.setText(busy ? "繁忙" : "开始");

        // 忙就不可以点击
        mBStartButton.setEnabled(!busy);
        mSModesSpinner.setEnabled(!busy);

        // 设置繁忙状态
        mRetainedFragment.setBusy(busy);
    }
}

