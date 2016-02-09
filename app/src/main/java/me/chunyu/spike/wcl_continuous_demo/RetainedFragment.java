package me.chunyu.spike.wcl_continuous_demo;

import android.app.Fragment;
import android.os.Bundle;

/**
 * 用于存储异步进程的Fragment
 *
 * @author wangchenlong
 */
public class RetainedFragment extends Fragment {
    private CustomAsyncTask mCustomAsyncTask; // 定制的异步任务

    private String mMode; // 进度条模式
    private boolean mBusy; // 是否繁忙

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public CustomAsyncTask getCustomAsyncTask() {
        return mCustomAsyncTask;
    }

    public void setCustomAsyncTask(CustomAsyncTask customAsyncTask) {
        mCustomAsyncTask = customAsyncTask;
    }

    public String getMode() {
        return mMode;
    }

    public void setMode(String mode) {
        mMode = mode;
    }

    public boolean isBusy() {
        return mBusy;
    }

    public void setBusy(boolean busy) {
        mBusy = busy;
    }
}
