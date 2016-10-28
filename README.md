# ToolbarSearch
ToolbarSearch 安卓列表搜索

# 一、列表数据搜索

> 对于常见的列表搜索，大概分两种，一种是在当前页面搜索，一种是跳页面搜索。


`常见场景`
1、RecyclerView和listView等的列表搜索
2、搜索联系人
3、搜索应用名称
等等。

`数据来源`
1、取自数据库
2、本地数据源

不管是联网取得，还是真的是写死在本地，都有存进内存的过程，所以姑且大言不惭的说，所有数据的都是本地数据。

其实每一个开发的搜索几乎都是必然面对的，写此文也是闹笑话，欢迎补充评论提点，如果空闲，定当fix。


`采取的简单方法`

三个数据集合
1、一个源数据
2、一个copy于源数据的完整拷贝数据
3、一个temp临时搜索结果数据

`可优化细节`
拼音模糊音搜索


上图，bie bi bi，show me your gif
![all.gif](http://upload-images.jianshu.io/upload_images/1083096-5c97151d605c41fa.gif?imageMogr2/auto-orient/strip)


# 二、说明

## 二.1、页面
demo大概分成四个小页，
1、很挫的Edittext+ListView
2、Android Toolbar+SearchView 
3、组装的ToolBar 搜索
（为什么要组装，因为SearchView这个哥们的颜色和图标等不好控制，野性难驯，Google亡我之心不死，无奈之下，剑走偏锋，组装算了）

注：2和3都是在内页搜索

4、跳页面搜索

## 二.2、数据来源
文本笑话API

Json 示例
```
{
				"id": "580a0ac06e36a4ea48ffa788",
				"title": "初中的体育老师说：谁",
				"text": "初中的体育老师说：谁敢再穿裙子上我的课，就罚她倒立。",
				"type": 1,
				"ct": "2016-10-21 20:32:00.319"
			}
```


抽取20个节点，写成固定返回字符串，方便demo演示。




附：gradle引入
```
    compile 'com.google.code.gson:gson:2.7'
    compile 'com.android.support:design:24.2.0'
    compile 'com.android.support:appcompat-v7:24.2.0'
    compile 'com.android.support:cardview-v7:24.2.0'
```


# 三、眼保健操第一节：Edittext+ListView


![简单E+L.gif](http://upload-images.jianshu.io/upload_images/1083096-077cfb14b15e6394.gif?imageMogr2/auto-orient/strip)


没什么好说的，上代码吧

布局文件
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    android:id="@+id/activity_main"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"

    android:orientation="vertical"
    tools:context="com.am.toolbarsearch.Activity.SimpleSearchActivity">

    <EditText
        android:id="@+id/mEtSearch"
        android:layout_width="match_parent"
        android:layout_height="30dp"
        android:layout_margin="10dp"
        android:hint="请输入搜索内容"
        android:background="#66000000"
        />

    <ListView
        android:id="@+id/mLv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_margin="10dp"
        android:background="#664fb9e2"
        ></ListView>

</LinearLayout>
```

adapter
```
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
```

.
.
Activity
```
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
```
.
.
`关键代码`
```
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
```


1、在onCreate得到完整源数据之后，复制一份到mCopyBeansList
2、在doSearch里面for循环，对标题，文本，时间进行搜索匹配，利用temp集合进行接收。
3、mBeansList的数据替换为temp集合
4、更新列表



# 四、眼保健操第二节：ToolBar+SearchView

![Toolbar+SearchView.gif](http://upload-images.jianshu.io/upload_images/1083096-4ef1716f17e7a870.gif?imageMogr2/auto-orient/strip)


其实关于搜索是几乎一样的，值得说一下就是这个Toolbar+SearchView的样式！


[Google Toolbar+Search官方指导书，有兴趣可以看一下](https://developer.android.com/training/search/setup.html)


Google大概这么说：
STEP1、menu的item你要说明你要一个SearchView呀少年
```
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      tools:context=".MainActivity" >

    <!--特殊之处就在于  app:actionViewClass="android.support.v7.widget.SearchView" 这一句-->
    <item android:id="@+id/search"
          android:title="@string/search"
          android:icon="@drawable/icon_search"
          android:orderInCategory="90"
          app:actionViewClass="android.support.v7.widget.SearchView"
          app:showAsAction="ifRoom" />
    
    <item android:id="@+id/about"
          android:title="@string/about"
          android:orderInCategory="90"
          app:showAsAction="never"
          />
</menu>
```
.
.
STEP2 、你要弄一个关于SearchView的xml啊少年，还有啊，你的Activity要把他关联进去呀


弄一个呗

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/1083096-0bba6cd650c4e86c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```
<?xml version="1.0" encoding="utf-8"?>

<searchable xmlns:android="http://schemas.android.com/apk/res/android"
            android:label="@string/app_name"
            android:imeOptions="actionSearch"
            android:hint="姓名"
            android:layout_height="30dp"
            android:layout_width="90dp"/>
```


关联就关联呗
```
 <activity android:name=".Activity.ToolBarSearchActivity"
                  android:theme="@style/AppTheme"
                  app:popupTheme="@style/OverflowMenuStyle"
            >
            <!-- meta tag and intent filter go into results activity -->
            <meta-data
                android:name="android.app.searchable"
                android:resource="@xml/xml_searchable"/>
            <intent-filter>
                <action android:name="android.intent.action.SEARCH"/>
            </intent-filter>
        </activity>
```

STEP3、在关联菜单你要把SearchView给我弄出来
```
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
```

揍是这样，你通过setOnQueryTextListener实现接口就可以用啦。
嗯，不假，可以用的啦。
但是，SearchView不可以该图片啊，不可改颜色啊。没有方法可以设置啊。

搜索图标是黑色
![Paste_Image.png](http://upload-images.jianshu.io/upload_images/1083096-f0a949b8de0fdd80.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


搜索时叉叉和hint一样的小搜索图片是黑色
![Paste_Image.png](http://upload-images.jianshu.io/upload_images/1083096-5aa3ee83c6cb7d11.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

还好，字体是白色，这个白色是这样子实现的。

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/1083096-0a95a7c5afae2cdc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

[图片引自： Toolbar SearchView cursor color](http://stackoverflow.com/questions/39530641/toolbar-searchview-cursor-color)


不行，黑色不爽，要白色！要自定义！要个性！

好的，稍等片刻，美味马上就好。
[解决toolbar中searchview不能改变图标](http://www.jianshu.com/p/4518af4f94e5)

按照上文的说法呢，是你引入mudole的方式引入v7，然后改图去。。。
其实三分有理，但是就是一想到module引入v7，瞬间不想动弹。

一口老血喷涌而出，谷大哥亡我之心不死！
罢了罢了，不用你的了，组装吧！！

组装归组装，我先把代码贴上呀。

```
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
```
.
.
用的是RecyclerView
贴上代码

adapter
```
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

```

.
.
接下来是布局呀
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
                                    xmlns:tools="http://schemas.android.com/tools"
                                    android:id="@+id/mCvItemRoot"
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    xmlns:card_view="http://schemas.android.com/apk/res-auto"
                                    card_view:cardBackgroundColor="@color/colorPrimary"
                                    card_view:cardCornerRadius="10dp"
                                    card_view:cardElevation="8dp"
                                    android:layout_margin="10dp"

    >

    <LinearLayout
        android:id="@+id/mLlItemRoot"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:padding="5dp"

        android:layout_margin="2dp">
        <TextView
            android:id="@+id/mTvJokeTit"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            tools:text="一个标题"
            android:maxLines="2"
            android:textSize="18sp"
            android:padding="10dp"
            android:textColor="#f5f2f2"

            />
        <TextView
            android:id="@+id/mTvJokeText"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"

            android:text="描述"
            android:textSize="16sp"
            android:padding="10dp"
            android:textColor="#72df9c"
            />

        <TextView
            android:id="@+id/mTvJokeTime"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="right"
            android:text="描述"
            android:textSize="16sp"
            android:padding="10dp"
            android:textColor="#aeb6b1"
            />

    </LinearLayout>
</android.support.v7.widget.CardView>
```

注：JokeToolSearchRvAdapter和item和后面的几个页面是通用的。


# 五、眼保健操第三节 在Toolbar的世界里组装Search


![组装.gif](http://upload-images.jianshu.io/upload_images/1083096-ce37901735507929.gif?imageMogr2/auto-orient/strip)

大概是实现这么几点
1、按下搜索，弹出edittext和清除的按钮
2、按下清除按钮，如果editte有文字，清除文字，如果没有文字，才切换为放大镜状态


好啦，上代码啦

是的没错，这次应该先看布局文件，组装嘛
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:app="http://schemas.android.com/apk/res-auto"
              xmlns:tools="http://schemas.android.com/tools"
              android:orientation="vertical"
              android:layout_width="match_parent"
              android:layout_height="match_parent">

    <android.support.v7.widget.Toolbar
        android:id="@+id/mToolBar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        android:elevation="4dp"
        android:theme="@style/AppTheme"
        app:popupTheme="@style/OverflowMenuStyle"
        >
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            >
            <TextView
                android:id="@+id/mTvDiyTitle"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:ellipsize="end"
                tools:text="标题先生"
                android:textColor="@color/white"
                android:layout_centerVertical="true"
                android:textSize="15dp"
                />

            <ImageView
                android:id="@+id/mIvSearch"
                android:layout_width="15dp"
                android:layout_height="15dp"
                android:layout_centerVertical="true"
                android:layout_alignParentRight="true"
                android:layout_marginRight="20dp"
                android:background="@drawable/icon_search"
                android:layout_marginLeft="10dp"
                />

            <ImageView
                android:id="@+id/mIvClean"
                android:layout_width="13dp"
                android:layout_height="13dp"
                android:background="@drawable/icon_error"
                android:layout_centerVertical="true"
                android:layout_toLeftOf="@id/mIvSearch"
                android:layout_marginRight="10dp"
                android:layout_marginLeft="10dp"
                android:visibility="gone"

                />
            <EditText
                android:id="@+id/mEtDiySearch"
                android:layout_width="match_parent"
                android:layout_height="30dp"
                android:layout_centerVertical="true"
                android:layout_toLeftOf="@id/mIvClean"
                android:background="@null"
                android:paddingRight="33dp"
                android:hint="请输入搜索内容"
                android:textColorHint="@color/white"
                android:textColor="@color/white"
                android:maxLines="1"
                android:textSize="14sp"
                android:visibility="gone"
                />

            <View
                android:id="@+id/mLine"
                android:layout_width="match_parent"
                android:layout_height="0.4dp"
                android:background="@color/little_gray"
                android:layout_alignParentBottom="true"
                android:layout_marginBottom="10dp"
                />
        </RelativeLayout>

    </android.support.v7.widget.Toolbar>

    <android.support.v7.widget.RecyclerView
        android:id="@+id/mRvJokeList"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
    </android.support.v7.widget.RecyclerView>

</LinearLayout>
```

注：有的地方虽然我们写的是match_parent，但是在运行中，如果出现Toobar的menu按钮或者Toobar的返回箭头，那么还是自动调整位置的，不用担心重叠的问题滴，


由于Adapter和item是一样，当然就没有贴上去必要啦。
看ToolBarSearchDiyActivity代码

```
public class ToolBarSearchDiyActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolBar;
    private ImageView mIvSearch;  //搜索的放大镜
    private ImageView mIvClean;  // 叉叉,清理 取消
    private TextView mTvDiyTitle;
    private EditText mEtDiySearch; // 搜索内容
    private View mLine;
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
    private String mFinalSearch = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_diy);

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

        try {
            JSONObject jsonObject = new JSONObject(GeneralConst.getImitateJson());
            Gson gson = new Gson();
            Type tokenType = new TypeToken<List<JokeEntity.ContentlistBean>>(){}.getType();
            mBeansList = gson.fromJson(jsonObject.optString("contentlist"), tokenType);
            mCopyBeansList.addAll(mBeansList);
            mJokeToolSearchRvAdapter = new JokeToolSearchRvAdapter(ToolBarSearchDiyActivity.this, mBeansList);
            mRvJokeList.setAdapter(mJokeToolSearchRvAdapter);
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
            mFinalSearch = s.toString();
            doSearch(mFinalSearch);
        }
        public void afterTextChanged(Editable s) {
        }
    }
}
```

大概就是这样啦。


# 六、眼保健操第四节 跳页搜索

这个其实没什么好说的，就是一个页面展示数据，然后我这里是通过intent传值的方式吧数据集合传过去搜索页面的。

JustListActivity
列表页面（按下搜索跳页面）
```
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
```

关键代码
```
Intent openSearch = new Intent(JustListActivity.this,OnlySearchActivity.class);
                            // 序列化,传递对象数组
                            openSearch.putParcelableArrayListExtra(GeneralConst.TRANS_LIST,mBeansList);
                            startActivity(openSearch);
