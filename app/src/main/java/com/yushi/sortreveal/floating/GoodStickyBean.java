package com.yushi.sortreveal.floating;

/**
 * function:
 * describe:
 * Created By uatql992792 on 2021/6/28.
 */
public class GoodStickyBean implements StickyItemDecoration.ItemBean {

    private String value;
    private String groupName;

    public void setValue(String value) {
        this.value = value;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    @Override
    public String getValue() {
        return value;
    }
}
