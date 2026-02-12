package com.zhongpin.mvvm_android.view;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing; // 相邻 item 之间的间隙

    public HorizontalSpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        if (layoutManager != null) {
            int spanCount = layoutManager.getSpanCount();
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            // 关键修正：正确计算左右偏移量
            // 总间隙数 = spanCount - 1（例如 3 列有 2 个间隙）
            int totalHorizontalSpacing = spacing * (spanCount - 1);

            // 每个 item 应承担的左侧间隙（除第一列外）
            int leftSpacing = column > 0 ? spacing : 0;

            // 每个 item 应承担的右侧间隙（除最后一列外）
            int rightSpacing = column < spanCount - 1 ? spacing : 0;

            // 计算左右偏移量（注意符号）
            outRect.left = leftSpacing;
            outRect.right = 0; // 关键修正：右侧间隙由下一个 item 的 leftSpacing 处理
            // 不设置垂直方向的间隙
        }

    }
}
