package com.zh.android.slidelockscreen.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * <b>Package:</b> com.zh.android.slidelockscreen <br>
 * <b>Create Date:</b> 2019-12-23  13:34 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 闪动文字 <br>
 */
public class ShineTextView extends TextView {
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private int mViewWidth = 0;
    private int mTranslate = 0;

    public ShineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                Paint paint = getPaint();
                mLinearGradient = new LinearGradient(0,
                        0,
                        mViewWidth,
                        0,
                        new int[]{getCurrentTextColor(), 0xffffffff, getCurrentTextColor()},
                        null,
                        Shader.TileMode.CLAMP);
                paint.setShader(mLinearGradient);
                mGradientMatrix = new Matrix();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mGradientMatrix != null) {
            mTranslate += mViewWidth / 5;
            if (mTranslate > 2 * mViewWidth) {
                mTranslate = -mViewWidth;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            //每100毫秒执行onDraw()
            postInvalidateDelayed(80);
        }
    }
}