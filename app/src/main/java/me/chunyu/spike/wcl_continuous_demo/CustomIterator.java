package me.chunyu.spike.wcl_continuous_demo;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 定制迭代器
 * <p>
 * Created by wangchenlong on 16/2/11.
 */
public class CustomIterator implements Iterable<Long> {

    private List<Long> mNumberList = new ArrayList<>();

    public CustomIterator() {
        for (long i = 0; i < MainActivity.MAX_PROGRESS; i++) {
            mNumberList.add(i + 1);
        }
    }

    @Override public Iterator<Long> iterator() {
        return new Iterator<Long>() {
            private int mCurrentIndex = 0;

            @Override public boolean hasNext() {
                return mCurrentIndex < mNumberList.size() && mNumberList.get(mCurrentIndex) != null;
            }

            @Override public Long next() {
                SystemClock.sleep(MainActivity.EMIT_DELAY_MS);
                return mNumberList.get(mCurrentIndex++);
            }

            // 不允许使用
            @Override public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
