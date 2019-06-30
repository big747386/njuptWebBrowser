package com.example.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.app.entity.History;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HistoryActivity extends Activity implements View.OnClickListener {
    private static final String HISTORY_DB_NAME = "History";

    private static final int VERSION = 1;

    private DBOperator dbOperator;
    private SQLiteDatabase sqLiteDatabase;

    private ListView listView;
    ArrayList<String> listItem = new ArrayList<>();
    private HashMap<Integer,String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dbOperator = new DBOperator(this, "browserdatabase.db",
                null, VERSION);
        sqLiteDatabase = dbOperator.getWritableDatabase();

        //视图初始化
        initView();
    }

    private void initView() {
        listView = findViewById(R.id.favListView);

        queryInfo();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                jumpBack(position);
            }
        });
    }

    public void jumpBack(int position) {
        Intent intent = new Intent();
        intent.putExtra("data", urlList.get(position));
        setResult(RESULT_OK, intent);
        finish();
    }
    //查询
    public void queryInfo() {
        listItem = new ArrayList<>();
        urlList = new HashMap<>();
        History history = new History();
        Cursor cursor = sqLiteDatabase.query(HISTORY_DB_NAME, null,null,
                null, null, null, "ID desc", null);
        if (cursor != null && cursor.getCount() > 0) {
            int position = 0;
            while (cursor.moveToNext()) {
                history.setId(cursor.getInt(0));
                history.setUrl(cursor.getString(1));
                //history.setTitle("dingweiwenti");
                history.setTitle(cursor.getString(3));
                //listItem.add("没拿到Title嘛？");
                urlList.put(position, history.getUrl());
                listItem.add(history.getTitle());
                position++;
            }
            try {
                ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                        R.layout.array_adapter, listItem);
                listView.setAdapter(arrayAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            listItem.add("还没有历史记录呢，快去网上冲浪吧");
            ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                    R.layout.array_adapter, listItem);
            listView.setAdapter(arrayAdapter);
        }

        cursor.close();
        sqLiteDatabase.close();
    }

    public void insert(History history) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("URL",history.getUrl());
        contentValues.put("TITLE",history.getTitle());
        //SQLITE的类型需要与JAVA中的Date转换
        Date currentTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(currentTime);
        contentValues.put("TIME", dateString);
        sqLiteDatabase.insert(HISTORY_DB_NAME, null, contentValues);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
}
