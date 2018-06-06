package com.ray.verticalviewpagerandscrolllayout;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 垂直滑动的ViewPager
 */
public class VerticalViewPager extends ViewPager {

    /**
     * 是否可以拖动
     */
    private boolean mIsUnableToDrag;

    /**
     * 判定为拖动的最小移动像素数
     */
    private int mTouchSlop;

    /**
     * 手指按下时的屏幕坐标（X）
     */
    private float mXDown;
    /**
     * 手指按下的纵坐标（Y）
     */
    private float mYDown;

    /**
     * 手指当时所处的屏幕坐标（X）
     */
    private float mXMove;

    /**
     * 手指现在所处的坐标(Y)
     */
    private float mYMove;

    public VerticalViewPager(Context context) {
        this(context, null);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollHorizontally(direction);
    }

    private void init() {
        setPageTransformer(true, new VerticalPageTransformer());
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        // 获取TouchSlop值
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(getContext())) / 3;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getX();
                mYDown = ev.getY();
                super.onInterceptTouchEvent(flipXY(ev));
                flipXY(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getX();
                mYMove = ev.getY();
                float diffX = Math.abs(mXMove - mXDown);
                float diffY = Math.abs(mYMove - mYDown);
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diffY > diffX) {
                    mIsUnableToDrag = true;
                } else {
                    mIsUnableToDrag = false;
                }
                super.onInterceptTouchEvent(flipXY(ev));
                flipXY(ev);
                return mIsUnableToDrag;
        }
        return mIsUnableToDrag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                super.onTouchEvent(flipXY(ev));
                flipXY(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                super.onTouchEvent(flipXY(ev));
                flipXY(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                super.onTouchEvent(flipXY(ev));
                flipXY(ev);
                mIsUnableToDrag = false;
                break;
        }
        return true;
    }

    private MotionEvent flipXY(MotionEvent ev) {
        final float width = getWidth();
        final float height = getHeight();
        final float x = (ev.getY() / height) * width;
        final float y = (ev.getX() / width) * height;
        ev.setLocation(x, y);
        return ev;
    }

    private static final class VerticalPageTransformer implements PageTransformer {
        @Override
        public void transformPage(View view, float position) {
            final int pageWidth = view.getWidth();
            final int pageHeight = view.getHeight();
            if (position < -1) {
                view.setAlpha(0);
            } else if (position <= 1) {
                view.setAlpha(1);
                view.setTranslationX(pageWidth * -position);
                float yPosition = position * pageHeight;
                view.setTranslationY(yPosition);
            } else {
                view.setAlpha(0);
            }
        }
    }
}