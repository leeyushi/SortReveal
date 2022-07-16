package com.yushi.sortreveal.floating;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yushi.SortRevealUtil;
import com.yushi.sortreveal.ListUtils;
import com.yushi.sortreveal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * function:
 * describe:
 * Created By uatql992792 on 2021/6/30.
 */
public class LineFeedLayout extends ViewGroup {
    private int horizontalPadding = SortRevealUtil.dp2px(30);
    private int verticalPadding = SortRevealUtil.dp2px(8);
    //0是追加，1是居中
    private int lineShowType = 1;

    private List<String> testValueList;

    public LineFeedLayout(Context context) {
        super(context);
    }

    public LineFeedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineFeedLayout);
        horizontalPadding = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.LineFeedLayout_horizontalPadding, 30));
        verticalPadding = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.LineFeedLayout_verticalPadding, 8));
        lineShowType = typedArray.getInteger(R.styleable.LineFeedLayout_lineShowType, 0);
        boolean testItem = typedArray.getBoolean(R.styleable.LineFeedLayout_open_test_item, false);
        if (testItem) {
            testValueList = new ListUtils<String>().add(
                    "中国香港",
                    "中国澳门",
                    "中国台湾",
                    "新加坡",
                    "马来西亚",
                    "澳大利亚",
                    "新西兰",
                    "美国",
                    "加拿大",
                    "阿根廷",
                    "埃及",
                    "爱尔兰",
                    "巴黎",
                    "中国",
                    "韩国",
                    "日本");
            for (String testValue : testValueList) {
                TextView textView = new TextView(context);
                LayoutParams layoutParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                textView.setText(testValue);
                addView(textView, layoutParams);
            }
        }
        typedArray.recycle();
    }


    private int getMeasuredViewWidth(int position) {
        int width = getChildAt(position).getMeasuredWidth();
        return isLastPosition(position) ? width : width + horizontalPadding;
    }

    private boolean isLastPosition(int position) {
        return position == getChildCount() - 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        for (int i = 0; i < getChildCount(); i++) {
            measureChildWithMargins(getChildAt(i), widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        int useHeight = 0, useWidth = 0;
        int limitWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = 0, lineMaxWidth = 0;
        int verticalLine = 0, horizontalLine = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            LayoutParams params = view.getLayoutParams();
            int leftMargin = 0, rightMargin = 0;
            if (params instanceof MarginLayoutParams) {
                leftMargin = ((MarginLayoutParams) params).leftMargin;
                rightMargin = ((MarginLayoutParams) params).rightMargin;
            }
            useWidth += (view.getMeasuredWidth() + leftMargin + rightMargin);
            maxHeight = Math.max(maxHeight, view.getMeasuredHeight());
            //是否换行
            boolean isLineFeed = useWidth + horizontalLine * horizontalPadding > limitWidth;
            if (!isLineFeed) {
                horizontalLine++;
            } else if (i != 0) {
                i--;
                verticalLine++;
                useHeight += maxHeight;
                useWidth -= (view.getMeasuredWidth() + leftMargin + rightMargin);
                lineMaxWidth = Math.max(lineMaxWidth, useWidth + (horizontalLine - 1) * horizontalPadding);
                horizontalLine = 0;
                useWidth = 0;
                if (!isLastPosition(i)) maxHeight = 0;
            }
        }
        useHeight += maxHeight;
        useHeight += verticalLine * verticalPadding;
        setMeasuredDimension(View.resolveSize(lineShowType == 0 ? lineMaxWidth : limitWidth, widthMeasureSpec), View.resolveSize(useHeight, heightMeasureSpec));
    }


    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    private void setLineCenterView(int lineViewWidth, int top) {
        int padding = (getMeasuredWidth() - lineViewWidth) / (centerViewList.size() + 1);
        int left = padding;
        for (int i = 0; i < centerViewList.size(); i++) {
            View view = centerViewList.get(i);
            view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
            left += (view.getMeasuredWidth() + padding);
        }
        centerViewList.clear();
    }

    //居中的ViewList
    private List<View> centerViewList;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int leftMargin, rightMargin;
        int top = 0;
        int viewGroupWidth = getMeasuredWidth();
        int lineViewWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            leftMargin = 0;
            rightMargin = 0;
            LayoutParams params = view.getLayoutParams();
            if (params instanceof MarginLayoutParams) {
                leftMargin = ((MarginLayoutParams) params).leftMargin;
                rightMargin = ((MarginLayoutParams) params).rightMargin;
                left += leftMargin;
            }
            //是否换行
            boolean isLineFeed = left + view.getMeasuredWidth() > viewGroupWidth;
            if (!isLineFeed) {
                if (lineShowType == 0) {
                    view.layout(left, top, left + view.getMeasuredWidth() + rightMargin, top + view.getMeasuredHeight());
                    left += getMeasuredViewWidth(i) + rightMargin;
                } else if (lineShowType == 1) {
                    if (centerViewList == null) {
                        centerViewList = new ArrayList<>();
                    }
                    centerViewList.add(view);
                    lineViewWidth += view.getMeasuredWidth();
                    if (i == getChildCount() - 1) {
                        setLineCenterView(lineViewWidth, top);
                        break;
                    }
                    left += getMeasuredViewWidth(i) + rightMargin;
                }
            } else {
                left = 0;
                if (lineShowType == 0) {
                    top += (getChildAt(i - 1).getMeasuredHeight() + verticalPadding);
                    view.layout(left, top, left + view.getMeasuredWidth() + rightMargin, top + view.getMeasuredHeight());
                    if (!isLastPosition(i)) left += getMeasuredViewWidth(i) + rightMargin;
                } else if (lineShowType == 1 && centerViewList != null) {
                    //设置一行里需要居中的View
                    setLineCenterView(lineViewWidth, top);
                    top += (getChildAt(i - 1).getMeasuredHeight() + verticalPadding);
                    lineViewWidth = 0;
                    i--;
                }
            }
        }
    }
}
