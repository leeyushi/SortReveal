package com.yushi.sortreveal.floating;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yushi.SortRevealUtil;
import com.yushi.sortreveal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * function:
 * describe:
 * Created By UATQL992792 on 2021/7/5.
 */
public class TypeLayout extends ViewGroup {
    private int allViewHeight;
    private int typeVerticalPadding;

    private boolean isPreview;

    private List<View> preViewList;
    private TouchListen touchListen;

    public TypeLayout(Context context) {
        super(context);
    }


    public TypeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TypeLayout);
        typeVerticalPadding = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.TypeLayout_typeVerticalPadding, 8));
        isPreview = typedArray.getBoolean(R.styleable.TypeLayout_isPreview, false);
        if (isPreview) {
            preViewList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                TextView textView = new TextView(context);
                textView.setText(getPreviewValue(i));
                preViewList.add(textView);
                addView(textView, new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }
        }
        typedArray.recycle();
    }

    private String getPreviewValue(int position) {
        String result = "null";
        switch (position) {
            case 0:
                result = "A";
                break;
            case 1:
                result = "B";
                break;
            case 2:
                result = "C";
                break;
            case 3:
                result = "D";
                break;
            case 4:
                result = "E";
                break;
        }
        return result;
    }


    public void setPreview(boolean preview) {
        isPreview = preview;
        if (!isPreview && preViewList != null) {
            for (View view : preViewList) {
                removeView(view);
            }
        }
        requestLayout();
    }

    public void setTouchListen(TouchListen touchListen) {
        this.touchListen = touchListen;
    }


    private void setTypeView() {
        int padding = getVerticalPadding();
        int top = padding;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int left = (getMeasuredWidth() - view.getMeasuredWidth()) / 2;
            view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
            top += (view.getMeasuredHeight() + padding);
        }
    }

    private int getVerticalPadding() {
        int measureHeight = getMeasuredHeight();
        int padding = typeVerticalPadding;
        LayoutParams params = getLayoutParams();
        if (params != null && params.height != LayoutParams.WRAP_CONTENT && measureHeight > allViewHeight) {
            padding = (measureHeight - allViewHeight) / (getChildCount() + 1);
        }
        return padding;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        setTypeView();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = 0, heightSize = 0;
        LayoutParams params = getLayoutParams();
        boolean isWrapContent = params != null && params.height == LayoutParams.WRAP_CONTENT;
        for (int i = 0; i < getChildCount(); i++) {
            measureChildWithMargins(getChildAt(i), widthMeasureSpec, 0, heightMeasureSpec, 0);
            View view = getChildAt(i);
            widthSize = Math.max(widthSize, view.getMeasuredWidth());
            heightSize += (view.getMeasuredHeight() + (isWrapContent ? typeVerticalPadding : 0));
        }
        allViewHeight = heightSize;
        if (isWrapContent) heightSize += typeVerticalPadding;
        setMeasuredDimension(View.resolveSize(widthSize, widthMeasureSpec), View.resolveSize(heightSize, heightMeasureSpec));
    }

    //当前触摸的View
    private View curTouchView;

    private View getTouchView(float touchX, float touchY) {
        int centerY = getMeasuredHeight() / 2;
        if (touchY > centerY) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View view = getTouchView(i, (int) touchX, (int) touchY);
                if (view != null) return view;
            }
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                View view = getTouchView(i, (int) touchX, (int) touchY);
                if (view != null) return view;
            }
        }
        return null;
    }

    private View getTouchView(int position, int x, int y) {
        View view = getChildAt(position);
        if (y >= (view.getY() - typeVerticalPadding) && y <= (view.getY() + view.getHeight() + typeVerticalPadding)) {
            return view;
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        super.onTouchEvent(event);
        float touchX = event.getX(), touchY = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                View view = getTouchView(touchX, touchY);
                if (view != null && touchListen != null) {
                    touchListen.down(view);
                    curTouchView = view;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                noRepeatTouchMove(touchX, touchY, view1 -> {
                    touchListen.move(view1);
                    return null;
                });
                break;
            case MotionEvent.ACTION_UP:
                view = getTouchView(touchX, touchY);
                if (touchListen != null) {
                    if (view != null) {
                        touchListen.up(view);
                        curTouchView = null;
                    }
                    touchListen.upEvent();
                }
                break;
        }
        return true;
    }

    //不重复触摸移动事件
    private void noRepeatTouchMove(float touchX, float touchY, Function<View, ?> function) {
        View view = getTouchView(touchX, touchY);
        if (view != null && touchListen != null && view != curTouchView) {
            function.apply(view);
            curTouchView = view;
        }
    }


    public interface TouchListen {
        void down(View view);

        void move(View view);

        void up(View view);

        void upEvent();
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
