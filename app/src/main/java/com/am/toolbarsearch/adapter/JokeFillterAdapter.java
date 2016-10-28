package com.am.toolbarsearch.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.am.toolbarsearch.R;
import com.am.toolbarsearch.entity.JokeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * User: LJM
 * Date&Time: 2016-10-20 & 21:44
 * Describe: Describe Text
 */
public class JokeFillterAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private ArrayList<JokeEntity.ContentlistBean> mDatas;
    private ArrayList<JokeEntity.ContentlistBean> mCppyDatas;
    private static final String TAG = "TEST_SEARCH";
    private  MyFilter mFilter;

    public JokeFillterAdapter(Context context, ArrayList<JokeEntity.ContentlistBean> datas) {
        mContext = context;
        mDatas = datas;
        mCppyDatas = datas; // 备份数据
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
            view = View.inflate(mContext, R.layout.item_joke_fix,null);
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

    //当ListView调用setTextFilter()方法的时候，便会调用该方法
    @Override
    public Filter getFilter() {
        if (mFilter ==null){
            mFilter = new MyFilter();
        }
        return mFilter;
    }


    //我们需要定义一个过滤器的类来定义过滤规则

    /**
     * 自定义 Filter类
     *
     * 两个方法:
     * performFiltering 定义搜索规则。
     * publishResults 对搜索结果的处理
     */

    class MyFilter extends Filter{
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
                notifyDataSetInvalidated();//通知数据失效
                Log.d(TAG,"publishResults:notifyDataSetInvalidated");
            }
        }
    }
}
