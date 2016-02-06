package me.chunyu.spike.wcl_continuous_demo;

import android.app.FragmentManager;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG-WCL: " + MainActivity.class.getSimpleName();

    @Bind(R.id.main_s_modes) Spinner mSModesSpinner; // 切换模式
    @Bind(R.id.main_s_track_leaks) Switch mSTrackLeaks; // 检测内存
    @Bind(R.id.main_tv_progress_text) TextView mTvProgressText; // 处理文本
    @Bind(R.id.main_pb_progress_bar) ProgressBar mPbProgressBar; // 处理条
    @Bind(R.id.main_b_start_button) Button mBStartButton; // 开始按钮

    private static final String RETAINED_FRAGMENT = "retained_fragment"; // Fragment的标签
    public final static int MAX_PROGRESS = 10; // 最大点
    public final static int EMIT_DELAY_MS = 1000; // 每次间隔

    private RetainedFragment mRetainedFragment; // 保留Fragment
    private CustomAsyncTask mCustomAsyncTask; // 异步任务
    private String mMode; // 进度条的选择模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPbProgressBar.setMax(MAX_PROGRESS); // 设置进度条最大值
        mSModesSpinner.setEnabled(mBStartButton.isEnabled()); // 设置是否可以允许

        FragmentManager fm = getFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag(RETAINED_FRAGMENT);

        if (mRetainedFragment == null) {
            Log.d(TAG, "新建存储Fragment");
            mRetainedFragment = new RetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, RETAINED_FRAGMENT).commit();
        } else {
            mMode = mRetainedFragment.getMode();
            if (mMode != null) {
                if (mMode.equals(getString(R.string.async_task))) {
                    mCustomAsyncTask = mRetainedFragment.getCustomAsyncTask();
                }
            }
        }

        mBStartButton.setOnClickListener(v -> {
            mMode = mSModesSpinner.getSelectedItem().toString();
            mRetainedFragment.setMode(mMode);

            setBusy(true);

            if (mMode.equals(getString(R.string.async_task))) {
                handleAsyncClick();
            }
        });

        mSModesSpinner.post(() -> mSModesSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mMode = (String) parent.getItemAtPosition(position);

                        mRetainedFragment.setMode(mMode);

                        Log.d(TAG, "onItemSelected() " + parent.getItemAtPosition(position));

                        if (mMode.equals(getString(R.string.async_task))) {
                            Log.d(TAG, "onCreate() Mode: Async Task");

                            mCustomAsyncTask = mRetainedFragment.getCustomAsyncTask();
                        }
                    }

                    @Override public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        ));
    }

    @Override protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume() Leak tracking enabled: " + mSTrackLeaks.isChecked());

        if (mSTrackLeaks.isChecked()) {
            LeakCanary.install(getApplication());
        }

        mMode = mRetainedFragment.getMode();

        Log.d(TAG, "onResume() Mode: " + mMode + " Button enabled: " + mBStartButton.isEnabled() + " Label: " + mBStartButton.getText() + " Text: " + mTvProgressText.getText());

        if (mMode != null) {
            if (mMode.equals(getString(R.string.async_task))) {
                mCustomAsyncTask = mRetainedFragment.getCustomAsyncTask();

                if (mCustomAsyncTask != null) {
                    if (!mCustomAsyncTask.isCompleted()) {
                        mCustomAsyncTask.setActivity(this);
                    } else {
                        mRetainedFragment.setCustomAsyncTask(null);
                    }
                }
            }
        }

        setBusy(mRetainedFragment.isBusy());
    }

    public void setProgressText(String text) {
        mTvProgressText.setText(text);
    }

    public void setProgressValue(int value) {
        mPbProgressBar.setProgress(value);
    }

    private void handleAsyncClick() {
        mCustomAsyncTask = new CustomAsyncTask();
        mCustomAsyncTask.setActivity(this);

        FragmentManager fm = getFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag(RETAINED_FRAGMENT);
        mRetainedFragment.setCustomAsyncTask(mCustomAsyncTask);
        mCustomAsyncTask.execute();
    }

    public void setBusy(boolean busy) {
        if (mPbProgressBar.getProgress() > 0 &&
                mPbProgressBar.getProgress() != mPbProgressBar.getMax()) {
            mTvProgressText.setText(String.valueOf("Progress: " + mPbProgressBar.getProgress()));
        } else {
            mTvProgressText.setText(busy ? "Busy" : "Idle");
        }

        mBStartButton.setText(busy ? "Busy" : "Start");

        mBStartButton.setEnabled(!busy);
        mSModesSpinner.setEnabled(!busy);
        mRetainedFragment.setBusy(busy);
    }
}

