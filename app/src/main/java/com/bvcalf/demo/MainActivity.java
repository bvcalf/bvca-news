
package com.bvcalf.demo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.bvcalf.demo.adapters.MenuAdapter;
import com.bvcalf.demo.beans.MenuItem;

import com.bvcalf.listeners.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    protected FragmentManager mFragmentManager;
    Fragment mArticleFragment = new WenzhangFragment();
    Fragment search = new frag0();
    Fragment kyfw = new frag1();
    Fragment zhyw = new frag2();
    Fragment jxjg = new frag3();
    Fragment dqgz = new frag4();
    Fragment xsgz = new frag5();
    Fragment tzgg = new frag6();
    Fragment szjy = new frag7();
    Fragment jlhz = new frag8();
    Fragment mAboutFragment;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mMenuRecyclerView;
    protected Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getFragmentManager();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mMenuRecyclerView = (RecyclerView) findViewById(R.id.menu_recyclerview);
        mMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        List<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add(new MenuItem(getString(R.string.article), R.drawable.home));
        menuItems.add(new MenuItem(getString(R.string.search), R.drawable.android_icon));
        menuItems.add(new MenuItem(getString(R.string.kyfw), R.drawable.favorite));
        menuItems.add(new MenuItem(getString(R.string.zhyw), R.drawable.favorite1));
        menuItems.add(new MenuItem(getString(R.string.jxjg), R.drawable.favorite2));
        menuItems.add(new MenuItem(getString(R.string.dqgz), R.drawable.favorite3));
        menuItems.add(new MenuItem(getString(R.string.xsgz), R.drawable.favorite4));
        menuItems.add(new MenuItem(getString(R.string.tzgg), R.drawable.favorite5));
        menuItems.add(new MenuItem(getString(R.string.szjy), R.drawable.favorite6));
        menuItems.add(new MenuItem(getString(R.string.jlhz), R.drawable.favorite7));
        menuItems.add(new MenuItem(getString(R.string.about_menu), R.drawable.about));
        menuItems.add(new MenuItem(getString(R.string.exit), R.drawable.exit));
        MenuAdapter menuAdapter = new MenuAdapter(menuItems);
        menuAdapter.setOnItemClickListener(new OnItemClickListener<MenuItem>() {
            @Override
            public void onClick(MenuItem item) {
                clickMenuItem(item);
            }
        });
        mMenuRecyclerView.setAdapter(menuAdapter);

        mFragmentManager.beginTransaction().add(R.id.articles_container, mArticleFragment)
                .commitAllowingStateLoss();
    }

    private void clickMenuItem(MenuItem item) {
        mDrawerLayout.closeDrawers();
        switch (item.iconResId) {
            case R.drawable.home: // 全部
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container, mArticleFragment)
                        .commit();
                break;
            case R.drawable.android_icon:
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container,search )
                        .commit();
                break;
            case R.drawable.favorite:
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container,kyfw )
                        .commit();
                break;
            case R.drawable.favorite1:
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container,zhyw )
                        .commit();
                break;
            case R.drawable.favorite2:
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container,jxjg )
                        .commit();
                break;
            case R.drawable.favorite3:
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container, dqgz)
                        .commit();
                break;
            case R.drawable.favorite4:
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container,xsgz )
                        .commit();
                break;
            case R.drawable.favorite5:
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container, tzgg)
                        .commit();
                break;
            case R.drawable.favorite6:
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container,szjy)
                        .commit();
                break;
            case R.drawable.favorite7:
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container, jlhz)
                        .commit();
                break;
            case R.drawable.about: // 招聘信息
                if (mAboutFragment == null) {
                    mAboutFragment = new GuanyuFragment();
                }
                mFragmentManager.beginTransaction()
                        .replace(R.id.articles_container, mAboutFragment)
                        .commit();
                break;

            case R.drawable.exit: // 退出
                isQuit();
                break;

            default:
                break;
        }
    }

    private void isQuit() {
        new AlertDialog.Builder(this)
                .setTitle("确认退出?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消", null).create().show();
    }
}
