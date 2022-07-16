package com.yushi.sortreveal.floating;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.yushi.sortreveal.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * function:
 * describe:
 * Created By uatql992792 on 2021/6/24.
 */
public class StickyItemDecoration extends RecyclerView.ItemDecoration {
    private View groupView;
    private GroupViewCallback groupViewCallback;
    private Adapter adapter;
    //    private List<? extends ItemBean> dataList;
    private int mGroupViewHeight;
    private String curGroupName;
    private String preGroupName;
    private View mHeadView;
    private int mHeadHeight;
//    private int  groupBottomMargin = 0;

//    public void setDataList(List<? extends ItemBean> dataList) {
//        this.dataList = dataList;
//    }


    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public void setHeadView(View headView) {
        this.mHeadView = headView;
    }

    public void setGroupView(View groupView) {
        this.groupView = groupView;

//        ViewGroup.LayoutParams params = groupView.getLayoutParams();
//        if (params instanceof ViewGroup.MarginLayoutParams) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
////                groupLeftMargin = ((ViewGroup.MarginLayoutParams) params).getMarginStart();
////                groupBottomMargin = ((ViewGroup.MarginLayoutParams) params).bottomMargin;
//            }
//        }
    }

    public void setGroupViewCallback(GroupViewCallback groupViewCallback) {
        this.groupViewCallback = groupViewCallback;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
//        if (adapter == null) return;
        int position = parent.getChildAdapterPosition(view);
        Log.i("testItemDecoration", "testItemDecoration: getItemOffsets\tItemCount:" + parent.getChildCount() + "\tposition:" + parent.getChildAdapterPosition(view));
        if (mGroupViewHeight == 0) {
            if (groupView.getLayoutParams() == null) {
                throw new RuntimeException(" groupView 的 LayoutParams 为null");
            }
            mGroupViewHeight = view2Bitmap(groupView, ((ViewGroup) parent.getParent()).getWidth(), ((ViewGroup) parent.getParent()).getHeight()).getHeight();
        }
        if (mHeadHeight == 0 && mHeadView != null) {
            mHeadHeight = view2Bitmap(mHeadView, ((ViewGroup) parent.getParent()).getWidth(), ((ViewGroup) parent.getParent()).getHeight()).getHeight();

        }

        if (isAddGroupView(position)) {
            outRect.top = mGroupViewHeight;
        }
        if (position == 0) {
            outRect.top += mHeadHeight;
        }
    }


    private boolean isAddGroupView(int position) {
        if (position == 0) return true;
        curGroupName = adapter.getGroupName(position);
        preGroupName = adapter.getGroupName(position - 1);
        return !TextUtils.equals(curGroupName, preGroupName);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        Log.i("testItemDecoration", "testItemDecoration: onDraw\tItemCount:" + parent.getChildCount());
    }

//    public int getChildCount() {
//
//    }

    private synchronized void drawGroupView(Canvas c, View groupView, int left, int top, RecyclerView parent) {
        Bitmap bitmap = view2Bitmap(groupView, ((ViewGroup) parent.getParent()).getWidth(), ((ViewGroup) parent.getParent()).getHeight());
        c.drawBitmap(bitmap, left, top, null);
    }

    private void setPositionInner(RecyclerView recyclerView, int position, int offsetHeight) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//        final StickyItemDecoration itemDecoration = (StickyItemDecoration) recyclerView.getItemDecorationAt(0);
        if (layoutManager != null) {
            View view = layoutManager.findViewByPosition(position);
            if (view != null && view.getTop() != view.getTop() - offsetHeight) {
                recyclerView.smoothScrollBy(0, view.getTop() - offsetHeight, null, 10);
            }

        }
    }


    public void setPosition(RecyclerView recyclerView, final int position) {
        if (recyclerView == null) return;
//        recyclerView.stopScroll();
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int offsetHeight = getGroupViewHeight();
        final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            protected void onStop() {
                super.onStop();
                if (position == 0) {
                    setPositionInner(recyclerView, position, offsetHeight);
                }
//                setPositionInner(recyclerView, position, offsetHeight);
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


//            if (layoutManager.findViewByPosition(position) == null) {
//                linearSmoothScroller.setTargetPosition(position);
//                layoutManager.startSmoothScroll(linearSmoothScroller);
//            } else {
//                setPositionInner(recyclerView, position, offsetHeight);
//            }
        }
    }


    private void addGroupView(Canvas c, int position, View groupView, int left, int top, RecyclerView parent) {
        if (isAddGroupView(position)) {
            adapter.adapter(groupView, position);
            drawGroupView(c, groupView, left, top - mGroupViewHeight, parent);
        }
    }

    public int getHeadHeight() {
        return mHeadHeight;
    }

    public int getGroupViewHeight() {
        return mGroupViewHeight;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (adapter == null) return;
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            if (position == 0 && view.getTop() > mGroupViewHeight) {
                ViewGroup.LayoutParams params = mHeadView.getLayoutParams();
                int leftMargin = 0;
                if (params instanceof ViewGroup.MarginLayoutParams) {
                    leftMargin = ((ViewGroup.MarginLayoutParams) params).leftMargin;
                }
                drawGroupView(c, mHeadView, leftMargin, view.getTop() - (mGroupViewHeight + mHeadHeight), parent);
            }
            if (parent.findChildViewUnder(view.getX(), mGroupViewHeight) == null) {
                if (view.getBottom() < mGroupViewHeight) {
                    adapter.adapter(groupView, position);
                    int top = view.getBottom() - mGroupViewHeight;
                    drawGroupView(c, groupView, 0, top, parent);
                }
                addGroupView(c, position, groupView, 0, view.getTop(), parent);
                continue;
            }

            if (i == 0) {
                adapter.adapter(groupView, position);
                drawGroupView(c, groupView, 0, 0, parent);
                continue;
            }
            addGroupView(c, position, groupView, 0, view.getTop(), parent);
        }
    }


    public interface GroupViewCallback {
        View getItemGroupView();

        int getRecyclerViewWidth();
    }

    public interface ItemBean {
        String getGroupName();

        String getValue();
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
//            int endMargin = 0, leftMargin = 0, topMargin = 0, bottomMargin = 0;
//            if (params instanceof ViewGroup.MarginLayoutParams) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    endMargin = ((ViewGroup.MarginLayoutParams) params).getMarginEnd();
//                    leftMargin = ((ViewGroup.MarginLayoutParams) params).getMarginStart();
//                    topMargin = ((ViewGroup.MarginLayoutParams) params).topMargin;
//                    bottomMargin = ((ViewGroup.MarginLayoutParams) params).bottomMargin;
//                }
//            }
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
}
