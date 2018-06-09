package org.androidtown.mycalendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView thedate;
    private Button btn_go_calendar;
    private TextView sum_view;

    MyDBHelper mHelper;
    SQLiteDatabase db;
    Cursor cursor;
    MyCursorAdapter myAdapter;

    final static String KEY_ID = "_id";
    final static String KEY_CONTEXT = "context";
    final static String KEY_PRICE = "price";
    final static String TABLE_NAME = "MyAccountList";
    final static String KEY_DATE = "date";
    public static String View_DATE = getToday_date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Account Book");

        //데이터베이스 생성
        mHelper = new MyDBHelper(this);
        db = mHelper.getWritableDatabase();

        //레이아웃 변수설정
        thedate = (TextView) findViewById(R.id.date);
        btn_go_calendar = (Button) findViewById(R.id.btn_go_calendar);
        ListView list = (ListView) findViewById( R.id.account_list );
        sum_view = (TextView) findViewById(R.id.total_sum);

        //날짜 표시 인텐트 설정
        Intent comingIntent = getIntent();
        Log.d(TAG, "getintent OK");
        String date = comingIntent.getStringExtra("date");
        if(!TextUtils.isEmpty(date)){
            View_DATE = date;
            thedate.setText(date);
            Log.d(TAG, "string is not empty");
        } else{
            //date = getToday_date();
            thedate.setText(View_DATE);
        }

        // 총합 가격 표시
        String queryPriceSum = String.format( " SELECT SUM(price) FROM %s WHERE date = '%s'", TABLE_NAME, View_DATE);
        cursor = db.rawQuery( queryPriceSum, null );
        cursor.moveToNext();
        String sum = String.valueOf(cursor.getInt(0));
        Log.d(TAG, "sum : " + sum);
        sum_view.setText(sum);

        //달력 이동
        btn_go_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
                //액티비티 옵션 추가 요망
            }
        });

        //커서 어댑터 생성
        String querySelectAll = String.format( "SELECT * FROM %s WHERE date = '%s'", TABLE_NAME, View_DATE);
        cursor = db.rawQuery( querySelectAll, null );
        myAdapter = new MyCursorAdapter ( this, cursor );

        //리스트뷰 어댑터 설정
        list.setAdapter( myAdapter );
    }

    public void OnClick_addButton( View v )
    {
        EditText eContext = (EditText) findViewById( R.id.edit_context );
        EditText ePrice = (EditText) findViewById( R.id.edit_price );

        String contexts = eContext.getText().toString();
        int price = Integer.parseInt( ePrice.getText().toString() );
        String today_Date = getToday_date();
        Log.d(TAG, "값 확인" + contexts +", " + price + ", " + today_Date);

        //문자열은 ''로 감싸야 한다.
        String query = String.format(
                "INSERT INTO %s VALUES ( null, '%s', %d, '%s' );", TABLE_NAME, contexts, price, View_DATE);
        db.execSQL( query );

        // 총합 가격 표시
        String queryPriceSum = String.format( " SELECT SUM(price) FROM %s WHERE date = '%s'", TABLE_NAME, View_DATE);
        cursor = db.rawQuery( queryPriceSum, null );
        cursor.moveToNext();
        String sum = String.valueOf(cursor.getInt(0));
        Log.d(TAG, "sum : " + sum);
        sum_view.setText(sum);

        // 아래 메서드를 실행하면 리스트가 갱신된다. 하지만 구글은 이 메서드를 deprecate한다. 고로 다른 방법으로 해보자.
        // cursor.requery();
        String querySelectAll = String.format( "SELECT * FROM %s WHERE date = '%s'", TABLE_NAME, View_DATE);
        cursor = db.rawQuery( querySelectAll, null );
        myAdapter.changeCursor( cursor );
        //myAdapter.notifyDataSetChanged();

        eContext.setText( "" );
        ePrice.setText( "" );

        // 저장 버튼 누른 후 키보드 안보이게 하기
        InputMethodManager imm =
                (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
        imm.hideSoftInputFromWindow( ePrice.getWindowToken(), 0 );
    }

    static public String getToday_date(){
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy/M/d", Locale.KOREA);
        Date currentTime = new Date();
        String Today_day = mSimpleDateFormat.format(currentTime).toString();
        return Today_day;
    }

    static public String getThis_time(){
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HHMMSS", Locale.KOREA);
        Date currentTime = new Date();
        String This_time = mSimpleDateFormat.format(currentTime).toString();
        return This_time;
    }

    static public void reset_table(){
        //테이블 리셋해야함 ㅠㅠ
        String TABLE_NAME = "a_" + getToday_date();
        String querySelectAll = String.format( "SELECT * FROM %s", TABLE_NAME );
    }
}
