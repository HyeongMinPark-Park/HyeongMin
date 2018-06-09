package org.androidtown.mycalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static org.androidtown.mycalendar.MainActivity.KEY_CONTEXT;
import static org.androidtown.mycalendar.MainActivity.KEY_DATE;
import static org.androidtown.mycalendar.MainActivity.KEY_ID;
import static org.androidtown.mycalendar.MainActivity.KEY_PRICE;
import static org.androidtown.mycalendar.MainActivity.TABLE_NAME;

/**
 * Created by 1hk_s on 2018-06-07.
 */

class MyDBHelper extends SQLiteOpenHelper {
    public MyDBHelper(Context context) {

        super(context, "My_Account_Data.db", null, 4);
    }

    public void onCreate(SQLiteDatabase db) {
        // AUTOINCREMENT 속성 사용 시 PRIMARY KEY로 지정한다.
        String query = String.format("CREATE TABLE %s ("
                + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "%s TEXT, "
                + "%s INTEGER, "
                + "%s TEXT);", TABLE_NAME, KEY_ID, KEY_CONTEXT, KEY_PRICE, KEY_DATE);
        db.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(query);
        onCreate(db);
    }
}