package com.yushi.sortreveal.floating;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * function:
 * describe:
 * Created By uatql992792 on 2021/6/30.
 */
public class StickyRecyclerView extends RecyclerView {
//    private int mHeadViewId;

    public StickyRecyclerView(@NonNull Context context) {
        super(context);
    }

    private StickyItemDecoration stickyDecoration;

    public StickyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StickyRecyclerView);
//        mHeadViewId = typedArray.getResourceId(R.styleable.StickyRecyclerView_head_view_id, 0);
//        typedArray.recycle();
    }




    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        post(new Runnable() {
//            @Override
//            public void run() {
//
//
//                setPosition(30);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        SystemClock.sleep(2000);
////                        setPosition(35);
//                        post(new Runnable() {
//                            @Override
//                            public void run() {
//                                setPosition(60);
//                            }
//                        });
//
//                        SystemClock.sleep(2000);
////                        setPosition(35);
//                        post(new Runnable() {
//                            @Override
//                            public void run() {
//                                setPosition(72);
//                            }
//                        });
//
//                    }
//                }).start();
//
//            }
//        });

    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Log.i("testXY", "testXY: " + e.getY());
        super.onTouchEvent(e);
//        if (e.getY() < 300)
        return true;

    }
}
