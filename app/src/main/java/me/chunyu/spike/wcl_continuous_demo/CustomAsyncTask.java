package me.chunyu.spike.wcl_continuous_demo;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.lang.ref.WeakReference;

public class CustomAsyncTask extends AsyncTask<Void, Integer, Void> {

    private WeakReference<MainActivity> mActivity; // 弱引用Activity, 防止内存泄露

    private boolean mCompleted = false; // 是否完成

    // 设置Activity控制ProgressBar
    public void setActivity(MainActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    // 判断是否完成
    public boolean isCompleted() {
        return mCompleted;
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 1; i < MainActivity.MAX_PROGRESS + 1; i++) {
            SystemClock.sleep(MainActivity.EMIT_DELAY_MS); // 暂停时间
            publishProgress(i); // AsyncTask的方法, 调用onProgressUpdate, 表示完成状态
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mActivity.get().setProgressValue(progress[0]); // 更新ProgressBar的值
        mActivity.get().setProgressPercentText(progress[0]); // 设置文字
    }

    @Override
    protected void onPreExecute() {
        mActivity.get().setProgressText("开始异步任务..."); // 准备开始
        mCompleted = false;
    }

    @Override
    protected void onPostExecute(Void result) {
        mCompleted = true; // 结束
        mActivity.get().setBusy(false);
        mActivity.get().setProgressValue(0);
    }
}
