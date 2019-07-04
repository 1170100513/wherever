package schedule.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE = "scheduleContext";

    public MySQLiteHelper(Context context) {
        super(context, "schedule.db", null, 1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String sql = "create table scheduleContext(_id integer primary key autoincrement"
                + ", create_date INTEGER NOT NULL DEFAULT" +
                " (strftime('%s','now') * 1000) " +
                ",modified_date INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)," +
                "context)";
        db.execSQL(sql);
        Log.d(">>>>>>", ">>>>创建数据库>>>");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
