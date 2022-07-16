package com.yushi.sortreveal.floating;

import android.view.View;

/**
 * function:
 * describe:
 * Created By uatql992792 on 2021/6/28.
 */
public class GroupHolder {
    //分组View
    private View groupView;
    //第一个index
    private int firstIndex;
    //最后一个index
    private int lastIndex;
    //分组名称
    private String groupName;

    public View getGroupView() {
        return groupView;
    }

    public void setGroupView(View groupView) {
        this.groupView = groupView;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
