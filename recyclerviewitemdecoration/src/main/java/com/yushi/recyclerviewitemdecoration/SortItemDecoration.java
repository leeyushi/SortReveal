package com.yushi.recyclerviewitemdecoration;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * function:
 * describe:
 * Created By uatql992792 on 2021/6/24.
 */
public class SortItemDecoration extends RecyclerView.ItemDecoration {
    private View groupView;
    private Adapter adapter;
    private int mGroupViewHeight;
    //    private int mGroupViewTopMargin;
    private View mHeadView;
    private int mHeadHeight;


    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public void setHeadView(View headView) {
        this.mHeadView = headView;
    }

    public void setGroupView(View groupView) {
        this.groupView = groupView;
    }


    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        Log.i("testItemDecoration", "testItemDecoration: getItemOffsets\tItemCount:" + parent.getChildCount() + "\tposition:" + parent.getChildAdapterPosition(view));
        if (mGroupViewHeight == 0) {
            if (groupView.getLayoutParams() == null) {
                throw new RuntimeException(" groupView 的 LayoutParams 为null");
            }
//            mGroupViewTopMargin = getViewMargin(groupView, "top");
            mGroupViewHeight = view2Bitmap(groupView, ((ViewGroup) parent.getParent()).getWidth(), ((ViewGroup) parent.getParent()).getHeight()).getHeight();

        }
        if (mHeadHeight == 0 && mHeadView != null) {
            mHeadHeight = view2Bitmap(mHeadView, ((ViewGroup) parent.getParent()).getWidth(), ((ViewGroup) parent.getParent()).getHeight()).getHeight();
        }

        if (isNewGroupName(position)) {
            outRect.top += mGroupViewHeight;
//            if (mGroupViewTopMargin > 0) outRect.top += mGroupViewTopMargin;
//            if (position != 0) outRect.top += getViewTopMargin(view);
        }
        if (position == 0) {
            outRect.top += mHeadHeight;
        }
    }


    private boolean isNewGroupName(int position) {
        if (position == 0) return true;
        String curGroupName = adapter.getGroupName(position);
        String preGroupName = adapter.getGroupName(position - 1);
        return !TextUtils.equals(curGroupName, preGroupName);
    }


    private synchronized void drawView(Canvas c, View view, int left, int top, RecyclerView parent) {
        Bitmap bitmap = view2Bitmap(view, ((ViewGroup) parent.getParent()).getWidth(), ((ViewGroup) parent.getParent()).getHeight());
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            left += layoutParams.leftMargin;
        }
        c.drawBitmap(bitmap, left, top, null);
