package com.ybg.yxym.yueshow.view.processButton.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.ybg.yxym.yueshow.view.processButton.ProcessButton;

/**
 * Created by yangbagang on 2016/11/6.
 */

public class SubmitProcessButton extends ProcessButton {

    public SubmitProcessButton(Context context) {
        super(context);
    }

    public SubmitProcessButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubmitProcessButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void drawProgress(Canvas canvas) {
        float scale = (float) getProgress() / (float) getMaxProgress();
        float indicatorWidth = (float) getMeasuredWidth() * scale;

        getProgressDrawable().setBounds(0, 0, (int) indicatorWidth, getMeasuredHeight());
        getProgressDrawable().draw(canvas);
    }

}
