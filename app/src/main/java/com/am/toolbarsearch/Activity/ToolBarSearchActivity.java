package com.am.toolbarsearch.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.am.toolbarsearch.GeneralConst;
import com.am.toolbarsearch.R;
import com.am.toolbarsearch.adapter.JokeToolSearchRvAdapter;
import com.am.toolbarsearch.entity.JokeEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * User: LJM
 * Date&Time: 2016-10-21 & 14:28
 * Describe: Describe Text
 */
public class ToolBarSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Toolbar mToolBar;
    private RecyclerView mRvJokeList;
    private JokeToolSearchRvAdapter mJokeToolSearchRvAdapter;
    private ArrayList<JokeEntity.ContentlistBean> mBeansList;
    private ArrayList<JokeEntity.ContentlistBean> mCopyBeansList = new ArrayList<>();

    private  static final int MSG_UPDATE_SEARCH_RESULT = 1001;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_SEARCH_RESULT:
                    mJokeToolSearchRvAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);

        mToolBar = (Toolbar) findViewById(R.id.mToolBar);
        mToolBar.setTitle("ToolBar搜索");
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.icon_arrow_nev);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolBar.setOnMenuItemClickListener(onMenuItemClick);
        mToolBar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));

        mRvJokeList = (RecyclerView) findViewById(R.id.mRvJokeList);
        mRvJokeList.setLayoutManager(new LinearLayoutManager(this));

        try {
            JSONObject jsonObject = new JSONObject(GeneralConst.getImitateJson());
            Gson gson = new Gson();
            Type tokenType = new TypeToken<List<JokeEntity.ContentlistBean>>(){}.getType();
            mBeansList = gson.fromJson(jsonObject.optString("contentlist"), tokenType);
            mCopyBeansList.addAll(mBeansList);
            mJokeToolSearchRvAdapter = new JokeToolSearchRvAdapter(ToolBarSearchActivity.this, mBeansList);
            mRvJokeList.setAdapter(mJokeToolSearchRvAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 创建关联菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =(SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    // 菜单的点击回调
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.search:
                    Log.d(GeneralConst.TAG,"====== Click Search");
                    break;
                case R.id.about:
                    Log.d(GeneralConst.TAG,"====== Click About");
                    break;
            }
            return true;
        }
    };


    // === SearchView 监听的两个方法  onQueryTextSubmit 和 onQueryTextChange

    /**
     * 提交 输入搜索内容,按下左侧的 × 不会触发此方法,只有按下键盘右下脚的搜索符号才会触发此方法
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(GeneralConst.TAG,"onQueryTextSubmit:  "+query);

        return false;
    }

    /**
     * 输入的文本发生变化
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(GeneralConst.TAG,"onQueryTextChange:  "+newText);
        doSearch(newText);
        return false;
    }

    private ArrayList<JokeEntity.ContentlistBean> mTempBeansList;
    private void doSearch(CharSequence str) {
        Log.d(GeneralConst.TAG,"doSearch");
        mTempBeansList = new ArrayList<>();
        // 如果需要排序考虑 Pair
        for(int i=0;i<mCopyBeansList.size();i++){
            JokeEntity.ContentlistBean contentlistBean = mCopyBeansList.get(i);
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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(GeneralConst.TAG,"onNewIntent");
    }
}
