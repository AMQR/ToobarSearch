package com.am.toolbarsearch.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.am.toolbarsearch.R;

import java.util.ArrayList;

import com.am.toolbarsearch.entity.JokeEntity;

/**
 * User: LJM
 * Date&Time: 2016-10-20 & 21:44
 * Describe: Describe Text
 */
public class JokeAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<JokeEntity.ContentlistBean> mDatas;

    public JokeAdapter(Context context, ArrayList<JokeEntity.ContentlistBean> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if(view ==null){
            view = View.inflate(mContext, R.layout.item_joke,null);
            viewHolder = new ViewHolder();
            viewHolder.mTvTit = (TextView) view.findViewById(R.id.mTvTit);
            viewHolder.mTvContent = (TextView) view.findViewById(R.id.mTvContent);
            viewHolder.mTvTime = (TextView) view.findViewById(R.id.mTvTime);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        JokeEntity.ContentlistBean contentlistBean = mDatas.get(i);
        viewHolder.mTvTit.setText(contentlistBean.title);
        viewHolder.mTvContent.setText(contentlistBean.text);
        viewHolder.mTvTime.setText(contentlistBean.ct);
        return view;
    }

    class ViewHolder{
        TextView mTvTit;
        TextView mTvContent;
        TextView mTvTime;
    }
}
