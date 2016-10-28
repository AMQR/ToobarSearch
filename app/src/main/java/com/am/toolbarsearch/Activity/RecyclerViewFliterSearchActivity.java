package com.am.toolbarsearch.Activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.am.toolbarsearch.GeneralConst;
import com.am.toolbarsearch.R;
import com.am.toolbarsearch.adapter.RvFilterAdapter;
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
 * Describe: 不使用系统的Toolbar结合SearchView搜索,自己改一下,依然是Search的效果
 */
public class RecyclerViewFliterSearchActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolBar;
    private ImageView mIvSearch;  //搜索的放大镜
    private ImageView mIvClean;  // 叉叉,清理 取消
    private TextView mTvDiyTitle;
    private EditText mEtDiySearch; // 搜索内容
    private View mLine;
    private RecyclerView mRvJokeList;
    private RvFilterAdapter mRvFilterAdapter;
    private ArrayList<JokeEntity.ContentlistBean> mBeansList = new ArrayList<>();
    private ArrayList<JokeEntity.ContentlistBean> mCopyBeansList = new ArrayList<>();

    private  static final int MSG_UPDATE_SEARCH_RESULT = 1001;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_SEARCH_RESULT:
                    mRvFilterAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private String mFinalSearch = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_filter);

        mToolBar = (Toolbar) findViewById(R.id.mToolBar);
        mToolBar.setTitle("");//无需设置,在此无用
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

        mTvDiyTitle = (TextView) findViewById(R.id.mTvDiyTitle);
        mTvDiyTitle.setText("ToolBar搜索DIY");
        mIvSearch = (ImageView) findViewById(R.id.mIvSearch);
        mIvClean = (ImageView) findViewById(R.id.mIvClean);
        mEtDiySearch = (EditText) findViewById(R.id.mEtDiySearch);
        mLine = findViewById(R.id.mLine);
        mIvSearch.setOnClickListener(this);
        mIvClean.setOnClickListener(this);
        mIvClean.setVisibility(View.GONE);
        mEtDiySearch.setVisibility(View.GONE);
        mLine.setVisibility(View.GONE);
        mEtDiySearch.addTextChangedListener(new DiySerachWatch());

        mRvJokeList = (RecyclerView) findViewById(R.id.mRvJokeList);
        mRvJokeList.setLayoutManager(new LinearLayoutManager(this));
        mRvJokeList.setFilterTouchesWhenObscured(true); // RecyclerView 开启Fliter过滤

        try {
            JSONObject jsonObject = new JSONObject(GeneralConst.getImitateJson());
            Gson gson = new Gson();
            Type tokenType = new TypeToken<List<JokeEntity.ContentlistBean>>(){}.getType();
            mBeansList = gson.fromJson(jsonObject.optString("contentlist"), tokenType);
            mCopyBeansList.addAll(mBeansList);
            mRvFilterAdapter = new RvFilterAdapter(RecyclerViewFliterSearchActivity.this, mBeansList);
            mRvJokeList.setAdapter(mRvFilterAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 创建关联菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_diy, menu);
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




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mIvSearch:
                mEtDiySearch.setVisibility(View.VISIBLE);
                mIvClean.setVisibility(View.VISIBLE);
                mLine.setVisibility(View.VISIBLE);
                mTvDiyTitle.setVisibility(View.GONE);
                alpha(mEtDiySearch);
                alpha(mIvClean);
                break;
            case R.id.mIvClean:
                if(mFinalSearch.length()>0){
                    mEtDiySearch.setText("");
                }else{
                    mEtDiySearch.setVisibility(View.GONE);
                    mIvClean.setVisibility(View.GONE);
                    mLine.setVisibility(View.GONE);
                    mTvDiyTitle.setVisibility(View.VISIBLE);
                    mBeansList.clear();
                    mBeansList.addAll(mCopyBeansList);
                    alpha(mTvDiyTitle);
                    mHandler.sendEmptyMessage(MSG_UPDATE_SEARCH_RESULT);
                }
                break;
        }
    }

    /**
     * 透明度
     * @param view
     */
    public void alpha(View view){
//		iv.setAlpha(alpha);
//		iv.getAlpha()
        ObjectAnimator oa = ObjectAnimator.ofFloat(view, "alpha", 0.0f,0.2f,0.4f,0.6f,0.8f,1.0f);
        oa.setDuration(300);
        oa.start();
    }

    class DiySerachWatch implements TextWatcher {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mFinalSearch = s.toString().trim();
            if (TextUtils.isEmpty(mFinalSearch)){
                // 搜索
                mRvFilterAdapter.getFilter().filter("");
                mHandler.sendEmptyMessage(MSG_UPDATE_SEARCH_RESULT);
            }else {
                mRvFilterAdapter.getFilter().filter(mFinalSearch);
                mHandler.sendEmptyMessage(MSG_UPDATE_SEARCH_RESULT);
            }

        }
        public void afterTextChanged(Editable s) {
        }
    }

}
