package com.yushi.sortreveal.floating;

import com.yushi.recyclerviewitemdecoration.SortItemDecoration;

/**
 * function:
 * describe:
 * Created By uatql992792 on 2021/6/24.
 */
public class StickyBean implements SortItemDecoration.ItemBean {
    public String groupName;
    public String value;


    public StickyBean(String groupName, String value) {
        this.groupName = groupName;
        this.value = value;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }


    public String getValue() {
        return value;
    }


    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public void setValue(String value) {
        this.value = value;
    }

    public int getSortNumber() {
        return groupName.charAt(0);
    }
}
