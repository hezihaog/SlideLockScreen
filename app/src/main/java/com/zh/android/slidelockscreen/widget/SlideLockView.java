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
     * 滑动滑块
     */
    private View mLockBtn;
    /**
     * 拽托帮助类
     */
    private ViewDragHelper mViewDragHelper;
    /**
     * 回调
     */
    private Callback mCallback;
    /**
     * 是否解锁
     */
    private boolean isUnlock = false;

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
        mViewDragHelper = ViewDragHelper.create(this, 0.3f, new ViewDragHelper.Callback() {
            private int mTop;

            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                //判断能拽托的View，这里会遍历内部子控件来决定是否可以拽托，我们只需要滑块可以滑动
                return child == mLockBtn;
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                //拽托子View横向滑动时回调，回调的left，则是可以滑动的左上角x坐标
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
                //拽托子View纵向滑动时回调，锁定顶部padding距离即可，不能不复写，否则少了顶部的padding，位置就偏去上面了
                return getPaddingTop();
            }

            @Override
            public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
                //捕获到拽托的View时回调，获取顶部距离
                mTop = capturedChild.getTop();
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                //获取滑块当前的位置
                int currentLeft = releasedChild.getLeft();
                //获取滑块的宽度
                int lockBtnWidth = mLockBtn.getWidth();
                //获取滑道宽度
                int fullWidth = slideRail.getWidth();
                //一般滑道的宽度，用来判断滑块距离起点近还是终点近
                int halfWidth = fullWidth / 2;
                //松手位置在小于一半，并且滑动速度小于1000，则回到左边
                if (currentLeft <= halfWidth && xvel < 1000) {
                    mViewDragHelper.settleCapturedViewAt(getPaddingStart(), mTop);
                } else {
                    //否则去到右边（宽度，减去padding和滑块宽度）
                    mViewDragHelper.settleCapturedViewAt(fullWidth - getPaddingEnd() - lockBtnWidth, mTop);
                }
                invalidate();
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
                int lockBtnWidth = mLockBtn.getWidth();
                //限制左右临界点
                int fullWidth = slideRail.getWidth();
                //最多的右边
                int leftMaxDistance = fullWidth - getPaddingEnd() - lockBtnWidth;
                int left = mLockBtn.getLeft();
                if (state == ViewDragHelper.STATE_IDLE) {
                    //移动到最右边，解锁完成
                    if (left == leftMaxDistance) {
                        //未解锁才进行解锁回调，由于这个判断会进两次，所以做了标志位限制
                        if (!isUnlock) {
                            isUnlock = true;
                            if (mCallback != null) {
                                mCallback.onUnlock();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //找到需要拽托的滑块
        mLockBtn = findViewById(R.id.lock_btn);
        if (mLockBtn == null) {
            throw new NullPointerException("必须要有一个滑动滑块");
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