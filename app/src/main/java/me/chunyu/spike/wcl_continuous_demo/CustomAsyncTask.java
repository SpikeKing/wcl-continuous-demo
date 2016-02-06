package me.chunyu.spike.wcl_continuous_demo;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.lang.ref.WeakReference;

public class CustomAsyncTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = CustomAsyncTask.class.getSimpleName();
    private WeakReference<MainActivity> mActivity;
    private boolean mCompleted = false;

    public void setActivity(MainActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 1; i < MainActivity.MAX_PROGRESS + 1; i++) {
            SystemClock.sleep(MainActivity.EMIT_DELAY_MS);

            publishProgress(i); // AsyncTask的方法

            Log.d(TAG, "count: " + i);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mActivity.get().setProgressValue((progress[0]));
        mActivity.get().setProgressText("Progress " + progress[0]);
    }

    @Override
    protected void onPreExecute() {
        mActivity.get().setProgressText("Starting Async Task...");
        mCompleted = false;
    }

    @Override
    protected void onPostExecute(Void result) {
        mCompleted = true;
        mActivity.get().setBusy(false);
    }
}
