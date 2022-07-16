package com.yushi.sortreveal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import com.yushi.SortRevealUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * function:
 * describe:
 * Created By uatql992792 on 2021/5/31.
 */
public class SortRevealLayout extends ViewGroup {

    int widthSize = 0;
    int heightSize = 0;
    //分类Map,key为分类Name，value为分类的List
    Map<String, List<String>> sortMap = new HashMap<>();
    //热门展示标签
    private String hotRevealLabel = "热门地区/国家";
    //热门展示内容
    private List<String> hotRevealList;
    //    //数据源
//    private List<String> dataSourceList;
    //热门标签Size
    private int hotLabelSize;
    //热门标签颜色
    private int hotLabelColor;
    //热门内容Size
    private int hotValueSize;
    //热门内容颜色
    private int hotValueColor;
    private Context context;
    //热门标签左边距
    private int hotLabelStartMargin;
    //热门内容左边距
    private int hotValueStartMargin;
    //热门内容横向间距
    private int hotValueHorizontalPadding;
    //热门标签顶边距
    private int hotLabelTopMargin;
    //热门内容顶边距
    private int hotValueTopMargin;
    //热门内容竖向间距
    private int hotValueVerticalPadding;
    //分类名称顶部边距
    private int sortNameTopMargin;
    //分类名称竖向间距
    private int sortNameVerticalPadding;
    //分类名称左边距
    private int sortNameStartMargin;
    //分类名称颜色
    private int sortNameColor;
    //分类名称大小
    private int sortNameSize;
    //分类名称线条的顶部边距
    private int sortNameLineTopMargin;
    //分类名称线条的颜色
    private int sortNameLineColor;
    //分类名称线条的颜色Size
    private int sortNameLineSize;
    //分类内容顶部边距
    private int sortValueTopMargin;
    //分类内容左边距
    private int sortValueStartMargin;
    //分类内容颜色
    private int sortValueColor;
    //分类内容大小
    private int sortValueSize;
    //分类内容竖向间距
    private int sortValueVerticalPadding;
    //热门内容一行显示的类型，0为默认，1为居中
    private int hotValueLineType;
    Handler handler = new Handler(Looper.getMainLooper());
    //热门标签View
    private TextView hotLabelView;


    //分类名称的高度
    private int sortNameHeight;
    //分类名称底部线条的高度
    private int sortNameBottomLineHeight;
    //单个分类内容的高度
    private int sortSingleValueHeight;
    //热门区域高度
    private int hotAreaHeight;
    //分类内容所能容纳的区域高度
    private int sortValueAreaHeight;
    //分类名称所能容纳的区域高度
    private int sortNameAreaHeight;
    //分类名称在可容纳区域能显示的最大数量
    private int sortNameMaxNumber;
    //分类内容在可容纳区域能显示的最大数量
    private int sortValueMaxNumber;

    //key为分类列表的NameView，value为分类列表内容的LinearLayout
//    private Map<TextView, LinearLayout> sortRevealViewMap = new LinkedHashMap<>();

    //添加缓存View到布局
    private boolean addCacheViewToLayout;
    //热门列表View
    private List<TextView> hotValueViewList;
    //缓存池里的ValueViewList
    private List<LinearLayout> valueCacheViewList;
    //缓存池里的NameViewList
    private List<TextView> nameCacheViewList;
    //缓存池里的NameBottomLineViewList
    private List<View> nameLineCacheViewList;

    //假数据的Json
    private JSONObject jsonObject;
    //数据源，key是SortName，value是SortValueList
    private Map<String, List<String>> dataSourceMap;
    //从缓存里已经用过的ValueViewList
    private List<View> cacheValueUserListView;
    //从缓存里已经用过的NameViewList
    private List<View> cacheNameUserListView;
    private TextView testPosition;

    private int mScrollHeight;


