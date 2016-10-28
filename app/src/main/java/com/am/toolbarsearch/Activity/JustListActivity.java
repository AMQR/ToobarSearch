package com.am.toolbarsearch.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
 * Date&Time: 2016-10-21 & 20:02
 * Describe: Describe Text
 */
public class JustListActivity extends AppCompatActivity{

    private Toolbar mToolBar;
    private RecyclerView mRvJokeList;
    private JokeToolSearchRvAdapter mJokeToolSearchRvAdapter;
    private ArrayList<JokeEntity.ContentlistBean> mBeansList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_just_list);
        mToolBar = (Toolbar) findViewById(R.id.mToolBar);
        mToolBar.setTitle("跳页搜索");
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.icon_arrow_nev);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mToolBar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));

        mRvJokeList = (RecyclerView) findViewById(R.id.mRvJokeList);
        mRvJokeList.setLayoutManager(new LinearLayoutManager(this));

        try {
            JSONObject jsonObject = new JSONObject(GeneralConst.getImitateJson());
            Gson gson = new Gson();
            Type tokenType = new TypeToken<List<JokeEntity.ContentlistBean>>(){}.getType();
            mBeansList = gson.fromJson(jsonObject.optString("contentlist"), tokenType);

            mJokeToolSearchRvAdapter = new JokeToolSearchRvAdapter(JustListActivity.this, mBeansList);
            mRvJokeList.setAdapter(mJokeToolSearchRvAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mToolBar.setOnMenuItemClickListener(onMenuItemClick);

    }

    // 创建关联菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_just_list, menu);
        return true;
    }

    // 菜单的点击回调
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.search:
                    if(null != mBeansList){
                        if(mBeansList.size()<=0){
                            Toast.makeText(JustListActivity.this,"暂无数据可以搜索",Toast.LENGTH_SHORT).show();
                        }else{
                            Intent openSearch = new Intent(JustListActivity.this,OnlySearchActivity.class);
                            // 序列化,传递对象数组
                            openSearch.putParcelableArrayListExtra(GeneralConst.TRANS_LIST,mBeansList);
                            startActivity(openSearch);
                        }
                    }
                    break;
                case R.id.about:
                    Log.d(GeneralConst.TAG,"====== Click About");
                    break;
            }
            return true;
        }
    };
}
