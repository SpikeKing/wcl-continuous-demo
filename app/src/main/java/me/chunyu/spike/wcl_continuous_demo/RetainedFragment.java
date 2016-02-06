package me.chunyu.spike.wcl_continuous_demo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import rx.Observable;
import rx.subjects.PublishSubject;

public class RetainedFragment extends Fragment {
    private CustomAsyncTask mCustomAsyncTask;
    private boolean mBusy;

    private RetainedFragment mRetainedFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        FragmentManager fm = getFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag(RETAINED_FRAGMENT);
    }

    public CustomAsyncTask getCustomAsyncTask() {
        return mCustomAsyncTask;
    }

    public void setCustomAsyncTask(CustomAsyncTask customAsyncTask) {
        mCustomAsyncTask = customAsyncTask;
    }

    public boolean isBusy() {
        return mBusy;
    }

    public void setBusy(boolean busy) {
        mBusy = busy;
    }
}
