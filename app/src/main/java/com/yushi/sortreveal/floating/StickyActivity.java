package com.yushi.sortreveal.floating;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yushi.recyclerviewitemdecoration.SortDecorationLayout;
import com.yushi.recyclerviewitemdecoration.SortItemDecoration;
import com.yushi.SortRevealUtil;
import com.yushi.sortreveal.ListUtils;
import com.yushi.sortreveal.PinYinUtil;
import com.yushi.sortreveal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StickyActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky);
        initView();
    }


    private List<StickyBean> stickyBeans;

    TextView selectView;


    private void initView() {
        recyclerView = findViewById(R.id.recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        stickyBeans = getData();

        recyclerView.setAdapter(new StickyExampleAdapter(this, stickyBeans));
//        recyclerView.getStickyDecoration().setDataList(stickyBeans);
        final View view = LayoutInflater.from(this).inflate(R.layout.group_view, (ViewGroup) recyclerView.getParent(), false);
        SortItemDecoration sortItemDecoration = new SortItemDecoration();
        fromPYToGroupName(stickyBeans, s -> PinYinUtil.chineseToPinYin(s.charAt(0)));
        sortItemDecoration.setGroupView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stickyBeans.sort(new Comparator<StickyBean>() {
                @Override
                public int compare(StickyBean o1, StickyBean o2) {
                    char c1 = o1.getGroupName().charAt(0);
                    char c2 = o2.getGroupName().charAt(0);
                    //o1的首字母大于o2的首字母，返回正数，否则返回负数，==时返回0
                    return c1 - c2;
                }
            });
        }
        View headView = LayoutInflater.from(this).inflate(R.layout.sticky_head, (ViewGroup) recyclerView.getParent(), false);
        LineFeedLayout feedLayout = headView.findViewById(R.id.ffl_head);
        List<String> testValueList = new ListUtils<String>().add(
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
            TextView textView = new TextView(this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setText(testValue);
            textView.setOnClickListener(v -> {
                Toast.makeText(this, testValue, Toast.LENGTH_SHORT).show();
            });
            feedLayout.addView(textView, layoutParams);
        }
//        sortItemDecoration.setHeadView(headView);
        recyclerView.addItemDecoration(sortItemDecoration);
        sortItemDecoration.setAdapter(new SortItemDecoration.Adapter() {
            @Override
            public void adapter(View holder, int position) {
                TextView textView = holder.findViewById(R.id.tv_sticky_header_view);
                textView.setText(stickyBeans.get(position).getGroupName());
            }

            @Override
            public String getGroupName(int position) {
                return stickyBeans.get(position).getGroupName();
            }
        });
        selectView = findViewById(R.id.select_type);
        SortDecorationLayout typeLayout = findViewById(R.id.type_layout);
        typeMap = getSortMap(stickyBeans);
        Set<Map.Entry<String, Integer>> entries = typeMap.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            TextView textView = new TextView(this);
            textView.setText(entry.getKey());
//            textView.setTextSize(SortRevealUtil.dp2px(9));
            typeLayout.addView(textView, new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        typeLayout.setTouchListen(new SortDecorationLayout.TouchListen() {
            long downTime;

            @Override
            public void down(View view) {
                Log.i("testListen", "testListen: down");
                downTime = SystemClock.elapsedRealtime();
                if (view instanceof TextView) {
                    setSelectViewAlpha(1);
                    String typeValue = ((TextView) view).getText().toString();
                    selectView.setText(typeValue);
                    sortItemDecoration.setPosition(recyclerView, typeMap.get(typeValue));
                }
            }

            @Override
            public void move(View view) {
                Log.i("testListen", "testListen: move");
                if (view instanceof TextView) {
                    String typeValue = ((TextView) view).getText().toString();
                    selectView.setText(typeValue);
                    sortItemDecoration.setPosition(recyclerView, typeMap.get(typeValue));
                }
            }

            @Override
            public void up(View view) {

            }

            @Override
            public void upEvent() {
                long downDuration = SystemClock.elapsedRealtime() - downTime;
                if (downDuration < 800) {
                    typeLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setSelectViewAlpha(0);
                            Log.i("testListen", "testListen: upEvent:" + downDuration);
                        }
                    }, 800 - downDuration);
                } else {
                    setSelectViewAlpha(0);
                    Log.i("testListen", "testListen: upEvent:" + downDuration);
                }
            }
        });
    }

    private ObjectAnimator hideSelectView() {
        return setSelectViewAlpha(0);
    }

    private ObjectAnimator showSelectView() {
        return setSelectViewAlpha(1);
    }

    private ObjectAnimator setSelectViewAlpha(float alpha) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(selectView, "Alpha", alpha);
        animator.setDuration(100);
        animator.start();
        return animator;
    }

    private Map<String, Integer> typeMap;

    public void fromPYToGroupName(List<? extends StickyBean> dataList, Function<String, String> toPinYin) {
        for (int i = 0; i < dataList.size(); ++i) {
            StickyBean itemBean = dataList.get(i);
            String firstPY = toPinYin.apply(itemBean.getValue());
            if (firstPY != null) {
                itemBean.setGroupName(firstPY.toUpperCase());
            }
        }

    }

    private List<StickyBean> getData() {
//        List<String> testValueList = new ListUtils<String>().add(
//                "中国香港",
//                "中国澳门",
//                "中国台湾",
//                "新加坡",
//                "马来西亚",
//                "澳大利亚",
//                "新西兰",
//                "美国",
//                "加拿大",
//                "阿根廷",
//                "埃及",
//                "爱尔兰",
//                "巴黎",
//                "中国",
//                "韩国",
//                "日本");
//        List<StickyBean> beans = new ArrayList<>();
//        for (String value : testValueList) {
//            StickyBean stickyBean = new StickyBean(null,value);
//            beans.add(stickyBean);
//        }
//        return beans;
        String json = SortRevealUtil.assetsFileToStrig(this, "data_source.json");
        JSONObject jsonObject = null;
        List<StickyBean> result = new ArrayList<>();
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject != null) {
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String groupName = iterator.next();
                JSONArray valueList;
                try {
                    valueList = jsonObject.getJSONArray(groupName);
                    if (valueList.length() > 0) {
                        for (int i = 0; i < valueList.length(); i++) {
                            String value = valueList.get(i).toString();
                            StickyBean stickyBean = new StickyBean(null, value);
                            result.add(stickyBean);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        return result;
//        String json = SortRevealUtil.assetsFileToStrig(this, "data_source.json");
//        JSONObject jsonObject = null;
//        List<StickyBean> result = new ArrayList<>();
//        typeMap = new HashMap<>();
//        try {
//            jsonObject = new JSONObject(json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if (jsonObject != null) {
//            Iterator<String> iterator = jsonObject.keys();
//            while (iterator.hasNext()) {
//                String groupName = iterator.next();
//                JSONArray valueList;
//                try {
//                    valueList = jsonObject.getJSONArray(groupName);
//                    if (valueList.length() > 0) {
//                        for (int i = 0; i < valueList.length(); i++) {
//                            String value = valueList.get(i).toString();
//                            StickyBean stickyBean = new StickyBean(groupName, value);
//                            result.add(stickyBean);
//                            if (typeMap.get(groupName) == null) {
//                                typeMap.put(groupName, result.size() - 1);
//                            }
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//        return result;
//        List<StickyBean> stickyExampleModels = new ArrayList<>();
//        for (int index = 0; index < 100; index++) {
//            if (index < 15) {
//                stickyExampleModels.add(new StickyBean("吸顶文本1", "name" + index, "gender1"));
//            } else if (index < 25) {
//                stickyExampleModels.add(new StickyBean("吸顶文本2", "name" + index, "gender2"));
//            } else if (index < 35) {
//                stickyExampleModels.add(new StickyBean("吸顶文本3", "name" + index, "gender3"));
//            } else if (index < 65) {
//                stickyExampleModels.add(new StickyBean("吸顶文本4", "name" + index, "gender4"));
//            } else {
//                stickyExampleModels.add(new StickyBean("吸顶文本5", "name" + index, "gender5"));
//            }
//        }
//        return stickyExampleModels;
    }

    /**
     * data from conversion groupName map
     *
     * @param dataList data
     * @return Map<String, Integer> key is groupName value is position
     */
    public Map<String, Integer> getSortMap(List<? extends SortItemDecoration.ItemBean> dataList) {
        if (dataList == null || dataList.size() == 0) return null;
        Map<String, Integer> sortMap = new LinkedHashMap<>();
        for (int i = 0; i < dataList.size(); i++) {
            SortItemDecoration.ItemBean itemBean = dataList.get(i);
            if (sortMap.get(itemBean.getGroupName()) == null) {
                sortMap.put(itemBean.getGroupName(), i);
            }
        }
        return sortMap;
    }
}