    public SortRevealLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        overScroller = new OverScroller(context);
        initTestData();
        initMap();
        initCustomAttrs(context, attrs);
        initHotView();
        initSortView();
    }

    private void initSortView() {
        Iterator<String> iterator = jsonObject.keys();
        dataSourceMap = new LinkedHashMap<>();
        nameLineCacheViewList = new ArrayList<>();
        nameCacheViewList = new ArrayList<>();
        valueCacheViewList = new ArrayList<>();
        while (iterator.hasNext()) {
            String sortName = iterator.next();
            try {
                JSONArray valueList = jsonObject.getJSONArray(sortName);
                if (valueList.length() > 0) {
                    TextView sortNameView = addTextView(sortNameSize, sortNameColor, sortName);
                    sortNameView.setBackgroundColor(Color.parseColor("#008577"));
                    nameLineCacheViewList.add(addLineView());
                    LinearLayout layout = new LinearLayout(context);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    for (int i = 0; i < valueList.length(); i++) {
                        TextView valueView = addTextView(sortValueSize, sortValueColor, valueList.get(i).toString(), layout);
                        MarginLayoutParams params = (MarginLayoutParams) valueView.getLayoutParams();
                        params.topMargin = sortValueTopMargin;
                        params.leftMargin = sortValueStartMargin;
                        valueView.setBackgroundColor(Color.parseColor("#008577"));
                    }
                    addView(layout, layoutParams);
                    nameCacheViewList.add(sortNameView);
                    valueCacheViewList.add(layout);

                    //可能多余的代码
                    List<String> strings = new ArrayList<>();
                    for (int i = 0; i < valueList.length(); i++) {
                        strings.add(valueList.get(i).toString());
                    }
                    dataSourceMap.put(sortName, strings);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {


//                    requestLayout();
//                    addCacheViewToLayout = true;
//                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

    }

    //热门居中的ViewList
    private List<View> hotCenterViewList;


    private int getYPosition(int userAreaHeight) {
        return hotAreaHeight + userAreaHeight;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        if (addCacheViewToLayout) {
//            //添加缓存View的布局
//            addCacheViewToLayout = false;
////            for (View view : nameCacheViewList) {
////                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
////                view.setVisibility(INVISIBLE);
////            }
////            for (View view : valueCacheViewList) {
////                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
////                view.setVisibility(INVISIBLE);
////            }
////            for (View view : nameLineCacheViewList) {
////                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
////                view.setVisibility(INVISIBLE);
////            }
////            Set<Map.Entry<String, List<String>>> sortRevealSet = dataSourceMap.entrySet();
////            //计算分类已用的区域高度
////            int sortRevealUserAreaHeight = 0;
////            int sortNameIndex = 0;
////            int sortValueIndex = 0;
////            int sortLineIndex = 0;
////            for (Map.Entry<String, List<String>> sortRevealEntry : sortRevealSet) {
////                if (sortRevealUserAreaHeight <= sortNameAreaHeight) {
////                    sortRevealUserAreaHeight += sortNameIndex == 0 ? sortNameTopMargin : sortNameVerticalPadding;
////                    //设置分类名称的位置
////                    String sortName = sortRevealEntry.getKey();
////                    TextView sortNameView = (TextView) nameCacheViewList.get(sortNameIndex++);
//////                    sortNameView.setText(sortName);
////                    sortNameView.setVisibility(VISIBLE);
////                    sortNameView.setX(sortNameStartMargin);
////                    sortNameView.setY(getYPosition(sortRevealUserAreaHeight));
////                    sortRevealUserAreaHeight += sortNameView.getMeasuredHeight();
////
////                    //设置分类名称底部线条的位置
////                    sortRevealUserAreaHeight += sortNameLineTopMargin;
////                    LineView lineView = nameLineCacheViewList.get(sortLineIndex++);
////                    lineView.setVisibility(VISIBLE);
////                    lineView.setY(getYPosition(sortRevealUserAreaHeight));
////                    lineView.setX(0);
////                    sortRevealUserAreaHeight += lineView.getMeasuredHeight();
////
////                    //设置分类名称对应的ListValue
////                    List<String> sortValueList = sortRevealEntry.getValue();
////                    for (int i = 0; i < valueCacheViewList.size(); i++) {
////                        sortRevealUserAreaHeight += i == 0 ? sortValueTopMargin : sortValueVerticalPadding;
////                        TextView sortValueView = (TextView) valueCacheViewList.get(i);
//////                        sortValueView.setText(value);
////                        sortValueView.setVisibility(VISIBLE);
////                        sortValueView.setX(sortValueStartMargin);
////                        sortValueView.setY(getYPosition(sortRevealUserAreaHeight));
////                        sortRevealUserAreaHeight += sortValueView.getMeasuredHeight();
////                    }
////                } else {
////                    break;
////                }
////
////            }
//        } else if (sortRevealViewMap != null) {
//            //添加测试View的布局
//            setTestViewLayout();
//        }
        //添加测试View的布局
        setTestViewLayout();
    }

    private void setTestViewLayout() {
        mScrollHeight = 0;
        hotAreaHeight = setHotViewLayout();
        int top = hotAreaHeight + sortNameTopMargin;
//        Set<Map.Entry<TextView, LinearLayout>> sortRevealSet = sortRevealViewMap.entrySet();
        for (int i = 0; i < nameCacheViewList.size(); i++) {
            TextView sortNameView = nameCacheViewList.get(i);
            //设置分类名称View的位置
            sortNameView.layout(sortNameStartMargin, top, sortNameView.getMeasuredWidth() + sortNameStartMargin, top + sortNameView.getMeasuredHeight());
//            if (testPosition != null) {
//                int namePadding = Math.min(sortNameVerticalPadding, sortNameTopMargin);
//                int valuePadding = Math.min(sortValueVerticalPadding, sortValueTopMargin);
//                int testTop = hotAreaHeight + sortNameHeight + namePadding +
//                        sortNameLineTopMargin + sortNameBottomLineHeight + sortSingleValueHeight + valuePadding;
//                testPosition.layout(sortNameStartMargin, testTop, testPosition.getMeasuredWidth() + sortNameStartMargin, testTop + testPosition.getMeasuredHeight());
//            }

            top += (sortNameView.getMeasuredHeight() + sortNameLineTopMargin);
            View bottomLineView = nameLineCacheViewList.get(i);
            bottomLineView.layout(0, top, bottomLineView.getMeasuredWidth(), top + bottomLineView.getMeasuredHeight());

            top += bottomLineView.getMeasuredHeight();
            LinearLayout sortValueLayout = valueCacheViewList.get(i);
            sortValueLayout.layout(0, top, sortValueLayout.getMeasuredWidth(), top + sortValueLayout.getMeasuredHeight());
            top += sortValueLayout.getMeasuredHeight();
//            for (int i = 0; i < sortValueList.size(); i++) {
//                View view = sortValueList.get(i);
//                if (i == 0) {
//                    //设置分类名称底部线条的位置
//                    top += sortNameLineTopMargin;
//                    view.layout(0, top, view.getMeasuredWidth(), top + view.getMeasuredHeight());
//                    top += view.getMeasuredHeight();
//                } else if (i == 1) {
//                    //设置分类名称的第一个ValueView
//                    top += sortValueTopMargin;
//                    view.layout(sortValueStartMargin, top, view.getMeasuredWidth() + sortValueStartMargin, top + view.getMeasuredHeight());
//                    top += view.getMeasuredHeight();
//                } else {
//                    top += sortValueVerticalPadding;
//                    view.layout(sortValueStartMargin, top, view.getMeasuredWidth() + sortValueStartMargin, top + view.getMeasuredHeight());
//                    top += view.getMeasuredHeight();
//                }
//            }
            top += sortNameVerticalPadding;
        }
        mScrollHeight = top - getMeasuredHeight();
    }

    private int setHotViewLayout() {
        if (hotValueViewList != null && hotValueViewList.size() > 0) {
            hotLabelView.layout(hotLabelStartMargin, hotLabelTopMargin, hotLabelView.getMeasuredWidth() + hotLabelStartMargin, hotLabelView.getMeasuredHeight() + hotLabelTopMargin);
            if (testPosition != null)
                testPosition.layout(hotLabelStartMargin, getMeasuredHeight() - sortValueAreaHeight, testPosition.getMeasuredWidth() + hotLabelStartMargin, (getMeasuredHeight() - sortValueAreaHeight) + testPosition.getMeasuredHeight());
            int left = hotValueStartMargin;
            int top = hotLabelView.getMeasuredHeight() + hotLabelTopMargin + hotValueTopMargin;
            int viewGroupWidth = getMeasuredWidth();
            int lineViewWidth = 0;
            for (int i = 0; i < hotValueViewList.size(); i++) {
                View view = hotValueViewList.get(i);
                //是否换行
                boolean isLineFeed = (left + (view.getMeasuredWidth() + hotValueHorizontalPadding)) > viewGroupWidth;
                if (!isLineFeed) {
                    if (hotValueLineType == 0) {
                        view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
                    } else if (hotValueLineType == 1) {
                        if (hotCenterViewList == null) {
                            hotCenterViewList = new ArrayList<>();
                        }
                        hotCenterViewList.add(view);
                        lineViewWidth += view.getMeasuredWidth();
                        if (i == hotValueViewList.size() - 1) {
                            setLineCenterView(lineViewWidth, top);
                            break;
                        }
                    }
                    left += view.getMeasuredWidth() + hotValueHorizontalPadding;
                } else {
                    left = hotValueStartMargin;
                    if (hotValueLineType == 0) {
                        top += (view.getMeasuredHeight() + hotValueVerticalPadding);
                        view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
                        left += view.getMeasuredWidth() + hotValueHorizontalPadding;
                    } else if (hotValueLineType == 1 && hotCenterViewList != null) {
                        //设置一行里需要居中的View
                        setLineCenterView(lineViewWidth, top);
                        top += (view.getMeasuredHeight() + hotValueVerticalPadding);
                        lineViewWidth = 0;
                        i--;
                    }

                }
            }
            return top + hotValueViewList.get(0).getMeasuredHeight();
        }
        return 0;
    }

    private void setLineCenterView(int lineViewWidth, int top) {
        int padding = (getMeasuredWidth() - lineViewWidth) / (hotCenterViewList.size() + 1);
        int left = padding;
        for (View centerView : hotCenterViewList) {
            centerView.layout(left, top, left + centerView.getMeasuredWidth(), top + centerView.getMeasuredHeight());
            left += (centerView.getMeasuredWidth() + padding);
        }
        hotCenterViewList.clear();
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SortRevealLayout);
        hotLabelSize = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_hotLabelSize, 9));
        hotValueSize = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_hotValueSize, 12));
        hotLabelStartMargin = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_hotLabelStartMargin, 12));
        hotLabelTopMargin = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_hotLabelTopMargin, 16));
        hotValueHorizontalPadding = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_hotValueHorizontalPadding, 30));
        hotValueVerticalPadding = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_hotValueVerticalPadding, 8));
        hotValueTopMargin = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_hotValueTopMargin, 10));
        sortNameTopMargin = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_sortNameTopMargin, 8));
        sortNameVerticalPadding = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_sortNameVerticalPadding, 8));
        sortNameStartMargin = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_sortNameStartMargin, 8));
        sortValueStartMargin = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_sortValueStartMargin, 8));
        sortNameLineTopMargin = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_sortNameLineTopMargin, 8));
        sortValueTopMargin = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_sortValueTopMargin, 8));
        sortValueVerticalPadding = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_sortValueVerticalPadding, 8));
        sortNameLineSize = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_sortNameLineSize, 8));
        sortValueSize = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_sortValueSize, 8));
        sortNameSize = SortRevealUtil.dp2px(typedArray.getInteger(R.styleable.SortRevealLayout_sortNameSize, 8));
        hotValueStartMargin = typedArray.getInteger(R.styleable.SortRevealLayout_hotValueStartMargin, hotLabelStartMargin);
        hotValueLineType = typedArray.getInteger(R.styleable.SortRevealLayout_hotValueLineType, 1);
        sortNameLineColor = typedArray.getColor(R.styleable.SortRevealLayout_sortNameLineColor, Color.parseColor("#000000"));
        sortNameColor = typedArray.getColor(R.styleable.SortRevealLayout_sortNameColor, Color.parseColor("#000000"));
        sortValueColor = typedArray.getColor(R.styleable.SortRevealLayout_sortValueColor, Color.parseColor("#000000"));
        hotLabelColor = typedArray.getResourceId(R.styleable.SortRevealLayout_hotLabelColor, Color.parseColor("#000000"));
        hotValueColor = typedArray.getResourceId(R.styleable.SortRevealLayout_hotLabelColor, Color.parseColor("#000000"));
        //读取完属性后必须释放
        typedArray.recycle();
    }

    private void initHotView() {
        hotLabelView = addTextView(hotLabelSize, hotLabelColor, hotRevealLabel);
        hotLabelView.setBackgroundColor(Color.parseColor("#008577"));
        if (hotRevealList != null) {
            if (hotValueViewList == null) hotValueViewList = new ArrayList<>();
            for (String hotValue : hotRevealList) {
                TextView view = addTextView(hotValueSize, hotValueColor, hotValue);
                view.setBackgroundColor(Color.parseColor("#008577"));
                hotValueViewList.add(view);
            }
        }
    }

    private TextView addTextView(float textSize, int textColor, String textValue) {
        return addTextView(textSize, textColor, textValue, this);
    }

    private TextView addTextView(float textSize, int textColor, String textValue, ViewGroup group) {
        TextView textView = initTextView(textSize, textColor, textValue);
        MarginLayoutParams layoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        group.addView(textView, layoutParams);
        return textView;
    }

    private View addLineView() {
//        LineView sortNameBottomLineView = new LineView(context, sortNameLineColor, sortNameLineSize);
        MarginLayoutParams layoutParams = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
        View sortNameBottomLineView = new View(context);
        layoutParams.height = sortNameLineSize;
        sortNameBottomLineView.setBackgroundColor(Color.parseColor("#008577"));
        addView(sortNameBottomLineView, layoutParams);
        return sortNameBottomLineView;
    }

    private TextView initTextView(float textSize, int textColor, String textValue) {
        TextView textView = new TextView(context);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setText(textValue);
        textView.setId(SortRevealUtil.generateViewId());
        return textView;
    }

    private void initMap() {
        String json = SortRevealUtil.assetsFileToStrig(context, "data_source.json");
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        for (String sort : dataSourceList) {
//            String key = PinYinUtil.chineseToPinYin(sort.substring(0, 1).toCharArray()[0]);
//            if (key != null) {
//                key = key.toUpperCase();
//                List<String> strings = sortMap.get(key);
//                if (strings == null) {
//                    strings = new ArrayList<>();
//                    sortMap.put(key, strings);
//                }
//                strings.add(sort);
//            }
//        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            measureChildWithMargins(getChildAt(i), widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec), heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.RED);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }


    private int actionIndex;
    private float downTouchY;
    private float moveViewY;

    //滑动距离
    private float slideDistance;
    private static final String TAG = "SortRevealLayout";
    private int mScrollY;

    private void setTestY(int slideDistance) {
//        setY(getY() + slideDistance);
        hotLabelView.setY(hotLabelView.getY() + slideDistance);
        for (View view : hotValueViewList) {
            view.setY(view.getY() + slideDistance);
        }

        for (View view : valueCacheViewList) {
            view.setY(view.getY() + slideDistance);
        }
        for (View view : nameLineCacheViewList) {
            view.setY(view.getY() + slideDistance);
        }

        for (View view : nameCacheViewList) {
            view.setY(view.getY() + slideDistance);
        }
    }

    private VelocityTracker velocityTracker;
    OverScroller overScroller;
    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 30, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    //    ExecutorService executor = Executors.newCachedThreadPool();
    boolean stop;
    boolean start;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                actionIndex = event.getActionIndex();
                velocityTracker = VelocityTracker.obtain();
                downTouchY = event.getY(actionIndex);
                if (start) stop = true;
                if (overScroller.computeScrollOffset()) {
                    overScroller.forceFinished(true);
//                    if (mScrollY != overScroller.getCurrY()) {
//                        mScrollY = -overScroller.getCurrY();
//                    }

//                    overScroller.notifyVerticalEdgeReached(-overScroller.getCurrY(), -overScroller.getCurrY(), -(getMeasuredHeight() + 2000));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                slideDistance = event.getY(actionIndex) - downTouchY;
                Log.i(TAG, "onTouchEvent: slideDistance:" + slideDistance);
//                setTestY((int) slideDistance);
                mScrollY = (mScrollY + ((int) -slideDistance));
                scrollBy(0, (int) -slideDistance);
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                downTouchY = event.getY(actionIndex);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
            case MotionEvent.ACTION_UP:
//                Log.i(TAG, "onTouchEvent: getYVelocity:" + velocityTracker.getYVelocity());
//                Log.i(TAG, "onTouchEvent: getYVelocity:" + 111);

//                overScroller.fling((int) nameCacheViewList.get(0).getX(), (int) nameCacheViewList.get(0).getY(),
//                        (int) velocityTracker.getXVelocity(), (int) velocityTracker.getYVelocity(), 0, 0, 0, getMeasuredHeight() - 500);
//                poolExecutor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        while (overScroller.computeScrollOffset()) {
//                            if (stop) {
//                                stop = false;
//                                return;
//                            }
//                            start = true;
//                            Log.i(TAG, "run:getCurrX: " + overScroller.getCurrX() + "\tgetCurrY:" + overScroller.getCurrY()
//                                    + "\tgetFinalX:" + overScroller.getFinalX() + "\tgetFinalY:" + overScroller.getFinalY() + "\tgetStartX:" + overScroller.getStartX()
//                                    + "\tgetStartY:" + overScroller.getStartY() + "\tgetCurrVelocity:" + overScroller.getCurrVelocity()
//                                    + "\tisFinished:" + overScroller.isFinished() + "\tisOverScrolled:" + overScroller.isOverScrolled()
//                            );
//                            nameCacheViewList.get(0).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    nameCacheViewList.get(0).setY(overScroller.getCurrY());
//                                }
//                            });
//
//
//                        }
//                        start = false;
//                    }
//                });
//                lastCurrY = 0;
                Log.i(TAG, "onTouchEvent: getScrollY:" + (getScrollY()) + "\tmScrollY:" + mScrollY);
                if (mScrollY > mScrollHeight) {
                    overScroller.startScroll(0, -mScrollY, 0, (mScrollY - mScrollHeight), 500);
                } else if (mScrollY < 0) {
                    overScroller.startScroll(0, -mScrollY, 0, mScrollY, 500);
                } else {
                    overScroller.fling(0, -mScrollY,
                            0, (int) velocityTracker.getYVelocity(), 0, 0, -mScrollHeight, 0);
                    velocityTracker.clear();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    int lastCurrY;

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (overScroller.computeScrollOffset()) {

//            if (mScrollY != overScroller.getCurrY()) {
//                mScrollY = -overScroller.getCurrY();
//            }
            int slideDistance = 0;

//            if (overScroller.getStartY() != overScroller.getCurrY() || overScroller.getCurrY() == overScroller.getFinalY()) {
//                slideDistance = overScroller.getCurrY() - lastCurrY;
//            }
            mScrollY = -overScroller.getCurrY();

//            Log.i(TAG, "computeScroll: mScrollY:" + mScrollY);
//            if (slideDistance != 0) setTestY(slideDistance);
            Log.i(TAG, "computeScroll: getCurrY:" + overScroller.getCurrY()
                    + "\tgetStartY:" + overScroller.getStartY() + "\tgetFinalY:" + overScroller.getFinalY() + "\toverScroller:" + overScroller.getCurrVelocity() + "\tisFinished:" + overScroller.isFinished()
                    + "\tmScrollY:" + mScrollY + "\tslideDistance:" + slideDistance
            );
            scrollTo(0, -overScroller.getCurrY());
//            setTestY(-overScroller.getCurrY());
            lastCurrY = overScroller.getCurrY();
            invalidate();
        }
    }

    private void initTestData() {
        hotRevealList = new ListUtils<String>().add(
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
//        dataSourceList = new ListUtils<String>().add("澳门特别行政区",
//                "阿尔巴尼亚",
//                "阿尔及利亚",
//                "阿富汗",
//                "阿根廷",
//                "阿曼",
//                "巴巴多斯",
//                "巴哈马",
//                "巴基斯坦",
//                "巴西",
//                "巴拿马",
//                "赤道几内亚",
//                "丹麦",
//                "德国",
//                "多哥",
//                "俄罗斯",
//                "厄瓜多尔",
//                "法国",
//                "菲律宾",
//                "芬兰",
//                "冈比亚",
//                "刚果布",
//                "格陵兰",
//                "韩国",
//                "黑山",
//                "洪都拉斯",
//                "吉布提",
//                "几内亚",
//                "加拿大",
//                "卡塔尔",
//                "科威特",
//                "肯尼亚",
//                "黎巴嫩",
//                "利比亚",
//                "卢旺达",
//                "马尔代夫",
//                "马里",
//                "马其顿",
//                "美国",
//                "纳米比亚",
//                "南非",
//                "尼泊尔",
//                "葡萄牙",
//                "帕劳",
//                "日本",
//                "瑞典",
//                "瑞士",
//                "萨摩亚",
//                "塞内加尔",
//                "突尼斯",
//                "泰国",
//                "瓦努阿图",
//                "文莱",
//                "乌克兰",
//                "叙利亚",
//                "西班牙",
//                "牙买加",
//                "伊朗",
//                "伊拉克",
//                "赞比亚",
//                "乍得",
//                "智利",
//                "中非"
//        );
    }


    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    private interface GlideListen {
        //滑动自身
        void onEselfGlide();

        //滑动内容布局
        void valueLayoutGlide(LinearLayout valueLayout);

    }



}