//        view.draw(c);
    }

    private void setPositionInner(RecyclerView recyclerView, int position, int offsetHeight) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            View view = layoutManager.findViewByPosition(position);
            if (view != null && view.getTop() != view.getTop() - offsetHeight) {
                recyclerView.smoothScrollBy(0, view.getTop() - offsetHeight, null, 10);
            }
        }
    }


    public void setPosition(final RecyclerView recyclerView, final int position) {
        if (recyclerView == null) return;
        recyclerView.stopScroll();
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        final int offsetHeight = getGroupViewHeight();
        final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            protected void onStop() {
                super.onStop();
                if (position == 0 && mHeadHeight != 0) {
                    setPositionInner(recyclerView, position, offsetHeight);
                }
            }

            @Override
            protected int getHorizontalSnapPreference() {
                return SNAP_TO_START;
            }

            @Override
            protected int getVerticalSnapPreference() {
                return SNAP_TO_START;
            }
        };


        if (layoutManager != null) {
            linearSmoothScroller.setTargetPosition(position);
            layoutManager.startSmoothScroll(linearSmoothScroller);
        }
    }


    private void isAddGroupView(Canvas c, int position, View groupView, int left, int top, int topMargin, RecyclerView parent) {
        if (isNewGroupName(position)) {
            adapter.adapter(groupView, position);
            drawView(c, groupView, left, top - mGroupViewHeight - topMargin, parent);
        }
    }


    public int getGroupViewHeight() {
        return mGroupViewHeight;
    }


    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        drawItemDecoration(c, parent);
    }


    private void drawItemDecoration(Canvas c, RecyclerView parent) {
        if (adapter == null) return;
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int topMargin = 0;
            if (params instanceof ViewGroup.MarginLayoutParams) {
                topMargin = ((ViewGroup.MarginLayoutParams) params).topMargin;
            }
            if (mHeadView != null && position == 0 && view.getTop() > mGroupViewHeight) {
                params = mHeadView.getLayoutParams();
                int leftMargin = 0;
                if (params instanceof ViewGroup.MarginLayoutParams) {
                    leftMargin = ((ViewGroup.MarginLayoutParams) params).leftMargin;
                }
                drawView(c, mHeadView, leftMargin, view.getTop() - (mGroupViewHeight + mHeadHeight), parent);
                Log.i("testTopException", "testTopException: 1 top" + (view.getTop() - (mGroupViewHeight + mHeadHeight)));
                continue;
            }

            if (groupView != null && parent.findChildViewUnder(view.getX(), mGroupViewHeight) == null) {
                if (topMargin == 0 || parent.findChildViewUnder(view.getX(), mGroupViewHeight + topMargin) == null) {
                    if (view.getBottom() < mGroupViewHeight) {
                        adapter.adapter(groupView, position);
                        int top = view.getBottom() - mGroupViewHeight;
                        drawView(c, groupView, 0, top, parent);
                        Log.i("testTopException", "testTopException: 2 top" + top + "\t" + mGroupViewHeight);
                    }
                    isAddGroupView(c, position, groupView, 0, view.getTop(), topMargin, parent);
                    continue;
                }
            }

            if (i == 0) {
                adapter.adapter(groupView, position);
                drawView(c, groupView, 0, 0, parent);
                Log.i("testTopException", "testTopException: 3 top");
                continue;
            }
            isAddGroupView(c, position, groupView, 0, view.getTop(), topMargin, parent);
        }
    }


    public interface Adapter {
        void adapter(View holder, int position);

        String getGroupName(int position);
    }

    private Bitmap view2Bitmap(final View view, int parentMaxWidth, int parentMaxHeight) {
        if (view == null) return null;
        boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
        boolean willNotCacheDrawing = view.willNotCacheDrawing();
        view.setDrawingCacheEnabled(true);
        view.setWillNotCacheDrawing(false);
        Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (null == drawingCache) {
            int width, height;
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int endMargin = 0, startMargin = 0;
            if (params instanceof ViewGroup.MarginLayoutParams) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    endMargin = ((ViewGroup.MarginLayoutParams) params).getMarginEnd();
                    startMargin = ((ViewGroup.MarginLayoutParams) params).getMarginStart();
                }
            }
            if (params.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                width = View.MeasureSpec.makeMeasureSpec(parentMaxWidth - endMargin - startMargin, View.MeasureSpec.EXACTLY);
            } else if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                width = View.MeasureSpec.makeMeasureSpec(parentMaxWidth - endMargin - startMargin, View.MeasureSpec.AT_MOST);
            } else {
                width = View.MeasureSpec.makeMeasureSpec(params.width - endMargin - startMargin, View.MeasureSpec.UNSPECIFIED);
            }
            if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                height = View.MeasureSpec.makeMeasureSpec(parentMaxHeight, View.MeasureSpec.EXACTLY);
            } else if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = View.MeasureSpec.makeMeasureSpec(parentMaxHeight, View.MeasureSpec.AT_MOST);
            } else {
                height = View.MeasureSpec.makeMeasureSpec(params.height, View.MeasureSpec.UNSPECIFIED);
            }

            view.measure(width, height);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            drawingCache = view.getDrawingCache();
            if (drawingCache != null) {
                bitmap = Bitmap.createBitmap(drawingCache);
            } else {
                bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
            }
        } else {
            bitmap = Bitmap.createBitmap(drawingCache);
        }
        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCacheDrawing);
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        return bitmap;
    }


    public interface ItemBean  {
        String getGroupName();
    }
}
