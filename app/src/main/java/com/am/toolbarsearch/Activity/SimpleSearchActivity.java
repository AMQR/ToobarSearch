package com.am.toolbarsearch.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.am.toolbarsearch.GeneralConst;
import com.am.toolbarsearch.R;
import com.am.toolbarsearch.adapter.JokeAdapter;
import com.am.toolbarsearch.entity.JokeEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SimpleSearchActivity extends AppCompatActivity {
    private ListView mLv;
    private JokeAdapter mJokeAdapter;
    private EditText mEtSearch;
    private ArrayList<JokeEntity.ContentlistBean> mBeansList;
    private ArrayList<JokeEntity.ContentlistBean> mCopyBeansList = new ArrayList<>();

    private  static final int MSG_UPDATE_SEARCH_RESULT = 1001;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_SEARCH_RESULT:
                    mJokeAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_search);
        mLv = (ListView) findViewById(R.id.mLv);
        mEtSearch = (EditText) findViewById(R.id.mEtSearch);
        mEtSearch.addTextChangedListener(new JokeListener());
        try {
            JSONObject jsonObject = new JSONObject(GeneralConst.getImitateJson());
            Gson gson = new Gson();
            Type tokenType = new TypeToken<List<JokeEntity.ContentlistBean>>(){}.getType();
            mBeansList = gson.fromJson(jsonObject.optString("contentlist"), tokenType);
            mCopyBeansList.addAll(mBeansList);

            mJokeAdapter = new JokeAdapter(SimpleSearchActivity.this, mBeansList);
            mLv.setAdapter(mJokeAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

     class JokeListener implements TextWatcher{
         @Override
         public void afterTextChanged(Editable s) {//表示最终内容
             Log.d("afterTextChanged", s.toString());
         }
         /**
          *
          * @param s
          * @param start 开始的位置
          * @param count 被改变的旧内容数
          * @param after 改变后的内容数量
          */
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
             //这里的s表示改变之前的内容，通常start和count组合，可以在s中读取本次改变字段中被改变的内容。而after表示改变后新的内容的数量。
         }
         /**
          *
          * @param s
          * @param start 开始位置
          * @param before 改变前的内容数量
          * @param count 新增数
          */
         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
             //这里的s表示改变之后的内容，通常start和count组合，可以在s中读取本次改变字段中新的内容。而before表示被改变的内容的数量。

             doSearch(s);

         }
     }

    private ArrayList<JokeEntity.ContentlistBean> mTempBeansList;
    private void doSearch(CharSequence str) {
        mTempBeansList = new ArrayList<>();
        // 如果需要排序考虑 Pair
        for(int i=0;i<mCopyBeansList.size();i++){
            if(mCopyBeansList.get(i).title.contains(str)
                    ||mCopyBeansList.get(i).text.contains(str)
                    ||mCopyBeansList.get(i).ct.contains(str)){
                mTempBeansList.add(mCopyBeansList.get(i));
            }
        }
        mBeansList.clear();
        mBeansList.addAll(mTempBeansList);
        mHandler.sendEmptyMessage(MSG_UPDATE_SEARCH_RESULT);
        mTempBeansList.clear();
    }

}
