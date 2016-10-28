package com.am.toolbarsearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.am.toolbarsearch.R;
import com.am.toolbarsearch.entity.JokeEntity;

import java.util.ArrayList;

/**
 * Created by jianghejie on 15/11/26.
 */
public class JokeToolSearchRvAdapter extends RecyclerView.Adapter<JokeToolSearchRvAdapter.ViewHolder> {
    public ArrayList<JokeEntity.ContentlistBean> mDatas = null;
    public Context mContext;

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

    public JokeToolSearchRvAdapter(Context context, ArrayList<JokeEntity.ContentlistBean> datas) {

        this.mDatas = datas;
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
}