```


.
.
OnlySearchActivity
跳转到的搜索页
（其实这个页面没什么逻辑，和前方大多一样，唯一区别就是数据是上一个传递过来的，你要说不传递应该联网获取当然也是可以的）

```
public class OnlySearchActivity extends AppCompatActivity implements  View.OnClickListener {

    private Toolbar mToolBar;
    private ImageView mIvSearch;  //搜索的放大镜
    private ImageView mIvClean;  // 叉叉,清理 取消
    private TextView mTvDiyTitle;
    private EditText mEtDiySearch; // 搜索内容
    private View mLine;
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
    private String mFinalSearch = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_search);

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
        mTvDiyTitle.setText("传数据 搜索");
        mIvSearch = (ImageView) findViewById(R.id.mIvSearch);
        mIvClean = (ImageView) findViewById(R.id.mIvClean);
        mEtDiySearch = (EditText) findViewById(R.id.mEtDiySearch);
        mLine = findViewById(R.id.mLine);
        mIvSearch.setOnClickListener(this);
        mIvClean.setOnClickListener(this);
        mIvClean.setVisibility(View.GONE);
        mEtDiySearch.setVisibility(View.GONE);
        mLine.setVisibility(View.GONE);
        mEtDiySearch.addTextChangedListener(new OnlySearchActivity.DiySerachWatch());


        mRvJokeList = (RecyclerView) findViewById(R.id.mRvJokeList);
        mRvJokeList.setLayoutManager(new LinearLayoutManager(this));

        try {
            /*JSONObject jsonObject = new JSONObject(GeneralConst.getImitateJson());
            Gson gson = new Gson();
            Type tokenType = new TypeToken<List<JokeEntity.ContentlistBean>>(){}.getType();
            mBeansList = gson.fromJson(jsonObject.optString("contentlist"), tokenType);*/
            mBeansList = getIntent().getParcelableArrayListExtra(GeneralConst.TRANS_LIST);
            mCopyBeansList.addAll(mBeansList);
            mJokeToolSearchRvAdapter = new JokeToolSearchRvAdapter(OnlySearchActivity.this, mBeansList);
            mRvJokeList.setAdapter(mJokeToolSearchRvAdapter);
        } catch (Exception e) {
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
        ObjectAnimator oa = ObjectAnimator.ofFloat(view, "alpha", 0.0f,0.2f,0.4f,0.6f,0.8f,1.0f);
        oa.setDuration(300);
        oa.start();
    }
    class DiySerachWatch implements TextWatcher {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mFinalSearch = s.toString();
            doSearch(mFinalSearch);

        }
        public void afterTextChanged(Editable s) {
        }
    }
}
```

关键代码

```
            mBeansList = getIntent().getParcelableArrayListExtra(GeneralConst.TRANS_LIST);
```


嗯，大概就到这里啦。


其实搜索大家肯定都有自己的方式，方式略low，权当练手Toolbar，不写易忘，姑且如此吧。


还有可以练手完善的地方，比如搜索的时候其实应该用拼音进行模糊搜索。
有空时再补上拼音模糊搜索，哈哈哈。


本篇待续