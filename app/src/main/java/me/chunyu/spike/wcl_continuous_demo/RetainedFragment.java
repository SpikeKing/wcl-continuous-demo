package me.chunyu.spike.wcl_continuous_demo;

import android.app.Fragment;
import android.os.Bundle;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * 用于存储异步进程的Fragment
 *
 * @author wangchenlong
 */
public class RetainedFragment extends Fragment {
    private CustomAsyncTask mCustomAsyncTask; // 定制的异步任务
    private Observable<Long> mObservable; // 观察者
    private PublishSubject<Long> mSubject; // 主题

    private int mMode; // 进度条模式
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

    public Observable<Long> getObservable() {
        return mObservable;
    }

    public void setObservable(Observable<Long> observable) {
        mObservable = observable;
    }

    public PublishSubject<Long> getSubject() {
        return mSubject;
    }

    public void setSubject(PublishSubject<Long> subject) {
        mSubject = subject;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public boolean isBusy() {
        return mBusy;
    }

    public void setBusy(boolean busy) {
        mBusy = busy;
    }
}
