package com.zj.calendar;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;

final class YearViewAdapter extends BaseRecyclerAdapter<Month> {
    private CalendarViewDelegate mDelegate;
    private int mItemWidth, mItemHeight;

    YearViewAdapter(Context context) {
        super(context);
    }

    final void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
    }


    final void setYearViewSize(int width, int height) {
        this.mItemWidth = width;
        this.mItemHeight = height;
    }

    @Override
    RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        YearView yearView = null;
        if (TextUtils.isEmpty(mDelegate.getYearViewClassPath())) {
            yearView = new DefaultYearView(mContext);
        } else {
            try {
                Class<?> cls = mDelegate.getYearViewClass();
                if (cls != null) {
                    Constructor<?> constructor = cls.getConstructor(Context.class);
                    yearView = (YearView) constructor.newInstance(mContext);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (yearView == null) {
            yearView = new DefaultYearView(mContext);
        }
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
        yearView.setLayoutParams(params);
        return new YearViewHolder(yearView, mDelegate);
    }

    @Override
    void onBindViewHolder(RecyclerView.ViewHolder holder, Month item, int position) {
        YearViewHolder h = (YearViewHolder) holder;
        YearView view = h.mYearView;
        view.init(item.getYear(), item.getMonth());
        view.measureSize(mItemWidth, mItemHeight);
    }

    private static class YearViewHolder extends RecyclerView.ViewHolder {
        YearView mYearView;

        YearViewHolder(View itemView, CalendarViewDelegate delegate) {
            super(itemView);
            mYearView = (YearView) itemView;
            mYearView.setup(delegate);
        }
    }
}
