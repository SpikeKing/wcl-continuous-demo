package me.chunyu.spike.wcl_continuous_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.main_s_modes) Spinner mSModes; // 切换模式
    @Bind(R.id.main_s_track_leaks) Switch mSTrackLeaks; // 检测内存
    @Bind(R.id.main_tv_progress_text) TextView mTvProgressText; // 处理文本
    @Bind(R.id.main_pb_progress_bar) ProgressBar mPbProgressBar; // 处理条
    @Bind(R.id.main_b_start_button) Button mBStartButton; // 开始按钮

    public final static int MAX_PROGRESS = 10; // 最大点
    public final static int EMIT_DELAY_MS = 1000; // 每次间隔

    private CustomAsyncTask mCustomAsyncTask; // 异步任务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPbProgressBar.setMax(MAX_PROGRESS);
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

        // Store in the retained fragment
        mRetainedFragment.setCustomAsyncTask(mCustomAsyncTask);
        mCustomAsyncTask.execute();
    }

    public void setBusy(boolean busy) {
        if (mPbProgressBar.getProgress() > 0 && mPbProgressBar.getProgress() != mPbProgressBar.getMax()) {
            mTvProgressText.setText("Progress: " + mPbProgressBar.getProgress());
        } else {
            mTvProgressText.setText(busy ? "Busy" : "Idle");
        }

        mBStartButton.setText(busy ?  "Busy" : "Start");

        mStartButton.setEnabled(!busy);
        mModeSpinner.setEnabled(!busy);
        mRetainedFragment.setBusy(busy);
    }
}

