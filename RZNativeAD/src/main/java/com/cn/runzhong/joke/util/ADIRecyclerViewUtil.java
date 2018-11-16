package com.cn.runzhong.joke.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.aspsine.irecyclerview.IRecyclerView;
import com.cn.recycler_refresh.view.footer.LoadMoreFooterView;


/**
 * Created by CN on 2017-11-24.
 */

public class ADIRecyclerViewUtil {
    /**
     * 判断上拉状态
     */
    public static void judgePullRefreshStatus(IRecyclerView recyclerView, int pageCount) {
        if (recyclerView == null)
            return;
        recyclerView.setRefreshing(false);
        if (recyclerView.getCurrentPage()+1 >= pageCount) {
            recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.THE_END);
            recyclerView.setLoadMoreEnabled(false);
//            recyclerView.getLoadMoreFooterView().getLayoutParams().height = 0;
        } else {
            recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.GONE);
            recyclerView.setLoadMoreEnabled(true);
        }
        recyclerView.increasePage();
    }

    public static void setVerticalLinearLayoutManager(Context context, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context));
        recyclerView.addItemDecoration(new LinearSpaceItemDecoration(Color.parseColor("#EEEEEE"), 1, LinearLayoutManager.VERTICAL));
    }

    public static void setVerticalLinearLayoutManager(Context context, RecyclerView recyclerView, int colorResId, int
            size) {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context));
        recyclerView.addItemDecoration(new LinearSpaceItemDecoration(context.getResources().getColor(colorResId), size, LinearLayoutManager.VERTICAL));
    }
    public static void setHorizontalLinearLayoutManager(Context context, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        recyclerView.addItemDecoration(new LinearSpaceItemDecoration(Color.parseColor("#EEEEEE"), 1, LinearLayoutManager.HORIZONTAL));
    }

    public static void setHorizontalLinearLayoutManager(Context context, RecyclerView recyclerView, int colorResId, int
            size) {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        recyclerView.addItemDecoration(new LinearSpaceItemDecoration(context.getResources().getColor(colorResId), size, LinearLayoutManager.HORIZONTAL));
    }

    /**
     * 由于一些view中的状态改变，需要其他view的状态也跟着改变时调用判断
     * 例如checkbox seekbar
     *
     * @param recyclerView
     * @return
     */
    public static boolean canNotifyChanged(RecyclerView recyclerView) {
        return recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.isComputingLayout();
    }

    public static class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;
        private int mSizeV;//行间隔
        private int mSizeH;//列间隔

        public GridSpaceItemDecoration(int colorRes, int sizeV, int sizeH) {
            mDivider = new ColorDrawable(colorRes);
            mSizeV = sizeV;
            mSizeH = sizeH;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {//这个时候垂直和水平方向都需要重新绘制
            drawHorizontal(c, parent);
            drawVertical(c, parent);
        }

        private int getSpanCount(RecyclerView parent) {
            // 列数
            int spanCount = -1;
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            }
            return spanCount;
        }

        private void drawHorizontal(Canvas c, RecyclerView parent) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getLeft() - params.leftMargin;
                final int right = child.getRight() + params.rightMargin
                        + mSizeH;
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mSizeH;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        private void drawVertical(Canvas c, RecyclerView parent) {
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);

                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getTop() - params.topMargin;
                final int bottom = child.getBottom() + params.bottomMargin;
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mSizeV;

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        /**
         * 判断是否是最后一列
         */
        private boolean isLastColum(RecyclerView parent, int pos, int spanCount, int childCount) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager)
                        .getOrientation();
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                    {
                        return true;
                    }
                } else {
                    childCount = childCount - childCount % spanCount;
                    if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                        return true;
                }
            }
            return false;
        }

        /**
         * 是否是最后一行
         */
        private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                    return true;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager)
                        .getOrientation();
                // StaggeredGridLayoutManager 且纵向滚动
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    childCount = childCount - childCount % spanCount;
                    // 如果是最后一行，则不需要绘制底部
                    if (pos >= childCount)
                        return true;
                } else
                // StaggeredGridLayoutManager 且横向滚动
                {
                    // 如果是最后一行，则不需要绘制底部
                    if ((pos + 1) % spanCount == 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition,
                                   RecyclerView parent) {
            int spanCount = getSpanCount(parent);
            int childCount = parent.getAdapter().getItemCount();
            if (isLastRaw(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
            {
                outRect.set(0, 0, mSizeH, 0);
            } else if (isLastColum(parent, itemPosition, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
            {
                outRect.set(0, 0, 0, mSizeV);
            } else {
                outRect.set(0, 0, mSizeH, mSizeV);
            }
        }
    }

    public static class LinearSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final Drawable mDivider;
        private final int mSize;
        private final int mOrientation;

        public LinearSpaceItemDecoration(int color, int size, int orientation) {
            mDivider = new ColorDrawable(color);
            mSize = size;
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left;
            int right;
            int top;
            int bottom;
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                top = parent.getPaddingTop();
                bottom = parent.getHeight() - parent.getPaddingBottom();
                final int childCount = parent.getChildCount();
                for (int i = 0; i < childCount - 1; i++) {
                    final View child = parent.getChildAt(i);
                    final RecyclerView.LayoutParams params =
                            (RecyclerView.LayoutParams) child.getLayoutParams();
                    left = child.getRight() + params.rightMargin;
                    right = left + mSize;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            } else {
                left = parent.getPaddingLeft();
                right = parent.getWidth() - parent.getPaddingRight();
                final int childCount = parent.getChildCount();
                for (int i = 0; i < childCount - 1; i++) {
                    final View child = parent.getChildAt(i);
                    final RecyclerView.LayoutParams params =
                            (RecyclerView.LayoutParams) child.getLayoutParams();
                    top = child.getBottom() + params.bottomMargin;
                    bottom = top + mSize;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                outRect.set(0, 0, mSize, 0);
            } else {
                outRect.set(0, 0, 0, mSize);
            }
        }
    }
    public static class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (Exception e) {
            }
        }
    }
}
