package com.example.classmanageapp.RecyclerViewRelated;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.classmanageapp.MainActivity;
import com.example.classmanageapp.R;

import java.util.List;

public class WeekSelectAdapter extends RecyclerView.Adapter<WeekSelectAdapter.ViewHolder> implements View.OnClickListener{
    private List<WeekClass> mWeekList;
    private View viewItem;

    // 点击响应监听器
    private OnWeekItemClickListener onWeekItemClickListener = null;

    // 定义Recycler 的Holder
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView weekNum;
        TextView classNum;
        public ViewHolder(View view){
            super(view);
            weekNum = (TextView)view.findViewById(R.id.week_num_text);
            classNum = (TextView)view.findViewById(R.id.class_num_text);
        }
    }

    public WeekSelectAdapter(List<WeekClass> weekList){
        mWeekList = weekList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_item_layout, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        this.viewItem = view;
        // 定义每个周的响应事件
        view.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        WeekClass weekClass = mWeekList.get(position);
        holder.weekNum.setText(weekClass.getNumberOfWeek());
        holder.classNum.setText(weekClass.getNumberOfClass());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount(){
        //return mWeekList.size();
        return 20;
    }

    public View getViewItem(){
        return viewItem;
    }

    @Override
    public void onClick(View view) {
        if(onWeekItemClickListener != null){
            onWeekItemClickListener.onItemClick(view, (int)view.getTag());
        }
    }

    public void setOnWeekItemClickListener(OnWeekItemClickListener listener){
        this.onWeekItemClickListener = listener;
    }

    // 定义响应事件接口
    public interface OnWeekItemClickListener{
        void onItemClick(View view, int position);
    }
}