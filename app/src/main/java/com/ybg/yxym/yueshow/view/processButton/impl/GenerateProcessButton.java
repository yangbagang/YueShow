package com.ybg.yxym.yueshow.view.processButton.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.ybg.yxym.yueshow.view.processButton.ProcessButton;

/**
 * Created by yangbagang on 2016/11/6.
 */

public class GenerateProcessButton extends ProcessButton {

    public GenerateProcessButton(Context context) {
        super(context);
    }

    public GenerateProcessButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GenerateProcessButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void drawProgress(Canvas canvas) {
        float scale = (float) getProgress() / (float) getMaxProgress();
        float indicatorHeight = (float) getMeasuredHeight() * scale;

        getProgressDrawable().setBounds(0, 0, getMeasuredWidth(), (int) indicatorHeight);
        getProgressDrawable().draw(canvas);
    }

}
