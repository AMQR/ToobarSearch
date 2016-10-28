package com.am.toolbarsearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.am.toolbarsearch.R;
import com.am.toolbarsearch.entity.JokeEntity;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by jianghejie on 15/11/26.
 */
public class RvFilterAdapter extends RecyclerView.Adapter<RvFilterAdapter.ViewHolder>  implements Filterable {
    public ArrayList<JokeEntity.ContentlistBean> mDatas = null;
    public ArrayList<JokeEntity.ContentlistBean> mCppyDatas = null;
    public Context mContext;
    private MyFilter mFilter;

    // 点击回调
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);  // 点击
        void onItemLongClick(View view, int position); // 长按
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public RvFilterAdapter(Context context, ArrayList<JokeEntity.ContentlistBean> datas) {

        this.mDatas = datas;
        this.mCppyDatas = datas;
        this.mContext = context;

    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_joke_toolbar_search,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        JokeEntity.ContentlistBean nearPeopleEntity = mDatas.get(position);
        viewHolder.mTvJokeTit.setText(nearPeopleEntity.title);
        viewHolder.mTvJokeText.setText(nearPeopleEntity.text);
        viewHolder.mTvJokeTime.setText(nearPeopleEntity.ct);

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            viewHolder.mLlItemRoot.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(viewHolder.mLlItemRoot, pos);
                }
            });
            viewHolder.mLlItemRoot.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(viewHolder.mLlItemRoot, pos);
                    return false;
                }
            });
        }
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLlItemRoot;
        public TextView mTvJokeTit;
        public TextView mTvJokeText;
        public TextView mTvJokeTime;

        public ViewHolder(View view){
            super(view);
            mLlItemRoot = (LinearLayout) view.findViewById(R.id.mLlItemRoot);
            mTvJokeTit = (TextView) view.findViewById(R.id.mTvJokeTit);
            mTvJokeText = (TextView) view.findViewById(R.id.mTvJokeText);
            mTvJokeTime = (TextView) view.findViewById(R.id.mTvJokeTime);
        }
    }


    //当ListView调用setTextFilter()方法的时候，便会调用该方法
    @Override
    public Filter getFilter() {
        if (mFilter ==null){
            mFilter = new RvFilterAdapter.MyFilter();
        }
        return mFilter;
    }

    class MyFilter extends Filter {
        //我们在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();

            List<JokeEntity.ContentlistBean> tempList ; // temp中间集合
            if (TextUtils.isEmpty(charSequence)){//当过滤的关键字为空的时候，我们则显示所有的数据
                tempList  = mCppyDatas;
            }else {//否则把符合条件的数据对象添加到集合中
                tempList = new ArrayList<>();
                for (JokeEntity.ContentlistBean recomend:mCppyDatas){
                    if (recomend.text.contains(charSequence)||recomend.title.contains(charSequence)||recomend.ct.contains(charSequence)){
                        Log.d(TAG,"performFiltering:"+recomend.toString());
                        tempList.add(recomend);
                    }

                }
            }
            result.values = tempList; //符合搜索的结果的数据集合
            result.count = tempList.size();//符合搜索的结果的数据个数

            return result;
        }
        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mDatas = (ArrayList<JokeEntity.ContentlistBean>)filterResults.values;
            Log.d(TAG,"publishResults:"+filterResults.count);
            if (filterResults.count>0){
                notifyDataSetChanged();//通知数据发生了改变
                Log.d(TAG,"publishResults:notifyDataSetChanged");
            }else {
                //notifyDataSetInvalidated();//通知数据失效
                Log.d(TAG,"publishResults:notifyDataSetInvalidated");
            }
        }
    }

}
