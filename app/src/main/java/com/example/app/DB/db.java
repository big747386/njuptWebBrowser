//package com.example.app.DB;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.widget.SimpleAdapter;
//
//import com.example.app.DBOperator;
//import com.example.app.R;
//import com.example.app.entity.BookMark;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class db {
//    private static final String BOOK_DB_NAME = "Bookmark";
//    private static final int VERSION = 1;
//
//    private DBOperator dbOperator;
//    private SQLiteDatabase sqLiteDatabase;
//    private Context mycontext;
//
//    private db(Context context) {
//        dbOperator = new DBOperator(context, "browserdatabase.db",
//                null, VERSION);
//        sqLiteDatabase = dbOperator.getWritableDatabase();
//    }
//
//    //查询
//    public void queryInfo(BookMark bookMark) {
//        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
//        Cursor cursor = sqLiteDatabase.query(BOOK_DB_NAME, null,null,
//                null, null, null, "ID desc", null);
//        if (cursor != null && cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                bookMark.setId(cursor.getInt(0));
//                bookMark.setUrl(cursor.getString(1));
//                HashMap<String, Object> map = new HashMap<>();
//                map.put("title", bookMark.getTitle());
//                map.put("url", bookMark.getTitle());
//                //此处有arrayList
//                listItem.add(map);
//                SimpleAdapter simpleAdapter = new SimpleAdapter(mycontext, listItem,
//                        R.layout.activity_favorite, new String[] {"title","url"},
//                        new int[] {R.id.listText1, R.id.listText2});
//
//            }
//        }
//    }
//}
