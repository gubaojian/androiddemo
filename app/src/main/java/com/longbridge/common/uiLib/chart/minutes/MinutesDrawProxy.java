package com.longbridge.common.uiLib.chart.minutes;

import android.content.Context;
import android.graphics.Canvas;
import android.view.GestureDetector;

/**
 * @author Kyle
 * on 2020/7/24
 */
public abstract class MinutesDrawProxy {
    protected DataChangeObserver mObserver;
    protected TouchProxy mTouchProxy;

    public abstract void onDraw(Canvas canvas, float topY, float bottomY, float customTitleHeight);

    /**
     * 用于主题颜色变更的通知
     */
    public void skinChanged() {
    }

    /**
     * 因为分时图存在大量数据，所以这里采用回调的形式，优先去计算绘制出path的路径，然后再去触发invalidate，减少在onDraw里的负担
     */
    public void setDataObserver(DataChangeObserver observer, MinutesChart chart) {
        chart.setDrawProperty((drawWidth, startMargin, topY, bottomY) -> initPath(drawWidth, startMargin, topY, bottomY));
        mObserver = observer;
    }

    protected void setTouchProxy(TouchProxy proxy) {
        mTouchProxy = proxy;
    }

    /**
     * drawWidth可绘制的宽度，有别于组件宽度，折线图有可能左边是有间距不去画的
     * startMargin 对应drawWidth所预留出来的宽度
     * topY 绘制的顶部位置，默认是2dp，稍微把顶部的线下移一点，避免线很细的时候，画不全
     * bottomY 绘制的底部位置
     */
    public abstract void initPath(float drawWidth, float startMargin, float topY, float bottomY);

    public interface DataChangeObserver {
        void onDataChange();

        void refreshUi();

        void post(Runnable runnable);

        void postDelay(Runnable runnable, long delay);

        void removeRunnable(Runnable runnable);
    }

    protected boolean drawTopLine() {
        return true;
    }

    protected boolean drawBottomLine() {
        return true;
    }

    protected GestureDetector getGestureDetector(Context context) {
        return null;
    }
}
