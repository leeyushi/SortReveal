package com.yushi.sortreveal.floating;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yushi.sortreveal.R;

import java.util.List;

/**
 * function:
 * describe:
 * Created By uatql992792 on 2021/6/24.
 */
public class StickyExampleAdapter extends RecyclerView.Adapter<StickyExampleAdapter.RecyclerViewHolder> {
    //第一个吸顶
    private static final int FIRST_STICKY_VIEW = 1;
    //别的吸顶
    static final int HAS_STICKY_VIEW = 2;
    //正常View
    static final int NONE_STICKY_VIEW = 3;
    private final LayoutInflater mInflate;
    private final List<StickyBean> datas;

    public StickyExampleAdapter(Context context, List<StickyBean> datas) {

        mInflate = LayoutInflater.from(context);
        this.datas = datas;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = mInflate.inflate(R.layout.item_ui, parent, false);
        return new RecyclerViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        StickyBean stickyBean = datas.get(position);
        holder.value.setText(stickyBean.value);
//        if (position == 0) {
//            holder.tvStickyHeader.setVisibility(View.VISIBLE);
//            holder.tvStickyHeader.setText(stickyBean.sticky);
//            holder.itemView.setTag(FIRST_STICKY_VIEW);
//        } else {
//            if (!TextUtils.equals(stickyBean.sticky, datas.get(position - 1).sticky)) {
//                holder.tvStickyHeader.setVisibility(View.VISIBLE);
//                holder.tvStickyHeader.setText(stickyBean.sticky);
//                holder.itemView.setTag(HAS_STICKY_VIEW);
//            } else {
//                holder.tvStickyHeader.setVisibility(View.GONE);
//                holder.itemView.setTag(NONE_STICKY_VIEW);
//            }
//        }
        //通过此处设置ContentDescription，作为内容描述，可以通过getContentDescription取出，功效跟setTag差不多。
//        holder.itemView.setContentDescription(stickyBean.sticky);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView value;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            value = itemView.findViewById(R.id.value);
        }
    }
}
