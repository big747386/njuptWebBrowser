package com.example.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FavoriteActivity extends Activity {

    private String data[] = {"nishisha","yuniwugua","chounizadi"};

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        initView();
    }

    private void initView() {
        listView = findViewById(R.id.favListView);
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, data);
        listView.setAdapter(arrayAdapter);
    }
}
