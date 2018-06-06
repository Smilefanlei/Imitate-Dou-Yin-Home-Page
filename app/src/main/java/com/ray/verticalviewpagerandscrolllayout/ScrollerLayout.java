package com.ray.verticalviewpagerandscrolllayout;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Scroller;

public class ScrollerLayout extends ViewGroup {

    /**
     * 用于完成滚动操作的实例
     */
    private Scroller mScroller;

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

    /**
     * 上次触发ACTION_MOVE事件时的屏幕坐标
     */
    private float mXLastMove;

    /**
     * 界面可滚动的左边界
     */
    private int leftBorder;

    /**
     * 界面可滚动的右边界
     */
    private int rightBorder;

    /**
     * 滑动的方向
     */
    private int mSlidingDirection = 0; // -1 左滑     1 右滑

    /**
     * 当前页码
     */
    private int mTargetIndex = 0;

    public ScrollerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 第一步，创建Scroller的实例  
        mScroller = new Scroller(context);
        // 获取TouchSlop值  
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 为ScrollerLayout中的每一个子控件测量大小  
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            if (childCount > 2) {
                throw new RuntimeException("该布局只能放2个View!!!");
            }
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                // 为ScrollerLayout中的每一个子控件在水平方向上进行布局  
                childView.layout(i * childView.getMeasuredWidth(), 0, (i + 1) * childView.getMeasuredWidth(), childView.getMeasuredHeight());
            }
            // 初始化左右边界值  
            leftBorder = getChildAt(0).getLeft();
            rightBorder = getChildAt(getChildCount() - 1).getRight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getX();
                mYDown = ev.getY();
                mXLastMove = mXDown;
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getX();
                mYMove = ev.getY();
                float diffX = Math.abs(mXMove - mXDown);
                float diffY = Math.abs(mYMove - mYDown);
                mXLastMove = mXMove;
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diffX > mTouchSlop && diffX * 0.5 > diffY) {
                    mIsUnableToDrag = true;
                } else {
                    mIsUnableToDrag = false;
                }
                return mIsUnableToDrag;
        }
        return super.onInterceptTouchEvent(ev);
    }

    // 只要进入到TouchEvent 就全部交易本View处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = event.getX();
                mXLastMove = mXDown;
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getX();
                mYMove = event.getY();
//                float diffX = Math.abs(mXMove - mXDown);
//                float diffY = Math.abs(mYMove - mYDown);

                // 判断 必须要符合左右滑动的条件，若是上下滑动则，不予处理

                int scrolledX = (int) (mXLastMove - mXMove);

                // 滑动方向判断
                mSlidingDirection = mXMove - mXLastMove > 0 ? 1 : -1;

                // 边界控制
                if (getScrollX() + scrolledX < leftBorder) {
                    scrollTo(leftBorder, 0);
                    return mIsUnableToDrag;
                } else if (getScrollX() + getWidth() + scrolledX > rightBorder) {
                    scrollTo(rightBorder - getWidth(), 0);
                    return mIsUnableToDrag;
                }

                // 移动到指定位置
                scrollBy(scrolledX, 0);

                mXLastMove = mXMove;

                // 通知父容器，不要再拦截事件
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (mSlidingDirection == 1) { // 右
                    if (getScrollX() % getWidth() < getWidth() / 5 * 4) {
                        mTargetIndex = getScrollX() / getWidth();
                    }
                } else { // 左
                    if (getScrollX() % getWidth() > getWidth() / 5) {
                        mTargetIndex = getScrollX() / getWidth() + 1;
                    }
                }
                // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
                int dx = mTargetIndex * getWidth() - getScrollX();// 所需距离
                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面  
                mScroller.startScroll(getScrollX(), 0, dx, 0, 800);
                invalidate();

                mIsUnableToDrag = false;
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑  
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}  