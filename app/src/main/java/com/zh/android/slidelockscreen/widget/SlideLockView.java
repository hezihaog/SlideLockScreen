package com.zh.android.slidelockscreen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

import com.zh.android.slidelockscreen.R;

/**
 * <b>Package:</b> com.zh.android.slidelockscreen <br>
 * <b>Create Date:</b> 2019-12-23  11:22 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 滑动解锁-滑道 <br>
 */
public class SlideLockView extends FrameLayout {
    /**
     * 滑动按钮
     */
    private View mLockBtn;
    /**
     * 拽托帮助类
     */
    private ViewDragHelper mViewDragHelper;
    private Callback mCallback;

    public SlideLockView(@NonNull Context context) {
        this(context, null);
    }

    public SlideLockView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLockView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        final SlideLockView slideRail = this;
        mViewDragHelper = ViewDragHelper.create(this, 0.7f, new ViewDragHelper.Callback() {
            private int mTop;

            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                //只有滑动按钮可以拖动
                return child == mLockBtn;
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                if (child != mLockBtn) {
                    return 0;
                }
                int lockBtnWidth = mLockBtn.getWidth();
                //限制左右临界点
                int fullWidth = slideRail.getWidth();
                //最少的左边
                int leftMinDistance = getPaddingStart();
                //最多的右边
                int leftMaxDistance = fullWidth - getPaddingEnd() - lockBtnWidth;
                //修复两端的临界值
                if (left < leftMinDistance) {
                    return leftMinDistance;
                } else if (left > leftMaxDistance) {
                    return leftMaxDistance;
                }
                return left;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                if (child != mLockBtn) {
                    return 0;
                }
                //竖向滑动，锁定顶部padding距离即可，不能不复写，否则少了顶部的padding，位置就偏去上面了
                return getPaddingTop();
            }

            @Override
            public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
                if (capturedChild != mLockBtn) {
                    return;
                }
                //捕获View，获取顶部距离
                mTop = capturedChild.getTop();
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (releasedChild != mLockBtn) {
                    return;
                }
                int currentLeft = releasedChild.getLeft();
                int lockBtnWidth = mLockBtn.getWidth();
                int fullWidth = slideRail.getWidth();
                int halfWidth = fullWidth / 2;
                //松手位置在小于一半，并且滑动速度小于700，则回到左边
                if (currentLeft <= halfWidth && xvel < 700) {
                    mViewDragHelper.settleCapturedViewAt(getPaddingStart(), mTop);
                } else {
                    //否则去到右边（宽度，减去padding和按钮宽度）
                    mViewDragHelper.settleCapturedViewAt(fullWidth - getPaddingEnd() - lockBtnWidth, mTop);
                }
                invalidate();
            }

            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (changedView != mLockBtn) {
                    return;
                }
                int lockBtnWidth = changedView.getWidth();
                //限制左右临界点
                int fullWidth = slideRail.getWidth();
                //最多的右边
                int leftMaxDistance = fullWidth - getPaddingEnd() - lockBtnWidth;
                //改动到最右边，解锁完成
                int dragState = mViewDragHelper.getViewDragState();
                if (dragState == ViewDragHelper.STATE_SETTLING) {
                    if (left >= (leftMaxDistance * 0.9f) && mCallback != null) {
                        mCallback.onUnlock();
                    }
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //找到需要拽托的按钮
        mLockBtn = findViewById(R.id.lock_btn);
        if (mLockBtn == null) {
            throw new NullPointerException("必须要有一个滑动按钮");
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //将onInterceptTouchEvent委托给ViewDragHelper
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将onTouchEvent委托给ViewDragHelper
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //判断是否移动到头了，未到头则继续
        if (mViewDragHelper != null) {
            if (mViewDragHelper.continueSettling(true)) {
                invalidate();
            }
        }
    }

    public interface Callback {
        /**
         * 当解锁时回调
         */
        void onUnlock();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }
}