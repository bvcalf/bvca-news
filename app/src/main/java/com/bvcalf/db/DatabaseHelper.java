/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.bvcalf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bvcalf.demo.beans.Article;
import com.bvcalf.demo.beans.ArticleDetail;
import com.bvcalf.demo.beans.News;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrsimple
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_ARTICLES = "articles";
    public static final String TABLE_ARTICLE_CONTENT = "article_content";

    private static final String CREATE_ARTICLES_TABLE_SQL = "CREATE TABLE articles (  "
            + " post_id INTEGER PRIMARY KEY UNIQUE, "
            + " author VARCHAR(50) NOT NULL ,"
            + " title VARCHAR(200) NOT NULL,"
            + " category VARCHAR(200) NOT NULL ,"
            + " publish_time VARCHAR(50) "
            + " )";

    private static final String CREATE_ARTICLE_CONTENT_TABLE_SQL = "CREATE TABLE article_content (  "
            + " post_id INTEGER PRIMARY KEY UNIQUE, "
            + " content TEXT NOT NULL "
            + " )";

    static final String DB_NAME = "bvca_news.db";
    static final int DB_VERSION = 1;
    private SQLiteDatabase mDatabase;
    static DatabaseHelper sDatabaseHelper;

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mDatabase = getWritableDatabase();
    }

    public static void init(Context context) {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelper(context);
        }
    }

    public static DatabaseHelper getInstance() {
        if (sDatabaseHelper == null) {
            throw new NullPointerException("sDatabaseHelper is null,please call init method first.");
        }
        return sDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ARTICLES_TABLE_SQL);
        db.execSQL(CREATE_ARTICLE_CONTENT_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_ARTICLES);
        db.execSQL("DROP TABLE " + TABLE_ARTICLE_CONTENT);
        onCreate(db);
    }

    public void saveArticles(List<News> dataList) {
        for (News news : dataList) {
            mDatabase.insertWithOnConflict(TABLE_ARTICLES, null, article2ContentValues(news),
                    SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    private ContentValues article2ContentValues(News item) {
        ContentValues newValues = new ContentValues();
        newValues.put("post_id", item.getId());
        newValues.put("author", item.getAuthor());
        newValues.put("title", item.getName());
        newValues.put("category", item.getType());
        newValues.put("publish_time", item.getUpdateDate());
        return newValues;
    }

    public List<News> loadArticles() {
        Cursor cursor = mDatabase.rawQuery("select * from " + TABLE_ARTICLES, null);
        List<News> result = parseArticles(cursor);
        cursor.close();
        return result;
    }

    private List<News> parseArticles(Cursor cursor) {
        List<News> articles = new ArrayList<News>();
        while (cursor.moveToNext()) {
            News item = new News();
            item.setId(cursor.getString(0)) ;
            item.setAuthor(cursor.getString(1)) ;
            item.setName(cursor.getString(2)) ;
            item.setType(cursor.getString(3));
            item.setUpdateDate( cursor.getString(4));
            // 解析数据
            articles.add(item);
        }
        return articles;
    }

    public void saveArticleDetails(ArticleDetail detail) {
        mDatabase.insertWithOnConflict(TABLE_ARTICLE_CONTENT, null,
                articleDetailtoContentValues(detail),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ArticleDetail loadArticleDetail(String postId) {
        Cursor cursor = mDatabase.rawQuery("select * from " + TABLE_ARTICLE_CONTENT
                + " where post_id = "
                + postId, null);
        ArticleDetail detail = new ArticleDetail(postId, parseArticleCotent(cursor));
        cursor.close();
        return detail;
    }

    private String parseArticleCotent(Cursor cursor) {
        return cursor.moveToNext() ? cursor.getString(1) : "";
    }

    protected ContentValues articleDetailtoContentValues(ArticleDetail detail) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("post_id", detail.postId);
        contentValues.put("content", detail.content);
        return contentValues;
    }

}
