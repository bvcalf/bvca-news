package com.bvcalf.demo;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bvcalf.db.DatabaseHelper;
import com.bvcalf.demo.adapters.ArticleAdapter;
import com.bvcalf.demo.beans.Article;
import com.bvcalf.demo.beans.News;
import com.bvcalf.demo.beans.NewsVo;
import com.bvcalf.listeners.OnItemClickListener;
import com.bvcalf.widgets.AutoLoadRecyclerView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 李子宣 on 2017/8/5.
 */

public class search extends AppCompatActivity implements AutoLoadRecyclerView.OnLoadListener {
    private SearchView mSearchView;
    private SearchView.SearchAutoComplete mSearchAutoComplete;
//    private Thread newThread;
    String str ;

    //protected int mCategory = Article.ALL;//有用吗？
    protected ArticleAdapter mAdapter;
    //RecyclerView mRecyclerView;
    protected AutoLoadRecyclerView mRecyclerView;
    final protected List<News> mDataSet = new ArrayList<News>();
    //private int mPageIndex = 1;
    //int a=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
//--------------------------------------------------------------------------------------
        mRecyclerView = (AutoLoadRecyclerView) findViewById(R.id.articles_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(search.this
                .getApplicationContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setOnLoadListener(this);
        initAdapter();
//--------------------------------------------------------------------------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchAutoComplete.isShown()) {
                    try {
                        mSearchAutoComplete.setText("");
                        Method method = mSearchView.getClass().getDeclaredMethod("onCloseClicked");
                        method.setAccessible(true);
                        method.invoke(mSearchView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);

        //通过MenuItem得到SearchView
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        mSearchView.setQueryHint("搜索新闻");

        //设置输入框提示文字样式
        mSearchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        mSearchAutoComplete.setTextColor(getResources().getColor(android.R.color.background_light));
        mSearchAutoComplete.setTextSize(14);
        //设置触发查询的最少字符数（默认2个字符才会触发查询）
        mSearchAutoComplete.setThreshold(1);

        //设置搜索框有字时显示叉叉，无字时隐藏叉叉
        mSearchView.onActionViewExpanded();
        mSearchView.setIconified(true);

        //修改搜索框控件间的间隔（这样只是为了更加接近网易云音乐的搜索框）
        LinearLayout search_edit_frame = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search_edit_frame.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        search_edit_frame.setLayoutParams(params);

        //监听SearchView的内容
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//--------------------------------------------------------------------------------------
                //这里提交
                try {
                    str= URLDecoder.decode(s,"utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getArticles(str);
                return true;
//--------------------------------------------------------------------------------------
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void getArticles(final String str) {
        new AsyncTask<Void, Void, List<News>>() {
            protected void onPreExecute() {
                Toast.makeText(search.this,"1111",Toast.LENGTH_SHORT).show();
            };
            /*@Override
            protected List<Article> doInBackground(Void... params) {
                return performRequest(page);
            }*/
            @Override
            protected List<News> doInBackground(Void... params) {
                return performRequest(str);
            }
            protected void onPostExecute(List<News> result) {
//----------------------------------------------------------------------------------
                // 移除已经更新的数据
                result.removeAll(mDataSet);
                // 添加心数据
                mDataSet.addAll(result);
                mAdapter.notifyDataSetChanged();
                // 存储文章列表
//                DatabaseHelper.getInstance().saveArticles(result);
//                if (result.size() > 0) {
//                    mPageIndex++;
//                }
//----------------------------------------------------------------------------------
            };
        }.execute();
    }
    //s
    private List<News> performRequest(String s) {
        HttpURLConnection urlConnection = null;
        try {
            //Toast.makeText(search.this,"开始搜索:"+s,Toast.LENGTH_SHORT).show();
            Log.i("Search","----------------------------------------------------------开始搜索:"+s);
            s= URLEncoder.encode(s,"UTF-8");
            String getUrl = "http://114.215.91.47:8080/q?q="+s;
            urlConnection = (HttpURLConnection) new URL(getUrl)
                    .openConnection();
            urlConnection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sBuilder.append(line).append("\n");
            }
            String result = sBuilder.toString();
            Toast.makeText(search.this,"返回搜索结果:"+s,Toast.LENGTH_SHORT).show();
            Log.i("Search","----------------------------------------------------------开始搜索:"+s);
            return parseNews(result);
            //return parse(new JSONArray(result));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return new ArrayList<News>();//???
    }
    // 让菜单同时显示图标和文字
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }
    //--------------------------------------------------------------------------------------
    //分界
//    @Override
//    public void onResume() {
//        super.onResume();
//        mDataSet.addAll(DatabaseHelper.getInstance().loadArticles());
//        mAdapter.notifyDataSetChanged();
//    }

    protected void initAdapter() {
        mAdapter = new ArticleAdapter(mDataSet);
        mAdapter.setOnItemClickListener(new OnItemClickListener<News>() {

            @Override
            public void onClick(News news) {
                if (news != null) {
                    loadArticle(news);
                }
            }
        });
        // 设置Adapter
        mRecyclerView.setAdapter(mAdapter);
    }

//    public void setArticleCategory(int category) {
//        mCategory = category;
//    }



    public List<News> parseNews(String jsonStr){
        Gson gson = new Gson();
        NewsVo newsVo = gson.fromJson(jsonStr, NewsVo.class);
        return newsVo.getNewsList();
    }

    //TODO：要改
    protected void loadArticle(News news) {
        Intent intent = new Intent(search.this, NeirongActivity.class);
        intent.putExtra("post_id", news.getId());
        intent.putExtra("title", news.getName());
        startActivity(intent);
    }

    @Override
    public void onLoad() {

    }
    //TODO 需要测试此方法
//    public void loadNews(News news){
//        Intent intent = new Intent(search.this, NeirongActivity.class);
//        intent.putExtra("post_id", news.getId());
//        intent.putExtra("title", news.getName());
//        startActivity(intent);
//    }
}
