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
    //一级列表的View
    private View groupView;
    //实现一级列表的适配器
    private Adapter adapter;
    //一级列表View的高度，配合实现逻辑计算
    private int mGroupViewHeight;
    //    private int mGroupViewTopMargin;
    //在一二级列表的顶部增加的View，可选
    private View mHeadView;
    //在一二级列表的顶部增加的View高度，配合实现逻辑计算
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


    /**
     * item在滑动时进入，确定将要进入的item属性
     *
     * @param outRect item所占的区域，改变这个宽高，可以改变最终输出到RecyclerView对应item所占的区域
     * @param view    当前要改变的item
     * @param parent  RecyclerView
     * @param state   RecyclerView 的当前状态
     */
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        Log.i("testItemDecoration", "testItemDecoration: getItemOffsets\tItemCount:" + parent.getChildCount() + "\tposition:" + parent.getChildAdapterPosition(view));
        //一级列表View的高度如果没有初始化
        if (mGroupViewHeight == 0) {
            if (groupView.getLayoutParams() == null) {
                throw new RuntimeException(" groupView 的 LayoutParams 为null");
            }
//            mGroupViewTopMargin = getViewMargin(groupView, "top");
            //初始化一级列表View的高度
            mGroupViewHeight = view2Bitmap(groupView, ((ViewGroup) parent.getParent()).getWidth(), ((ViewGroup) parent.getParent()).getHeight()).getHeight();

        }
        //顶部View如果没有初始化
        if (mHeadHeight == 0 && mHeadView != null) {
            //初始化顶部View
            mHeadHeight = view2Bitmap(mHeadView, ((ViewGroup) parent.getParent()).getWidth(), ((ViewGroup) parent.getParent()).getHeight()).getHeight();
        }

        //当前的二级position是否为新的一级列表
        if (isNewGroupName(position)) {
            //给二级position增加一级列表View的高度
            outRect.top += mGroupViewHeight;
        }
        if (position == 0 && mHeadHeight > 0) {
            //当前的二级position是第一个列表，增加顶部View的高度
            outRect.top += mHeadHeight;
        }
    }


    /**
     * 当前的二级position是否为新的一级列表
     *
     * @param position position
     * @return true/false
     */
    private boolean isNewGroupName(int position) {
        if (position == 0) return true;
        String curGroupName = adapter.getGroupName(position);
        String preGroupName = adapter.getGroupName(position - 1);
        return !TextUtils.equals(curGroupName, preGroupName);
    }


    /**
     * 给指定Position的View往下滚动指定的偏移
     *
     * @param recyclerView recyclerView
     * @param position     position
     * @param offsetHeight 要往下滚动的偏移
     */
    private void setPositionInner(RecyclerView recyclerView, int position, int offsetHeight) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            View view = layoutManager.findViewByPosition(position);
            if (view != null && view.getTop() != view.getTop() - offsetHeight) {
                recyclerView.smoothScrollBy(0, view.getTop() - offsetHeight, null, 10);
            }
        }
    }


    /**
     * 把当前列表滚动到指定position
     *
     * @param recyclerView recyclerView
     * @param position     滚动到指定position
     */
    public void setPosition(final RecyclerView recyclerView, final int position) {
        if (recyclerView == null) return;
        recyclerView.stopScroll();
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        final int offsetHeight = getGroupViewHeight();
        //滚动到指定Position的实现对象
        final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            //滚动停止后回调的方法
            @Override
            protected void onStop() {
                super.onStop();
                //如果滚动到了第一个position，并且有添加顶部View
                if (position == 0 && mHeadHeight > 0) {
                    /*
                     * 再滚动下移「顶部View」的高度，以此来显示出「顶部View」，因为「顶部View」的展示没有在getItemOffsets中实现，所以在这里进行偏移。
                     * 这个顶部View还没有实现完，这里只是起到一个辅助构思的作用，后面可以考虑是否能把这个的实现迁移到getItemOffsets方法里，这样在实现上比较一致性。
                     */
                    setPositionInner(recyclerView, position, offsetHeight);
                }
            }

            //配置横向滚动的配置
            @Override
            protected int getHorizontalSnapPreference() {
                //滚动过后，指定的position在顶部，SNAP_TO_END为指定的position在底部。
                return SNAP_TO_END;
            }

            //配置竖向滚动的配置
            @Override
            protected int getVerticalSnapPreference() {
                //滚动过后，指定的position在顶部，SNAP_TO_END为指定的position在底部。
                return SNAP_TO_START;
            }
        };
        if (layoutManager != null) {
            //滚动到指定Position
            linearSmoothScroller.setTargetPosition(position);
            //开始滚动
            layoutManager.startSmoothScroll(linearSmoothScroller);
        }
    }


    /**
     * 是否给当前position的item绘制所属的一列表View
     *
     * @param c         item的绘制对象
     * @param position  position
     * @param groupView 一级列表View
     * @param left      绘制的X起始轴
     * @param top       绘制的Y起始轴
     * @param topMargin topMargin
     * @param parent    RecyclerView
     */
    private void isAddGroupView(Canvas c, int position, View groupView, int left, int top, int topMargin, RecyclerView parent) {
        if (isNewGroupName(position)) {
            adapter.adapter(groupView, position);
            drawView(c, groupView, left, top - mGroupViewHeight - topMargin, parent);
        }
    }


    public int getGroupViewHeight() {
        return mGroupViewHeight;
    }


    /**
     * item在滑动进入前调用，给当前item增加绘制装饰
     *
     * @param c      当前item的绘制对象
     * @param parent RecyclerView
     * @param state  RecyclerView的状态
     */
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        drawItemDecoration(c, parent);
    }

    /**
     * 绘制item的装饰
     *
     * @param c      item的绘制对象
     * @param parent RecyclerView
     */
    private void drawItemDecoration(Canvas c, RecyclerView parent) {
        if (adapter == null) return;
        //得到RecyclerView里面的子View
        int count = parent.getChildCount();
        //遍历RecyclerView里面的子View
        for (int i = 0; i < count; i++) {
            //通过index拿到子view
            View view = parent.getChildAt(i);
            //拿到RecyclerView中该子view所在的position
            int position = parent.getChildAdapterPosition(view);
            //拿到该子view的topMargin
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int topMargin = 0;
            if (params instanceof ViewGroup.MarginLayoutParams) {
                topMargin = ((ViewGroup.MarginLayoutParams) params).topMargin;
            }
            //如果存在顶部view，并且当前position是第一个，并且该view的所在位置有显示出所属的一级列表View
            if (mHeadView != null && position == 0 && view.getTop() > mGroupViewHeight) {
                //拿到顶部view的leftMargin
                params = mHeadView.getLayoutParams();
                int leftMargin = 0;
                if (params instanceof ViewGroup.MarginLayoutParams) {
                    leftMargin = ((ViewGroup.MarginLayoutParams) params).leftMargin;
                }
                //在当前item的位置上绘制
                drawView(c, mHeadView, leftMargin, view.getTop() - (mGroupViewHeight + mHeadHeight), parent);
                Log.i("testTopException", "testTopException: 1 top" + (view.getTop() - (mGroupViewHeight + mHeadHeight)));
                continue;
            }
            //如果一级列表的View不等于null，并且RecyclerView在指定的x，y坐标里找不到子view
            if (groupView != null && parent.findChildViewUnder(view.getX(), mGroupViewHeight) == null) {
                //如果该子view的topMargin等于0，或者在topMargin不等于0的情况下，在指定的x，y坐标里还是找不到子view
                if (topMargin == 0 || parent.findChildViewUnder(view.getX(), mGroupViewHeight + topMargin) == null) {
                    //如果该子view已经滑动到了一级列表view的上面
                    if (view.getBottom() < mGroupViewHeight) {
                        //给一级列表view设置属性
                        adapter.adapter(groupView, position);
                        //计算出一级列表View的Y起始轴，以此来达到视觉向上推动
                        int top = view.getBottom() - mGroupViewHeight;
                        //把一级列表的View转换成Bitmap绘制到item上
                        drawView(c, groupView, 0, top, parent);
                        Log.i("testTopException", "testTopException: 2 top" + top + "\t" + mGroupViewHeight);
                    }
                    isAddGroupView(c, position, groupView, 0, view.getTop(), topMargin, parent);
                    continue;
                }
            }
            //如果是第一个子view
            if (i == 0) {
                //给一级列表view设置属性
                adapter.adapter(groupView, position);
                //把一级列表的View转换成Bitmap绘制到item上
                drawView(c, groupView, 0, 0, parent);
                Log.i("testTopException", "testTopException: 3 top");
                continue;
            }
            isAddGroupView(c, position, groupView, 0, view.getTop(), topMargin, parent);
        }
    }

    /**
     * 给item的位置上绘制指定的View
     *
     * @param c      item的绘制对象
     * @param view   要绘制的View
     * @param left   绘制的X轴起点
     * @param top    绘制的Y轴起点
     * @param parent RecyclerView
     */
    private synchronized void drawView(Canvas c, View view, int left, int top, RecyclerView parent) {
        //把View转换成Bitmap
        Bitmap bitmap = view2Bitmap(view, ((ViewGroup) parent.getParent()).getWidth(), ((ViewGroup) parent.getParent()).getHeight());
        //拿到要绘制View的leftMargin
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            left += layoutParams.leftMargin;
        }
        //开始绘制Bitmap
        c.drawBitmap(bitmap, left, top, null);
//        view.draw(c);
    }


    public interface Adapter {
        /**
         * 给一级列表设置属性
         *
         * @param holder   当前position一级列表对应的View
         * @param position position
         */
        void adapter(View holder, int position);

        /**
         * 获取一级列表的名称
         *
         * @param position position
         * @return 当前position一级列表的名称
         */
        String getGroupName(int position);
    }

    /**
     * 把View转成Bitmap，因为父容器是一个RecyclerView，因此不能按照addView的方式添加一级列表，因为这样一级列表就没有办法吸顶，因此只能转成Bitmap后，画在当前item的上面，以此来达到吸顶的效果。
     *
     * @param view            view
     * @param parentMaxWidth  父容器的宽度
     * @param parentMaxHeight 父容器的高度
     * @return Bitmap
     */
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


    public interface ItemBean {
        String getGroupName();
    }
}
