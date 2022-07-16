package com.yushi;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * function:
 * describe:
 * Created By uatql992792 on 2021/5/31.
 */
 public class SortRevealUtil {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    public   static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

   public   static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

   public   static String assetsFileToStrig(Context context, String fileName) {
        String json = null;
        if (context != null) {
            try {
                InputStream input = context.getAssets().open(fileName);
                json = convertStreamToString(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    private static String convertStreamToString(InputStream is) {
        String s = null;
        try {
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("//A");
            if (scanner.hasNext()) {
                s = scanner.next();
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }



    public static boolean getViewMeasured(View view) {
        if (view == null) return false;
        if (view.getMeasuredWidth() == 0) {
            ViewGroup viewGroup;
            do {
                viewGroup = (ViewGroup) view.getParent();
            } while (viewGroup != null && viewGroup.getMeasuredWidth() == 0);
            if (viewGroup != null && viewGroup.getMeasuredWidth() > 0 && viewGroup.getMeasuredHeight() > 0) {
                return getViewMeasured(view, viewGroup.getMeasuredWidth(), viewGroup.getMeasuredHeight());
            }
        }
        return false;
    }

    public static boolean getViewMeasured(View view, int parentMaxWidth, int parentMaxHeight) {
        if (view == null) return false;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        int endMargin = 0, startMargin = 0;
        int width, height;
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
        return true;
    }

}
